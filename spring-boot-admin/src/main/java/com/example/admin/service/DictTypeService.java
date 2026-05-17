package com.example.admin.service;

import com.example.admin.entity.DictType;
import com.example.admin.mapper.DictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典类型管理 Service
 */
@Service
public class DictTypeService {

    @Autowired
    private DictTypeMapper dictTypeMapper;

    /**
     * 查询字典类型列表，支持关键字模糊搜索
     */
    public List<DictType> list(String keyword) {
        return dictTypeMapper.selectList(keyword);
    }

    /**
     * 根据ID查询字典类型
     */
    public DictType getById(Long id) {
        return dictTypeMapper.selectById(id);
    }

    /**
     * 根据字典类型标识查询
     */
    public DictType getByDictType(String dictType) {
        return dictTypeMapper.selectByDictType(dictType);
    }

    /**
     * 新增字典类型
     */
    public DictType add(DictType dictType) {
        dictType.setCreateTime(LocalDateTime.now());
        dictTypeMapper.insert(dictType);
        return dictType;
    }

    /**
     * 更新字典类型信息
     */
    public DictType update(DictType dictType) {
        DictType existing = dictTypeMapper.selectById(dictType.getId());
        if (existing == null) return null;

        existing.setDictName(dictType.getDictName());
        existing.setDictType(dictType.getDictType());
        existing.setStatus(dictType.getStatus());

        dictTypeMapper.update(existing);
        return existing;
    }

    /**
     * 删除字典类型
     */
    public void delete(Long id) {
        dictTypeMapper.deleteById(id);
    }
}
