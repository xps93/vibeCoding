package com.example.admin.controller;

import com.example.admin.entity.Notice;
import com.example.admin.entity.Result;
import com.example.admin.service.NoticeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "通知公告管理", description = "通知公告维护")
@RestController
@RequestMapping("/api/notices")
/** 通知公告控制器 */
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /** 查询通知公告列表 */
    @GetMapping
    @PreAuthorize("hasAuthority('system:notice:list')")
    public Result list(@RequestParam(required = false) String keyword) {
        List<Notice> list = noticeService.list(keyword);
        return Result.success(list);
    }

    /** 根据ID获取通知公告详情 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:notice:list')")
    public Result get(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        if (notice == null) return Result.error("通知公告不存在");
        return Result.success(notice);
    }

    /** 新增通知公告 */
    @PostMapping
    @PreAuthorize("hasAuthority('system:notice:create')")
    public Result add(@RequestBody Notice notice) {
        Notice created = noticeService.add(notice);
        return Result.success("新增成功", created);
    }

    /** 修改通知公告 */
    @PutMapping
    @PreAuthorize("hasAuthority('system:notice:edit')")
    public Result update(@RequestBody Notice notice) {
        Notice updated = noticeService.update(notice);
        if (updated == null) return Result.error("通知公告不存在");
        return Result.success("修改成功", updated);
    }

    /** 删除通知公告 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:notice:delete')")
    public Result delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success("删除成功");
    }
}
