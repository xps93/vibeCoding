package com.example.admin.controller;

import com.example.admin.entity.AiConversation;
import com.example.admin.entity.AiMessage;
import com.example.admin.entity.Result;
import com.example.admin.entity.User;
import com.example.admin.service.AiChatService;
import com.example.admin.service.AiConversationService;
import com.example.admin.service.AiMessageService;
import com.example.admin.service.StreamSessionManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

@Tag(name = "AI聊天", description = "AI对话与流式消息")
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private AiConversationService conversationService;

    @Autowired
    private AiMessageService messageService;

    @Autowired
    private StreamSessionManager streamSessionManager;

    /** SSE流式发送消息 */
    @PostMapping("/chat/send")
    public SseEmitter sendMessage(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> body) {
        Long conversationId = toLong(body.get("conversationId"));
        String content = toString(body.get("content"));
        Long documentId = toLong(body.get("documentId"));
        String modelId = toString(body.get("modelId"));
        List<Long> knowledgeBaseIds = toLongList(body.get("knowledgeBaseIds"));
        Double temperature = toDouble(body.get("temperature"), 0.7);
        Integer maxTokens = toInt(body.get("maxTokens"), 2048);
        String systemPrompt = toString(body.get("systemPrompt"));
        Boolean webSearch = Boolean.TRUE.equals(body.get("webSearch"));
        Boolean ragEnabled = Boolean.TRUE.equals(body.get("ragEnabled"));
        // 校验冗余，造轮子，方法传参数太多，需要遵守单一职责
        return aiChatService.chat(user, conversationId, content, documentId, modelId,
                knowledgeBaseIds, temperature, maxTokens, systemPrompt, webSearch, ragEnabled);
    }

    /** 重新生成：删除最后一条AI回复，重新发起请求 */
    @PostMapping("/chat/regenerate")
    public SseEmitter regenerate(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> body) {
        Long conversationId = toLong(body.get("conversationId"));
        if (conversationId == null) {
            SseEmitter errEmitter = new SseEmitter();
            errEmitter.completeWithError(new IllegalArgumentException("缺少对话ID"));
            return errEmitter;
        }
        AiConversation conv = conversationService.getById(conversationId);
        if (conv == null || !conv.getUserId().equals(user.getId())) {
            SseEmitter errEmitter = new SseEmitter();
            errEmitter.completeWithError(new IllegalArgumentException("对话不存在"));
            return errEmitter;
        }
        // 删除最后一条AI回复
        List<AiMessage> msgs = messageService.listByConversationId(conversationId);
        if (!msgs.isEmpty()) {
            AiMessage last = msgs.get(msgs.size() - 1);
            if ("assistant".equals(last.getRole())) {
                messageService.delete(last.getId());
            }
        }
        // 找到最后一条用户消息作为发送内容
        String content = toString(body.get("content"));
        if (content == null || content.isEmpty()) {
            // 没有指定内容则用最后一条用户消息
            for (int i = msgs.size() - 1; i >= 0; i--) {
                if ("user".equals(msgs.get(i).getRole())) {
                    content = msgs.get(i).getContent();
                    break;
                }
            }
        }
        if (content == null || content.isEmpty()) {
            SseEmitter errEmitter = new SseEmitter();
            errEmitter.completeWithError(new IllegalArgumentException("没有可重新生成的内容"));
            return errEmitter;
        }
        String modelId = toString(body.get("modelId"));
        Double temperature = toDouble(body.get("temperature"), 0.7);
        Integer maxTokens = toInt(body.get("maxTokens"), 2048);
        String systemPrompt = toString(body.get("systemPrompt"));
        Boolean webSearch = Boolean.TRUE.equals(body.get("webSearch"));
        Boolean ragEnabled = Boolean.TRUE.equals(body.get("ragEnabled"));

        return aiChatService.chat(user, conversationId, content, null, modelId,
                null, temperature, maxTokens, systemPrompt, webSearch, ragEnabled);
    }

    /** 停止生成 */
    @PostMapping("/chat/stop")
    public Result stopChat(@RequestBody Map<String, Object> body) {
        Long conversationId = toLong(body.get("conversationId"));
        if (conversationId == null) {
            return Result.error("缺少对话ID");
        }
        boolean stopped = streamSessionManager.stop(conversationId);
        Map<String, Object> data = new HashMap<>();
        data.put("stopped", stopped);
        return Result.success(data);
    }

    /** 查询对话状态 */
    @GetMapping("/chat/status")
    public Result status(@RequestParam Long conversationId) {
        Map<String, Object> data = new HashMap<>();
        data.put("streaming", streamSessionManager.isStreaming(conversationId));
        return Result.success(data);
    }

    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof Long) return (Long) val;
        if (val instanceof Integer) return ((Integer) val).longValue();
        if (val instanceof String) return Long.valueOf((String) val);
        return Long.valueOf(val.toString());
    }

    private Integer toInt(Object val, Integer defaultVal) {
        if (val == null) return defaultVal;
        if (val instanceof Integer) return (Integer) val;
        if (val instanceof Long) return ((Long) val).intValue();
        if (val instanceof Double) return ((Double) val).intValue();
        if (val instanceof String) return Integer.valueOf((String) val);
        return defaultVal;
    }

    private Double toDouble(Object val, Double defaultVal) {
        if (val == null) return defaultVal;
        if (val instanceof Double) return (Double) val;
        if (val instanceof Integer) return ((Integer) val).doubleValue();
        if (val instanceof Long) return ((Long) val).doubleValue();
        if (val instanceof String) return Double.valueOf((String) val);
        return defaultVal;
    }

    private String toString(Object val) {
        if (val == null) return null;
        return val.toString();
    }

    private List<Long> toLongList(Object val) {
        if (val == null) return new ArrayList<>();
        if (!(val instanceof List)) return new ArrayList<>();
        List<?> list = (List<?>) val;
        List<Long> result = new ArrayList<>();
        for (Object item : list) {
            result.add(toLong(item));
        }
        return result;
    }
}
