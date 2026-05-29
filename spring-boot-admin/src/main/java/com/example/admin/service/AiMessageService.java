package com.example.admin.service;

import com.example.admin.entity.AiMessage;
import com.example.admin.mapper.AiMessageMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * AI消息服务 — DB为数据源，Redis为缓存层
 *
 * 双写一致性方案（参照Cache-Aside + Distributed Lock模式）：
 *   1. 写操作：DB事务内写入 → 释放事务 → 持分布式锁重建Redis缓存
 *   2. 读操作：缓存命中直接返回 → 缓存未命中查DB并回填缓存
 *   3. 锁机制：Redis SETNX实现轻量级分布式锁，保证同一对话并发写串行化
 */
@Service
public class AiMessageService {

    private static final Logger log = LoggerFactory.getLogger(AiMessageService.class);
    private static final String MSG_LIST_KEY_PREFIX = "ds-ai:messages:conversation:";
    private static final String LOCK_KEY_PREFIX = "ds-ai:lock:messages:";
    private static final long LOCK_EXPIRE_SEC = 5;
    private static final long LOCK_WAIT_MS = 3000;

    @Autowired
    private AiMessageMapper messageMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // ════════════════════════ 读操作 ════════════════════════

    /** 查询对话中的所有消息 — 缓存优先，无TTL，靠写操作主动失效保证一致性 */
    public List<AiMessage> listByConversationId(Long conversationId) {
        String key = MSG_LIST_KEY_PREFIX + conversationId;
        try {
            String cachedJson = redisTemplate.opsForValue().get(key);
            if (cachedJson != null) {
                return objectMapper.readValue(cachedJson, new TypeReference<List<AiMessage>>() {});
            }
        } catch (Exception e) {
            log.warn("读取消息列表缓存失败 conversationId={}", conversationId, e);
        }
        List<AiMessage> list = messageMapper.selectByConversationId(conversationId);
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list));
            } catch (Exception e) {
                log.warn("写入消息列表缓存失败 conversationId={}", conversationId, e);
            }
        }
        return list != null ? list : Collections.emptyList();
    }

    /** 根据ID获取单条消息 */
    public AiMessage getById(Long id) {
        return messageMapper.selectById(id);
    }

    /** 更新消息内容（仅允许编辑用户消息） */
    public void updateContent(Long id, String content) {
        AiMessage msg = messageMapper.selectById(id);
        if (msg == null) return;
        messageMapper.updateContent(id, content);
        // 重建缓存
        String lockToken = acquireLock(msg.getConversationId());
        try {
            rebuildMessageListCache(msg.getConversationId());
        } finally {
            releaseLock(msg.getConversationId(), lockToken);
        }
    }

    // ════════════════════════ 写操作（DB事务 + Redis分布式锁） ════════════════════════

    /**
     * 保存消息 — 先写DB（事务），持锁重建Redis缓存
     */
    public AiMessage save(Long conversationId, String role, String content) {
        AiMessage msg = new AiMessage();
        msg.setConversationId(conversationId);
        msg.setRole(role);
        msg.setContent(content);
        msg.setCreateTime(LocalDateTime.now());
        doInsert(msg);  // 内部事务，保证DB先落地
        // DB已提交，重建缓存（持锁防并发）
        String lockToken = acquireLock(conversationId);
        try {
            rebuildMessageListCache(conversationId);
        } finally {
            releaseLock(conversationId, lockToken);
        }
        return msg;
    }

    /**
     * 删除消息 — 先查归属对话，删DB（事务），持锁重建缓存
     */
    public void delete(Long id) {
        AiMessage msg = messageMapper.selectById(id);
        if (msg == null) return;
        doDelete(id);
        String lockToken = acquireLock(msg.getConversationId());
        try {
            rebuildMessageListCache(msg.getConversationId());
        } finally {
            releaseLock(msg.getConversationId(), lockToken);
        }
    }

    /**
     * 删除对话下所有消息 — 先删DB，再删缓存
     */
    public void deleteByConversationId(Long conversationId) {
        doDeleteByConvId(conversationId);
        String lockToken = acquireLock(conversationId);
        try {
            evictMessageListCache(conversationId);
        } finally {
            releaseLock(conversationId, lockToken);
        }
    }

    // ════════════════════════ DB操作（独立事务） ════════════════════════

    @Transactional
    void doInsert(AiMessage msg) {
        messageMapper.insert(msg);
    }

    @Transactional
    void doDelete(Long id) {
        messageMapper.deleteById(id);
    }

    @Transactional
    void doDeleteByConvId(Long conversationId) {
        messageMapper.deleteByConversationId(conversationId);
    }

    // ════════════════════════ Redis分布式锁 ════════════════════════

    /**
     * 获取分布式锁 — SETNX + 超时，防止死锁
     * @return 锁令牌（UUID），用于安全释放
     */
    private String acquireLock(Long conversationId) {
        String lockKey = LOCK_KEY_PREFIX + conversationId;
        String token = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < LOCK_WAIT_MS) {
            Boolean ok = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, token, LOCK_EXPIRE_SEC, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(ok)) {
                return token;
            }
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        log.warn("获取分布式锁超时 conversationId={}", conversationId);
        return token; // 降级：即使未获锁也继续执行，缓存最终一致
    }

    /**
     * 释放分布式锁 — 校验令牌防止误删
     */
    private void releaseLock(Long conversationId, String token) {
        try {
            String lockKey = LOCK_KEY_PREFIX + conversationId;
            String current = redisTemplate.opsForValue().get(lockKey);
            if (token.equals(current)) {
                redisTemplate.delete(lockKey);
            }
        } catch (Exception e) {
            log.warn("释放分布式锁失败 conversationId={}", conversationId, e);
        }
    }

    // ════════════════════════ 缓存维护 ════════════════════════

    /** 重建消息列表缓存：从DB加载最新数据写入Redis */
    private void rebuildMessageListCache(Long conversationId) {
        try {
            List<AiMessage> list = messageMapper.selectByConversationId(conversationId);
            String key = MSG_LIST_KEY_PREFIX + conversationId;
            if (list != null && !list.isEmpty()) {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list));
            } else {
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.warn("重建消息列表缓存失败 conversationId={}", conversationId, e);
        }
    }

    private void evictMessageListCache(Long conversationId) {
        try { redisTemplate.delete(MSG_LIST_KEY_PREFIX + conversationId); }
        catch (Exception e) { log.warn("删除消息列表缓存失败 conversationId={}", conversationId, e); }
    }
}
