# Vibe Coding 实战：从零搭建可商业落地的 Web 项目

> 🎯 **Vibe Coding = 你用自然语言描述想要什么，AI 负责全部编码实现。你不需要写一行代码。**
>
> 本项目就是一个真实的 Vibe Coding 产物——一个功能完备的 AI 聊天平台（30+ 功能、86 个 Java 类、300+ 前端组件），**100% 由 AI 通过对话生成，零手写代码**。总成本：不到 2 天 + 不到 5 块钱的 API 费用。
>
> 本文档把从 "hello world" 到可部署上线的**每一步对话技巧、踩坑经验、方法论**全部公开，让你也能用同样的方式，快速交付能卖钱的 Web 项目。

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://gitee.com/xp_s/vibeCoding)
[![Spring Boot 2.7](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue-3.5-4FC08D.svg)](https://vuejs.org/)
[![AI Built](https://img.shields.io/badge/100%25-AI%20Built-blueviolet.svg)](https://claude.ai/code)

---

## 什么是 Vibe Coding

**Vibe Coding**（氛围编程）是 2025 年由 Andrei Karpathy（OpenAI 联合创始人）提出的开发范式：

```
传统开发：人想架构 → 人写代码 → 人调试 → 人写文档
Vibe Coding：人描述需求 → AI 写代码 → 人验证效果 → 提交上线
```

核心转变：**你不再是一个"写代码的人"，而是一个"用自然语言指挥 AI 写代码的产品经理+架构师"。**

你只需要：
- 🗣️ 能用中文/英文清楚描述你想要什么
- 👀 能看懂 AI 生成的代码大概在干什么（不需要精通）
- 🔄 愿意迭代：描述 → 验证 → 反馈 → 再描述

你不需要：
- ❌ 记住任何框架的 API 或配置语法
- ❌ 手写 SQL、CSS、或者 XML 配置
- ❌ 提前设计类图和数据库 ER 图

> 📖 本项目就是 Vibe Coding 的一个完整案例。继续往下读，你会看到每一轮对话说了什么、AI 做了什么、以及你该怎么复制。

---

## 这份教程能让你学会什么

| 技能 | 具体你会做什么 | 对应章节 |
|------|--------------|---------|
| 🤖 **搭建 AI 编程环境** | 安装 Claude Code + DeepSeek，30 分钟拥有自己的 AI 程序员 | [第一步](#第一步搭建你的-vibe-coding-环境) |
| 💬 **跟 AI 高效对话** | 掌握 4 种对话模式，从"帮我写个系统"到精准控制每个模块 | [第二步：核心方法论](#第二步核心方法论怎么跟-ai-对话) |
| 🏗️ **从零到上线完整流程** | 看一个真实项目 15 轮对话的全过程：MVP → 迭代 → 部署 | [实战案例](#实战案例ds-ai-完整开发过程) |
| 🌐 **本地 → 公网部署** | 一键脚本 + 内网穿透，把项目变成可访问的在线服务 | [部署上线](#部署上线本地--公网) |
| 💼 **商业落地检查清单** | 安全、性能、体验——上线前必须验证的 8 个点 | [商业落地清单](#商业落地检查清单) |

> 🏆 **最终成果**：不到 2 天，得到一个可直接部署上线、能拿去给客户演示的完整 Web 系统。

---

## 目录

1. [第一步：搭建你的 Vibe Coding 环境](#第一步搭建你的-vibe-coding-环境)
2. [第二步：核心方法论——怎么跟 AI 对话](#第二步核心方法论怎么跟-ai-对话)
3. [实战案例：Ds-Ai 完整开发过程](#实战案例ds-ai-完整开发过程)
4. [成果展示](#成果展示)
5. [轮到你了：开始你自己的项目](#轮到你了开始你自己的项目)
6. [部署上线：本地 → 公网](#部署上线本地--公网)
7. [商业落地检查清单](#商业落地检查清单)
8. [参考附录](#参考附录)

---

## 第一步：搭建你的 Vibe Coding 环境

> ⏱️ **耗时**：首次约 30 分钟

### 1.1 需要准备什么

| 工具 | 说明 | 获取 |
|------|------|------|
| **Claude Code** | AI 编程终端，负责读写文件、执行命令、操作 Git | `npm install -g @anthropic-ai/claude-code` |
| **DeepSeek API Key** | 大模型，性价比极高——**10 元够开发 3-5 个这样规模的项目** | [platform.deepseek.com](https://platform.deepseek.com) |
| **Node.js ≥ 18** | Claude Code 的运行环境 | [nodejs.org](https://nodejs.org) |

### 1.2 安装

```bash
node -v                              # 确认 ≥ 18
npm install -g @anthropic-ai/claude-code
claude --version                     # 验证
```

### 1.3 接入 DeepSeek

DeepSeek 提供了与 Anthropic **完全兼容的 API 端点**，只需配置环境变量即可让 Claude Code 底层使用 DeepSeek 模型，无需任何第三方适配层。

> 📖 官方配置指南：[api-docs.deepseek.com/zh-cn/quick_start/agent_integrations/claude_code](https://api-docs.deepseek.com/zh-cn/quick_start/agent_integrations/claude_code)

**Step 1：获取 API Key**

1. 注册 [platform.deepseek.com](https://platform.deepseek.com)
2. 进入「API Keys」→ 创建 Key → 复制保存
3. 充值 10 元（够开发 3-5 个这样规模的项目）

**Step 2：配置 `~/.claude/settings.json`**

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

**Windows 用户也可用 PowerShell 设置环境变量**（临时生效，关窗口失效）：

```powershell
$env:ANTHROPIC_BASE_URL="https://api.deepseek.com/anthropic"
$env:ANTHROPIC_AUTH_TOKEN="你的DeepSeek API Key"
$env:ANTHROPIC_MODEL="deepseek-v4-pro"
$env:ANTHROPIC_DEFAULT_OPUS_MODEL="deepseek-v4-pro"
$env:ANTHROPIC_DEFAULT_SONNET_MODEL="deepseek-v4-pro"
$env:ANTHROPIC_DEFAULT_HAIKU_MODEL="deepseek-v4-flash"
$env:CLAUDE_CODE_SUBAGENT_MODEL="deepseek-v4-pro"
$env:API_TIMEOUT_MS="600000"
```

> 💡 **模型分工**：`deepseek-v4-pro` 处理复杂代码和深度推理；`deepseek-v4-flash` 处理快速问答和文件读取等轻量操作。
>
> 💡 **超时设置**：V4-Pro 在 max effort 模式下推理耗时较长，`API_TIMEOUT_MS` 建议设 600000（10 分钟），避免长任务超时中断。
>
> 💡 **成本**：相比 Claude Opus 原生 API，节省 **90%+**。本项目全程用 DeepSeek，总 API 花费不到 5 元。

**Step 3：验证**

```bash
claude
# 输入 /status，确认 model 显示为 deepseek-v4-pro
# 或直接问"你是什么模型"，应回答 DeepSeek V4 Pro
```

> ⚠️ **注意**：DeepSeek V4-Pro 是纯文本模型，不支持图片输入（截图/设计稿无法识别）。如需图片理解能力，需切换到 Claude 原生模型。

### 1.4 创建 CLAUDE.md——给 AI 的"员工手册"

在项目根目录放一个 `CLAUDE.md`，AI 每次对话都会先读它。**这是让 AI 写出符合你标准的代码的最有效手段。**

```markdown
# 全局技术规范

## 语言与输出
1. 注释和文档用简体中文
2. 代码、变量名严格英文

## Java 规范
- 类名 PascalCase，方法名 camelCase，缩进 4 空格
- 禁止 System.out，用 SLF4J
- 禁止 foreach 内增删元素

## 工程结构
- Controller → Service → Mapper → Entity 严格分层
- 禁止凭空编造类和方法，必须基于真实项目
```

> 📄 完整版：[CLAUDE.md](./CLAUDE.md)，可直接复制改造成你自己的规范。
>
> 💡 **迭代技巧**：不用一次写全。每次发现 AI 不按你的习惯来，就加一条规则。3-5 轮后，AI 的代码风格跟你手写的一模一样。

### 1.5 验证

```bash
cd your-project
claude
# > 帮我创建一个 Spring Boot 项目基础结构
```

AI 正确生成文件 → 环境就绪。接下来进入核心——**怎么说话才能让 AI 输出最好的代码**。

---

## 第二步：核心方法论——怎么跟 AI 对话

> ⏱️ **阅读 15 分钟** | 这是本文档最重要的章节

### 2.1 第一条铁律：描述需求，不描述实现

Vibe Coding 的精髓——**你只需要告诉 AI "我要什么效果"，不需要告诉它"用什么技术实现"。**

| ❌ 不要这样说 | ✅ 这样说 | 原因 |
|------------|---------|------|
| "用策略模式实现多模型切换" | "支持 DeepSeek 和 OpenAI，以后可能加更多，切换要方便" | AI 自己选了策略模式 + 抽象工厂，比大多数中级程序员选得好 |
| "建 ai_message 表，字段 id, content, role..." | "每条聊天消息都要存下来，包括谁发的、内容是什么、什么时候发的" | AI 设计表结构不会漏字段，而且会自动加索引 |
| "加个拦截器校验 token" | "没登录的人访问管理接口要返回 401" | AI 会选最合适的安全实现方式 |

**一句话**：你管 **What & Why**，AI 管 **How**。

### 2.2 四种对话模式

#### 模式一：从零冷启动

```
你：帮我创建一个 Spring Boot + Vue 前后端分离的后台管理系统。
   要能用户登录、角色权限管理、菜单管理。
   数据库 MySQL，缓存 Redis。

AI 产出：
  - Maven 项目骨架（pom.xml + Application 主类）
  - 9 张 RBAC 表（user / role / menu / dept / post / dict / config / notice / job）
  - Spring Security + JWT 认证
  - 16 个 REST 控制器
  - Vue 管理后台（登录页 + 布局 + 各管理页面）
  - schema.sql + data.sql（含默认管理员账号）
```

> 🔑 **冷启动公式**：项目类型 + 技术栈 + 3-5 个核心功能。只说这些就够了，不要展开细节。

#### 模式二：增量迭代（最常用）

```
第一轮："给系统加 AI 对话，用 DeepSeek 模型，流式输出"
  → AI：AiChatService + AiChatController + ChatView.vue

第二轮："再加 OpenAI"
  → AI：发现代码重复，自动抽了 AbstractOpenAIProvider

第三轮："再加通义千问、智谱"
  → AI：建了 ModelProviderFactory，新模型只需要 30 行代码

第四轮："还要 Moonshot"
  → 直接复用，1 分钟
```

> 🔑 **一次只加一个功能。commit 后再加下一个。**

#### 模式三：横切修改

```
你：所有错误提示改成中英文双语，前端要能切换语言

AI 自动：
  1. 扫描 31 个 Controller，找到所有硬编码中文
  2. 新建 sys_error_code 表 + ErrorCodeService
  3. 逐个替换为双语错误码
  4. 30+ 个 Vue 组件加 vue-i18n
  5. Swagger 注解同步更新
```

> 🔑 **明确范围**（"全部"还是"只改 Controller"）+ **目标效果**（"中英文双语"）。

#### 模式四：探索式任务

```
你：写个一键启动脚本，自动检测 MySQL/Redis，让用户选启动哪些服务

AI：start-all.sh（240 行）→ 端口检测 + 服务拉起 + 交互菜单 + Cloudflare Tunnel
```

> 🔑 **描述用户交互流程**，技术细节 AI 自己搞定。

### 2.3 提示词通用公式

```
[场景] + [目标] + [约束] + [效果]

"给这个 Spring Boot 项目（场景）
 加上 AI 对话功能（目标）
 用 SSE 流式输出，模型可切换，API Key 放配置文件（约束）
 前端打字机效果，像 ChatGPT（效果）"
```

### 2.4 踩坑速查表

| 问题 | 解法 |
|------|------|
| AI 一次改太多，结果半对半错 | 拆成 2-3 个需求一轮，commit 后再继续 |
| AI 用了不存在的类/方法 | CLAUDE.md 加"禁止编造类和方法" |
| AI 理解错了需求 | `git reset --hard` 回退，换种说法重新描述 |
| 代码编译报错 | 把错误信息直接贴给 AI："这个报错，帮我修" |
| 风格不一致 | CLAUDE.md 补充偏好，下一轮 AI 就统一 |

### 2.5 迭代节奏（跟着这个节奏走）

```
第 1 轮：MVP — 核心流程跑通，功能对就行，不求完美
第 2 轮：增强 — 搜索、筛选、导出、分页等辅助功能
第 3 轮：体验 — Loading 状态、错误提示、空数据占位、响应式适配
第 4 轮：国际化 — 如果要做多语言，这轮统一加
第 5 轮：工程化 — .gitignore、启动脚本、环境变量、README
```

> 🎯 **每轮不超过 30 分钟，每轮结束 git commit。**

---

## 实战案例：Ds-Ai 完整开发过程

> 以下是本项目真实的 15 轮对话复盘。展示了 Vibe Coding 的完整流程。

### 开发全景

```
项目：Ds-Ai 多模块 AI 聊天平台
工期：不到 2 天
对话：15 轮，每轮 20-40 分钟
API 费用：< 5 元（DeepSeek）
产出：后端 86 个 Java 类 + 前端 50+ 组件 + 30 张表 + 4 份文档
```

### 提示词参考了哪些文档

在每一轮对话中，我**只提供自然语言需求描述和少量参考文档片段作为提示词**，没有任何脚手架代码或项目模板。AI 自行完成了所有架构设计和技术选型。

| 开发阶段 | 参考的文档（片段） | 用途 |
|---------|-----------------|------|
| RBAC 权限体系 | RuoYi-Vue README（功能列表描述部分） | 告诉 AI "一个完整的后台管理系统应该有哪些功能"，AI 据此反推表结构和代码 |
| AI 模型接入 | DeepSeek API 文档（Chat Completions 接口说明） | 告诉 AI "API 的 URL、请求格式、SSE 流式格式"，AI 自动生成 OkHttp 调用代码 |
| 管理后台 UI | Element UI / Element Plus 组件文档 | 告诉 AI "表单用这个组件、表格用那个组件"，AI 自动拼出完整页面 |
| 编码规范 | CLAUDE.md（本项目根目录） | 全程约束 AI 的代码风格——命名、分层、注释语言 |

> 🔑 **关键认知**：你不需要给 AI 完整文档，只需要给**相关片段**。比如"这是 DeepSeek 的 SSE 响应格式：`data: {"choices":[{"delta":{"content":"你好"}}]}`"，AI 就能自己写出完整的流式解析代码。

### 逐轮对话实录

| 轮次 | 我对 AI 说的话 | AI 的产出 | 耗时 |
|------|-------------|---------|------|
| 1 | "创建 Spring Boot + Vue 后台管理系统，完整 RBAC 权限" | Maven 骨架 + 9 张表 + Security/JWT + 16 个 Controller + Vue 后台 | 40min |
| 2 | "清理编译产物，配 .gitignore" | .gitignore + 清理 Git 追踪 | 5min |
| 3-12 | "加 AI 对话：多模型、SSE 流式、会话管理、知识库、助手模板"（10 轮增量） | AiChatService + 策略模式 Provider 体系 + 7 张 AI 表 + SSE + Chat 组件 | 6h |
| 13 | "加 RAG 检索增强、国际化、收藏分享、AI 管理后台" | rag/ 包（10 文件）+ vue-i18n + 收藏/分享 + 模型管理界面 | 2h |
| 14 | "Vite 允许局域网访问" | 2 行配置 | 3min |
| 15 | "一键启动脚本，自动检测 MySQL/Redis，支持公网穿透" | start-all.sh(240行) + stop-all.sh + start-services.bat | 30min |

### AI 自主选择的架构

注意——以下设计全是 AI 自己选的，我从未指定过任何技术方案：

| 场景 | AI 选的设计 | 为什么 |
|------|----------|--------|
| 多 AI 模型 | 策略模式 + 抽象工厂 | 加新模型只需 30 行 |
| RAG 向量存储 | 接口抽象（内存 / PgVector 双实现） | 开发零配置，生产切 pgvector |
| 流式输出 | SSE（非 WebSocket） | 够用且更简单 |
| 前端状态 | Pinia（Vue3）/ Vuex（Vue2） | 按版本自动匹配 |

> 👆 **这就是 Vibe Coding 的威力：不懂策略模式，也能得到策略模式的代码。**

### 效率数据

| 模块 | 手写预估 | AI 实际 | 倍数 |
|------|---------|--------|------|
| 后端（86 类） | 6 周 | 不到 2 天 | ×20+ |
| 前端（50+ 组件） | 4 周 | 不到 1 天 | ×30+ |
| 数据库（30 表） | 3 天 | 2 小时 | ×12 |
| 文档（5000+ 行） | 1 周 | 2 小时 | ×20+ |
| **总计** | **2-3 个月** | **不到 2 天** | **×30+** |

---

## 成果展示

> 👇 这就是不到 2 天 Vibe Coding 的产出——一个可直接部署的 AI 聊天平台。

| 子项目 | 定位 | 技术栈 | 端口 |
|--------|------|--------|------|
| **spring-boot-admin** | 后端 API | Spring Boot 2.7 + MyBatis + Redis + MySQL 8.0 | `8090` |
| **ds_ai_web** | AI 用户端 | Vue 3.5 + Element Plus + Pinia + Vite | `5174` |
| **vue-admin** | 管理后台 | Vue 2.7 + Element UI + Vuex + Vite | `5173` |

**30 项功能**：RBAC 系统管理(8) + 系统监控(6) + 系统工具(3) + AI 对话/RAG/搜索/文档/知识库/收藏/分享/国际化(13)

> 详见 [技术文档](./项目技术文档.md)。

**内置账号**：管理员 `admin/admin123` · 普通用户 `user/user123`

---

## 轮到你了：开始你自己的项目

看到这里，你已经具备了所有理论知识。现在动手。

### 你的第一个 Vibe Coding 任务

```bash
# 1. 创建项目目录
mkdir my-first-ai-project && cd my-first-ai-project

# 2. 创建 CLAUDE.md（复制本项目的改一改）
# 3. 启动 Claude Code
claude

# 4. 说出你的第一句话（选一个）：
```

**如果你有明确的项目想法**：
> "帮我创建一个 [XX 类型] 项目，技术栈用 [Spring Boot / Vue / Python / ...]，核心功能是 [A、B、C]"

**如果你只是想试试**：
> "帮我创建一个简单的任务管理 Web 应用，能添加任务、标记完成、按日期筛选。后端用 Spring Boot，前端用 Vue 3"

**如果你完全没想法**：
> "帮我创建一个个人博客系统，支持文章发布、分类标签、评论功能。前后端分离。"

### 你的第一个迭代

第一轮对话后，AI 会生成项目骨架。接下来：

```bash
git init && git add -A && git commit -m "init: MVP"  # 先保存

# 然后继续对话：
"帮我加个搜索功能"
"列表页加个分页"
"登录页太丑了，优化一下 UI"
```

> 🎯 **记住**：每次只加一个功能，commit 后再加下一个。

---

## 部署上线：本地 → 公网

### 环境要求

JDK ≥ 1.8 · MySQL ≥ 5.7 · Redis ≥ 3.0 · Maven ≥ 3.0 · Node.js ≥ 16

### 本地启动

```bash
# 建库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ai_test DEFAULT CHARACTER SET utf8mb4;"

# 一键启动
bash start-all.sh

# 访问
# AI 用户端 → http://localhost:5174
# 管理后台 → http://localhost:5173
# Swagger  → http://localhost:8090/doc.html
```

### 公网访问（演示/给客户看）

| 方案 | 费用 | 难度 | 场景 |
|------|------|------|------|
| **Cloudflare Tunnel** | 免费 | ⭐ 脚本已集成 | 临时演示 |
| **ngrok** | 免费 | ⭐ 2 条命令 | 快速分享 |
| **frp** | 需服务器 | ⭐⭐⭐ | 长期上线 |

**Cloudflare Tunnel（推荐，本项目已集成）**：

```bash
# 1. 下载 cloudflared.exe
# https://github.com/cloudflare/cloudflared/releases → 放到用户目录

# 2. 启动选公网模式
bash start-all.sh
# → 第 4 项选 y

# 3. 得到公网地址
# ds_ai_web: https://xxxx.trycloudflare.com
```

---

## 商业落地检查清单

AI 写的代码能跑，但能不能拿来卖钱？上线前逐项检查：

| # | 检查项 | 通过标准 |
|---|--------|---------|
| 1 | 🔐 **认证鉴权** | 未登录用户访问任何 /api/** 返回 401，普通用户不能访问管理接口 |
| 2 | 🔒 **密码安全** | 数据库中密码字段为 BCrypt 密文，不可逆 |
| 3 | 🚫 **XSS 防护** | 输入 `<script>alert(1)</script>` 不会被浏览器执行 |
| 4 | ⏱️ **登录限流** | 同一 IP 连续 5 次输错密码后暂时无法登录 |
| 5 | 📝 **操作审计** | 每个增删改操作都能在操作日志中查到（谁、什么时候、做了什么） |
| 6 | 🖼️ **UI 体验** | 有 Loading 状态、错误提示、空数据提示；手机端布局不崩 |
| 7 | 🌐 **CORS** | 只有白名单域名能跨域调用 API，不是 `*` |
| 8 | 📖 **Swagger 保护** | 生产环境 Swagger UI 关闭或需要登录才能访问 |

> 💡 以上 8 点，本项目在开发过程中已逐项让 AI 实现了。你可以直接对照检查自己的项目。

---

## 参考附录

### 项目结构

```
IdeaProjects/
├── spring-boot-admin/     # 后端 Spring Boot
│   └── src/main/java/com/example/admin/
│       ├── controller/    # 31 个 Controller
│       ├── service/model/ # AI 模型策略模式
│       ├── rag/           # RAG 引擎
│       ├── mapper/        # 20 个 MyBatis Mapper
│       └── entity/        # 22 个实体
├── ds_ai_web/             # AI 用户端 Vue 3
├── vue-admin/             # 管理后台 Vue 2
├── CLAUDE.md              # ⭐ AI 编码规范
├── start-all.sh           # 一键启动
└── stop-all.sh            # 一键停止
```

### 相关文档

| 文档 | 说明 |
|------|------|
| [CLAUDE.md](./CLAUDE.md) | AI 编码规范——可直接复制用 |
| [技术文档](./项目技术文档.md) | 架构、数据库、运维详解 |
| [API 文档](./spring-boot-admin/API.md) | 全部接口 |
| [架构文档](./spring-boot-admin/ARCHITECTURE.md) | 认证流程、数据流 |

---

## 开源协议

[MIT](https://opensource.org/licenses/MIT)

---

> 🎯 **Vibe Coding 不是一个概念——它是一个已经验证过的、可以复制的开发范式。**
>
> 这个项目就是证据：**不到 2 天 + 5 块钱 = 一个可商业落地的完整 Web 系统。**
>
> 现在轮到你了——[搭建环境](#第一步搭建你的-vibe-coding-环境)，说出你的第一句提示词。
>
> ⭐ Star · 🔱 Fork · 💬 Issue
