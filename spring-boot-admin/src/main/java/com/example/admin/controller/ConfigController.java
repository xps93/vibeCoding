package com.example.admin.controller;

import com.example.admin.entity.Config;
import com.example.admin.entity.Result;
import com.example.admin.service.ConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "参数管理", description = "系统参数配置维护")
@RestController
@RequestMapping("/api/configs")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:config:list')")
    public Result list(@RequestParam(required = false) String keyword) {
        List<Config> list = configService.list(keyword);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:list')")
    public Result get(@PathVariable Long id) {
        Config config = configService.getById(id);
        if (config == null) return Result.error("参数配置不存在");
        return Result.success(config);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:config:create')")
    public Result add(@RequestBody Config config) {
        Config created = configService.add(config);
        return Result.success("新增成功", created);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:config:edit')")
    public Result update(@RequestBody Config config) {
        Config updated = configService.update(config);
        if (updated == null) return Result.error("参数配置不存在");
        return Result.success("修改成功", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:delete')")
    public Result delete(@PathVariable Long id) {
        configService.delete(id);
        return Result.success("删除成功");
    }
}
