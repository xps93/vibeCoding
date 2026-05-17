<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" v-for="card in statCards" :key="card.key">
        <div class="stat-body">
          <div class="stat-icon" :style="{ background: card.bg }">
            <i :class="card.icon"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value"><span class="count-up">{{ card.value }}</span></div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </div>
        <div class="stat-footer" :style="{ background: card.bg }"></div>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 系统信息 -->
      <el-col :span="14">
        <el-card shadow="never" class="sys-card">
          <div slot="header" class="card-header">
            <i class="el-icon-monitor"></i>
            <span>系统信息</span>
          </div>
          <div class="sys-info" v-if="system">
            <div class="info-item">
              <span class="info-label">操作系统</span>
              <span class="info-value">{{ system.osName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">CPU 核心数</span>
              <span class="info-value">{{ system.cpuCores }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">已用内存</span>
              <span class="info-value">{{ system.usedMemory }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">最大内存</span>
              <span class="info-value">{{ system.maxMemory }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Java 版本</span>
              <span class="info-value">{{ system.javaVersion }}</span>
            </div>
          </div>
          <el-skeleton :rows="5" animated v-else />
        </el-card>
      </el-col>

      <!-- 快捷入口 + 用户信息 -->
      <el-col :span="10">
        <el-card shadow="never" class="quick-card">
          <div slot="header" class="card-header">
            <i class="el-icon-s-home"></i>
            <span>欢迎回来</span>
          </div>
          <div class="welcome" v-if="user">
            <div class="welcome-avatar">
              <el-avatar :size="56" icon="el-icon-user-solid" />
            </div>
            <div class="welcome-text">
              <h3>{{ user.nickname || user.username }}</h3>
              <p v-if="roles && roles.length">
                <el-tag size="mini" v-for="r in roles" :key="r" style="margin-right:6px">{{ r }}</el-tag>
              </p>
            </div>
          </div>
          <el-divider></el-divider>
          <div class="quick-links">
            <div class="quick-link" v-for="link in quickLinks" :key="link.path" @click="$router.push(link.path)">
              <i :class="link.icon" :style="{ color: link.color }"></i>
              <span>{{ link.label }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getDashboard } from '../../api/dashboard'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {},
      system: null,
      quickLinks: [
        { icon: 'el-icon-user', color: '#409eff', path: '/system/user', label: '用户管理' },
        { icon: 'el-icon-s-custom', color: '#67c23a', path: '/system/role', label: '角色管理' },
        { icon: 'el-icon-menu', color: '#e6a23c', path: '/system/menu', label: '菜单管理' },
        { icon: 'el-icon-s-data', color: '#9b59b6', path: '/system/dept', label: '部门管理' },
        { icon: 'el-icon-document', color: '#f56c6c', path: '/system/notice', label: '通知公告' },
        { icon: 'el-icon-set-up', color: '#909399', path: '/monitor/server', label: '服务监控' }
      ]
    }
  },
  computed: {
    ...mapState(['user', 'roles']),
    statCards() {
      const s = this.stats
      return [
        { key: 'userCount', label: '用户数', value: s.userCount ?? '-', icon: 'el-icon-user', bg: 'linear-gradient(135deg, #409eff, #337ecc)' },
        { key: 'roleCount', label: '角色数', value: s.roleCount ?? '-', icon: 'el-icon-s-custom', bg: 'linear-gradient(135deg, #67c23a, #529b2e)' },
        { key: 'menuCount', label: '菜单数', value: s.menuCount ?? '-', icon: 'el-icon-menu', bg: 'linear-gradient(135deg, #e6a23c, #cf9236)' },
        { key: 'deptCount', label: '部门数', value: s.deptCount ?? '-', icon: 'el-icon-s-data', bg: 'linear-gradient(135deg, #9b59b6, #8e44ad)' },
        { key: 'postCount', label: '岗位数', value: s.postCount ?? '-', icon: 'el-icon-s-flag', bg: 'linear-gradient(135deg, #f56c6c, #e04848)' },
        { key: 'onlineCount', label: '在线用户', value: s.onlineCount ?? '-', icon: 'el-icon-s-promotion', bg: 'linear-gradient(135deg, #36cfc9, #13c2c2)' }
      ]
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        const res = await getDashboard()
        this.stats = res.data.stats || {}
        this.system = res.data.system || null
      } catch (e) {
        // fallback to empty
      }
    }
  }
}
</script>

<style scoped>
.dashboard { padding: 0; }

.stats-row {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
@media (max-width: 1400px) { .stats-row { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 900px)  { .stats-row { grid-template-columns: repeat(2, 1fr); } }

.stat-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: transform .2s, box-shadow .2s;
  cursor: default;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
}
.stat-body {
  display: flex;
  align-items: center;
  padding: 20px 16px;
}
.stat-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 22px;
  flex-shrink: 0; margin-right: 14px;
}
.stat-info { flex: 1; min-width: 0; }
.stat-value { font-size: 24px; font-weight: 700; color: #1d2129; line-height: 1.2; }
.stat-label { font-size: 13px; color: #86909c; margin-top: 2px; }
.stat-footer { height: 3px; }

/* 卡片公共样式 */
.card-header { display: flex; align-items: center; font-weight: 600; font-size: 15px; color: #1d2129; }
.card-header i { margin-right: 8px; font-size: 16px; color: #409eff; }

.sys-card, .quick-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

/* 系统信息 */
.sys-info { padding: 4px 0; }
.info-item {
  display: flex; align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f2f3f5;
}
.info-item:last-child { border-bottom: none; }
.info-label { width: 100px; color: #86909c; font-size: 13px; flex-shrink: 0; }
.info-value { color: #1d2129; font-size: 14px; }

/* 欢迎区域 */
.welcome { display: flex; align-items: center; }
.welcome-avatar { margin-right: 16px; }
.welcome-text h3 { margin: 0 0 6px; font-size: 18px; color: #1d2129; }
.welcome-text p { margin: 0; }

/* 快捷入口 */
.quick-links { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
.quick-link {
  display: flex; align-items: center;
  padding: 10px 12px;
  border-radius: 6px;
  background: #f7f8fa;
  cursor: pointer;
  transition: background .2s;
}
.quick-link:hover { background: #e8f3ff; }
.quick-link i { font-size: 18px; margin-right: 8px; }
.quick-link span { font-size: 13px; color: #4e5969; }
</style>
