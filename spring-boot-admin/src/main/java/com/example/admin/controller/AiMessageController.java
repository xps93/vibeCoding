package com.example.admin.controller;

import com.example.admin.entity.AiConversation;
import com.example.admin.entity.AiMessage;
import com.example.admin.entity.Result;
import com.example.admin.entity.User;
import com.example.admin.service.AiConversationService;
import com.example.admin.service.AiMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI消息", description = "AI消息查询与删除")
@RestController
@RequestMapping("/api/ai")
public class AiMessageController {

    @Autowired
    private AiMessageService messageService;

    @Autowired
    private AiConversationService conversationService;

    /** 获取对话下所有消息 */
    @GetMapping("/conversations/{conversationId}/messages")
    public Result listMessages(@AuthenticationPrincipal User user, @PathVariable Long conversationId) {
        AiConversation conv = conversationService.getById(conversationId);
        if (conv == null || !conv.getUserId().equals(user.getId())) {
            return Result.error("对话不存在");
        }
        List<AiMessage> messages = messageService.listByConversationId(conversationId);
        return Result.success(messages);
    }

    /** 删除单条消息 */
    @DeleteMapping("/messages/{id}")
    public Result delete(@PathVariable Long id) {
        messageService.delete(id);
        return Result.success();
    }
}
