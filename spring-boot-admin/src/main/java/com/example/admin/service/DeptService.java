package com.example.admin.service;

import com.example.admin.entity.Dept;
import com.example.admin.mapper.DeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DeptService {

    @Autowired
    private DeptMapper deptMapper;

    public List<Dept> list(String keyword) {
        return deptMapper.selectList(keyword);
    }

    public List<Dept> tree() {
        List<Dept> all = deptMapper.selectList(null);
        return buildTree(all);
    }

    public Dept getById(Long id) {
        return deptMapper.selectById(id);
    }

    public Dept add(Dept dept) {
        dept.setCreateTime(LocalDateTime.now());
        deptMapper.insert(dept);
        return dept;
    }

    public Dept update(Dept dept) {
        Dept existing = deptMapper.selectById(dept.getId());
        if (existing == null) return null;

        existing.setParentId(dept.getParentId());
        existing.setDeptName(dept.getDeptName());
        existing.setOrderNum(dept.getOrderNum());
        existing.setLeader(dept.getLeader());
        existing.setPhone(dept.getPhone());
        existing.setEmail(dept.getEmail());
        existing.setStatus(dept.getStatus());

        deptMapper.update(existing);
        return existing;
    }

    public void delete(Long id) {
        deptMapper.deleteById(id);
    }

    private List<Dept> buildTree(List<Dept> flatList) {
        List<Dept> trees = new ArrayList<>();
        for (Dept d : flatList) {
            if (d.getParentId() == 0) {
                trees.add(findChildren(d, flatList));
            }
        }
        trees.sort(Comparator.comparingInt(Dept::getOrderNum));
        return trees;
    }

    private Dept findChildren(Dept parent, List<Dept> flatList) {
        List<Dept> children = new ArrayList<>();
        for (Dept d : flatList) {
            if (d.getParentId().equals(parent.getId())) {
                children.add(findChildren(d, flatList));
            }
        }
        children.sort(Comparator.comparingInt(Dept::getOrderNum));
        parent.setChildren(children.isEmpty() ? null : children);
        return parent;
    }
}
