package com.example.admin.mapper;

import com.example.admin.entity.DictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictTypeMapper {

    DictType selectById(Long id);

    List<DictType> selectList(@Param("keyword") String keyword);

    DictType selectByDictType(@Param("dictType") String dictType);

    int insert(DictType dictType);

    int update(DictType dictType);

    int deleteById(Long id);
}
