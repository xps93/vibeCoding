package com.example.admin.controller;

import com.example.admin.config.LoginRateLimitFilter;
import com.example.admin.entity.LoginLog;
import com.example.admin.entity.Menu;
import com.example.admin.entity.Result;
import com.example.admin.entity.Role;
import com.example.admin.entity.User;
import com.example.admin.mapper.LoginLogMapper;
import com.example.admin.mapper.RoleMapper;
import com.example.admin.mapper.UserMapper;
import com.example.admin.service.TokenService;
import com.example.admin.service.UserService;
import com.example.admin.store.DataStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "认证管理", description = "用户登录、退出、获取用户信息与菜单路由")
@RestController
@RequestMapping("/api")
/** 登录认证控制器 */
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DataStore store;

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private LoginRateLimitFilter loginRateLimitFilter;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    /** 用户注册 */
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String phone = body.get("phone");
        String code = body.get("code");

        if (username == null || username.trim().isEmpty()) return Result.error("用户名不能为空");
        if (password == null || password.length() < 6) return Result.error("密码至少6位");
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) return Result.error("手机号格式不正确");

        // 检查用户名唯一性
        User existing = userMapper.selectByUsername(username.trim());
        if (existing != null) return Result.error("用户名已存在");

        // 检查手机号唯一性
        User phoneUser = userMapper.selectByPhone(phone);
        if (phoneUser != null) return Result.error("该手机号已被注册");

        // 验证码校验（演示环境：1234 或手机后4位）
        String storedCode = store.verificationCodes.get(phone);
        if (storedCode == null || !storedCode.equals(code)) {
            return Result.error("验证码错误");
        }
        store.verificationCodes.remove(phone);

        // 创建用户
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password);
        user.setNickname(username.trim());
        user.setPhone(phone);
        user.setStatus(0);
        java.util.List<Long> roleIds = new java.util.ArrayList<>();
        roleIds.add(2L);
        user.setRoleIds(roleIds);
        User created = userService.add(user);

        created.setPassword(null);
        return Result.success("注册成功", created);
    }

    /** 发送短信验证码（演示环境：直接返回code） */
    @PostMapping("/send-code")
    public Result sendCode(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }
        // 演示环境：验证码固定为 1234
        String code = "1234";
        store.verificationCodes.put(phone, code);
        log.info("向手机号 {} 发送验证码: {}", phone, code);
        return Result.success("验证码已发送");
    }

    /** 通过手机号验证身份（忘记密码第一步） */
    @PostMapping("/verify-identity")
    public Result verifyIdentity(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String code = body.get("code");

        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }
        User user = userMapper.selectByPhone(phone);
        if (user == null) return Result.error("该手机号未注册");

        String storedCode = store.verificationCodes.get(phone);
        if (storedCode == null || !storedCode.equals(code)) {
            return Result.error("验证码错误");
        }
        store.verificationCodes.remove(phone);

        // 生成重置令牌
        String resetToken = UUID.randomUUID().toString();
        store.verificationCodes.put("reset:" + resetToken, phone);

        Map<String, Object> data = new HashMap<>();
        data.put("resetToken", resetToken);
        return Result.success(data);
    }

    /** 重置密码（忘记密码第二步） */
    @PostMapping("/reset-password")
    public Result resetPassword(@RequestBody Map<String, String> body) {
        String resetToken = body.get("resetToken");
        String newPassword = body.get("newPassword");

        if (resetToken == null || resetToken.isEmpty()) return Result.error("缺少重置令牌");
        if (newPassword == null || newPassword.length() < 6) return Result.error("新密码至少6位");

        String phone = store.verificationCodes.get("reset:" + resetToken);
        if (phone == null) return Result.error("重置令牌无效或已过期");

        User user = userMapper.selectByPhone(phone);
        if (user == null) return Result.error("用户不存在");

        userMapper.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
        store.verificationCodes.remove("reset:" + resetToken);

        return Result.success("密码重置成功");
    }

    /** 用户登录 */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String ip = request.getRemoteAddr();

        if (username == null || password == null) {
            return Result.error("用户名或密码不能为空");
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!userDetails.isEnabled()) {
                recordLoginLog(username, ip, 1, "账号已停用，请联系管理员");
                return Result.error("账号已停用，请联系管理员");
            }

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                loginRateLimitFilter.recordFailure(ip, username);
                recordLoginLog(username, ip, 1, "用户名或密码错误");
                return Result.error("用户名或密码错误");
            }

            User user = userMapper.selectByUsername(username);
            String token = tokenService.generateToken(user);

            loginRateLimitFilter.recordSuccess(ip, username);
            recordLoginLog(username, ip, 0, "登录成功");

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            return Result.success(data);

        } catch (Exception e) {
            log.error("Login failed for user={}", username, e);
            loginRateLimitFilter.recordFailure(ip, username);
            recordLoginLog(username, ip, 1, "用户名或密码错误");
            return Result.error("用户名或密码错误");
        }
    }

    /** 用户退出登录 */
    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenService.logout(authHeader.substring(7));
        }
        return Result.success();
    }

    /** 获取当前用户信息 */
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

    /** 获取用户菜单路由 */
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

    /** 当前用户修改个人资料 */
    @PutMapping("/user/profile")
    public Result updateProfile(@RequestHeader("Authorization") String authHeader,
                                @RequestBody Map<String, String> body) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(401, "未登录");
        }
        String token = authHeader.substring(7);
        User currentUser = tokenService.getUserFromToken(token);
        if (currentUser == null) {
            return Result.error(401, "token无效");
        }
        User toUpdate = userMapper.selectById(currentUser.getId());
        if (toUpdate == null) {
            return Result.error("用户不存在");
        }
        if (body.containsKey("nickname")) toUpdate.setNickname(body.get("nickname"));
        if (body.containsKey("email")) toUpdate.setEmail(body.get("email"));
        if (body.containsKey("phone")) toUpdate.setPhone(body.get("phone"));
        if (body.containsKey("avatar")) toUpdate.setAvatar(body.get("avatar"));
        userMapper.update(toUpdate);
        toUpdate.setPassword(null);
        return Result.success(toUpdate);
    }

    /** 记录登录日志 */
    private void recordLoginLog(String username, String ip, int status, String msg) {
        LoginLog log = new LoginLog();
        log.setUserName(username);
        log.setIpAddr(ip);
        log.setStatus(status);
        log.setMsg(msg);
        log.setLoginTime(LocalDateTime.now());
        loginLogMapper.insert(log);
    }
}
