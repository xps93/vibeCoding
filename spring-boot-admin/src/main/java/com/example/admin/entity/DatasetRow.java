package com.example.admin.entity;

/**
 * 数据集行数据实体
 */
public class DatasetRow {
    private Long id;
    private Long datasetId;
    private Integer rowIndex;
    private String rowData;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDatasetId() { return datasetId; }
    public void setDatasetId(Long datasetId) { this.datasetId = datasetId; }
    public Integer getRowIndex() { return rowIndex; }
    public void setRowIndex(Integer rowIndex) { this.rowIndex = rowIndex; }
    public String getRowData() { return rowData; }
    public void setRowData(String rowData) { this.rowData = rowData; }
}
