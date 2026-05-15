package com.example.admin.service;

import com.example.admin.entity.Role;
import com.example.admin.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    public List<Role> list(String keyword) {
        List<Role> roles = roleMapper.selectList(keyword);
        for (Role r : roles) {
            r.setMenuIds(roleMapper.selectMenuIdsByRoleId(r.getId()));
        }
        return roles;
    }

    public Role getById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role != null) {
            role.setMenuIds(roleMapper.selectMenuIdsByRoleId(id));
        }
        return role;
    }

    @Transactional
    public Role add(Role role) {
        role.setCreateTime(java.time.LocalDateTime.now());
        roleMapper.insert(role);
        if (role.getMenuIds() != null) {
            for (Long menuId : role.getMenuIds()) {
                roleMapper.insertRoleMenu(role.getId(), menuId);
            }
        }
        return role;
    }

    @Transactional
    public Role update(Role role) {
        Role existing = roleMapper.selectById(role.getId());
        if (existing == null) return null;

        existing.setRoleKey(role.getRoleKey());
        existing.setRoleName(role.getRoleName());
        existing.setStatus(role.getStatus());

        roleMapper.update(existing);

        // Update menu associations
        roleMapper.deleteRoleMenuByRoleId(role.getId());
        if (role.getMenuIds() != null) {
            for (Long menuId : role.getMenuIds()) {
                roleMapper.insertRoleMenu(role.getId(), menuId);
            }
        }
        existing.setMenuIds(role.getMenuIds());

        return existing;
    }

    @Transactional
    public void delete(Long id) {
        roleMapper.deleteRoleMenuByRoleId(id);
        roleMapper.deleteById(id);
    }
}
