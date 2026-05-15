package com.example.admin.mapper;

import com.example.admin.entity.Config;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConfigMapper {

    Config selectById(Long id);

    List<Config> selectList(@Param("keyword") String keyword);

    Config selectByConfigKey(@Param("configKey") String configKey);

    int insert(Config config);

    int update(Config config);

    int deleteById(Long id);
}
