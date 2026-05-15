package com.example.admin.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Menu {
    private Long id;
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
    private List<Menu> children;
    private LocalDateTime createTime;

    public Menu() {}

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getPerms() { return perms; }
    public void setPerms(String perms) { this.perms = perms; }
    public String getMenuType() { return menuType; }
    public void setMenuType(String menuType) { this.menuType = menuType; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getVisible() { return visible; }
    public void setVisible(Integer visible) { this.visible = visible; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<Menu> getChildren() { return children; }
    public void setChildren(List<Menu> children) { this.children = children; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
