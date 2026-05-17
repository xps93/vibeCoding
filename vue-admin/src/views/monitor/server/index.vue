<template>
  <div class="server-page">
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header"><span><i class="el-icon-monitor"></i> 服务器信息</span></div>
          <el-table :data="sysData" border stripe size="small" style="width:100%">
            <el-table-column prop="label" label="属性" width="120" />
            <el-table-column prop="value" label="值" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header"><span><i class="el-icon-coffee-cup"></i> JVM信息</span></div>
          <el-table :data="jvmData" border stripe size="small" style="width:100%">
            <el-table-column prop="label" label="属性" width="120" />
            <el-table-column prop="value" label="值" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header"><span><i class="el-icon-data-board"></i> 内存使用</span></div>
          <div style="padding:10px">
            <div style="margin-bottom:15px">
              <span>总内存: {{ serverInfo.jvm ? serverInfo.jvm.totalMemory : '-' }}</span>
              <el-progress :percentage="memoryPercent" :color="memoryColor" style="margin-top:5px" />
            </div>
            <div style="margin-bottom:15px">
              <span>已用内存: {{ usedMemory }}</span>
            </div>
            <div>
              <span>最大内存: {{ serverInfo.jvm ? serverInfo.jvm.maxMemory : '-' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getServerInfo } from '../../../api/server'

export default {
  name: 'ServerMonitor',
  // 返回服务器监控页面加载状态和服务信息
  data() {
    return {
      loading: false,
      serverInfo: {}
    }
  },
  computed: {
    // 格式化操作系统信息为表格数据
    sysData() {
      if (!this.serverInfo.sys) return []
      const s = this.serverInfo.sys
      return [
        { label: '操作系统', value: s.osName },
        { label: '系统架构', value: s.osArch },
        { label: '系统版本', value: s.osVersion },
        { label: 'CPU核心数', value: s.cpuCores + ' 核' }
      ]
    },
    // 格式化 JVM 信息为表格数据
    jvmData() {
      if (!this.serverInfo.jvm) return []
      const j = this.serverInfo.jvm
      return [
        { label: 'Java版本', value: j.javaVersion },
        { label: 'JavaHome', value: j.javaHome },
        { label: '总内存', value: j.totalMemory },
        { label: '空闲内存', value: j.freeMemory }
      ]
    },
    // 计算已使用的内存量
    usedMemory() {
      if (!this.serverInfo.jvm) return '-'
      const total = parseInt(this.serverInfo.jvm.totalMemory)
      const free = parseInt(this.serverInfo.jvm.freeMemory)
      return (total - free) + 'MB'
    },
    // 计算内存使用百分比
    memoryPercent() {
      if (!this.serverInfo.jvm) return 0
      const total = parseInt(this.serverInfo.jvm.totalMemory) || 1
      const free = parseInt(this.serverInfo.jvm.freeMemory) || 0
      return Math.round(((total - free) / total) * 100)
    },
    // 根据内存使用百分比返回对应颜色
    memoryColor() {
      const pct = this.memoryPercent
      if (pct > 80) return '#f56c6c'
      if (pct > 50) return '#e6a23c'
      return '#67c23a'
    }
  },
  // 页面创建时加载服务器信息
  created() { this.fetchData() },
  methods: {
    // 获取服务器监控数据
    async fetchData() {
      this.loading = true
      try {
        const res = await getServerInfo()
        this.serverInfo = res.data || {}
      } catch (e) {
        this.serverInfo = {}
      }
      this.loading = false
    }
  }
}
</script>

<style scoped>
</style>
