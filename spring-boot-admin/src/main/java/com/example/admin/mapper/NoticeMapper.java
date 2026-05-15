package com.example.admin.mapper;

import com.example.admin.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    Notice selectById(Long id);

    List<Notice> selectList(@Param("keyword") String keyword);

    int insert(Notice notice);

    int update(Notice notice);

    int deleteById(Long id);
}
