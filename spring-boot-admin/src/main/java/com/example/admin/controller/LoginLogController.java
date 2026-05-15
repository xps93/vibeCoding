package com.example.admin.controller;

import com.example.admin.entity.LoginLog;
import com.example.admin.entity.Result;
import com.example.admin.service.LoginLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "登录日志", description = "登录日志查询与清理")
@RestController
@RequestMapping("/api/login-logs")
public class LoginLogController {

    @Autowired
    private LoginLogService loginLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('monitor:loginlog:list')")
    public Result list(@RequestParam(required = false) String userName,
                       @RequestParam(required = false) Integer status) {
        List<LoginLog> list = loginLogService.list(userName, status);
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('monitor:loginlog:delete')")
    public Result delete(@PathVariable Long id) {
        loginLogService.delete(id);
        return Result.success("删除成功");
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('monitor:loginlog:delete')")
    public Result clear() {
        loginLogService.clearAll();
        return Result.success("清除成功");
    }
}
