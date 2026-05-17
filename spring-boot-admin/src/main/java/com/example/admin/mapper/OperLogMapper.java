package com.example.admin.mapper;

import com.example.admin.entity.OperLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志Mapper接口
 */
@Mapper
public interface OperLogMapper {

    /**
     * 根据日志ID查询操作日志
     */
    OperLog selectById(Long id);

    /**
     * 查询操作日志列表（支持多条件筛选）
     */
    List<OperLog> selectList(@Param("operName") String operName,
                             @Param("businessType") Integer businessType,
                             @Param("status") Integer status);

    /**
     * 新增操作日志
     */
    int insert(OperLog operLog);

    /**
     * 根据日志ID删除操作日志
     */
    int deleteById(Long id);

    /**
     * 清空所有操作日志
     */
    int deleteAll();
}
