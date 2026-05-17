<template>
  <div class="config-page">
    <!-- Search -->
    <el-card shadow="hover" class="mb16">
      <el-form :inline="true" size="small">
        <el-form-item label="关键字">
          <el-input v-model="keyword" placeholder="参数名称/参数键名" clearable @keyup.enter="fetchData" />
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
        <span>参数设置</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('system:config:create')">新增</el-button>
      </div>
      <el-table :data="configs" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column prop="configName" label="参数名称" min-width="150"></el-table-column>
        <el-table-column prop="configKey" label="参数键名" min-width="150"></el-table-column>
        <el-table-column prop="configValue" label="参数键值" min-width="150"></el-table-column>
        <el-table-column label="类型" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.configType === 0 ? 'success' : 'info'" size="mini">
              {{ row.configType === 0 ? '内置' : '自定义' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('system:config:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('system:config:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="80px" size="small">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="form.configKey" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="form.configValue" />
        </el-form-item>
        <el-form-item label="类型" prop="configType">
          <el-radio-group v-model="form.configType">
            <el-radio :label="1">自定义</el-radio>
            <el-radio :label="0">内置</el-radio>
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
import { listConfigs, addConfig, updateConfig, deleteConfig } from '../../../api/config'
import { mapState } from 'vuex'

export default {
  name: 'ConfigManagement',
  // 返回参数配置列表数据、搜索关键字和表单状态
  data() {
    return {
      configs: [],
      keyword: '',
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: { configName: '', configKey: '', configValue: '', configType: 1 },
      formRules: {
        configName: [{ required: true, message: '必填', trigger: 'blur' }],
        configKey: [{ required: true, message: '必填', trigger: 'blur' }],
        configValue: [{ required: true, message: '必填', trigger: 'blur' }]
      }
    }
  },
  // 从 Vuex 获取当前用户的权限列表
  computed: { ...mapState(['permissions']) },
  // 页面创建时加载参数列表
  created() { this.fetchData() },
  methods: {
    // 检查当前用户是否拥有指定权限
    hasPerm(perm) { return this.permissions.includes(perm) },
    // 获取参数列表数据
    async fetchData() {
      this.loading = true
      const res = await listConfigs({ keyword: this.keyword || undefined })
      this.configs = res.data || []
      this.loading = false
    },
    // 重置搜索关键字并重新查询
    resetSearch() { this.keyword = ''; this.fetchData() },
    // 打开新增参数弹窗
    handleAdd() {
      this.dialogTitle = '新增参数'
      this.form = { configName: '', configKey: '', configValue: '', configType: 'N' }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 打开修改参数弹窗并回填数据
    handleEdit(row) {
      this.dialogTitle = '修改参数'
      this.form = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 提交新增或修改参数的表单
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (this.form.id) {
          await updateConfig(this.form)
        } else {
          await addConfig(this.form)
        }
        this.dialogVisible = false
        this.$message.success('操作成功')
        this.fetchData()
      })
    },
    // 确认后删除指定参数
    async handleDelete(row) {
      this.$confirm(`确认删除参数"${row.configName}"?`, '提示', { type: 'warning' }).then(async () => {
        await deleteConfig(row.id)
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
