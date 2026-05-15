<template>
  <div class="dashboard">
    <el-row :gutter="20" class="mb20">
      <el-col :span="6" v-for="card in stats" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div :class="'stat-icon ' + card.color">
              <i :class="card.icon"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="hover">
      <div slot="header" class="card-header">
        <span>欢迎</span>
      </div>
      <div class="welcome-content">
        <p>欢迎来到后台管理系统</p>
        <p v-if="user">当前用户: <strong>{{ user.nickname || user.username }}</strong></p>
        <p>角色: <el-tag size="small" type="primary" v-for="r in roles" :key="r">{{ r }}</el-tag></p>
        <el-divider></el-divider>
        <h4>测试账号</h4>
        <el-table :data="testAccounts" border size="small" style="width:500px;margin-top:10px">
          <el-table-column prop="username" label="用户名" width="120"></el-table-column>
          <el-table-column prop="password" label="密码" width="120"></el-table-column>
          <el-table-column prop="role" label="角色"></el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: [
        { label: '用户数', value: 3, icon: 'el-icon-user', color: 'blue' },
        { label: '角色数', value: 2, icon: 'el-icon-s-custom', color: 'green' },
        { label: '菜单数', value: 8, icon: 'el-icon-menu', color: 'orange' },
        { label: '权限数', value: 12, icon: 'el-icon-key', color: 'purple' }
      ],
      testAccounts: [
        { username: 'admin', password: 'admin123', role: '超级管理员' },
        { username: 'user', password: 'user123', role: '普通用户' },
        { username: 'test', password: 'test123', role: '普通用户' }
      ]
    }
  },
  computed: { ...mapState(['user', 'roles']) }
}
</script>

<style scoped>
.dashboard { padding: 0; }
.mb20 { margin-bottom: 20px; }
.stat-card { cursor: default; }
.stat-inner { display: flex; align-items: center; }
.stat-icon {
  width: 56px; height: 56px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  font-size: 28px; color: #fff; margin-right: 16px;
}
.stat-icon.blue { background: linear-gradient(135deg, #409eff, #337ecc); }
.stat-icon.green { background: linear-gradient(135deg, #67c23a, #529b2e); }
.stat-icon.orange { background: linear-gradient(135deg, #e6a23c, #cf9236); }
.stat-icon.purple { background: linear-gradient(135deg, #9b59b6, #8e44ad); }
.stat-value { font-size: 26px; font-weight: bold; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
.card-header { font-weight: bold; }
.welcome-content p { margin: 8px 0; }
</style>
