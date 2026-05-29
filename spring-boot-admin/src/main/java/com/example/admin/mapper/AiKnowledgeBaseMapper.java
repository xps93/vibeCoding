package com.example.admin.mapper;

import com.example.admin.entity.AiKnowledgeBase;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiKnowledgeBaseMapper {

    List<AiKnowledgeBase> selectAll();

    AiKnowledgeBase selectById(@Param("id") Long id);

    int insert(AiKnowledgeBase entity);

    int update(AiKnowledgeBase entity);

    int deleteById(@Param("id") Long id);
}
