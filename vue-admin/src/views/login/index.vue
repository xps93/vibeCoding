<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">后台管理系统</h2>
      <el-form ref="form" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" prefix-icon="el-icon-user" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" prefix-icon="el-icon-lock" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
        <div class="login-tips">
          <p>管理员: admin / admin123</p>
          <p>普通用户: user / user123</p>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { login } from '../../api/login'

export default {
  name: 'Login',
  // 返回登录表单数据、验证规则和加载状态
  data() {
    return {
      form: { username: 'admin', password: 'admin123' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    // 处理登录：校验表单、调用登录接口、保存 Token 并跳转首页
    async handleLogin() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await login(this.form)
          this.$store.commit('SET_TOKEN', res.data.token)
          await this.$store.dispatch('getUserInfoAndMenus')
          this.$router.push('/')
        } catch (e) {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
  font-size: 24px;
  font-weight: bold;
}
.login-form { margin-top: 10px; }
.login-btn { width: 100%; }
.login-tips {
  text-align: center;
  color: #909399;
  font-size: 12px;
  line-height: 1.8;
}
</style>
