package com.example.admin.mapper;

import com.example.admin.entity.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobMapper {

    Job selectById(Long id);

    List<Job> selectList(@Param("keyword") String keyword);

    int insert(Job job);

    int update(Job job);

    int deleteById(Long id);
}
