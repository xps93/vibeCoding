package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * 通知公告实体类
 */
public class Notice {
    // 公告ID
    private Long id;
    // 公告标题
    private String noticeTitle;
    private Integer noticeType; // 1=notice, 2=announcement
    // 公告内容
    private String noticeContent;
    // 状态（0=正常, 1=停用）
    private Integer status;
    // 创建时间
    private LocalDateTime createTime;

    /**
     * 无参构造方法
     */
    public Notice() {}

    /**
     * 全参构造方法（不含创建时间，创建时间自动设为当前时间）
     */
    public Notice(Long id, String noticeTitle, Integer noticeType, String noticeContent, Integer status) {
        this.id = id;
        this.noticeTitle = noticeTitle;
        this.noticeType = noticeType;
        this.noticeContent = noticeContent;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取公告ID
     */
    public Long getId() { return id; }
    /**
     * 设置公告ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取公告标题
     */
    public String getNoticeTitle() { return noticeTitle; }
    /**
     * 设置公告标题
     */
    public void setNoticeTitle(String noticeTitle) { this.noticeTitle = noticeTitle; }
    /**
     * 获取公告类型（1=通知, 2=公告）
     */
    public Integer getNoticeType() { return noticeType; }
    /**
     * 设置公告类型（1=通知, 2=公告）
     */
    public void setNoticeType(Integer noticeType) { this.noticeType = noticeType; }
    /**
     * 获取公告内容
     */
    public String getNoticeContent() { return noticeContent; }
    /**
     * 设置公告内容
     */
    public void setNoticeContent(String noticeContent) { this.noticeContent = noticeContent; }
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
