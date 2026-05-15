package com.example.admin.mapper;

import com.example.admin.entity.OperLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperLogMapper {

    OperLog selectById(Long id);

    List<OperLog> selectList(@Param("operName") String operName,
                             @Param("businessType") Integer businessType,
                             @Param("status") Integer status);

    int insert(OperLog operLog);

    int deleteById(Long id);

    int deleteAll();
}
