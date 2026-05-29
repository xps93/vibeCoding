package com.example.admin.controller;

import com.example.admin.entity.*;
import com.example.admin.service.AiConversationService;
import com.example.admin.service.AiMessageService;
import com.example.admin.service.AiShareService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "对话分享", description = "AI对话分享链接")
@RestController
@RequestMapping("/api/ai")
public class AiShareController {

    @Autowired
    private AiShareService shareService;

    @Autowired
    private AiConversationService conversationService;

    @Autowired
    private AiMessageService messageService;

    /** 创建或获取分享链接 */
    @PostMapping("/conversations/{conversationId}/share")
    public Result createShare(@AuthenticationPrincipal User user, @PathVariable Long conversationId) {
        AiConversation conv = conversationService.getById(conversationId);
        if (conv == null || !conv.getUserId().equals(user.getId())) {
            return Result.error("对话不存在或无权操作");
        }
        AiShare share = shareService.createOrGet(conversationId, user.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("shareToken", share.getShareToken());
        data.put("shareUrl", "/share/" + share.getShareToken());
        return Result.success(data);
    }

    /** 撤销分享 */
    @DeleteMapping("/conversations/{conversationId}/share")
    public Result revokeShare(@AuthenticationPrincipal User user, @PathVariable Long conversationId) {
        AiConversation conv = conversationService.getById(conversationId);
        if (conv == null || !conv.getUserId().equals(user.getId())) {
            return Result.error("对话不存在或无权操作");
        }
        shareService.revokeByConversation(conversationId, user.getId());
        return Result.success();
    }

    /** 通过token查看分享的对话内容（无需登录） */
    @GetMapping("/share/{shareToken}")
    public Result viewShare(@PathVariable String shareToken) {
        AiShare share = shareService.getByToken(shareToken);
        if (share == null) {
            return Result.error("分享链接不存在或已失效");
        }
        AiConversation conv = conversationService.getById(share.getConversationId());
        if (conv == null) {
            return Result.error("对话已被删除");
        }
        List<AiMessage> messages = messageService.listByConversationId(share.getConversationId());
        Map<String, Object> data = new HashMap<>();
        data.put("conversation", conv);
        data.put("messages", messages);
        return Result.success(data);
    }
}
