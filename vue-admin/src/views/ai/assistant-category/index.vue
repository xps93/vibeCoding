<template>
  <div class="category-page">
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>助手分类管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('ai:assistant:category:create')">新增分类</el-button>
      </div>
      <el-table :data="tableData" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">{{ row.status === 0 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('ai:assistant:category:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('ai:assistant:category:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="450px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="100px" size="small">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="如：编程开发" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" controls-position="right" style="width:140px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="submitForm" size="small" :loading="submitting">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listCategories, createCategory, updateCategory, deleteCategory } from '../../../api/assistantCategory'
import { mapState } from 'vuex'

export default {
  name: 'AssistantCategoryManagement',
  data() {
    return {
      tableData: [], loading: false, dialogVisible: false, dialogTitle: '', submitting: false, isEdit: false,
      form: { id: null, name: '', sort: 0, status: 0 },
      formRules: { name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }] }
    }
  },
  computed: { ...mapState(['permissions']) },
  mounted() { this.fetchData() },
  methods: {
    hasPerm(perm) { return this.permissions.indexOf(perm) > -1 },
    async fetchData() {
      this.loading = true
      try { const res = await listCategories(); this.tableData = Array.isArray(res.data) ? res.data : [] }
      finally { this.loading = false }
    },
    handleAdd() {
      this.isEdit = false; this.dialogTitle = '新增分类'
      this.form = { id: null, name: '', sort: 0, status: 0 }; this.dialogVisible = true
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      this.isEdit = true; this.dialogTitle = '编辑分类'
      this.form = { ...row }; this.dialogVisible = true
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.clearValidate() })
    },
    async handleDelete(row) {
      try { await this.$confirm('确认删除分类 "' + row.name + '"？', '提示', { type: 'warning' }) }
      catch (e) { return }
      await deleteCategory(row.id); this.$message.success('删除成功'); this.fetchData()
    },
    async submitForm() {
      try { await this.$refs.form.validate() } catch (e) { return }
      this.submitting = true
      try {
        if (this.isEdit) { await updateCategory(this.form); this.$message.success('更新成功') }
        else { await createCategory(this.form); this.$message.success('新增成功') }
        this.dialogVisible = false; this.fetchData()
      } finally { this.submitting = false }
    }
  }
}
</script>

<style lang="scss" scoped>
.category-page .card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
