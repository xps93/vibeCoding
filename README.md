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

<p align="center">
  <img src="docs/screenshots/1.png" alt="AI 用户端聊天界面" width="45%" />
  &nbsp;&nbsp;
  <img src="docs/screenshots/2.png" alt="管理后台界面" width="45%" />
</p>
<p align="center"><em>左：AI 用户端聊天界面 &nbsp;|&nbsp; 右：管理后台界面</em></p>

---

## 前置准备

### 1. 环境清单

在开始之前，逐条检查你的电脑是否已安装：

| 工具 | 最低版本 | 验证命令 | 下载 |
|------|---------|---------|------|
| JDK | ≥ 1.8 | `java -version` | [Adoptium](https://adoptium.net/) |
| MySQL | ≥ 5.7 | `mysql --version` | [MySQL Community](https://dev.mysql.com/downloads/) |
| Redis | ≥ 3.0 | `redis-server --version` | [Redis for Windows](https://github.com/tporadowski/redis/releases) |
| Maven | ≥ 3.0 | `mvn --version` | [Maven](https://maven.apache.org/download.cgi) |
| Node.js | ≥ 18 | `node -v` | [Node.js](https://nodejs.org/) |
| Git | 任意 | `git --version` | [Git for Windows](https://git-scm.com/) |

> **Windows 用户特别注意**：安装 Git for Windows 时会自带 **Git Bash**，后面启动脚本必须在 Git Bash 中运行（不能在 PowerShell/CMD 中执行 `bash` 命令）。

### 2. 克隆项目

```bash
git clone https://gitee.com/xp_s/vibeCoding.git
cd vibeCoding
```

### 3. 安装前端依赖

`node_modules` 未入库，clone 后必须手动安装：

```bash
cd vue-admin && npm install && cd ..
cd ds_ai_web && npm install && cd ..
```

> 如果下载慢，先换国内源：`npm config set registry https://registry.npmmirror.com`

### 4. 修改启动脚本中的路径（重要）

`start-all.sh` 和 `start-services.bat` 里硬编码了作者本机的 JDK/Maven 路径，你需要改成自己的。

打开 `start-all.sh`，修改第 7-8 行：

```bash
# 改前（作者本机路径，你跑必报错）
MAVEN_HOME="D:/devTool-2022-new/apache-maven-3.6.3"
JAVA_HOME="D:/devTool-2022-new/jdk"

# 改后（换成你自己的安装路径）
MAVEN_HOME="C:/apache-maven-3.9.6"        # 你的 Maven 路径
JAVA_HOME="C:/Program Files/Java/jdk-17"  # 你的 JDK 路径
```

> 如果 `java` 和 `mvn` 已配好系统环境变量，直接把第 6-10 行简化为一行 `MVN=mvn`，删掉 `export` 那几行即可。

---

## 快速开始

### 1. 导入数据库

SQL 文件位置：`spring-boot-admin/src/main/resources/sql/schema.sql`（含建库、30 张表、初始数据）

**方式一：命令行**
```bash
mysql -u root -p < spring-boot-admin/src/main/resources/sql/schema.sql
```

**方式二：客户端**
用 Navicat / DBeaver / HeidiSQL 等工具，新建连接 → 新建数据库 `ai_test`（字符集 utf8mb4）→ 右键"运行 SQL 文件"→ 选择 `schema.sql` 导入。

### 2. 启动服务

```bash
bash start-all.sh              # 在 Git Bash 中运行（推荐）
# 或双击 start-services.bat    # CMD 备选
```

### 3. 访问

| 地址 | 说明 |
|------|------|
| http://localhost:5174 | AI 用户端 |
| http://localhost:5173 | 管理后台 |
| http://localhost:8090/doc.html | 接口文档（Swagger） |

内置账号：`admin` / `admin123` 和 `user` / `user123`

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

## 常见问题

### 启动报错 'java'/'mvn' 不是内部命令？
JDK/Maven 未安装或环境变量未配置。先用 `java -version` 和 `mvn --version` 自查。如果确实装了但脚本报错，检查 `start-all.sh` 中的 `JAVA_HOME` 和 `MAVEN_HOME` 路径是否正确。

### 前端页面空白或报错？
大概率是没装依赖。确认 `vue-admin/node_modules` 和 `ds_ai_web/node_modules` 目录存在，不存在则运行：

```bash
cd vue-admin && npm install && cd ..
cd ds_ai_web && npm install && cd ..
```

### 数据库连接失败？
- MySQL 服务是否启动？（任务管理器 → 服务 → 查找 MySQL）
- `start-all.sh` 默认用 root 空密码连接，如果设了密码，需修改脚本中的连接参数
- 确认已先执行导入 SQL 文件

### 端口被占用？
```bash
netstat -ano | findstr "8090 5173 5174"
```
找到占用进程 PID 后，在任务管理器中结束，或修改 `application.yml` / `vite.config.js` 中的端口。

### Redis 未运行？
- Windows 用户下载 [Redis for Windows](https://github.com/tporadowski/redis/releases)，安装后启动
- `start-all.sh` 会尝试自动启动 Redis，但需要 `redis-server.exe` 在正确路径

### npm install 太慢或报错？
```bash
npm config set registry https://registry.npmmirror.com
```
然后删掉 `node_modules` 和 `package-lock.json` 重试。

---

[MIT](https://opensource.org/licenses/MIT) 开源协议
