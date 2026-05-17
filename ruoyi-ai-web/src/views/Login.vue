<template>
  <div class="login-page" :class="{ dark: isDark }">
    <div class="login-bg">
      <div class="bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>

    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">
            <svg viewBox="0 0 40 40" fill="none" class="logo-icon">
              <rect width="40" height="40" rx="10" fill="var(--accent-color)"/>
              <path d="M12 14C12 12.8954 12.8954 12 14 12H26C27.1046 12 28 12.8954 28 14V20L20 26L12 20V14Z" fill="white" opacity="0.9"/>
              <circle cx="20" cy="22" r="3" fill="var(--accent-color)"/>
            </svg>
          </div>
          <h1 class="login-title">Ds-Ai</h1>
          <p class="login-subtitle">登录您的账号，开始AI对话</p>
        </div>

        <!-- 登录方式切换 -->
        <div class="login-tabs">
          <button
            :class="['tab-btn', { active: loginMode === 'phone' }]"
            @click="loginMode = 'phone'"
          >手机登录</button>
          <button
            :class="['tab-btn', { active: loginMode === 'account' }]"
            @click="loginMode = 'account'"
          >账号登录</button>
        </div>

        <!-- 手机登录表单 -->
        <form v-if="loginMode === 'phone'" class="login-form" @submit.prevent="handlePhoneLogin">
          <div class="form-item">
            <label class="form-label">手机号</label>
            <div class="input-wrapper">
              <span class="country-code">+86</span>
              <input
                v-model="phoneForm.phone"
                type="tel"
                class="form-input has-prefix"
                placeholder="请输入手机号"
                maxlength="11"
                :disabled="loading"
              />
            </div>
          </div>

          <div class="form-item">
            <label class="form-label">验证码</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><Message /></el-icon>
              <input
                v-model="phoneForm.code"
                type="text"
                class="form-input"
                placeholder="请输入验证码"
                maxlength="6"
                :disabled="loading"
              />
              <button
                type="button"
                class="code-btn"
                :disabled="countdown > 0 || !phoneForm.phone"
                @click="sendCode"
              >{{ countdown > 0 ? countdown + 's' : '发送验证码' }}</button>
            </div>
          </div>

          <div v-if="errorMsg" class="form-error">
            <el-icon><WarningFilled /></el-icon>
            <span>{{ errorMsg }}</span>
          </div>

          <button type="submit" class="login-btn" :disabled="loading || !phoneValid">
            <el-icon v-if="loading" class="loading-spin"><Loading /></el-icon>
            <span>{{ loading ? '登录中...' : '登 录' }}</span>
          </button>
        </form>

        <!-- 账号密码登录表单 -->
        <form v-else class="login-form" @submit.prevent="handleLogin">
          <div class="form-item">
            <label class="form-label">用户名</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><User /></el-icon>
              <input
                v-model="accountForm.username"
                type="text"
                class="form-input"
                placeholder="请输入用户名"
                autocomplete="username"
                :disabled="loading"
              />
            </div>
          </div>

          <div class="form-item">
            <label class="form-label">密码</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <input
                v-model="accountForm.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                placeholder="请输入密码"
                autocomplete="current-password"
                :disabled="loading"
                @keyup.enter="handleLogin"
              />
              <button type="button" class="toggle-pwd" @click="showPassword = !showPassword">
                <el-icon><View v-if="!showPassword" /><Hide v-else /></el-icon>
              </button>
            </div>
          </div>

          <div v-if="errorMsg" class="form-error">
            <el-icon><WarningFilled /></el-icon>
            <span>{{ errorMsg }}</span>
          </div>

          <button type="submit" class="login-btn" :disabled="loading || !accountValid">
            <el-icon v-if="loading" class="loading-spin"><Loading /></el-icon>
            <span>{{ loading ? '登录中...' : '登 录' }}</span>
          </button>
        </form>

        <!-- 底部操作链接 -->
        <div class="login-links">
          <router-link to="/register" class="link-item">注册账号</router-link>
          <router-link to="/forgot-password" class="link-item">忘记密码？</router-link>
        </div>

        <!-- 第三方登录 -->
        <div class="third-party-login">
          <div class="divider">
            <span class="divider-text">其他方式登录</span>
          </div>
          <div class="social-icons">
            <button class="social-btn wechat" title="微信登录">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 0 1 .598.082l1.584.926a.272.272 0 0 0 .14.047c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 0 1-.023-.156.49.49 0 0 1 .201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.952-7.062-6.122zm-2.18 2.769c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982zm4.844 0c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982z"/></svg>
            </button>
            <button class="social-btn alipay" title="支付宝登录">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M18.5 7.5c-1.2 0-2.4.5-3.3 1.3-1.1-1.3-2.7-2.1-4.5-2.1-3.3 0-6 2.7-6 6s2.7 6 6 6c1.8 0 3.4-.8 4.5-2.1.9.8 2.1 1.3 3.3 1.3 2.8 0 5-2.2 5-5s-2.2-5-5-5zm0 8.5c-1.6 0-2.9-1.1-3.3-2.5h3.3v-1.5h-3.5c0-.3.1-.7.1-1h3.4V9.5H15c-.3-.9-1.1-1.5-2-1.5-1.2 0-2.2 1-2.2 2.2S11.8 13 13 13c.9 0 1.7-.6 2-1.4h1.2c-.2 1.6-1.2 2.9-2.5 2.9-1.4 0-2.5-1.1-2.5-2.5s1.1-2.5 2.5-2.5c.7 0 1.4.3 1.8.8l1.1-.9c-.7-.8-1.7-1.3-2.9-1.3-2.2 0-4 1.8-4 4s1.8 4 4 4c1.5 0 2.9-.8 3.5-2h1.3c-.5 2.2-2.3 3.5-4.8 3.5-1.3 0-2.5-.3-3.5-.9l-.8 1.3c1 .6 2.1.9 3.3.9 3.1 0 5.8-2.5 5.8-5.8 0-1.9-1.5-3.5-3.5-3.5z"/></svg>
            </button>
            <button class="social-btn google" title="Google 登录">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z"/><path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/><path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/><path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/></svg>
            </button>
            <button class="social-btn github" title="GitHub 登录">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 0C5.374 0 0 5.373 0 12c0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23A11.509 11.509 0 0 1 12 5.803c1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576C20.566 21.797 24 17.3 24 12 24 5.373 18.627 0 12 0z"/></svg>
            </button>
          </div>
        </div>

        <div class="login-footer">
          <span>登录即表示同意 <a href="#" class="link">服务协议</a> 和 <a href="#" class="link">隐私政策</a></span>
        </div>
      </div>

      <div class="theme-toggle-wrapper">
        <button class="theme-btn" @click="toggleTheme">
          <el-icon><Sunny v-if="isDark" /><Moon v-else /></el-icon>
          <span>{{ isDark ? '浅色模式' : '深色模式' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useThemeStore } from '@/stores/theme'
import { storeToRefs } from 'pinia'

const router = useRouter()
const route = useRoute()
const themeStore = useThemeStore()
const { isDark } = storeToRefs(themeStore)

const loginMode = ref('phone')
const showPassword = ref(false)
const loading = ref(false)
const errorMsg = ref('')
const countdown = ref(0)
let countdownTimer = null

const phoneForm = ref({ phone: '', code: '' })
const accountForm = ref({ username: '', password: '' })

const redirect = route.query.redirect || '/'

const phoneValid = computed(() => /^1[3-9]\d{9}$/.test(phoneForm.value.phone) && phoneForm.value.code.length >= 4)
const accountValid = computed(() => accountForm.value.username.trim() && accountForm.value.password.trim())

function toggleTheme() {
  themeStore.toggle()
}

function sendCode() {
  if (countdown.value > 0 || !phoneForm.value.phone) return
  ElMessage.success('验证码已发送（演示：输入 1234）')
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
  }, 1000)
}

function handlePhoneLogin() {
  errorMsg.value = ''
  if (!phoneValid.value) return
  loading.value = true
  // 手机验证码登录（后台逻辑暂不实现，使用账号密码登录接口占位）
  login('admin', 'admin123')
    .then(res => {
      if (res.code === 200 && res.data && res.data.token) {
        localStorage.setItem('token', res.data.token)
        ElMessage.success('登录成功')
        router.replace(redirect)
      } else {
        errorMsg.value = res.msg || '登录失败'
      }
    })
    .catch(() => { errorMsg.value = '网络连接失败，请稍后重试' })
    .finally(() => { loading.value = false })
}

function handleLogin() {
  errorMsg.value = ''
  if (!accountValid.value) return

  loading.value = true
  login(accountForm.value.username.trim(), accountForm.value.password)
    .then(res => {
      if (res.code === 200 && res.data && res.data.token) {
        localStorage.setItem('token', res.data.token)
        ElMessage.success('登录成功')
        router.replace(redirect)
      } else {
        errorMsg.value = res.msg || '用户名或密码错误'
      }
    })
    .catch(() => { errorMsg.value = '网络连接失败，请稍后重试' })
    .finally(() => { loading.value = false })
}
</script>

<style lang="scss" scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-shapes {
  position: absolute;
  inset: 0;
  opacity: 0.4;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.shape-1 {
  width: 500px; height: 500px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  top: -150px; right: -100px;
}

.shape-2 {
  width: 400px; height: 400px;
  background: linear-gradient(135deg, #a78bfa, #818cf8);
  bottom: -100px; left: -100px;
}

.shape-3 {
  width: 300px; height: 300px;
  background: linear-gradient(135deg, #34d399, #6ee7b7);
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
}

.login-container {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  padding: 36px 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 28px;
}

.logo {
  margin-bottom: 16px;
  display: flex;
  justify-content: center;
}

.logo-icon {
  width: 48px; height: 48px;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px 0;
}

.login-subtitle {
  font-size: 14px;
  color: var(--text-tertiary);
  margin: 0;
}

/* 登录方式切换 */
.login-tabs {
  display: flex;
  margin-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
}

.tab-btn {
  flex: 1;
  padding: 10px 0;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-tertiary);
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  transition: color 0.2s, border-color 0.2s;

  &.active {
    color: var(--accent-color);
    border-bottom-color: var(--accent-color);
  }

  &:hover:not(.active) {
    color: var(--text-secondary);
  }
}

/* 表单 */
.form-item {
  margin-bottom: 18px;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 6px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  color: var(--text-tertiary);
  font-size: 18px;
  pointer-events: none;
  z-index: 1;
}

.country-code {
  position: absolute;
  left: 14px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  z-index: 1;
  padding-right: 10px;
  border-right: 1px solid var(--border-color);
}

.form-input {
  width: 100%;
  height: 46px;
  padding: 0 14px 0 42px;
  font-size: 15px;
  color: var(--text-primary);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;

  &::placeholder { color: var(--text-tertiary); }

  &:focus {
    border-color: var(--accent-color);
    box-shadow: 0 0 0 3px var(--accent-light);
  }

  &:disabled { opacity: 0.6; cursor: not-allowed; }

  &.has-prefix { padding-left: 58px; }
}

.code-btn {
  position: absolute;
  right: 4px;
  padding: 6px 14px;
  font-size: 13px;
  font-weight: 500;
  color: var(--accent-color);
  background: none;
  border: none;
  cursor: pointer;
  white-space: nowrap;

  &:disabled {
    color: var(--text-tertiary);
    cursor: not-allowed;
  }

  &:hover:not(:disabled) {
    color: var(--accent-hover);
  }
}

.toggle-pwd {
  position: absolute;
  right: 4px;
  border: none;
  background: none;
  padding: 8px;
  cursor: pointer;
  color: var(--text-tertiary);
  font-size: 18px;
  display: flex;
  align-items: center;

  &:hover { color: var(--text-secondary); }
}

.form-error {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  margin-bottom: 18px;
  font-size: 13px;
  color: var(--danger-color);
  background: #fef0f0;
  border-radius: var(--radius-sm);
  border: 1px solid #fde2e2;
}

html.dark .form-error {
  background: rgba(245, 108, 108, 0.1);
  border-color: rgba(245, 108, 108, 0.2);
}

.login-btn {
  width: 100%;
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  background: var(--accent-color);
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: background 0.2s, transform 0.1s;

  &:hover:not(:disabled) { background: var(--accent-hover); }
  &:active:not(:disabled) { transform: scale(0.98); }
  &:disabled { opacity: 0.6; cursor: not-allowed; }
}

.loading-spin { animation: spin 1s linear infinite; }

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 底部链接 */
.login-links {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
}

.link-item {
  font-size: 13px;
  color: var(--accent-color);
  text-decoration: none;

  &:hover { text-decoration: underline; }
}

/* 第三方登录 */
.third-party-login {
  margin-top: 24px;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: 16px;

  &::before, &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: var(--border-color);
  }
}

.divider-text {
  padding: 0 16px;
  font-size: 12px;
  color: var(--text-tertiary);
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.social-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid var(--border-color);
  background: var(--bg-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.2s, transform 0.1s;

  svg {
    width: 20px;
    height: 20px;
  }

  &:hover { transform: scale(1.1); }
  &:active { transform: scale(0.95); }

  &.wechat {
    color: #07c160;
    &:hover { border-color: #07c160; }
  }
  &.alipay {
    color: #1677ff;
    &:hover { border-color: #1677ff; }
  }
  &.google {
    color: #4285f4;
    &:hover { border-color: #4285f4; }
  }
  &.github {
    color: #333;
    &:hover { border-color: #333; }
  }
}

html.dark .social-btn.github {
  color: #e6e6e6;
  &:hover { border-color: #e6e6e6; }
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 12px;
  color: var(--text-tertiary);

  .link {
    color: var(--accent-color);
    text-decoration: none;
    &:hover { text-decoration: underline; }
  }
}

.theme-toggle-wrapper {
  margin-top: 24px;
}

.theme-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 13px;
  color: var(--text-tertiary);
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  cursor: pointer;
  transition: color 0.2s, border-color 0.2s;

  &:hover {
    color: var(--text-secondary);
    border-color: var(--text-tertiary);
  }
}
</style>
