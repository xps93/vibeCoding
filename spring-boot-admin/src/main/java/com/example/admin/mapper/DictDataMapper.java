package com.example.admin.mapper;

import com.example.admin.entity.DictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictDataMapper {

    DictData selectById(Long id);

    List<DictData> selectListByTypeId(@Param("dictTypeId") Long dictTypeId);

    List<DictData> selectByDictType(@Param("dictType") String dictType);

    int insert(DictData dictData);

    int update(DictData dictData);

    int deleteById(Long id);

    int deleteByTypeId(@Param("dictTypeId") Long dictTypeId);
}
