package com.example.admin.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 */
public class User {
    // 用户ID
    private Long id;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 昵称
    private String nickname;
    // 邮箱
    private String email;
    // 手机号
    private String phone;
    // 头像URL
    private String avatar;
    private Integer status; // 0=正常, 1=停用
    // 角色ID列表
    private List<Long> roleIds;
    // 创建时间
    private LocalDateTime createTime;

    /**
     * 无参构造方法
     */
    public User() {}

    /**
     * 全参构造方法（不含创建时间，创建时间自动设为当前时间）
     */
    public User(Long id, String username, String password, String nickname, String email, String phone, Integer status, List<Long> roleIds) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.roleIds = roleIds;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取用户ID
     */
    public Long getId() { return id; }
    /**
     * 设置用户ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取用户名
     */
    public String getUsername() { return username; }
    /**
     * 设置用户名
     */
    public void setUsername(String username) { this.username = username; }
    /**
     * 获取密码
     */
    public String getPassword() { return password; }
    /**
     * 设置密码
     */
    public void setPassword(String password) { this.password = password; }
    /**
     * 获取昵称
     */
    public String getNickname() { return nickname; }
    /**
     * 设置昵称
     */
    public void setNickname(String nickname) { this.nickname = nickname; }
    /**
     * 获取邮箱
     */
    public String getEmail() { return email; }
    /**
     * 设置邮箱
     */
    public void setEmail(String email) { this.email = email; }
    /**
     * 获取手机号
     */
    public String getPhone() { return phone; }
    /**
     * 设置手机号
     */
    public void setPhone(String phone) { this.phone = phone; }
    /**
     * 获取头像URL
     */
    public String getAvatar() { return avatar; }
    /**
     * 设置头像URL
     */
    public void setAvatar(String avatar) { this.avatar = avatar; }
    /**
     * 获取状态（0=正常, 1=停用）
     */
    public Integer getStatus() { return status; }
    /**
     * 设置状态（0=正常, 1=停用）
     */
    public void setStatus(Integer status) { this.status = status; }
    /**
     * 获取角色ID列表
     */
    public List<Long> getRoleIds() { return roleIds; }
    /**
     * 设置角色ID列表
     */
    public void setRoleIds(List<Long> roleIds) { this.roleIds = roleIds; }
    /**
     * 获取创建时间
     */
    public LocalDateTime getCreateTime() { return createTime; }
    /**
     * 设置创建时间
     */
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
