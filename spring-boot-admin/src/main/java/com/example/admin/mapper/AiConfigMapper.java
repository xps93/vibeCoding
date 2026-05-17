package com.example.admin.mapper;

import com.example.admin.entity.AiConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiConfigMapper {

    AiConfig selectByUserId(@Param("userId") Long userId);

    int insert(AiConfig config);

    int update(AiConfig config);
}
