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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AiConversationService {

    private static final Logger log = LoggerFactory.getLogger(AiConversationService.class);
    private static final String CACHE_KEY_PREFIX = "ds-ai:conversation:";
    private static final String USER_LIST_KEY_PREFIX = "ds-ai:conversations:user:";
    private static final long SINGLE_TTL_MIN = 30;
    private static final long LIST_TTL_MIN = 10;

    @Autowired
    private AiConversationMapper conversationMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /** 查询用户的所有对话 — 缓存优先 */
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
                redisTemplate.opsForValue().set(listKey, objectMapper.writeValueAsString(list),
                        LIST_TTL_MIN, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("写入对话列表缓存失败 userId={}", userId, e);
            }
        }
        return list;
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

    /** 创建新对话 — 先写DB，再删列表缓存 */
    @Transactional
    public AiConversation create(Long userId, String title) {
        AiConversation conv = new AiConversation();
        conv.setUserId(userId);
        conv.setTitle(title != null ? title : "新对话");
        conv.setCreateTime(LocalDateTime.now());
        conv.setUpdateTime(LocalDateTime.now());
        conversationMapper.insert(conv);
        evictUserListCache(userId);
        try {
            redisTemplate.opsForValue().set(CACHE_KEY_PREFIX + conv.getId(),
                    objectMapper.writeValueAsString(conv), SINGLE_TTL_MIN, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("写入对话缓存失败 id={}", conv.getId(), e);
        }
        return conv;
    }

    /** 更新对话 — 先更新DB，再删缓存 */
    @Transactional
    public AiConversation update(AiConversation conv) {
        AiConversation existing = conversationMapper.selectById(conv.getId());
        if (existing == null) return null;
        if (conv.getTitle() != null) existing.setTitle(conv.getTitle());
        if (conv.getModelId() != null) existing.setModelId(conv.getModelId());
        existing.setUpdateTime(LocalDateTime.now());
        conversationMapper.update(existing);
        evictSingleCache(conv.getId());
        evictUserListCache(existing.getUserId());
        return existing;
    }

    /** 删除对话 — 先删DB，再删缓存 */
    @Transactional
    public void delete(Long id) {
        AiConversation conv = conversationMapper.selectById(id);
        if (conv != null) {
            conversationMapper.deleteById(id);
            evictSingleCache(id);
            evictUserListCache(conv.getUserId());
        }
    }

    private void evictSingleCache(Long id) {
        try { redisTemplate.delete(CACHE_KEY_PREFIX + id); }
        catch (Exception e) { log.warn("删除对话缓存失败 id={}", id, e); }
    }

    private void evictUserListCache(Long userId) {
        try { redisTemplate.delete(USER_LIST_KEY_PREFIX + userId); }
        catch (Exception e) { log.warn("删除用户对话列表缓存失败 userId={}", userId, e); }
    }
}
