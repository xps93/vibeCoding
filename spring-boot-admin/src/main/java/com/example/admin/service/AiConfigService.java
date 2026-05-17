package com.example.admin.service;

import com.example.admin.entity.AiConfig;
import com.example.admin.mapper.AiConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AiConfigService {

    @Autowired
    private AiConfigMapper configMapper;

    /** 获取用户AI配置，不存在则返回默认值 */
    public AiConfig getByUserId(Long userId) {
        AiConfig config = configMapper.selectByUserId(userId);
        if (config == null) {
            config = new AiConfig();
            config.setUserId(userId);
            config.setTemperature(0.7);
            config.setMaxTokens(2048);
            config.setSystemPrompt("你是一个有帮助的AI助手，请用简洁清晰的中文回答问题。");
        }
        return config;
    }

    /** 保存用户AI配置 */
    @Transactional
    public AiConfig save(AiConfig config) {
        AiConfig existing = configMapper.selectByUserId(config.getUserId());
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            config.setCreateTime(now);
            config.setUpdateTime(now);
            configMapper.insert(config);
        } else {
            existing.setTemperature(config.getTemperature());
            existing.setMaxTokens(config.getMaxTokens());
            existing.setSystemPrompt(config.getSystemPrompt());
            existing.setUpdateTime(now);
            configMapper.update(existing);
            return existing;
        }
        return config;
    }
}
