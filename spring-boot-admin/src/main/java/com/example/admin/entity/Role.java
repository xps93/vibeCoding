package com.example.admin.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Role {
    private Long id;
    private String roleKey;   // e.g. "admin", "user"
    private String roleName;  // e.g. "超级管理员", "普通用户"
    private Integer status;   // 0=正常, 1=停用
    private List<Long> menuIds;
    private LocalDateTime createTime;

    public Role() {}

    public Role(Long id, String roleKey, String roleName, Integer status, List<Long> menuIds) {
        this.id = id;
        this.roleKey = roleKey;
        this.roleName = roleName;
        this.status = status;
        this.menuIds = menuIds;
        this.createTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoleKey() { return roleKey; }
    public void setRoleKey(String roleKey) { this.roleKey = roleKey; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<Long> getMenuIds() { return menuIds; }
    public void setMenuIds(List<Long> menuIds) { this.menuIds = menuIds; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
