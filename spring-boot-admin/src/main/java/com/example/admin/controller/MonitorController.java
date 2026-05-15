package com.example.admin.controller;

import com.example.admin.entity.Result;
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
import java.util.Properties;

@Tag(name = "服务监控", description = "服务器与JVM信息、缓存监控")
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private DataStore store;

    @GetMapping("/server")
    public Result server() {
        Map<String, Object> info = new HashMap<>();
        try {
            OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
            Runtime runtime = Runtime.getRuntime();

            Map<String, Object> sys = new HashMap<>();
            sys.put("osName", os.getName());
            sys.put("osArch", os.getArch());
            sys.put("osVersion", os.getVersion());
            sys.put("cpuCores", os.getAvailableProcessors());
            info.put("sys", sys);

            Map<String, Object> jvm = new HashMap<>();
            jvm.put("totalMemory", runtime.totalMemory() / 1024 / 1024 + "MB");
            jvm.put("freeMemory", runtime.freeMemory() / 1024 / 1024 + "MB");
            jvm.put("maxMemory", runtime.maxMemory() / 1024 / 1024 + "MB");
            jvm.put("javaVersion", System.getProperty("java.version"));
            jvm.put("javaHome", System.getProperty("java.home"));
            info.put("jvm", jvm);
        } catch (Exception e) {
            info.put("error", e.getMessage());
        }
        return Result.success(info);
    }

    @GetMapping("/cache")
    public Result cache() {
        Map<String, Object> info = new HashMap<>();
        info.put("tokenCount", store.tokenMap.size());
        info.put("userSessionCount", store.userTokens.size());
        info.put("cacheType", "ConcurrentHashMap (内存缓存)");
        return Result.success(info);
    }
}
