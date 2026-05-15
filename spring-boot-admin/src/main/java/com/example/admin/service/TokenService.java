package com.example.admin.service;

import com.example.admin.entity.Menu;
import com.example.admin.entity.User;
import com.example.admin.mapper.MenuMapper;
import com.example.admin.mapper.RoleMapper;
import com.example.admin.mapper.UserMapper;
import com.example.admin.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TokenService {

    @Autowired
    private DataStore store;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuMapper menuMapper;

    /**
     * Generate a token for an already-authenticated user.
     */
    public String generateToken(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        store.tokenMap.put(token, user.getId());
        store.userTokens.computeIfAbsent(user.getId(), k -> new CopyOnWriteArrayList<>()).add(token);
        return token;
    }

    public void logout(String token) {
        Long userId = store.tokenMap.remove(token);
        if (userId != null) {
            List<String> tokens = store.userTokens.get(userId);
            if (tokens != null) {
                tokens.remove(token);
            }
        }
    }

    public User getUserFromToken(String token) {
        if (token == null || token.isEmpty()) return null;
        Long userId = store.tokenMap.get(token);
        if (userId == null) return null;
        User user = userMapper.selectById(userId);
        if (user != null && user.getStatus() != null && user.getStatus() == 1) {
            invalidateUserTokens(userId);
            return null;
        }
        if (user != null) {
            user.setRoleIds(userMapper.selectRoleIdsByUserId(userId));
        }
        return user;
    }

    public Set<String> getPermissionsByRoleIds(List<Long> roleIds) {
        Set<String> perms = new HashSet<>();
        if (roleIds == null) return perms;
        for (Long rid : roleIds) {
            List<Long> menuIds = roleMapper.selectMenuIdsByRoleId(rid);
            if (menuIds != null) {
                for (Long mid : menuIds) {
                    Menu m = menuMapper.selectById(mid);
                    if (m != null && m.getPerms() != null && !m.getPerms().isEmpty()) {
                        perms.add(m.getPerms());
                    }
                }
            }
        }
        return perms;
    }

    public List<Menu> getMenusByRoleIds(List<Long> roleIds) {
        Set<Long> menuIdSet = new HashSet<>();
        if (roleIds != null) {
            for (Long rid : roleIds) {
                List<Long> mids = roleMapper.selectMenuIdsByRoleId(rid);
                if (mids != null) {
                    menuIdSet.addAll(mids);
                }
            }
        }
        List<Menu> allMenus = menuMapper.selectAll();
        List<Menu> result = new ArrayList<>();
        for (Menu m : allMenus) {
            if (menuIdSet.contains(m.getId()) && !"F".equals(m.getMenuType())) {
                result.add(m);
            }
        }
        return result;
    }

    public void invalidateUserTokens(Long userId) {
        List<String> tokens = store.userTokens.remove(userId);
        if (tokens != null) {
            for (String t : tokens) {
                store.tokenMap.remove(t);
            }
        }
    }
}
