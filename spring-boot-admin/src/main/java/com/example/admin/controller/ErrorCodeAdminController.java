package com.example.admin.controller;

import com.example.admin.entity.Result;
import com.example.admin.entity.SysErrorCode;
import com.example.admin.service.ErrorCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 系统错误码管理
 */
@Tag(name = "错误码管理", description = "系统错误码CRUD与缓存刷新")
@RestController
@RequestMapping("/api/admin/error-codes")
public class ErrorCodeAdminController {

    @Autowired
    private ErrorCodeService errorCodeService;

    /** 获取全部错误码 */
    @Operation(summary = "获取全部错误码列表")
    @GetMapping
    @PreAuthorize("hasAuthority('system:config:list')")
    public Result list() {
        List<SysErrorCode> list = errorCodeService.listAll();
        return Result.success(list);
    }

    /** 新增/更新错误码 */
    @Operation(summary = "新增或更新错误码")
    @PostMapping
    @PreAuthorize("hasAuthority('system:config:edit')")
    public Result save(@RequestBody SysErrorCode errorCode) {
        if (errorCode.getCode() == null) {
            return Result.error(400, "错误码不能为空");
        }
        if (errorCode.getZhMsg() == null || errorCode.getZhMsg().isEmpty()) {
            return Result.error(400, "中文消息不能为空");
        }
        errorCodeService.save(errorCode);
        return Result.success();
    }

    /** 删除错误码 */
    @Operation(summary = "删除错误码")
    @DeleteMapping("/{code}")
    @PreAuthorize("hasAuthority('system:config:edit')")
    public Result delete(@PathVariable Integer code) {
        errorCodeService.delete(code);
        return Result.success();
    }

    /** 刷新缓存 */
    @Operation(summary = "从DB重新加载错误码到Redis")
    @PostMapping("/refresh-cache")
    @PreAuthorize("hasAuthority('system:config:edit')")
    public Result refreshCache() {
        errorCodeService.refreshCache();
        return Result.success("错误码缓存已刷新");
    }
}
