package com.example.admin.mapper;

import com.example.admin.entity.AiMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiMessageMapper {

    List<AiMessage> selectByConversationId(@Param("conversationId") Long conversationId);

    int insert(AiMessage message);

    int deleteById(@Param("id") Long id);

    int deleteByConversationId(@Param("conversationId") Long conversationId);
}
