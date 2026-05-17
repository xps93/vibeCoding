package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * 字典数据实体类
 */
public class DictData {
    // 字典数据ID
    private Long id;
    // 字典类型ID
    private Long dictTypeId;
    // 字典标签
    private String dictLabel;
    // 字典键值
    private String dictValue;
    // 排序号
    private Integer dictSort;
    // 状态（0=正常, 1=停用）
    private Integer status;
    // 创建时间
    private LocalDateTime createTime;

    /**
     * 无参构造方法
     */
    public DictData() {}

    /**
     * 全参构造方法（不含创建时间，创建时间自动设为当前时间）
     */
    public DictData(Long id, Long dictTypeId, String dictLabel, String dictValue, Integer dictSort, Integer status) {
        this.id = id;
        this.dictTypeId = dictTypeId;
        this.dictLabel = dictLabel;
        this.dictValue = dictValue;
        this.dictSort = dictSort;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取字典数据ID
     */
    public Long getId() { return id; }
    /**
     * 设置字典数据ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取字典类型ID
     */
    public Long getDictTypeId() { return dictTypeId; }
    /**
     * 设置字典类型ID
     */
    public void setDictTypeId(Long dictTypeId) { this.dictTypeId = dictTypeId; }
    /**
     * 获取字典标签
     */
    public String getDictLabel() { return dictLabel; }
    /**
     * 设置字典标签
     */
    public void setDictLabel(String dictLabel) { this.dictLabel = dictLabel; }
    /**
     * 获取字典键值
     */
    public String getDictValue() { return dictValue; }
    /**
     * 设置字典键值
     */
    public void setDictValue(String dictValue) { this.dictValue = dictValue; }
    /**
     * 获取排序号
     */
    public Integer getDictSort() { return dictSort; }
    /**
     * 设置排序号
     */
    public void setDictSort(Integer dictSort) { this.dictSort = dictSort; }
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
