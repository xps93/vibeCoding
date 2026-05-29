package com.example.admin.service;

import com.example.admin.entity.AiConversation;
import com.example.admin.entity.AiMessage;
import com.example.admin.entity.AiModel;
import com.example.admin.entity.User;
import com.example.admin.rag.QueryAnalysisService;
import com.example.admin.rag.QueryParser;
import com.example.admin.rag.dto.PropertySearchRequest;
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
import java.util.concurrent.CompletableFuture;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    @Autowired private AiConversationService conversationService;
    @Autowired private AiMessageService messageService;
    @Autowired private ModelProviderFactory providerFactory;
    @Autowired private AiModelService modelService;
    @Autowired private AiDocumentService documentService;
    @Autowired private StreamSessionManager streamSessionManager;
    @Autowired private WebSearchService webSearchService;
    @Autowired(required = false) private PropertyRetrievalService propertyRetrievalService;
    @Autowired(required = false) private QueryAnalysisService queryAnalysisService;

    /**
     * SSE 流式聊天。
     * ragEnabled: true=强制RAG检索 / false=自动检测是否为房产查询
     */
    public SseEmitter chat(User user, Long conversationId, String content, Long documentId,
                           String modelId, List<Long> knowledgeBaseIds, Double temperature,
                           Integer maxTokens, String systemPrompt, Boolean webSearch,
                           Boolean ragEnabled) {
        SseEmitter emitter = new SseEmitter(300000L);

        log.info("[Chat] 收到请求 convId={} userId={} docId={} ragEnabled={} webSearch={}",
                conversationId, user.getId(), documentId, ragEnabled, webSearch);

        // 1. 会话校验
        AiConversation conv = conversationService.getById(conversationId);
        if (conv == null || conv.getUserId() == null || !conv.getUserId().equals(user.getId())) {
            log.warn("[Chat] 会话校验失败 convId={}", conversationId);
            sendSseError(emitter, "对话不存在或无权访问");
            return emitter;
        }

        // 2. 文档注入 + 保存用户消息
        String finalContent = injectDocumentContent(content, documentId);
        messageService.save(conversationId, "user", content != null ? content : "");

        // 3. 模型解析
        AiModel model = resolveModel(modelId);
        String apiModelKey = model != null ? model.getModelKey() : null;
        ModelProvider provider = providerFactory.getProvider(model != null ? model.getProvider() : null);
        log.info("[Chat] 模型解析 modelId={} modelKey={} provider={}",
                modelId, apiModelKey, provider.getProviderName());

        // 4. 对话维护
        updateConversationModel(conv, modelId);
        streamSessionManager.register(conversationId, user.getId(), emitter);
        updateConversationTitle(conv, content, finalContent);

        // 5. RAG + 联网搜索（并行）
        CompletableFuture<String> ragFuture = CompletableFuture.supplyAsync(
                () -> retrieveRagContext(finalContent, Boolean.TRUE.equals(ragEnabled)));
        CompletableFuture<String> searchFuture = CompletableFuture.supplyAsync(
                () -> doWebSearch(finalContent, Boolean.TRUE.equals(webSearch)));

        String ragContext = ragFuture.join();
        if (!ragContext.isEmpty()) {
            log.info("[Chat] RAG命中，注入上下文 {} 字符", ragContext.length());
            sendSseEvent(emitter, "{\"ragEnabled\":true}");
        }
        String searchContext = searchFuture.join();

        // 6. 构建消息
        List<Map<String, String>> messages = buildMessages(conversationId, finalContent,
                systemPrompt, ragContext, searchContext);

        // 7. 异步流式
        doStreamChat(emitter, conversationId, provider, apiModelKey,
                messages, temperature, maxTokens);

        return emitter;
    }

    // ════════════════════════════ 文档注入 ════════════════════════════

    private String injectDocumentContent(String content, Long documentId) {
        if (documentId == null) return content;
        com.example.admin.entity.AiDocument doc = documentService.getById(documentId);
        if (doc == null || doc.getContent() == null || doc.getContent().isEmpty()) {
            log.warn("[Chat] 文档不存在或为空 docId={}", documentId);
            return content;
        }
        log.info("[Chat] 注入文档 docId={} fileName={} size={}",
                documentId, doc.getFileName(), doc.getContent().length());
        return "[文档: " + doc.getFileName() + "]\n" + doc.getContent()
                + "\n\n---\n用户问题:\n" + (content != null ? content : "");
    }

    // ════════════════════════════ 对话维护 ════════════════════════════

    private void updateConversationModel(AiConversation conv, String modelId) {
        if (modelId == null || modelId.isEmpty()) return;
        conv.setModelId(modelId);
        conv.setUpdateTime(LocalDateTime.now());
        conversationService.update(conv);
    }

    private void updateConversationTitle(AiConversation conv, String content, String finalContent) {
        List<AiMessage> msgs = messageService.listByConversationId(conv.getId());
        if (msgs.size() > 1 || !"新对话".equals(conv.getTitle())) return;
        String display = (content != null && !content.isEmpty()) ? content : finalContent;
        String title = display.length() > 20 ? display.substring(0, 20) + "..." : display;
        conv.setTitle(title);
        conv.setUpdateTime(LocalDateTime.now());
        conversationService.update(conv);
        log.info("[Chat] 自动生成标题 convId={} title=\"{}\"", conv.getId(), title);
    }

    // ════════════════════════════ 上下文增强 ════════════════════════════

    private String retrieveRagContext(String query, boolean forceRag) {
        if (propertyRetrievalService == null || query == null || query.trim().isEmpty()) {
            return "";
        }
        if (!forceRag && !isPropertyQuery(query)) {
            log.info("[RAG] 非房产查询，跳过");
            return "";
        }
        log.info("[RAG] 触发检索 forceRag={}", forceRag);
        PropertySearchRequest ragReq = new PropertySearchRequest();
        ragReq.setQuery(query);
        ragReq.setTopK(5);
        analyzeQuery(query, ragReq);
        try {
            String context = propertyRetrievalService.searchAsContext(ragReq);
            log.info("[RAG] 检索完成: {} 字符", context.length());
            return context;
        } catch (Exception e) {
            log.warn("[RAG] 检索失败，回退纯AI: {}", e.getMessage());
            return "";
        }
    }

    private boolean isPropertyQuery(String query) {
        String lower = query.toLowerCase();
        return lower.contains("房") || lower.contains("小区") || lower.contains("楼盘")
                || lower.contains("户型") || lower.contains("面积") || lower.contains("房价")
                || lower.contains("买房") || lower.contains("租房") || lower.contains("二手房")
                || lower.contains("总价") || lower.contains("房产") || lower.contains("住宅")
                || lower.contains("property") || lower.contains("house") || lower.contains("apartment");
    }

    private void analyzeQuery(String query, PropertySearchRequest ragReq) {
        if (queryAnalysisService != null) {
            queryAnalysisService.analyze(query, ragReq);
        } else {
            QueryParser.parse(query, ragReq);
        }
    }

    private String doWebSearch(String query, boolean enabled) {
        if (!enabled || query == null) return "";
        log.info("[Chat] 触发联网搜索");
        String q = query.length() > 200 ? query.substring(0, 200) : query;
        String result = webSearchService.search(q);
        log.info("[Chat] 联网搜索完成: {} 字符", result != null ? result.length() : 0);
        return result != null ? result : "";
    }

    // ════════════════════════════ 消息构建 ════════════════════════════

    private List<Map<String, String>> buildMessages(Long conversationId, String currentContent,
                                                     String systemPrompt,
                                                     String ragContext, String searchContext) {
        List<Map<String, String>> messages = new ArrayList<>();

        String combined = joinNonNull("\n\n", ragContext, searchContext);

        if (systemPrompt != null || !combined.isEmpty()) {
            Map<String, String> sys = new HashMap<>();
            sys.put("role", "system");
            String text = systemPrompt != null ? systemPrompt : "";
            if (text.length() > 500) {
                log.warn("[Chat] 系统提示过长({})，截断到500字符", text.length());
                text = text.substring(0, 500);
            }
            if (!combined.isEmpty()) {
                text = text.isEmpty() ? combined : text + "\n\n" + combined;
            }
            sys.put("content", text);
            messages.add(sys);
        }

        List<AiMessage> history = messageService.listByConversationId(conversationId);
        int maxHistory = 6;
        int start = Math.max(0, history.size() - 1 - maxHistory);
        int historyCount = 0;
        for (int i = start; i < history.size() - 1; i++) {
            AiMessage m = history.get(i);
            Map<String, String> msg = new HashMap<>();
            msg.put("role", m.getRole());
            msg.put("content", m.getContent());
            messages.add(msg);
            historyCount++;
        }

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", currentContent);
        messages.add(userMsg);

        log.info("[Chat] 消息构建完成 total={} (system={} history={}/{})",
                messages.size(), combined.isEmpty() ? "无" : "有",
                historyCount, Math.max(0, history.size() - 1));

        return messages;
    }

    // ════════════════════════════ 流式调用 ════════════════════════════

    private void doStreamChat(SseEmitter emitter, Long conversationId,
                              ModelProvider provider, String apiModelKey,
                              List<Map<String, String>> messages,
                              Double temperature, Integer maxTokens) {
        String modelKey = (apiModelKey != null && !apiModelKey.isEmpty())
                ? apiModelKey : "deepseek-chat";

        log.info("[Chat] 开始流式调用 model={} temperature={} maxTokens={} msgCount={}",
                modelKey, temperature, maxTokens, messages.size());

        StringBuilder reply = new StringBuilder();
        StringBuilder reasoning = new StringBuilder();

        new Thread(() -> {
            try {
                provider.streamChat(messages, modelKey, temperature, maxTokens,
                        createCallback(emitter, conversationId, reply, reasoning));
            } catch (Exception e) {
                log.error("[Chat] 流式调用异常", e);
                sendSseError(emitter, "AI服务异常: " + e.getMessage());
                saveAssistantMessage(conversationId, reply, reasoning);
            }
        }).start();
    }

    private ModelProvider.StreamCallback createCallback(SseEmitter emitter, Long conversationId,
                                                         StringBuilder reply, StringBuilder reasoning) {
        return new ModelProvider.StreamCallback() {
            @Override
            public void onContent(String chunk) {
                reply.append(chunk);
                sendSseEvent(emitter, "{\"content\":\"" + escapeJson(chunk) + "\"}");
            }

            @Override
            public void onReasoningContent(String chunk) {
                reasoning.append(chunk);
                sendSseEvent(emitter, "{\"reasoning_content\":\"" + escapeJson(chunk) + "\"}");
            }

            @Override
            public void onDone() {
                log.info("[Chat] 流式完成 convId={} replyLen={} reasoningLen={}",
                        conversationId, reply.length(), reasoning.length());
                sendSseEvent(emitter, "{\"done\":true}");
                emitter.complete();
                saveAssistantMessage(conversationId, reply, reasoning);
            }

            @Override
            public void onError(String error) {
                log.warn("[Chat] 流式出错 convId={} error={}", conversationId, error);
                sendSseError(emitter, error);
                saveAssistantMessage(conversationId, reply, reasoning);
            }
        };
    }

    private void saveAssistantMessage(Long conversationId, StringBuilder reply, StringBuilder reasoning) {
        String content = reasoning.length() > 0
                ? "[思考]\n" + reasoning + "\n\n" + reply
                : reply.toString();
        if (!content.isEmpty()) {
            messageService.save(conversationId, "assistant", content);
        }
    }

    // ════════════════════════════ 工具 ════════════════════════════

    private AiModel resolveModel(String modelId) {
        try {
            if (modelId != null && !modelId.isEmpty()) {
                return modelService.getById(Long.valueOf(modelId));
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }

    private void sendSseEvent(SseEmitter emitter, String data) {
        try {
            emitter.send(SseEmitter.event().data(data));
        } catch (IOException e) {
            log.error("SSE send error", e);
        }
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

    private String joinNonNull(String delimiter, String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p != null && !p.isEmpty()) {
                if (sb.length() > 0) sb.append(delimiter);
                sb.append(p);
            }
        }
        return sb.toString();
    }
}
