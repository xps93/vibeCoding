package com.example.admin.store;

import com.example.admin.entity.Menu;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 内存数据存储组件，用于缓存运行时数据（如token映射、菜单树等）
 */
@Component
public class DataStore {

    /** token -> userId 映射 */
    public final Map<String, Long> tokenMap = new ConcurrentHashMap<>();
    /** userId -> token列表 映射 */
    public final Map<Long, List<String>> userTokens = new ConcurrentHashMap<>();
    /** phone -> 验证码 映射 */
    public final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    /**
     * 构建菜单树
     * @param flatList 扁平的菜单列表
     * @return 构建完成的菜单树列表
     */
    public List<Menu> buildTree(List<Menu> flatList) {
        List<Menu> trees = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (Menu m : flatList) ids.add(m.getId());
        for (Menu m : flatList) {
            if (m.getParentId() == 0 || !ids.contains(m.getParentId())) {
                trees.add(findChildren(m, flatList));
            }
        }
        trees.sort(Comparator.comparingInt(Menu::getSort));
        return trees;
    }

    /**
     * 递归查找子菜单
     * @param parent 父菜单
     * @param flatList 扁平的菜单列表
     * @return 包含子菜单的父菜单
     */
    private Menu findChildren(Menu parent, List<Menu> flatList) {
        List<Menu> children = new ArrayList<>();
        for (Menu m : flatList) {
            if (m.getParentId().equals(parent.getId())) {
                children.add(findChildren(m, flatList));
            }
        }
        children.sort(Comparator.comparingInt(Menu::getSort));
        parent.setChildren(children.isEmpty() ? null : children);
        return parent;
    }
}
