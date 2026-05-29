<template>
  <div class="ai-model-page">
    <!-- 表格 -->
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>AI模型配置</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('ai:model:create')">新增模型</el-button>
      </div>
      <el-table :data="models" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="name" label="模型名称" min-width="120" />
        <el-table-column prop="modelKey" label="模型标识" min-width="130" />
        <el-table-column label="提供商" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag size="mini" :type="providerType(row.provider)">{{ row.provider }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="能力" min-width="180">
          <template slot-scope="{ row }">
            <el-tag v-for="cap in getCapabilities(row.capabilities)" :key="cap.value" size="mini" :type="cap.type" style="margin-right:4px">
              {{ cap.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="API地址" min-width="180" show-overflow-tooltip>
          <template slot-scope="{ row }">
            <span v-if="row.apiBaseUrl">{{ row.apiBaseUrl }}</span>
            <span v-else style="color:#c0c4cc">使用默认地址</span>
          </template>
        </el-table-column>
        <el-table-column label="密钥" width="130" align="center">
          <template slot-scope="{ row }">
            <span v-if="row.apiKey" style="font-family:monospace">{{ row.apiKey }}</span>
            <span v-else style="color:#c0c4cc">使用默认密钥</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-switch :value="row.status === 0" active-color="#13ce66" inactive-color="#ff4949" @change="(val) => handleToggleStatus(row, val)" v-if="hasPerm('ai:model:edit')" />
            <el-tag v-else :type="row.status === 0 ? 'success' : 'danger'" size="mini">{{ row.status === 0 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('ai:model:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('ai:model:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="560px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="100px" size="small">
        <el-form-item label="模型名称" prop="name">
          <el-input v-model="form.name" placeholder="如：DeepSeek Chat" />
        </el-form-item>
        <el-form-item label="模型标识" prop="modelKey">
          <el-input v-model="form.modelKey" placeholder="API调用的model参数，如deepseek-chat" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="提供商" prop="provider">
          <el-select v-model="form.provider" placeholder="选择提供商" style="width:100%">
            <el-option label="DeepSeek" value="deepseek" />
            <el-option label="OpenAI" value="openai" />
            <el-option label="通义千问" value="qwen" />
            <el-option label="智谱清言" value="zhipu" />
            <el-option label="月之暗面" value="moonshot" />
          </el-select>
        </el-form-item>
        <el-form-item label="能力标签">
          <el-checkbox-group v-model="capabilityList">
            <el-checkbox label="chat">对话</el-checkbox>
            <el-checkbox label="code">代码</el-checkbox>
            <el-checkbox label="reasoning">推理</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="API地址">
          <el-input v-model="form.apiBaseUrl" placeholder="留空使用提供商默认地址" />
          <div style="font-size:11px;color:#909399;margin-top:2px">示例: https://api.deepseek.com</div>
        </el-form-item>
        <el-form-item label="API密钥">
          <el-input v-model="form.apiKey" placeholder="留空使用全局配置密钥" show-password />
          <div style="font-size:11px;color:#909399;margin-top:2px">密钥将安全存储，编辑时以脱敏形式展示</div>
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="form.sort" :min="0" :max="999" controls-position="right" style="width:140px" />
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
import { listModels, createModel, updateModel, deleteModel, toggleModelStatus } from '../../../api/aiModel'
import { mapState } from 'vuex'

export default {
  name: 'AiModelManagement',
  data() {
    return {
      models: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      submitting: false,
      isEdit: false,
      capabilityList: [],
      form: {
        id: null,
        name: '',
        modelKey: '',
        provider: 'deepseek',
        capabilities: '',
        apiBaseUrl: '',
        apiKey: '',
        sort: 0
      },
      formRules: {
        name: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
        modelKey: [{ required: true, message: '请输入模型标识', trigger: 'blur' }],
        provider: [{ required: true, message: '请选择提供商', trigger: 'change' }]
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
        const { data } = await listModels()
        this.models = data && data.data ? data.data : (data || [])
      } finally { this.loading = false }
    },
    providerType(provider) {
      const map = { deepseek: '', openai: 'success', qwen: 'warning', zhipu: '', moonshot: 'danger' }
      return map[provider] || 'info'
    },
    getCapabilities(caps) {
      if (!caps) return []
      const capMap = { chat: { label: '对话', type: 'success' }, code: { label: '代码', type: 'warning' }, reasoning: { label: '推理', type: '' } }
      return caps.split(',').map(c => capMap[c.trim()] || { label: c, type: 'info' })
    },
    handleAdd() {
      this.isEdit = false
      this.dialogTitle = '新增AI模型'
      this.form = { id: null, name: '', modelKey: '', provider: 'deepseek', capabilities: '', apiBaseUrl: '', apiKey: '', sort: 0 }
      this.capabilityList = []
      this.dialogVisible = true
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑AI模型'
      this.form = { ...row }
      this.capabilityList = row.capabilities ? row.capabilities.split(',').map(c => c.trim()).filter(Boolean) : []
      this.dialogVisible = true
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.clearValidate() })
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除模型 "' + row.name + '"？', '提示', { type: 'warning' })
      } catch (e) { return }
      await deleteModel(row.id)
      this.$message.success('删除成功')
      this.fetchData()
    },
    async handleToggleStatus(row, val) {
      const status = val ? 0 : 1
      await toggleModelStatus(row.id, status)
      this.$message.success(status === 0 ? '已启用' : '已禁用')
      this.fetchData()
    },
    async submitForm() {
      try {
        await this.$refs.form.validate()
      } catch (e) { return }
      this.submitting = true
      try {
        this.form.capabilities = this.capabilityList.join(',')
        if (this.isEdit) {
          await updateModel(this.form)
          this.$message.success('更新成功')
        } else {
          await createModel(this.form)
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
.ai-model-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
