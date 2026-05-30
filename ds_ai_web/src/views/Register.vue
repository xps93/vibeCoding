<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>

    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <router-link to="/login" class="back-btn">
            <el-icon><ArrowLeft /></el-icon>
          </router-link>
          <h1 class="auth-title">{{ $t('account.register') }}</h1>
          <p class="auth-subtitle">{{ $t('account.createAccount') }}</p>
        </div>

        <form class="auth-form" @submit.prevent="handleRegister">
          <div class="form-item">
            <label class="form-label">{{ $t('account.username') }}</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><User /></el-icon>
              <input v-model="form.username" type="text" class="form-input" :placeholder="$t('common.usernamePlaceholder')" autocomplete="username" />
            </div>
          </div>

          <div class="form-item">
            <label class="form-label">{{ $t('account.phone') }}</label>
            <div class="input-wrapper">
              <span class="country-code">+86</span>
              <input v-model="form.phone" type="tel" class="form-input has-prefix" :placeholder="$t('common.phonePlaceholder')" maxlength="11" />
            </div>
          </div>

          <div class="form-item">
            <label class="form-label">{{ $t('account.password') }}</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <input v-model="form.password" :type="showPwd ? 'text' : 'password'" class="form-input" :placeholder="$t('common.passwordPlaceholder')" />
              <button type="button" class="toggle-pwd" @click="showPwd = !showPwd">
                <el-icon><View v-if="!showPwd" /><Hide v-else /></el-icon>
              </button>
            </div>
          </div>

          <div class="form-item">
            <label class="form-label">{{ $t('account.confirmPassword') }}</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <input v-model="form.confirmPassword" :type="showPwd ? 'text' : 'password'" class="form-input" :placeholder="$t('common.reEnterPwdPlaceholder')" />
            </div>
          </div>

          <div class="form-item">
            <label class="form-label">{{ $t('account.verificationCode') }}</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><Message /></el-icon>
              <input v-model="form.code" type="text" class="form-input" :placeholder="$t('common.codePlaceholder')" maxlength="6" />
              <button type="button" class="code-btn" :disabled="countdown > 0 || !form.phone" @click="sendCode">
                {{ countdown > 0 ? countdown + 's' : $t('account.sendCode') }}
              </button>
            </div>
          </div>

          <div v-if="errorMsg" class="form-error">
            <el-icon><WarningFilled /></el-icon>
            <span>{{ errorMsg }}</span>
          </div>

          <button type="submit" class="auth-btn" :disabled="!formValid">
            <span>{{ $t('account.register') }}</span>
          </button>
        </form>

        <div class="auth-footer">
          <span>{{ $t('account.haveAccount') }}</span>
          <router-link to="/login" class="link">{{ $t('account.goLogin') }}</router-link>
        </div>
      </div>

      <div class="theme-toggle-wrapper">
        <LanguageSwitcher />
        <ThemeToggle />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { register, sendSmsCode } from '@/api/auth'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import LanguageSwitcher from '@/components/common/LanguageSwitcher.vue'

const { t } = useI18n()
const router = useRouter()

const form = ref({ username: '', phone: '', password: '', confirmPassword: '', code: '' })
const showPwd = ref(false)
const errorMsg = ref('')
const countdown = ref(0)
const submitting = ref(false)
let countdownTimer = null

const formValid = computed(() =>
  form.value.username.trim() &&
  /^1[3-9]\d{9}$/.test(form.value.phone) &&
  form.value.password.length >= 6 &&
  form.value.password === form.value.confirmPassword &&
  form.value.code.length >= 4
)

async function sendCode() {
  if (countdown.value > 0 || !form.value.phone) return
  try {
    const res = await sendSmsCode(form.value.phone)
    if (res.code === 200) {
      ElMessage.success(t('account.codeSent'))
      countdown.value = 60
      countdownTimer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) { clearInterval(countdownTimer); countdownTimer = null }
      }, 1000)
    } else {
      ElMessage.error(res.msg || t('common.networkError'))
    }
  } catch (e) {
    ElMessage.error(t('common.networkError'))
  }
}

async function handleRegister() {
  errorMsg.value = ''
  if (!formValid.value) {
    if (form.value.password !== form.value.confirmPassword) {
      errorMsg.value = t('common.passwordMismatch'); return
    }
    errorMsg.value = t('common.completeInfo'); return
  }
  if (submitting.value) return
  submitting.value = true
  try {
    const res = await register({
      username: form.value.username.trim(),
      password: form.value.password,
      phone: form.value.phone,
      code: form.value.code
    })
    if (res.code === 200) {
      ElMessage.success(t('account.registerSuccess'))
      router.push('/login')
    } else {
      errorMsg.value = res.msg || t('account.registerFailed')
    }
  } catch (e) {
    errorMsg.value = t('common.networkError')
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.auth-page {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
  position: relative;
  overflow: hidden;
}

.auth-bg {
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

.auth-container {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  padding: 36px 40px;
}

.auth-header {
  text-align: center;
  margin-bottom: 28px;
  position: relative;
}

.back-btn {
  position: absolute;
  left: 0;
  top: 4px;
  font-size: 20px;
  color: var(--text-tertiary);
  cursor: pointer;

  &:hover { color: var(--text-primary); }
}

.auth-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px 0;
}

.auth-subtitle {
  font-size: 14px;
  color: var(--text-tertiary);
  margin: 0;
}

.form-item { margin-bottom: 18px; }

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 6px;
}

.input-wrapper { position: relative; display: flex; align-items: center; }

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

  &.has-prefix { padding-left: 58px; }
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

  &:disabled { color: var(--text-tertiary); cursor: not-allowed; }
  &:hover:not(:disabled) { color: var(--accent-hover); }
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

html[data-theme="dark"] .form-error,
html[data-theme="blue-pro"] .form-error,
html[data-theme="purple"] .form-error {
  background: rgba(245, 108, 108, 0.1);
  border-color: rgba(245, 108, 108, 0.2);
}

.auth-btn {
  width: 100%;
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  background: var(--accent-color);
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: background 0.2s;

  &:hover:not(:disabled) { background: var(--accent-hover); }
  &:disabled { opacity: 0.6; cursor: not-allowed; }
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
  color: var(--text-tertiary);

  .link {
    color: var(--accent-color);
    text-decoration: none;
    margin-left: 4px;
    &:hover { text-decoration: underline; }
  }
}

.theme-toggle-wrapper { display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 24px; }

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

  &:hover {
    color: var(--text-secondary);
    border-color: var(--text-tertiary);
  }
}
</style>
