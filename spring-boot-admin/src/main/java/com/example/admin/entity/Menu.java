package com.example.admin.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单实体类
 */
public class Menu {
    // 菜单ID
    private Long id;
    // 父菜单ID
    private Long parentId;
    private String name;       // 菜单名称
    private String path;       // 路由地址
    private String component;  // 组件路径
    private String icon;       // 图标
    private String perms;      // 权限标识
    private String menuType;   // M=目录, C=菜单, F=按钮
    private Integer sort;      // 排序
    private Integer visible;   // 0=显示, 1=隐藏
    private Integer status;    // 0=正常, 1=停用
    // 子菜单列表
    private List<Menu> children;
    // 创建时间
    private LocalDateTime createTime;

    /**
     * 无参构造方法
     */
    public Menu() {}

    /**
     * 全参构造方法（不含子菜单和创建时间，创建时间自动设为当前时间）
     */
    public Menu(Long id, Long parentId, String name, String path, String component,
                String icon, String perms, String menuType, Integer sort, Integer visible, Integer status) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.path = path;
        this.component = component;
        this.icon = icon;
        this.perms = perms;
        this.menuType = menuType;
        this.sort = sort;
        this.visible = visible;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取菜单ID
     */
    public Long getId() { return id; }
    /**
     * 设置菜单ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取父菜单ID
     */
    public Long getParentId() { return parentId; }
    /**
     * 设置父菜单ID
     */
    public void setParentId(Long parentId) { this.parentId = parentId; }
    /**
     * 获取菜单名称
     */
    public String getName() { return name; }
    /**
     * 设置菜单名称
     */
    public void setName(String name) { this.name = name; }
    /**
     * 获取路由地址
     */
    public String getPath() { return path; }
    /**
     * 设置路由地址
     */
    public void setPath(String path) { this.path = path; }
    /**
     * 获取组件路径
     */
    public String getComponent() { return component; }
    /**
     * 设置组件路径
     */
    public void setComponent(String component) { this.component = component; }
    /**
     * 获取图标
     */
    public String getIcon() { return icon; }
    /**
     * 设置图标
     */
    public void setIcon(String icon) { this.icon = icon; }
    /**
     * 获取权限标识
     */
    public String getPerms() { return perms; }
    /**
     * 设置权限标识
     */
    public void setPerms(String perms) { this.perms = perms; }
    /**
     * 获取菜单类型（M=目录, C=菜单, F=按钮）
     */
    public String getMenuType() { return menuType; }
    /**
     * 设置菜单类型（M=目录, C=菜单, F=按钮）
     */
    public void setMenuType(String menuType) { this.menuType = menuType; }
    /**
     * 获取排序号
     */
    public Integer getSort() { return sort; }
    /**
     * 设置排序号
     */
    public void setSort(Integer sort) { this.sort = sort; }
    /**
     * 获取显示状态（0=显示, 1=隐藏）
     */
    public Integer getVisible() { return visible; }
    /**
     * 设置显示状态（0=显示, 1=隐藏）
     */
    public void setVisible(Integer visible) { this.visible = visible; }
    /**
     * 获取状态（0=正常, 1=停用）
     */
    public Integer getStatus() { return status; }
    /**
     * 设置状态（0=正常, 1=停用）
     */
    public void setStatus(Integer status) { this.status = status; }
    /**
     * 获取子菜单列表
     */
    public List<Menu> getChildren() { return children; }
    /**
     * 设置子菜单列表
     */
    public void setChildren(List<Menu> children) { this.children = children; }
    /**
     * 获取创建时间
     */
    public LocalDateTime getCreateTime() { return createTime; }
    /**
     * 设置创建时间
     */
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
