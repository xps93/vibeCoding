package com.example.admin.mapper;

import com.example.admin.entity.Config;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统配置Mapper接口
 */
@Mapper
public interface ConfigMapper {

    /**
     * 根据配置ID查询配置
     */
    Config selectById(Long id);

    /**
     * 查询配置列表（支持关键字模糊搜索）
     */
    List<Config> selectList(@Param("keyword") String keyword);

    /**
     * 根据配置键查询配置
     */
    Config selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 新增配置
     */
    int insert(Config config);

    /**
     * 修改配置信息
     */
    int update(Config config);

    /**
     * 根据配置ID删除配置
     */
    int deleteById(Long id);
}
