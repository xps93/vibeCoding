package com.example.admin.controller;

import com.example.admin.entity.Result;
import com.example.admin.service.SysSiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 公开接口 - 前端获取站点配置
 */
@Tag(name = "站点配置", description = "前端公开获取站点配置")
@RestController
@RequestMapping("/api/site")
public class SiteConfigPublicController {

    @Autowired
    private SysSiteConfigService configService;

    @Operation(summary = "获取站点配置（公开）")
    @GetMapping("/config")
    public Result config() {
        Map<String, String> map = configService.getAllAsMap();
        return Result.success(map);
    }
}
