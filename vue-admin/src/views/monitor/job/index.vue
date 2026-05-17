<template>
  <div class="job-page">
    <!-- Search -->
    <el-card shadow="hover" class="mb16">
      <el-form :inline="true" size="small">
        <el-form-item label="关键字">
          <el-input v-model="keyword" placeholder="任务名称" clearable @keyup.enter="fetchData" />
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
        <span>定时任务</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('monitor:job:create')">新增</el-button>
      </div>
      <el-table :data="jobs" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column prop="jobName" label="任务名称" min-width="150"></el-table-column>
        <el-table-column prop="jobGroup" label="任务组名" width="120"></el-table-column>
        <el-table-column prop="invokeTarget" label="调用目标" min-width="200"></el-table-column>
        <el-table-column prop="cronExpression" label="cron表达式" min-width="160"></el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('monitor:job:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('monitor:job:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="550px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="100px" size="small">
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="form.jobName" />
        </el-form-item>
        <el-form-item label="任务组名" prop="jobGroup">
          <el-input v-model="form.jobGroup" />
        </el-form-item>
        <el-form-item label="调用目标" prop="invokeTarget">
          <el-input v-model="form.invokeTarget" />
        </el-form-item>
        <el-form-item label="cron表达式" prop="cronExpression">
          <el-input v-model="form.cronExpression" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="submitForm" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listJobs, addJob, updateJob, deleteJob } from '../../../api/job'
import { mapState } from 'vuex'

export default {
  name: 'JobManagement',
  // 返回定时任务列表数据、搜索关键字和表单状态
  data() {
    return {
      jobs: [],
      keyword: '',
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: { jobName: '', jobGroup: '', invokeTarget: '', cronExpression: '', status: 0 },
      formRules: {
        jobName: [{ required: true, message: '必填', trigger: 'blur' }],
        invokeTarget: [{ required: true, message: '必填', trigger: 'blur' }],
        cronExpression: [{ required: true, message: '必填', trigger: 'blur' }]
      }
    }
  },
  // 从 Vuex 获取当前用户的权限列表
  computed: { ...mapState(['permissions']) },
  // 页面创建时加载定时任务列表
  created() { this.fetchData() },
  methods: {
    // 检查当前用户是否拥有指定权限
    hasPerm(perm) { return this.permissions.includes(perm) },
    // 获取定时任务列表数据
    async fetchData() {
      this.loading = true
      const res = await listJobs({ keyword: this.keyword || undefined })
      this.jobs = res.data || []
      this.loading = false
    },
    // 重置搜索关键字并重新查询
    resetSearch() { this.keyword = ''; this.fetchData() },
    // 打开新增定时任务弹窗
    handleAdd() {
      this.dialogTitle = '新增任务'
      this.form = { jobName: '', jobGroup: '', invokeTarget: '', cronExpression: '', status: 0 }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 打开修改定时任务弹窗并回填数据
    handleEdit(row) {
      this.dialogTitle = '修改任务'
      this.form = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 提交新增或修改定时任务的表单
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (this.form.id) {
          await updateJob(this.form)
        } else {
          await addJob(this.form)
        }
        this.dialogVisible = false
        this.$message.success('操作成功')
        this.fetchData()
      })
    },
    // 确认后删除指定定时任务
    async handleDelete(row) {
      this.$confirm(`确认删除任务"${row.jobName}"?`, '提示', { type: 'warning' }).then(async () => {
        await deleteJob(row.id)
        this.$message.success('删除成功')
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
