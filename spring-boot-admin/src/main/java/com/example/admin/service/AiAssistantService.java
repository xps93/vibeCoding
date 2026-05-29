package com.example.admin.service;

import com.example.admin.entity.AiAssistant;
import com.example.admin.mapper.AiAssistantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiAssistantService {

    @Autowired
    private AiAssistantMapper assistantMapper;

    public List<AiAssistant> listAll() {
        return assistantMapper.selectAll();
    }

    public List<AiAssistant> listByCategoryId(Long categoryId) {
        return assistantMapper.selectByCategoryId(categoryId);
    }

    public AiAssistant getById(Long id) {
        return assistantMapper.selectById(id);
    }

    public AiAssistant create(AiAssistant entity) {
        assistantMapper.insert(entity);
        return entity;
    }

    public void update(AiAssistant entity) {
        assistantMapper.update(entity);
    }

    public void delete(Long id) {
        assistantMapper.deleteById(id);
    }
}
