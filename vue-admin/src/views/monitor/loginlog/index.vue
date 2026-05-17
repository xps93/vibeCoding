<template>
  <div class="loginlog-page">
    <!-- Search -->
    <el-card shadow="hover" class="mb16">
      <el-form :inline="true" size="small">
        <el-form-item label="用户名称">
          <el-input v-model="query.userName" placeholder="用户名称" clearable @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="状态" clearable style="width:100px">
            <el-option label="成功" :value="0" />
            <el-option label="失败" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="fetchData">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table -->
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>登录日志</span>
        <el-button type="danger" size="small" icon="el-icon-delete" @click="handleClear" v-if="hasPerm('monitor:loginlog:delete')">清空</el-button>
      </div>
      <el-table :data="loginLogs" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column prop="userName" label="用户名称" width="120"></el-table-column>
        <el-table-column prop="ipAddr" label="登录地址" width="130"></el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">
              {{ row.status === 0 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="msg" label="消息" min-width="180"></el-table-column>
        <el-table-column prop="loginTime" label="登录时间" width="170"></el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('monitor:loginlog:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { listLoginLogs, deleteLoginLog, clearLoginLogs } from '../../../api/loginlog'
import { mapState } from 'vuex'

export default {
  name: 'LoginLogManagement',
  // 返回登录日志列表和查询条件状态
  data() {
    return {
      loginLogs: [],
      query: { userName: '', status: '' },
      loading: false
    }
  },
  // 从 Vuex 获取当前用户的权限列表
  computed: { ...mapState(['permissions']) },
  // 页面创建时加载登录日志列表
  created() { this.fetchData() },
  methods: {
    // 检查当前用户是否拥有指定权限
    hasPerm(perm) { return this.permissions.includes(perm) },
    // 根据查询条件获取登录日志列表
    async fetchData() {
      this.loading = true
      const params = {}
      if (this.query.userName) params.userName = this.query.userName
      if (this.query.status !== '') params.status = this.query.status
      const res = await listLoginLogs(params)
      this.loginLogs = res.data || []
      this.loading = false
    },
    // 重置查询条件并重新加载
    resetSearch() { this.query = { userName: '', status: '' }; this.fetchData() },
    // 确认后删除指定登录日志
    async handleDelete(row) {
      this.$confirm('确认删除该条日志?', '提示', { type: 'warning' }).then(async () => {
        await deleteLoginLog(row.id)
        this.$message.success('删除成功')
        this.fetchData()
      }).catch(() => {})
    },
    // 确认后清空所有登录日志
    async handleClear() {
      this.$confirm('确认清空所有登录日志?', '提示', { type: 'warning' }).then(async () => {
        await clearLoginLogs()
        this.$message.success('清空成功')
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
