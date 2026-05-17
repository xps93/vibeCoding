package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
public class OperLog {
    // 日志ID
    private Long id;
    // 操作模块标题
    private String title;
    // 业务类型
    private Integer businessType;
    // 方法名称
    private String method;
    // 请求方式
    private String requestMethod;
    // 操作人员
    private String operName;
    // 操作URL
    private String operUrl;
    // 操作IP
    private String operIp;
    // 请求参数
    private String operParam;
    // 返回结果
    private String jsonResult;
    // 状态（0=正常, 1=异常）
    private Integer status;
    // 错误消息
    private String errorMsg;
    // 操作时间
    private LocalDateTime operTime;

    /**
     * 无参构造方法
     */
    public OperLog() {}

    /**
     * 全参构造方法
     */
    public OperLog(Long id, String title, Integer businessType, String method, String requestMethod,
                   String operName, String operUrl, String operIp, String operParam, String jsonResult,
                   Integer status, String errorMsg, LocalDateTime operTime) {
        this.id = id;
        this.title = title;
        this.businessType = businessType;
        this.method = method;
        this.requestMethod = requestMethod;
        this.operName = operName;
        this.operUrl = operUrl;
        this.operIp = operIp;
        this.operParam = operParam;
        this.jsonResult = jsonResult;
        this.status = status;
        this.errorMsg = errorMsg;
        this.operTime = operTime;
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
     * 获取操作模块标题
     */
    public String getTitle() { return title; }
    /**
     * 设置操作模块标题
     */
    public void setTitle(String title) { this.title = title; }
    /**
     * 获取业务类型
     */
    public Integer getBusinessType() { return businessType; }
    /**
     * 设置业务类型
     */
    public void setBusinessType(Integer businessType) { this.businessType = businessType; }
    /**
     * 获取方法名称
     */
    public String getMethod() { return method; }
    /**
     * 设置方法名称
     */
    public void setMethod(String method) { this.method = method; }
    /**
     * 获取请求方式
     */
    public String getRequestMethod() { return requestMethod; }
    /**
     * 设置请求方式
     */
    public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }
    /**
     * 获取操作人员
     */
    public String getOperName() { return operName; }
    /**
     * 设置操作人员
     */
    public void setOperName(String operName) { this.operName = operName; }
    /**
     * 获取操作URL
     */
    public String getOperUrl() { return operUrl; }
    /**
     * 设置操作URL
     */
    public void setOperUrl(String operUrl) { this.operUrl = operUrl; }
    /**
     * 获取操作IP
     */
    public String getOperIp() { return operIp; }
    /**
     * 设置操作IP
     */
    public void setOperIp(String operIp) { this.operIp = operIp; }
    /**
     * 获取请求参数
     */
    public String getOperParam() { return operParam; }
    /**
     * 设置请求参数
     */
    public void setOperParam(String operParam) { this.operParam = operParam; }
    /**
     * 获取返回结果
     */
    public String getJsonResult() { return jsonResult; }
    /**
     * 设置返回结果
     */
    public void setJsonResult(String jsonResult) { this.jsonResult = jsonResult; }
    /**
     * 获取状态（0=正常, 1=异常）
     */
    public Integer getStatus() { return status; }
    /**
     * 设置状态（0=正常, 1=异常）
     */
    public void setStatus(Integer status) { this.status = status; }
    /**
     * 获取错误消息
     */
    public String getErrorMsg() { return errorMsg; }
    /**
     * 设置错误消息
     */
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    /**
     * 获取操作时间
     */
    public LocalDateTime getOperTime() { return operTime; }
    /**
     * 设置操作时间
     */
    public void setOperTime(LocalDateTime operTime) { this.operTime = operTime; }
}
