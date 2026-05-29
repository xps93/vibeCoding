package com.example.admin.service;

import com.example.admin.entity.AiFavoriteGroup;
import com.example.admin.mapper.AiFavoriteGroupMapper;
import com.example.admin.mapper.AiFavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 收藏分组服务
 */
@Service
public class AiFavoriteGroupService {

    @Autowired
    private AiFavoriteGroupMapper groupMapper;

    @Autowired
    private AiFavoriteMapper favoriteMapper;

    /** 创建分组 */
    public AiFavoriteGroup create(Long userId, String name) {
        AiFavoriteGroup group = new AiFavoriteGroup();
        group.setUserId(userId);
        group.setName(name);
        group.setSort(0);
        group.setCreateTime(LocalDateTime.now());
        groupMapper.insert(group);
        return group;
    }

    /** 更新分组名称 */
    public AiFavoriteGroup update(Long id, String name) {
        AiFavoriteGroup group = groupMapper.selectById(id);
        if (group == null) return null;
        group.setName(name);
        groupMapper.update(group);
        return group;
    }

    /** 删除分组（组内收藏的group_id置为null） */
    @Transactional
    public boolean delete(Long id) {
        AiFavoriteGroup group = groupMapper.selectById(id);
        if (group == null) return false;
        // 将该分组下的收藏移到"未分组"（group_id = null）
        favoriteMapper.clearGroupId(id);
        groupMapper.deleteById(id);
        return true;
    }

    /** 查询用户所有分组 */
    public List<AiFavoriteGroup> listByUser(Long userId) {
        List<AiFavoriteGroup> list = groupMapper.selectByUserId(userId);
        return list != null ? list : Collections.emptyList();
    }
}
