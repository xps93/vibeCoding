package com.example.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理活跃的SSE流会话，支持按对话ID停止流式生成和查询状态
 */
@Component
public class StreamSessionManager {

    private static final Logger log = LoggerFactory.getLogger(StreamSessionManager.class);

    private final ConcurrentHashMap<Long, SseEmitter> sessions = new ConcurrentHashMap<>();

    /** 注册活跃流 */
    public void register(Long conversationId, Long userId, SseEmitter emitter) {
        sessions.put(conversationId, emitter);
        log.debug("SSE stream registered: conversationId={}, userId={}", conversationId, userId);
        emitter.onCompletion(() -> {
            sessions.remove(conversationId);
            log.debug("SSE stream completed: conversationId={}", conversationId);
        });
        emitter.onTimeout(() -> {
            sessions.remove(conversationId);
            log.debug("SSE stream timeout: conversationId={}", conversationId);
        });
        emitter.onError(e -> {
            sessions.remove(conversationId);
            log.debug("SSE stream error: conversationId={}", conversationId, e);
        });
    }

    /** 停止指定对话的流 */
    public boolean stop(Long conversationId) {
        SseEmitter emitter = sessions.remove(conversationId);
        if (emitter != null) {
            try {
                emitter.complete();
                log.info("SSE stream stopped by user: conversationId={}", conversationId);
                return true;
            } catch (Exception e) {
                log.warn("Failed to complete SSE emitter: conversationId={}", conversationId, e);
            }
        }
        return false;
    }

    /** 查询指定对话是否有活跃流 */
    public boolean isStreaming(Long conversationId) {
        return sessions.containsKey(conversationId);
    }

    /** 获取当前活跃流数量 */
    public int activeCount() {
        return sessions.size();
    }
}
