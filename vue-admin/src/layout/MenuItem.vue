<template>
  <el-menu-item v-if="!menu.children || menu.children.length === 0 || menu.menuType === 'F'" :index="fullPath">
    <i :class="'el-icon-' + menu.icon.toLowerCase()" v-if="menu.icon"></i>
    <i class="el-icon-document" v-else></i>
    <span slot="title">{{ menu.name }}</span>
  </el-menu-item>
  <el-submenu v-else :index="fullPath">
    <template slot="title">
      <i :class="'el-icon-' + menu.icon.toLowerCase()" v-if="menu.icon"></i>
      <i class="el-icon-folder" v-else></i>
      <span slot="title">{{ menu.name }}</span>
    </template>
    <MenuItem v-for="child in menu.children" :key="child.id" :menu="child" :basePath="fullPath" />
  </el-submenu>
</template>

<script>
export default {
  name: 'MenuItem',
  // 接收父组件传入的菜单项和基础路径
  props: {
    menu: { type: Object, required: true },
    basePath: { type: String, default: '' }
  },
  computed: {
    // 拼接菜单项的完整路由路径
    fullPath() {
      if (!this.basePath || this.basePath === '/') return this.menu.path
      return this.basePath + '/' + this.menu.path.split('/').pop()
    }
  }
}
</script>
