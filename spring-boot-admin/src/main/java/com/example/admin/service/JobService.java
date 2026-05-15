package com.example.admin.service;

import com.example.admin.entity.Job;
import com.example.admin.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobMapper jobMapper;

    public List<Job> list(String keyword) {
        return jobMapper.selectList(keyword);
    }

    public Job getById(Long id) {
        return jobMapper.selectById(id);
    }

    public Job add(Job job) {
        job.setCreateTime(LocalDateTime.now());
        jobMapper.insert(job);
        return job;
    }

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

    public void delete(Long id) {
        jobMapper.deleteById(id);
    }
}
