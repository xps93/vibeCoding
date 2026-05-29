package com.example.admin.controller;

import com.example.admin.entity.AiConversation;
import com.example.admin.entity.AiMessage;
import com.example.admin.entity.AiMessageRating;
import com.example.admin.entity.Result;
import com.example.admin.entity.User;
import com.example.admin.service.AiConversationService;
import com.example.admin.service.AiMessageRatingService;
import com.example.admin.service.AiMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "AI消息", description = "AI消息查询、编辑、评分")
@RestController
@RequestMapping("/api/ai")
public class AiMessageController {

    @Autowired
    private AiMessageService messageService;

    @Autowired
    private AiConversationService conversationService;

    @Autowired
    private AiMessageRatingService ratingService;

    /** 获取对话下所有消息 */
    @GetMapping("/conversations/{conversationId}/messages")
    public Result listMessages(@AuthenticationPrincipal User user, @PathVariable Long conversationId) {
        AiConversation conv = conversationService.getById(conversationId);
        if (conv == null || !conv.getUserId().equals(user.getId())) {
            return Result.error("对话不存在");
        }
        List<AiMessage> messages = messageService.listByConversationId(conversationId);
        // 查询用户对各消息的评分
        List<AiMessageRating> ratings = ratingService.listByConversation(conversationId);
        Map<Long, Integer> ratingMap = ratings.stream()
                .filter(r -> r.getUserId().equals(user.getId()))
                .collect(Collectors.toMap(AiMessageRating::getMessageId, AiMessageRating::getRating));
        Map<String, Object> data = new HashMap<>();
        data.put("messages", messages);
        data.put("ratings", ratingMap);
        return Result.success(data);
    }

    /** 删除单条消息 */
    @DeleteMapping("/messages/{id}")
    public Result delete(@PathVariable Long id) {
        messageService.delete(id);
        return Result.success();
    }

    /** 编辑用户消息内容 */
    @PutMapping("/messages/{id}")
    public Result updateMessage(@AuthenticationPrincipal User user,
                                @PathVariable Long id,
                                @RequestBody Map<String, String> body) {
        AiMessage msg = messageService.getById(id);
        if (msg == null || !"user".equals(msg.getRole())) {
            return Result.error("消息不存在或不可编辑");
        }
        AiConversation conv = conversationService.getById(msg.getConversationId());
        if (conv == null || !conv.getUserId().equals(user.getId())) {
            return Result.error("无权操作");
        }
        String newContent = body.get("content");
        if (newContent == null || newContent.trim().isEmpty()) {
            return Result.error("内容不能为空");
        }
        messageService.updateContent(id, newContent.trim());
        return Result.success();
    }

    /** 提交消息评分（赞/踩） */
    @PostMapping("/messages/{id}/rate")
    public Result rateMessage(@AuthenticationPrincipal User user,
                              @PathVariable Long id,
                              @RequestBody Map<String, Object> body) {
        AiMessage msg = messageService.getById(id);
        if (msg == null || !"assistant".equals(msg.getRole())) {
            return Result.error("只能对AI回复进行评分");
        }
        Integer rating = body.get("rating") != null ? ((Number) body.get("rating")).intValue() : null;
        if (rating == null || (rating != 1 && rating != 0)) {
            return Result.error("评分值必须为1(赞)或0(踩)");
        }
        ratingService.rate(id, user.getId(), msg.getConversationId(), rating);
        return Result.success();
    }

    /** 取消消息评分 */
    @DeleteMapping("/messages/{id}/rate")
    public Result cancelRate(@AuthenticationPrincipal User user, @PathVariable Long id) {
        ratingService.cancelRate(id, user.getId());
        return Result.success();
    }

    /** 获取对话的评分统计 */
    @GetMapping("/conversations/{conversationId}/ratings")
    public Result getRatings(@AuthenticationPrincipal User user, @PathVariable Long conversationId) {
        AiConversation conv = conversationService.getById(conversationId);
        if (conv == null || !conv.getUserId().equals(user.getId())) {
            return Result.error("对话不存在");
        }
        List<Map<String, Object>> stats = ratingService.getRatingStats(conversationId);
        return Result.success(stats);
    }
}
