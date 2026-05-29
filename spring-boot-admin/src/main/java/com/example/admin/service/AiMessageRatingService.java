package com.example.admin.service;

import com.example.admin.entity.AiMessageRating;
import com.example.admin.mapper.AiMessageRatingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AiMessageRatingService {

    @Autowired
    private AiMessageRatingMapper ratingMapper;

    /** 提交评分：赞/踩。重复评分会覆盖 */
    public AiMessageRating rate(Long messageId, Long userId, Long conversationId, Integer rating) {
        // 先删除旧评分
        ratingMapper.delete(messageId, userId);
        // 插入新评分
        AiMessageRating r = new AiMessageRating();
        r.setMessageId(messageId);
        r.setUserId(userId);
        r.setConversationId(conversationId);
        r.setRating(rating);
        r.setCreateTime(LocalDateTime.now());
        ratingMapper.insert(r);
        return r;
    }

    /** 取消评分 */
    public void cancelRate(Long messageId, Long userId) {
        ratingMapper.delete(messageId, userId);
    }

    /** 查询用户对某条消息的评分 */
    public AiMessageRating getUserRating(Long messageId, Long userId) {
        return ratingMapper.selectByMessageAndUser(messageId, userId);
    }

    /** 查询对话中所有评分 */
    public List<AiMessageRating> listByConversation(Long conversationId) {
        return ratingMapper.selectByConversationId(conversationId);
    }

    /** 查询对话中消息的评分统计 */
    public List<Map<String, Object>> getRatingStats(Long conversationId) {
        return ratingMapper.selectRatingStatsByConversation(conversationId);
    }
}
