package com.example.admin.service;

import com.example.admin.entity.User;
import com.example.admin.mapper.UserMapper;
import com.example.admin.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户管理 Service
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DataStore store;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 查询用户列表，支持关键字模糊搜索
     */
    public List<User> list(String keyword) {
        List<User> users = userMapper.selectList(keyword);
        for (User u : users) {
            u.setRoleIds(userMapper.selectRoleIdsByUserId(u.getId()));
        }
        return users;
    }

    /**
     * 根据ID查询用户
     */
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setRoleIds(userMapper.selectRoleIdsByUserId(id));
        }
        return user;
    }

    /**
     * 根据用户名查询用户
     */
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 新增用户（密码加密，同步保存角色关联）
     */
    @Transactional
    public User add(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(java.time.LocalDateTime.now());
        userMapper.insert(user);
        if (user.getRoleIds() != null) {
            for (Long roleId : user.getRoleIds()) {
                userMapper.insertUserRole(user.getId(), roleId);
            }
        }
        return user;
    }

    /**
     * 更新用户信息，同步更新角色关联，禁用时清除Token
     */
    @Transactional
    public User update(User user) {
        User existing = userMapper.selectById(user.getId());
        if (existing == null) return null;

        existing.setNickname(user.getNickname());
        existing.setEmail(user.getEmail());
        existing.setPhone(user.getPhone());
        existing.setStatus(user.getStatus());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userMapper.update(existing);

        // Update role associations
        userMapper.deleteUserRoleByUserId(user.getId());
        if (user.getRoleIds() != null) {
            for (Long roleId : user.getRoleIds()) {
                userMapper.insertUserRole(user.getId(), roleId);
            }
        }
        existing.setRoleIds(user.getRoleIds());

        // Invalidate tokens if user is disabled
        if (user.getStatus() != null && user.getStatus() == 1) {
            tokenService.invalidateUserTokens(user.getId());
        }

        return existing;
    }

    /**
     * 删除用户及其角色关联
     */
    @Transactional
    public void delete(Long id) {
        userMapper.deleteUserRoleByUserId(id);
        userMapper.deleteById(id);
    }
}
