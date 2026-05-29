package com.example.admin.controller;

import com.example.admin.entity.AiModel;
import com.example.admin.entity.Result;
import com.example.admin.service.AiModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - AI模型管理
 */
@Tag(name = "AI模型管理", description = "后台模型配置与密钥管理")
@RestController
@RequestMapping("/api/admin/models")
public class AiModelAdminController {

    @Autowired
    private AiModelService modelService;

    /** 获取所有模型（含禁用） */
    @Operation(summary = "获取模型列表")
    @GetMapping
    @PreAuthorize("hasAuthority('ai:model:list')")
    public Result list() {
        List<AiModel> models = modelService.listAllWithDisabled();
        // 对前端脱敏API密钥
        models.forEach(m -> m.setApiKey(m.getMaskedApiKey()));
        return Result.success(models);
    }

    /** 获取单个模型 */
    @Operation(summary = "获取模型详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ai:model:list')")
    public Result get(@PathVariable Long id) {
        AiModel model = modelService.getById(id);
        if (model == null) {
            return Result.error("模型不存在");
        }
        model.setApiKey(model.getMaskedApiKey());
        return Result.success(model);
    }

    /** 新增模型 */
    @Operation(summary = "新增模型")
    @PostMapping
    @PreAuthorize("hasAuthority('ai:model:create')")
    public Result create(@RequestBody AiModel model) {
        if (model.getName() == null || model.getName().isEmpty()) {
            return Result.error("模型名称不能为空");
        }
        if (model.getModelKey() == null || model.getModelKey().isEmpty()) {
            return Result.error("模型标识不能为空");
        }
        modelService.create(model);
        return Result.success(model);
    }

    /** 更新模型 */
    @Operation(summary = "更新模型")
    @PutMapping
    @PreAuthorize("hasAuthority('ai:model:edit')")
    public Result update(@RequestBody AiModel model) {
        AiModel existing = modelService.getById(model.getId());
        if (existing == null) {
            return Result.error("模型不存在");
        }
        // 如果前端未传apiKey（脱敏值或空），保留原有密钥
        if (model.getApiKey() == null || model.getApiKey().isEmpty()
                || model.getApiKey().contains("****")) {
            model.setApiKey(existing.getApiKey());
        }
        modelService.update(model);
        return Result.success();
    }

    /** 删除模型 */
    @Operation(summary = "删除模型")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ai:model:delete')")
    public Result delete(@PathVariable Long id) {
        modelService.delete(id);
        return Result.success();
    }

    /** 切换模型启用/禁用状态 */
    @Operation(summary = "切换模型状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ai:model:edit')")
    public Result toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        modelService.toggleStatus(id, status);
        return Result.success();
    }
}
