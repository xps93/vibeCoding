package com.example.admin.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门实体类
 */
public class Dept {
    // 部门ID
    private Long id;
    // 父部门ID
    private Long parentId;
    // 部门名称
    private String deptName;
    // 显示顺序
    private Integer orderNum;
    // 负责人
    private String leader;
    // 联系电话
    private String phone;
    // 邮箱
    private String email;
    // 状态（0=正常, 1=停用）
    private Integer status;
    // 子部门列表
    private List<Dept> children;
    // 创建时间
    private LocalDateTime createTime;

    /**
     * 无参构造方法
     */
    public Dept() {}

    /**
     * 全参构造方法（不含子部门和创建时间，创建时间自动设为当前时间）
     */
    public Dept(Long id, Long parentId, String deptName, Integer orderNum, String leader, String phone, String email, Integer status) {
        this.id = id;
        this.parentId = parentId;
        this.deptName = deptName;
        this.orderNum = orderNum;
        this.leader = leader;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取部门ID
     */
    public Long getId() { return id; }
    /**
     * 设置部门ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取父部门ID
     */
    public Long getParentId() { return parentId; }
    /**
     * 设置父部门ID
     */
    public void setParentId(Long parentId) { this.parentId = parentId; }
    /**
     * 获取部门名称
     */
    public String getDeptName() { return deptName; }
    /**
     * 设置部门名称
     */
    public void setDeptName(String deptName) { this.deptName = deptName; }
    /**
     * 获取显示顺序
     */
    public Integer getOrderNum() { return orderNum; }
    /**
     * 设置显示顺序
     */
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
    /**
     * 获取负责人
     */
    public String getLeader() { return leader; }
    /**
     * 设置负责人
     */
    public void setLeader(String leader) { this.leader = leader; }
    /**
     * 获取联系电话
     */
    public String getPhone() { return phone; }
    /**
     * 设置联系电话
     */
    public void setPhone(String phone) { this.phone = phone; }
    /**
     * 获取邮箱
     */
    public String getEmail() { return email; }
    /**
     * 设置邮箱
     */
    public void setEmail(String email) { this.email = email; }
    /**
     * 获取状态（0=正常, 1=停用）
     */
    public Integer getStatus() { return status; }
    /**
     * 设置状态（0=正常, 1=停用）
     */
    public void setStatus(Integer status) { this.status = status; }
    /**
     * 获取子部门列表
     */
    public List<Dept> getChildren() { return children; }
    /**
     * 设置子部门列表
     */
    public void setChildren(List<Dept> children) { this.children = children; }
    /**
     * 获取创建时间
     */
    public LocalDateTime getCreateTime() { return createTime; }
    /**
     * 设置创建时间
     */
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
