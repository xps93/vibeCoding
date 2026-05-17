# 架构文档

> 基于 RuoYi 风格的企业级后台管理系统，前后端分离架构。

---

## 技术栈

| 层次   | 技术                                    | 版本    |
|--------|-----------------------------------------|---------|
| 后端   | Spring Boot                             | 2.7.18  |
| 后端   | Spring Security                         | (嵌入)  |
| 后端   | MyBatis                                 | 2.3.2   |
| 后端   | MySQL Connector                         | 8.0.33  |
| 后端   | Knife4j (SpringDoc OpenAPI 增强)        | 4.3.0   |
| 后端   | Spring AOP                              | (嵌入)  |
| 前端   | Vue                                     | 2.7.16  |
| 前端   | Element UI                              | 2.15.14 |
| 前端   | Vite                                    | 5.x     |
| 前端   | Vue Router                              | 3.x     |
| 数据库 | MySQL                                   | 8.0     |
| 运行   | JDK                                     | 1.8     |

---

## 项目结构

### 后端模块 (spring-boot-admin)

```
src/main/java/com/example/admin/
├── AdminApplication.java          # 启动入口
├── aspect/
│   └── LogAspect.java             # AOP 操作日志切面
├── config/
│   ├── CorsConfig.java            # 跨域配置（允许所有来源）
│   ├── LoginRateLimitFilter.java  # 登录限流过滤器（5次/分钟，封锁15分钟）
│   ├── PasswordMigrationRunner.java # 启动时自动迁移 {noop} 密码到 BCrypt
│   ├── SecurityConfig.java        # Spring Security 安全配置
│   ├── SwaggerConfig.java         # Knife4j / OpenAPI 文档配置
│   └── TokenAuthenticationFilter.java # Token 鉴权过滤器
├── controller/                    # REST 控制器层（共16个控制器）
│   ├── LoginController.java       # 认证管理：登录/退出/用户信息/菜单路由
│   ├── UserController.java        # 用户管理 CRUD
│   ├── RoleController.java        # 角色管理 CRUD + 权限分配
│   ├── MenuController.java        # 菜单管理 CRUD + 树形结构
│   ├── DeptController.java        # 部门管理 CRUD + 树形结构
│   ├── PostController.java        # 岗位管理 CRUD
│   ├── DictTypeController.java    # 字典类型管理 CRUD
│   ├── DictDataController.java    # 字典数据管理 CRUD
│   ├── ConfigController.java      # 参数配置管理 CRUD
│   ├── NoticeController.java      # 通知公告管理 CRUD
│   ├── OperLogController.java     # 操作日志查询/删除/清空
│   ├── LoginLogController.java    # 登录日志查询/删除/清空
│   ├── OnlineUserController.java  # 在线用户查看/强制退出
│   ├── JobController.java         # 定时任务 CRUD
│   ├── MonitorController.java     # 服务器/JVM/缓存监控
│   └── GenController.java         # 代码生成器（表/列查询 + ZIP 下载）
├── entity/                        # 数据实体（共11个）
│   ├── Result.java                # 统一响应封装
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
├── mapper/                        # MyBatis Mapper 接口
│   ├── UserMapper.java
│   ├── RoleMapper.java
│   ├── MenuMapper.java
│   ├── DeptMapper.java
│   ├── PostMapper.java
│   ├── DictTypeMapper.java
│   ├── DictDataMapper.java
│   ├── ConfigMapper.java
│   ├── NoticeMapper.java
│   ├── OperLogMapper.java
│   ├── LoginLogMapper.java
│   └── JobMapper.java
├── service/                       # 业务逻辑层
│   ├── TokenService.java          # Token 生成/校验/权限/菜单查询
│   ├── UserDetailsServiceImpl.java # Spring Security 用户加载
│   ├── UserService.java           # 用户管理（含密码加密、角色关联、token 失效）
│   ├── RoleService.java           # 角色管理（含菜单权限关联）
│   ├── MenuService.java           # 菜单管理（含树形构建）
│   ├── DeptService.java
│   ├── PostService.java
│   ├── DictTypeService.java
│   ├── DictDataService.java
│   ├── ConfigService.java
│   ├── NoticeService.java
│   ├── OperLogService.java
│   ├── LoginLogService.java
│   ├── OnlineUserService.java     # 在线用户管理（基于内存 token）
│   ├── JobService.java
│   └── GenService.java            # 代码生成（动态读表、生成 ZIP）
└── store/
    └── DataStore.java             # 全局内存数据存储（token、菜单树构建）
```

### 前端模块 (vue-admin)

```
src/
├── api/                    # API 接口封装层
├── assets/                 # 静态资源
├── components/             # 公共组件
├── layout/                 # 布局组件（Layout, Sidebar, Navbar 等）
├── router/                 # 路由配置（动态路由）
├── store/                  # Vuex / Pinia 状态管理
├── utils/                  # 工具函数（request.js 等）
├── views/                  # 页面组件（按模块组织）
│   ├── dashboard/          # 首页
│   ├── system/             # 系统管理页面
│   │   ├── user/           # 用户管理
│   │   ├── role/           # 角色管理
│   │   ├── menu/           # 菜单管理
│   │   ├── dept/           # 部门管理
│   │   ├── post/           # 岗位管理
│   │   ├── dict/           # 字典管理
│   │   ├── config/         # 参数管理
│   │   └── notice/         # 通知公告
│   ├── monitor/            # 系统监控页面
│   │   ├── operlog/        # 操作日志
│   │   ├── loginlog/       # 登录日志
│   │   ├── online/         # 在线用户
│   │   ├── job/            # 定时任务
│   │   ├── server/         # 服务监控
│   │   ├── cache/          # 缓存监控
│   │   └── druid/          # 连接池监视
│   └── tool/               # 系统工具页面
│       ├── gen/            # 代码生成
│       ├── swagger/        # 系统接口
│       └── build/          # 在线构建器
├── App.vue
└── main.js
```

### 配置文件

```
src/main/resources/
├── application.yml     # 应用配置（数据库、MyBatis、Knife4j、Actuator）
├── schema.sql          # 数据库建表脚本（12 张表）
├── data.sql            # 初始化数据（菜单/角色/用户/权限关联）
└── mapper/             # MyBatis XML 映射（12 个）
    ├── UserMapper.xml
    ├── RoleMapper.xml
    ├── MenuMapper.xml
    └── ...
```

---

## 认证流程

```
请求 → LoginRateLimitFilter(限流) → TokenAuthenticationFilter(校验Token) → Controller
```

### 登录流程

```
1. POST /api/login { username, password }
2. LoginRateLimitFilter 检查该IP+用户是否被限流
3. UserDetailsServiceImpl.loadUserByUsername() 加载用户
4. PasswordEncoder.matches() 校验 BCrypt 密码
5. 校验通过 → TokenService.generateToken() → 生成 UUID Token
6. Token 存入 DataStore.tokenMap (ConcurrentHashMap)
7. 返回 token 给前端
8. 记录登录日志 (sys_login_log)
```

### 请求鉴权流程

```
1. 请求携带 Authorization: Bearer {token}
2. TokenAuthenticationFilter 提取 token
3. 从 DataStore.tokenMap 查询 userId
4. 根据 userId 加载 User 和其角色/权限
5. 构建 UsernamePasswordAuthenticationToken 设置到 SecurityContextHolder
6. @PreAuthorize 注解自动校验权限
7. 无 token → 返回 401；权限不足 → 返回 403
```

### Token 数据结构

```java
DataStore {
  tokenMap: ConcurrentHashMap<String, Long>       // token -> userId
  userTokens: ConcurrentHashMap<Long, List<String>> // userId -> [token1, token2, ...]
}
```

---

## 数据库表

| 表名               | 说明         | 主要字段 |
|--------------------|--------------|----------|
| sys_user           | 用户表       | id, username, password, nickname, email, phone, avatar, status, create_time |
| sys_role           | 角色表       | id, role_key, role_name, status, create_time |
| sys_menu           | 菜单权限表   | id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status, create_time |
| sys_user_role      | 用户角色关联表 | user_id, role_id |
| sys_role_menu      | 角色菜单关联表 | role_id, menu_id |
| sys_dept           | 部门表       | id, parent_id, dept_name, order_num, leader, phone, email, status, create_time |
| sys_post           | 岗位表       | id, post_code, post_name, post_sort, status, create_time |
| sys_dict_type      | 字典类型表   | id, dict_name, dict_type, status, create_time |
| sys_dict_data      | 字典数据表   | id, dict_type_id, dict_label, dict_value, dict_sort, status, create_time |
| sys_config         | 参数配置表   | id, config_name, config_key, config_value, config_type, create_time |
| sys_notice         | 通知公告表   | id, notice_title, notice_type, notice_content, status, create_time |
| sys_oper_log       | 操作日志表   | id, title, business_type, method, request_method, oper_name, oper_url, oper_ip, oper_param, json_result, status, error_msg, oper_time |
| sys_login_log      | 登录日志表   | id, user_name, status, ip_addr, msg, login_time |
| sys_job            | 定时任务表   | id, job_name, job_group, invoke_target, cron_expression, status, create_time |

**初始化数据**
- 默认用户: admin(密码: admin123) / user(密码: user123) / test(密码: test123)
- 默认角色: admin(超级管理员) / user(普通用户)
- admin 角色拥有所有菜单和按钮权限
- user 角色拥有基础菜单权限

---

## 安全措施

| 措施 | 说明 |
|------|------|
| BCrypt 密码加密 | 所有用户密码使用 BCryptPasswordEncoder 加密存储 |
| 登录限流 | LoginRateLimitFilter: 同一 IP+用户 5 次/分钟尝试，超限封锁 15 分钟 |
| Token 认证 | 基于 UUID Token 的无状态认证 (STATELESS) |
| @PreAuthorize 权限控制 | 每个接口标注所需权限标识，Spring Security 自动校验 |
| CSRF 防护禁用 | RESTful API 采用 Token 认证，禁用 CSRF |
| 密码迁移 | 启动时自动检测 {noop} 前缀密码并迁移到 BCrypt |
| 自动停用 | 用户被禁用时自动清除其所有 token 使其强制下线 |
| AOP 操作日志 | LogAspect 切面自动记录新增/修改/删除操作到操作日志表 |
| 认证异常处理 | 401 返回 "未登录或token已过期"，403 返回 "权限不足" |

---

## 运行说明

### 后端

```bash
# 1. 创建数据库 (MySQL 8.0)
CREATE DATABASE ai_test DEFAULT CHARACTER SET utf8mb4;

# 2. 配置 application.yml 中的数据库连接信息

# 3. 启动应用 (端口 8089)
cd spring-boot-admin
mvn spring-boot:run
# 或
java -jar target/spring-boot-admin-1.0.0.jar
```

应用启动时自动执行 `schema.sql` 建表 + `data.sql` 初始化数据。

### 前端

```bash
cd vue-admin
npm install
npm run dev
```

### 访问地址

| 地址 | 说明 |
|------|------|
| http://localhost:5173 | 前端页面（Vite 开发服务器） |
| http://localhost:8089/doc.html | Knife4j API 文档 |
| http://localhost:8089/v3/api-docs | OpenAPI JSON |
| http://localhost:8089/actuator/health | 健康检查 |

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin  | admin123 | 超级管理员 |
| user   | user123  | 普通用户 |
| test   | test123  | 普通用户 |
