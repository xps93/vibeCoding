package com.example.admin.controller;

import com.example.admin.entity.AiFavorite;
import com.example.admin.entity.Result;
import com.example.admin.entity.User;
import com.example.admin.service.AiFavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "对话收藏", description = "收藏/取消收藏/收藏列表/分组移动")
@RestController
@RequestMapping("/api/ai")
public class AiFavoriteController {

    @Autowired
    private AiFavoriteService favoriteService;

    /** 添加收藏 */
    @PostMapping("/favorites/{conversationId}")
    public Result add(@AuthenticationPrincipal User user, @PathVariable Long conversationId) {
        AiFavorite fav = favoriteService.add(user.getId(), conversationId);
        if (fav == null) {
            return Result.error("已收藏过该对话");
        }
        return Result.success(fav);
    }

    /** 取消收藏 */
    @DeleteMapping("/favorites/{conversationId}")
    public Result remove(@AuthenticationPrincipal User user, @PathVariable Long conversationId) {
        boolean removed = favoriteService.remove(user.getId(), conversationId);
        return removed ? Result.success() : Result.error("未收藏该对话");
    }

    /** 获取收藏列表（支持 groupId 和 keyword 过滤） */
    @GetMapping("/favorites")
    public Result list(@AuthenticationPrincipal User user,
                       @RequestParam(required = false) Long groupId,
                       @RequestParam(required = false) String keyword) {
        List<AiFavorite> list = favoriteService.listByUser(user.getId(), groupId, keyword);
        return Result.success(list);
    }

    /** 移动收藏到指定分组 */
    @PutMapping("/favorites/{conversationId}/group")
    public Result moveToGroup(@AuthenticationPrincipal User user,
                              @PathVariable Long conversationId,
                              @RequestBody Map<String, Object> body) {
        Object gidObj = body.get("groupId");
        Long groupId = (gidObj != null && !gidObj.toString().isEmpty())
                ? Long.valueOf(gidObj.toString()) : null;
        boolean ok = favoriteService.moveToGroup(user.getId(), conversationId, groupId);
        return ok ? Result.success() : Result.error("未收藏该对话");
    }

    /** 批量检查对话是否已收藏（传入conversationIds数组） */
    @PostMapping("/favorites/check")
    public Result check(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> body) {
        Object idsObj = body.get("conversationIds");
        if (!(idsObj instanceof List)) {
            return Result.error("参数格式错误");
        }
        List<?> idsList = (List<?>) idsObj;
        Map<Long, Boolean> result = new HashMap<>();
        for (Object idObj : idsList) {
            Long convId = Long.valueOf(idObj.toString());
            result.put(convId, favoriteService.isFavorited(user.getId(), convId));
        }
        return Result.success(result);
    }
}
