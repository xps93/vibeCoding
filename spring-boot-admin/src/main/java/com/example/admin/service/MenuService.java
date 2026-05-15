package com.example.admin.service;

import com.example.admin.entity.Menu;
import com.example.admin.mapper.MenuMapper;
import com.example.admin.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private DataStore store;

    public List<Menu> list() {
        return menuMapper.selectAll();
    }

    public List<Menu> tree() {
        List<Menu> all = menuMapper.selectList();
        return store.buildTree(all);
    }

    public List<Menu> treeAll() {
        List<Menu> all = menuMapper.selectAll();
        return store.buildTree(all);
    }

    public Menu getById(Long id) {
        return menuMapper.selectById(id);
    }

    public Menu add(Menu menu) {
        menu.setCreateTime(java.time.LocalDateTime.now());
        menuMapper.insert(menu);
        return menu;
    }

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

    @Transactional
    public void delete(Long id) {
        menuMapper.deleteChildren(id);
        menuMapper.deleteById(id);
    }
}
