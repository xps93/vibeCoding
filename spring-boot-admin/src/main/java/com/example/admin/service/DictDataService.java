package com.example.admin.service;

import com.example.admin.entity.DictData;
import com.example.admin.entity.DictType;
import com.example.admin.mapper.DictDataMapper;
import com.example.admin.mapper.DictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典数据管理 Service
 */
@Service
public class DictDataService {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Autowired
    private DictTypeMapper dictTypeMapper;

    /**
     * 根据字典类型ID查询字典数据列表
     */
    public List<DictData> listByTypeId(Long dictTypeId) {
        return dictDataMapper.selectListByTypeId(dictTypeId);
    }

    /**
     * 根据字典类型标识查询字典数据列表
     */
    public List<DictData> listByDictType(String dictType) {
        DictType dt = dictTypeMapper.selectByDictType(dictType);
        if (dt == null) return null;
        return dictDataMapper.selectListByTypeId(dt.getId());
    }

    /**
     * 根据ID查询字典数据
     */
    public DictData getById(Long id) {
        return dictDataMapper.selectById(id);
    }

    /**
     * 新增字典数据
     */
    public DictData add(DictData dictData) {
        dictData.setCreateTime(LocalDateTime.now());
        dictDataMapper.insert(dictData);
        return dictData;
    }

    /**
     * 更新字典数据信息
     */
    public DictData update(DictData dictData) {
        DictData existing = dictDataMapper.selectById(dictData.getId());
        if (existing == null) return null;

        existing.setDictTypeId(dictData.getDictTypeId());
        existing.setDictLabel(dictData.getDictLabel());
        existing.setDictValue(dictData.getDictValue());
        existing.setDictSort(dictData.getDictSort());
        existing.setStatus(dictData.getStatus());

        dictDataMapper.update(existing);
        return existing;
    }

    /**
     * 删除字典数据
     */
    public void delete(Long id) {
        dictDataMapper.deleteById(id);
    }
}
