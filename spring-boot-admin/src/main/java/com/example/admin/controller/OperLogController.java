package com.example.admin.controller;

import com.example.admin.entity.OperLog;
import com.example.admin.entity.Result;
import com.example.admin.service.OperLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "操作日志", description = "操作日志查询与清理")
@RestController
@RequestMapping("/api/oper-logs")
public class OperLogController {

    @Autowired
    private OperLogService operLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('monitor:operlog:list')")
    public Result list(@RequestParam(required = false) String operName,
                       @RequestParam(required = false) Integer businessType,
                       @RequestParam(required = false) Integer status) {
        List<OperLog> list = operLogService.list(operName, businessType, status);
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('monitor:operlog:delete')")
    public Result delete(@PathVariable Long id) {
        operLogService.delete(id);
        return Result.success("删除成功");
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('monitor:operlog:delete')")
    public Result clear() {
        operLogService.clearAll();
        return Result.success("清除成功");
    }
}
