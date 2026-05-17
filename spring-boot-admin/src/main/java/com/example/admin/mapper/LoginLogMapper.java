package com.example.admin.mapper;

import com.example.admin.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 登录日志Mapper接口
 */
@Mapper
public interface LoginLogMapper {

    /**
     * 根据日志ID查询登录日志
     */
    LoginLog selectById(Long id);

    /**
     * 查询登录日志列表（支持多条件筛选）
     */
    List<LoginLog> selectList(@Param("userName") String userName,
                              @Param("status") Integer status);

    /**
     * 新增登录日志
     */
    int insert(LoginLog loginLog);

    /**
     * 根据日志ID删除登录日志
     */
    int deleteById(Long id);

    /**
     * 清空所有登录日志
     */
    int deleteAll();
}
