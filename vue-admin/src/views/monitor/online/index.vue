<template>
  <div class="online-page">
    <!-- Table -->
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>在线用户</span>
        <el-button type="primary" size="small" icon="el-icon-refresh" @click="fetchData">刷新</el-button>
      </div>
      <el-table :data="onlineUsers" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column prop="userId" label="用户编号" width="100" align="center"></el-table-column>
        <el-table-column prop="username" label="登录账号" width="120"></el-table-column>
        <el-table-column prop="nickname" label="用户名称" width="120"></el-table-column>
        <el-table-column prop="loginTime" label="登录时间" width="170"></el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-close" style="color:#f56c6c" @click="handleForceLogout(row)">强退</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { listOnlineUsers, forceLogout } from '../../../api/online'
import { mapState } from 'vuex'

export default {
  name: 'OnlineUserManagement',
  // 返回在线用户列表和加载状态
  data() {
    return {
      onlineUsers: [],
      loading: false
    }
  },
  // 从 Vuex 获取当前用户的权限列表
  computed: { ...mapState(['permissions']) },
  // 页面创建时加载在线用户列表
  created() { this.fetchData() },
  methods: {
    // 检查当前用户是否拥有指定权限
    hasPerm(perm) { return this.permissions.includes(perm) },
    // 获取在线用户列表数据
    async fetchData() {
      this.loading = true
      const res = await listOnlineUsers()
      this.onlineUsers = res.data || []
      this.loading = false
    },
    // 确认后强制指定用户下线
    async handleForceLogout(row) {
      this.$confirm(`确认强制下线用户"${row.nickname || row.username}"?`, '提示', { type: 'warning' }).then(async () => {
        await forceLogout(row.token)
        this.$message.success('操作成功')
        this.fetchData()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
