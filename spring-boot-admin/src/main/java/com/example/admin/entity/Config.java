package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 */
public class Config {
    // 配置ID
    private Long id;
    // 配置名称
    private String configName;
    // 配置键
    private String configKey;
    // 配置值
    private String configValue;
    private Integer configType; // 0=builtin, 1=custom
    // 创建时间
    private LocalDateTime createTime;

    /**
     * 无参构造方法
     */
    public Config() {}

    /**
     * 全参构造方法（不含创建时间，创建时间自动设为当前时间）
     */
    public Config(Long id, String configName, String configKey, String configValue, Integer configType) {
        this.id = id;
        this.configName = configName;
        this.configKey = configKey;
        this.configValue = configValue;
        this.configType = configType;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取配置ID
     */
    public Long getId() { return id; }
    /**
     * 设置配置ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取配置名称
     */
    public String getConfigName() { return configName; }
    /**
     * 设置配置名称
     */
    public void setConfigName(String configName) { this.configName = configName; }
    /**
     * 获取配置键
     */
    public String getConfigKey() { return configKey; }
    /**
     * 设置配置键
     */
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    /**
     * 获取配置值
     */
    public String getConfigValue() { return configValue; }
    /**
     * 设置配置值
     */
    public void setConfigValue(String configValue) { this.configValue = configValue; }
    /**
     * 获取配置类型（0=内置, 1=自定义）
     */
    public Integer getConfigType() { return configType; }
    /**
     * 设置配置类型（0=内置, 1=自定义）
     */
    public void setConfigType(Integer configType) { this.configType = configType; }
    /**
     * 获取创建时间
     */
    public LocalDateTime getCreateTime() { return createTime; }
    /**
     * 设置创建时间
     */
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
