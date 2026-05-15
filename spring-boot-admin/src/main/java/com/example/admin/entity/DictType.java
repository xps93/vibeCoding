package com.example.admin.entity;

import java.time.LocalDateTime;

public class DictType {
    private Long id;
    private String dictName;
    private String dictType;
    private Integer status;
    private LocalDateTime createTime;

    public DictType() {}

    public DictType(Long id, String dictName, String dictType, Integer status) {
        this.id = id;
        this.dictName = dictName;
        this.dictType = dictType;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDictName() { return dictName; }
    public void setDictName(String dictName) { this.dictName = dictName; }
    public String getDictType() { return dictType; }
    public void setDictType(String dictType) { this.dictType = dictType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
