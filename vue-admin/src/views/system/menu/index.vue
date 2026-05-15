<template>
  <div class="menu-page">
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>菜单列表</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="hasPerm('system:menu:create')">新增</el-button>
      </div>
      <el-table :data="menus" border stripe size="small" row-key="id" lazy :load="loadChildren" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" v-loading="loading">
        <el-table-column prop="name" label="菜单名称" width="160"></el-table-column>
        <el-table-column prop="icon" label="图标" width="70" align="center">
          <template slot-scope="{ row }">
            <i :class="'el-icon-' + (row.icon ? row.icon.toLowerCase() : 'menu')"></i>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="类型" width="70" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.menuType === 'M'" size="mini" type="primary">目录</el-tag>
            <el-tag v-else-if="row.menuType === 'C'" size="mini" type="success">菜单</el-tag>
            <el-tag v-else size="mini" type="info">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由地址" width="160"></el-table-column>
        <el-table-column prop="perms" label="权限标识" width="180"></el-table-column>
        <el-table-column prop="sort" label="排序" width="60" align="center"></el-table-column>
        <el-table-column label="状态" width="70" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="mini">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-plus" @click="handleAddChild(row)" v-if="hasPerm('system:menu:create')">新增</el-button>
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEdit(row)" v-if="hasPerm('system:menu:edit')">修改</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)" v-if="hasPerm('system:menu:delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" :rules="formRules" label-width="80px" size="small">
        <el-form-item label="上级菜单">
          <el-cascader v-model="form.parentId" :options="menuTreeOptions" :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true }" clearable style="width:100%">
            <template slot-scope="{ data }">{{ data.name }}</template>
          </el-cascader>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由地址" v-if="form.menuType !== 'F'">
          <el-input v-model="form.path" placeholder="如 /system/user" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="form.menuType === 'C'">
          <el-input v-model="form.component" placeholder="如 system/user/index" />
        </el-form-item>
        <el-form-item label="权限标识" v-if="form.menuType === 'F'">
          <el-input v-model="form.perms" placeholder="如 system:user:list" />
        </el-form-item>
        <el-form-item label="图标" v-if="form.menuType !== 'F'">
          <el-input v-model="form.icon" placeholder="如 User" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" size="small"></el-input-number>
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
  </div>
</template>

<script>
import { listMenus, getAllMenus, addMenu, updateMenu, deleteMenu } from '../../../api/menu'
import { mapState } from 'vuex'

export default {
  name: 'MenuManagement',
  data() {
    return {
      menus: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: { parentId: [0], name: '', menuType: 'M', path: '', component: '', icon: '', perms: '', sort: 0, status: 0 },
      formRules: {
        name: [{ required: true, message: '必填', trigger: 'blur' }],
        menuType: [{ required: true, message: '必选', trigger: 'change' }]
      },
      menuTreeOptions: []
    }
  },
  computed: { ...mapState(['permissions']) },
  created() { this.fetchData() },
  methods: {
    hasPerm(perm) { return this.permissions.includes(perm) },
    async fetchData() {
      this.loading = true
      const res = await listMenus()
      this.menus = res.data || []
      this.loading = false
    },
    async fetchMenuTree() {
      const res = await getAllMenus()
      this.menuTreeOptions = [{ id: 0, name: '根目录', children: res.data || [] }]
    },
    loadChildren(tree, treeNode, resolve) {
      resolve([])
    },
    handleAdd() {
      this.dialogTitle = '新增菜单'
      this.form = { parentId: [0], name: '', menuType: 'M', path: '', component: '', icon: '', perms: '', sort: 0, status: 0 }
      this.dialogVisible = true
      this.fetchMenuTree()
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleAddChild(row) {
      this.dialogTitle = '新增菜单'
      this.form = { parentId: [row.id], name: '', menuType: 'C', path: '', component: '', icon: '', perms: '', sort: 0, status: 0 }
      this.dialogVisible = true
      this.fetchMenuTree()
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleEdit(row) {
      this.dialogTitle = '修改菜单'
      this.form = {
        id: row.id, parentId: row.parentId ? [row.parentId] : [0],
        name: row.name, menuType: row.menuType, path: row.path || '',
        component: row.component || '', icon: row.icon || '', perms: row.perms || '',
        sort: row.sort, status: row.status
      }
      this.dialogVisible = true
      this.fetchMenuTree()
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        const data = { ...this.form }
        data.parentId = Array.isArray(data.parentId) ? data.parentId[data.parentId.length - 1] : data.parentId
        if (data.menuType === 'M') { data.path = '/system/' + Date.now(); data.component = 'Layout' }
        if (data.id) {
          await updateMenu(data)
        } else {
          await addMenu(data)
        }
        this.dialogVisible = false
        this.$message.success('操作成功')
        this.fetchData()
      })
    },
    async handleDelete(row) {
      this.$confirm(`确认删除菜单"${row.name}"?`, '提示', { type: 'warning' }).then(async () => {
        await deleteMenu(row.id)
        this.$message.success('删除成功')
        this.fetchData()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
