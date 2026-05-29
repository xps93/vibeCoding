package com.example.admin.service;

import com.example.admin.entity.SysSiteConfig;
import com.example.admin.mapper.SysSiteConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysSiteConfigService {

    @Autowired
    private SysSiteConfigMapper configMapper;

    /** 获取所有配置 */
    public List<SysSiteConfig> listAll() {
        return configMapper.selectAll();
    }

    /** 以Map形式返回所有配置（key->value），供前端读取 */
    public Map<String, String> getAllAsMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (SysSiteConfig c : configMapper.selectAll()) {
            map.put(c.getConfigKey(), c.getConfigValue());
        }
        return map;
    }

    /** 根据key获取配置值 */
    public String getValue(String key) {
        SysSiteConfig config = configMapper.selectByKey(key);
        return config != null ? config.getConfigValue() : null;
    }

    /** 根据ID获取配置 */
    public SysSiteConfig getById(Long id) {
        return configMapper.selectById(id);
    }

    /** 更新配置 */
    public void update(SysSiteConfig config) {
        configMapper.update(config);
    }
}
