package com.example.admin.controller;

import com.example.admin.entity.Result;
import com.example.admin.service.OnlineUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "在线用户", description = "在线用户查看与强制退出")
@RestController
@RequestMapping("/api/online-users")
/** 在线用户控制器 */
public class OnlineUserController {

    @Autowired
    private OnlineUserService onlineUserService;

    /** 查询在线用户列表 */
    @GetMapping
    @PreAuthorize("hasAuthority('monitor:online:list')")
    public Result list() {
        List<Map<String, Object>> list = onlineUserService.list();
        return Result.success(list);
    }

    /** 强制退出在线用户 */
    @DeleteMapping("/{token}")
    @PreAuthorize("hasAuthority('monitor:online:forceLogout')")
    public Result forceLogout(@PathVariable String token) {
        onlineUserService.forceLogout(token);
        return Result.success("强制退出成功");
    }
}
