package com.example.admin.service.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 智谱清言（ChatGLM）模型提供者
 * 兼容OpenAI接口格式
 */
@Component
public class ZhipuProvider extends AbstractOpenAIProvider {

    @Value("${zhipu.api-key:}")
    private String apiKey;

    @Value("${zhipu.base-url:https://open.bigmodel.cn/api/paas}")
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
        return "智谱清言";
    }
}
