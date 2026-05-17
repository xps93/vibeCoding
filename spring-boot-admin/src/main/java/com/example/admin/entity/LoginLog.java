package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * 登录日志实体类
 */
public class LoginLog {
    // 日志ID
    private Long id;
    // 用户名
    private String userName;
    private Integer status; // 0=success, 1=fail
    // IP地址
    private String ipAddr;
    // 登录消息
    private String msg;
    // 登录时间
    private LocalDateTime loginTime;

    /**
     * 无参构造方法
     */
    public LoginLog() {}

    /**
     * 全参构造方法
     */
    public LoginLog(Long id, String userName, Integer status, String ipAddr, String msg, LocalDateTime loginTime) {
        this.id = id;
        this.userName = userName;
        this.status = status;
        this.ipAddr = ipAddr;
        this.msg = msg;
        this.loginTime = loginTime;
    }

    /**
     * 获取日志ID
     */
    public Long getId() { return id; }
    /**
     * 设置日志ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取用户名
     */
    public String getUserName() { return userName; }
    /**
     * 设置用户名
     */
    public void setUserName(String userName) { this.userName = userName; }
    /**
     * 获取状态（0=成功, 1=失败）
     */
    public Integer getStatus() { return status; }
    /**
     * 设置状态（0=成功, 1=失败）
     */
    public void setStatus(Integer status) { this.status = status; }
    /**
     * 获取IP地址
     */
    public String getIpAddr() { return ipAddr; }
    /**
     * 设置IP地址
     */
    public void setIpAddr(String ipAddr) { this.ipAddr = ipAddr; }
    /**
     * 获取登录消息
     */
    public String getMsg() { return msg; }
    /**
     * 设置登录消息
     */
    public void setMsg(String msg) { this.msg = msg; }
    /**
     * 获取登录时间
     */
    public LocalDateTime getLoginTime() { return loginTime; }
    /**
     * 设置登录时间
     */
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
}
