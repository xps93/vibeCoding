<template>
  <div class="post-page">
    <!-- Search -->
    <el-card shadow="hover" class="mb16">
      <el-form :inline="true" size="small">
        <el-form-item label="关键字">
          <el-input v-model="keyword" placeholder="岗位编码/岗位名称" clearable @keyup.enter="fetchData" />
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
        <span>岗位列表</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('system:post:create')">新增</el-button>
      </div>
      <el-table :data="posts" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column prop="postCode" label="岗位编码" width="150"></el-table-column>
        <el-table-column prop="postName" label="岗位名称" width="150"></el-table-column>
        <el-table-column prop="postSort" label="排序" width="80" align="center"></el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('system:post:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('system:post:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="80px" size="small">
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" />
        </el-form-item>
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" />
        </el-form-item>
        <el-form-item label="显示排序" prop="orderNum">
          <el-input-number v-model="form.postSort" :min="0" />
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
import { listPosts, addPost, updatePost, deletePost } from '../../../api/post'
import { mapState } from 'vuex'

export default {
  name: 'PostManagement',
  // 返回岗位列表数据、搜索关键字和表单状态
  data() {
    return {
      posts: [],
      keyword: '',
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: { postCode: '', postName: '', postSort: 0, status: 0 },
      formRules: {
        postCode: [{ required: true, message: '必填', trigger: 'blur' }],
        postName: [{ required: true, message: '必填', trigger: 'blur' }]
      }
    }
  },
  // 从 Vuex 获取当前用户的权限列表
  computed: { ...mapState(['permissions']) },
  // 页面创建时加载岗位列表
  created() { this.fetchData() },
  methods: {
    // 检查当前用户是否拥有指定权限
    hasPerm(perm) { return this.permissions.includes(perm) },
    // 获取岗位列表数据
    async fetchData() {
      this.loading = true
      const res = await listPosts({ keyword: this.keyword || undefined })
      this.posts = res.data || []
      this.loading = false
    },
    // 重置搜索关键字并重新查询
    resetSearch() { this.keyword = ''; this.fetchData() },
    // 打开新增岗位弹窗
    handleAdd() {
      this.dialogTitle = '新增岗位'
      this.form = { postCode: '', postName: '', orderNum: 0, status: 0 }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 打开修改岗位弹窗并回填数据
    handleEdit(row) {
      this.dialogTitle = '修改岗位'
      this.form = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 提交新增或修改岗位的表单
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (this.form.id) {
          await updatePost(this.form)
        } else {
          await addPost(this.form)
        }
        this.dialogVisible = false
        this.$message.success('操作成功')
        this.fetchData()
      })
    },
    // 确认后删除指定岗位
    async handleDelete(row) {
      this.$confirm(`确认删除岗位"${row.postName}"?`, '提示', { type: 'warning' }).then(async () => {
        await deletePost(row.id)
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
