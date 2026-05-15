package com.example.admin.entity;

import java.time.LocalDateTime;

public class Post {
    private Long id;
    private String postCode;
    private String postName;
    private Integer postSort;
    private Integer status;
    private LocalDateTime createTime;

    public Post() {}

    public Post(Long id, String postCode, String postName, Integer postSort, Integer status) {
        this.id = id;
        this.postCode = postCode;
        this.postName = postName;
        this.postSort = postSort;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }
    public String getPostName() { return postName; }
    public void setPostName(String postName) { this.postName = postName; }
    public Integer getPostSort() { return postSort; }
    public void setPostSort(Integer postSort) { this.postSort = postSort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
