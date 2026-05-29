package com.example.admin.service;

import com.example.admin.entity.AiConversation;
import com.example.admin.mapper.AiConversationMapper;
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
 * AI对话服务 — DB为数据源，Redis为缓存层
 *
 * 双写一致性方案（参照Cache-Aside + Distributed Lock模式）：
 *   1. 写操作：DB事务内写入 → 释放事务 → 持分布式锁重建Redis缓存
 *   2. 读操作：缓存命中直接返回 → 缓存未命中查DB并回填缓存
 *   3. 锁机制：Redis SETNX实现轻量级分布式锁，锁粒度为用户级
 */
@Service
public class AiConversationService {

    private static final Logger log = LoggerFactory.getLogger(AiConversationService.class);
    private static final String CACHE_KEY_PREFIX = "ds-ai:conversation:";
    private static final String USER_LIST_KEY_PREFIX = "ds-ai:conversations:user:";
    private static final String LOCK_KEY_PREFIX = "ds-ai:lock:conversations:";
    private static final long SINGLE_TTL_MIN = 60;
    private static final long LOCK_EXPIRE_SEC = 5;
    private static final long LOCK_WAIT_MS = 3000;

    @Autowired
    private AiConversationMapper conversationMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // ════════════════════════ 读操作 ════════════════════════

    /** 查询用户的所有对话 — 缓存优先，无TTL，靠写操作主动失效保证一致性 */
    public List<AiConversation> listByUser(Long userId) {
        String listKey = USER_LIST_KEY_PREFIX + userId;
        try {
            String cachedJson = redisTemplate.opsForValue().get(listKey);
            if (cachedJson != null) {
                return objectMapper.readValue(cachedJson, new TypeReference<List<AiConversation>>() {});
            }
        } catch (Exception e) {
            log.warn("读取对话列表缓存失败 userId={}", userId, e);
        }
        List<AiConversation> list = conversationMapper.selectByUserId(userId);
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(listKey, objectMapper.writeValueAsString(list));
            } catch (Exception e) {
                log.warn("写入对话列表缓存失败 userId={}", userId, e);
            }
        }
        return list != null ? list : Collections.emptyList();
    }

    /** 根据ID获取对话 — 缓存优先 */
    public AiConversation getById(Long id) {
        String key = CACHE_KEY_PREFIX + id;
        try {
            String cachedJson = redisTemplate.opsForValue().get(key);
            if (cachedJson != null) {
                return objectMapper.readValue(cachedJson, AiConversation.class);
            }
        } catch (Exception e) {
            log.warn("读取对话缓存失败 id={}", id, e);
        }
        AiConversation conv = conversationMapper.selectById(id);
        if (conv != null) {
            try {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(conv),
                        SINGLE_TTL_MIN, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("写入对话缓存失败 id={}", id, e);
            }
        }
        return conv;
    }

    // ════════════════════════ 写操作（DB事务 + Redis分布式锁） ════════════════════════

    /**
     * 创建新对话 — 先写DB（事务），持锁维护单项缓存+重建列表缓存
     */
    public AiConversation create(Long userId, String title) {
        AiConversation conv = new AiConversation();
        conv.setUserId(userId);
        conv.setTitle(title != null ? title : "新对话");
        conv.setCreateTime(LocalDateTime.now());
        conv.setUpdateTime(LocalDateTime.now());
        doInsert(conv);
        String lockToken = acquireLock(userId);
        try {
            // 单项缓存：写入新创建的对话
            try {
                redisTemplate.opsForValue().set(CACHE_KEY_PREFIX + conv.getId(),
                        objectMapper.writeValueAsString(conv), SINGLE_TTL_MIN, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("写入对话缓存失败 id={}", conv.getId(), e);
            }
            rebuildUserListCache(userId);
        } finally {
            releaseLock(userId, lockToken);
        }
        return conv;
    }

    /**
     * 更新对话 — 先更新DB（事务），持锁淘汰单项缓存+重建列表缓存
     */
    public AiConversation update(AiConversation conv) {
        AiConversation existing = conversationMapper.selectById(conv.getId());
        if (existing == null) return null;
        if (conv.getTitle() != null) existing.setTitle(conv.getTitle());
        if (conv.getModelId() != null) existing.setModelId(conv.getModelId());
        existing.setUpdateTime(LocalDateTime.now());
        doUpdate(existing);
        Long userId = existing.getUserId();
        String lockToken = acquireLock(userId);
        try {
            evictSingleCache(conv.getId());
            rebuildUserListCache(userId);
        } finally {
            releaseLock(userId, lockToken);
        }
        return existing;
    }

    /**
     * 删除对话 — 先删DB（事务），持锁淘汰单项缓存+重建列表缓存
     */
    public void delete(Long id) {
        AiConversation conv = conversationMapper.selectById(id);
        if (conv == null) return;
        doDelete(id);
        Long userId = conv.getUserId();
        String lockToken = acquireLock(userId);
        try {
            evictSingleCache(id);
            rebuildUserListCache(userId);
        } finally {
            releaseLock(userId, lockToken);
        }
    }

    // ════════════════════════ DB操作（独立事务） ════════════════════════

    @Transactional
    void doInsert(AiConversation conv) {
        conversationMapper.insert(conv);
    }

    @Transactional
    void doUpdate(AiConversation conv) {
        conversationMapper.update(conv);
    }

    @Transactional
    void doDelete(Long id) {
        conversationMapper.deleteById(id);
    }

    // ════════════════════════ Redis分布式锁 ════════════════════════

    /**
     * 获取分布式锁 — SETNX + 超时，防止死锁
     * @param userId 用户ID，锁粒度为用户级
     * @return 锁令牌（UUID），用于安全释放
     */
    private String acquireLock(Long userId) {
        String lockKey = LOCK_KEY_PREFIX + userId;
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
        log.warn("获取分布式锁超时 userId={}", userId);
        return token; // 降级：即使未获锁也继续执行，缓存最终一致
    }

    /**
     * 释放分布式锁 — 校验令牌防止误删
     */
    private void releaseLock(Long userId, String token) {
        try {
            String lockKey = LOCK_KEY_PREFIX + userId;
            String current = redisTemplate.opsForValue().get(lockKey);
            if (token.equals(current)) {
                redisTemplate.delete(lockKey);
            }
        } catch (Exception e) {
            log.warn("释放分布式锁失败 userId={}", userId, e);
        }
    }

    // ════════════════════════ 缓存维护 ════════════════════════

    private void evictSingleCache(Long id) {
        try { redisTemplate.delete(CACHE_KEY_PREFIX + id); }
        catch (Exception e) { log.warn("删除对话缓存失败 id={}", id, e); }
    }

    /** 从DB加载最新列表写入Redis */
    private void rebuildUserListCache(Long userId) {
        try {
            List<AiConversation> list = conversationMapper.selectByUserId(userId);
            String key = USER_LIST_KEY_PREFIX + userId;
            if (list != null && !list.isEmpty()) {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list));
            } else {
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.warn("重建用户对话列表缓存失败 userId={}", userId, e);
        }
    }

    // ════════════════════════ 管理端（不涉及缓存） ════════════════════════

    /** 管理员分页查询所有对话 */
    public List<AiConversation> listAll(int page, int pageSize, String keyword, String username) {
        int offset = (page - 1) * pageSize;
        return conversationMapper.selectAll(offset, pageSize, keyword, username);
    }

    /** 管理员统计对话总数 */
    public int countAll(String keyword, String username) {
        return conversationMapper.countAll(keyword, username);
    }
}
