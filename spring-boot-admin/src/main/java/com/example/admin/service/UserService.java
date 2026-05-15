package com.example.admin.service;

import com.example.admin.entity.User;
import com.example.admin.mapper.UserMapper;
import com.example.admin.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<User> list(String keyword) {
        List<User> users = userMapper.selectList(keyword);
        for (User u : users) {
            u.setRoleIds(userMapper.selectRoleIdsByUserId(u.getId()));
        }
        return users;
    }

    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setRoleIds(userMapper.selectRoleIdsByUserId(id));
        }
        return user;
    }

    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

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

    @Transactional
    public void delete(Long id) {
        userMapper.deleteUserRoleByUserId(id);
        userMapper.deleteById(id);
    }
}
