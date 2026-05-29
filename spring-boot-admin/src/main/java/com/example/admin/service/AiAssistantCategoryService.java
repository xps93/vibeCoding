package com.example.admin.service;

import com.example.admin.entity.AiAssistantCategory;
import com.example.admin.mapper.AiAssistantCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiAssistantCategoryService {

    @Autowired
    private AiAssistantCategoryMapper categoryMapper;

    public List<AiAssistantCategory> listAll() {
        return categoryMapper.selectAll();
    }

    public AiAssistantCategory getById(Long id) {
        return categoryMapper.selectById(id);
    }

    public AiAssistantCategory create(AiAssistantCategory entity) {
        categoryMapper.insert(entity);
        return entity;
    }

    public void update(AiAssistantCategory entity) {
        categoryMapper.update(entity);
    }

    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
