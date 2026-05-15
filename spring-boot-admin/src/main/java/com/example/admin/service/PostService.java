package com.example.admin.service;

import com.example.admin.entity.Post;
import com.example.admin.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    public List<Post> list(String keyword) {
        return postMapper.selectList(keyword);
    }

    public Post getById(Long id) {
        return postMapper.selectById(id);
    }

    public Post add(Post post) {
        post.setCreateTime(LocalDateTime.now());
        postMapper.insert(post);
        return post;
    }

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

    public void delete(Long id) {
        postMapper.deleteById(id);
    }
}
