<template>
  <div class="site-config-page">
    <el-card shadow="hover">
      <div slot="header">
        <span>站点配置</span>
      </div>
      <el-table :data="tableData" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="configName" label="配置名称" width="150" />
        <el-table-column prop="configKey" label="配置键" width="160">
          <template slot-scope="{ row }"><el-tag size="mini" type="info">{{ row.configKey }}</el-tag></template>
        </el-table-column>
        <el-table-column label="配置值" min-width="250">
          <template slot-scope="{ row }">
            <template v-if="editingId === row.id">
              <el-input v-model="editValue" size="small" style="width: calc(100% - 80px)" />
              <el-button type="success" size="mini" icon="el-icon-check" @click="saveEdit(row)" style="margin-left:6px">保存</el-button>
              <el-button type="info" size="mini" icon="el-icon-close" @click="cancelEdit">取消</el-button>
            </template>
            <span v-else>{{ row.configValue || '(空)' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="startEdit(row)" v-if="hasPerm('system:site-config:edit') && editingId !== row.id">修改</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { listSiteConfigs, updateSiteConfig } from '../../../api/siteConfig'
import { mapState } from 'vuex'

export default {
  name: 'SiteConfigManagement',
  data() {
    return { tableData: [], loading: false, editingId: null, editValue: '' }
  },
  computed: { ...mapState(['permissions']) },
  mounted() { this.fetchData() },
  methods: {
    hasPerm(perm) { return this.permissions.indexOf(perm) > -1 },
    async fetchData() {
      this.loading = true
      try {
        const res = await listSiteConfigs()
        this.tableData = Array.isArray(res.data) ? res.data : []
      } finally { this.loading = false }
    },
    startEdit(row) { this.editingId = row.id; this.editValue = row.configValue },
    cancelEdit() { this.editingId = null; this.editValue = '' },
    async saveEdit(row) {
      await updateSiteConfig({ id: row.id, configValue: this.editValue })
      row.configValue = this.editValue
      this.$message.success('保存成功')
      this.cancelEdit()
    }
  }
}
</script>
