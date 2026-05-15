package com.example.admin.mapper;

import com.example.admin.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    Post selectById(Long id);

    List<Post> selectList(@Param("keyword") String keyword);

    int insert(Post post);

    int update(Post post);

    int deleteById(Long id);
}
