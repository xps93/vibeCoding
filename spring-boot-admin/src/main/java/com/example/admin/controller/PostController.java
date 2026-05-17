package com.example.admin.controller;

import com.example.admin.entity.Post;
import com.example.admin.entity.Result;
import com.example.admin.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "岗位管理", description = "系统岗位维护")
@RestController
@RequestMapping("/api/posts")
/** 岗位控制器 */
public class PostController {

    @Autowired
    private PostService postService;

    /** 查询岗位列表 */
    @GetMapping
    @PreAuthorize("hasAuthority('system:post:list')")
    public Result list(@RequestParam(required = false) String keyword) {
        List<Post> list = postService.list(keyword);
        return Result.success(list);
    }

    /** 根据ID获取岗位详情 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:post:list')")
    public Result get(@PathVariable Long id) {
        Post post = postService.getById(id);
        if (post == null) return Result.error("岗位不存在");
        return Result.success(post);
    }

    /** 新增岗位 */
    @PostMapping
    @PreAuthorize("hasAuthority('system:post:create')")
    public Result add(@RequestBody Post post) {
        Post created = postService.add(post);
        return Result.success("新增成功", created);
    }

    /** 修改岗位 */
    @PutMapping
    @PreAuthorize("hasAuthority('system:post:edit')")
    public Result update(@RequestBody Post post) {
        Post updated = postService.update(post);
        if (updated == null) return Result.error("岗位不存在");
        return Result.success("修改成功", updated);
    }

    /** 删除岗位 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:post:delete')")
    public Result delete(@PathVariable Long id) {
        postService.delete(id);
        return Result.success("删除成功");
    }
}
