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
     * 同步聊天（非流式），直接返回完整回复文本。
     * 用于查询分析等不需要流式输出的场景。
     *
     * @param messages    消息列表
     * @param model       模型名称
     * @param temperature 温度参数，null 则使用默认值
     * @param maxTokens   最大 token 数，null 则使用默认值
     * @return 完整的回复文本
     */
    default String chatSync(List<Map<String, String>> messages, String model,
                            Double temperature, Integer maxTokens) {
        throw new UnsupportedOperationException("同步调用未实现: " + getProviderName());
    }

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
