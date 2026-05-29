package com.example.admin.controller;

import com.example.admin.entity.AiFavoriteGroup;
import com.example.admin.entity.Result;
import com.example.admin.entity.User;
import com.example.admin.service.AiFavoriteGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "收藏分组", description = "收藏分组CRUD")
@RestController
@RequestMapping("/api/ai")
public class AiFavoriteGroupController {

    @Autowired
    private AiFavoriteGroupService groupService;

    /** 获取用户所有分组 */
    @GetMapping("/favorites/groups")
    public Result list(@AuthenticationPrincipal User user) {
        List<AiFavoriteGroup> list = groupService.listByUser(user.getId());
        return Result.success(list);
    }

    /** 创建分组 */
    @PostMapping("/favorites/groups")
    public Result create(@AuthenticationPrincipal User user, @RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.trim().isEmpty()) {
            return Result.error("分组名称不能为空");
        }
        AiFavoriteGroup group = groupService.create(user.getId(), name.trim());
        return Result.success(group);
    }

    /** 更新分组 */
    @PutMapping("/favorites/groups/{id}")
    public Result update(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.trim().isEmpty()) {
            return Result.error("分组名称不能为空");
        }
        AiFavoriteGroup group = groupService.update(id, name.trim());
        if (group == null) return Result.error("分组不存在");
        return Result.success(group);
    }

    /** 删除分组 */
    @DeleteMapping("/favorites/groups/{id}")
    public Result delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        boolean ok = groupService.delete(id);
        return ok ? Result.success() : Result.error("分组不存在");
    }
}
