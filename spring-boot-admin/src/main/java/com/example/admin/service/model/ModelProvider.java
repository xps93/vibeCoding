package com.example.admin.service.model;

import java.util.List;
import java.util.Map;

/**
 * AI模型提供者接口，定义统一的模型调用规范
 */
public interface ModelProvider {

    /**
     * 流式聊天，通过回调返回每段内容
     */
    void streamChat(List<Map<String, String>> messages, String model,
                    Double temperature, Integer maxTokens,
                    StreamCallback callback);

    /**
     * 获取模型提供商标识
     */
    String getProviderName();

    /**
     * 流式回调接口
     */
    interface StreamCallback {
        void onContent(String content);
        void onReasoningContent(String reasoningContent);
        void onDone();
        void onError(String error);
    }
}
