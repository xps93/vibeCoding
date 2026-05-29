package com.example.admin.service.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 模型提供者工厂，根据模型提供商标识路由到对应实现
 */
@Component
public class ModelProviderFactory {

    private final Map<String, ModelProvider> providerMap = new HashMap<>();
    private final ModelProvider defaultProvider;

    @Autowired
    public ModelProviderFactory(DeepSeekProvider deepSeekProvider,
                                OpenAIProvider openAIProvider,
                                QwenProvider qwenProvider,
                                ZhipuProvider zhipuProvider,
                                MoonshotProvider moonshotProvider) {
        this.defaultProvider = deepSeekProvider;
        providerMap.put("deepseek", deepSeekProvider);
        providerMap.put("openai", openAIProvider);
        providerMap.put("qwen", qwenProvider);
        providerMap.put("zhipu", zhipuProvider);
        providerMap.put("moonshot", moonshotProvider);
        providerMap.put("kimi", moonshotProvider); // 别名
    }

    /**
     * 根据提供商标识获取对应的模型提供者
     * @param provider 提供商标识（如 deepseek, openai, qwen, zhipu, moonshot）
     * @return 对应的ModelProvider，未找到时返回默认的DeepSeek
     */
    public ModelProvider getProvider(String provider) {
        if (provider == null || provider.isEmpty()) {
            return defaultProvider;
        }
        ModelProvider p = providerMap.get(provider.toLowerCase());
        return p != null ? p : defaultProvider;
    }

    /**
     * 注册自定义提供者（支持动态扩展）
     */
    public void register(String key, ModelProvider provider) {
        providerMap.put(key.toLowerCase(), provider);
    }
}
