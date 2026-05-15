package com.example.admin.controller;

import com.example.admin.entity.Result;
import com.example.admin.entity.User;
import com.example.admin.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理", description = "系统用户增删改查")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result list(@RequestParam(required = false) String keyword) {
        List<User> users = userService.list(keyword);
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result get(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) return Result.error("用户不存在");
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:create')")
    public Result add(@RequestBody User user) {
        User created = userService.add(user);
        created.setPassword(null);
        return Result.success("新增成功", created);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result update(@RequestBody User user) {
        User updated = userService.update(user);
        if (updated == null) return Result.error("用户不存在");
        updated.setPassword(null);
        return Result.success("修改成功", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功");
    }
}
