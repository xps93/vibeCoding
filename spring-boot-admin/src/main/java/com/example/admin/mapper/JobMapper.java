package com.example.admin.mapper;

import com.example.admin.entity.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务Mapper接口
 */
@Mapper
public interface JobMapper {

    /**
     * 根据任务ID查询定时任务
     */
    Job selectById(Long id);

    /**
     * 查询定时任务列表（支持关键字模糊搜索）
     */
    List<Job> selectList(@Param("keyword") String keyword);

    /**
     * 新增定时任务
     */
    int insert(Job job);

    /**
     * 修改定时任务信息
     */
    int update(Job job);

    /**
     * 根据任务ID删除定时任务
     */
    int deleteById(Long id);
}
