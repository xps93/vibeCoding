package com.example.admin.mapper;

import com.example.admin.entity.SysSiteConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysSiteConfigMapper {
    List<SysSiteConfig> selectAll();
    SysSiteConfig selectByKey(@Param("configKey") String configKey);
    SysSiteConfig selectById(@Param("id") Long id);
    int insert(SysSiteConfig config);
    int update(SysSiteConfig config);
}
