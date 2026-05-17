package com.example.admin.mapper;

import com.example.admin.entity.DictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典数据Mapper接口
 */
@Mapper
public interface DictDataMapper {

    /**
     * 根据字典数据ID查询字典数据
     */
    DictData selectById(Long id);

    /**
     * 根据字典类型ID查询字典数据列表
     */
    List<DictData> selectListByTypeId(@Param("dictTypeId") Long dictTypeId);

    /**
     * 根据字典类型字符串查询字典数据列表
     */
    List<DictData> selectByDictType(@Param("dictType") String dictType);

    /**
     * 新增字典数据
     */
    int insert(DictData dictData);

    /**
     * 修改字典数据
     */
    int update(DictData dictData);

    /**
     * 根据字典数据ID删除字典数据
     */
    int deleteById(Long id);

    /**
     * 根据字典类型ID删除所有字典数据
     */
    int deleteByTypeId(@Param("dictTypeId") Long dictTypeId);
}
