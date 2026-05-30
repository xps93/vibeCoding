# Vibe Coding 实战：零基础用 AI 搭建 Web 项目

> Vibe Coding = 你说需求，AI 写代码。本项目 30+ 功能、86 个 Java 类、300+ 前端组件，100% 由 AI 对话生成，零手写。总成本不到 2 天 + 5 块钱，传统手写同等规模需 2-3 个月。

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://gitee.com/xp_s/vibeCoding)
[![Spring Boot 2.7](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue-3.5-4FC08D.svg)](https://vuejs.org/)
[![AI Built](https://img.shields.io/badge/100%25-AI%20Built-blueviolet.svg)](https://claude.ai/code)

---

## 目录

1. [搭建 AI 编程环境](#1-搭建-ai-编程环境)
2. [怎么跟 AI 对话](#2-怎么跟-ai-对话)
3. [完整开发过程（真实记录）](#3-完整开发过程真实记录)
4. [开始你自己的项目](#4-开始你自己的项目)
5. [部署上线](#5-部署上线)
6. [参考附录](#6-参考附录)

---

## 1. 搭建 AI 编程环境

需要三样东西：**Node.js**（运行环境）、**Claude Code**（AI 编程终端）、**DeepSeek API Key**（AI 大脑，10 元够用）。

### 安装 Claude Code

打开 PowerShell（Win+R → `powershell`），依次执行：

```powershell
node -v                          # 确认 ≥ 18，没有的话去 nodejs.org 下载
npm install -g @anthropic-ai/claude-code
claude --version                 # 验证安装
```

### 配置 DeepSeek

1. 注册 [platform.deepseek.com](https://platform.deepseek.com) → API Keys → 创建 Key → 充值 10 元
2. 用记事本打开 `C:\Users\<你的用户名>\.claude\settings.json`，写入：

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
mkdir ~/test-project
cd ~/test-project
claude
# 输入 /status → 确认显示 deepseek-v4-pro
# 输入 "创建一个 test.txt，内容 Hello" → 验证 AI 能正常工作
```

配置详情参考 [DeepSeek 官方指南](https://api-docs.deepseek.com/zh-cn/quick_start/agent_integrations/claude_code)。DeepSeek 比 Claude 官方 API 便宜 90%+，本项目全程花费不到 5 元。注意它是纯文本模型，不能识别图片。

### 创建 CLAUDE.md（可选但推荐）

在项目根目录放一个 `CLAUDE.md`，AI 每次对话都会先读它。内容示例：

```markdown
# 开发规范
1. 注释和文档用简体中文，代码和变量名用英文
2. Java 类名大驼峰、方法名小驼峰、缩进 4 空格
3. 禁止 System.out，用 SLF4J
4. 分层：Controller → Service → Mapper → Entity
5. 禁止凭空编造不存在的类或方法
```

完整版见 [CLAUDE.md](./CLAUDE.md)。不用一次写全，每次发现 AI 风格不对就加一条。

---

## 2. 怎么跟 AI 对话

### 核心原则

**说需求，不说实现。** 你管"要什么效果"，AI 管"用什么技术"。

| 错误 | 正确 |
|------|------|
| "用策略模式实现多模型切换" | "支持多个 AI 模型，切换要方便，以后可能加新的" |
| "建表，字段 id、content、role" | "每条消息要存下来，知道谁发的、发的什么、什么时间" |
| "加个拦截器校验 token" | "没登录的人访问后台接口要提示登录" |

### 四类任务

**从零开始**。第一次说三件事：项目类型 + 技术栈 + 3-5 个核心功能。

```
帮我做一个任务管理网站，后端 Spring Boot、前端 Vue 3。
核心功能：新建任务、标记完成、按日期筛选。
```

**加新功能**。一次只加一个，加完 commit 再加下一个。

```
给任务管理加个标签功能，一个任务可以有多个标签，颜色可以自定义。
```

**全局修改**。说清楚是"全部"还是"只改某个页面"。

```
把所有错误提示都改成中文，前端做一个中英文切换开关。
```

**写脚本**。描述交互流程——先干嘛、再干嘛。

```
写个一键启动脚本，先检测数据库是否运行，没运行就启动，
然后让用户选择启动哪些服务，最后把访问地址打印出来。
```

### 通用公式

```
[在哪] + [干什么] + [限制条件] + [期望效果]

"这个 Spring Boot 项目，加上 AI 对话功能，用流式输出、模型可切换，
 前端要 ChatGPT 那种打字机效果。"
```

### 踩坑速查

| 问题 | 解法 |
|------|------|
| AI 改太多结果半对半错 | 拆成小需求，每次 commit 后再继续 |
| AI 编造不存在的类 | CLAUDE.md 加"禁止编造类" |
| AI 理解错了 | `git reset --hard` 回退，换说法重新描述 |
| 代码跑不起来 | 把报错信息直接贴给 AI |
| 风格不一致 | CLAUDE.md 补充规则 |

### 建议节奏

```
第 1 次：跑通核心流程（能 CRUD 就行，界面丑没关系）
第 2 次：加搜索、筛选、分页
第 3 次：优化界面（Loading / 错误提示 / 空状态 / 移动端）
第 4 次：工程收尾（.gitignore、启动脚本、文档）
```

每次对话不超过半小时，结束就 commit。

---

## 3. 完整开发过程（真实记录）

本项目 15 次对话、不到 2 天、不到 5 元，产出 86 个 Java 类 + 50+ 前端组件 + 30 张表 + 4 份文档。

### 对话实录

| # | 我说了什么 | AI 产出了什么 | 耗时 |
|---|----------|-------------|------|
| 1 | 创建 Spring Boot + Vue 后台管理系统，要有用户、角色、菜单管理 | 项目骨架 + 9 张 RBAC 表 + JWT 认证 + 16 个接口 + Vue 后台页面 | 40min |
| 2 | 清理编译文件，配 .gitignore | .gitignore + 清理 Git 追踪 | 5min |
| 3~12 | 加 AI 对话——多模型、SSE 流式、会话、知识库、助手模板（10 次，每次一个功能） | AiChatService + 5 个 Provider + 7 张 AI 表 + SSE + Chat 组件 | 6h |
| 13 | 加 RAG 检索、中英文切换、收藏分享、AI 管理后台 | rag/ 包（10 文件）+ vue-i18n + 收藏/分享 + 模型管理界面 | 2h |
| 14 | Vite 允许局域网访问 | 2 行配置 | 3min |
| 15 | 一键启动脚本，检测 MySQL/Redis，支持公网穿透 | start-all.sh（240 行）+ stop-all.sh + bat | 30min |

### 参考了哪些文档

每次对话只给 AI **自然语言需求 + 少量文档片段**，没有给任何现成代码。

| 对话主题 | 参考的片段 | AI 做了什么 |
|---------|-----------|-----------|
| RBAC 权限 | 一份后台管理系统的功能列表 | 自己设计 9 张表、JWT 认证、16 个接口 |
| AI 对话 | DeepSeek API 的请求/响应格式（几行 JSON） | 自己写 OkHttp 调用、SSE 解析、前端打字机 |
| 后台界面 | Element UI 组件列表 | 自己拼出完整 CRUD 页面 |
| 全流程 | CLAUDE.md | 全程遵守命名规范、分层架构 |

### AI 自主选择的设计

以下决策我一句没指定，AI 理解需求后自己选的：多模型切换用策略模式+抽象工厂、RAG 向量存储用接口抽象双实现、流式输出选 SSE（非 WebSocket）、两个 Vue 版本自动匹配不同状态管理方案。

### 效率对比

| 模块 | 手写预估 | AI 实际 | 提效 |
|------|---------|--------|------|
| 后端（86 类） | 6 周 | 约 1 天 | ×30 |
| 前端（50+ 组件） | 4 周 | 约半天 | ×40 |
| 数据库（30 表） | 3 天 | 2 小时 | ×12 |
| 文档（5000+ 行） | 1 周 | 2 小时 | ×20 |
| **总计** | **2-3 个月** | **不到 2 天** | **×30+** |

---

## 4. 开始你自己的项目

```powershell
mkdir my-project
cd my-project
claude
```

进入对话界面后，选一个直接复制：

**有明确想法：**
```
帮我创建一个 [项目类型]，技术栈 Spring Boot + Vue 3。
核心功能：1.XXX  2.XXX  3.XXX。数据库 MySQL，缓存 Redis。
```

**想练手但没想法：**
```
帮我创建一个任务管理 Web 应用。能添加任务、标记完成、按日期筛选、
搜索标题。后端 Spring Boot，前端 Vue 3，数据库 MySQL。
```

**完全不知道做什么：**
```
帮我创建一个个人博客系统。发布文章（标题+正文）、分类、标签、
评论、后台管理。前端 Vue 3，后端 Spring Boot，数据库 MySQL。
```

第一次对话 AI 生成完代码后：

```bash
git init && git add -A && git commit -m "第一版"
# 然后继续，每次只加一个功能：
"帮我加个搜索功能"
"列表加个分页"
"登录页优化一下 UI"
```

每次对话只做一件事，做完就 commit——这是 Vibe Coding 最重要的纪律。

---

## 5. 部署上线

**环境要求**：JDK ≥ 1.8 · MySQL ≥ 5.7 · Redis ≥ 3.0 · Maven ≥ 3.0 · Node.js ≥ 16

**本地启动**：

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ai_test DEFAULT CHARACTER SET utf8mb4;"
bash start-all.sh          # Git Bash 用户
# 或双击 start-services.bat  # Windows 无 Git Bash 的用户
# → http://localhost:5174（AI 用户端）
# → http://localhost:5173（管理后台）
# → http://localhost:8090/doc.html（接口文档）
```

**公网访问**：下载 [cloudflared.exe](https://github.com/cloudflare/cloudflared/releases) 到用户目录，运行 `bash start-all.sh` 选第 4 项，自动获得 `https://xxxx.trycloudflare.com` 公网地址。其他方案（ngrok/frp）见 [技术文档](./项目技术文档.md)。

**内置账号**：管理员 `admin/admin123`，普通用户 `user/user123`。

**上线前检查**：登录保护 · 密码 BCrypt 加密 · XSS 防护 · 登录限流（5次/分/IP） · 操作审计日志 · Loading/错误/空状态 · CORS 白名单 · 生产环境关闭 Swagger。本项目已逐项实现，可直接作为需求告诉 AI。

---

## 6. 参考附录

### 项目结构

```
├── spring-boot-admin/       # 后端 Spring Boot（31 Controller + 20 Mapper + 22 Entity）
├── ds_ai_web/               # AI 用户端 Vue 3（17 组件 + 8 Store + 9 API 模块）
├── vue-admin/               # 管理后台 Vue 2
├── CLAUDE.md                # AI 编码规范
├── start-all.sh / start-services.bat / stop-all.sh
└── 项目技术文档.md / API.md / ARCHITECTURE.md
```

### 配套文档

[CLAUDE.md](./CLAUDE.md) · [技术文档](./项目技术文档.md) · [API 文档](./spring-boot-admin/API.md) · [架构文档](./spring-boot-admin/ARCHITECTURE.md)

---

[MIT](https://opensource.org/licenses/MIT) 开源协议

> 不到 2 天 + 5 块钱 = 一个可商业落地的完整 Web 系统。这个项目就是证据。现在——搭环境，打开 PowerShell，输入 `claude`，说出你的第一个需求。
