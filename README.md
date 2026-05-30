# Ds-Ai：Vibe Coding 实战项目

> 一个功能完备的 AI 聊天平台，**100% 由 AI 对话生成，零手写代码**。总成本不到 2 天 + 5 块钱。

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://gitee.com/xp_s/vibeCoding)
[![Spring Boot 2.7](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue-3.5-4FC08D.svg)](https://vuejs.org/)
[![AI Built](https://img.shields.io/badge/100%25-AI%20Built-blueviolet.svg)](https://claude.ai/code)

---

## 这是什么

一个多模块 AI 聊天平台，含 30 项功能、86 个 Java 类、50+ 前端组件、30 张数据库表。代码全部由 Claude Code + DeepSeek 通过对话生成。

**但它更重要的价值是一份教程**——从零搭建 AI 编程环境到交付商用级项目，每一步的操作方法都记录在 [GUIDE.md](./GUIDE.md) 中。

**[截图1]** *（AI 用户端聊天界面）*
**[截图2]** *（管理后台界面）*

---

## 快速开始

```bash
# 1. 建库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ai_test DEFAULT CHARACTER SET utf8mb4;"

# 2. 启动
bash start-all.sh
# Windows 用户备选：双击 start-services.bat

# 3. 访问
# http://localhost:5174   → AI 用户端
# http://localhost:5173   → 管理后台
# http://localhost:8090/doc.html → 接口文档
```

内置账号：`admin` / `admin123` 和 `user` / `user123`

**环境要求**：JDK ≥ 1.8 · MySQL ≥ 5.7 · Redis ≥ 3.0 · Maven ≥ 3.0 · Node.js ≥ 16

---

## 模块概览

| 模块 | 技术栈 | 端口 |
|------|--------|------|
| spring-boot-admin | Spring Boot 2.7 + MyBatis + Redis + MySQL | 8090 |
| ds_ai_web | Vue 3.5 + Element Plus + Pinia + Vite | 5174 |
| vue-admin | Vue 2.7 + Element UI + Vuex + Vite | 5173 |

**功能清单**：RBAC 权限管理(8) · 系统监控(6) · 代码生成器 · AI 多模型对话(SSE) · RAG 检索 · 联网搜索 · 文档解析 · 知识库 · 助手模板 · 国际化 · 收藏分享

---

## 项目结构

```
├── spring-boot-admin/     # 后端（31 Controller + 20 Mapper + 22 Entity）
├── ds_ai_web/             # AI 用户端 Vue 3（17 组件 + 8 Store）
├── vue-admin/             # 管理后台 Vue 2
├── GUIDE.md               # 完整教程——从零用 AI 搭建项目的每一步
├── CLAUDE.md              # AI 编码规范
├── start-all.sh / stop-all.sh / start-services.bat
└── 项目技术文档.md / API.md / ARCHITECTURE.md
```

---

## 文档导航

| 想要什么 | 看哪个 |
|---------|--------|
| 学会用 AI 从零搭建项目 | [GUIDE.md](./GUIDE.md) —— 完整教程 |
| 了解项目架构和数据库设计 | [技术文档](./项目技术文档.md) |
| 查 API 接口 | [API 文档](./spring-boot-admin/API.md) |
| 看认证流程和数据流 | [架构文档](./spring-boot-admin/ARCHITECTURE.md) |
| 复制 AI 编码规范 | [CLAUDE.md](./CLAUDE.md) |

---

[MIT](https://opensource.org/licenses/MIT) 开源协议
