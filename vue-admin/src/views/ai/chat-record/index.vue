<template>
  <div class="chat-record-page">
    <!-- 搜索栏 -->
    <el-card shadow="hover" style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item label="对话标题">
          <el-input v-model="queryParams.keyword" placeholder="搜索标题关键词" clearable @keyup.enter.native="handleSearch" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="搜索用户名" clearable @keyup.enter.native="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>聊天记录</span>
      </div>
      <el-table :data="tableData" border stripe size="small" v-loading="loading">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="id" label="对话ID" width="80" align="center" />
        <el-table-column prop="title" label="对话标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="username" label="用户" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag size="mini" type="info">{{ row.username }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="modelId" label="模型" width="140" show-overflow-tooltip />
        <el-table-column prop="messageCount" label="消息数" width="80" align="center" />
        <el-table-column label="创建时间" width="160" align="center">
          <template slot-scope="{ row }">{{ row.createTime }}</template>
        </el-table-column>
        <el-table-column label="更新时间" width="160" align="center">
          <template slot-scope="{ row }">{{ row.updateTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" icon="el-icon-view" @click="handleView(row)">查看</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; text-align: right">
        <el-pagination
          :current-page="queryParams.page"
          :page-sizes="[10, 20, 50]"
          :page-size="queryParams.pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 查看消息弹窗 -->
    <el-dialog
      :title="'对话详情 - ' + currentConv.title"
      :visible.sync="msgDialogVisible"
      width="760px"
      :close-on-click-modal="false"
    >
      <div class="msg-container">
        <div v-for="(msg, i) in messages" :key="i" :class="['msg-item', msg.role]">
          <div class="msg-role">
            <el-tag :type="msg.role === 'user' ? '' : 'success'" size="mini">
              {{ msg.role === 'user' ? '用户' : 'AI' }}
            </el-tag>
            <span class="msg-time">{{ msg.createTime }}</span>
          </div>
          <div class="msg-content" v-html="formatContent(msg.content)"></div>
        </div>
        <el-empty v-if="!loadingMsg && messages.length === 0" description="暂无消息" />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listChatRecords, getChatMessages, deleteChatRecord } from '../../../api/aiChatRecord'

export default {
  name: 'ChatRecordManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      queryParams: { page: 1, pageSize: 10, keyword: '', username: '' },
      msgDialogVisible: false,
      loadingMsg: false,
      messages: [],
      currentConv: {}
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await listChatRecords(this.queryParams)
        this.tableData = res.data && res.data.list ? res.data.list : []
        this.total = res.data && res.data.total ? res.data.total : 0
      } finally { this.loading = false }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchData()
    },
    handleReset() {
      this.queryParams = { page: 1, pageSize: 10, keyword: '', username: '' }
      this.fetchData()
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.fetchData()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchData()
    },
    async handleView(row) {
      this.currentConv = row
      this.msgDialogVisible = true
      this.messages = []
      this.loadingMsg = true
      try {
        const res = await getChatMessages(row.id)
        this.messages = res.data && res.data.messages ? res.data.messages : []
      } finally { this.loadingMsg = false }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除对话 "' + row.title + '" 及其所有消息？', '提示', { type: 'warning' })
      } catch (e) { return }
      await deleteChatRecord(row.id)
      this.$message.success('删除成功')
      this.fetchData()
    },
    formatContent(content) {
      if (!content) return ''
      return content.replace(/\n/g, '<br>')
    }
  }
}
</script>

<style lang="scss" scoped>
.chat-record-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.msg-container {
  max-height: 500px;
  overflow-y: auto;

  .msg-item {
    margin-bottom: 16px;
    padding: 12px;
    border-radius: 8px;
    background: #f5f7fa;

    &.user { background: #ecf5ff; }
    &.assistant { background: #f0f9eb; }
  }

  .msg-role {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }

  .msg-time {
    font-size: 12px;
    color: #909399;
  }

  .msg-content {
    font-size: 14px;
    line-height: 1.6;
    color: #333;
    word-break: break-word;
  }
}
</style>
