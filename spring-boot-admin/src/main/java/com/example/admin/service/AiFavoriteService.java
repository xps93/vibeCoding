package com.example.admin.service;

import com.example.admin.entity.AiFavorite;
import com.example.admin.mapper.AiFavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 对话收藏服务
 */
@Service
public class AiFavoriteService {

    @Autowired
    private AiFavoriteMapper favoriteMapper;

    /** 添加收藏，返回收藏记录；已收藏则返回null */
    public AiFavorite add(Long userId, Long conversationId) {
        if (favoriteMapper.countByUserAndConversation(userId, conversationId) > 0) {
            return null;
        }
        AiFavorite fav = new AiFavorite();
        fav.setUserId(userId);
        fav.setConversationId(conversationId);
        fav.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(fav);
        return fav;
    }

    /** 取消收藏 */
    public boolean remove(Long userId, Long conversationId) {
        return favoriteMapper.deleteByUserAndConversation(userId, conversationId) > 0;
    }

    /** 查询用户收藏，支持按分组和关键词过滤 */
    public List<AiFavorite> listByUser(Long userId, Long groupId, String keyword) {
        List<AiFavorite> list = favoriteMapper.selectByUserId(userId, groupId, keyword);
        return list != null ? list : Collections.emptyList();
    }

    /** 检查是否已收藏 */
    public boolean isFavorited(Long userId, Long conversationId) {
        return favoriteMapper.countByUserAndConversation(userId, conversationId) > 0;
    }

    /** 移动收藏到指定分组 */
    public boolean moveToGroup(Long userId, Long conversationId, Long groupId) {
        if (favoriteMapper.countByUserAndConversation(userId, conversationId) == 0) {
            return false;
        }
        favoriteMapper.updateGroup(userId, conversationId, groupId);
        return true;
    }
}
