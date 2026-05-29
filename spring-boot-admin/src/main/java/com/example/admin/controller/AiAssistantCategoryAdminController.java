package com.example.admin.controller;

import com.example.admin.entity.AiAssistantCategory;
import com.example.admin.entity.Result;
import com.example.admin.service.AiAssistantCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI助手分类管理", description = "助手分类增删改查")
@RestController
@RequestMapping("/api/admin/assistant-categories")
public class AiAssistantCategoryAdminController {

    @Autowired
    private AiAssistantCategoryService categoryService;

    @Operation(summary = "获取分类列表")
    @GetMapping
    @PreAuthorize("hasAuthority('ai:assistant:category:list')")
    public Result list() {
        List<AiAssistantCategory> list = categoryService.listAll();
        return Result.success(list);
    }

    @Operation(summary = "新增分类")
    @PostMapping
    @PreAuthorize("hasAuthority('ai:assistant:category:create')")
    public Result create(@RequestBody AiAssistantCategory entity) {
        if (entity.getName() == null || entity.getName().isEmpty()) return Result.error("分类名称不能为空");
        categoryService.create(entity);
        return Result.success(entity);
    }

    @Operation(summary = "更新分类")
    @PutMapping
    @PreAuthorize("hasAuthority('ai:assistant:category:edit')")
    public Result update(@RequestBody AiAssistantCategory entity) {
        if (categoryService.getById(entity.getId()) == null) return Result.error("分类不存在");
        categoryService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ai:assistant:category:delete')")
    public Result delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
