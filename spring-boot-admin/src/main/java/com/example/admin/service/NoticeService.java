package com.example.admin.service;

import com.example.admin.entity.Notice;
import com.example.admin.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    public List<Notice> list(String keyword) {
        return noticeMapper.selectList(keyword);
    }

    public Notice getById(Long id) {
        return noticeMapper.selectById(id);
    }

    public Notice add(Notice notice) {
        notice.setCreateTime(LocalDateTime.now());
        noticeMapper.insert(notice);
        return notice;
    }

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

    public void delete(Long id) {
        noticeMapper.deleteById(id);
    }
}
