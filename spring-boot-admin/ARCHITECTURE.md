# 架构文档

基于 Spring Boot + Vue 2 前后端分离的企业级后台管理系统。

---

## 1. 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 1.8 | 运行时 |
| Spring Boot | 2.7.18 | 应用框架 |
| Spring Security | 5.7.x | 认证授权 |
| MyBatis | 3.x | ORM 框架 |
| MySQL | 8.0 | 数据库 |
| HikariCP | - | 连接池 |
| SpringDoc OpenAPI | 1.7.0 | API 文档 / Swagger UI |
| Spring Boot Actuator | - | 服务监控 |
| Spring AOP | - | 操作日志切面 |
| Maven | - | 构建工具 |

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue.js | 2.7.16 | 前端框架 |
| Vue Router | 3.6.5 | 路由管理 |
| Vuex | 3.6.2 | 状态管理 |
| Element UI | 2.15.14 | UI 组件库 |
| Axios | 1.7.2 | HTTP 客户端 |
| NProgress | 0.2.0 | 进度条 |
| Vite | 5.4.19 | 构建工具 / 开发服务器 |

---

## 2. 项目结构

### 2.1 后端结构

```
spring-boot-admin/
├── pom.xml                                  # Maven 依赖配置
├── src/main/resources/
│   ├── application.yml                      # 主配置文件
│   ├── schema.sql                           # 数据库表结构
│   ├── data.sql                             # 种子数据
│   └── mapper/                              # MyBatis XML 映射文件 (12个)
│       ├── UserMapper.xml
│       ├── RoleMapper.xml
│       ├── MenuMapper.xml
│       ├── DeptMapper.xml
│       ├── PostMapper.xml
│       ├── DictTypeMapper.xml
│       ├── DictDataMapper.xml
│       ├── ConfigMapper.xml
│       ├── NoticeMapper.xml
│       ├── OperLogMapper.xml
│       ├── LoginLogMapper.xml
│       └── JobMapper.xml
└── src/main/java/com/example/admin/
    ├── AdminApplication.java                # 启动类
    ├── config/                              # 配置 (6个)
    │   ├── SecurityConfig.java              # Spring Security 安全配置
    │   ├── TokenAuthenticationFilter.java   # Token 认证过滤器
    │   ├── LoginRateLimitFilter.java        # 登录限流过滤器
    │   ├── CorsConfig.java                  # CORS 跨域配置
    │   ├── SwaggerConfig.java               # Swagger/SpringDoc 配置
    │   └── PasswordMigrationRunner.java      # 密码迁移执行器
    ├── controller/                          # REST 控制器 (17个)
    │   ├── LoginController.java             # 登录认证
    │   ├── UserController.java              # 用户管理
    │   ├── RoleController.java              # 角色管理
    │   ├── MenuController.java              # 菜单管理
    │   ├── DeptController.java              # 部门管理
    │   ├── PostController.java              # 岗位管理
    │   ├── DictTypeController.java          # 字典类型
    │   ├── DictDataController.java          # 字典数据
    │   ├── ConfigController.java            # 参数配置
    │   ├── NoticeController.java            # 通知公告
    │   ├── OperLogController.java           # 操作日志
    │   ├── LoginLogController.java          # 登录日志
    │   ├── OnlineUserController.java       # 在线用户
    │   ├── JobController.java               # 定时任务
    │   ├── MonitorController.java           # 系统监控
    │   ├── GenController.java               # 代码生成
    │   └── DashboardController.java         # 仪表盘
    ├── entity/                              # 数据实体 (13个)
    │   ├── Result.java                      # 统一响应封装
    │   ├── User.java
    │   ├── Role.java
    │   ├── Menu.java
    │   ├── Dept.java
    │   ├── Post.java
    │   ├── DictType.java
    │   ├── DictData.java
    │   ├── Config.java
    │   ├── Notice.java
    │   ├── OperLog.java
    │   ├── LoginLog.java
    │   └── Job.java
    ├── mapper/                              # MyBatis Mapper 接口 (12个)
    ├── service/                             # 业务逻辑层 (16个)
    │   ├── TokenService.java                # Token 认证核心
    │   ├── UserDetailsServiceImpl.java      # Spring Security 用户加载
    │   ├── UserService.java
    │   ├── RoleService.java
    │   ├── MenuService.java
    │   ├── DeptService.java
    │   ├── PostService.java
    │   ├── DictTypeService.java
    │   ├── DictDataService.java
    │   ├── ConfigService.java
    │   ├── NoticeService.java
    │   ├── OperLogService.java
    │   ├── LoginLogService.java
    │   ├── OnlineUserService.java
    │   ├── JobService.java
    │   └── GenService.java
    ├── store/
    │   └── DataStore.java                   # 内存数据存储
    └── aspect/
        └── LogAspect.java                   # 操作日志 AOP 切面
```

### 2.2 前端结构

```
vue-admin/
├── index.html
├── vite.config.js                           # Vite 配置（含 API 代理）
├── package.json
└── src/
    ├── main.js                              # 入口文件
    ├── App.vue                              # 根组件
    ├── router/
    │   └── index.js                         # 路由配置 + dynamicRouteMap
    ├── permission.js                        # 路由守卫（菜单动态生成）
    ├── store/
    │   └── index.js                         # Vuex 状态管理
    ├── utils/
    │   └── request.js                       # Axios 封装（拦截器）
    ├── api/                                 # API 接口层 (17个)
    │   ├── login.js                         # 登录 + 用户信息 + 菜单路由
    │   ├── dashboard.js                     # 仪表盘统计
    │   ├── user.js
    │   ├── role.js
    │   ├── menu.js
    │   ├── dept.js
    │   ├── post.js
    │   ├── dict.js
    │   ├── config.js
    │   ├── notice.js
    │   ├── operlog.js
    │   ├── loginlog.js
    │   ├── online.js
    │   ├── job.js
    │   ├── server.js
    │   ├── cache.js
    │   └── gen.js
    ├── layout/                              # 布局组件 (4个)
    │   ├── index.vue                        # 主布局（侧边栏 + 主内容）
    │   ├── Sidebar.vue                      # 侧边栏菜单
    │   ├── MenuItem.vue                     # 递归菜单项
    │   └── Navbar.vue                       # 顶部导航栏
    └── views/                               # 页面组件 (20个)
        ├── login/index.vue
        ├── dashboard/index.vue
        ├── system/
        │   ├── user/index.vue
        │   ├── role/index.vue
        │   ├── menu/index.vue
        │   ├── dept/index.vue
        │   ├── post/index.vue
        │   ├── dict/index.vue
        │   ├── config/index.vue
        │   └── notice/index.vue
        ├── monitor/
        │   ├── operlog/index.vue
        │   ├── loginlog/index.vue
        │   ├── online/index.vue
        │   ├── job/index.vue
        │   ├── server/index.vue
        │   ├── cache/index.vue
        │   └── druid/index.vue
        └── tool/
            ├── gen/index.vue
            ├── swagger/index.vue
            └── build/index.vue
```

---

## 3. 数据库设计

### 3.1 核心权限表 (5张)

```
sys_user  ←── sys_user_role ──→  sys_role  ──→  sys_role_menu  ──→  sys_menu
```

| 表 | 用途 | 关键字段 |
|------|------|---------|
| `sys_user` | 用户 | id, username, password（BCrypt 加密）, nickname, status |
| `sys_role` | 角色 | id, role_key（唯一标识）, role_name, status |
| `sys_menu` | 菜单/权限 | id, parent_id（树形）, menu_type（M/C/F）, perms（权限标识） |
| `sys_user_role` | 用户-角色关联 | user_id, role_id（联合主键） |
| `sys_role_menu` | 角色-菜单关联 | role_id, menu_id（联合主键） |

### 3.2 业务表 (7张)

| 表 | 用途 | 关键字段 |
|------|------|---------|
| `sys_dept` | 部门管理 | parent_id（树形）, dept_name, order_num, leader |
| `sys_post` | 岗位管理 | post_code, post_name, post_sort |
| `sys_dict_type` | 字典类型 | dict_name, dict_type（唯一编码） |
| `sys_dict_data` | 字典数据 | dict_type_id（外键）, dict_label, dict_value, dict_sort |
| `sys_config` | 参数配置 | config_name, config_key（唯一）, config_value, config_type |
| `sys_notice` | 通知公告 | notice_title, notice_type（1=通知/2=公告）, notice_content |

### 3.3 日志表 (2张)

| 表 | 用途 | 记录方式 |
|------|------|---------|
| `sys_login_log` | 登录日志 | LoginController 登录/登出时自动记录 |
| `sys_oper_log` | 操作日志 | LogAspect AOP 切面自动记录 |

### 3.4 定时任务表 (1张)

| 表 | 用途 |
|------|------|
| `sys_job` | 定时任务配置 |

### 3.5 菜单类型说明

| menuType | 含义 | 示例 | 前端处理 |
|----------|------|------|---------|
| M | 目录（有子菜单） | 系统管理 | 渲染为 el-submenu |
| C | 菜单（叶子节点） | 用户管理 | 渲染为 el-menu-item，映射路由 |
| F | 按钮权限 | system:user:create | 页面内控制按钮显隐 |

---

## 4. 认证与授权流程

```
┌──────────┐     ┌──────────────────┐     ┌─────────────┐
│ 前端登录  │ ──→ │ POST /api/login  │ ──→ │ LoginRate    │
│          │     │                  │     │ LimitFilter  │
└──────────┘     └──────────────────┘     │ (限流检查)    │
                                          └──────┬───────┘
                                                 │
                                          ┌──────▼───────┐
                                          │ UserDetails  │
                                          │ Service      │
                                          │ 加载用户      │
                                          └──────┬───────┘
                                                 │
                                          ┌──────▼───────┐
                                          │ BCrypt       │
                                          │ 密码校验      │
                                          └──────┬───────┘
                                                 │
                                          ┌──────▼───────┐
                                          │ 生成 UUID    │
                                          │ Token        │
                                          │ 存入          │
                                          │ DataStore     │
                                          └──────┬───────┘
                                                 │
                                          ┌──────▼───────┐
                                          │ 前端存储      │
                                          │ Token 到      │
                                          │ localStorage │
                                          └──────┬───────┘
                                                 │
                                          ┌──────▼──────────┐
                                          │ 后续请求自动附加 │
                                          │ Authorization:  │
                                          │ Bearer {token}  │
                                          └──────┬──────────┘
                                                 │
                                          ┌──────▼──────────────────┐
                                          │ TokenAuthentication     │
                                          │ Filter 解析 Token       │
                                          │ 查询用户 → 加载权限    │
                                          │ 设置 SecurityContext    │
                                          └──────┬──────────────────┘
                                                 │
                                          ┌──────▼─────────┐
                                          │ @PreAuthorize   │
                                          │ 注解鉴权        │
                                          └────────────────┘
```

### 4.1 Token 认证过滤链

```
SecurityConfig
  └── SecurityFilterChain
       ├── .csrf().disable()
       ├── .sessionManagement().stateless()
       ├── .antMatchers("/api/login").permitAll()              # 登录接口开放
       ├── .antMatchers("/swagger-ui/**", ...).permitAll()      # Swagger 开放
       ├── .antMatchers("/actuator/**").permitAll()              # 监控端点开放
       ├── .antMatchers("/api/**").authenticated()               # 其他 API 需认证
       └── 过滤器链（按顺序）:
            ├── LoginRateLimitFilter                           # 登录限流（1分钟5次，超出封禁15分钟）
            ├── TokenAuthenticationFilter                      # Token 解析 + 权限加载
            │    ├── 从 Authorization 头提取 Bearer token
            │    ├── 查询 DataStore.tokenMap 获取 userId
            │    ├── 加载用户权限标识（permissions）+ 角色标识（ROLE_xxx）
            │    ├── 设置 UsernamePasswordAuthenticationToken
            │    └── 写入 SecurityContextHolder
            └── 统一异常处理:
                 ├── 401 → {"code":401,"msg":"未登录或token已过期"}
                 └── 403 → {"code":403,"msg":"权限不足"}
```

### 4.2 登录限流策略

| 参数 | 值 |
|------|-----|
| 统计维度 | IP + 用户名 |
| 时间窗口 | 1 分钟 |
| 最大尝试次数 | 5 次 |
| 封禁时长 | 15 分钟 |
| 超出后响应 | HTTP 429 |

- 登录成功自动清除该维度的计数
- 用户名不存在或密码错误均计入失败次数
- 存储基于 `ConcurrentHashMap` 内存实现，服务重启后清零

### 4.3 权限模型

```
用户 ──→ 角色 ──→ 菜单（含 perms 字段） ──→ 权限标识
                                                  │
                                                  ▼
                                       @PreAuthorize("hasAuthority('system:user:list')")
```

---

## 5. 前端数据流

### 5.1 启动流程

```
页面加载
  │
  ▼
router.beforeEach（permission.js）
  │
  ├── 未登录 → /login
  │
  └── 已登录
        │
        ├── 已加载用户信息 → next()
        │
        └── 未加载用户信息
              │
              ├── store.dispatch('getUserInfoAndMenus')
              │      ├── GET /api/user/info → { user, roles, permissions }
              │      └── GET /api/menus/routers → 菜单树
              │
              ├── router.addRoutes() → 注册动态路由
              └── next({ ...to, replace: true })
```

### 5.2 动态路由生成

```
后端菜单树（menuType ≠ 'F'）
  │
  ▼
permission.js → generateRoutes(menus)
  │
  ├── 遍历菜单树
  ├── menuType === 'C' → 创建路由
  │     ├── path = menu.path（去除前导 /）
  │     ├── meta = { title, icon }
  │     └── component = dynamicRouteMap[menu.component]
  │
  └── 注册到 Router
        └── { path: '/', component: Layout, children: [动态路由] }
```

dynamicRouteMap 在 `router/index.js` 中定义，包含所有页面组件的懒加载映射（共 19 个映射项）：

```js
'dashboard/index':       () => import('../views/dashboard/index.vue'),
'system/user/index':     () => import('../views/system/user/index.vue'),
'system/role/index':     () => import('../views/system/role/index.vue'),
'system/menu/index':     () => import('../views/system/menu/index.vue'),
'system/dept/index':     () => import('../views/system/dept/index.vue'),
'system/post/index':     () => import('../views/system/post/index.vue'),
'system/dict/index':     () => import('../views/system/dict/index.vue'),
'system/config/index':   () => import('../views/system/config/index.vue'),
'system/notice/index':   () => import('../views/system/notice/index.vue'),
'monitor/operlog/index': () => import('../views/monitor/operlog/index.vue'),
'monitor/loginlog/index':() => import('../views/monitor/loginlog/index.vue'),
'monitor/online/index':  () => import('../views/monitor/online/index.vue'),
'monitor/job/index':     () => import('../views/monitor/job/index.vue'),
'monitor/server/index':  () => import('../views/monitor/server/index.vue'),
'monitor/cache/index':   () => import('../views/monitor/cache/index.vue'),
'monitor/druid/index':   () => import('../views/monitor/druid/index.vue'),
'tool/gen/index':        () => import('../views/tool/gen/index.vue'),
'tool/swagger/index':    () => import('../views/tool/swagger/index.vue'),
'tool/build/index':      () => import('../views/tool/build/index.vue'),
```

### 5.3 侧边栏渲染

```
Vuex state.menus（树形结构）
  │
  ▼
Layout.vue → Sidebar.vue → MenuItem.vue（递归）
  │
  ├── el-submenu（menuType === 'M' 且有子菜单）
  │     └── MenuItem.vue（递归渲染子节点）
  │
  └── el-menu-item（menuType === 'C'）
```

### 5.4 Axios 拦截器

```
请求拦截器
  └── 自动添加 Authorization: Bearer {token}

响应拦截器
  ├── code === 200 → 返回 response.data
  ├── code === 401 → 清除 Token，跳转 /login
  ├── code !== 200 → Message.error(msg)
  └── HTTP 401 → 跳转 /login
      HTTP 403 → Message.error('权限不足')
      HTTP 其他 → 显示 error.message
```

### 5.5 按钮级权限

```vue
<el-button v-if="hasPerm('system:config:create')">新增</el-button>
```

```js
computed: { ...mapState(['permissions']) },
methods: {
  hasPerm(perm) {
    return this.permissions.includes(perm)
  }
}
```

---

## 6. 后端核心模块说明

### 6.1 Result 统一响应类

继承 `HashMap<String, Object>`，支持链式调用：

```java
Result.success(data)        // { code: 200, msg: "success", data: ... }
Result.success(msg, data)   // { code: 200, msg: "...", data: ... }
Result.error(msg)           // { code: 500, msg: "..." }
Result.error(code, msg)     // { code: xxx, msg: "..." }
```

### 6.2 DataStore 内存存储

`@Component` 单例，使用 `ConcurrentHashMap` 管理 Token：

```java
public final Map<String, Long> tokenMap;           // token → userId
public final Map<Long, List<String>> userTokens;   // userId → token 集合
```

- `buildTree(flatList)` — 将平铺菜单列表构建为树形结构

### 6.3 TokenService 认证核心

| 方法 | 用途 |
|------|------|
| `generateToken(user)` | 生成 UUID Token → 存入 DataStore |
| `logout(token)` | 移除 Token → 清理 userTokens |
| `getUserFromToken(token)` | 解析 Token → 查询用户 → 检查禁用状态 |
| `getPermissionsByRoleIds(roleIds)` | 收集所有角色的权限标识 |
| `getMenusByRoleIds(roleIds)` | 收集所有角色的菜单（不含按钮 F 类型） |
| `invalidateUserTokens(userId)` | 使指定用户的所有 Token 失效 |

### 6.4 LoginRateLimitFilter 登录限流

登录请求在进入 Controller 之前经过限流过滤器：

- 仅拦截 `POST /api/login`
- 按 `IP:用户名` 维度统计
- 1 分钟窗口内最多 5 次尝试，超出后封禁 15 分钟
- 登录成功调用 `recordSuccess()` 清除计数
- 登录失败调用 `recordFailure()` 累加计数

### 6.5 LogAspect 操作日志切面

```
@Aspect
@Pointcut: 拦截 com.example.admin.controller 下所有方法
  │
  ├── @AfterReturning → 记录成功操作
  └── @AfterThrowing → 记录异常操作
        │
        └── recordLog()
              ├── 跳过 /login, /logout, /user/info, /menus/routers
              ├── 分类 businessType: 新增=1, 修改=2, 删除=3, 其他=0
              ├── 跳过纯查询操作（businessType=0）
              └── 写入 sys_oper_log
```

### 6.6 GenService 代码生成器

从 MySQL `information_schema` 读取表和列信息，使用 StringBuilder 模板生成 7 个文件打包为 ZIP。

**MySQL 类型 → Java 类型映射**:

| MySQL 类型 | Java 类型 |
|-----------|-----------|
| bigint | Long |
| int/tinyint/smallint | Integer |
| double/decimal | Double |
| datetime/timestamp | LocalDateTime |
| date | java.time.LocalDate |
| varchar/char/text/其他 | String |

**生成的 ZIP 包含**:

| 路径 | 说明 |
|------|------|
| `entity/{EntityName}.java` | 实体类 |
| `mapper/{EntityName}Mapper.java` | MyBatis Mapper 接口 |
| `mapper/{EntityName}Mapper.xml` | MyBatis XML 映射 |
| `service/{EntityName}Service.java` | Service 层 |
| `controller/{EntityName}Controller.java` | Controller（含 @PreAuthorize） |
| `api/{instanceName}.js` | Axios API 文件 |
| `vue/{instanceName}/index.vue` | Element UI 管理页面 |

### 6.7 Controller 通用模式

所有业务模块的 Controller 遵循统一 CRUD 模式：

```
@RestController
@RequestMapping("/api/{resource}s")
public class XxxController {

    @GetMapping            → list(keyword)      → @PreAuthorize
    @GetMapping("/{id}")   → get(id)             → @PreAuthorize
    @PostMapping           → add(entity)         → @PreAuthorize
    @PutMapping            → update(entity)      → @PreAuthorize
    @DeleteMapping("/{id}") → delete(id)         → @PreAuthorize
}
```

### 6.8 Service 通用模式

```java
@Service
public class XxxService {
    public List<Xxx> list(String keyword)    // 调用 Mapper.selectList
    public Xxx getById(Long id)              // 调用 Mapper.selectById
    public Xxx add(Xxx entity)               // setCreateTime + Mapper.insert
    public Xxx update(Xxx entity)            // 先查后改
    public void delete(Long id)              // 调用 Mapper.deleteById
}
```

---

## 7. 前端核心模块说明

### 7.1 router/index.js — 路由映射

```js
// 公开路由（无需认证）
constantRoutes = [{ path: '/login', component: Login }]

// 动态路由映射（component 路径 → 懒加载函数）
dynamicRouteMap = {
  'dashboard/index': () => import('../views/dashboard/index.vue'),
  'system/user/index': () => import('../views/system/user/index.vue'),
  // ... 共 19 个映射
  '*': () => import('...')  // 兜底占位
}
```

### 7.2 permission.js — 路由守卫

- 白名单：`/login`
- 首次访问时调用 `getUserInfoAndMenus` 获取用户信息 + 菜单 + 权限
- 将后端菜单树转化为 Vue Router 路由动态注册
- `routesAdded` 标记防止重复注册

### 7.3 store/index.js — Vuex 状态

```js
state: {
  token,        // 认证 Token（同步到 localStorage）
  user,         // 用户信息
  roles,        // 角色列表
  permissions,  // 权限标识列表
  menus         // 菜单树（侧边栏使用）
}
```

Actions：
- `login` — 登录
- `getUserInfoAndMenus` — 依次调用 `getUserInfo()` + `getRouters()`，填充 user/roles/permissions/menus
- `resetState` — 清除所有状态 + localStorage

### 7.4 Layout 布局

```
┌─────────────────────────────────────────────┐
│  Logo                       Navbar（折叠按钮）│
├──────────────┬──────────────────────────────┤
│              │                              │
│  侧边栏      │       <router-view />        │
│  (210px)     │       主内容区域               │
│              │                              │
│  el-menu     │                              │
│              │                              │
└──────────────┴──────────────────────────────┘
```

---

## 8. 数据流示例：用户管理

```
操作：打开用户管理页面

1. 路由守卫 → 已认证 → 放行
2. router → /user → dynamicRouteMap['system/user/index'] → 加载页面组件
3. 页面 created → this.fetchData()
4. fetchData → listUsers({ keyword }) → Axios GET /api/users
5. Axios 请求拦截器自动添加 Authorization: Bearer {token}
6. 后端 TokenAuthenticationFilter 解析 Token → 设置 SecurityContext
7. UserController.list → @PreAuthorize('system:user:list') → 鉴权
8. UserService.list → UserMapper.selectList → MySQL 查询
9. 返回 Result.success(users)
10. Axios 响应拦截器 → code === 200 → 返回 data
11. 页面渲染 el-table
```

---

## 9. 安全配置摘要

```
CSRF:         禁用（使用 Token 认证，无需 CSRF）
Session:      无状态（STATELESS）
密码存储:     BCryptPasswordEncoder
Token:        UUID 随机字符串，ConcurrentHashMap 内存存储
认证入口:     Bearer Token → TokenAuthenticationFilter → SecurityContextHolder
权限控制:     @PreAuthorize + Spring Security Method Security
登录限流:     LoginRateLimitFilter（1分钟/5次/15分钟封禁，HTTP 429）
异常处理:
  401 → { code: 401, msg: "未登录或token已过期" }
  403 → { code: 403, msg: "权限不足" }
```

---

## 10. 部署说明

### 开发环境

```bash
# 后端
cd spring-boot-admin
mvn spring-boot:run    # http://localhost:8089

# 前端
cd vue-admin
npm run dev            # http://localhost:5173（Vite 代理 /api → :8089）
```

### 构建生产

```bash
# 后端
mvn package -DskipTests
java -jar target/spring-boot-admin-1.0.0.jar

# 前端
npm run build          # 输出到 dist/
```

### 环境要求

- JDK 1.8+
- MySQL 8.0+
- Node.js（前端开发）
- 默认端口：8089（后端）, 5173（前端开发服务器）

### Vite 代理配置

```js
proxy: {
  '/api':              { target: 'http://localhost:8089', changeOrigin: true },
  '/swagger-ui':       { target: 'http://localhost:8089', changeOrigin: true },
  '/v3/api-docs':      { target: 'http://localhost:8089', changeOrigin: true },
  '/swagger-resources':{ target: 'http://localhost:8089', changeOrigin: true },
  '/webjars':          { target: 'http://localhost:8089', changeOrigin: true },
  '/doc.html':         { target: 'http://localhost:8089', changeOrigin: true }
}
```
