package com.example.admin.mapper;

import com.example.admin.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位Mapper接口
 */
@Mapper
public interface PostMapper {

    /**
     * 根据岗位ID查询岗位
     */
    Post selectById(Long id);

    /**
     * 查询岗位列表（支持关键字模糊搜索）
     */
    List<Post> selectList(@Param("keyword") String keyword);

    /**
     * 新增岗位
     */
    int insert(Post post);

    /**
     * 修改岗位信息
     */
    int update(Post post);

    /**
     * 根据岗位ID删除岗位
     */
    int deleteById(Long id);
}
