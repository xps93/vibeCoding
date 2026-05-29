package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * 系统错误码实体 — DB存储，Redis缓存，中英文双语
 */
public class SysErrorCode {

    private Integer code;
    private String zhMsg;
    private String enMsg;
    private String module;
    private LocalDateTime createTime;

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public String getZhMsg() { return zhMsg; }
    public void setZhMsg(String zhMsg) { this.zhMsg = zhMsg; }

    public String getEnMsg() { return enMsg; }
    public void setEnMsg(String enMsg) { this.enMsg = enMsg; }

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
