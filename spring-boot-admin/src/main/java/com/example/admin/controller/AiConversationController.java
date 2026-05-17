package com.example.admin.controller;

import com.example.admin.entity.AiConversation;
import com.example.admin.entity.Result;
import com.example.admin.entity.User;
import com.example.admin.service.AiConversationService;
import com.example.admin.service.AiMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "AI对话", description = "AI对话CRUD")
@RestController
@RequestMapping("/api/ai")
public class AiConversationController {

    @Autowired
    private AiConversationService conversationService;

    @Autowired
    private AiMessageService messageService;

    /** 获取用户所有对话 */
    @GetMapping("/conversations")
    public Result list(@AuthenticationPrincipal User user) {
        List<AiConversation> list = conversationService.listByUser(user.getId());
        return Result.success(list);
    }

    /** 获取单个对话 */
    @GetMapping("/conversations/{id}")
    public Result get(@AuthenticationPrincipal User user, @PathVariable Long id) {
        AiConversation conv = conversationService.getById(id);
        if (conv == null || !conv.getUserId().equals(user.getId())) {
            return Result.error("对话不存在");
        }
        return Result.success(conv);
    }

    /** 创建新对话 */
    @PostMapping("/conversations")
    public Result create(@AuthenticationPrincipal User user, @RequestBody Map<String, String> body) {
        String title = body.getOrDefault("title", "新对话");
        AiConversation conv = conversationService.create(user.getId(), title);
        return Result.success(conv);
    }

    /** 更新对话 */
    @PutMapping("/conversations")
    public Result update(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> body) {
        Long id = body.get("id") != null ? Long.valueOf(body.get("id").toString()) : null;
        if (id == null) return Result.error("缺少对话ID");
        AiConversation existing = conversationService.getById(id);
        if (existing == null || !existing.getUserId().equals(user.getId())) {
            return Result.error("对话不存在");
        }
        AiConversation conv = new AiConversation();
        conv.setId(id);
        if (body.get("title") != null) conv.setTitle((String) body.get("title"));
        if (body.get("modelId") != null) conv.setModelId((String) body.get("modelId"));
        AiConversation updated = conversationService.update(conv);
        return Result.success(updated);
    }

    /** 删除对话 */
    @DeleteMapping("/conversations/{id}")
    public Result delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        AiConversation conv = conversationService.getById(id);
        if (conv == null || !conv.getUserId().equals(user.getId())) {
            return Result.error("对话不存在");
        }
        messageService.deleteByConversationId(id);
        conversationService.delete(id);
        return Result.success();
    }

    /** 批量删除对话 */
    @DeleteMapping("/conversations/batch")
    public Result batchDelete(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> body) {
        Object idsObj = body.get("ids");
        if (!(idsObj instanceof List)) {
            return Result.error("参数格式错误：ids应为数组");
        }
        List<?> idsList = (List<?>) idsObj;
        int count = 0;
        for (Object idObj : idsList) {
            Long id = Long.valueOf(idObj.toString());
            AiConversation conv = conversationService.getById(id);
            if (conv == null || !conv.getUserId().equals(user.getId())) {
                continue;
            }
            messageService.deleteByConversationId(id);
            conversationService.delete(id);
            count++;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("deletedCount", count);
        return Result.success(data);
    }
}
