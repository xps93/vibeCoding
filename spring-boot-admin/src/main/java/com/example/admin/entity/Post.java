package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * 岗位实体类
 */
public class Post {
    // 岗位ID
    private Long id;
    // 岗位编码
    private String postCode;
    // 岗位名称
    private String postName;
    // 显示顺序
    private Integer postSort;
    // 状态（0=正常, 1=停用）
    private Integer status;
    // 创建时间
    private LocalDateTime createTime;

    /**
     * 无参构造方法
     */
    public Post() {}

    /**
     * 全参构造方法（不含创建时间，创建时间自动设为当前时间）
     */
    public Post(Long id, String postCode, String postName, Integer postSort, Integer status) {
        this.id = id;
        this.postCode = postCode;
        this.postName = postName;
        this.postSort = postSort;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取岗位ID
     */
    public Long getId() { return id; }
    /**
     * 设置岗位ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取岗位编码
     */
    public String getPostCode() { return postCode; }
    /**
     * 设置岗位编码
     */
    public void setPostCode(String postCode) { this.postCode = postCode; }
    /**
     * 获取岗位名称
     */
    public String getPostName() { return postName; }
    /**
     * 设置岗位名称
     */
    public void setPostName(String postName) { this.postName = postName; }
    /**
     * 获取显示顺序
     */
    public Integer getPostSort() { return postSort; }
    /**
     * 设置显示顺序
     */
    public void setPostSort(Integer postSort) { this.postSort = postSort; }
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
