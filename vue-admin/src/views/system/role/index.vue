<template>
  <div class="role-page">
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>角色列表</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('system:role:create')">新增</el-button>
      </div>
      <el-table :data="roles" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column prop="roleName" label="角色名称" width="140"></el-table-column>
        <el-table-column prop="roleKey" label="角色标识" width="120"></el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template slot-scope="{ row }">{{ row.createTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('system:role:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-setting" @click="handlePermission(row)" v-if="hasPerm('system:role:edit')">分配权限</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('system:role:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Role Dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" :rules="formRules" label-width="80px" size="small">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="form.roleKey" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="状态">
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

    <!-- Permission Dialog -->
    <el-dialog title="分配权限" :visible.sync="permDialogVisible" width="400px">
      <el-tree
        ref="permTree"
        :data="allMenus"
        node-key="id"
        show-checkbox
        default-expand-all
        :props="{ label: 'name', children: 'children' }"
        check-strictly
      />
      <span slot="footer">
        <el-button @click="permDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="submitPermission" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listRoles, getRole, addRole, updateRole, deleteRole, getRoleMenuIds, getAllMenus } from '../../../api/role'
import { mapState } from 'vuex'

export default {
  name: 'RoleManagement',
  // 返回角色列表数据、表单数据和权限弹窗相关状态
  data() {
    return {
      roles: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: { roleName: '', roleKey: '', status: 0 },
      formRules: {
        roleName: [{ required: true, message: '必填', trigger: 'blur' }],
        roleKey: [{ required: true, message: '必填', trigger: 'blur' }]
      },
      permDialogVisible: false,
      currentRoleId: null,
      allMenus: []
    }
  },
  // 从 Vuex 获取当前用户的权限列表
  computed: { ...mapState(['permissions']) },
  // 页面创建时加载角色列表
  created() { this.fetchData() },
  methods: {
    // 检查当前用户是否拥有指定权限
    hasPerm(perm) { return this.permissions.includes(perm) },
    // 获取角色列表数据
    async fetchData() {
      this.loading = true
      const res = await listRoles()
      this.roles = res.data || []
      this.loading = false
    },
    // 打开新增角色弹窗
    handleAdd() {
      this.dialogTitle = '新增角色'
      this.form = { roleName: '', roleKey: '', status: 0 }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 打开修改角色弹窗并回填数据
    handleEdit(row) {
      this.dialogTitle = '修改角色'
      this.form = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    // 提交新增或修改角色的表单
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (this.form.id) {
          await updateRole(this.form)
        } else {
          await addRole(this.form)
        }
        this.dialogVisible = false
        this.$message.success('操作成功')
        this.fetchData()
      })
    },
    // 确认后删除指定角色
    async handleDelete(row) {
      this.$confirm(`确认删除角色"${row.roleName}"?`, '提示', { type: 'warning' }).then(async () => {
        await deleteRole(row.id)
        this.$message.success('删除成功')
        this.fetchData()
      }).catch(() => {})
    },
    // 打开分配权限弹窗，加载菜单树并回填已选权限
    async handlePermission(row) {
      this.currentRoleId = row.id
      const menuRes = await getAllMenus()
      this.allMenus = menuRes.data || []
      const menuIdsRes = await getRoleMenuIds(row.id)
      const checkedIds = menuIdsRes.data || []
      this.permDialogVisible = true
      this.$nextTick(() => {
        this.$refs.permTree.setCheckedKeys(checkedIds)
      })
    },
    // 提交角色的权限分配
    async submitPermission() {
      if (!this.currentRoleId) return
      const checkedKeys = this.$refs.permTree.getCheckedKeys()
      await updateRole({ id: this.currentRoleId, menuIds: checkedKeys })
      this.permDialogVisible = false
      this.$message.success('权限分配成功')
      this.fetchData()
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
