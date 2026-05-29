package com.example.admin.mapper;

import com.example.admin.entity.AiDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiDocumentMapper {

    int insert(AiDocument doc);

    AiDocument selectById(@Param("id") Long id);

    List<AiDocument> selectByConversationId(@Param("conversationId") Long conversationId);

    List<AiDocument> selectByUserId(@Param("userId") Long userId);

    int deleteById(@Param("id") Long id);

    int deleteByConversationId(@Param("conversationId") Long conversationId);
}
