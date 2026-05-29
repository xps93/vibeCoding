package com.example.admin.mapper;

import com.example.admin.entity.SysErrorCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysErrorCodeMapper {

    List<SysErrorCode> selectAll();

    SysErrorCode selectByCode(@Param("code") Integer code);

    int insert(SysErrorCode errorCode);

    int update(SysErrorCode errorCode);

    int deleteByCode(@Param("code") Integer code);
}
