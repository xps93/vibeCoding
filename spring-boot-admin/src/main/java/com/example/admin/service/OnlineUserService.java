package com.example.admin.service;

import com.example.admin.entity.User;
import com.example.admin.mapper.UserMapper;
import com.example.admin.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OnlineUserService {

    @Autowired
    private DataStore store;

    @Autowired
    private UserMapper userMapper;

    public List<Map<String, Object>> list() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : store.tokenMap.entrySet()) {
            String token = entry.getKey();
            Long userId = entry.getValue();
            User user = userMapper.selectById(userId);
            if (user != null) {
                Map<String, Object> info = new java.util.HashMap<>();
                info.put("token", token);
                info.put("userId", userId);
                info.put("username", user.getUsername());
                info.put("nickname", user.getNickname());
                info.put("loginTime", user.getCreateTime());
                result.add(info);
            }
        }
        return result;
    }

    public void forceLogout(String token) {
        Long userId = store.tokenMap.remove(token);
        if (userId != null) {
            List<String> tokens = store.userTokens.get(userId);
            if (tokens != null) {
                tokens.remove(token);
            }
        }
    }
}
