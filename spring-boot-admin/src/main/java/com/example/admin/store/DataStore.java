package com.example.admin.store;

import com.example.admin.entity.Menu;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DataStore {

    public final Map<String, Long> tokenMap = new ConcurrentHashMap<>(); // token -> userId
    public final Map<Long, List<String>> userTokens = new ConcurrentHashMap<>(); // userId -> list of tokens

    // Build menu tree
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
