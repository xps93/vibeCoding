package com.example.admin.controller;

import com.example.admin.entity.Result;
import com.example.admin.entity.SysSiteConfig;
import com.example.admin.service.SysSiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 站点配置管理
 */
@Tag(name = "站点配置管理", description = "站点名称、Logo、版权等配置")
@RestController
@RequestMapping("/api/admin/site-configs")
public class SiteConfigAdminController {

    @Autowired
    private SysSiteConfigService configService;

    @Operation(summary = "获取所有站点配置")
    @GetMapping
    @PreAuthorize("hasAuthority('system:site-config:list')")
    public Result list() {
        List<SysSiteConfig> list = configService.listAll();
        return Result.success(list);
    }

    @Operation(summary = "更新站点配置")
    @PutMapping
    @PreAuthorize("hasAuthority('system:site-config:edit')")
    public Result update(@RequestBody SysSiteConfig config) {
        SysSiteConfig existing = configService.getById(config.getId());
        if (existing == null) return Result.error("配置项不存在");
        configService.update(config);
        return Result.success();
    }
}
