package com.example.admin.service;

import com.example.admin.entity.Config;
import com.example.admin.mapper.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 参数配置管理 Service
 */
@Service
public class ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    /**
     * 查询参数配置列表，支持关键字模糊搜索
     */
    public List<Config> list(String keyword) {
        return configMapper.selectList(keyword);
    }

    /**
     * 根据ID查询参数配置
     */
    public Config getById(Long id) {
        return configMapper.selectById(id);
    }

    /**
     * 新增参数配置
     */
    public Config add(Config config) {
        config.setCreateTime(LocalDateTime.now());
        configMapper.insert(config);
        return config;
    }

    /**
     * 更新参数配置信息
     */
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

    /**
     * 删除参数配置
     */
    public void delete(Long id) {
        configMapper.deleteById(id);
    }
}
