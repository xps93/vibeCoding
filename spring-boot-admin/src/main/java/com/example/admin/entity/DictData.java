package com.example.admin.entity;

import java.time.LocalDateTime;

public class DictData {
    private Long id;
    private Long dictTypeId;
    private String dictLabel;
    private String dictValue;
    private Integer dictSort;
    private Integer status;
    private LocalDateTime createTime;

    public DictData() {}

    public DictData(Long id, Long dictTypeId, String dictLabel, String dictValue, Integer dictSort, Integer status) {
        this.id = id;
        this.dictTypeId = dictTypeId;
        this.dictLabel = dictLabel;
        this.dictValue = dictValue;
        this.dictSort = dictSort;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDictTypeId() { return dictTypeId; }
    public void setDictTypeId(Long dictTypeId) { this.dictTypeId = dictTypeId; }
    public String getDictLabel() { return dictLabel; }
    public void setDictLabel(String dictLabel) { this.dictLabel = dictLabel; }
    public String getDictValue() { return dictValue; }
    public void setDictValue(String dictValue) { this.dictValue = dictValue; }
    public Integer getDictSort() { return dictSort; }
    public void setDictSort(Integer dictSort) { this.dictSort = dictSort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
