package com.example.admin.controller;

import com.example.admin.entity.AiKnowledgeBase;
import com.example.admin.entity.Result;
import com.example.admin.service.AiKnowledgeBaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI知识库", description = "AI知识库列表")
@RestController
@RequestMapping("/api/ai")
public class AiKnowledgeBaseController {

    @Autowired
    private AiKnowledgeBaseService knowledgeBaseService;

    /** 获取知识库列表 */
    @GetMapping("/knowledge-bases")
    public Result list() {
        List<AiKnowledgeBase> list = knowledgeBaseService.listAll();
        return Result.success(list);
    }
}
