package com.example.admin.service;

import com.example.admin.entity.AiKnowledgeBase;
import com.example.admin.mapper.AiKnowledgeBaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiKnowledgeBaseService {

    @Autowired
    private AiKnowledgeBaseMapper knowledgeBaseMapper;

    /** 获取所有启用的知识库列表 */
    public List<AiKnowledgeBase> listAll() {
        return knowledgeBaseMapper.selectAll();
    }
}
