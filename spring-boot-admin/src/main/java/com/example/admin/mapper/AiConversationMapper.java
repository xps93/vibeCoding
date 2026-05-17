package com.example.admin.mapper;

import com.example.admin.entity.AiConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiConversationMapper {

    List<AiConversation> selectByUserId(@Param("userId") Long userId);

    AiConversation selectById(@Param("id") Long id);

    int insert(AiConversation conversation);

    int update(AiConversation conversation);

    int deleteById(@Param("id") Long id);
}
