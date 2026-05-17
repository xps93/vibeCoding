package com.example.admin.service;

import com.example.admin.entity.Job;
import com.example.admin.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务管理 Service
 */
@Service
public class JobService {

    @Autowired
    private JobMapper jobMapper;

    /**
     * 查询定时任务列表，支持关键字模糊搜索
     */
    public List<Job> list(String keyword) {
        return jobMapper.selectList(keyword);
    }

    /**
     * 根据ID查询定时任务
     */
    public Job getById(Long id) {
        return jobMapper.selectById(id);
    }

    /**
     * 新增定时任务
     */
    public Job add(Job job) {
        job.setCreateTime(LocalDateTime.now());
        jobMapper.insert(job);
        return job;
    }

    /**
     * 更新定时任务信息
     */
    public Job update(Job job) {
        Job existing = jobMapper.selectById(job.getId());
        if (existing == null) return null;

        existing.setJobName(job.getJobName());
        existing.setJobGroup(job.getJobGroup());
        existing.setInvokeTarget(job.getInvokeTarget());
        existing.setCronExpression(job.getCronExpression());
        existing.setStatus(job.getStatus());

        jobMapper.update(existing);
        return existing;
    }

    /**
     * 删除定时任务
     */
    public void delete(Long id) {
        jobMapper.deleteById(id);
    }
}
