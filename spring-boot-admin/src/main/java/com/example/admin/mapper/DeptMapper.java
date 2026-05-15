package com.example.admin.mapper;

import com.example.admin.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeptMapper {

    Dept selectById(Long id);

    List<Dept> selectList(@Param("keyword") String keyword);

    int insert(Dept dept);

    int update(Dept dept);

    int deleteById(Long id);
}
