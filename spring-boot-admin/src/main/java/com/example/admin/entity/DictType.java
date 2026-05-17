package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * 字典类型实体类
 */
public class DictType {
    // 字典ID
    private Long id;
    // 字典名称
    private String dictName;
    // 字典类型
    private String dictType;
    // 状态（0=正常, 1=停用）
    private Integer status;
    // 创建时间
    private LocalDateTime createTime;

    /**
     * 无参构造方法
     */
    public DictType() {}

    /**
     * 全参构造方法（不含创建时间，创建时间自动设为当前时间）
     */
    public DictType(Long id, String dictName, String dictType, Integer status) {
        this.id = id;
        this.dictName = dictName;
        this.dictType = dictType;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取字典ID
     */
    public Long getId() { return id; }
    /**
     * 设置字典ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取字典名称
     */
    public String getDictName() { return dictName; }
    /**
     * 设置字典名称
     */
    public void setDictName(String dictName) { this.dictName = dictName; }
    /**
     * 获取字典类型
     */
    public String getDictType() { return dictType; }
    /**
     * 设置字典类型
     */
    public void setDictType(String dictType) { this.dictType = dictType; }
    /**
     * 获取状态（0=正常, 1=停用）
     */
    public Integer getStatus() { return status; }
    /**
     * 设置状态（0=正常, 1=停用）
     */
    public void setStatus(Integer status) { this.status = status; }
    /**
     * 获取创建时间
     */
    public LocalDateTime getCreateTime() { return createTime; }
    /**
     * 设置创建时间
     */
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
