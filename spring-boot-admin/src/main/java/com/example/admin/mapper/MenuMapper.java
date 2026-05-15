package com.example.admin.mapper;

import com.example.admin.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {

    Menu selectById(Long id);

    List<Menu> selectList();

    List<Menu> selectAll();

    int insert(Menu menu);

    int update(Menu menu);

    int deleteById(Long id);

    int deleteChildren(Long parentId);
}
