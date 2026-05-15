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
        <el-table-column prop="userName" label="用户名称" width="120"></el-table-column>
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
  data() {
    return {
      onlineUsers: [],
      loading: false
    }
  },
  computed: { ...mapState(['permissions']) },
  created() { this.fetchData() },
  methods: {
    hasPerm(perm) { return this.permissions.includes(perm) },
    async fetchData() {
      this.loading = true
      const res = await listOnlineUsers()
      this.onlineUsers = res.data || []
      this.loading = false
    },
    async handleForceLogout(row) {
      this.$confirm(`确认强制下线用户"${row.userName}"?`, '提示', { type: 'warning' }).then(async () => {
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
