package com.example.admin.controller;

import com.example.admin.entity.AiKnowledgeBase;
import com.example.admin.entity.Result;
import com.example.admin.service.AiKnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - AI知识库管理
 */
@Tag(name = "AI知识库管理", description = "知识库增删改查")
@RestController
@RequestMapping("/api/admin/knowledge-bases")
public class AiKnowledgeBaseAdminController {

    @Autowired
    private AiKnowledgeBaseService knowledgeBaseService;

    /** 获取所有知识库列表 */
    @Operation(summary = "获取知识库列表")
    @GetMapping
    @PreAuthorize("hasAuthority('ai:model:list')")
    public Result list() {
        List<AiKnowledgeBase> list = knowledgeBaseService.listAll();
        return Result.success(list);
    }

    /** 获取单个知识库 */
    @Operation(summary = "获取知识库详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ai:model:list')")
    public Result get(@PathVariable Long id) {
        AiKnowledgeBase entity = knowledgeBaseService.getById(id);
        if (entity == null) {
            return Result.error("知识库不存在");
        }
        return Result.success(entity);
    }

    /** 新增知识库 */
    @Operation(summary = "新增知识库")
    @PostMapping
    @PreAuthorize("hasAuthority('ai:model:create')")
    public Result create(@RequestBody AiKnowledgeBase entity) {
        if (entity.getName() == null || entity.getName().isEmpty()) {
            return Result.error("知识库名称不能为空");
        }
        knowledgeBaseService.create(entity);
        return Result.success(entity);
    }

    /** 更新知识库 */
    @Operation(summary = "更新知识库")
    @PutMapping
    @PreAuthorize("hasAuthority('ai:model:edit')")
    public Result update(@RequestBody AiKnowledgeBase entity) {
        AiKnowledgeBase existing = knowledgeBaseService.getById(entity.getId());
        if (existing == null) {
            return Result.error("知识库不存在");
        }
        knowledgeBaseService.update(entity);
        return Result.success();
    }

    /** 删除知识库 */
    @Operation(summary = "删除知识库")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ai:model:delete')")
    public Result delete(@PathVariable Long id) {
        knowledgeBaseService.delete(id);
        return Result.success();
    }
}
