<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="isCollapsed"
    :unique-opened="true"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409eff"
    router
  >
    <template v-for="item in menus">
      <el-submenu v-if="item.children && item.children.length > 0 && item.menuType !== 'F'" :key="item.id" :index="item.path">
        <template slot="title">
          <i :class="'el-icon-' + item.icon.toLowerCase()" v-if="item.icon && item.icon !== 'Layout'"></i>
          <i class="el-icon-menu" v-else></i>
          <span slot="title">{{ item.name }}</span>
        </template>
        <MenuItem v-for="child in item.children" :key="child.id" :menu="child" :basePath="item.path" />
      </el-submenu>
      <el-menu-item v-else-if="item.menuType !== 'F'" :key="item.id" :index="item.path">
        <i :class="'el-icon-' + item.icon.toLowerCase()" v-if="item.icon"></i>
        <i class="el-icon-menu" v-else></i>
        <span slot="title">{{ item.name }}</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script>
import MenuItem from './MenuItem.vue'

export default {
  name: 'SidebarMenu',
  components: { MenuItem },
  props: {
    menus: { type: Array, default: () => [] },
    isCollapsed: { type: Boolean, default: false }
  },
  computed: {
    activeMenu() {
      const route = this.$route
      return route.path
    }
  }
}
</script>
