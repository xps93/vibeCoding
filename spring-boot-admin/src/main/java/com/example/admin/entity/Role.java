package com.example.admin.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体类
 */
public class Role {
    // 角色ID
    private Long id;
    private String roleKey;   // e.g. "admin", "user"
    private String roleName;  // e.g. "超级管理员", "普通用户"
    private Integer status;   // 0=正常, 1=停用
    // 菜单ID列表
    private List<Long> menuIds;
    // 创建时间
    private LocalDateTime createTime;

    /**
     * 无参构造方法
     */
    public Role() {}

    /**
     * 全参构造方法（不含创建时间，创建时间自动设为当前时间）
     */
    public Role(Long id, String roleKey, String roleName, Integer status, List<Long> menuIds) {
        this.id = id;
        this.roleKey = roleKey;
        this.roleName = roleName;
        this.status = status;
        this.menuIds = menuIds;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取角色ID
     */
    public Long getId() { return id; }
    /**
     * 设置角色ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取角色标识（如admin、user）
     */
    public String getRoleKey() { return roleKey; }
    /**
     * 设置角色标识（如admin、user）
     */
    public void setRoleKey(String roleKey) { this.roleKey = roleKey; }
    /**
     * 获取角色名称（如超级管理员、普通用户）
     */
    public String getRoleName() { return roleName; }
    /**
     * 设置角色名称（如超级管理员、普通用户）
     */
    public void setRoleName(String roleName) { this.roleName = roleName; }
    /**
     * 获取状态（0=正常, 1=停用）
     */
    public Integer getStatus() { return status; }
    /**
     * 设置状态（0=正常, 1=停用）
     */
    public void setStatus(Integer status) { this.status = status; }
    /**
     * 获取菜单ID列表
     */
    public List<Long> getMenuIds() { return menuIds; }
    /**
     * 设置菜单ID列表
     */
    public void setMenuIds(List<Long> menuIds) { this.menuIds = menuIds; }
    /**
     * 获取创建时间
     */
    public LocalDateTime getCreateTime() { return createTime; }
    /**
     * 设置创建时间
     */
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
