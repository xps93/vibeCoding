package com.example.admin.service;

import com.example.admin.entity.AiConversation;
import com.example.admin.entity.AiMessage;
import com.example.admin.entity.AiModel;
import com.example.admin.entity.User;
import com.example.admin.service.model.ModelProvider;
import com.example.admin.service.model.ModelProviderFactory;
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
    private ModelProviderFactory providerFactory;

    @Autowired
    private AiModelService modelService;

    @Autowired
    private AiDocumentService documentService;

    @Autowired
    private StreamSessionManager streamSessionManager;

    @Autowired
    private WebSearchService webSearchService;

    /**
     * SSE流式聊天，根据模型提供者路由到对应API
     */
    public SseEmitter chat(User user, Long conversationId, String content, Long documentId,
                           String modelId, List<Long> knowledgeBaseIds, Double temperature,
                           Integer maxTokens, String systemPrompt, Boolean webSearch) {
        SseEmitter emitter = new SseEmitter(300000L);

        AiConversation conv = conversationService.getById(conversationId);
        if (conv == null || conv.getUserId() == null || !conv.getUserId().equals(user.getId())) {
            sendSseError(emitter, "对话不存在或无权访问");
            return emitter;
        }

        // 如果携带了文档ID，后端自动注入文档内容作为上下文
        String finalContent = content;
        if (documentId != null) {
            com.example.admin.entity.AiDocument doc = documentService.getById(documentId);
            if (doc != null && doc.getContent() != null && !doc.getContent().isEmpty()) {
                finalContent = "[文档: " + doc.getFileName() + "]\n" + doc.getContent()
                        + "\n\n---\n用户问题:\n" + (content != null ? content : "");
            }
        }

        // 保存用户消息（仅保存用户可见内容，不含文档内容）
        messageService.save(conversationId, "user", content != null ? content : "");

        // 解析模型信息：获取modelKey和provider
        AiModel model = resolveModel(modelId);
        String apiModelKey = model != null ? model.getModelKey() : null;
        String modelProvider = model != null ? model.getProvider() : null;

        // 通过工厂获取对应的模型提供者
        ModelProvider provider = providerFactory.getProvider(modelProvider);

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
            String displayContent = content != null && !content.isEmpty() ? content : finalContent;
            String newTitle = displayContent.length() > 20 ? displayContent.substring(0, 20) + "..." : displayContent;
            conv.setTitle(newTitle);
            conv.setUpdateTime(LocalDateTime.now());
            conversationService.update(conv);
        }

        // 构建对话历史（使用包含文档内容的最终消息）
        List<Map<String, String>> messages = buildMessages(existingMsgs, finalContent, systemPrompt, webSearch);

        // 调用模型提供者流式返回
        final String resolvedModelKey = (apiModelKey != null && !apiModelKey.isEmpty())
                ? apiModelKey : "deepseek-chat";
        StringBuilder fullReply = new StringBuilder();
        StringBuilder fullReasoning = new StringBuilder();
        new Thread(() -> {
            try {
                provider.streamChat(messages, resolvedModelKey, temperature, maxTokens,
                        new ModelProvider.StreamCallback() {
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
                            public void onReasoningContent(String reasoningChunk) {
                                fullReasoning.append(reasoningChunk);
                                try {
                                    emitter.send(SseEmitter.event()
                                            .data("{\"reasoning_content\":\"" + escapeJson(reasoningChunk) + "\"}"));
                                } catch (IOException e) {
                                    log.error("SSE send reasoning error", e);
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
                                String savedContent = fullReasoning.length() > 0
                                        ? "[思考]\n" + fullReasoning.toString() + "\n\n" + fullReply.toString()
                                        : fullReply.toString();
                                messageService.save(conversationId, "assistant", savedContent);
                            }

                            @Override
                            public void onError(String error) {
                                sendSseError(emitter, error);
                                if (fullReply.length() > 0 || fullReasoning.length() > 0) {
                                    String savedContent = fullReasoning.length() > 0
                                            ? "[思考]\n" + fullReasoning.toString() + "\n\n" + fullReply.toString()
                                            : fullReply.toString();
                                    messageService.save(conversationId, "assistant", savedContent);
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
     * 根据模型ID解析AiModel对象
     */
    private AiModel resolveModel(String modelId) {
        try {
            if (modelId != null && !modelId.isEmpty()) {
                Long id = Long.valueOf(modelId);
                return modelService.getById(id);
            }
        } catch (NumberFormatException e) {
            // modelId不是数字，忽略
        }
        return null;
    }

    /**
     * 构建消息列表（系统提示 + 搜索上下文 + 历史消息 + 当前消息）
     */
    private List<Map<String, String>> buildMessages(List<AiMessage> history,
                                                     String currentContent, String systemPrompt, Boolean webSearch) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 联网搜索：调用搜索API获取实时信息，注入到系统提示中
        String searchContext = "";
        if (Boolean.TRUE.equals(webSearch) && currentContent != null && currentContent.trim().length() > 0) {
            // 提取搜索关键词（使用用户问题原文）
            String query = currentContent.length() > 200 ? currentContent.substring(0, 200) : currentContent;
            searchContext = webSearchService.search(query);
        }

        // 系统提示
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            String prompt = systemPrompt;
            // 注入联网搜索结果
            if (searchContext != null && searchContext.length() > 0) {
                prompt += "\n\n" + searchContext;
            }
            sysMsg.put("content", prompt);
            messages.add(sysMsg);
        } else if (searchContext != null && searchContext.length() > 0) {
            // 没有系统提示但有搜索结果，单独添加搜索上下文
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", searchContext);
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
