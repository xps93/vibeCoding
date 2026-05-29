package com.example.admin.mapper;

import com.example.admin.entity.AiModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiModelMapper {

    List<AiModel> selectAll();

    List<AiModel> selectAllWithDisabled();

    AiModel selectById(Long id);

    int insert(AiModel model);

    int update(AiModel model);

    int deleteById(Long id);
}
