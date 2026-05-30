# Ds-Ai 多模块AI聊天平台

> 基于 [RuoYi](https://gitee.com/y_project/RuoYi-Vue) 框架的全新AI聊天平台，集成多模型AI对话、RAG增强检索、RBAC权限管理，前后端完全分离。

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://gitee.com/xp_s/vibe_coding)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D.svg)](https://vuejs.org/)
[![JDK](https://img.shields.io/badge/JDK-1.8-orange.svg)](https://www.oracle.com/java/)

- **在线演示**：`http://localhost:5174`（AI用户端）/ `http://localhost:5173`（管理后台）
- **在线文档**：[API 文档](./spring-boot-admin/API.md) · [架构文档](./spring-boot-admin/ARCHITECTURE.md) · [技术文档](./项目技术文档.md)
- **源码地址（后端）**：https://gitee.com/xp_s/vibe_coding
- **若依框架**：https://gitee.com/y_project/RuoYi-Vue

---

## 平台简介

**Ds-Ai** 是一套全部开源的AI聊天平台，基于 **Spring Boot + Vue 前后端分离** 架构，在经典的 RuoYi 权限管理系统之上深度扩展了AI对话能力。项目毫无保留给个人及企业免费使用。

| 子项目 | 说明 | 技术栈 | 端口 |
|--------|------|--------|------|
| **spring-boot-admin** | 后端服务（API + 管理后台） | Spring Boot 2.7.18 + MyBatis + Redis + MySQL 8.0 | `8090` |
| **ruoyi-ai-web** | AI 用户端（面向普通用户） | Vue 3.5 + Element Plus + Pinia + Vite | `5174` |
| **vue-admin** | 管理后台（面向管理员） | Vue 2.7 + Element UI + Vuex + Vite | `5173` |

### 核心特色

- 🤖 **多模型AI对话**：支持 DeepSeek / OpenAI / 通义千问 / 智谱 / Moonshot，统一 OpenAI 兼容协议接入
- 📡 **SSE 流式输出**：实时打字效果，支持思考过程展示（DeepSeek-R1 等推理模型）
- 🔍 **RAG 增强检索**：基于向量检索 + 关键字 + 结构化匹配的混合评分，可接入知识库
- 🌐 **联网搜索**：内置搜狗网页搜索，实时获取最新信息作为AI上下文
- 📄 **文件上传解析**：支持 PDF / Word / Excel / PPT / TXT，内容自动注入对话
- 🎭 **助手模板**：预设角色 Prompt，按通用/编程/写作/数据分析分类，一键切换
- ⭐ **收藏与分享**：对话收藏到分组，一键生成分享链接
- 🌍 **国际化**：支持中英文切换，错误码双语
- 🔐 **安全可靠**：JWT认证 + RBAC权限 + XSS防护 + 登录限流

---

## 内置功能

### 一、系统管理

| 序号 | 功能 | 说明 |
|------|------|------|
| 1 | 用户管理 | 系统操作者配置，支持用户新增/编辑/删除/导出 |
| 2 | 部门管理 | 组织机构配置（公司、部门、小组），树结构展现，支持数据权限 |
| 3 | 岗位管理 | 配置系统用户所属担任职务 |
| 4 | 菜单管理 | 配置系统菜单、操作权限、按钮权限标识等 |
| 5 | 角色管理 | 角色菜单权限分配，按机构进行数据范围权限划分 |
| 6 | 字典管理 | 对系统中经常使用的固定数据进行维护 |
| 7 | 参数管理 | 对系统动态配置常用参数 |
| 8 | 通知公告 | 系统通知公告信息发布维护 |

### 二、系统监控

| 序号 | 功能 | 说明 |
|------|------|------|
| 9 | 操作日志 | 系统正常操作日志记录和查询，异常信息日志记录和查询 |
| 10 | 登录日志 | 系统登录日志记录查询，包含登录异常 |
| 11 | 在线用户 | 当前系统中活跃用户状态监控 |
| 12 | 定时任务 | 在线添加/修改/删除任务调度，包含执行结果日志 |
| 13 | 服务监控 | 监视系统 CPU、内存、磁盘、堆栈等相关信息 |
| 14 | 缓存监控 | 对系统缓存信息查询、命令统计等 |

### 三、系统工具

| 序号 | 功能 | 说明 |
|------|------|------|
| 15 | 代码生成 | 前后端代码生成（Java、HTML、XML、SQL），支持 CRUD 打包下载 |
| 16 | 系统接口 | 根据业务代码自动生成相关 API 接口文档（Swagger / Knife4j） |
| 17 | 在线构建器 | 拖动表单元素生成相应的 HTML/Vue 代码 |

### 四、AI 功能

| 序号 | 功能 | 说明 |
|------|------|------|
| 18 | AI 对话 | 支持多模型的 SSE 流式对话，实时打字效果 |
| 19 | 多模型切换 | 策略模式支持 DeepSeek / OpenAI / 通义千问 / 智谱 / Moonshot |
| 20 | 会话管理 | 对话历史保存、搜索、批量删除 |
| 21 | 助手模板 | 预设角色 Prompt，按分类组织（通用/编程/写作/数据分析） |
| 22 | 模型管理 | 管理员配置模型参数、启用/禁用、API 密钥管理 |
| 23 | 知识库 | 关联知识库到对话中，增强 AI 回答质量 |
| 24 | 文档上传 | 上传 PDF/Word/Excel/PPT/TXT，内容自动注入对话上下文 |
| 25 | 联网搜索 | 实时搜狗网页搜索，结果注入 AI 上下文 |
| 26 | RAG 增强检索 | 向量相似度 + 关键字 + 结构化匹配的混合检索 |
| 27 | 对话收藏 | 收藏对话到自定义分组 |
| 28 | 对话分享 | 一键生成分享链接，支持外部查看 |
| 29 | 消息评价 | 点赞/点踩消息，收集用户反馈 |
| 30 | 站点配置 | 网站名称/Logo/版权等全局配置 |

---

## 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 1.8 | 运行时环境 |
| Spring Boot | 2.7.18 | 应用框架 |
| Spring Security | 5.7.x | 认证授权 |
| Spring AOP | — | 操作日志切面 |
| MyBatis | 2.3.2 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | ≥ 3.0 | 缓存与会话 |
| HikariCP | — | 数据库连接池 |
| Knife4j (Swagger) | 4.3.0 | API 文档 |
| OkHttp | 4.12.0 | AI 模型 API 调用 |
| Apache Tika | 2.9.2 | 文档解析（PDF/Word/Excel/PPT/TXT） |
| Jsoup | 1.15.4 | 网页抓取（搜索引擎） |
| Maven | ≥ 3.0 | 构建工具 |

### 前端（AI 用户端）

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.13 | 前端框架（Composition API） |
| Element Plus | 2.9.1 | UI 组件库 |
| Pinia | 2.3.0 | 状态管理 |
| Vue Router | 4.5.0 | 路由管理 |
| Axios | 1.7.9 | HTTP 客户端 |
| Vite | 5.4.19 | 构建工具 |
| marked | 14.1.4 | Markdown 渲染 |
| highlight.js | 11.11.1 | 代码语法高亮 |
| KaTeX | 0.16.47 | 数学公式渲染 |
| vue-i18n | ^10.0.7 | 国际化 |

### 前端（管理后台）

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 2.7.16 | 前端框架（Options API） |
| Element UI | 2.15.14 | UI 组件库 |
| Vuex | 3.6.2 | 状态管理 |
| Vue Router | 3.6.5 | 路由管理 |
| Axios | 1.7.2 | HTTP 客户端 |
| Vite | 5.4.19 | 构建工具 |

---

## 系统需求

| 环境 | 版本要求 |
|------|----------|
| JDK | ≥ 1.8 |
| MySQL | ≥ 5.7（推荐 8.0） |
| Redis | ≥ 3.0 |
| Maven | ≥ 3.0 |
| Node.js | ≥ 16（推荐 18+） |

---

## 快速开始

### 1. 环境准备

- 安装 JDK 1.8+ 并配置环境变量
- 安装 MySQL 5.7+ 并创建数据库 `ai_test`
- 安装 Redis 并启动（默认端口 6379）
- 安装 Maven 3.0+
- 安装 Node.js 16+

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ai_test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"

# 导入初始化脚本（项目首次启动时自动执行 schema.sql 和 data.sql）
# 或在 application.yml 中设置 spring.sql.init.mode=always
```

### 3. 启动后端

```bash
cd spring-boot-admin

# 编译打包
mvn clean package -DskipTests

# 启动
java -jar target/spring-boot-admin-1.0.0.jar
```

后端启动后访问：
- API 地址：`http://localhost:8090`
- Swagger UI：`http://localhost:8090/doc.html`

### 4. 启动前端

```bash
# 启动 AI 用户端（端口 5174）
cd ruoyi-ai-web
npm install
npm run dev

# 启动管理后台（端口 5173）
cd vue-admin
npm install
npm run dev
```

### 5. 开始使用

| 入口 | 地址 | 账号 | 密码 |
|------|------|------|------|
| AI 用户端 | `http://localhost:5174` | `admin` | `admin123` |
| 管理后台 | `http://localhost:5173` | `admin` | `admin123` |

> 普通用户可使用 `user` / `user123` 或 `test` / `test123` 登录。

---

## 项目结构

```
IdeaProjects/
├── spring-boot-admin/          # 后端服务
│   ├── src/main/java/com/example/admin/
│   │   ├── config/             # Security、CORS、Swagger 等配置
│   │   ├── controller/         # REST 控制器（31个）
│   │   ├── service/            # 业务逻辑层
│   │   │   └── model/          # AI 模型提供商（策略模式）
│   │   ├── mapper/             # MyBatis Mapper 接口（20个）
│   │   ├── entity/             # 实体类（22个）
│   │   ├── rag/                # RAG 检索引擎
│   │   │   └── vectorstore/    # 向量存储（内存/PgVector）
│   │   ├── aspect/             # AOP 切面（操作日志）
│   │   └── store/              # 内存数据存储
│   ├── src/main/resources/
│   │   ├── mapper/             # MyBatis XML（28个）
│   │   ├── application.yml     # 主配置文件
│   │   ├── schema.sql          # 数据库建表脚本
│   │   └── data.sql            # 初始数据脚本
│   └── pom.xml
├── ruoyi-ai-web/               # AI 用户端前端
│   ├── src/
│   │   ├── components/         # Vue 组件（17个）
│   │   ├── stores/             # Pinia 状态管理（8个）
│   │   ├── composables/        # 组合式函数（4个）
│   │   ├── views/              # 页面视图
│   │   └── api/                # API 请求模块（9个）
│   └── package.json
├── vue-admin/                  # 管理后台前端
│   ├── src/
│   │   ├── views/              # 业务页面
│   │   ├── components/         # 通用组件
│   │   └── api/                # API 请求模块
│   └── package.json
├── 项目技术文档.md              # 详细技术文档
└── README.md                   # 本文件
```

---

## AI 模型配置

项目支持以下 AI 模型提供商，通过策略模式统一接入：

| 提供商 | 配置方式 | API 地址 | 默认模型 |
|--------|----------|----------|----------|
| **DeepSeek** | 硬编码配置 | `https://api.deepseek.com` | `deepseek-chat` |
| **OpenAI** | 环境变量 `OPENAI_API_KEY` | `https://api.openai.com` | — |
| **通义千问** | 环境变量 `QWEN_API_KEY` | `https://dashscope.aliyuncs.com/compatible-mode` | — |
| **智谱** | 环境变量 `ZHIPU_API_KEY` | `https://open.bigmodel.cn/api/paas` | — |
| **Moonshot** | 环境变量 `MOONSHOT_API_KEY` | `https://api.moonshot.cn` | — |

> 管理后台提供模型管理界面，可动态配置模型参数（Temperature、Max Tokens 等）和启停状态。

---

## RAG 检索增强

项目内置 RAG 检索引擎，支持两种向量存储方案：

| 方案 | 适用场景 | 配置 |
|------|----------|------|
| **SimpleVectorStore** | 开发环境，内存存储 + JSON 持久化 | `application-dev.yml` 默认 |
| **PgVectorStore** | 生产环境，PostgreSQL + pgvector 扩展 | `application-prod.yml` 配置 |

混合评分策略：**40% 向量相似度 + 40% 关键字匹配 + 20% 结构化字段匹配**

---

## 安全特性

- 🔑 **JWT 认证**：基于 Bearer Token 的无状态认证
- 🛡️ **RBAC 权限**：用户 → 角色 → 菜单/按钮 三级权限模型
- 🔒 **BCrypt 加密**：密码哈希存储，不可逆
- 🚫 **XSS 防护**：请求参数过滤，防止脚本注入
- ⏱️ **登录限流**：单 IP 每分钟最多 5 次尝试，超限锁定 15 分钟
- 📝 **操作审计**：AOP 自动记录全部操作日志
- 🌐 **CORS 安全**：跨域请求白名单控制

---

## 内置账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 超级管理员 | `admin` | `admin123` | 拥有全部权限 |
| 普通用户 | `user` | `user123` | 受限权限 |
| 测试用户 | `test` | `test123` | 测试用途 |

---

## 相关文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 项目技术文档 | [`./项目技术文档.md`](./项目技术文档.md) | 架构设计、数据库设计、部署运维全覆盖 |
| API 文档 | [`./spring-boot-admin/API.md`](./spring-boot-admin/API.md) | 全部 30+ API 接口详细说明 |
| 架构文档 | [`./spring-boot-admin/ARCHITECTURE.md`](./spring-boot-admin/ARCHITECTURE.md) | 前后端架构、认证流程、数据流 |
| Swagger UI | `http://localhost:8090/doc.html` | 在线 API 调试 |

---

## 贡献与支持

- 若依框架：[RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue)
- 源码仓库：[Gitee](https://gitee.com/xp_s/vibe_coding)
- 问题反馈：欢迎提交 Issue 和 PR

---

## 开源协议

本项目基于 [MIT](https://opensource.org/licenses/MIT) 协议开源，完全免费。

---

> 🔔 项目正在持续迭代中，欢迎 Star & Fork！
