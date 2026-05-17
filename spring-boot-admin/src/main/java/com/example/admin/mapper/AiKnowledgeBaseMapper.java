package com.example.admin.mapper;

import com.example.admin.entity.AiKnowledgeBase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiKnowledgeBaseMapper {

    List<AiKnowledgeBase> selectAll();
}
