package com.example.admin.controller;

import com.example.admin.entity.Result;
import com.example.admin.entity.Result;
import com.example.admin.mapper.*;
import com.example.admin.store.DataStore;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "仪表盘", description = "首页统计信息")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private AiConversationMapper conversationMapper;
    @Autowired
    private AiMessageMapper messageMapper;
    @Autowired
    private AiModelMapper modelMapper;
    @Autowired
    private DataStore store;

    @GetMapping
    public Result dashboard() {
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userMapper.selectList(null).size());
        stats.put("roleCount", roleMapper.selectList(null).size());
        stats.put("menuCount", menuMapper.selectAll().stream().filter(m -> !"F".equals(m.getMenuType())).count());
        stats.put("deptCount", deptMapper.selectList(null).size());
        stats.put("postCount", postMapper.selectList(null).size());
        stats.put("onlineCount", store.tokenMap.size());

        // AI统计
        stats.put("aiConversationCount", conversationMapper.countAll(null, null));
        stats.put("aiModelCount", modelMapper.selectAllWithDisabled().size());
        data.put("stats", stats);

        try {
            OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> sys = new HashMap<>();
            sys.put("osName", os.getName());
            sys.put("cpuCores", os.getAvailableProcessors());
            sys.put("usedMemory", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + " MB");
            sys.put("maxMemory", runtime.maxMemory() / 1024 / 1024 + " MB");
            sys.put("javaVersion", System.getProperty("java.version"));
            data.put("system", sys);
        } catch (Exception ignored) {}

        return Result.success(data);
    }
}
