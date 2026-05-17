package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * 定时任务实体类
 */
public class Job {
    // 任务ID
    private Long id;
    // 任务名称
    private String jobName;
    // 任务组名
    private String jobGroup;
    // 调用目标方法
    private String invokeTarget;
    // cron执行表达式
    private String cronExpression;
    // 状态（0=正常, 1=停用）
    private Integer status;
    // 创建时间
    private LocalDateTime createTime;

    /**
     * 无参构造方法
     */
    public Job() {}

    /**
     * 全参构造方法（不含创建时间，创建时间自动设为当前时间）
     */
    public Job(Long id, String jobName, String jobGroup, String invokeTarget, String cronExpression, Integer status) {
        this.id = id;
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.invokeTarget = invokeTarget;
        this.cronExpression = cronExpression;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取任务ID
     */
    public Long getId() { return id; }
    /**
     * 设置任务ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取任务名称
     */
    public String getJobName() { return jobName; }
    /**
     * 设置任务名称
     */
    public void setJobName(String jobName) { this.jobName = jobName; }
    /**
     * 获取任务组名
     */
    public String getJobGroup() { return jobGroup; }
    /**
     * 设置任务组名
     */
    public void setJobGroup(String jobGroup) { this.jobGroup = jobGroup; }
    /**
     * 获取调用目标方法
     */
    public String getInvokeTarget() { return invokeTarget; }
    /**
     * 设置调用目标方法
     */
    public void setInvokeTarget(String invokeTarget) { this.invokeTarget = invokeTarget; }
    /**
     * 获取cron执行表达式
     */
    public String getCronExpression() { return cronExpression; }
    /**
     * 设置cron执行表达式
     */
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
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
