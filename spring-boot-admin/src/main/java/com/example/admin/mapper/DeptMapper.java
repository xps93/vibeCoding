package com.example.admin.mapper;

import com.example.admin.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface DeptMapper {

    /**
     * 根据部门ID查询部门
     */
    Dept selectById(Long id);

    /**
     * 查询部门列表（支持关键字模糊搜索）
     */
    List<Dept> selectList(@Param("keyword") String keyword);

    /**
     * 新增部门
     */
    int insert(Dept dept);

    /**
     * 修改部门信息
     */
    int update(Dept dept);

    /**
     * 根据部门ID删除部门
     */
    int deleteById(Long id);
}
