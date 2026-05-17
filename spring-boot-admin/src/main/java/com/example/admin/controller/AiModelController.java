package com.example.admin.controller;

import com.example.admin.entity.AiModel;
import com.example.admin.entity.Result;
import com.example.admin.service.AiModelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI模型", description = "AI模型列表")
@RestController
@RequestMapping("/api/ai")
public class AiModelController {

    @Autowired
    private AiModelService modelService;

    /** 获取可用模型列表 */
    @GetMapping("/models")
    public Result list() {
        List<AiModel> models = modelService.listAll();
        return Result.success(models);
    }
}
