package com.example.admin.controller;

import com.example.admin.entity.Job;
import com.example.admin.entity.Result;
import com.example.admin.service.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "定时任务", description = "定时任务配置维护")
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    @PreAuthorize("hasAuthority('monitor:job:list')")
    public Result list(@RequestParam(required = false) String keyword) {
        List<Job> list = jobService.list(keyword);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('monitor:job:list')")
    public Result get(@PathVariable Long id) {
        Job job = jobService.getById(id);
        if (job == null) return Result.error("定时任务不存在");
        return Result.success(job);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('monitor:job:create')")
    public Result add(@RequestBody Job job) {
        Job created = jobService.add(job);
        return Result.success("新增成功", created);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('monitor:job:edit')")
    public Result update(@RequestBody Job job) {
        Job updated = jobService.update(job);
        if (updated == null) return Result.error("定时任务不存在");
        return Result.success("修改成功", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('monitor:job:delete')")
    public Result delete(@PathVariable Long id) {
        jobService.delete(id);
        return Result.success("删除成功");
    }
}
