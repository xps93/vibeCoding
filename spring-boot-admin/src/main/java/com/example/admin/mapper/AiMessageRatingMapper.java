package com.example.admin.mapper;

import com.example.admin.entity.AiMessageRating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AiMessageRatingMapper {

    int insert(AiMessageRating rating);

    int delete(@Param("messageId") Long messageId, @Param("userId") Long userId);

    AiMessageRating selectByMessageAndUser(@Param("messageId") Long messageId, @Param("userId") Long userId);

    List<AiMessageRating> selectByConversationId(@Param("conversationId") Long conversationId);

    /** 统计对话中每条消息的赞/踩数 */
    List<Map<String, Object>> selectRatingStatsByConversation(@Param("conversationId") Long conversationId);
}
