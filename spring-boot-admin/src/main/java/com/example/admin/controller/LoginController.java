package com.example.admin.controller;

import com.example.admin.entity.Menu;
import com.example.admin.entity.Result;
import com.example.admin.entity.Role;
import com.example.admin.entity.User;
import com.example.admin.mapper.RoleMapper;
import com.example.admin.service.TokenService;
import com.example.admin.store.DataStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "认证管理", description = "用户登录、退出、获取用户信息与菜单路由")
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private DataStore store;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return Result.error("用户名或密码不能为空");
        }
        String token = tokenService.login(username, password);
        if (token == null) {
            return Result.error("用户名或密码错误");
        }
        if ("DISABLED".equals(token)) {
            return Result.error("账号已停用，请联系管理员");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenService.logout(authHeader.substring(7));
        }
        return Result.success();
    }

    @GetMapping("/user/info")
    public Result userInfo(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(401, "未登录");
        }
        String token = authHeader.substring(7);
        User user = tokenService.getUserFromToken(token);
        if (user == null) {
            return Result.error(401, "token无效");
        }
        Set<String> perms = tokenService.getPermissionsByRoleIds(user.getRoleIds());
        List<String> roles = new ArrayList<>();
        for (Long rid : user.getRoleIds()) {
            Role r = roleMapper.selectById(rid);
            if (r != null) roles.add(r.getRoleKey());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("roles", roles);
        data.put("permissions", perms);
        user.setPassword(null);
        return Result.success(data);
    }

    @GetMapping("/menus/routers")
    public Result getRouters(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(401, "未登录");
        }
        String token = authHeader.substring(7);
        User user = tokenService.getUserFromToken(token);
        if (user == null) return Result.error(401, "token无效");

        List<Menu> menus = tokenService.getMenusByRoleIds(user.getRoleIds());
        List<Menu> tree = store.buildTree(menus);
        return Result.success(tree);
    }
}
