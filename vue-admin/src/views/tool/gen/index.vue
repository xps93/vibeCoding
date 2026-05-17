<template>
  <div class="gen-page">
    <el-row :gutter="16">
      <!-- Left: table list -->
      <el-col :span="10">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span>数据库表</span>
            <el-button type="primary" size="small" icon="el-icon-refresh" @click="fetchTables">刷新</el-button>
          </div>
          <el-input v-model="tableKeyword" size="small" placeholder="搜索表名" clearable class="mb16" @input="filterTables" />
          <el-table :data="filteredTables" highlight-current-row stripe size="small" v-loading="loadingTables" height="420" @current-change="onTableSelect">
            <el-table-column prop="tableName" label="表名" min-width="160">
              <template slot-scope="{ row }">
                <span class="table-name">{{ row.tableName }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="tableComment" label="注释" min-width="100" />
          </el-table>
        </el-card>
      </el-col>

      <!-- Right: column preview -->
      <el-col :span="14">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span v-if="selectedTable">字段列表 - {{ selectedTable.tableName }} ({{ selectedTable.tableComment }})</span>
            <span v-else>字段列表</span>
            <el-button type="primary" size="small" icon="el-icon-download" :disabled="!selectedTable" @click="showConfigDialog">生成代码</el-button>
          </div>
          <el-table :data="columns" stripe size="small" v-loading="loadingColumns" height="420" empty-text="请选择左侧数据表">
            <el-table-column prop="columnName" label="字段名" min-width="150" />
            <el-table-column prop="columnType" label="类型" width="100" />
            <el-table-column prop="columnComment" label="注释" min-width="120" />
            <el-table-column prop="columnKey" label="主键" width="60" align="center">
              <template slot-scope="{ row }">
                <el-tag v-if="row.columnKey === 'PRI'" type="danger" size="mini">PK</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="isNullable" label="可空" width="60" align="center">
              <template slot-scope="{ row }">
                <span v-if="row.isNullable === 'YES'">是</span>
                <span v-else style="color:#f56c6c">否</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- Config dialog -->
    <el-dialog title="生成配置" :visible.sync="configVisible" width="450px" :close-on-click-modal="false">
      <el-form ref="genForm" :model="genConfig" :rules="genRules" label-width="100px" size="small">
        <el-form-item label="表名">
          <el-input :value="selectedTable ? selectedTable.tableName : ''" disabled />
        </el-form-item>
        <el-form-item label="包名" prop="packageName">
          <el-input v-model="genConfig.packageName" placeholder="com.example.admin" />
        </el-form-item>
        <el-form-item label="模块名" prop="moduleName">
          <el-input v-model="genConfig.moduleName" placeholder="system" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="genConfig.author" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="configVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="doGenerate" size="small" :loading="generating">生 成</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listTables, getTableColumns, generateCode } from '@/api/gen'

export default {
  name: 'GenTool',
  // 返回代码生成页面的表数据、字段数据、搜索过滤及生成配置状态
  data() {
    return {
      tables: [],
      filteredTables: [],
      tableKeyword: '',
      selectedTable: null,
      columns: [],
      loadingTables: false,
      loadingColumns: false,
      configVisible: false,
      generating: false,
      genConfig: {
        packageName: 'com.example.admin',
        moduleName: 'system',
        author: 'admin'
      },
      genRules: {
        packageName: [{ required: true, message: '必填', trigger: 'blur' }],
        moduleName: [{ required: true, message: '必填', trigger: 'blur' }],
        author: [{ required: true, message: '必填', trigger: 'blur' }]
      }
    }
  },
  // 页面创建时加载数据库表列表
  created() { this.fetchTables() },
  methods: {
    // 获取所有数据库表列表
    async fetchTables() {
      this.loadingTables = true
      try {
        const res = await listTables()
        this.tables = res.data || []
        this.filterTables()
      } catch (e) {
        this.tables = []
        this.filteredTables = []
      }
      this.loadingTables = false
    },
    // 根据表名关键字过滤数据库表列表
    filterTables() {
      const kw = this.tableKeyword.toLowerCase()
      if (!kw) {
        this.filteredTables = this.tables
      } else {
        this.filteredTables = this.tables.filter(t => t.tableName.toLowerCase().includes(kw))
      }
    },
    // 选中表时加载该表的字段列表
    async onTableSelect(row) {
      this.selectedTable = row
      if (!row) { this.columns = []; return }
      this.loadingColumns = true
      try {
        const res = await getTableColumns(row.tableName)
        this.columns = res.data || []
      } catch (e) {
        this.columns = []
      }
      this.loadingColumns = false
    },
    // 打开代码生成配置弹窗
    showConfigDialog() {
      this.configVisible = true
      this.$nextTick(() => this.$refs.genForm && this.$refs.genForm.clearValidate())
    },
    // 执行代码生成操作
    async doGenerate() {
      this.$refs.genForm.validate(async valid => {
        if (!valid || !this.selectedTable) return
        this.generating = true
        try {
          await generateCode({
            tableName: this.selectedTable.tableName,
            packageName: this.genConfig.packageName,
            moduleName: this.genConfig.moduleName,
            author: this.genConfig.author
          })
          this.$message.success('代码生成成功')
          this.configVisible = false
        } catch (e) {
          this.$message.error('生成失败')
        }
        this.generating = false
      })
    }
  }
}
</script>

<style scoped>
.gen-page { margin: 16px; }
.mb16 { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.table-name { font-family: 'Courier New', monospace; font-weight: bold; }
</style>
