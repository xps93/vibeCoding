package com.example.admin.service;

import com.example.admin.entity.OperLog;
import com.example.admin.mapper.OperLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperLogService {

    @Autowired
    private OperLogMapper operLogMapper;

    public List<OperLog> list(String operName, Integer businessType, Integer status) {
        return operLogMapper.selectList(operName, businessType, status);
    }

    public OperLog add(OperLog operLog) {
        operLog.setOperTime(java.time.LocalDateTime.now());
        operLogMapper.insert(operLog);
        return operLog;
    }

    public void delete(Long id) {
        operLogMapper.deleteById(id);
    }

    public void clearAll() {
        operLogMapper.deleteAll();
    }
}
