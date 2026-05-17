package com.example.admin.controller;

import com.example.admin.entity.Menu;
import com.example.admin.entity.Result;
import com.example.admin.entity.Role;
import com.example.admin.service.MenuService;
import com.example.admin.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理", description = "系统角色增删改查与权限分配")
@RestController
@RequestMapping("/api/roles")
/** 角色控制器 */
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    /** 查询角色列表 */
    @GetMapping
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result list(@RequestParam(required = false) String keyword) {
        return Result.success(roleService.list(keyword));
    }

    /** 根据ID获取角色详情 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result get(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) return Result.error("角色不存在");
        return Result.success(role);
    }

    /** 新增角色 */
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:create')")
    public Result add(@RequestBody Role role) {
        Role created = roleService.add(role);
        return Result.success("新增成功", created);
    }

    /** 修改角色 */
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result update(@RequestBody Role role) {
        Role updated = roleService.update(role);
        if (updated == null) return Result.error("角色不存在");
        return Result.success("修改成功", updated);
    }

    /** 删除角色 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success("删除成功");
    }

    /** 获取角色的菜单ID列表 */
    @GetMapping("/menuIds/{id}")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result getMenuIds(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) return Result.error("角色不存在");
        return Result.success(role.getMenuIds());
    }

    /** 获取全部菜单树 */
    @GetMapping("/menus")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result getAllMenus() {
        List<Menu> tree = menuService.treeAll();
        return Result.success(tree);
    }
}
