package com.example.admin.mapper;

import com.example.admin.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知公告Mapper接口
 */
@Mapper
public interface NoticeMapper {

    /**
     * 根据公告ID查询通知公告
     */
    Notice selectById(Long id);

    /**
     * 查询通知公告列表（支持关键字模糊搜索）
     */
    List<Notice> selectList(@Param("keyword") String keyword);

    /**
     * 新增通知公告
     */
    int insert(Notice notice);

    /**
     * 修改通知公告
     */
    int update(Notice notice);

    /**
     * 根据公告ID删除通知公告
     */
    int deleteById(Long id);
}
