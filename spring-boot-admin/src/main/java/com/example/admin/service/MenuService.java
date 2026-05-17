package com.example.admin.service;

import com.example.admin.entity.Menu;
import com.example.admin.mapper.MenuMapper;
import com.example.admin.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单管理 Service
 */
@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private DataStore store;

    /**
     * 查询所有菜单列表
     */
    public List<Menu> list() {
        return menuMapper.selectAll();
    }

    /**
     * 查询菜单树（仅启用状态的菜单）
     */
    public List<Menu> tree() {
        List<Menu> all = menuMapper.selectList();
        return store.buildTree(all);
    }

    /**
     * 查询所有菜单树（包含禁用状态的菜单）
     */
    public List<Menu> treeAll() {
        List<Menu> all = menuMapper.selectAll();
        return store.buildTree(all);
    }

    /**
     * 根据ID查询菜单
     */
    public Menu getById(Long id) {
        return menuMapper.selectById(id);
    }

    /**
     * 新增菜单
     */
    public Menu add(Menu menu) {
        menu.setCreateTime(java.time.LocalDateTime.now());
        menuMapper.insert(menu);
        return menu;
    }

    /**
     * 更新菜单信息
     */
    public Menu update(Menu menu) {
        Menu existing = menuMapper.selectById(menu.getId());
        if (existing == null) return null;

        existing.setName(menu.getName());
        existing.setParentId(menu.getParentId());
        existing.setPath(menu.getPath());
        existing.setComponent(menu.getComponent());
        existing.setIcon(menu.getIcon());
        existing.setPerms(menu.getPerms());
        existing.setMenuType(menu.getMenuType());
        existing.setSort(menu.getSort());
        existing.setVisible(menu.getVisible());
        existing.setStatus(menu.getStatus());

        menuMapper.update(existing);
        return existing;
    }

    /**
     * 删除菜单及其子菜单
     */
    @Transactional
    public void delete(Long id) {
        menuMapper.deleteChildren(id);
        menuMapper.deleteById(id);
    }
}
