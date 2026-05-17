package com.example.admin.service;

import com.example.admin.entity.AiConversation;
import com.example.admin.entity.AiMessage;
import com.example.admin.entity.AiModel;
import com.example.admin.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    @Autowired
    private AiConversationService conversationService;

    @Autowired
    private AiMessageService messageService;

    @Autowired
    private DeepSeekClient deepSeekClient;

    @Autowired
    private AiModelService modelService;

    @Autowired
    private StreamSessionManager streamSessionManager;

    /**
     * SSE流式聊天，调用DeepSeek API并逐字返回
     */
    public SseEmitter chat(User user, Long conversationId, String content, String modelId,
                           List<Long> knowledgeBaseIds, Double temperature, Integer maxTokens,
                           String systemPrompt) {
        SseEmitter emitter = new SseEmitter(300000L);

        AiConversation conv = conversationService.getById(conversationId);
        if (conv == null || !conv.getUserId().equals(user.getId())) {
            sendSseError(emitter, "对话不存在或无权访问");
            return emitter;
        }

        // 保存用户消息
        messageService.save(conversationId, "user", content);

        // 解析模型key：优先通过ID查找模型获取modelKey，否则使用默认模型
        String apiModelKey = resolveModelKey(modelId);

        // 更新对话模型
        if (modelId != null && !modelId.isEmpty()) {
            conv.setModelId(modelId);
            conv.setUpdateTime(LocalDateTime.now());
            conversationService.update(conv);
        }

        // 注册流会话，支持停止和状态查询
        streamSessionManager.register(conversationId, user.getId(), emitter);

        // 首条消息更新对话标题
        List<AiMessage> existingMsgs = messageService.listByConversationId(conversationId);
        if (existingMsgs.size() <= 1 && "新对话".equals(conv.getTitle())) {
            String newTitle = content.length() > 20 ? content.substring(0, 20) + "..." : content;
            conv.setTitle(newTitle);
            conv.setUpdateTime(LocalDateTime.now());
            conversationService.update(conv);
        }

        // 构建对话历史
        List<Map<String, String>> messages = buildMessages(existingMsgs, content, systemPrompt);

        // 调用DeepSeek API流式返回
        StringBuilder fullReply = new StringBuilder();
        new Thread(() -> {
            try {
                deepSeekClient.streamChat(messages, apiModelKey, temperature, maxTokens,
                        new DeepSeekClient.StreamCallback() {
                            @Override
                            public void onContent(String chunk) {
                                fullReply.append(chunk);
                                try {
                                    emitter.send(SseEmitter.event()
                                            .data("{\"content\":\"" + escapeJson(chunk) + "\"}"));
                                } catch (IOException e) {
                                    log.error("SSE send error", e);
                                }
                            }

                            @Override
                            public void onDone() {
                                try {
                                    emitter.send(SseEmitter.event().data("{\"done\":true}"));
                                    emitter.complete();
                                } catch (IOException e) {
                                    log.error("SSE complete error", e);
                                }
                                messageService.save(conversationId, "assistant", fullReply.toString());
                            }

                            @Override
                            public void onError(String error) {
                                sendSseError(emitter, error);
                                if (fullReply.length() > 0) {
                                    messageService.save(conversationId, "assistant", fullReply.toString());
                                }
                            }
                        });
            } catch (Exception e) {
                log.error("AI Chat error", e);
                sendSseError(emitter, "AI服务异常: " + e.getMessage());
                if (fullReply.length() > 0) {
                    messageService.save(conversationId, "assistant", fullReply.toString());
                }
            }
        }).start();

        return emitter;
    }

    /**
     * 将用户选择的模型ID解析为DeepSeek API的模型key，解析失败则返回默认模型
     */
    private String resolveModelKey(String modelId) {
        try {
            if (modelId != null && !modelId.isEmpty()) {
                Long id = Long.valueOf(modelId);
                AiModel model = modelService.getById(id);
                if (model != null && model.getModelKey() != null && !model.getModelKey().isEmpty()) {
                    return model.getModelKey();
                }
                // 兜底：模型存在但没有modelKey，用模型name
                if (model != null && model.getName() != null && !model.getName().isEmpty()) {
                    return model.getName();
                }
            }
        } catch (NumberFormatException e) {
            // modelId不是数字，直接作为模型key使用
            return modelId;
        }
        return null; // 使用DeepSeekClient的defaultModel
    }

    /**
     * 构建消息列表（系统提示 + 历史消息 + 当前消息）
     */
    private List<Map<String, String>> buildMessages(List<AiMessage> history,
                                                     String currentContent, String systemPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 系统提示
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);
            messages.add(sysMsg);
        }

        // 历史消息（排除刚保存的用户消息，因为已计入currentContent）
        for (int i = 0; i < history.size() - 1; i++) {
            AiMessage m = history.get(i);
            Map<String, String> msg = new HashMap<>();
            msg.put("role", m.getRole());
            msg.put("content", m.getContent());
            messages.add(msg);
        }

        // 当前用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", currentContent);
        messages.add(userMsg);

        return messages;
    }

    private void sendSseError(SseEmitter emitter, String error) {
        try {
            emitter.send(SseEmitter.event().data("{\"error\":\"" + escapeJson(error) + "\"}"));
            emitter.complete();
        } catch (IOException e) {
            log.error("SSE error send failed", e);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
