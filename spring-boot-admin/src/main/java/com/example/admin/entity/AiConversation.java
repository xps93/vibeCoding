package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * AI对话实体
 */
public class AiConversation {
    private Long id;
    private String title;
    private Long userId;
    private String modelId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    /** 以下为查询结果扩展字段，非表字段 */
    private String username;
    private Integer messageCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }
}
