# API 文档

> 基于 RuoYi 风格的企业级后台管理系统 API 文档，涵盖认证、系统管理、系统监控、系统工具四大模块。

---

## 通用响应格式

所有接口统一返回 JSON 格式：

```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```

| code | 说明 |
|------|------|
| 200  | 成功 |
| 401  | 未登录或 token 已过期 |
| 403  | 权限不足 |
| 429  | 登录频率受限 |
| 500  | 服务器错误 |

## 通用鉴权方式

在请求头中添加 `Authorization: Bearer {token}`，除登录接口外均需鉴权。

---

## 一、认证管理

### POST /api/login - 用户登录

**请求参数 (JSON Body)**

| 参数名   | 类型   | 必填 | 说明   |
|----------|--------|------|--------|
| username | String | 是   | 用户名 |
| password | String | 是   | 密码   |

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "token": "uuid-string"
  }
}
```

**说明**
- 密码使用 BCrypt 加密校验
- 登录失败会记录登录日志并触发限流策略（5次/分钟，超限封锁15分钟）
- 登录成功会生成本地内存 Token 并记录登录日志
- 账号已停用时返回错误

---

### POST /api/logout - 用户退出

**请求头**

| 参数名        | 必填 | 说明                |
|---------------|------|---------------------|
| Authorization | 是   | Bearer {token} 格式 |

**响应**

```json
{ "code": 200, "msg": "success" }
```

**说明**
- 从内存中移除 token，后续请求失效

---

### GET /api/user/info - 获取用户信息

**请求头**

| 参数名        | 必填 | 说明 |
|---------------|------|------|
| Authorization | 是   | Bearer {token} |

**响应**

```json
{
  "code": 200,
  "data": {
    "user": { "id": 1, "username": "admin", "nickname": "管理员", ... },
    "roles": ["admin"],
    "permissions": ["system:user:list", "system:user:create", ...]
  }
}
```

**说明**
- 返回用户基本信息、角色标识列表和权限标识集合
- password 字段会被置空

---

### GET /api/menus/routers - 获取菜单路由

**请求头**

| 参数名        | 必填 | 说明 |
|---------------|------|------|
| Authorization | 是   | Bearer {token} |

**响应**

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
      "menuType": "C",
      "children": null
    }
  ]
}
```

**说明**
- 根据用户角色关联的菜单构建树形结构
- 仅返回目录(M)和菜单(C)类型，不包含按钮(F)

---

## 二、系统管理

### 2.1 用户管理

**基础路径：** `/api/users`
**权限前缀：** `system:user`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/users | system:user:list | keyword (选填, 模糊搜索) | 获取用户列表，password 被置空 |
| 详情查询 | GET | /api/users/{id} | system:user:list | id (路径参数) | 获取单个用户 |
| 新增用户 | POST | /api/users | system:user:create | JSON Body (User 对象) | 密码自动 BCrypt 加密，同步保存角色关联 |
| 修改用户 | PUT | /api/users | system:user:edit | JSON Body (User 对象) | 修改基本信息，密码为空时不修改密码；禁用用户时自动清除其所有 token |
| 删除用户 | DELETE | /api/users/{id} | system:user:delete | id (路径参数) | 同时删除用户-角色关联 |

**User 对象字段**

| 字段名   | 类型       | 说明                       |
|----------|------------|----------------------------|
| id       | Long       | 主键                       |
| username | String     | 用户名                     |
| password | String     | 密码（返回时置空）         |
| nickname | String     | 昵称                       |
| email    | String     | 邮箱                       |
| phone    | String     | 手机号                     |
| avatar   | String     | 头像URL                    |
| status   | Integer    | 0=正常, 1=停用             |
| roleIds  | Long[]     | 关联角色 ID 列表           |
| createTime | LocalDateTime | 创建时间                |

---

### 2.2 角色管理

**基础路径：** `/api/roles`
**权限前缀：** `system:role`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/roles | system:role:list | keyword (选填) | 获取角色列表，包含菜单权限 ID 列表 |
| 详情查询 | GET | /api/roles/{id} | system:role:list | id (路径参数) | 获取单个角色及其菜单权限 |
| 新增角色 | POST | /api/roles | system:role:create | JSON Body (Role 对象) | 同步保存角色-菜单权限关联 |
| 修改角色 | PUT | /api/roles | system:role:edit | JSON Body (Role 对象) | 重建角色-菜单权限关联 |
| 删除角色 | DELETE | /api/roles/{id} | system:role:delete | id (路径参数) | 同时删除角色-菜单关联 |
| 获取菜单ID列表 | GET | /api/roles/menuIds/{id} | system:role:list | id (路径参数) | 返回角色拥有的菜单 ID 数组 |
| 获取所有菜单树 | GET | /api/roles/menus | system:role:list | 无 | 返回所有菜单的完整树形结构 |

**Role 对象字段**

| 字段名    | 类型       | 说明             |
|-----------|------------|------------------|
| id        | Long       | 主键             |
| roleKey   | String     | 角色标识 (如 admin) |
| roleName  | String     | 角色名称         |
| status    | Integer    | 0=正常, 1=停用   |
| menuIds   | Long[]     | 关联菜单 ID 列表 |
| createTime | LocalDateTime | 创建时间       |

---

### 2.3 菜单管理

**基础路径：** `/api/menus`
**权限前缀：** `system:menu`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 菜单树 | GET | /api/menus | system:menu:list | 无 | 返回菜单和目录的树形结构 |
| 全部菜单树 | GET | /api/menus/all | system:menu:list | 无 | 返回全部（含按钮）树形结构 |
| 详情查询 | GET | /api/menus/{id} | system:menu:list | id (路径参数) | 获取单个菜单 |
| 新增菜单 | POST | /api/menus | system:menu:create | JSON Body (Menu 对象) | 创建菜单/目录/按钮 |
| 修改菜单 | PUT | /api/menus | system:menu:edit | JSON Body (Menu 对象) | 修改菜单信息 |
| 删除菜单 | DELETE | /api/menus/{id} | system:menu:delete | id (路径参数) | 同时删除子菜单 |

**Menu 对象字段**

| 字段名    | 类型       | 说明                           |
|-----------|------------|--------------------------------|
| id        | Long       | 主键                           |
| parentId  | Long       | 父菜单 ID (0 为根节点)         |
| name      | String     | 菜单名称                       |
| path      | String     | 路由地址                       |
| component | String     | 组件路径                       |
| icon      | String     | 图标                           |
| perms     | String     | 权限标识                       |
| menuType  | String     | M=目录, C=菜单, F=按钮         |
| sort      | Integer    | 排序号                         |
| visible   | Integer    | 0=显示, 1=隐藏                 |
| status    | Integer    | 0=正常, 1=停用                 |
| children  | Menu[]     | 子菜单列表（树形结构返回时使用）|
| createTime | LocalDateTime | 创建时间                    |

---

### 2.4 部门管理

**基础路径：** `/api/depts`
**权限前缀：** `system:dept`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/depts | system:dept:list | keyword (选填) | 获取部门列表（扁平） |
| 部门树 | GET | /api/depts/tree | system:dept:list | 无 | 返回部门树形结构 |
| 详情查询 | GET | /api/depts/{id} | system:dept:list | id (路径参数) | 获取单个部门 |
| 新增部门 | POST | /api/depts | system:dept:create | JSON Body (Dept 对象) | 创建部门 |
| 修改部门 | PUT | /api/depts | system:dept:edit | JSON Body (Dept 对象) | 修改部门信息 |
| 删除部门 | DELETE | /api/depts/{id} | system:dept:delete | id (路径参数) | 删除部门 |

**Dept 对象字段**

| 字段名    | 类型       | 说明             |
|-----------|------------|------------------|
| id        | Long       | 主键             |
| parentId  | Long       | 父部门 ID        |
| deptName  | String     | 部门名称         |
| orderNum  | Integer    | 排序号           |
| leader    | String     | 负责人           |
| phone     | String     | 联系电话         |
| email     | String     | 邮箱             |
| status    | Integer    | 0=正常, 1=停用   |
| children  | Dept[]     | 子部门列表       |
| createTime | LocalDateTime | 创建时间       |

---

### 2.5 岗位管理

**基础路径：** `/api/posts`
**权限前缀：** `system:post`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/posts | system:post:list | keyword (选填) | 获取岗位列表 |
| 详情查询 | GET | /api/posts/{id} | system:post:list | id (路径参数) | 获取单个岗位 |
| 新增岗位 | POST | /api/posts | system:post:create | JSON Body (Post 对象) | 创建岗位 |
| 修改岗位 | PUT | /api/posts | system:post:edit | JSON Body (Post 对象) | 修改岗位信息 |
| 删除岗位 | DELETE | /api/posts/{id} | system:post:delete | id (路径参数) | 删除岗位 |

**Post 对象字段**

| 字段名    | 类型       | 说明             |
|-----------|------------|------------------|
| id        | Long       | 主键             |
| postCode  | String     | 岗位编码         |
| postName  | String     | 岗位名称         |
| postSort  | Integer    | 排序号           |
| status    | Integer    | 0=正常, 1=停用   |
| createTime | LocalDateTime | 创建时间       |

---

### 2.6 字典类型管理

**基础路径：** `/api/dict-types`
**权限前缀：** `system:dict`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/dict-types | system:dict:list | keyword (选填) | 获取字典类型列表 |
| 详情查询 | GET | /api/dict-types/{id} | system:dict:list | id (路径参数) | 获取单个字典类型 |
| 按类型标识查询 | GET | /api/dict-types/dictType/{dictType} | system:dict:list | dictType (路径参数) | 根据字典类型标识查询 |
| 新增字典类型 | POST | /api/dict-types | system:dict:create | JSON Body (DictType 对象) | 创建字典类型 |
| 修改字典类型 | PUT | /api/dict-types | system:dict:edit | JSON Body (DictType 对象) | 修改字典类型 |
| 删除字典类型 | DELETE | /api/dict-types/{id} | system:dict:delete | id (路径参数) | 删除字典类型 |

**DictType 对象字段**

| 字段名    | 类型       | 说明           |
|-----------|------------|----------------|
| id        | Long       | 主键           |
| dictName  | String     | 字典名称       |
| dictType  | String     | 字典类型标识   |
| status    | Integer    | 0=正常, 1=停用 |
| createTime | LocalDateTime | 创建时间     |

---

### 2.7 字典数据管理

**基础路径：** `/api/dict-data`
**权限前缀：** `system:dict`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/dict-data | system:dict:list | dictTypeId (必填) | 按字典类型 ID 获取字典数据列表 |
| 详情查询 | GET | /api/dict-data/{id} | system:dict:list | id (路径参数) | 获取单个字典数据 |
| 按类型查询 | GET | /api/dict-data/type/{dictType} | system:dict:list | dictType (路径参数) | 根据字典类型标识获取所有数据项 |
| 新增字典数据 | POST | /api/dict-data | system:dict:create | JSON Body (DictData 对象) | 创建字典数据项 |
| 修改字典数据 | PUT | /api/dict-data | system:dict:edit | JSON Body (DictData 对象) | 修改字典数据项 |
| 删除字典数据 | DELETE | /api/dict-data/{id} | system:dict:delete | id (路径参数) | 删除字典数据项 |

**DictData 对象字段**

| 字段名     | 类型       | 说明             |
|------------|------------|------------------|
| id         | Long       | 主键             |
| dictTypeId | Long       | 关联字典类型 ID  |
| dictLabel  | String     | 字典标签         |
| dictValue  | String     | 字典键值         |
| dictSort   | Integer    | 排序号           |
| status     | Integer    | 0=正常, 1=停用   |
| createTime | LocalDateTime | 创建时间       |

---

### 2.8 参数管理

**基础路径：** `/api/configs`
**权限前缀：** `system:config`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/configs | system:config:list | keyword (选填) | 获取参数配置列表 |
| 详情查询 | GET | /api/configs/{id} | system:config:list | id (路径参数) | 获取单个参数配置 |
| 新增参数 | POST | /api/configs | system:config:create | JSON Body (Config 对象) | 创建参数配置 |
| 修改参数 | PUT | /api/configs | system:config:edit | JSON Body (Config 对象) | 修改参数配置 |
| 删除参数 | DELETE | /api/configs/{id} | system:config:delete | id (路径参数) | 删除参数配置 |

**Config 对象字段**

| 字段名      | 类型       | 说明               |
|-------------|------------|--------------------|
| id          | Long       | 主键               |
| configName  | String     | 参数名称           |
| configKey   | String     | 参数键名           |
| configValue | String     | 参数键值           |
| configType  | Integer    | 0=内置, 1=自定义   |
| createTime  | LocalDateTime | 创建时间         |

---

### 2.9 通知公告管理

**基础路径：** `/api/notices`
**权限前缀：** `system:notice`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/notices | system:notice:list | keyword (选填) | 获取通知公告列表 |
| 详情查询 | GET | /api/notices/{id} | system:notice:list | id (路径参数) | 获取单个通知公告 |
| 新增公告 | POST | /api/notices | system:notice:create | JSON Body (Notice 对象) | 创建通知公告 |
| 修改公告 | PUT | /api/notices | system:notice:edit | JSON Body (Notice 对象) | 修改通知公告 |
| 删除公告 | DELETE | /api/notices/{id} | system:notice:delete | id (路径参数) | 删除通知公告 |

**Notice 对象字段**

| 字段名        | 类型       | 说明                     |
|---------------|------------|--------------------------|
| id            | Long       | 主键                     |
| noticeTitle   | String     | 公告标题                 |
| noticeType    | Integer    | 1=通知, 2=公告           |
| noticeContent | String     | 公告内容（TEXT 类型）    |
| status        | Integer    | 0=正常, 1=关闭           |
| createTime    | LocalDateTime | 创建时间               |

---

## 三、系统监控

### 3.1 操作日志

**基础路径：** `/api/oper-logs`
**权限前缀：** `monitor:operlog`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/oper-logs | monitor:operlog:list | operName(选填), businessType(选填), status(选填) | 支持按操作人员、业务类型、状态筛选 |
| 删除日志 | DELETE | /api/oper-logs/{id} | monitor:operlog:delete | id (路径参数) | 删除单条日志 |
| 清除日志 | DELETE | /api/oper-logs/clear | monitor:operlog:delete | 无 | 清空所有操作日志 |

**说明**
- 操作日志通过 AOP 切面（LogAspect）自动记录所有 Controller 请求
- 登录、退出、信息查询相关的请求不记录
- businessType: 0=其它, 1=新增, 2=修改, 3=删除
- status: 0=正常, 1=异常

**OperLog 对象字段**

| 字段名        | 类型       | 说明                 |
|---------------|------------|----------------------|
| id            | Long       | 主键                 |
| title         | String     | 操作模块             |
| businessType  | Integer    | 业务类型             |
| method        | String     | 方法名               |
| requestMethod | String     | 请求方式（GET/POST） |
| operName      | String     | 操作人员             |
| operUrl       | String     | 请求URL              |
| operIp        | String     | 操作IP               |
| operParam     | String     | 请求参数             |
| jsonResult    | String     | 返回结果             |
| status        | Integer    | 操作状态             |
| errorMsg      | String     | 错误信息             |
| operTime      | LocalDateTime | 操作时间           |

---

### 3.2 登录日志

**基础路径：** `/api/login-logs`
**权限前缀：** `monitor:loginlog`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/login-logs | monitor:loginlog:list | userName(选填), status(选填) | 支持按用户名和状态筛选 |
| 删除日志 | DELETE | /api/login-logs/{id} | monitor:loginlog:delete | id (路径参数) | 删除单条日志 |
| 清除日志 | DELETE | /api/login-logs/clear | monitor:loginlog:delete | 无 | 清空所有登录日志 |

**LoginLog 对象字段**

| 字段名    | 类型       | 说明             |
|-----------|------------|------------------|
| id        | Long       | 主键             |
| userName  | String     | 用户名           |
| status    | Integer    | 0=成功, 1=失败   |
| ipAddr    | String     | 登录 IP          |
| msg       | String     | 提示信息         |
| loginTime | LocalDateTime | 登录时间       |

---

### 3.3 在线用户

**基础路径：** `/api/online-users`
**权限前缀：** `monitor:online`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/online-users | monitor:online:list | 无 | 获取当前所有在线用户（基于内存 token） |
| 强制退出 | DELETE | /api/online-users/{token} | monitor:online:forceLogout | token (路径参数) | 强制用户下线，删除其 token |

**说明**
- 在线用户数据来自 DataStore 中的 tokenMap（ConcurrentHashMap 内存缓存）

---

### 3.4 定时任务

**基础路径：** `/api/jobs`
**权限前缀：** `monitor:job`

| 接口 | 方法 | 路径 | 权限 | 参数 | 说明 |
|------|------|------|------|------|------|
| 列表查询 | GET | /api/jobs | monitor:job:list | keyword (选填) | 获取定时任务列表 |
| 详情查询 | GET | /api/jobs/{id} | monitor:job:list | id (路径参数) | 获取单个任务 |
| 新增任务 | POST | /api/jobs | monitor:job:create | JSON Body (Job 对象) | 创建定时任务 |
| 修改任务 | PUT | /api/jobs | monitor:job:edit | JSON Body (Job 对象) | 修改定时任务 |
| 删除任务 | DELETE | /api/jobs/{id} | monitor:job:delete | id (路径参数) | 删除定时任务 |

**Job 对象字段**

| 字段名        | 类型       | 说明             |
|---------------|------------|------------------|
| id            | Long       | 主键             |
| jobName       | String     | 任务名称         |
| jobGroup      | String     | 任务组名         |
| invokeTarget  | String     | 调用目标         |
| cronExpression | String    | Cron 表达式      |
| status        | Integer    | 0=正常, 1=暂停   |
| createTime    | LocalDateTime | 创建时间       |

---

### 3.5 服务监控

**基础路径：** `/api/monitor`

| 接口 | 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|------|
| 服务器信息 | GET | /api/monitor/server | 无 | 获取操作系统和 JVM 信息 |
| 缓存信息 | GET | /api/monitor/cache | 无 | 获取内存缓存状态（token 数等） |

**/api/monitor/server 响应**

```json
{
  "code": 200,
  "data": {
    "sys": {
      "osName": "Windows 10",
      "osArch": "amd64",
      "osVersion": "10.0",
      "cpuCores": 8
    },
    "jvm": {
      "totalMemory": "512MB",
      "freeMemory": "256MB",
      "maxMemory": "1024MB",
      "javaVersion": "1.8.0_301",
      "javaHome": "C:\\Java\\jdk1.8"
    }
  }
}
```

**/api/monitor/cache 响应**

```json
{
  "code": 200,
  "data": {
    "tokenCount": 3,
    "userSessionCount": 3,
    "cacheType": "ConcurrentHashMap (内存缓存)"
  }
}
```

---

## 四、系统工具

### 4.1 代码生成

**基础路径：** `/api/gen`

| 接口 | 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|------|
| 获取数据库表 | GET | /api/gen/tables | 无 | 获取所有 sys_ 前缀的数据表名和注释 |
| 获取表字段 | GET | /api/gen/tables/{tableName} | tableName (路径参数) | 获取指定表的列信息 |
| 生成代码 | POST | /api/gen/generate | JSON Body (参数) | 根据表生成完整代码，返回 ZIP 下载 |

**POST /api/gen/generate 请求参数**

| 参数名      | 类型   | 必填 | 说明                 | 默认值            |
|-------------|--------|------|----------------------|-------------------|
| tableName   | String | 是   | 数据库表名           | -                 |
| packageName | String | 否   | 生成代码的包名       | com.example.admin |
| moduleName  | String | 否   | 模块名（权限标识用） | system            |
| author      | String | 否   | 代码作者             | admin             |

**生成的文件列表**

- Entity.java - 实体类
- Mapper.java - MyBatis Mapper 接口
- Mapper.xml - MyBatis XML 映射
- Service.java - Service 业务层
- Controller.java - REST Controller
- api/{module}.js - 前端 API 封装
- vue/{module}/index.vue - 前端页面组件

---

## 五、通用错误响应

### 401 未登录

```json
{ "code": 401, "msg": "未登录或token已过期" }
```

### 403 权限不足

```json
{ "code": 403, "msg": "权限不足" }
```

### 429 登录频率限制

```json
{ "code": 429, "msg": "登录尝试过于频繁，请15分钟后再试" }
```

### 500 业务错误

```json
{ "code": 500, "msg": "具体的错误提示信息" }
```
