package com.example.admin.service;

import com.example.admin.entity.AiModel;
import com.example.admin.mapper.AiModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AiModelService {

    @Autowired
    private AiModelMapper modelMapper;

    /** 获取所有启用的模型列表 */
    public List<AiModel> listAll() {
        return modelMapper.selectAll();
    }

    /** 获取所有模型（含禁用） */
    public List<AiModel> listAllWithDisabled() {
        return modelMapper.selectAllWithDisabled();
    }

    /** 根据ID获取模型 */
    public AiModel getById(Long id) {
        return modelMapper.selectById(id);
    }

    /** 新增模型 */
    @Transactional
    public AiModel create(AiModel model) {
        modelMapper.insert(model);
        return model;
    }

    /** 更新模型（含密钥） */
    @Transactional
    public void update(AiModel model) {
        modelMapper.update(model);
    }

    /** 删除模型 */
    @Transactional
    public void delete(Long id) {
        modelMapper.deleteById(id);
    }

    /** 切换模型启用/禁用状态 */
    @Transactional
    public void toggleStatus(Long id, Integer status) {
        AiModel model = new AiModel();
        model.setId(id);
        model.setStatus(status);
        modelMapper.update(model);
    }
}
