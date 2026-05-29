package com.example.admin.service;

import com.example.admin.entity.AiKnowledgeBase;
import com.example.admin.mapper.AiKnowledgeBaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiKnowledgeBaseService {

    @Autowired
    private AiKnowledgeBaseMapper knowledgeBaseMapper;

    /** 获取所有启用的知识库列表 */
    public List<AiKnowledgeBase> listAll() {
        return knowledgeBaseMapper.selectAll();
    }

    /** 根据ID获取知识库 */
    public AiKnowledgeBase getById(Long id) {
        return knowledgeBaseMapper.selectById(id);
    }

    /** 新增知识库 */
    public AiKnowledgeBase create(AiKnowledgeBase entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setStatus(entity.getStatus() != null ? entity.getStatus() : 0);
        knowledgeBaseMapper.insert(entity);
        return entity;
    }

    /** 更新知识库 */
    public void update(AiKnowledgeBase entity) {
        knowledgeBaseMapper.update(entity);
    }

    /** 删除知识库 */
    public void delete(Long id) {
        knowledgeBaseMapper.deleteById(id);
    }
}
