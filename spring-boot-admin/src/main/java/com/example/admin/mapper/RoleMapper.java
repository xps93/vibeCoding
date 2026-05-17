package com.example.admin.mapper;

import com.example.admin.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据角色ID查询角色
     */
    Role selectById(Long id);

    /**
     * 查询角色列表（支持关键字模糊搜索）
     */
    List<Role> selectList(@Param("keyword") String keyword);

    /**
     * 新增角色
     */
    int insert(Role role);

    /**
     * 修改角色信息
     */
    int update(Role role);

    /**
     * 根据角色ID删除角色
     */
    int deleteById(Long id);

    /**
     * 根据角色ID查询菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(Long roleId);

    /**
     * 新增角色菜单关联
     */
    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    /**
     * 根据角色ID删除角色菜单关联
     */
    int deleteRoleMenuByRoleId(Long roleId);
}
