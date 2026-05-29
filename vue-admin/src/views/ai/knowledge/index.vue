<template>
  <div class="knowledge-page">
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>AI知识库管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('ai:model:create')">新增知识库</el-button>
      </div>
      <el-table :data="tableData" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="name" label="知识库名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip>
          <template slot-scope="{ row }">
            <span v-if="row.description">{{ row.description }}</span>
            <span v-else style="color:#c0c4cc">暂无描述</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">{{ row.status === 0 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('ai:model:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('ai:model:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="100px" size="small">
        <el-form-item label="知识库名称" prop="name">
          <el-input v-model="form.name" placeholder="如：通用知识库" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入知识库描述" />
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
import { listKnowledgeBases, createKnowledgeBase, updateKnowledgeBase, deleteKnowledgeBase } from '../../../api/aiKnowledgeBase'
import { mapState } from 'vuex'

export default {
  name: 'KnowledgeBaseManagement',
  data() {
    return {
      tableData: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      submitting: false,
      isEdit: false,
      form: { id: null, name: '', description: '', status: 0 },
      formRules: {
        name: [{ required: true, message: '请输入知识库名称', trigger: 'blur' }]
      }
    }
  },
  computed: {
    ...mapState(['permissions'])
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    hasPerm(perm) {
      return this.permissions.indexOf(perm) > -1
    },
    async fetchData() {
      this.loading = true
      try {
        const res = await listKnowledgeBases()
        this.tableData = Array.isArray(res.data) ? res.data : (res.data && res.data.list ? res.data.list : [])
      } finally { this.loading = false }
    },
    handleAdd() {
      this.isEdit = false
      this.dialogTitle = '新增知识库'
      this.form = { id: null, name: '', description: '', status: 0 }
      this.dialogVisible = true
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑知识库'
      this.form = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.clearValidate() })
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除知识库 "' + row.name + '"？', '提示', { type: 'warning' })
      } catch (e) { return }
      await deleteKnowledgeBase(row.id)
      this.$message.success('删除成功')
      this.fetchData()
    },
    async submitForm() {
      try {
        await this.$refs.form.validate()
      } catch (e) { return }
      this.submitting = true
      try {
        if (this.isEdit) {
          await updateKnowledgeBase(this.form)
          this.$message.success('更新成功')
        } else {
          await createKnowledgeBase(this.form)
          this.$message.success('新增成功')
        }
        this.dialogVisible = false
        this.fetchData()
      } finally { this.submitting = false }
    }
  }
}
</script>

<style lang="scss" scoped>
.knowledge-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
