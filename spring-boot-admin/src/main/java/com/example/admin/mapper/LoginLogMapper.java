package com.example.admin.mapper;

import com.example.admin.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoginLogMapper {

    LoginLog selectById(Long id);

    List<LoginLog> selectList(@Param("userName") String userName,
                              @Param("status") Integer status);

    int insert(LoginLog loginLog);

    int deleteById(Long id);

    int deleteAll();
}
