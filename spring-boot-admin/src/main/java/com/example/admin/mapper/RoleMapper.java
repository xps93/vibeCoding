package com.example.admin.mapper;

import com.example.admin.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {

    Role selectById(Long id);

    List<Role> selectList(@Param("keyword") String keyword);

    int insert(Role role);

    int update(Role role);

    int deleteById(Long id);

    List<Long> selectMenuIdsByRoleId(Long roleId);

    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    int deleteRoleMenuByRoleId(Long roleId);
}
