package com.example.admin.service;

import com.example.admin.entity.DictType;
import com.example.admin.mapper.DictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DictTypeService {

    @Autowired
    private DictTypeMapper dictTypeMapper;

    public List<DictType> list(String keyword) {
        return dictTypeMapper.selectList(keyword);
    }

    public DictType getById(Long id) {
        return dictTypeMapper.selectById(id);
    }

    public DictType getByDictType(String dictType) {
        return dictTypeMapper.selectByDictType(dictType);
    }

    public DictType add(DictType dictType) {
        dictType.setCreateTime(LocalDateTime.now());
        dictTypeMapper.insert(dictType);
        return dictType;
    }

    public DictType update(DictType dictType) {
        DictType existing = dictTypeMapper.selectById(dictType.getId());
        if (existing == null) return null;

        existing.setDictName(dictType.getDictName());
        existing.setDictType(dictType.getDictType());
        existing.setStatus(dictType.getStatus());

        dictTypeMapper.update(existing);
        return existing;
    }

    public void delete(Long id) {
        dictTypeMapper.deleteById(id);
    }
}
