<template>
  <div class="operlog-page">
    <!-- Search -->
    <el-card shadow="hover" class="mb16">
      <el-form :inline="true" size="small">
        <el-form-item label="操作名称">
          <el-input v-model="query.operName" placeholder="日志标题" clearable @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="query.businessType" placeholder="业务类型" clearable style="width:140px">
            <el-option label="新增" :value="1" />
            <el-option label="修改" :value="2" />
            <el-option label="删除" :value="3" />
            <el-option label="查询" :value="4" />
          </el-select>
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
        <span>操作日志</span>
        <el-button type="danger" size="small" icon="el-icon-delete" @click="handleClear" v-if="hasPerm('monitor:operlog:delete')">清空</el-button>
      </div>
      <el-table :data="operLogs" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column prop="title" label="日志标题" min-width="140"></el-table-column>
        <el-table-column prop="businessType" label="业务类型" width="100" align="center">
          <template slot-scope="{ row }">
            <span>{{ ['', '新增', '修改', '删除', '查询'][row.businessType] || '' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="operName" label="操作人员" width="120"></el-table-column>
        <el-table-column prop="operIp" label="操作地址" width="130"></el-table-column>
        <el-table-column prop="requestMethod" label="请求方式" width="90" align="center"></el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">
              {{ row.status === 0 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operTime" label="操作时间" width="170"></el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-view" @click="handleDetail(row)">详情</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('monitor:operlog:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Detail Dialog -->
    <el-dialog title="日志详情" :visible.sync="detailVisible" width="600px" :close-on-click-modal="false">
      <el-form label-width="100px" size="small">
        <el-form-item label="日志标题：">{{ detail.title }}</el-form-item>
        <el-form-item label="业务类型：">{{ ['', '新增', '修改', '删除', '查询'][detail.businessType] || '' }}</el-form-item>
        <el-form-item label="操作人员：">{{ detail.operName }}</el-form-item>
        <el-form-item label="请求方式：">{{ detail.requestMethod }}</el-form-item>
        <el-form-item label="请求参数：">{{ detail.operParam || '无' }}</el-form-item>
        <el-form-item label="返回结果：">{{ detail.jsonResult || '无' }}</el-form-item>
        <el-form-item label="错误信息：">{{ detail.errorMsg || '无' }}</el-form-item>
        <el-form-item label="操作时间：">{{ detail.operTime }}</el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="detailVisible = false" size="small">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listOperLogs, deleteOperLog, clearOperLogs } from '../../../api/operlog'
import { mapState } from 'vuex'

export default {
  name: 'OperLogManagement',
  // 返回操作日志列表和查询条件状态
  data() {
    return {
      operLogs: [],
      query: { operName: '', businessType: '', status: '' },
      loading: false,
      detailVisible: false,
      detail: {}
    }
  },
  // 从 Vuex 获取当前用户的权限列表
  computed: { ...mapState(['permissions']) },
  // 页面创建时加载操作日志列表
  created() { this.fetchData() },
  methods: {
    // 检查当前用户是否拥有指定权限
    hasPerm(perm) { return this.permissions.includes(perm) },
    // 根据查询条件获取操作日志列表
    async fetchData() {
      this.loading = true
      const params = {}
      if (this.query.operName) params.operName = this.query.operName
      if (this.query.businessType !== '') params.businessType = this.query.businessType
      if (this.query.status !== '') params.status = this.query.status
      const res = await listOperLogs(params)
      this.operLogs = res.data || []
      this.loading = false
    },
    // 重置查询条件并重新加载
    resetSearch() { this.query = { operName: '', businessType: '', status: '' }; this.fetchData() },
    // 打开日志详情弹窗
    handleDetail(row) {
      this.detail = { ...row }
      this.detailVisible = true
    },
    // 确认后删除指定操作日志
    async handleDelete(row) {
      this.$confirm('确认删除该条日志?', '提示', { type: 'warning' }).then(async () => {
        await deleteOperLog(row.id)
        this.$message.success('删除成功')
        this.fetchData()
      }).catch(() => {})
    },
    // 确认后清空所有操作日志
    async handleClear() {
      this.$confirm('确认清空所有操作日志?', '提示', { type: 'warning' }).then(async () => {
        await clearOperLogs()
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
