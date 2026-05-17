<template>
  <div class="dict-page">
    <!-- Dict Type Search -->
    <el-card shadow="hover" class="mb16">
      <el-form :inline="true" size="small">
        <el-form-item label="关键字">
          <el-input v-model="keyword" placeholder="字典名称/字典类型" clearable @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="fetchData">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Dict Type Table -->
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>字典类型</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('system:dict:create')">新增</el-button>
      </div>
      <el-table :data="dictTypes" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column prop="dictName" label="字典名称" min-width="150"></el-table-column>
        <el-table-column prop="dictType" label="字典类型" min-width="150"></el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-menu" @click="handleDictData(row)">字典数据</el-button>
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('system:dict:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('system:dict:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Dict Type Dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="80px" size="small">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" :disabled="!!form.id" />
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

    <!-- Dict Data Dialog -->
    <el-dialog :title="dataDialogTitle" :visible.sync="dataDialogVisible" width="650px" :close-on-click-modal="false">
      <div class="mb16" style="text-align:right">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleDataAdd" v-if="hasPerm('system:dict:create')">新增字典数据</el-button>
      </div>
      <el-table :data="dictDataList" border stripe size="small" v-loading="dataLoading">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column prop="dictLabel" label="字典标签" width="120"></el-table-column>
        <el-table-column prop="dictValue" label="字典键值" width="120"></el-table-column>
        <el-table-column prop="dictSort" label="字典排序" width="80" align="center"></el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleDataEdit(row)" v-if="hasPerm('system:dict:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDataDelete(row)" v-if="hasPerm('system:dict:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Data Item Dialog (nested) -->
      <el-dialog :title="dataItemDialogTitle" :visible.sync="dataItemDialogVisible" width="500px" :close-on-click-modal="false" append-to-body>
        <el-form ref="dataForm" :model="dataForm" :rules="dataFormRules" label-width="80px" size="small">
          <el-form-item label="字典标签" prop="dictLabel">
            <el-input v-model="dataForm.dictLabel" />
          </el-form-item>
          <el-form-item label="字典键值" prop="dictValue">
            <el-input v-model="dataForm.dictValue" />
          </el-form-item>
          <el-form-item label="显示排序" prop="dictSort">
            <el-input-number v-model="dataForm.dictSort" :min="0" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="dataForm.status">
              <el-radio :label="0">正常</el-radio>
              <el-radio :label="1">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="dataItemDialogVisible = false" size="small">取 消</el-button>
          <el-button type="primary" @click="submitDataForm" size="small">确 定</el-button>
        </span>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script>
import { listDictTypes, addDictType, updateDictType, deleteDictType, listDictDataByTypeId, addDictData, updateDictData, deleteDictData } from '../../../api/dict'
import { mapState } from 'vuex'

export default {
  name: 'DictManagement',
  // 返回字典类型和字典数据相关的所有状态
  data() {
    return {
      dictTypes: [],
      keyword: '',
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: { dictName: '', dictType: '', status: 0 },
      formRules: {
        dictName: [{ required: true, message: '必填', trigger: 'blur' }],
        dictType: [{ required: true, message: '必填', trigger: 'blur' }]
      },
      // 字典数据弹窗状态
      dataDialogVisible: false,
      dataDialogTitle: '',
      dataLoading: false,
      dictDataList: [],
      currentDictTypeId: null,
      dataItemDialogVisible: false,
      dataItemDialogTitle: '',
      dataForm: { dictLabel: '', dictValue: '', dictSort: 0, status: 0 },
      dataFormRules: {
        dictLabel: [{ required: true, message: '必填', trigger: 'blur' }],
        dictValue: [{ required: true, message: '必填', trigger: 'blur' }]
      }
    }
  },
  // 从 Vuex 获取当前用户的权限列表
  computed: { ...mapState(['permissions']) },
  // 页面创建时加载字典类型列表
  created() { this.fetchData() },
  methods: {
    // 检查当前用户是否拥有指定权限
    hasPerm(perm) { return this.permissions.includes(perm) },
    // 获取字典类型列表数据
    async fetchData() {
      this.loading = true
      const res = await listDictTypes({ keyword: this.keyword || undefined })
      this.dictTypes = res.data || []
      this.loading = false
    },
    // 重置搜索关键字并重新查询
    resetSearch() { this.keyword = ''; this.fetchData() },
    // 打开新增字典类型弹窗
    handleAdd() {
      this.dialogTitle = '新增字典类型'
      this.form = { dictName: '', dictType: '', status: 0 }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 打开修改字典类型弹窗并回填数据
    handleEdit(row) {
      this.dialogTitle = '修改字典类型'
      this.form = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 提交新增或修改字典类型的表单
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (this.form.id) {
          await updateDictType(this.form)
        } else {
          await addDictType(this.form)
        }
        this.dialogVisible = false
        this.$message.success('操作成功')
        this.fetchData()
      })
    },
    // 确认后删除指定字典类型
    async handleDelete(row) {
      this.$confirm(`确认删除字典类型"${row.dictName}"?`, '提示', { type: 'warning' }).then(async () => {
        await deleteDictType(row.id)
        this.$message.success('删除成功')
        this.fetchData()
      }).catch(() => {})
    },
    // 打开字典数据管理弹窗
    async handleDictData(row) {
      this.currentDictTypeId = row.id
      this.dataDialogTitle = `字典数据 - ${row.dictName}`
      this.dataDialogVisible = true
      await this.fetchDictData()
    },
    // 获取当前字典类型下的所有字典数据
    async fetchDictData() {
      this.dataLoading = true
      const res = await listDictDataByTypeId(this.currentDictTypeId)
      this.dictDataList = res.data || []
      this.dataLoading = false
    },
    // 打开新增字典数据弹窗
    handleDataAdd() {
      this.dataItemDialogTitle = '新增字典数据'
      this.dataForm = { dictLabel: '', dictValue: '', dictSort: 0, status: 0 }
      this.dataItemDialogVisible = true
      this.$nextTick(() => this.$refs.dataForm && this.$refs.dataForm.clearValidate())
    },
    // 打开修改字典数据弹窗并回填数据
    handleDataEdit(row) {
      this.dataItemDialogTitle = '修改字典数据'
      this.dataForm = { ...row }
      this.dataItemDialogVisible = true
      this.$nextTick(() => this.$refs.dataForm && this.$refs.dataForm.clearValidate())
    },
    // 提交新增或修改字典数据的表单
    async submitDataForm() {
      this.$refs.dataForm.validate(async valid => {
        if (!valid) return
        const data = { ...this.dataForm, dictTypeId: this.currentDictTypeId }
        if (this.dataForm.id) {
          await updateDictData(data)
        } else {
          await addDictData(data)
        }
        this.dataItemDialogVisible = false
        this.$message.success('操作成功')
        this.fetchDictData()
      })
    },
    // 确认后删除指定字典数据项
    async handleDataDelete(row) {
      this.$confirm(`确认删除字典数据"${row.dictLabel}"?`, '提示', { type: 'warning' }).then(async () => {
        await deleteDictData(row.id)
        this.$message.success('删除成功')
        this.fetchDictData()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
