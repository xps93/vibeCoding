# Vibe Coding 完整教程：零基础用 AI 搭建 Web 项目

> 这份教程教你如何用 AI（Claude Code + DeepSeek）从零搭建一个能卖钱的 Web 项目。不需要编程基础。

配套项目源码在隔壁 [README.md](./README.md)。

---

## 目录

1. [什么是 Vibe Coding](#1-什么是-vibe-coding)
2. [搭建 AI 编程环境](#2-搭建-ai-编程环境)
3. [怎么跟 AI 对话](#3-怎么跟-ai-对话)
4. [真实案例：本项目完整开发过程](#4-真实案例本项目完整开发过程)
5. [开始你自己的项目](#5-开始你自己的项目)
6. [部署上线 + 安全检查](#6-部署上线--安全检查)

---

## 1. 什么是 Vibe Coding

Vibe Coding（氛围编程）的核心思想：**你描述想要什么，AI 负责全部编码。你只需要验证效果对不对。**

```
传统开发：想清楚怎么做 → 手写每行代码 → 调试改 Bug → 写文档
Vibe Coding：说想要什么 → AI 写全部代码 → 运行看效果 → 不对就让它改
```

### 你需要什么

- 能清楚描述想要什么（比如"做一个能登录注册的任务管理网站"）
- 能判断功能是否符合预期（跑起来点一点就行，不需要看懂代码）
- 愿意试错（效果不对就告诉 AI 哪里不对）

### 你不需要什么

不需要背框架 API、不需要手写 SQL/CSS/XML、不需要提前画类图或 ER 图、不需要会 Linux。

---

## 2. 搭建 AI 编程环境

需要三样：**Node.js ≥ 18**、**Claude Code**、**DeepSeek API Key**（10 元够用）。

### 安装 Claude Code

打开 PowerShell，依次执行：

```powershell
node -v                          # 确认 ≥ 18，没有的话去 nodejs.org 下载
npm install -g @anthropic-ai/claude-code
claude --version                 # 验证
```

### 配置 DeepSeek

1. 注册 [platform.deepseek.com](https://platform.deepseek.com) → API Keys → 创建 Key → 充值 10 元
2. 用记事本打开 `C:\Users\<你的用户名>\.claude\settings.json`：

```json
{
  "env": {
    "ANTHROPIC_BASE_URL": "https://api.deepseek.com/anthropic",
    "ANTHROPIC_AUTH_TOKEN": "你的DeepSeek API Key",
    "ANTHROPIC_MODEL": "deepseek-v4-pro",
    "ANTHROPIC_DEFAULT_OPUS_MODEL": "deepseek-v4-pro",
    "ANTHROPIC_DEFAULT_SONNET_MODEL": "deepseek-v4-pro",
    "ANTHROPIC_DEFAULT_HAIKU_MODEL": "deepseek-v4-flash",
    "CLAUDE_CODE_SUBAGENT_MODEL": "deepseek-v4-pro",
    "API_TIMEOUT_MS": "600000",
    "CLAUDE_CODE_EFFORT_LEVEL": "max"
  }
}
```

3. 验证：

```powershell
mkdir ~/test-project && cd ~/test-project && claude
# 输入 /status → 确认显示 deepseek-v4-pro
# 输入 "创建一个 test.txt，内容 Hello" → 检查是否生成
```

配置详情参考 [DeepSeek 官方指南](https://api-docs.deepseek.com/zh-cn/quick_start/agent_integrations/claude_code)。

<p align="center">
  <img src="docs/screenshots/3.png" alt="终端 /status 截图" width="70%" />
</p>
<p align="center"><em>终端输入 /status 确认模型为 deepseek-v4-pro</em></p>

> **重要**：DeepSeek V4-Pro 是纯文本模型，不能识别图片。你不能截图给 AI 让它照着做——只能用文字描述。如需图片识别，切回 Claude 原生模型。

### 创建 CLAUDE.md

在项目根目录放一个 `CLAUDE.md`，AI 每次对话会先读它：

```markdown
# 开发规范
1. 注释和文档用简体中文，代码和变量名用英文
2. Java 类名大驼峰、方法名小驼峰、缩进 4 空格
3. 禁止 System.out，用 SLF4J
4. 分层：Controller → Service → Mapper → Entity
5. 禁止凭空编造不存在的类或方法
```

完整版见 [CLAUDE.md](./CLAUDE.md)。不用一次写全，发现 AI 风格不对就加一条。

---

## 3. 怎么跟 AI 对话

### 核心原则

**说需求，不说实现。** 你管"要什么效果"，AI 管"用什么技术"。

| 错误 | 正确 |
|------|------|
| "用策略模式实现多模型切换" | "支持多个 AI 模型，切换要方便，以后可能加新的" |
| "建表，字段 id、content、role" | "每条消息要存下来，知道谁发的、发的什么、什么时间" |
| "加个拦截器校验 token" | "没登录的人访问后台接口要提示登录" |

### 通用公式

```
[在哪] + [干什么] + [限制条件] + [期望效果]

"这个 Spring Boot 项目，加上 AI 对话功能，用流式输出、模型可切换，
 前端要 ChatGPT 那种打字机效果。"
```

### 四类任务

**从零开始**。说清楚三件事：项目类型 + 技术栈 + 3-5 个核心功能。

```
帮我做一个任务管理网站，后端 Spring Boot、前端 Vue 3。
核心功能：新建任务、标记完成、按日期筛选。
```

**加新功能**。一次只加一个，加完 commit 再加下一个。

```
给任务管理加个标签功能，一个任务多个标签，颜色可自定义。
```

**全局修改**。说清楚范围——"全部"还是"只改某页"。

```
把所有错误提示改成中文，前端做中英文切换开关。
```

**写脚本**。描述流程——先干嘛、再干嘛、结果怎样。

```
写个一键启动脚本，先检测数据库是否运行，没运行就启动，
让用户选启动哪些服务，最后打印访问地址。
```

<p align="center">
  <img src="docs/screenshots/4.png" alt="Claude Code 对话示例" width="70%" />
</p>
<p align="center"><em>Claude Code 对话示例——人说需求，AI 生成文件列表</em></p>

### 踩坑速查

| 问题 | 解法 |
|------|------|
| AI 改太多半对半错 | 拆成小需求，每次 commit 再继续 |
| AI 编造不存在的类 | CLAUDE.md 加"禁止编造类" |
| AI 理解错了 | `git reset --hard` 回退，换说法重来 |
| 代码跑不起来 | 把报错直接贴给 AI |
| 风格不一致 | CLAUDE.md 补充规则 |

### 建议节奏

```
第 1 次：跑通核心流程（CRUD 就行，丑没关系）
第 2 次：加搜索、筛选、分页
第 3 次：优化界面（Loading / 错误提示 / 空状态 / 移动端）
第 4 次：工程收尾（.gitignore、启动脚本、文档）
```

每次不超过半小时，结束就 commit。

---

## 4. 真实案例：本项目完整开发过程

15 次对话、不到 2 天、不到 5 元，产出 86 个 Java 类 + 50+ 前端组件 + 30 张表 + 4 份文档。

### 对话实录

| # | 我说了什么 | AI 产出了什么 | 耗时 |
|---|----------|-------------|------|
| 1 | 创建 Spring Boot + Vue 后台管理系统，有用户、角色、菜单管理 | 项目骨架 + 9 张 RBAC 表 + JWT 认证 + 16 个接口 + Vue 后台 | 40min |
| 2 | 清理编译文件，配 .gitignore | .gitignore + 清理 Git 追踪 | 5min |
| 3~12 | 加 AI 对话——多模型、SSE 流式、会话、知识库、助手模板（10 次，每次加一个功能） | AiChatService + 5 个 Provider + 7 张 AI 表 + SSE + Chat 组件 | 6h |
| 13 | 加 RAG 检索、中英文切换、收藏分享、AI 管理后台 | rag/ 包（10 文件）+ vue-i18n + 收藏/分享 + 模型管理 | 2h |
| 14 | Vite 允许局域网访问 | 2 行配置 | 3min |
| 15 | 一键启动脚本，检测 MySQL/Redis，公网穿透 | start-all.sh（240 行）+ stop-all.sh + bat | 30min |

### 参考了哪些文档

每次对话只给 AI **自然语言需求 + 少量文档片段**，没给任何现成代码：

| 对话主题 | 参考的片段 | AI 据此做了什么 |
|---------|-----------|---------------|
| RBAC 权限 | 一份后台管理系统的功能列表 | 设计 9 张表、JWT 认证、16 个接口 |
| AI 对话 | DeepSeek API 的请求/响应格式（几行 JSON） | 写 OkHttp 调用、SSE 解析、前端打字机 |
| 后台界面 | Element UI 组件列表 | 拼出完整 CRUD 页面 |
| 全流程 | CLAUDE.md | 遵守命名规范、分层架构 |

### AI 自主选择的设计

我一句没指定，AI 自己选的：

| 需求 | AI 选的方案 | 原因 |
|------|-----------|------|
| 5 个 AI 厂商切换 | 策略模式 + 抽象工厂 | 加新厂商只需 30 行 |
| 向量存储（开发/生产） | 接口抽象双实现 | 开发零配置，上线一行切换 |
| AI 对话实时输出 | SSE（非 WebSocket） | 更简单，够用 |
| Vue 2/3 共存 | 自动匹配 Pinia / Vuex | 按版本选最佳 |

### 效率对比

| 模块 | 手写预估 | AI 实际 | 提效 |
|------|---------|--------|------|
| 后端（86 类） | 6 周 | 约 1 天 | ×30 |
| 前端（50+ 组件） | 4 周 | 约半天 | ×40 |
| 数据库（30 表） | 3 天 | 2 小时 | ×12 |
| 文档（5000+ 行） | 1 周 | 2 小时 | ×20 |
| **总计** | **2-3 个月** | **不到 2 天** | **×30+** |

---

## 5. 开始你自己的项目

```powershell
mkdir my-project && cd my-project && claude
```

进入对话界面后，选一个：

**有明确想法：**
```
帮我创建一个 [项目类型]，技术栈 Spring Boot + Vue 3。
核心功能：1.XXX  2.XXX  3.XXX。数据库 MySQL，缓存 Redis。
```

**想练手：**
```
帮我创建一个任务管理 Web 应用。能添加任务、标记完成、按日期筛选、
搜索标题。后端 Spring Boot，前端 Vue 3，数据库 MySQL。
```

**完全不知道做什么：**
```
帮我创建一个个人博客系统。发布文章（标题+正文）、分类、标签、
评论、后台管理。前端 Vue 3，后端 Spring Boot，数据库 MySQL。
```

第一次对话结束后：

```bash
git init && git add -A && git commit -m "第一版"
# 然后每次只加一个功能：
"帮我加个搜索功能"
"列表加个分页"
"登录页优化一下 UI"
```

每次对话只做一件事，做完就 commit。

---

## 6. 部署上线 + 安全检查

### 环境要求

JDK ≥ 1.8 · MySQL ≥ 5.7 · Redis ≥ 3.0 · Maven ≥ 3.0 · Node.js ≥ 16

### 本地启动

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ai_test DEFAULT CHARACTER SET utf8mb4;"
bash start-all.sh           # Git Bash
# 或双击 start-services.bat  # Windows 无 Git Bash

# http://localhost:5174   AI 用户端
# http://localhost:5173   管理后台
# http://localhost:8090/doc.html  接口文档
```

内置账号：`admin/admin123` · `user/user123`

### 公网访问

1. 下载 [cloudflared.exe](https://github.com/cloudflare/cloudflared/releases) 到用户目录
2. 运行 `bash start-all.sh`，选第 4 项
3. 获得 `https://xxxx.trycloudflare.com` 公网地址

其他方案（ngrok/frp/ZeroTier）见 [技术文档](./项目技术文档.md)。

### 上线前 8 项检查

| # | 检查项 | 通过标准 |
|---|--------|---------|
| 1 | 登录保护 | 不登录访问 → 跳转登录页 |
| 2 | 密码安全 | 数据库密码是 BCrypt 密文 |
| 3 | XSS 防护 | `<script>alert(1)</script>` 不被执行 |
| 4 | 防暴力破解 | 输错 5 次密码暂时锁定 |
| 5 | 操作审计 | 日志可查谁在什么时间做了什么 |
| 6 | 界面完整 | 有 Loading / 错误提示 / 空状态 |
| 7 | 跨域安全 | CORS 白名单而非 `*` |
| 8 | 文档保护 | 生产环境 Swagger 关闭或需登录 |

---

> 不到 2 天 + 5 块钱 = 一个可商用 Web 系统。现在搭环境，打开 PowerShell，输入 `claude`，说第一句话。
