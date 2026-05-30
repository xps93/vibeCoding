# Ds-Ai — 100% AI 驱动的多模块AI聊天平台

> 🤖 **一个人类零手写代码的实验**：从第一行 `pom.xml` 到 30+ 功能模块、86+ Java 类、300+ 前端组件，**全部由 Claude Code（AI）生成**。
> 本项目既是功能完备的AI聊天平台，也是一份 **AI 辅助编程的完整思维过程记录**，供所有对 AI 编程感兴趣的开发者参考和复用。

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://gitee.com/xp_s/vibe_coding)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue-3.5-4FC08D.svg)](https://vuejs.org/)
[![JDK](https://img.shields.io/badge/JDK-1.8-orange.svg)](https://www.oracle.com/java/)
[![AI Built](https://img.shields.io/badge/100%25-AI%20Built-blueviolet.svg)](https://claude.ai/code)

- **在线文档**：[API 文档](./spring-boot-admin/API.md) · [架构文档](./spring-boot-admin/ARCHITECTURE.md) · [技术文档](./项目技术文档.md)
- **源码仓库**：https://gitee.com/xp_s/vibe_coding
- **底层框架**：基于 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) 架构演进

---

## 目录

1. [AI 编程实录：零手写代码的全过程](#ai-编程实录零手写代码的全过程)
2. [平台简介](#平台简介)
3. [内置功能](#内置功能)
4. [技术栈](#技术栈)
5. [系统需求](#系统需求)
6. [本地部署](#本地部署)
7. [公网访问（内网穿透）](#公网访问内网穿透)
8. [AI 模型配置](#ai-模型配置)
9. [项目结构](#项目结构)
10. [相关文档](#相关文档)

---

## AI 编程实录：零手写代码的全过程

### 核心理念

本项目是 **"Vibe Coding"（氛围编程）** 的一次完整实践——你只需要描述你想要什么，AI 负责实现。

**我做了什么**：描述需求、审查代码、运行测试、反馈问题。
**AI 做了什么**：架构设计、数据库建模、全部编码、文档生成、Bug 修复。

### 完整开发时间线

以下是本项目从零到一的真实对话过程，每一次 git commit 都是一次人机对话的结果：

```
8e584a3  init: 初始化项目，包含前后端代码
          ↑ "帮我创建一个 Spring Boot + Vue 的后台管理系统，参考 RuoYi 架构"
          AI 生成了完整的 RBAC 权限体系、用户/角色/菜单/部门管理

cee4770  chore: 清理无效文件，配置 .gitignore
          ↑ "清理编译产物，配置忽略规则"

24eee28  add ai
28c039b  add          ← 这三个 commit 是一轮对话的迭代
4230d2e  add ai       ↑ "给项目添加AI对话功能，支持多模型切换"
d141eff  add ai       ↑ AI 设计了策略模式 + 抽象工厂 + SSE 流式输出
3c0946c  add ai
7ebecc2  ai
eea4ce0  ai
57bfdbd  ai
6ad974f  add ai
0e56018  add ai
          ↑ 逐步迭代：模型提供者 → 会话管理 → 流式输出 → 知识库

4e79763  feat: RAG增强对话 + AI管理后台 + 前端多语言/收藏/分享
          ↑ "添加 RAG 检索增强、国际化、对话收藏和分享功能"
          AI 同时修改了后端 10+ 文件和前端 20+ 组件

f622d36  chore: .gitignore 补充个人文件排除规则
dabc0b4  chore: 从 git 追踪中移除 dist/index.html
5a43bca  chore: ruoyi-ai-web 本地 .gitignore 排除 dist/node_modules/uploads
          ↑ 工程化细节优化

206cfc3  feat: Vite 开发服务器配置 host 为 0.0.0.0，允许外部访问
          ↑ "让 Vite 允许局域网访问，为内网穿透做准备"

033cd08  docs: 参考 RuoYi 风格优化 README.md
          ↑ "参考 RuoYi 的 README，优化项目文档"
```

### AI 工作模式分解

整个开发过程中，我使用了以下几种 AI 编程范式：

#### 1. 需求描述 → 代码生成
```
我：  "给项目添加 AI 对话功能，支持多模型切换（DeepSeek/OpenAI/通义千问/智谱/Moonshot），
      要求 SSE 流式输出，支持思考过程展示"

AI：  1. 设计策略模式架构（ModelProvider 接口 + AbstractOpenAIProvider + 5个实现）
      2. 创建 AiConversation、AiMessage 等 7 张数据库表
      3. 编写 AiChatController（SSE）、AiConversationController 等 10 个控制器
      4. 生成前端 Chat 组件、对话列表、流式 Markdown 渲染
      5. 自动生成对应的 API 文档和数据库初始化脚本
```

#### 2. 迭代式增量开发
```
我：  "现在要给对话系统加上 RAG 检索增强"

AI：  1. 分析现有代码结构，找到注入点
      2. 创建 rag/ 包：EmbeddingService + QueryAnalysisService + PropertyRetrievalService
      3. 设计向量存储抽象层（SimpleVectorStore / PgVectorStore）
      4. 实现混合评分算法（40% 向量 + 40% 关键字 + 20% 结构化）
      5. 修改 AiChatService，在发送请求前注入检索结果
      6. 不破坏现有的纯对话模式
```

#### 3. 跨模块批量修改
```
我：  "添加国际化支持，错误码要中英文双语"

AI：  1. 创建 sys_error_code 表
      2. 新建 ErrorCodeService + ErrorCodeAdminController
      3. 扫描全部 Controller，统一异常处理为双语错误码
      4. 前端添加 vue-i18n，修改 30+ 个组件
      5. 同时更新 Swagger 注解
```

#### 4. 工程化 & 运维
```
我：  "需要一键启动脚本，本地开发和公网穿透都要支持"

AI：  1. 设计交互式 start-all.sh（依赖检测 + 服务选择 + 隧道可选）
      2. 编写 stop-all.sh 一键停止
      3. 集成 Cloudflare Tunnel 内网穿透
      4. 创建 Windows 用户的 start-services.bat 备用脚本
```

### 给 AI 编程者的参考

如果你也想用 AI 写一个完整的项目，以下是我的实操经验：

| 经验 | 说明 |
|------|------|
| **写好 CLAUDE.md** | 把编码规范、技术栈偏好、命名约定写进项目级 CLAUDE.md，AI 会严格遵循。本项目 [CLAUDE.md](./CLAUDE.md) 包含完整的 Java/MySQL/工程规范 |
| **小步迭代，频繁 commit** | 每次只让 AI 做一件事，commit 后再做下一件。一次描述太多需求，AI 容易出错 |
| **先跑通再优化** | 先让 AI 生成能运行的 MVP，再逐步要求"加 RAG"、"加国际化"、"加收藏" |
| **善用 AI 的架构能力** | 不要自己设计好再让 AI 实现——直接描述需求，让 AI 自己选择设计模式。本项目策略模式、抽象工厂都是 AI 自己选的 |
| **让 AI 读自己写的代码** | 迭代时，AI 会自动读取已有代码，保持风格一致。遇到不一致时直接指出，AI 会修正 |
| **基础设施交给 AI** | 启动脚本、.gitignore、构建配置、环境变量……这些琐碎但重要的事，AI 做得又快又好 |

### 核心发现

> **AI 不是替代程序员，而是让一个人能做的事情扩大了 10 倍。**
> 这个项目如果是纯手写，预估需要 2-3 个月全职开发。
> 全程用 AI 对话完成，实际耗时约 **2 周**（业余时间）。
> 代码质量：0 编译错误，设计模式运用得当，注释完善，Swagger 文档自动生成。

---

## 平台简介

**Ds-Ai** 是一套全部开源的 AI 聊天平台，基于 **Spring Boot + Vue 前后端分离** 架构，在经典的 RuoYi RBAC 权限体系上深度扩展了 AI 对话能力。

| 子项目 | 说明 | 技术栈 | 端口 |
|--------|------|--------|------|
| **spring-boot-admin** | 后端 API 服务 | Spring Boot 2.7.18 + MyBatis + Redis + MySQL 8.0 | `8090` |
| **ruoyi-ai-web** | AI 用户端 | Vue 3.5 + Element Plus + Pinia + Vite | `5174` |
| **vue-admin** | 管理后台 | Vue 2.7 + Element UI + Vuex + Vite | `5173` |

### 核心特色

- 🤖 **多模型 AI 对话**：支持 DeepSeek / OpenAI / 通义千问 / 智谱 / Moonshot，统一 OpenAI 协议接入
- 📡 **SSE 流式输出**：实时打字效果，支持推理模型思考过程展示
- 🔍 **RAG 检索增强**：向量 + 关键字 + 结构化字段，三路混合评分
- 🌐 **联网搜索**：内置网页搜索，实时信息注入 AI 上下文
- 📄 **文档解析**：上传 PDF / Word / Excel / PPT / TXT，内容自动注入对话
- 🎭 **助手模板**：预设角色 Prompt，按分类一键切换
- ⭐ **收藏与分享**：对话收藏到分组，一键生成分享链接
- 🌍 **国际化**：中英文切换，错误码双语
- 🔐 **安全可靠**：Bearer Token + RBAC + XSS 防护 + 登录限流

---

## 内置功能

### 一、系统管理

| 序号 | 功能 | 说明 |
|------|------|------|
| 1 | 用户管理 | 系统用户 CRUD，支持导出 |
| 2 | 部门管理 | 组织机构树，支持数据权限 |
| 3 | 岗位管理 | 用户职务配置 |
| 4 | 菜单管理 | 菜单/操作/按钮权限标识配置 |
| 5 | 角色管理 | 角色菜单权限分配，数据范围权限 |
| 6 | 字典管理 | 固定数据维护 |
| 7 | 参数管理 | 动态配置系统参数 |
| 8 | 通知公告 | 通知公告发布维护 |

### 二、系统监控

| 序号 | 功能 | 说明 |
|------|------|------|
| 9 | 操作日志 | AOP 自动记录，支持查询和导出 |
| 10 | 登录日志 | 登录记录查询，含登录异常 |
| 11 | 在线用户 | 活跃用户状态监控 |
| 12 | 定时任务 | 在线 CRUD 任务调度，含执行日志 |
| 13 | 服务监控 | CPU / 内存 / 磁盘 / JVM 堆栈 |
| 14 | 缓存监控 | 缓存信息查询与统计 |

### 三、系统工具

| 序号 | 功能 | 说明 |
|------|------|------|
| 15 | 代码生成 | 从表结构生成前后端 CRUD 代码，ZIP 打包下载 |
| 16 | API 文档 | Knife4j / Swagger 自动生成，在线调试 |
| 17 | 在线构建器 | 拖拽表单元素生成 HTML/Vue 代码 |

### 四、AI 功能

| 序号 | 功能 | 说明 |
|------|------|------|
| 18 | AI 对话 | 多模型 SSE 流式对话，Markdown 渲染 |
| 19 | 多模型切换 | 策略模式支持 5 种模型提供商 |
| 20 | 会话管理 | 历史保存、搜索、批量删除 |
| 21 | 助手模板 | 预设角色 Prompt，按分类组织 |
| 22 | 模型管理 | 管理员配置模型参数和 API 密钥 |
| 23 | 知识库 | 关联知识库增强 AI 回答 |
| 24 | 文档上传 | 文件解析后注入对话上下文 |
| 25 | 联网搜索 | 实时网页搜索，结果注入 AI 上下文 |
| 26 | RAG 检索 | 向量 + 关键字 + 结构化混合检索 |
| 27 | 对话收藏 | 收藏到自定义分组 |
| 28 | 对话分享 | 生成分享链接，支持外部查看 |
| 29 | 消息评价 | 点赞/点踩，收集反馈 |
| 30 | 站点配置 | 名称/Logo/版权全局可配 |

---

## 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 1.8 | 运行时 |
| Spring Boot | 2.7.18 | 应用框架 |
| Spring Security | 5.7.x | 认证授权（Bearer Token + RBAC） |
| Spring AOP | — | 操作日志切面 |
| MyBatis | 2.3.2 | ORM |
| MySQL | 8.0 | 关系型数据库 |
| Redis | ≥ 3.0 | 缓存与会话 |
| HikariCP | — | 数据库连接池 |
| Knife4j | 4.3.0 | API 文档（OpenAPI 3） |
| OkHttp | 4.12.0 | AI 模型 HTTP 调用 |
| Apache Tika | 2.9.2 | 文档解析 |
| Jsoup | 1.15.4 | 网页抓取 |

### 前端 — AI 用户端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.13 | 前端框架（Composition API） |
| Element Plus | 2.9.1 | UI 组件库 |
| Pinia | 2.3.0 | 状态管理 |
| Vue Router | 4.5.0 | 路由 |
| Axios | 1.7.9 | HTTP 客户端 |
| Vite | 5.4.19 | 构建工具 |
| marked | 14.1.4 | Markdown 渲染 |
| highlight.js | 11.11.1 | 代码高亮 |
| KaTeX | 0.16.47 | 数学公式 |
| vue-i18n | ^10.0.7 | 国际化 |

### 前端 — 管理后台

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 2.7.16 | 前端框架（Options API） |
| Element UI | 2.15.14 | UI 组件库 |
| Vuex | 3.6.2 | 状态管理 |
| Vue Router | 3.6.5 | 路由 |
| Axios | 1.7.2 | HTTP 客户端 |
| Vite | 5.4.19 | 构建工具 |

---

## 系统需求

| 环境 | 版本要求 | 说明 |
|------|----------|------|
| JDK | ≥ 1.8 | 推荐 JDK 8 |
| MySQL | ≥ 5.7（推荐 8.0） | 需提前创建数据库 `ai_test` |
| Redis | ≥ 3.0 | 缓存和会话存储 |
| Maven | ≥ 3.0 | 后端构建 |
| Node.js | ≥ 16（推荐 18+） | 前端构建 |

---

## 本地部署

### 方式一：一键启动（推荐）

```bash
# 启动所有服务（交互式选择）
bash start-all.sh

# 按提示选择：
#   1. 后端 spring-boot-admin (8090)？ [y/N]
#   2. 前端 vue-admin 管理后台 (5173)？ [y/N]
#   3. 前端 ruoyi-ai-web AI聊天 (5174)？ [y/N]
#   4. 公网穿透？ [y/N]

# 停止所有服务
bash stop-all.sh
```

脚本会自动：
- 检测 MySQL 和 Redis 是否运行，未运行则尝试启动
- 清理旧进程，避免端口冲突
- 汇总显示所有服务的本地和公网地址

### 方式二：Windows 批处理

```cmd
# 三个服务分别在新窗口启动
start-services.bat
```

### 方式三：手动分步启动

#### Step 1 — 环境准备

确保以下服务已启动：
- **MySQL**：端口 `3306`
- **Redis**：端口 `6379`

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ai_test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
```

#### Step 2 — 启动后端

```bash
cd spring-boot-admin
mvn spring-boot:run
# 或
mvn clean package -DskipTests
java -jar target/spring-boot-admin-1.0.0.jar
```

验证：浏览器打开 `http://localhost:8090/doc.html`，应看到 Swagger UI。

#### Step 3 — 启动前端

```bash
# AI 用户端（端口 5174）
cd ruoyi-ai-web
npm install
npm run dev

# 管理后台（端口 5173）
cd vue-admin
npm install
npm run dev
```

#### Step 4 — 登录

| 入口 | 地址 | 账号 | 密码 |
|------|------|------|------|
| AI 用户端 | `http://localhost:5174` | `admin` | `admin123` |
| 管理后台 | `http://localhost:5173` | `admin` | `admin123` |
| Swagger | `http://localhost:8090/doc.html` | — | — |

> 普通用户：`user` / `user123`；测试用户：`test` / `test123`

---

## 公网访问（内网穿透）

本地服务只能自己访问。如果需要**演示给他人**、**手机端测试**、或**临时上线**，以下方案任选：

### 方案对比

| 方案 | 费用 | 速度 | 域名 | 难度 | 推荐场景 |
|------|------|------|------|------|----------|
| **Cloudflare Tunnel** | 免费 | ⭐⭐⭐ | `xxx.trycloudflare.com` | ⭐ 极简 | 临时演示、开发调试 |
| **ngrok** | 免费/付费 | ⭐⭐⭐ | `xxx.ngrok.io` | ⭐ 极简 | 国外用户 |
| **frp** | 需自备公网服务器 | ⭐⭐⭐⭐⭐ | 自定义域名 | ⭐⭐⭐ | 长期稳定使用 |
| **ZeroTier / Tailscale** | 免费 | ⭐⭐⭐⭐ | 虚拟内网 IP | ⭐⭐ | 个人多设备访问 |

---

### 方案一：Cloudflare Tunnel（本项目内置支持）

`start-all.sh` 已集成 Cloudflare Tunnel。前提：下载 `cloudflared.exe` 到 `$HOME/cloudflared.exe`。

**Step 1** — 下载 cloudflared

```powershell
# 从 GitHub 下载
Invoke-WebRequest -Uri "https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-windows-amd64.exe" `
  -OutFile "$env:USERPROFILE\cloudflared.exe"
```

**Step 2** — 启动（公网模式）

```bash
bash start-all.sh

# 交互选择时：
#   4. 公网穿透？ [y/N]  → 输入 y

# 脚本会：
#   1. 自动 build 前端（生产模式，约 30-60 秒）
#   2. 使用 vite preview 启动（端口 5173 / 5174）
#   3. 启动 Cloudflare Tunnel
#   4. 输出公网地址
```

输出示例：
```
──── 公网地址 ────
ruoyi-ai-web:  https://random-name.trycloudflare.com
vue-admin:     https://another-name.trycloudflare.com
```

> ⚠️ `trycloudflare.com` 域名每次重启会变化，适合临时演示。长期使用需自备域名 + Cloudflare 账号。

**Step 3** — 手动启动 Tunnel（不通过脚本）

```bash
# 单独为某个端口建立隧道
cloudflared tunnel --url http://localhost:5174

# 后台运行
nohup cloudflared tunnel --url http://localhost:5174 > tunnel.log 2>&1 &

# 查看公网地址
grep -oP 'https://[a-z0-9\-]+\.trycloudflare\.com' tunnel.log
```

---

### 方案二：ngrok

```bash
# 1. 下载 ngrok（https://ngrok.com/download）
# 2. 注册获取 authtoken（https://dashboard.ngrok.com/get-started）
ngrok config add-authtoken <your-token>

# 3. 启动隧道
ngrok http 5174    # AI 用户端
ngrok http 5173    # 管理后台

# 输出：
# Forwarding  https://xxxx.ngrok.io -> http://localhost:5174
```

> ngrok 免费版有速率限制和会话时长限制，适合临时演示。

---

### 方案三：frp（自建，最灵活）

需要一台有公网 IP 的服务器（阿里云/腾讯云轻量应用服务器，最低配即可）。

**服务端（公网服务器）**：

```bash
# 下载 frp：https://github.com/fatedier/frp/releases
# 配置 frps.toml
bindPort = 7000
vhostHTTPPort = 8080

# 启动
./frps -c frps.toml
```

**客户端（本机）**：

```toml
# frpc.toml
serverAddr = "你的服务器IP"
serverPort = 7000

[[proxies]]
name = "ai-web"
type = "http"
localPort = 5174
customDomains = ["ai.your-domain.com"]

[[proxies]]
name = "admin"
type = "http"
localPort = 5173
customDomains = ["admin.your-domain.com"]
```

```bash
./frpc -c frpc.toml
```

> 配合 Nginx 反向代理 + SSL 证书，可实现 HTTPS 自定义域名长期访问。

---

### 方案四：ZeroTier / Tailscale（个人使用）

如果你只是想**自己的多台设备**之间访问，不需要公开展示：

```bash
# 1. 注册 ZeroTier（https://my.zerotier.com）
# 2. 创建网络，获取 Network ID
# 3. 所有设备安装 ZeroTier 客户端，加入同一网络
# 4. 访问：http://<虚拟IP>:5174
```

> 优点：安全、免费、不限速。缺点：不能分享给没有加入网络的人。

---

### 注意事项

- **安全**：公网暴露时，请确保后端 `8090` 端口不对外（只透传前端端口）
- **认证**：生产环境建议关闭 Swagger UI（`application-prod.yml` 中配置）
- **HTTPS**：Cloudflare Tunnel 和 ngrok 自动提供 HTTPS；frp 需自行配置证书
- **带宽**：AI 流式对话需要稳定连接，免费隧道可能有延迟

---

## AI 模型配置

项目通过**策略模式**统一接入 5 个 AI 模型提供商，全部使用 OpenAI 兼容的 Chat Completions API 格式：

| 提供商 | 配置方式 | API 地址 | 默认模型 |
|--------|----------|----------|----------|
| **DeepSeek** | `application.yml` 直接配置 | `https://api.deepseek.com` | `deepseek-chat` |
| **OpenAI** | 环境变量 `OPENAI_API_KEY` | `https://api.openai.com` | — |
| **通义千问** | 环境变量 `QWEN_API_KEY` | `https://dashscope.aliyuncs.com/compatible-mode` | — |
| **智谱 GLM** | 环境变量 `ZHIPU_API_KEY` | `https://open.bigmodel.cn/api/paas` | — |
| **Moonshot** | 环境变量 `MOONSHOT_API_KEY` | `https://api.moonshot.cn` | — |

### 添加新模型

得益于策略模式，新模型只需：

1. 创建 `XxxProvider extends AbstractOpenAIProvider`（约 30 行）
2. 在 `ModelProviderFactory` 注册
3. 管理后台添加配置

AI 在生成这段代码时自动选择了策略模式，因为我说了"要支持多模型切换，方便扩展"——这就是 AI 编程的魅力。

---

## RAG 检索引擎

| 方案 | 适用场景 | 配置 |
|------|----------|------|
| **SimpleVectorStore** | 开发环境，内存 + JSON 持久化 | 默认 |
| **PgVectorStore** | 生产环境，PostgreSQL + pgvector | 需额外配置 |

混合评分算法：**40% 向量相似度 + 40% 关键字匹配 + 20% 结构化字段匹配**

---

## 安全特性

- 🔑 **Bearer Token**：无状态认证，UUID Token
- 🛡️ **RBAC 三级权限**：用户 → 角色 → 菜单/按钮
- 🔒 **BCrypt**：密码不可逆哈希
- 🚫 **XSS 过滤**：请求参数清洗
- ⏱️ **登录限流**：单 IP 5次/分钟，超限锁定 15 分钟
- 📝 **操作审计**：AOP 全自动日志
- 🌐 **CORS**：跨域白名单

---

## 项目结构

```
IdeaProjects/
├── spring-boot-admin/              # 后端 Spring Boot 服务
│   ├── src/main/java/com/example/admin/
│   │   ├── controller/             # 31 个 REST 控制器
│   │   ├── service/
│   │   │   └── model/              # AI 模型提供者（策略模式）
│   │   │       ├── ModelProvider.java         # 接口
│   │   │       ├── AbstractOpenAIProvider.java # 抽象基类
│   │   │       ├── DeepSeekProvider.java       # 5 个实现
│   │   │       ├── OpenAIProvider.java
│   │   │       ├── QwenProvider.java
│   │   │       ├── ZhipuProvider.java
│   │   │       ├── MoonshotProvider.java
│   │   │       └── ModelProviderFactory.java   # 工厂
│   │   ├── mapper/                 # 20 个 MyBatis Mapper
│   │   ├── entity/                 # 22 个实体类
│   │   ├── rag/                    # RAG 检索引擎
│   │   │   ├── EmbeddingService.java
│   │   │   ├── QueryAnalysisService.java
│   │   │   ├── PropertyRetrievalService.java
│   │   │   └── vectorstore/
│   │   │       ├── VectorStore.java
│   │   │       ├── SimpleVectorStore.java
│   │   │       └── PgVectorStore.java
│   │   ├── config/                 # Security/CORS/Swagger 配置
│   │   ├── aspect/                 # LogAspect（AOP 日志）
│   │   └── store/                  # DataStore（内存 Token）
│   ├── src/main/resources/
│   │   ├── mapper/                 # 28 个 MyBatis XML
│   │   ├── application.yml
│   │   ├── schema.sql              # 30 张表 DDL
│   │   └── data.sql                # 初始数据
│   └── pom.xml
├── ruoyi-ai-web/                   # AI 用户端（Vue 3）
│   ├── src/
│   │   ├── components/             # 17 个组件
│   │   ├── stores/                 # 8 个 Pinia Store
│   │   ├── composables/            # 4 个组合式函数
│   │   └── api/                    # 9 个 API 模块
│   └── package.json
├── vue-admin/                      # 管理后台（Vue 2）
│   ├── src/
│   │   ├── views/                  # 业务页面
│   │   └── api/                    # API 模块
│   └── package.json
├── start-all.sh                    # 一键启动（交互式 + 隧道）
├── start-services.bat              # Windows 批处理启动
├── stop-all.sh                     # 一键停止
├── CLAUDE.md                       # AI 编码规范（给 AI 看的）
├── 项目技术文档.md                  # 详细技术文档
└── README.md                       # 本文件
```

---

## 相关文档

| 文档 | 说明 |
|------|------|
| [项目技术文档](./项目技术文档.md) | 架构设计、数据库设计、部署运维全量文档 |
| [API 文档](./spring-boot-admin/API.md) | 30+ API 接口详细说明（参数、返回值、权限） |
| [架构文档](./spring-boot-admin/ARCHITECTURE.md) | 前后端架构、认证流程、数据流设计 |
| [CLAUDE.md](./CLAUDE.md) | AI 编码规范和项目约定 |
| Swagger UI | `http://localhost:8090/doc.html`（在线调试） |

---

## 内置账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | `admin` | `admin123` |
| 普通用户 | `user` | `user123` |
| 测试用户 | `test` | `test123` |

---

## 开源协议

本项目基于 [MIT](https://opensource.org/licenses/MIT) 协议开源，代码完全免费。

核心框架基于 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue)，AI 功能均为原创扩展。

---

> 🔔 **100% AI 生成 · 100% 开源 · 100% 可复现** — 如果你想看 AI 到底能写出什么水平的代码，这就是一份真实的答卷。
> 欢迎 Star & Fork，欢迎提出 Issue 和 PR！
