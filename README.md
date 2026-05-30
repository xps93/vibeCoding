# 用 AI 快速开发 Web 项目 — 从零到上线的完整教程

> 🎯 **这不是一个普通的开源项目，而是一份 "如何用 AI 写代码" 的操作手册。**
>
> 本项目（一个功能完备的 AI 聊天平台：30+ 功能模块、86 个 Java 类、300+ 前端组件）**100% 由 AI 生成，人类零手写代码**。
> 更重要的——本文档把每一步的对话技巧、踩坑经验、方法论都记录了下来，让你也能复制同样的开发效率。

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://gitee.com/xp_s/vibe_coding)
[![Spring Boot 2.7](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue-3.5-4FC08D.svg)](https://vuejs.org/)
[![AI Built](https://img.shields.io/badge/100%25-AI%20Built-blueviolet.svg)](https://claude.ai/code)

---

## 这份教程能让你学会什么

读完并跟着操作一遍，你将掌握：

| 技能 | 具体你会做什么 |
|------|--------------|
| 🤖 **AI 编程环境搭建** | 安装 Claude Code + DeepSeek，5 分钟内拥有自己的 AI 程序员 |
| 📝 **写出高效的 AI 提示词** | 从"帮我写个系统"到"给 XX 模块加个 YY 功能"，学会让 AI 精准理解你的意图 |
| 🏗️ **AI 驱动的架构设计** | 不需要自己画类图、选设计模式——告诉 AI 需求，它自己选策略模式还是工厂模式 |
| 🔄 **迭代式 AI 开发流程** | MVP 先行 → 逐层加功能 → AI 自动重构，每一步都 commit，出了问题秒回滚 |
| 🌐 **从本地到公网** | 一键脚本启动 + 内网穿透，把本地项目变成可公网访问的在线服务 |

> 🏆 **最终成果预览**：花 2 周业余时间（每天 1-2 小时和 AI 对话），得到一个完整的前后端分离 Web 系统——RBAC 权限 + AI 对话 + RAG 检索 + 国际化 + 管理后台，可直接部署上线。

---

## 目录

1. [第一步：搭建你的 AI 编程环境](#第一步搭建你的-ai-编程环境)
2. [第二步：学会跟 AI 对话（核心方法论）](#第二步学会跟-ai-对话核心方法论)
3. [实战案例：Ds-Ai 的完整开发过程](#实战案例ds-ai-的完整开发过程)
4. [成果展示：项目能做什么](#成果展示项目能做什么)
5. [部署上线：本地 → 公网](#部署上线本地--公网)
6. [参考附录](#参考附录)

---

## 第一步：搭建你的 AI 编程环境

> ⏱️ **耗时**：首次约 30 分钟

### 1.1 你需要什么

| 工具 | 说明 | 获取方式 |
|------|------|---------|
| **Claude Code** | AI 编程终端，负责读写文件、执行命令 | `npm install -g @anthropic-ai/claude-code` |
| **DeepSeek API Key** | 大模型服务，性价比极高（10 元够开发一个完整项目） | [platform.deepseek.com](https://platform.deepseek.com) |
| **Node.js ≥ 18** | Claude Code 的运行环境 | [nodejs.org](https://nodejs.org) |

### 1.2 安装 Claude Code

```bash
# 确认 Node.js 版本
node -v   # 需要 ≥ 18

# 全局安装
npm install -g @anthropic-ai/claude-code

# 验证
claude --version
```

### 1.3 接入 DeepSeek（省钱方案）

Claude Code 默认使用 Claude API，但支持接入第三方模型。DeepSeek 的价格约为 Claude 的 1/20，效果足够好。

**获取 Key**：
1. 注册 [DeepSeek 开放平台](https://platform.deepseek.com)
2. 进入「API Keys」→ 创建 Key → 复制保存
3. 充值 10 元（够本项目这种体量的项目开发 3-5 个）

**配置 Claude Code**（具体方式随版本更新，请以 [官方文档](https://docs.anthropic.com/en/docs/claude-code) 为准）：

```json
// ~/.claude/settings.json
{
  "model": "deepseek-v4-pro"
}
```

> 💡 本项目全程使用 DeepSeek，总 API 花费不到 5 元。

### 1.4 给 AI 定规矩：创建 CLAUDE.md

在项目根目录放一个 `CLAUDE.md`，AI 每次对话都会先读它。**这是保证 AI 写出你想要的代码风格的核心技巧。**

```markdown
# 全局技术规范

## 语言与输出
1. 注释和文档用简体中文
2. 代码、变量名严格英文

## Java 规范
- 类名 PascalCase，方法名 camelCase
- 缩进 4 空格，单行 ≤ 120
- 禁止 System.out，用 SLF4J

## 工程结构
- 分层：Controller → Service → Mapper → Entity
- 禁止凭空编造类和方法
```

> 📄 完整版参考本项目的 [CLAUDE.md](./CLAUDE.md)，可以直接复制改造。
>
> 💡 **技巧**：CLAUDE.md 不需要一次写全。每次发现 AI 不按你的习惯来，就加一条进去。迭代 3-5 轮后，AI 的代码风格会和你手写的一模一样。

### 1.5 验证环境

```bash
cd your-project
claude
```

看到 Claude Code 的对话界面后，输入：

```
> 帮我创建一个 Spring Boot 项目的基础结构，包含 pom.xml 和 Application 主类
```

如果 AI 正确生成了文件，环境就搭好了。接下来进入核心——**怎么跟 AI 说话才能得到最好的结果**。

---

## 第二步：学会跟 AI 对话（核心方法论）

> ⏱️ **阅读**：15 分钟 | 这是本文档最重要的章节

AI 编程的本质不是"AI 替代程序员"，而是**你变成架构师+产品经理，AI 变成执行层**。你只需要描述"要什么"，AI 负责"怎么做"。

### 2.1 核心原则：描述需求，不描述实现

| ❌ 错误说法 | ✅ 正确说法 | 为什么 |
|-----------|-----------|--------|
| "用策略模式实现多模型切换" | "支持 DeepSeek 和 OpenAI 两个模型，以后可能还会加更多，切换要方便" | AI 自己会选策略模式，而且可能比你选的更合适 |
| "建一个 ai_message 表，字段有..." | "对话中的每条消息都要存下来，包含发送者、内容、时间" | AI 会自己设计表结构，不会漏字段 |
| "在 controller 层加个拦截器" | "所有管理员接口要做权限校验，没权限返回 403" | AI 会选择最合适的实现方式 |

**一句话总结**：你管"做什么"和"为什么"，AI 管"怎么做"。

### 2.2 四种对话模式（按场景选择）

#### 模式一：从零搭建（新项目起步）

```
你：帮我创建一个 Spring Boot + Vue 前后端分离的后台管理系统，
   要有用户登录、角色权限管理、菜单管理。
   数据库用 MySQL，缓存用 Redis。

AI 会：
  1. 创建完整的 Maven 项目结构
  2. 设计 9 张 RBAC 表（user、role、menu、dept...）
  3. 编写 Security 配置 + JWT 认证
  4. 生成 10+ 个 REST 控制器
  5. 用 Vue 搭建管理后台页面
  6. 输出 schema.sql + data.sql 初始化脚本
```

> 🔑 **要点**：第一次描述要说清楚三件事——**项目类型**（管理系统/电商/博客）、**核心技术栈**（Spring Boot/Vue/Python）、**核心功能**（3-5 个最关键的）。

#### 模式二：增量迭代（日常开发最常用）

```
第一轮：给系统加 AI 对话功能，支持 DeepSeek 模型，流式输出

AI 写了 AiChatService + AiChatController + 前端 ChatView 组件

第二轮：再加 OpenAI 模型

AI 发现两套代码很像，自动抽了 AbstractOpenAIProvider 抽象类

第三轮：再加通义千问、智谱

AI 建了 ModelProviderFactory 工厂，新模型只需写 30 行

第四轮：还要 Moonshot

直接复用，1 分钟搞定
```

> 🔑 **要点**：**一次只加一个功能，commit 后再加下一个**。AI 擅长增量修改，但一次性描述 5 个需求容易出错。

#### 模式三：横切修改（国际化、日志、安全加固）

```
你：把系统所有错误提示改成中英文双语，前端也要能切换语言

AI 自动：
  1. 扫描全部 31 个 Controller，找到所有硬编码错误消息
  2. 新建 sys_error_code 表和 ErrorCodeService
  3. 逐个替换为双语错误码调用
  4. 前端 30+ 组件添加 vue-i18n
  5. Swagger 注解同步更新
  6. 管理后台新增错误码管理界面
```

> 🔑 **要点**：横切需求要**明确范围**（"全部"还是"只改 Controller 层"）和**目标效果**（"中英文双语"）。

#### 模式四：探索式任务（工具脚本、配置调优）

```
你：帮我写个一键启动脚本，要能自动检测 MySQL 和 Redis 是否在运行，
   没运行的话试试启动它们，然后让用户选择启动哪些服务

AI：写出 start-all.sh（240 行 Bash），包含：
  - 端口占用检测（netstat）
  - 服务自动拉起（MySQL + Redis）
  - 交互式菜单（后端/管理后台/AI前端/公网穿透，四选多）
  - Cloudflare Tunnel 集成
  - 结果汇总展示
```

> 🔑 **要点**：运维类任务**描述用户交互流程**，不需要说技术细节。AI 会自己查命令、找路径。

### 2.3 高质量提示词的通用公式

```
角色/场景 + 目标功能 + 约束条件 + 期望效果

示例：
"给这个 Spring Boot 项目（角色）
 添加 AI 对话功能（目标）
 用 SSE 流式输出，模型可以切换，API Key 放配置文件里（约束）
 前端打字机效果，像 ChatGPT 那样（期望效果）"
```

### 2.4 常见踩坑与解法

| 问题 | 现象 | 解法 |
|------|------|------|
| **AI 一次改太多** | 10 个需求一起提，结果 5 个对、3 个半对、2 个错 | 拆成 2-3 个需求一轮，每次 commit 后再继续 |
| **AI 凭空编造类** | 引用了不存在的 `XxxUtil` 或 `YyyConfig` | CLAUDE.md 里加一句"禁止编造类和方法，必须基于真实项目" |
| **代码风格不一致** | 有时用 Stream，有时用 for 循环 | CLAUDE.md 里补充风格偏好，AI 下一轮就会统一 |
| **AI 错误理解需求** | 生成了跟你预期完全不同的东西 | 不要让它继续改，直接 `git reset --hard` 回退，换个说法重新描述 |
| **生成代码编译报错** | 引用了错误的包名或 API | 把错误信息直接贴给 AI："这个编译报错了，帮我修复" |

### 2.5 迭代节奏建议

```
第一轮（MVP）：    跑通核心流程，不求完美
第二轮（增强）：    加辅助功能（搜索、导出、筛选）
第三轮（体验）：    加 Loading 状态、错误提示、空数据占位
第四轮（国际化）：   如果要做多语言，这轮统一加
第五轮（工程化）：   .gitignore、启动脚本、环境变量、README
```

> 🎯 **黄金法则**：每轮对话不超过 30 分钟，每轮结束 `git commit`。这样出了问题永远可以 `git diff` 看 AI 改了什么。

---

## 实战案例：Ds-Ai 的完整开发过程

> 以下展示的是本项目真实的开发过程——每一次 commit 对应的对话内容、AI 做了什么、以及为什么这样做。

### 3.1 开发全景

```
项目：Ds-Ai 多模块 AI 聊天平台
工期：2 周（业余时间，每天 1-2 小时对话）
对话次数：约 15 轮
每轮耗时：20-40 分钟
API 费用：< 5 元（DeepSeek）
代码量：后端 86 个 Java 类 + 前端 50+ 组件 + 30 张数据库表
```

### 3.2 逐轮复盘

| 轮次 | 我对 AI 说的话 | AI 产出 | 耗时 |
|------|-------------|--------|------|
| 1 | "创建一个 Spring Boot + Vue 后台管理系统，参考业界最佳实践，要有完整的 RBAC 权限" | Maven 项目骨架 + 9 张 RBAC 表 + Security/JWT + 16 个 Controller + Vue 管理后台 | 40 min |
| 2 | "清理编译产物和 IDE 临时文件，配置 .gitignore" | `.gitignore` + 清理无效追踪 | 5 min |
| 3-12 | "加 AI 对话——多模型、SSE 流式、会话管理、知识库、助手模板"（分 10 轮迭代） | `AiChatService` + 策略模式 Provider 体系 + 7 张 AI 表 + SSE 流式 + 前端 Chat 组件 | 6 hr |
| 13 | "加 RAG 检索增强、国际化中英文、对话收藏分享、AI 管理后台" | `rag/` 包（10 个文件）+ `vue-i18n` + 收藏/分享功能 + AI 模型管理界面 | 2 hr |
| 14 | "Vite 配置 host 为 0.0.0.0，允许局域网访问" | 两个 `vite.config.js` 各加 2 行 | 3 min |
| 15 | "写一键启停脚本，要自动检测 MySQL/Redis、支持公网穿透" | `start-all.sh`(240 行) + `stop-all.sh` + `start-services.bat` | 30 min |

### 3.3 AI 自动选择的架构（无需人工指定）

| 场景 | AI 选的设计 | 为什么选这个 |
|------|----------|------------|
| 多 AI 模型切换 | 策略模式 + 抽象工厂 | 扩展新模型只需加 30 行的 Provider 类 |
| RAG 向量存储 | 接口抽象（内存 / PgVector） | 开发用内存版零配置，生产切 pgvector 一行配置 |
| 前端状态管理 | Pinia（Vue 3）/ Vuex（Vue 2） | 两个前端用了不同方案，AI 根据 Vue 版本自动匹配 |
| 流式输出 | SSE（Server-Sent Events） | 比 WebSocket 简单，够用，Spring Boot 原生支持 |

> 👆 **这就是"描述需求不描述实现"的价值**——AI 选的和资深工程师选的一致，但你不需要懂策略模式。

### 3.4 效率对比（真实数据）

| 模块 | 传统手写 | AI 驱动 | 倍数 |
|------|---------|--------|------|
| 后端 Java（86 类） | ~6 周 | ~5 天 | ×8 |
| 前端 Vue（50+ 组件） | ~4 周 | ~3 天 | ×9 |
| 数据库（30 张表） | ~3 天 | ~2 小时 | ×12 |
| 文档（5000+ 行） | ~1 周 | ~1 天 | ×5 |
| **总计** | **2-3 个月** | **2 周** | **×5~10** |

---

## 成果展示：项目能做什么

> 👇 这就是用上面那套方法，2 周对话产出的完整项目。

### 三大模块

| 子项目 | 定位 | 技术栈 | 端口 |
|--------|------|--------|------|
| **spring-boot-admin** | 后端 API | Spring Boot 2.7 + MyBatis + Redis + MySQL 8.0 | `8090` |
| **ds_ai_web** | AI 用户端 | Vue 3.5 + Element Plus + Pinia + Vite | `5174` |
| **vue-admin** | 管理后台 | Vue 2.7 + Element UI + Vuex + Vite | `5173` |

### 功能一览

**系统管理（RBAC）**：用户/角色/菜单/部门/岗位/字典/参数/通知公告 — 8 项
**系统监控**：操作日志/登录日志/在线用户/定时任务/服务监控/缓存监控 — 6 项
**系统工具**：代码生成器/API 文档(Swagger)/在线构建器 — 3 项
**AI 能力**：多模型对话(SSE)/RAG 检索/联网搜索/文档解析/助手模板/知识库/收藏/分享/消息评价/站点配置 — 13 项

> 共 30 项功能，详见 [技术文档](./项目技术文档.md)。

### 技术栈速览

| 层 | 技术 |
|----|------|
| 后端 | Java 8 · Spring Boot 2.7 · Spring Security · MyBatis · MySQL · Redis · OkHttp · Apache Tika |
| AI 前端 | Vue 3.5 · Element Plus · Pinia · Vite · marked · highlight.js · vue-i18n |
| 管理前端 | Vue 2.7 · Element UI · Vuex · Vite |

### 内置账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | `admin` | `admin123` |
| 普通用户 | `user` | `user123` |

---

## 部署上线：本地 → 公网

### 5.1 系统需求

| 环境 | 版本 |
|------|------|
| JDK | ≥ 1.8 |
| MySQL | ≥ 5.7（建议 8.0）|
| Redis | ≥ 3.0 |
| Maven | ≥ 3.0 |
| Node.js | ≥ 16 |

### 5.2 本地运行

```bash
# 1. 准备数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ai_test DEFAULT CHARACTER SET utf8mb4;"

# 2. 一键启动（交互式选择要启动的服务）
bash start-all.sh

# 3. 访问
# AI 用户端：http://localhost:5174
# 管理后台：http://localhost:5173
# Swagger：  http://localhost:8090/doc.html
```

### 5.3 公网访问（给别人演示）

本地只能自己看。要让别人也能访问，用内网穿透：

| 方案 | 费用 | 难度 | 适合 |
|------|------|------|------|
| **Cloudflare Tunnel** | 免费 | ⭐ 脚本已集成 | 临时演示（推荐） |
| **ngrok** | 免费/付费 | ⭐ 2 条命令 | 快速分享 |
| **frp** | 需服务器 | ⭐⭐⭐ 灵活 | 长期部署 |
| **ZeroTier** | 免费 | ⭐⭐ 安全 | 个人多设备 |

**Cloudflare Tunnel（一键）**：

```bash
# 1. 下载 cloudflared.exe 到用户目录
# https://github.com/cloudflare/cloudflared/releases

# 2. 启动时选"公网穿透"
bash start-all.sh
# 第 4 项选 y

# 3. 脚本自动输出公网地址
# ds_ai_web:  https://xxxx.trycloudflare.com
```

> 📖 其他方案（ngrok/frp/ZeroTier）的详细配置见 [技术文档](./项目技术文档.md) 第 7 节。

---

## 参考附录

### 项目结构

```
IdeaProjects/
├── spring-boot-admin/          # 后端（Spring Boot）
│   └── src/main/java/com/example/admin/
│       ├── controller/         # 31 个 REST 控制器
│       ├── service/model/      # AI 模型提供者（策略模式）
│       ├── rag/                # RAG 检索引擎
│       ├── mapper/             # 20 个 MyBatis Mapper
│       └── entity/             # 22 个实体
├── ds_ai_web/                  # AI 用户端（Vue 3）
├── vue-admin/                  # 管理后台（Vue 2）
├── CLAUDE.md                   # AI 编码规范 ⭐
├── start-all.sh                # 一键启动
└── stop-all.sh                 # 一键停止
```

### 相关文档

| 文档 | 说明 |
|------|------|
| [CLAUDE.md](./CLAUDE.md) | 给 AI 看的编码规范（可以直接复制用） |
| [项目技术文档](./项目技术文档.md) | 架构设计、数据库、部署运维详解 |
| [API 文档](./spring-boot-admin/API.md) | 全部接口说明 |
| [架构文档](./spring-boot-admin/ARCHITECTURE.md) | 认证流程、数据流设计 |

### 安全机制

Bearer Token 认证 · RBAC 三级权限 · BCrypt 密码哈希 · XSS 过滤 · 登录限流（5次/分/IP） · AOP 操作审计 · CORS 白名单

---

## 开源协议

基于 [MIT](https://opensource.org/licenses/MIT) 协议开源。

---

> 🎯 **这份教程 + 这个项目，就是想证明一件事：会用 AI 的开发者，效率是不用的 5-10 倍。**
>
> 如果你也想达到这个效率——从 [第一步](#第一步搭建你的-ai-编程环境) 开始，搭建环境，然后挑一个小功能试试让 AI 帮你实现。
>
> 欢迎 ⭐ Star、🔱 Fork、💬 Issue！
