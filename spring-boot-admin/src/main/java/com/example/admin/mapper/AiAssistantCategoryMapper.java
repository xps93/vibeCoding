package com.example.admin.mapper;

import com.example.admin.entity.AiAssistantCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiAssistantCategoryMapper {
    List<AiAssistantCategory> selectAll();
    AiAssistantCategory selectById(@Param("id") Long id);
    int insert(AiAssistantCategory entity);
    int update(AiAssistantCategory entity);
    int deleteById(@Param("id") Long id);
}
