package com.example.admin.controller;

import com.example.admin.entity.AiDocument;
import com.example.admin.entity.Result;
import com.example.admin.entity.User;
import com.example.admin.service.AiDocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "AI文档", description = "文档上传、解析与下载")
@RestController
@RequestMapping("/api/ai")
public class AiDocumentController {

    @Autowired
    private AiDocumentService documentService;

    /** 上传文档 — multipart/form-data，返回解析后的文本内容 */
    @PostMapping("/documents/upload")
    public Result upload(@AuthenticationPrincipal User user,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam(value = "conversationId", required = false) Long conversationId) {
        try {
            AiDocument doc = documentService.upload(file, user.getId(), conversationId);
            Map<String, Object> data = new HashMap<>();
            data.put("id", doc.getId());
            data.put("fileName", doc.getFileName());
            data.put("fileType", doc.getFileType());
            data.put("fileSize", doc.getFileSize());
            data.put("content", doc.getContent());
            data.put("createTime", doc.getCreateTime());
            return Result.success(data);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error("文档上传失败: " + e.getMessage());
        }
    }

    /** 获取对话关联的文档列表 */
    @GetMapping("/conversations/{conversationId}/documents")
    public Result listByConversation(@AuthenticationPrincipal User user,
                                     @PathVariable Long conversationId) {
        List<AiDocument> docs = documentService.listByConversationId(conversationId);
        return Result.success(docs);
    }

    /** 获取用户上传的文档列表 */
    @GetMapping("/documents")
    public Result listByUser(@AuthenticationPrincipal User user) {
        List<AiDocument> docs = documentService.listByUserId(user.getId());
        return Result.success(docs);
    }

    /** 获取文档详情（含解析文本） */
    @GetMapping("/documents/{id}")
    public Result getById(@PathVariable Long id) {
        AiDocument doc = documentService.getById(id);
        if (doc == null) {
            return Result.error("文档不存在");
        }
        return Result.success(doc);
    }

    /** 删除文档 */
    @DeleteMapping("/documents/{id}")
    public Result delete(@PathVariable Long id) {
        documentService.delete(id);
        return Result.success();
    }
}
