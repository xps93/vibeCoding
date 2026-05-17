package com.example.admin.mapper;

import com.example.admin.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单Mapper接口
 */
@Mapper
public interface MenuMapper {

    /**
     * 根据菜单ID查询菜单
     */
    Menu selectById(Long id);

    /**
     * 查询菜单列表
     */
    List<Menu> selectList();

    /**
     * 查询所有菜单（不分页、无过滤）
     */
    List<Menu> selectAll();

    /**
     * 新增菜单
     */
    int insert(Menu menu);

    /**
     * 修改菜单信息
     */
    int update(Menu menu);

    /**
     * 根据菜单ID删除菜单
     */
    int deleteById(Long id);

    /**
     * 根据父菜单ID删除所有子菜单
     */
    int deleteChildren(Long parentId);
}
