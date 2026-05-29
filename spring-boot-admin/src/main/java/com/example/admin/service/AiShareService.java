package com.example.admin.service;

import com.example.admin.entity.AiShare;
import com.example.admin.mapper.AiShareMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AiShareService {

    @Autowired
    private AiShareMapper shareMapper;

    /** 创建或获取分享链接 */
    public AiShare createOrGet(Long conversationId, Long userId) {
        // 已有有效分享则直接返回
        AiShare existing = shareMapper.selectByConversationAndUser(conversationId, userId);
        if (existing != null && existing.getIsActive() == 1) {
            return existing;
        }
        AiShare share = new AiShare();
        share.setConversationId(conversationId);
        share.setUserId(userId);
        share.setShareToken(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        share.setIsActive(1);
        share.setCreateTime(LocalDateTime.now());
        shareMapper.insert(share);
        return share;
    }

    /** 通过token查询分享 */
    public AiShare getByToken(String shareToken) {
        AiShare share = shareMapper.selectByToken(shareToken);
        if (share == null || share.getIsActive() != 1) {
            return null;
        }
        return share;
    }

    /** 撤销分享（按对话ID） */
    public void revokeByConversation(Long conversationId, Long userId) {
        AiShare existing = shareMapper.selectByConversationAndUser(conversationId, userId);
        if (existing != null) {
            shareMapper.deactivate(existing.getId());
        }
    }
}
