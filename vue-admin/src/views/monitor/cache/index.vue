<template>
  <div class="cache-page">
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header"><span><i class="el-icon-key"></i> Token 数量</span></div>
          <div style="text-align:center;padding:20px">
            <span style="font-size:48px;font-weight:bold;color:#409EFF">{{ cacheInfo.tokenCount || 0 }}</span>
            <p style="color:#909399;margin-top:10px">当前在线 Token 数</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header"><span><i class="el-icon-user"></i> 用户会话数</span></div>
          <div style="text-align:center;padding:20px">
            <span style="font-size:48px;font-weight:bold;color:#67C23A">{{ cacheInfo.userSessionCount || 0 }}</span>
            <p style="color:#909399;margin-top:10px">当前活跃用户会话数</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header"><span><i class="el-icon-coin"></i> 缓存类型</span></div>
          <div style="text-align:center;padding:20px">
            <span style="font-size:16px;color:#303133">{{ cacheInfo.cacheType || '无' }}</span>
            <p style="color:#909399;margin-top:10px">缓存实现方式</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getCacheInfo } from '../../../api/cache'

export default {
  name: 'CacheMonitor',
  // 返回缓存监控页面加载状态和缓存信息
  data() {
    return {
      loading: false,
      cacheInfo: {}
    }
  },
  // 页面创建时加载缓存信息
  created() { this.fetchData() },
  methods: {
    // 获取缓存监控数据
    async fetchData() {
      this.loading = true
      try {
        const res = await getCacheInfo()
        this.cacheInfo = res.data || {}
      } catch (e) {
        this.cacheInfo = {}
      }
      this.loading = false
    }
  }
}
</script>

<style scoped>
</style>
