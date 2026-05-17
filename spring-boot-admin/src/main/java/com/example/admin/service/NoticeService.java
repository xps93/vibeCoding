package com.example.admin.service;

import com.example.admin.entity.Notice;
import com.example.admin.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知公告管理 Service
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 查询通知公告列表，支持关键字模糊搜索
     */
    public List<Notice> list(String keyword) {
        return noticeMapper.selectList(keyword);
    }

    /**
     * 根据ID查询通知公告
     */
    public Notice getById(Long id) {
        return noticeMapper.selectById(id);
    }

    /**
     * 新增通知公告
     */
    public Notice add(Notice notice) {
        notice.setCreateTime(LocalDateTime.now());
        noticeMapper.insert(notice);
        return notice;
    }

    /**
     * 更新通知公告信息
     */
    public Notice update(Notice notice) {
        Notice existing = noticeMapper.selectById(notice.getId());
        if (existing == null) return null;

        existing.setNoticeTitle(notice.getNoticeTitle());
        existing.setNoticeType(notice.getNoticeType());
        existing.setNoticeContent(notice.getNoticeContent());
        existing.setStatus(notice.getStatus());

        noticeMapper.update(existing);
        return existing;
    }

    /**
     * 删除通知公告
     */
    public void delete(Long id) {
        noticeMapper.deleteById(id);
    }
}
