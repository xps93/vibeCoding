package com.example.admin.service.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 通义千问（阿里云DashScope）模型提供者
 * 兼容OpenAI接口格式
 */
@Component
public class QwenProvider extends AbstractOpenAIProvider {

    @Value("${qwen.api-key:}")
    private String apiKey;

    @Value("${qwen.base-url:https://dashscope.aliyuncs.com/compatible-mode}")
    private String baseUrl;

    @Override
    protected String getApiKey() {
        return apiKey;
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getProviderName() {
        return "通义千问";
    }
}
