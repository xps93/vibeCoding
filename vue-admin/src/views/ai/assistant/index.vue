<template>
  <div class="assistant-page">
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>助手模板管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('ai:assistant:create')">新增模板</el-button>
      </div>
      <el-table :data="tableData" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="name" label="模板名称" min-width="130" />
        <el-table-column prop="categoryName" label="所属分类" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag size="mini" type="warning">{{ row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="icon" label="图标" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">{{ row.status === 0 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('ai:assistant:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('ai:assistant:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="100px" size="small">
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="form.name" placeholder="如：代码专家" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" placeholder="简要描述助手功能" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="Element Plus 图标名，如：Monitor" />
        </el-form-item>
        <el-form-item label="Prompt预设" prop="prompt">
          <el-input v-model="form.prompt" type="textarea" :rows="4" placeholder="System Prompt 预设内容" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" controls-position="right" style="width:140px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :label="0">启用</el-radio><el-radio :label="1">禁用</el-radio></el-radio-group>
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
import { listAssistants, createAssistant, updateAssistant, deleteAssistant } from '../../../api/assistant'
import { listCategories } from '../../../api/assistantCategory'
import { mapState } from 'vuex'

export default {
  name: 'AssistantManagement',
  data() {
    return {
      tableData: [], categories: [], loading: false, dialogVisible: false, dialogTitle: '', submitting: false, isEdit: false,
      form: { id: null, categoryId: null, name: '', description: '', icon: '', prompt: '', sort: 0, status: 0 },
      formRules: {
        name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
        prompt: [{ required: true, message: '请输入Prompt预设', trigger: 'blur' }]
      }
    }
  },
  computed: { ...mapState(['permissions']) },
  mounted() { this.fetchData(); this.fetchCategories() },
  methods: {
    hasPerm(perm) { return this.permissions.indexOf(perm) > -1 },
    async fetchData() {
      this.loading = true
      try { const res = await listAssistants(); this.tableData = Array.isArray(res.data) ? res.data : [] }
      finally { this.loading = false }
    },
    async fetchCategories() {
      try { const res = await listCategories(); this.categories = Array.isArray(res.data) ? res.data : [] }
      catch (e) { this.categories = [] }
    },
    handleAdd() {
      this.isEdit = false; this.dialogTitle = '新增助手模板'
      this.form = { id: null, categoryId: null, name: '', description: '', icon: '', prompt: '', sort: 0, status: 0 }
      this.dialogVisible = true
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      this.isEdit = true; this.dialogTitle = '编辑助手模板'
      this.form = { ...row }; this.dialogVisible = true
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.clearValidate() })
    },
    async handleDelete(row) {
      try { await this.$confirm('确认删除模板 "' + row.name + '"？', '提示', { type: 'warning' }) }
      catch (e) { return }
      await deleteAssistant(row.id); this.$message.success('删除成功'); this.fetchData()
    },
    async submitForm() {
      try { await this.$refs.form.validate() } catch (e) { return }
      this.submitting = true
      try {
        if (this.isEdit) { await updateAssistant(this.form); this.$message.success('更新成功') }
        else { await createAssistant(this.form); this.$message.success('新增成功') }
        this.dialogVisible = false; this.fetchData()
      } finally { this.submitting = false }
    }
  }
}
</script>

<style lang="scss" scoped>
.assistant-page .card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
