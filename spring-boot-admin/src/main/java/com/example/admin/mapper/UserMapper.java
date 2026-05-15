package com.example.admin.mapper;

import com.example.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    User selectById(Long id);

    User selectByUsername(String username);

    List<User> selectList(@Param("keyword") String keyword);

    int insert(User user);

    int update(User user);

    int deleteById(Long id);

    List<Long> selectRoleIdsByUserId(Long userId);

    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    int deleteUserRoleByUserId(Long userId);

    List<Map<String, Object>> selectAllPasswords();

    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
