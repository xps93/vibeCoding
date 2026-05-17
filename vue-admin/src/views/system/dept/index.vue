<template>
  <div class="dept-page">
    <!-- Search -->
    <el-card shadow="hover" class="mb16">
      <el-form :inline="true" size="small">
        <el-form-item label="关键字">
          <el-input v-model="keyword" placeholder="部门名称" clearable @keyup.enter="fetchData" />
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
        <span>部门列表</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('system:dept:create')">新增</el-button>
      </div>
      <el-table :data="depts" border stripe size="small" v-loading="loading" row-key="id" default-expand-all :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
        <el-table-column prop="deptName" label="部门名称" min-width="200"></el-table-column>
        <el-table-column prop="orderNum" label="排序" width="80" align="center"></el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-plus" @click="handleAdd(row)" v-if="hasPerm('system:dept:create')">新增</el-button>
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('system:dept:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('system:dept:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="formRules" label-width="80px" size="small">
        <el-form-item label="上级部门" prop="parentId">
          <el-cascader v-model="form.parentId" :options="deptOptions" :props="cascaderProps" clearable style="width:100%" />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" />
        </el-form-item>
        <el-form-item label="显示排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" />
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="form.leader" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
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
import { getDeptTree, addDept, updateDept, deleteDept } from '../../../api/dept'
import { mapState } from 'vuex'

export default {
  name: 'DeptManagement',
  // 返回部门列表数据、搜索关键字和表单状态
  data() {
    return {
      depts: [],
      deptOptions: [],
      keyword: '',
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: { parentId: [], deptName: '', orderNum: 0, leader: '', phone: '', email: '', status: 0 },
      formRules: {
        deptName: [{ required: true, message: '必填', trigger: 'blur' }]
      },
      cascaderProps: {
        value: 'id',
        label: 'deptName',
        children: 'children',
        checkStrictly: true,
        emitPath: false
      }
    }
  },
  // 从 Vuex 获取当前用户的权限列表
  computed: { ...mapState(['permissions']) },
  // 页面创建时加载部门树
  created() { this.fetchData() },
  methods: {
    // 检查当前用户是否拥有指定权限
    hasPerm(perm) { return this.permissions.includes(perm) },
    // 获取部门树数据
    async fetchData() {
      this.loading = true
      const res = await getDeptTree()
      this.depts = res.data || []
      this.deptOptions = this.buildDeptOptions(this.depts)
      this.loading = false
    },
    // 递归构建级联选择器所需的部门树结构
    buildDeptOptions(depts) {
      return depts.map(d => ({
        id: d.id,
        deptName: d.deptName,
        children: d.children ? this.buildDeptOptions(d.children) : []
      }))
    },
    // 重置搜索关键字并重新查询
    resetSearch() { this.keyword = ''; this.fetchData() },
    // 打开新增部门弹窗，可指定上级部门
    handleAdd(row) {
      this.dialogTitle = '新增部门'
      this.form = { parentId: row ? row.id : [], deptName: '', orderNum: 0, leader: '', phone: '', email: '', status: 0 }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 打开修改部门弹窗并回填数据
    handleEdit(row) {
      this.dialogTitle = '修改部门'
      this.form = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 提交新增或修改部门的表单
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (this.form.id) {
          await updateDept(this.form)
        } else {
          await addDept(this.form)
        }
        this.dialogVisible = false
        this.$message.success('操作成功')
        this.fetchData()
      })
    },
    // 确认后删除指定部门
    async handleDelete(row) {
      this.$confirm(`确认删除部门"${row.deptName}"?`, '提示', { type: 'warning' }).then(async () => {
        await deleteDept(row.id)
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
