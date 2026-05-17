package com.example.admin.mapper;

import com.example.admin.entity.AiModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiModelMapper {

    List<AiModel> selectAll();

    AiModel selectById(Long id);
}
