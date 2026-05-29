package com.example.admin.controller;

import com.example.admin.entity.AiConversation;
import com.example.admin.entity.AiMessage;
import com.example.admin.entity.Result;
import com.example.admin.service.AiConversationService;
import com.example.admin.service.AiMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - AI聊天记录管理
 */
@Tag(name = "AI聊天记录管理", description = "管理员查看、删除对话记录")
@RestController
@RequestMapping("/api/admin/chat-records")
public class AiChatRecordAdminController {

    @Autowired
    private AiConversationService conversationService;

    @Autowired
    private AiMessageService messageService;

    /** 分页查询对话列表 */
    @Operation(summary = "分页查询对话列表")
    @GetMapping
    @PreAuthorize("hasAuthority('ai:model:list')")
    public Result list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String username) {
        List<AiConversation> list = conversationService.listAll(page, pageSize, keyword, username);
        int total = conversationService.countAll(keyword, username);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return Result.success(data);
    }

    /** 获取对话的消息历史 */
    @Operation(summary = "查看对话消息详情")
    @GetMapping("/{id}/messages")
    @PreAuthorize("hasAuthority('ai:model:list')")
    public Result messages(@PathVariable Long id) {
        AiConversation conv = conversationService.getById(id);
        if (conv == null) {
            return Result.error("对话不存在");
        }
        List<AiMessage> messages = messageService.listByConversationId(id);
        Map<String, Object> data = new HashMap<>();
        data.put("conversation", conv);
        data.put("messages", messages);
        return Result.success(data);
    }

    /** 删除对话 */
    @Operation(summary = "删除对话记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ai:model:delete')")
    public Result delete(@PathVariable Long id) {
        AiConversation conv = conversationService.getById(id);
        if (conv == null) {
            return Result.error("对话不存在");
        }
        messageService.deleteByConversationId(id);
        conversationService.delete(id);
        return Result.success();
    }
}
