package com.example.admin.controller;

import com.example.admin.entity.AiAssistant;
import com.example.admin.entity.AiAssistantCategory;
import com.example.admin.entity.Result;
import com.example.admin.service.AiAssistantCategoryService;
import com.example.admin.service.AiAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 公开接口 - 用户端获取助手分类与模板列表
 */
@Tag(name = "AI助手", description = "用户端助手选择器数据")
@RestController
@RequestMapping("/api/ai")
public class AiAssistantPublicController {

    @Autowired
    private AiAssistantCategoryService categoryService;

    @Autowired
    private AiAssistantService assistantService;

    @Operation(summary = "获取助手分类及模板列表（公开）")
    @GetMapping("/assistants")
    public Result list() {
        List<AiAssistantCategory> categories = categoryService.listAll();
        List<AiAssistant> allAssistants = assistantService.listAll();
        Map<Long, List<AiAssistant>> grouped = new LinkedHashMap<>();
        for (AiAssistantCategory cat : categories) {
            grouped.put(cat.getId(), new ArrayList<>());
        }
        for (AiAssistant a : allAssistants) {
            List<AiAssistant> list = grouped.get(a.getCategoryId());
            if (list != null) list.add(a);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);
        data.put("assistants", grouped);
        return Result.success(data);
    }
}
