# Ds-Ai —— 多模块 AI 聊天平台

> 🤖 **100% AI 生成，人类零手写代码。** 从第一行配置到 30+ 功能模块、86 个 Java 类、300+ 前端组件，全部由 Claude Code + DeepSeek 完成。
>
> 本项目既是功能完备的AI聊天平台，也是一份 **AI 驱动开发的完整实操参考**——从环境搭建到交付上线，每一步的思考过程都记录在案。

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://gitee.com/xp_s/vibe_coding)
[![Spring Boot 2.7](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue-3.5-4FC08D.svg)](https://vuejs.org/)
[![JDK 8](https://img.shields.io/badge/JDK-1.8-orange.svg)](https://www.oracle.com/java/)
[![AI Built](https://img.shields.io/badge/100%25-AI%20Built-blueviolet.svg)](https://claude.ai/code)

- 📦 **源码仓库**：https://gitee.com/xp_s/vibe_coding
- 📖 **配套文档**：[技术文档](./项目技术文档.md) · [API 文档](./spring-boot-admin/API.md) · [架构文档](./spring-boot-admin/ARCHITECTURE.md)
- 🏗️ **底层框架**：[RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue)

---

## 目录

1. [平台简介](#1-平台简介)
2. [AI 驱动开发：从零到一的完整实录](#2-ai-驱动开发从零到一的完整实录)
3. [搭建 AI 驱动开发环境（环境篇）](#3-搭建-ai-驱动开发环境环境篇)
4. [系统需求](#4-系统需求)
5. [本地部署（快速开始）](#5-本地部署快速开始)
6. [公网访问 · 内网穿透](#6-公网访问--内网穿透)
7. [内置功能一览](#7-内置功能一览)
8. [技术栈](#8-技术栈)
9. [AI 模型接入](#9-ai-模型接入)
10. [项目结构](#10-项目结构)
11. [安全特性](#11-安全特性)
12. [相关文档](#12-相关文档)

---

## 1. 平台简介

**Ds-Ai** 是一套全部开源的 AI 聊天平台，采用 **Spring Boot + Vue 前后端分离**架构，在 RuoYi RBAC 权限体系之上深度扩展了多模型 AI 对话、RAG 检索增强、知识库管理、国际化等能力。

### 三大模块

| 子项目 | 定位 | 技术栈 | 端口 |
|--------|------|--------|------|
| **spring-boot-admin** | 后端 API 服务 | Spring Boot 2.7 + MyBatis + Redis + MySQL 8.0 | `8090` |
| **ds_ai_web** | AI 用户端 | Vue 3.5 + Element Plus + Pinia + Vite | `5174` |
| **vue-admin** | 管理后台 | Vue 2.7 + Element UI + Vuex + Vite | `5173` |

### 核心亮点

- 🤖 **多模型 AI 对话**：DeepSeek / OpenAI / 通义千问 / 智谱 / Moonshot，统一协议接入
- 📡 **SSE 流式输出**：实时打字效果，推理模型思考过程可视化
- 🔍 **RAG 检索增强**：向量 + 关键字 + 结构化字段三路混合评分
- 🌐 **联网搜索**：实时网页内容注入 AI 上下文
- 📄 **文档解析**：PDF / Word / Excel / PPT / TXT 上传即用
- 🎭 **助手模板**：预设角色 Prompt，按通用/编程/写作/数据分析分类
- ⭐ **收藏与分享**：对话分组收藏，一键生成分享链接
- 🌍 **国际化**：中英文切换，错误码双语
- 🔐 **安全机制**：Bearer Token + RBAC 三级权限 + XSS 防护 + 登录限流

---

## 2. AI 驱动开发：从零到一的完整实录

### 2.1 核心理念

本项目是 **"Vibe Coding"（氛围编程）** 的完整实践。核心工作流：

```
人（描述需求）→ AI（架构设计 + 编码实现）→ 人（审查验证）→ 提交 → 迭代
```

**我做了什么**：描述需求、审查代码、运行验证、反馈问题。
**AI 做了什么**：技术选型、架构设计、全部编码（Java + Vue + SQL + Shell）、文档生成、Bug 修复。

### 2.2 开发时间线

每一次 `git commit` 就是一次人机对话的结果。以下展示完整迭代过程：

| 阶段 | Commit 信息 | 对话内容（实际对 AI 说的话） |
|------|------------|---------------------------|
| 🏗️ 初始化 | `init: 初始化项目` | *"帮我创建一个 Spring Boot + Vue 的后台管理系统，参考 RuoYi 架构，要有完整的 RBAC 权限"* |
| 🧹 清理 | `chore: 清理无效文件` | *"清理编译产物和 IDE 临时文件，配置 .gitignore"* |
| 🤖 AI 核心 | `add ai` × 10 次迭代 | *"添加 AI 对话功能——要支持多模型切换（DeepSeek/OpenAI/通义千问/智谱），SSE 流式输出，会话历史管理"* |
| 🚀 大版本 | `feat: RAG增强对话 + AI管理后台 + 多语言/收藏/分享` | *"加上 RAG 检索增强、国际化中英文、对话收藏和分享功能，管理后台要能管理 AI 模型配置"* |
| 🔧 工程化 | `chore: .gitignore + dist 管理` | *"把前端构建产物和 node_modules 加入 gitignore，配置好本地开发环境"* |
| 🌐 网络 | `feat: Vite host 0.0.0.0 + allowedHosts` | *"让 Vite 允许局域网和外部访问，配好 allowedHosts"* |

### 2.3 四种 AI 编程范式

在实际开发中，针对不同任务使用了不同的对话策略：

#### 范式一：需求 → 架构 → 代码（最常用）

```
用户：给项目添加 RAG 检索增强功能

AI 思考过程：
  1. 分析现有代码——AiChatService 在哪儿发送请求？
  2. 在请求发送前插入检索步骤最合适，不破坏现有逻辑
  3. 需要三个子模块：嵌入服务、查询分析、向量存储
  4. 向量存储做成接口抽象——开发用内存版，生产用 pgvector
  5. 评分算法用混合模型（向量 40% + 关键字 40% + 结构化 20%）

AI 输出：
  - 新建 rag/ 包（10 个文件）
  - 修改 AiChatService，3 行注入检索结果
  - 新增 DatasetController + PropertyRagController
  - 自动生成数据库脚本
```

**适用场景**：新增功能、系统重构。

#### 范式二：渐进式迭代

```
第一轮："支持 DeepSeek 模型对话"
第二轮："再加 OpenAI" → AI 发现代码重复，自己抽了 AbstractOpenAIProvider
第三轮："再加通义千问、智谱" → AI 建了 ModelProviderFactory 工厂
第四轮："还要 Moonshot" → 只需新增一个 30 行的 Provider 类
```

**适用场景**：不确定最终范围时，逐步扩展。

#### 范式三：跨模块批量修改

```
用户：全部错误信息要中英文双语

AI 做的事：
  1. 创建 sys_error_code 表
  2. 新建 ErrorCodeService + ErrorCodeAdminController
  3. 扫描所有 31 个 Controller，逐个替换硬编码错误消息
  4. 前端添加 vue-i18n，修改 30+ 组件
  5. Swagger 注解同步更新
  6. 管理后台新增错误码管理界面
```

**适用场景**：横切关注点（国际化、日志、安全）。

#### 范式四：探索式开发

```
用户：帮我写一个一键启停脚本，要自动检测 MySQL 和 Redis

AI 做的事：
  1. 搜索系统端口占用方式（netstat）
  2. 检查 Redis 和 MySQL 的启动方式
  3. 设计交互式菜单（选服务、选隧道）
  4. 集成 Cloudflare Tunnel 内网穿透
  5. 编写对应的 Windows .bat 版本
  6. 编写 stop-all.sh 清理脚本
```

**适用场景**：运维自动化、工具脚本、配置管理。

### 2.4 效率对比

| 维度 | 传统手写 | AI 驱动（本项目） | 效率倍数 |
|------|---------|-----------------|---------|
| 后端 Java 代码（86 个类） | ~6 周 | ~5 天 | **×8** |
| 前端 Vue 组件（50+） | ~4 周 | ~3 天 | **×9** |
| 数据库设计（30 张表） | ~3 天 | ~2 小时 | **×12** |
| 文档（4 份，5000+ 行） | ~1 周 | ~1 天 | **×5** |
| 运维脚本 | ~2 天 | ~30 分钟 | **×32** |
| **总计** | **约 2-3 个月** | **约 2 周（业余时间）** | **×5~10** |

### 2.5 给 AI 编程者的 6 条实战经验

| # | 经验 | 具体做法 |
|---|------|---------|
| 1 | **写好 CLAUDE.md** | 编码规范、技术栈偏好、命名约定都写进去，AI 100% 遵循。本项目的 [CLAUDE.md](./CLAUDE.md) 就是给 AI 的开发手册 |
| 2 | **小步迭代，频繁 commit** | 一次只让 AI 做一件事。10 条需求一起提，AI 会乱；拆成 10 次对话，每次都对 |
| 3 | **先 MVP 再增强** | 先"能跑就行"，再要求"加 RAG"、"加国际化"、"加收藏"。AI 擅长在已有代码基础上增量修改 |
| 4 | **信任 AI 的架构判断** | 说"我要支持多模型"，不要说"用策略模式"。AI 自己选的架构往往就是最合适的 |
| 5 | **让 AI 读自己的代码** | 迭代时 AI 自动阅读已有文件，保持风格一致。不一致时直接指出，下一轮就修正 |
| 6 | **琐事全扔给 AI** | `.gitignore`、启动脚本、`application.yml`、环境变量——这些 AI 做得又快又好，别浪费自己时间 |

---

## 3. 搭建 AI 驱动开发环境（环境篇）

> 如果你也想复现"一个人 + AI = 一个团队"的开发体验，下面是从零搭建环境的完整指南。

### 3.1 整体架构

```
┌─────────────────────────────────────────┐
│              你（人类）                   │
│   描述需求 · 审查代码 · 运行验证 · 提交   │
└──────────────────┬──────────────────────┘
                   │ 对话交互
                   ▼
┌─────────────────────────────────────────┐
│          Claude Code（AI 终端）           │
│  ┌─────────────────────────────────────┐ │
│  │ CLI 交互 · 文件读写 · Shell 执行 · Git│ │
│  │ Agent 子任务 · Workflow 编排 · Memory│ │
│  └─────────────────────────────────────┘ │
└──────────────────┬──────────────────────┘
                   │ API 调用
                   ▼
┌─────────────────────────────────────────┐
│         DeepSeek / Claude API           │
│     deepseek-v4-pro / claude-sonnet     │
└─────────────────────────────────────────┘
```

### 3.2 安装 Claude Code

Claude Code 是 Anthropic 推出的 AI 编程终端，支持接入第三方模型。

**Step 1：安装 Node.js**

```bash
# 要求 Node.js ≥ 18
node -v    # 确认版本
# 去 https://nodejs.org 下载 LTS 版本
```

**Step 2：安装 Claude Code**

```bash
npm install -g @anthropic-ai/claude-code

# 验证安装
claude --version
```

**Step 3：选择 AI 模型**

Claude Code 支持两种模型来源：

| 方案 | 费用 | 能力 | 推荐 |
|------|------|------|------|
| **Claude API（官方）** | 按量付费 | ⭐⭐⭐⭐⭐ 最强 | 生产级项目 |
| **DeepSeek API（第三方）** | 极低 | ⭐⭐⭐⭐ 很强 | 学习/日常开发 |

> 本项目全程使用 **DeepSeek** 模型，性价比极高。

### 3.3 配置 DeepSeek 模型

**Step 1：获取 DeepSeek API Key**

1. 注册 [DeepSeek 开放平台](https://platform.deepseek.com)
2. 进入「API Keys」创建 Key
3. 充值（最低 10 元，够用很久）

**Step 2：创建配置文件**

在用户目录创建 `.claude/settings.json`：

```json
{
  "model": "deepseek-v4-pro",
  "apiKeyHelper": "deepseek.bat"
}
```

> 本项目实际使用的模型是 `deepseek-v4-pro`，Claude Code 会自动调用 DeepSeek 兼容 API。

> ⚠️ **注意**：Claude Code 原生需配置 `apiKeyHelper` 脚本来桥接 DeepSeek API。具体配置方式随版本更新可能变化，请参考 [Claude Code 官方文档](https://docs.anthropic.com/en/docs/claude-code) 中的「第三方模型」章节获取最新配置方法。

### 3.4 项目级 CLAUDE.md：给 AI 定规矩

在项目根目录创建 `CLAUDE.md`，这是 AI 遵守的开发规范。本项目实测，AI 对 CLAUDE.md 的遵循度接近 100%。

> 📄 完整内容见项目根目录的 [CLAUDE.md](./CLAUDE.md)

**CLAUDE.md 核心内容**：

```markdown
# 全局技术规范

## 一、语言与输出
1. 所有对话、注释、文档：简体中文
2. 代码、变量、类名：严格英文命名
3. 禁止无意义英文缩写

## 二、Java 编码规范
- 类：PascalCase、方法：camelCase、常量：UPPER_SNAKE_CASE
- 缩进 4 空格、单行 ≤ 120
- 禁止 foreach 内增删元素
- 禁止 System.out，使用 SLF4J

## 三、MySQL 规范
- 表/字段：小写+下划线
- 主键：bigint unsigned 自增
- 索引：idx_字段、uniq_字段
- 禁止 SELECT *

## 四、工程结构
- 分层：Controller → Service → Mapper → Entity
- 禁止编造类、方法、配置，必须基于真实项目
```

> 💡 **实操技巧**：CLAUDE.md 不需要大而全，但要把"你最在意的规范"写进去。每次发现 AI 不按你的习惯写，就加一条规则到 CLAUDE.md。

### 3.5 Memory 记忆系统

Claude Code 支持持久记忆（Memory），配置路径：`.claude/projects/<project>/memory/`

```
C:\Users\<用户名>\.claude\projects\C--Users-25894-IdeaProjects\memory\
├── MEMORY.md              ← 记忆索引（自动加载到每次对话）
├── project_scripts.md     ← 启动/停止脚本的用法记忆
└── ...                    ← 其他记忆文件
```

每个记忆文件的结构：

```markdown
---
name: project_scripts
description: "一键启动/停止脚本"
metadata:
  type: project
---

start-all.sh 一键启动三个服务
stop-all.sh 一键停止三个服务

**Why:** 用户不想每次手动启动三个服务。
**How to apply:** 当用户说"启动"时运行 bash start-all.sh。
```

> 💡 **记忆的妙用**：AI 会记住你的习惯偏好，省去每次重复说明。比如"用户说启动 = 运行 start-all.sh"。

### 3.6 一键启动开发环境

配置完成后，每天的开发流程：

```bash
# 1. 进入项目目录
cd C:\Users\25894\IdeaProjects

# 2. 启动 Claude Code
claude

# 3. 开始对话编程
> "给项目添加一个 XXX 功能"
> "这段代码有问题，帮我排查"
> "帮我生成 XXX 的单元测试"
```

---

## 4. 系统需求

| 环境 | 最低版本 | 推荐版本 | 说明 |
|------|---------|---------|------|
| **JDK** | 1.8 | 1.8 | 后端运行环境 |
| **MySQL** | 5.7 | 8.0 | 需提前创建 `ai_test` 数据库 |
| **Redis** | 3.0 | 7.x | 缓存和 Token 存储 |
| **Maven** | 3.0 | 3.6+ | 后端构建 |
| **Node.js** | 16 | 18+ | 前端构建 |

---

## 5. 本地部署（快速开始）

### 方式一：一键脚本（推荐）

```bash
# 启动（交互式选择启动哪些服务）
bash start-all.sh

# 界面示例：
#   1. 后端 spring-boot-admin (8090)？ [y/N]
#   2. 前端 vue-admin 管理后台 (5173)？ [y/N]
#   3. 前端 ds_ai_web AI聊天 (5174)？ [y/N]
#   4. 公网穿透？ [y/N]

# 停止全部
bash stop-all.sh
```

### 方式二：Windows 批处理

```cmd
start-services.bat
# 三个服务分别在新窗口启动
```

### 方式三：手动分步启动

#### Step 1 — 前置依赖

确保 **MySQL**（端口 3306）和 **Redis**（端口 6379）已运行。

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ai_test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
```

#### Step 2 — 启动后端

```bash
cd spring-boot-admin
mvn spring-boot:run
# 验证：浏览器打开 http://localhost:8090/doc.html（Swagger UI）
```

#### Step 3 — 启动前端

```bash
# AI 用户端（5174）
cd ds_ai_web && npm install && npm run dev

# 管理后台（5173）
cd vue-admin && npm install && npm run dev
```

#### Step 4 — 登录验证

| 入口 | 地址 | 默认账号 |
|------|------|---------|
| AI 用户端 | `http://localhost:5174` | `admin` / `admin123` |
| 管理后台 | `http://localhost:5173` | `admin` / `admin123` |
| Swagger | `http://localhost:8090/doc.html` | — |

> 其他内置账号：`user` / `user123`、`test` / `test123`

---

## 6. 公网访问 · 内网穿透

本地服务只有自己能访问。如果需要**手机测试、远程演示、临时上线**，从以下方案中选择。

### 方案对比

| 方案 | 费用 | 速度 | 难度 | 适合场景 |
|------|------|------|------|---------|
| **Cloudflare Tunnel** | 免费 | ⭐⭐⭐ | ⭐ 极简 | 临时演示（本项目内置支持） |
| **ngrok** | 免费/付费 | ⭐⭐⭐ | ⭐ 极简 | 快速分享 |
| **frp** | 需服务器 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | 长期稳定使用 |
| **ZeroTier / Tailscale** | 免费 | ⭐⭐⭐⭐ | ⭐⭐ | 个人多设备 |

### 6.1 Cloudflare Tunnel（本项目内置）

`start-all.sh` 已集成，启动时选择"公网穿透"即可。

```bash
# 1. 下载 cloudflared（一次性）
# 从 https://github.com/cloudflare/cloudflared/releases 下载 Windows 版
# 放到 C:\Users\<用户名>\cloudflared.exe

# 2. 启动时选择公网模式
bash start-all.sh
# 第4项：公网穿透？ [y/N] → y

# 3. 脚本自动完成：
#    - 前端打包为生产模式
#    - 启动 vite preview
#    - 建立 Cloudflare Tunnel
#    - 输出公网地址
```

输出示例：

```
──── 公网地址 ────
ds_ai_web:  https://sunny-moon-1234.trycloudflare.com
vue-admin:     https://dark-star-5678.trycloudflare.com
```

> ⚠️ 免费域名每次重启会变化，适合临时演示。如需固定域名，注册 Cloudflare 账号并配置。

### 6.2 ngrok（2 分钟上手）

```bash
# 1. 下载 ngrok：https://ngrok.com/download
# 2. 获取 authtoken：https://dashboard.ngrok.com/get-started
ngrok config add-authtoken <your-token>

# 3. 启动
ngrok http 5174    # AI 用户端
ngrok http 5173    # 管理后台
# → https://xxxx.ngrok.io → http://localhost:5174
```

### 6.3 frp（自建，最灵活）

需要一台有公网 IP 的轻量服务器（阿里云/腾讯云最低配即可）。

**服务端** `frps.toml`：

```toml
bindPort = 7000
vhostHTTPPort = 8080
```

**客户端** `frpc.toml`：

```toml
serverAddr = "你的服务器IP"
serverPort = 7000

[[proxies]]
name = "ai-web"
type = "http"
localPort = 5174
customDomains = ["ai.your-domain.com"]
```

```bash
# 服务端
./frps -c frps.toml

# 客户端（本机）
./frpc -c frpc.toml
```

> 配合 Nginx + Let's Encrypt，可实现 HTTPS 自定义域名长期稳定访问。

### 6.4 ZeroTier / Tailscale（个人专用）

不想公开，只想自己多台设备之间用：

```bash
# 1. 注册 ZeroTier：https://my.zerotier.com
# 2. 创建网络，获取 Network ID
# 3. 所有设备安装客户端，加入同一网络
# 4. 直接用虚拟 IP 访问：http://10.147.x.x:5174
```

> 优点：安全、免费、不限速。缺点：访问方也需要安装客户端。

### ⚠️ 安全提醒

- 后端 `8090` 端口**不要对外暴露**，只透传前端端口
- 生产环境关闭 Swagger（`springdoc.swagger-ui.enabled=false`）
- Cloudflare Tunnel / ngrok 自动 HTTPS；frp 需自行配置证书

---

## 7. 内置功能一览

### 系统管理（RBAC）

| # | 功能 | 说明 |
|---|------|------|
| 1 | 用户管理 | 用户 CRUD、导出、角色分配 |
| 2 | 部门管理 | 组织机构树，数据权限绑定 |
| 3 | 岗位管理 | 用户职务配置 |
| 4 | 菜单管理 | 目录/菜单/按钮三级权限标识 |
| 5 | 角色管理 | 菜单权限 + 数据范围权限分配 |
| 6 | 字典管理 | 类型 + 键值对固定数据维护 |
| 7 | 参数管理 | 系统动态参数配置 |
| 8 | 通知公告 | 发布与维护 |

### 系统监控

| # | 功能 | 说明 |
|---|------|------|
| 9 | 操作日志 | AOP 自动记录，可按模块/操作人查询 |
| 10 | 登录日志 | 登录记录含异常信息 |
| 11 | 在线用户 | 活跃 Token 实时监控 |
| 12 | 定时任务 | 在线 CRUD，含执行日志 |
| 13 | 服务监控 | CPU / 内存 / 磁盘 / JVM 信息 |
| 14 | 缓存监控 | Redis 缓存统计 |

### 系统工具

| # | 功能 | 说明 |
|---|------|------|
| 15 | 代码生成 | 从表结构自动生成前后端 CRUD，ZIP 下载 |
| 16 | API 文档 | Knife4j / Swagger 在线调试 |
| 17 | 在线构建器 | 拖拽表单生成 HTML/Vue 代码 |

### AI 功能

| # | 功能 | 说明 |
|---|------|------|
| 18 | AI 对话 | 多模型 SSE 流式输出，Markdown + 代码高亮 |
| 19 | 多模型切换 | 策略模式，5 个 AI 提供商自由切换 |
| 20 | 会话管理 | 历史保存、搜索、批量删除 |
| 21 | 助手模板 | 预设 Prompt，按通用/编程/写作/数据分析分类 |
| 22 | 模型管理 | 管理后台配置模型参数、API Key、启停状态 |
| 23 | 知识库 | 关联指定知识库到对话 |
| 24 | 文档解析 | 上传 PDF/Word/Excel/PPT/TXT，自动注入上下文 |
| 25 | 联网搜索 | 搜狗网页搜索，实时信息注入 |
| 26 | RAG 检索 | 向量 + 关键字 + 结构化字段混合评分 |
| 27 | 收藏 | 对话分组收藏 |
| 28 | 分享 | 一键生成分享链接，外部可查看 |
| 29 | 消息评价 | 点赞/点踩，收集反馈 |
| 30 | 站点配置 | 名称/Logo/版权等全局可配 |

---

## 8. 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 1.8 | 运行时 |
| Spring Boot | 2.7.18 | 应用框架 |
| Spring Security | 5.7.x | 认证授权 |
| Spring AOP | — | 操作日志切面 |
| MyBatis | 2.3.2 | ORM |
| MySQL | 8.0 | 数据库 |
| Redis | ≥ 3.0 | 缓存 / Token 存储 |
| HikariCP | — | 连接池 |
| Knife4j | 4.3.0 | API 文档 |
| OkHttp | 4.12.0 | AI 模型 HTTP 调用 |
| Apache Tika | 2.9.2 | 文档解析 |
| Jsoup | 1.15.4 | 网页抓取 |

### 前端 — AI 用户端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.13 | 前端框架 |
| Element Plus | 2.9.1 | UI 组件库 |
| Pinia | 2.3.0 | 状态管理 |
| Vue Router | 4.5.0 | 路由 |
| Axios | 1.7.9 | HTTP |
| Vite | 5.4.19 | 构建 |
| marked | 14.1.4 | Markdown |
| highlight.js | 11.11.1 | 代码高亮 |
| vue-i18n | ^10.0.7 | 国际化 |

### 前端 — 管理后台

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 2.7.16 | 前端框架 |
| Element UI | 2.15.14 | UI 组件库 |
| Vuex | 3.6.2 | 状态管理 |
| Vue Router | 3.6.5 | 路由 |
| Axios | 1.7.2 | HTTP |
| Vite | 5.4.19 | 构建 |

---

## 9. AI 模型接入

通过**策略模式**统一接入 5 个 AI 提供商，全部使用 OpenAI 兼容的 Chat Completions API 格式。

| 提供商 | API 地址 | 默认模型 | 配置方式 |
|--------|----------|---------|---------|
| **DeepSeek** | `https://api.deepseek.com` | `deepseek-chat` | `application.yml` |
| **OpenAI** | `https://api.openai.com` | — | 环境变量 |
| **通义千问** | `https://dashscope.aliyuncs.com/compatible-mode` | — | 环境变量 |
| **智谱 GLM** | `https://open.bigmodel.cn/api/paas` | — | 环境变量 |
| **Moonshot** | `https://api.moonshot.cn` | — | 环境变量 |

### 架构设计

```
AiChatService
  └── ModelProviderFactory.getProvider("deepseek" | "openai" | "qwen" | "zhipu" | "moonshot")
        └── AbstractOpenAIProvider（公共逻辑：请求构建、SSE 解析、错误处理）
              ├── DeepSeekProvider   ← 30 行
              ├── OpenAIProvider      ← 30 行
              ├── QwenProvider        ← 30 行
              ├── ZhipuProvider       ← 30 行
              └── MoonshotProvider    ← 30 行
```

> 💡 这是 AI 自己选的设计——我说"要支持多个模型，以后可能还会加"，AI 就选了策略模式 + 抽象工厂，无需我指定。

---

## 10. 项目结构

```
IdeaProjects/
│
├── spring-boot-admin/                 # 后端 Spring Boot
│   ├── src/main/java/com/example/admin/
│   │   ├── controller/                # 31 个 REST 控制器
│   │   ├── service/
│   │   │   └── model/                 # AI 模型提供者（策略模式）
│   │   │       ├── ModelProvider.java
│   │   │       ├── AbstractOpenAIProvider.java
│   │   │       ├── DeepSeekProvider.java
│   │   │       ├── OpenAIProvider.java
│   │   │       ├── QwenProvider.java
│   │   │       ├── ZhipuProvider.java
│   │   │       ├── MoonshotProvider.java
│   │   │       └── ModelProviderFactory.java
│   │   ├── mapper/                    # 20 个 MyBatis Mapper
│   │   ├── entity/                    # 22 个实体
│   │   ├── rag/                       # RAG 检索引擎
│   │   │   ├── EmbeddingService.java
│   │   │   ├── QueryAnalysisService.java
│   │   │   ├── PropertyRetrievalService.java
│   │   │   └── vectorstore/
│   │   │       ├── VectorStore.java
│   │   │       ├── SimpleVectorStore.java    # 开发环境
│   │   │       └── PgVectorStore.java        # 生产环境
│   │   ├── config/                    # Security / CORS / Swagger
│   │   ├── aspect/LogAspect.java      # AOP 操作日志
│   │   └── store/DataStore.java       # 内存 Token 存储
│   ├── src/main/resources/
│   │   ├── mapper/                    # 28 个 MyBatis XML
│   │   ├── application.yml
│   │   ├── schema.sql                 # 30 张表 DDL
│   │   └── data.sql                   # 初始数据
│   └── pom.xml
│
├── ds_ai_web/                      # AI 用户端（Vue 3）
│   └── src/
│       ├── components/                # 17 个组件
│       ├── stores/                    # 8 个 Pinia Store
│       ├── composables/               # 4 个组合式函数
│       └── api/                       # 9 个 API 模块
│
├── vue-admin/                         # 管理后台（Vue 2）
│   └── src/
│       ├── views/                     # 业务页面
│       └── api/                       # API 模块
│
├── CLAUDE.md                          # AI 编码规范（给 AI 看的手册）
├── start-all.sh                       # 一键启动（交互式 + 隧道）
├── start-services.bat                 # Windows 批处理
├── stop-all.sh                        # 一键停止
├── 项目技术文档.md                      # 技术全量文档
└── README.md                          # 本文件
```

---

## 11. 安全特性

| 机制 | 实现方式 |
|------|---------|
| 🔑 认证 | Bearer Token（UUID），无状态 |
| 🛡️ 鉴权 | RBAC 三级：用户 → 角色 → 菜单/按钮 |
| 🔒 密码 | BCrypt 不可逆哈希 |
| 🚫 XSS | 请求参数过滤清洗 |
| ⏱️ 限流 | 单 IP 5 次/分钟，超限锁定 15 分钟 |
| 📝 审计 | AOP 自动记录全部操作日志 |
| 🌐 CORS | 跨域白名单控制 |

---

## 12. 相关文档

| 文档 | 说明 |
|------|------|
| [📘 项目技术文档](./项目技术文档.md) | 架构设计、数据库设计、部署运维全覆盖 |
| [📗 API 文档](./spring-boot-admin/API.md) | 30+ 接口详细说明（参数、返回值、权限） |
| [📙 架构文档](./spring-boot-admin/ARCHITECTURE.md) | 前后端架构、认证流程、数据流 |
| [📕 CLAUDE.md](./CLAUDE.md) | AI 编码规范和约定（给 AI 看的） |
| 🔧 Swagger UI | `http://localhost:8090/doc.html`（在线调试） |

---

## 开源协议

基于 [MIT](https://opensource.org/licenses/MIT) 协议开源，核心框架基于 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue)。

---

> 🔔 **一个人 + Claude Code + DeepSeek = 2 周写完一个完整项目。** 如果你也想体验这种开发方式，从 [第 3 节](#3-搭建-ai-驱动开发环境环境篇) 开始搭建你的 AI 编程环境。
>
> 欢迎 ⭐ Star、🔱 Fork、💬 Issue、🔧 PR！
