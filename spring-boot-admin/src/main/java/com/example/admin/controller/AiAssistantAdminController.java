package com.example.admin.controller;

import com.example.admin.entity.AiAssistant;
import com.example.admin.entity.Result;
import com.example.admin.service.AiAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI助手模板管理", description = "助手模板增删改查")
@RestController
@RequestMapping("/api/admin/assistants")
public class AiAssistantAdminController {

    @Autowired
    private AiAssistantService assistantService;

    @Operation(summary = "获取模板列表")
    @GetMapping
    @PreAuthorize("hasAuthority('ai:assistant:list')")
    public Result list() {
        List<AiAssistant> list = assistantService.listAll();
        return Result.success(list);
    }

    @Operation(summary = "新增模板")
    @PostMapping
    @PreAuthorize("hasAuthority('ai:assistant:create')")
    public Result create(@RequestBody AiAssistant entity) {
        if (entity.getName() == null || entity.getName().isEmpty()) return Result.error("模板名称不能为空");
        if (entity.getPrompt() == null || entity.getPrompt().isEmpty()) return Result.error("Prompt预设不能为空");
        assistantService.create(entity);
        return Result.success(entity);
    }

    @Operation(summary = "更新模板")
    @PutMapping
    @PreAuthorize("hasAuthority('ai:assistant:edit')")
    public Result update(@RequestBody AiAssistant entity) {
        if (assistantService.getById(entity.getId()) == null) return Result.error("模板不存在");
        assistantService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ai:assistant:delete')")
    public Result delete(@PathVariable Long id) {
        assistantService.delete(id);
        return Result.success();
    }
}
