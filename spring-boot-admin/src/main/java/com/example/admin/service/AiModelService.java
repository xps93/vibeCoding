package com.example.admin.service;

import com.example.admin.entity.AiModel;
import com.example.admin.mapper.AiModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiModelService {

    @Autowired
    private AiModelMapper modelMapper;

    /** 获取所有启用的模型列表 */
    public List<AiModel> listAll() {
        return modelMapper.selectAll();
    }

    /** 根据ID获取模型 */
    public AiModel getById(Long id) {
        return modelMapper.selectById(id);
    }
}
