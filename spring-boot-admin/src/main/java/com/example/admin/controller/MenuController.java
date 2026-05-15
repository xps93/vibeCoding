package com.example.admin.controller;

import com.example.admin.entity.Menu;
import com.example.admin.entity.Result;
import com.example.admin.service.MenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理", description = "系统菜单/权限树维护")
@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result list() {
        List<Menu> tree = menuService.tree();
        return Result.success(tree);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result all() {
        List<Menu> tree = menuService.treeAll();
        return Result.success(tree);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result get(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        if (menu == null) return Result.error("菜单不存在");
        return Result.success(menu);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:create')")
    public Result add(@RequestBody Menu menu) {
        Menu created = menuService.add(menu);
        return Result.success("新增成功", created);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result update(@RequestBody Menu menu) {
        Menu updated = menuService.update(menu);
        if (updated == null) return Result.error("菜单不存在");
        return Result.success("修改成功", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.success("删除成功");
    }
}
