<template>
  <div class="notice-page">
    <!-- Search -->
    <el-card shadow="hover" class="mb16">
      <el-form :inline="true" size="small">
        <el-form-item label="关键字">
          <el-input v-model="keyword" placeholder="公告标题" clearable @keyup.enter="fetchData" />
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
        <span>公告列表</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('system:notice:create')">新增</el-button>
      </div>
      <el-table :data="notices" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column prop="noticeTitle" label="公告标题" min-width="200"></el-table-column>
        <el-table-column label="公告类型" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.noticeType === 1 ? 'warning' : 'primary'" size="mini">
              {{ row.noticeType === 1 ? '通知' : '公告' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('system:notice:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('system:notice:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="80px" size="small">
        <el-form-item label="公告标题" prop="noticeTitle">
          <el-input v-model="form.noticeTitle" />
        </el-form-item>
        <el-form-item label="公告类型" prop="noticeType">
          <el-radio-group v-model="form.noticeType">
            <el-radio :label="1">通知</el-radio>
            <el-radio :label="2">公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="noticeContent">
          <el-input type="textarea" v-model="form.noticeContent" :rows="4" />
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
import { listNotices, addNotice, updateNotice, deleteNotice } from '../../../api/notice'
import { mapState } from 'vuex'

export default {
  name: 'NoticeManagement',
  // 返回公告列表数据、搜索关键字和表单状态
  data() {
    return {
      notices: [],
      keyword: '',
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: { noticeTitle: '', noticeType: 1, noticeContent: '', status: 0 },
      formRules: {
        noticeTitle: [{ required: true, message: '必填', trigger: 'blur' }]
      }
    }
  },
  // 从 Vuex 获取当前用户的权限列表
  computed: { ...mapState(['permissions']) },
  // 页面创建时加载公告列表
  created() { this.fetchData() },
  methods: {
    // 检查当前用户是否拥有指定权限
    hasPerm(perm) { return this.permissions.includes(perm) },
    // 获取公告列表数据
    async fetchData() {
      this.loading = true
      const res = await listNotices({ keyword: this.keyword || undefined })
      this.notices = res.data || []
      this.loading = false
    },
    // 重置搜索关键字并重新查询
    resetSearch() { this.keyword = ''; this.fetchData() },
    // 打开新增公告弹窗
    handleAdd() {
      this.dialogTitle = '新增公告'
      this.form = { noticeTitle: '', noticeType: 1, noticeContent: '', status: 0 }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 打开修改公告弹窗并回填数据
    handleEdit(row) {
      this.dialogTitle = '修改公告'
      this.form = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 提交新增或修改公告的表单
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (this.form.id) {
          await updateNotice(this.form)
        } else {
          await addNotice(this.form)
        }
        this.dialogVisible = false
        this.$message.success('操作成功')
        this.fetchData()
      })
    },
    // 确认后删除指定公告
    async handleDelete(row) {
      this.$confirm(`确认删除公告"${row.noticeTitle}"?`, '提示', { type: 'warning' }).then(async () => {
        await deleteNotice(row.id)
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
