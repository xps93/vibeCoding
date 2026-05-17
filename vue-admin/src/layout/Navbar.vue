<template>
  <div class="navbar">
    <div class="navbar-left">
      <i :class="'el-icon-s-fold'" class="toggle-btn" @click="$emit('toggle')"></i>
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="navbar-right">
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="user-info">
          <el-avatar :size="28" icon="el-icon-user-solid" style="background:#409eff;vertical-align:middle"></el-avatar>
          <span class="username">{{ user ? user.nickname || user.username : '' }}</span>
          <i class="el-icon-arrow-down"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { logout } from '../api/login'

export default {
  name: 'Navbar',
  // 返回当前页面标题
  data() {
    return { currentTitle: '' }
  },
  // 从 Vuex 获取当前用户信息
  computed: {
    ...mapState(['user'])
  },
  // 监听路由变化，更新面包屑标题
  watch: {
    '$route'(route) {
      this.currentTitle = route.meta ? route.meta.title : ''
    }
  },
  // 页面创建时初始化面包屑标题
  created() {
    this.currentTitle = this.$route.meta ? this.$route.meta.title : ''
  },
  methods: {
    // 处理下拉菜单命令（退出登录）
    async handleCommand(cmd) {
      if (cmd === 'logout') {
        try { await logout() } catch (e) {}
        this.$store.commit('RESET_STATE')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.navbar {
  height: 50px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  z-index: 10;
}
.navbar-left {
  display: flex;
  align-items: center;
}
.toggle-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 16px;
  color: #606266;
}
.toggle-btn:hover { color: #409eff; }
.breadcrumb { font-size: 14px; }
.navbar-right { display: flex; align-items: center; }
.user-info { cursor: pointer; display: flex; align-items: center; }
.username { margin: 0 6px; color: #333; font-size: 14px; }
</style>
