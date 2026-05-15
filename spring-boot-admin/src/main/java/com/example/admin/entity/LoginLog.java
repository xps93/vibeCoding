package com.example.admin.entity;

import java.time.LocalDateTime;

public class LoginLog {
    private Long id;
    private String userName;
    private Integer status; // 0=success, 1=fail
    private String ipAddr;
    private String msg;
    private LocalDateTime loginTime;

    public LoginLog() {}

    public LoginLog(Long id, String userName, Integer status, String ipAddr, String msg, LocalDateTime loginTime) {
        this.id = id;
        this.userName = userName;
        this.status = status;
        this.ipAddr = ipAddr;
        this.msg = msg;
        this.loginTime = loginTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getIpAddr() { return ipAddr; }
    public void setIpAddr(String ipAddr) { this.ipAddr = ipAddr; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
}
