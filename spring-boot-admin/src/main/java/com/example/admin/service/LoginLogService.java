package com.example.admin.service;

import com.example.admin.entity.LoginLog;
import com.example.admin.mapper.LoginLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    public List<LoginLog> list(String userName, Integer status) {
        return loginLogMapper.selectList(userName, status);
    }

    public LoginLog add(LoginLog loginLog) {
        loginLog.setLoginTime(java.time.LocalDateTime.now());
        loginLogMapper.insert(loginLog);
        return loginLog;
    }

    public void delete(Long id) {
        loginLogMapper.deleteById(id);
    }

    public void clearAll() {
        loginLogMapper.deleteAll();
    }
}
