package com.example.admin.mapper;

import com.example.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户ID查询用户
     */
    User selectById(Long id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);

    /**
     * 查询用户列表（支持关键字模糊搜索）
     */
    List<User> selectList(@Param("keyword") String keyword);

    /**
     * 新增用户
     */
    int insert(User user);

    /**
     * 修改用户信息
     */
    int update(User user);

    /**
     * 根据用户ID删除用户
     */
    int deleteById(Long id);

    /**
     * 根据用户ID查询角色ID列表
     */
    List<Long> selectRoleIdsByUserId(Long userId);

    /**
     * 新增用户角色关联
     */
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 根据用户ID删除用户角色关联
     */
    int deleteUserRoleByUserId(Long userId);

    /**
     * 查询所有用户的密码
     */
    List<Map<String, Object>> selectAllPasswords();

    /**
     * 修改用户密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 根据手机号查询用户
     */
    User selectByPhone(String phone);
}
