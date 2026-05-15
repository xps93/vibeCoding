package com.example.admin.controller;

import com.example.admin.entity.Dept;
import com.example.admin.entity.Result;
import com.example.admin.service.DeptService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理", description = "组织架构维护")
@RestController
@RequestMapping("/api/depts")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result list(@RequestParam(required = false) String keyword) {
        List<Dept> list = deptService.list(keyword);
        return Result.success(list);
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result tree() {
        return Result.success(deptService.tree());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result get(@PathVariable Long id) {
        Dept dept = deptService.getById(id);
        if (dept == null) return Result.error("部门不存在");
        return Result.success(dept);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:create')")
    public Result add(@RequestBody Dept dept) {
        Dept created = deptService.add(dept);
        return Result.success("新增成功", created);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:dept:edit')")
    public Result update(@RequestBody Dept dept) {
        Dept updated = deptService.update(dept);
        if (updated == null) return Result.error("部门不存在");
        return Result.success("修改成功", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    public Result delete(@PathVariable Long id) {
        deptService.delete(id);
        return Result.success("删除成功");
    }
}
