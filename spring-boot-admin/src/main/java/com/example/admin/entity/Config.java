package com.example.admin.entity;

import java.time.LocalDateTime;

public class Config {
    private Long id;
    private String configName;
    private String configKey;
    private String configValue;
    private Integer configType; // 0=builtin, 1=custom
    private LocalDateTime createTime;

    public Config() {}

    public Config(Long id, String configName, String configKey, String configValue, Integer configType) {
        this.id = id;
        this.configName = configName;
        this.configKey = configKey;
        this.configValue = configValue;
        this.configType = configType;
        this.createTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getConfigName() { return configName; }
    public void setConfigName(String configName) { this.configName = configName; }
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
    public Integer getConfigType() { return configType; }
    public void setConfigType(Integer configType) { this.configType = configType; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
