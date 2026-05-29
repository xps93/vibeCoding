package com.example.admin.service.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 月之暗面（Kimi）模型提供者
 * 兼容OpenAI接口格式
 */
@Component
public class MoonshotProvider extends AbstractOpenAIProvider {

    @Value("${moonshot.api-key:}")
    private String apiKey;

    @Value("${moonshot.base-url:https://api.moonshot.cn}")
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
        return "月之暗面";
    }
}
