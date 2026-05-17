package com.example.admin.mapper;

import com.example.admin.entity.DictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典类型Mapper接口
 */
@Mapper
public interface DictTypeMapper {

    /**
     * 根据字典ID查询字典类型
     */
    DictType selectById(Long id);

    /**
     * 查询字典类型列表（支持关键字模糊搜索）
     */
    List<DictType> selectList(@Param("keyword") String keyword);

    /**
     * 根据字典类型字符串查询字典类型
     */
    DictType selectByDictType(@Param("dictType") String dictType);

    /**
     * 新增字典类型
     */
    int insert(DictType dictType);

    /**
     * 修改字典类型
     */
    int update(DictType dictType);

    /**
     * 根据字典ID删除字典类型
     */
    int deleteById(Long id);
}
