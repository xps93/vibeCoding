# API 文档

基于 Spring Boot 的后台管理系统 API 文档。

**基础路径**: `http://localhost:8089/api`

**认证方式**: Bearer Token（请求头 `Authorization: Bearer {token}`）

**Swagger UI**: `http://localhost:8089/swagger-ui/index.html`

---

## 目录

1. [通用说明](#1-通用说明)
2. [仪表盘](#2-仪表盘)
3. [认证管理](#3-认证管理)
4. [用户管理](#4-用户管理)
5. [角色管理](#5-角色管理)
6. [菜单管理](#6-菜单管理)
7. [部门管理](#7-部门管理)
8. [岗位管理](#8-岗位管理)
9. [字典类型管理](#9-字典类型管理)
10. [字典数据管理](#10-字典数据管理)
11. [参数配置管理](#11-参数配置管理)
12. [通知公告管理](#12-通知公告管理)
13. [操作日志](#13-操作日志)
14. [登录日志](#14-登录日志)
15. [在线用户](#15-在线用户)
16. [定时任务](#16-定时任务)
17. [系统监控](#17-系统监控)
18. [代码生成](#18-代码生成)
19. [基础设施端点](#19-基础设施端点)
20. [权限标识附录](#20-权限标识附录)

---

## 1. 通用说明

### 统一响应格式

所有 API 返回统一的 JSON 格式：

```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | int | 200=成功, 401=未登录, 403=无权限, 429=限流, 500=服务器错误 |
| `msg` | string | 提示信息 |
| `data` | object | 响应数据（可选） |

### 通用查询参数

- `keyword` — 按名称模糊搜索（适用于大多数列表接口）

### 认证流程

```
POST /api/login       → 返回 { token: "xxx" }
GET  /api/user/info   → 获取用户信息 + 权限列表
GET  /api/menus/routers → 获取菜单树
```

所有后续请求需在 Header 中添加 `Authorization: Bearer {token}`。

### 登录限流

| 参数 | 值 |
|------|-----|
| 统计维度 | IP + 用户名 |
| 时间窗口 | 1 分钟 |
| 最大次数 | 5 次 |
| 封禁时长 | 15 分钟 |
| 超限响应 | HTTP 429 `{"code":429,"msg":"登录尝试过于频繁，请15分钟后再试"}` |

登录成功后该维度的失败计数自动清零。

---

## 2. 仪表盘

**Controller**: `DashboardController.java` — `/api/dashboard`

### 2.1 仪表盘统计

```
GET /api/dashboard
```

认证: 已登录

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "stats": {
      "userCount": 5,
      "roleCount": 3,
      "menuCount": 25,
      "deptCount": 4,
      "postCount": 6,
      "onlineCount": 2
    },
    "system": {
      "osName": "Windows 11",
      "cpuCores": 16,
      "usedMemory": "256 MB",
      "maxMemory": "4096 MB",
      "javaVersion": "1.8.0_162"
    }
  }
}
```

| 字段 | 说明 |
|------|------|
| `stats.userCount` | 用户总数 |
| `stats.roleCount` | 角色总数 |
| `stats.menuCount` | 菜单总数（不含按钮） |
| `stats.deptCount` | 部门总数 |
| `stats.postCount` | 岗位总数 |
| `stats.onlineCount` | 当前在线 Token 数 |
| `system` | JVM 和操作系统信息 |

---

## 3. 认证管理

**Controller**: `LoginController.java` — `/api`

### 3.1 用户登录

```
POST /api/login
```

认证: 无

**请求体**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**成功响应** (200):
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "token": "23c930a536f245edb8de4a5728bbfbc3"
  }
}
```

**错误响应**:
- `用户名或密码不能为空` — 参数缺失
- `用户名或密码错误` — 凭据无效
- `账号已停用，请联系管理员` — 账号 status=1

**限流**: 登录失败受限流策略控制，超限返回 HTTP 429。

**日志记录**: 每次登录尝试自动写入 `sys_login_log` 表（成功 status=0，失败 status=1）。

### 3.2 退出登录

```
POST /api/logout
```

认证: Bearer Token

**请求头**: `Authorization: Bearer {token}`

**响应**: `{"code": 200, "msg": "success"}`

### 3.3 获取当前用户信息

```
GET /api/user/info
```

认证: Bearer Token

**响应**:
```json
{
  "code": 200,
  "data": {
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "email": "admin@example.com",
      "phone": "13800000000",
      "avatar": "",
      "status": 0,
      "roleIds": [1],
      "createTime": "2026-05-15 20:00:00"
    },
    "roles": ["admin"],
    "permissions": ["system:user:list", "system:role:list", "system:menu:create", ...]
  }
}
```

> `password` 字段在响应中被置为 `null`。

### 3.4 获取路由菜单树

```
GET /api/menus/routers
```

认证: Bearer Token

**响应**: 返回菜单树形结构（仅包含 M=目录 和 C=菜单 类型，过滤 F=按钮 类型）。

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "name": "首页",
      "path": "/dashboard",
      "component": "dashboard/index",
      "icon": "s-home",
      "perms": "",
      "menuType": "C",
      "sort": 0,
      "visible": 0,
      "status": 0,
      "children": null,
      "createTime": null
    },
    {
      "id": 2,
      "parentId": 0,
      "name": "系统管理",
      "path": "/system",
      "component": "Layout",
      "icon": "setting",
      "menuType": "M",
      "children": [ ... ]
    }
  ]
}
```

---

## 4. 用户管理

**Controller**: `UserController.java` — `/api/users`

### 4.1 用户列表

```
GET /api/users?keyword=管理员
```

认证: `system:user:list`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 用户名模糊搜索 |

### 4.2 用户详情

```
GET /api/users/{id}
```

认证: `system:user:list`

### 4.3 新增用户

```
POST /api/users
```

认证: `system:user:create`

```json
{
  "username": "newuser",
  "password": "password123",
  "nickname": "新用户",
  "email": "new@example.com",
  "phone": "13800138000",
  "status": 0,
  "roleIds": [1]
}
```

> 密码使用 BCrypt 加密存储。新增用户时，密码不为空则加密后写入。

### 4.4 修改用户

```
PUT /api/users
```

认证: `system:user:edit`

```json
{
  "id": 1,
  "nickname": "管理员",
  "email": "admin@example.com",
  "phone": "13800000000",
  "status": 1,
  "password": "newpassword",
  "roleIds": [1, 2]
}
```

> `password` 为空字符串则不修改密码。用户被禁用（status=1）时自动使所有 Token 失效。

### 4.5 删除用户

```
DELETE /api/users/{id}
```

认证: `system:user:delete`

### 4.6 数据权限

| 权限标识 | 操作 |
|----------|------|
| `system:user:list` | 查询用户 |
| `system:user:create` | 新增用户 |
| `system:user:edit` | 修改用户 |
| `system:user:delete` | 删除用户 |

---

## 5. 角色管理

**Controller**: `RoleController.java` — `/api/roles`

### 5.1 角色列表

```
GET /api/roles?keyword=管理员
```

认证: `system:role:list`

### 5.2 角色详情

```
GET /api/roles/{id}
```

认证: `system:role:list`

### 5.3 新增角色

```
POST /api/roles
```

认证: `system:role:create`

```json
{
  "roleKey": "editor",
  "roleName": "编辑员",
  "status": 0,
  "menuIds": [1, 2, 3]
}
```

### 5.4 修改角色

```
PUT /api/roles
```

认证: `system:role:edit`

```json
{
  "id": 1,
  "roleName": "超级管理员",
  "status": 0,
  "menuIds": [1, 2, 3, 4, 5]
}
```

### 5.5 删除角色

```
DELETE /api/roles/{id}
```

认证: `system:role:delete`

### 5.6 获取角色菜单 ID 列表

```
GET /api/roles/menuIds/{roleId}
```

认证: `system:role:list`

返回: `{ "code": 200, "data": [1, 2, 3, 4, 5] }`

### 5.7 获取完整菜单树（角色分配用）

```
GET /api/roles/menus
```

认证: `system:role:list`

返回完整菜单树（含按钮类型 F），用于角色权限分配界面。

### 5.8 数据权限

| 权限标识 | 操作 |
|----------|------|
| `system:role:list` | 查询角色 |
| `system:role:create` | 新增角色 |
| `system:role:edit` | 修改角色 |
| `system:role:delete` | 删除角色 |

---

## 6. 菜单管理

**Controller**: `MenuController.java` — `/api/menus`

### 6.1 菜单树（仅目录+菜单）

```
GET /api/menus
```

认证: `system:menu:list`

返回: 树形结构，仅包含 `menuType=M` 和 `menuType=C` 的记录。

### 6.2 完整菜单树（含按钮）

```
GET /api/menus/all
```

认证: `system:menu:list`

返回: 包含所有类型（M/C/F）的树形结构。

### 6.3 菜单详情

```
GET /api/menus/{id}
```

认证: `system:menu:list`

### 6.4 新增菜单

```
POST /api/menus
```

认证: `system:menu:create`

```json
{
  "parentId": 0,
  "name": "新菜单",
  "path": "/new-path",
  "component": "system/new/index",
  "icon": "setting",
  "perms": "system:new:list",
  "menuType": "C",
  "sort": 1,
  "visible": 0,
  "status": 0
}
```

**菜单类型说明**:

| menuType | 含义 | 说明 |
|----------|------|------|
| M | 目录 | 有子菜单的父级，path=目录路径 |
| C | 菜单 | 叶子菜单，path+component 指向具体页面 |
| F | 按钮 | 页面内的操作权限标识 |

### 6.5 修改菜单

```
PUT /api/menus
```

认证: `system:menu:edit`

### 6.6 删除菜单

```
DELETE /api/menus/{id}
```

认证: `system:menu:delete`

> 删除菜单时会自动删除其所有子菜单。

### 6.7 数据权限

| 权限标识 | 操作 |
|----------|------|
| `system:menu:list` | 查询菜单 |
| `system:menu:create` | 新增菜单 |
| `system:menu:edit` | 修改菜单 |
| `system:menu:delete` | 删除菜单 |

---

## 7. 部门管理

**Controller**: `DeptController.java` — `/api/depts`

### 7.1 部门列表

```
GET /api/depts?keyword=技术部
```

认证: `system:dept:list`

### 7.2 部门树

```
GET /api/depts/tree
```

认证: `system:dept:list`

返回树形结构。

### 7.3 部门详情

```
GET /api/depts/{id}
```

认证: `system:dept:list`

### 7.4 ~ 7.6 新增 / 修改 / 删除

| 方法 | 路径 | 权限 |
|------|------|------|
| POST | `/api/depts` | `system:dept:create` |
| PUT | `/api/depts` | `system:dept:edit` |
| DELETE | `/api/depts/{id}` | `system:dept:delete` |

部门数据模型字段: `parentId, deptName, orderNum, leader, phone, email, status`

---

## 8. 岗位管理

**Controller**: `PostController.java` — `/api/posts`

### 8.1 岗位列表

```
GET /api/posts?keyword=经理
```

认证: `system:post:list`

### 8.2 ~ 8.5 CRUD

| 方法 | 路径 | 权限 |
|------|------|------|
| GET | `/api/posts/{id}` | `system:post:list` |
| POST | `/api/posts` | `system:post:create` |
| PUT | `/api/posts` | `system:post:edit` |
| DELETE | `/api/posts/{id}` | `system:post:delete` |

岗位数据模型字段: `postCode, postName, postSort, status`

---

## 9. 字典类型管理

**Controller**: `DictTypeController.java` — `/api/dict-types`

### 9.1 字典类型列表

```
GET /api/dict-types?keyword=
```

认证: `system:dict:list`

### 9.2 字典类型详情

```
GET /api/dict-types/{id}
```

认证: `system:dict:list`

### 9.3 按类型编码查询

```
GET /api/dict-types/dictType/{dictType}
```

认证: `system:dict:list`

示例: `GET /api/dict-types/dictType/sys_normal_disable`

### 9.4 ~ 9.6 新增 / 修改 / 删除

| 方法 | 路径 | 权限 |
|------|------|------|
| POST | `/api/dict-types` | `system:dict:create` |
| PUT | `/api/dict-types` | `system:dict:edit` |
| DELETE | `/api/dict-types/{id}` | `system:dict:delete` |

---

## 10. 字典数据管理

**Controller**: `DictDataController.java` — `/api/dict-data`

### 10.1 字典数据列表

```
GET /api/dict-data?dictTypeId=1
```

认证: `system:dict:list`

### 10.2 字典数据详情

```
GET /api/dict-data/{id}
```

认证: `system:dict:list`

### 10.3 按字典类型查询数据

```
GET /api/dict-data/type/{dictType}
```

认证: `system:dict:list`

示例: `GET /api/dict-data/type/sys_normal_disable`

### 10.4 ~ 10.6 新增 / 修改 / 删除

| 方法 | 路径 | 权限 |
|------|------|------|
| POST | `/api/dict-data` | `system:dict:create` |
| PUT | `/api/dict-data` | `system:dict:edit` |
| DELETE | `/api/dict-data/{id}` | `system:dict:delete` |

### 10.7 数据权限

| 权限标识 | 操作 |
|----------|------|
| `system:dict:list` | 查询字典 |
| `system:dict:create` | 新增字典 |
| `system:dict:edit` | 修改字典 |
| `system:dict:delete` | 删除字典 |

---

## 11. 参数配置管理

**Controller**: `ConfigController.java` — `/api/configs`

### 11.1 参数列表

```
GET /api/configs?keyword=
```

认证: `system:config:list`

### 11.2 ~ 11.5 CRUD

| 方法 | 路径 | 权限 |
|------|------|------|
| GET | `/api/configs/{id}` | `system:config:list` |
| POST | `/api/configs` | `system:config:create` |
| PUT | `/api/configs` | `system:config:edit` |
| DELETE | `/api/configs/{id}` | `system:config:delete` |

配置数据模型: `configName, configKey, configValue, configType`（0=内置, 1=自定义）

---

## 12. 通知公告管理

**Controller**: `NoticeController.java` — `/api/notices`

### 12.1 ~ 12.5 CRUD

| 方法 | 路径 | 权限 |
|------|------|------|
| GET | `/api/notices?keyword=` | `system:notice:list` |
| GET | `/api/notices/{id}` | `system:notice:list` |
| POST | `/api/notices` | `system:notice:create` |
| PUT | `/api/notices` | `system:notice:edit` |
| DELETE | `/api/notices/{id}` | `system:notice:delete` |

通知数据模型: `noticeTitle, noticeType`（1=通知, 2=公告）, `noticeContent, status`

---

## 13. 操作日志

**Controller**: `OperLogController.java` — `/api/oper-logs`

> 操作日志由 AOP 切面（`LogAspect`）自动记录，无需手动写入。

### 13.1 日志列表

```
GET /api/oper-logs?operName=admin&businessType=1&status=0
```

认证: `monitor:operlog:list`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| operName | string | 否 | 操作人用户名 |
| businessType | int | 否 | 业务类型: 1=新增, 2=修改, 3=删除 |
| status | int | 否 | 状态: 0=正常, 1=异常 |

### 13.2 删除日志

```
DELETE /api/oper-logs/{id}
```

认证: `monitor:operlog:delete`

### 13.3 清空日志

```
DELETE /api/oper-logs/clear
```

认证: `monitor:operlog:delete`

### 13.4 日志数据模型

| 字段 | 类型 | 说明 |
|------|------|------|
| title | string | 操作标题 |
| businessType | int | 1=新增, 2=修改, 3=删除 |
| method | string | 方法名（Controller类名.方法名） |
| requestMethod | string | HTTP 方法（GET/POST/PUT/DELETE） |
| operName | string | 操作人 |
| operUrl | string | 请求 URL |
| operIp | string | 请求 IP |
| operParam | text | 请求参数 JSON |
| jsonResult | text | 响应结果 |
| status | int | 0=正常, 1=异常 |
| errorMsg | text | 错误信息 |
| operTime | datetime | 操作时间 |

### 13.5 自动记录规则

- 切面拦截 `com.example.admin.controller` 包下所有 `@RestController`
- 自动跳过 `/api/login`, `/api/logout`, `/api/user/info`, `/api/menus/routers`
- 仅记录写操作（新增/修改/删除），纯查询不记录

---

## 14. 登录日志

**Controller**: `LoginLogController.java` — `/api/login-logs`

> 登录日志在 `LoginController.login()` 中自动记录。

### 14.1 日志列表

```
GET /api/login-logs?userName=admin&status=0
```

认证: `monitor:loginlog:list`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userName | string | 否 | 用户名 |
| status | int | 否 | 0=成功, 1=失败 |

### 14.2 删除日志

```
DELETE /api/login-logs/{id}
```

认证: `monitor:loginlog:delete`

### 14.3 清空日志

```
DELETE /api/login-logs/clear
```

认证: `monitor:loginlog:delete`

### 14.4 日志数据模型

| 字段 | 类型 | 说明 |
|------|------|------|
| userName | string | 登录用户名 |
| status | int | 0=成功, 1=失败 |
| ipAddr | string | 登录 IP |
| msg | string | 提示信息（登录成功/用户名或密码错误/账号已停用） |
| loginTime | datetime | 登录时间 |

---

## 15. 在线用户

**Controller**: `OnlineUserController.java` — `/api/online-users`

### 15.1 在线用户列表

```
GET /api/online-users
```

认证: `monitor:online:list`

返回当前所有有效的 Token 及其对应用户信息。

### 15.2 强制退出

```
DELETE /api/online-users/{token}
```

认证: `monitor:online:forceLogout`

---

## 16. 定时任务

**Controller**: `JobController.java` — `/api/jobs`

> 当前为轻量实现，不依赖 Quartz。提供完整的 CRUD 接口，任务执行需自行集成调度器。

### 16.1 ~ 16.5 CRUD

| 方法 | 路径 | 权限 |
|------|------|------|
| GET | `/api/jobs?keyword=` | `monitor:job:list` |
| GET | `/api/jobs/{id}` | `monitor:job:list` |
| POST | `/api/jobs` | `monitor:job:create` |
| PUT | `/api/jobs` | `monitor:job:edit` |
| DELETE | `/api/jobs/{id}` | `monitor:job:delete` |

任务数据模型: `jobName, jobGroup, invokeTarget, cronExpression, status`

---

## 17. 系统监控

**Controller**: `MonitorController.java` — `/api/monitor`

无需特定权限，任何已认证用户可访问。

### 17.1 服务监控

```
GET /api/monitor/server
```

返回服务器和 JVM 信息:

```json
{
  "sys": {
    "osName": "Windows 11",
    "osArch": "amd64",
    "osVersion": "10.0",
    "cpuCores": 16
  },
  "jvm": {
    "totalMemory": "512MB",
    "freeMemory": "256MB",
    "maxMemory": "1024MB",
    "javaVersion": "1.8.0_162",
    "javaHome": "C:\\Java\\jdk1.8.0_162"
  }
}
```

### 17.2 缓存监控

```
GET /api/monitor/cache
```

```json
{
  "tokenCount": 5,
  "userSessionCount": 3,
  "cacheType": "ConcurrentHashMap (内存缓存)"
}
```

| 字段 | 说明 |
|------|------|
| tokenCount | 当前有效 Token 总数 |
| userSessionCount | 当前在线用户数 |
| cacheType | 缓存实现方式 |

---

## 18. 代码生成

**Controller**: `GenController.java` — `/api/gen`

无需特定权限，任何已认证用户可访问。

### 18.1 获取数据库表列表

```
GET /api/gen/tables
```

返回所有 `sys_%` 前缀的表名和注释。

### 18.2 获取表字段详情

```
GET /api/gen/tables/{tableName}
```

返回指定表的列信息（列名、类型、注释、主键、可空等）。

### 18.3 生成代码

```
POST /api/gen/generate
```

```json
{
  "tableName": "sys_dept",
  "packageName": "com.example.admin",
  "moduleName": "system",
  "author": "admin"
}
```

**响应**: 返回 ZIP 文件下载（`Content-Type: application/octet-stream`）。

**ZIP 包含的文件**:

| 路径 | 说明 |
|------|------|
| `entity/{EntityName}.java` | 实体类 |
| `mapper/{EntityName}Mapper.java` | MyBatis Mapper 接口 |
| `mapper/{EntityName}Mapper.xml` | MyBatis XML 映射 |
| `service/{EntityName}Service.java` | Service 层 |
| `controller/{EntityName}Controller.java` | Controller（含 @PreAuthorize） |
| `api/{instanceName}.js` | Axios API 文件 |
| `vue/{instanceName}/index.vue` | Element UI 管理页面 |

---

## 19. 基础设施端点

### 19.1 Spring Boot Actuator

```
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

无需认证，已公开访问。

### 19.2 Swagger UI

```
GET /swagger-ui/index.html
GET /v3/api-docs
GET /doc.html
```

无需认证，已公开访问。

**API 分组** (Swagger 配置):

| 分组名称 | 包含的 Controller |
|----------|-------------------|
| 认证管理 | LoginController |
| 系统管理 | User, Role, Menu, Dept, Post, DictType, DictData, Config, Notice |
| 系统监控 | OperLog, LoginLog, OnlineUser, Job, Monitor, Dashboard |
| 系统工具 | Gen |

---

## 20. 权限标识附录

### 系统管理

| 权限标识 | 模块 |
|----------|------|
| `system:user:list` | 用户管理 |
| `system:user:create` | 用户管理 |
| `system:user:edit` | 用户管理 |
| `system:user:delete` | 用户管理 |
| `system:role:list` | 角色管理 |
| `system:role:create` | 角色管理 |
| `system:role:edit` | 角色管理 |
| `system:role:delete` | 角色管理 |
| `system:menu:list` | 菜单管理 |
| `system:menu:create` | 菜单管理 |
| `system:menu:edit` | 菜单管理 |
| `system:menu:delete` | 菜单管理 |
| `system:dept:list` | 部门管理 |
| `system:dept:create` | 部门管理 |
| `system:dept:edit` | 部门管理 |
| `system:dept:delete` | 部门管理 |
| `system:post:list` | 岗位管理 |
| `system:post:create` | 岗位管理 |
| `system:post:edit` | 岗位管理 |
| `system:post:delete` | 岗位管理 |
| `system:dict:list` | 字典管理 |
| `system:dict:create` | 字典管理 |
| `system:dict:edit` | 字典管理 |
| `system:dict:delete` | 字典管理 |
| `system:config:list` | 参数管理 |
| `system:config:create` | 参数管理 |
| `system:config:edit` | 参数管理 |
| `system:config:delete` | 参数管理 |
| `system:notice:list` | 通知公告 |
| `system:notice:create` | 通知公告 |
| `system:notice:edit` | 通知公告 |
| `system:notice:delete` | 通知公告 |

### 系统监控

| 权限标识 | 模块 |
|----------|------|
| `monitor:operlog:list` | 操作日志 |
| `monitor:operlog:delete` | 操作日志 |
| `monitor:loginlog:list` | 登录日志 |
| `monitor:loginlog:delete` | 登录日志 |
| `monitor:online:list` | 在线用户 |
| `monitor:online:forceLogout` | 在线用户 |
| `monitor:job:list` | 定时任务 |
| `monitor:job:create` | 定时任务 |
| `monitor:job:edit` | 定时任务 |
| `monitor:job:delete` | 定时任务 |
