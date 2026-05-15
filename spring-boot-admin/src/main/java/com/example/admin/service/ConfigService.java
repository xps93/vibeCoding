package com.example.admin.service;

import com.example.admin.entity.Config;
import com.example.admin.mapper.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    public List<Config> list(String keyword) {
        return configMapper.selectList(keyword);
    }

    public Config getById(Long id) {
        return configMapper.selectById(id);
    }

    public Config add(Config config) {
        config.setCreateTime(LocalDateTime.now());
        configMapper.insert(config);
        return config;
    }

    public Config update(Config config) {
        Config existing = configMapper.selectById(config.getId());
        if (existing == null) return null;

        existing.setConfigName(config.getConfigName());
        existing.setConfigKey(config.getConfigKey());
        existing.setConfigValue(config.getConfigValue());
        existing.setConfigType(config.getConfigType());

        configMapper.update(existing);
        return existing;
    }

    public void delete(Long id) {
        configMapper.deleteById(id);
    }
}
