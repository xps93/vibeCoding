<template>
  <div class="layout-container">
    <div class="layout-sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-logo">
        <img src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48'%3E%3Cpath fill='%23fff' d='M24 4L6 14v20l18 10 18-10V14L24 4zm0 4.5L36 16l-12 6.7L12 16l12-7.5zM10 18.5l12 6.7V40L10 33.3V18.5zm26 0V33.3L24 40V25.2l12-6.7z'/%3E%3C/svg%3E"
             class="logo-img" />
        <span class="logo-title" v-show="!isCollapsed">后台管理系统</span>
      </div>
      <el-scrollbar class="sidebar-menu">
        <SidebarMenu :menus="menus" :isCollapsed="isCollapsed" />
      </el-scrollbar>
    </div>
    <div class="layout-main" :class="{ collapsed: isCollapsed }">
      <Navbar @toggle="toggleCollapse" />
      <div class="main-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import SidebarMenu from './Sidebar.vue'
import Navbar from './Navbar.vue'

export default {
  name: 'Layout',
  components: { SidebarMenu, Navbar },
  data() {
    return { isCollapsed: false }
  },
  computed: {
    ...mapState(['menus'])
  },
  methods: {
    toggleCollapse() {
      this.isCollapsed = !this.isCollapsed
    }
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100%;
  background: #f0f2f5;
}
.layout-sidebar {
  width: 210px;
  min-width: 210px;
  background: #304156;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.layout-sidebar.collapsed {
  width: 54px;
  min-width: 54px;
}
.sidebar-logo {
  height: 50px;
  display: flex;
  align-items: center;
  padding: 0 12px;
  background: #2b3a4b;
  overflow: hidden;
}
.logo-img {
  width: 28px;
  height: 28px;
  flex-shrink: 0;
}
.logo-title {
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  margin-left: 10px;
  white-space: nowrap;
}
.sidebar-menu {
  flex: 1;
  overflow: auto;
}
.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.main-content {
  flex: 1;
  padding: 16px;
  overflow: auto;
}
</style>
