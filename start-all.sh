#!/bin/bash
# ============================================
# 服务启动脚本 — 交互式选择 + 依赖检测
# ============================================

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
MAVEN_HOME="D:/devTool-2022-new/apache-maven-3.6.3"
JAVA_HOME="D:/devTool-2022-new/jdk"
CLOUDFLARED="$HOME/cloudflared.exe"
MVN="$MAVEN_HOME/bin/mvn"

export MAVEN_HOME="$MAVEN_HOME"
export M2_HOME="$MAVEN_HOME"
export JAVA_HOME="$JAVA_HOME"
export PATH="$MAVEN_HOME/bin:$JAVA_HOME/bin:$PATH"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m'

cleanup() {
    echo ""
    echo -e "${YELLOW}正在停止所有服务...${NC}"
    cmd.exe //c "taskkill /F /IM java.exe /T 2>nul"
    cmd.exe //c "taskkill /F /IM node.exe /T 2>nul"
    cmd.exe //c "taskkill /F /IM cloudflared.exe /T 2>nul"
    echo -e "${GREEN}所有服务已停止${NC}"
    exit 0
}
trap cleanup SIGINT SIGTERM

# ============================================ 清理旧进程 ============================================
echo -e "${YELLOW}清理旧进程...${NC}"
cmd.exe //c "taskkill /F /IM java.exe /T 2>nul"
cmd.exe //c "taskkill /F /IM node.exe /T 2>nul"
cmd.exe //c "taskkill /F /IM cloudflared.exe /T 2>nul"
sleep 2

# ============================================ 依赖检测 ============================================
echo ""
echo -e "${CYAN}=========================================="
echo "  检测依赖服务"
echo -e "==========================================${NC}"

REDIS_EXE="D:/devTool-2022-new/Redis-x64-3.0.504/redis-server.exe"

check_port() {
    cmd.exe //c "netstat -ano 2>nul | findstr LISTENING | findstr :$1" 2>/dev/null
}

# 逐项检测
DEPS_OK=0

# ──── MySQL ────
if [ -n "$(check_port 3306)" ]; then
    echo -e "  ${GREEN}✓${NC} MySQL (端口 3306) — 已运行"
else
    echo -e "  ${YELLOW}○${NC} MySQL (端口 3306) — 未运行，尝试启动..."
    cmd.exe //c "net start MySQL 2>nul || net start MySQL80 2>nul"
    sleep 3
    if [ -n "$(check_port 3306)" ]; then
        echo -e "  ${GREEN}✓${NC} MySQL — 启动成功"
    else
        echo -e "  ${RED}✗${NC} MySQL — 启动失败，请手动启动"
        DEPS_OK=1
    fi
fi

# ──── Redis ────
if [ -n "$(check_port 6379)" ]; then
    echo -e "  ${GREEN}✓${NC} Redis (端口 6379) — 已运行"
else
    echo -e "  ${YELLOW}○${NC} Redis (端口 6379) — 未运行，尝试启动..."
    if [ -f "$REDIS_EXE" ]; then
        "$REDIS_EXE" &
        sleep 2
        if [ -n "$(check_port 6379)" ]; then
            echo -e "  ${GREEN}✓${NC} Redis — 启动成功"
        else
            echo -e "  ${RED}✗${NC} Redis — 启动失败，请检查 $REDIS_EXE"
            DEPS_OK=1
        fi
    else
        echo -e "  ${RED}✗${NC} Redis — 找不到 $REDIS_EXE"
        DEPS_OK=1
    fi
fi

if [ "$DEPS_OK" = "1" ]; then
    echo ""
    echo -e "${RED}依赖服务未就绪，是否继续启动？(可能报错) [y/N]${NC}"
    read -p "  > " yn
    yn=$(echo "$yn" | tr '[:upper:]' '[:lower:]')
    [ "$yn" != "y" ] && { echo "已取消"; exit 1; }
fi

# ============================================ 交互选择 ============================================
echo ""
echo -e "${CYAN}=========================================="
echo "  选择要启动的服务"
echo -e "==========================================${NC}"
echo ""

START_BACKEND="n"
START_VUE_ADMIN="n"
START_DS_AI="n"
USE_TUNNEL="n"

echo -e "  ${GREEN}1.${NC} 后端 ${YELLOW}spring-boot-admin (8090)${NC}？ [y/N]"
read -p "  > " yn
yn=$(echo "$yn" | tr '[:upper:]' '[:lower:]')
[ "$yn" = "y" ] && START_BACKEND="y"

echo ""
echo -e "  ${GREEN}2.${NC} 前端 ${YELLOW}vue-admin 管理后台 (5173)${NC}？ [y/N]"
read -p "  > " yn
yn=$(echo "$yn" | tr '[:upper:]' '[:lower:]')
[ "$yn" = "y" ] && START_VUE_ADMIN="y"

echo ""
echo -e "  ${GREEN}3.${NC} 前端 ${YELLOW}ds_ai_web AI聊天 (5174)${NC}？ [y/N]"
read -p "  > " yn
yn=$(echo "$yn" | tr '[:upper:]' '[:lower:]')
[ "$yn" = "y" ] && START_DS_AI="y"

if [ -f "$CLOUDFLARED" ]; then
    echo ""
    echo -e "  ${GREEN}4.${NC} ${CYAN}公网穿透${NC}？ [y/N]"
    read -p "  > " yn
    yn=$(echo "$yn" | tr '[:upper:]' '[:lower:]')
    [ "$yn" = "y" ] && USE_TUNNEL="y"
fi

if [ "$START_BACKEND" = "n" ] && [ "$START_VUE_ADMIN" = "n" ] && [ "$START_DS_AI" = "n" ]; then
    echo ""
    echo -e "${RED}没有选择任何服务，退出。${NC}"
    exit 0
fi

# ============================================ 启动服务 ============================================
echo ""
echo -e "${CYAN}=========================================="
echo "  正在启动..."
echo -e "==========================================${NC}"

# ──── 后端 ────
if [ "$START_BACKEND" = "y" ]; then
    echo ""
    echo -e "${YELLOW}[后端]${NC} spring-boot-admin (8090)..."
    (cd "$ROOT_DIR/spring-boot-admin" && "$MVN" spring-boot:run) &
    sleep 3
fi

# ──── 公网模式：先 build 再 preview ────
if [ "$USE_TUNNEL" = "y" ]; then
    echo ""
    echo -e "${YELLOW}[构建]${NC} 公网模式需要打包前端，约 30-60 秒..."

    if [ "$START_VUE_ADMIN" = "y" ]; then
        echo -e "  ${YELLOW}→${NC} vue-admin 构建中..."
        (cd "$ROOT_DIR/vue-admin" && npm run build)
    fi
    if [ "$START_DS_AI" = "y" ]; then
        echo -e "  ${YELLOW}→${NC} ds_ai_web 构建中..."
        (cd "$ROOT_DIR/ds_ai_web" && npm run build)
    fi

    echo -e "  ${GREEN}✓${NC} 构建完成，启动 preview..."

    if [ "$START_VUE_ADMIN" = "y" ]; then
        echo ""
        echo -e "${YELLOW}[vue-admin]${NC} preview (5173)..."
        (cd "$ROOT_DIR/vue-admin" && npx vite preview --port 5173 --host 0.0.0.0) &
    fi
    if [ "$START_DS_AI" = "y" ]; then
        echo ""
        echo -e "${YELLOW}[ds_ai_web]${NC} preview (5174)..."
        (cd "$ROOT_DIR/ds_ai_web" && npx vite preview --port 5174 --host 0.0.0.0) &
    fi
    sleep 3

# ──── 本地模式：dev server（HMR 热更新）────
else
    if [ "$START_VUE_ADMIN" = "y" ]; then
        echo ""
        echo -e "${YELLOW}[vue-admin]${NC} dev (5173)..."
        (cd "$ROOT_DIR/vue-admin" && npm run dev) &
    fi
    if [ "$START_DS_AI" = "y" ]; then
        echo ""
        echo -e "${YELLOW}[ds_ai_web]${NC} dev (5174)..."
        (cd "$ROOT_DIR/ds_ai_web" && npm run dev) &
    fi
    sleep 8
fi

# ============================================ 内网穿透 ============================================
URL_5173=""
URL_5174=""
if [ "$USE_TUNNEL" = "y" ]; then
    echo ""
    echo -e "${CYAN}[隧道]${NC} Cloudflare Tunnel..."
    TUNNEL_DIR="$HOME/.tunnel_logs"
    mkdir -p "$TUNNEL_DIR"

    [ "$START_DS_AI" = "y" ]  && "$CLOUDFLARED" tunnel --url http://localhost:5174 > "$TUNNEL_DIR/tunnel_5174.log" 2>&1 &
    [ "$START_VUE_ADMIN" = "y" ] && "$CLOUDFLARED" tunnel --url http://localhost:5173 > "$TUNNEL_DIR/tunnel_5173.log" 2>&1 &

    echo "  等待隧道建立..."
    sleep 8

    URL_5174=$(grep -oP 'https://[a-z0-9\-]+\.trycloudflare\.com' "$TUNNEL_DIR/tunnel_5174.log" 2>/dev/null | head -1)
    URL_5173=$(grep -oP 'https://[a-z0-9\-]+\.trycloudflare\.com' "$TUNNEL_DIR/tunnel_5173.log" 2>/dev/null | head -1)
fi

# ============================================ 结果汇总 ============================================
echo ""
echo -e "${GREEN}=========================================="
echo "  启动完成"
echo -e "==========================================${NC}"

[ "$START_BACKEND" = "y" ]   && echo -e "  ${GREEN}后端${NC}           http://localhost:8090"
[ "$START_VUE_ADMIN" = "y" ] && echo -e "  ${GREEN}vue-admin${NC}       http://localhost:5173"
[ "$START_DS_AI" = "y" ]  && echo -e "  ${GREEN}ds_ai_web${NC}   http://localhost:5174"

if [ -n "$URL_5174" ] || [ -n "$URL_5173" ]; then
    echo ""
    echo -e "  ${CYAN}──── 公网地址 ────${NC}"
    [ -n "$URL_5174" ] && echo -e "  ${CYAN}ds_ai_web:${NC}  $URL_5174"
    [ -n "$URL_5173" ] && echo -e "  ${CYAN}vue-admin:${NC}     $URL_5173"
fi

echo ""
echo -e "${YELLOW}  Ctrl+C 停止所有服务${NC}"
echo -e "${GREEN}==========================================${NC}"

wait
