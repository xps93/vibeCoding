package com.example.admin.mapper;

import com.example.admin.entity.AiFavoriteGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiFavoriteGroupMapper {

    int insert(AiFavoriteGroup group);

    int update(AiFavoriteGroup group);

    int deleteById(@Param("id") Long id);

    AiFavoriteGroup selectById(@Param("id") Long id);

    List<AiFavoriteGroup> selectByUserId(@Param("userId") Long userId);
}
