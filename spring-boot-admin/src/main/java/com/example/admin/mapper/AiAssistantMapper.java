package com.example.admin.mapper;

import com.example.admin.entity.AiAssistant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiAssistantMapper {
    List<AiAssistant> selectAll();
    List<AiAssistant> selectByCategoryId(@Param("categoryId") Long categoryId);
    AiAssistant selectById(@Param("id") Long id);
    int insert(AiAssistant entity);
    int update(AiAssistant entity);
    int deleteById(@Param("id") Long id);
}
