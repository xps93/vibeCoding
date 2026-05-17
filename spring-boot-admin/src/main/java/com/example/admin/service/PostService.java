package com.example.admin.service;

import com.example.admin.entity.Post;
import com.example.admin.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 岗位管理 Service
 */
@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    /**
     * 查询岗位列表，支持关键字模糊搜索
     */
    public List<Post> list(String keyword) {
        return postMapper.selectList(keyword);
    }

    /**
     * 根据ID查询岗位
     */
    public Post getById(Long id) {
        return postMapper.selectById(id);
    }

    /**
     * 新增岗位
     */
    public Post add(Post post) {
        post.setCreateTime(LocalDateTime.now());
        postMapper.insert(post);
        return post;
    }

    /**
     * 更新岗位信息
     */
    public Post update(Post post) {
        Post existing = postMapper.selectById(post.getId());
        if (existing == null) return null;

        existing.setPostCode(post.getPostCode());
        existing.setPostName(post.getPostName());
        existing.setPostSort(post.getPostSort());
        existing.setStatus(post.getStatus());

        postMapper.update(existing);
        return existing;
    }

    /**
     * 删除岗位
     */
    public void delete(Long id) {
        postMapper.deleteById(id);
    }
}
