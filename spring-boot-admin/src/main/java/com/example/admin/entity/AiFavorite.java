package com.example.admin.entity;

import java.time.LocalDateTime;

/**
 * 对话收藏实体
 */
public class AiFavorite {
    private Long id;
    private Long userId;
    private Long conversationId;
    private Long groupId;
    private LocalDateTime createTime;
    /** 以下为查询结果扩展字段 */
    private String title;        // 对话标题（关联查询）
    private Long convUserId;     // 对话所属用户ID
    private String groupName;    // 分组名称（关联查询）

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getConvUserId() { return convUserId; }
    public void setConvUserId(Long convUserId) { this.convUserId = convUserId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}
