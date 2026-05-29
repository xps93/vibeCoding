package com.example.admin.mapper;

import com.example.admin.entity.AiShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiShareMapper {

    int insert(AiShare share);

    AiShare selectByToken(@Param("shareToken") String shareToken);

    AiShare selectByConversationAndUser(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    int deactivate(@Param("id") Long id);
}
