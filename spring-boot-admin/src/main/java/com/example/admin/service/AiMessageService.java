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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AiMessageService {

    private static final Logger log = LoggerFactory.getLogger(AiMessageService.class);
    private static final String MSG_LIST_KEY_PREFIX = "ds-ai:messages:conversation:";
    private static final long MSG_LIST_TTL_MIN = 10;

    @Autowired
    private AiMessageMapper messageMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /** 查询对话中的所有消息 — 缓存优先 */
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
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list),
                        MSG_LIST_TTL_MIN, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("写入消息列表缓存失败 conversationId={}", conversationId, e);
            }
        }
        return list;
    }

    /** 保存消息 — 先写DB，再删列表缓存 */
    @Transactional
    public AiMessage save(Long conversationId, String role, String content) {
        AiMessage msg = new AiMessage();
        msg.setConversationId(conversationId);
        msg.setRole(role);
        msg.setContent(content);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
        evictMessageListCache(conversationId);
        return msg;
    }

    /** 删除消息 */
    @Transactional
    public void delete(Long id) {
        messageMapper.deleteById(id);
    }

    /** 删除对话下所有消息 — 先删DB，再删缓存 */
    @Transactional
    public void deleteByConversationId(Long conversationId) {
        messageMapper.deleteByConversationId(conversationId);
        evictMessageListCache(conversationId);
    }

    private void evictMessageListCache(Long conversationId) {
        try { redisTemplate.delete(MSG_LIST_KEY_PREFIX + conversationId); }
        catch (Exception e) { log.warn("删除消息列表缓存失败 conversationId={}", conversationId, e); }
    }
}
