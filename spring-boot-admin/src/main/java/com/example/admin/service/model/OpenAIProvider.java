package com.example.admin.service.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * OpenAI 模型提供者
 * API密钥通过后台模型配置动态传入，此处仅作为兜底默认值
 */
@Component
public class OpenAIProvider extends AbstractOpenAIProvider {

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.base-url:https://api.openai.com}")
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
        return "OpenAI";
    }
}
