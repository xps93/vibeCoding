<template>
  <div class="profile-page" :class="{ dark: isDark }">
    <div class="profile-container">
      <div class="profile-header">
        <button class="back-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回</span>
        </button>
        <h2 class="page-title">个人资料</h2>
      </div>

      <div class="profile-card">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <el-avatar :size="80" :src="form.avatar">
              <el-icon :size="40"><UserFilled /></el-icon>
            </el-avatar>
            <div class="avatar-overlay" @click="showAvatarInput = true">
              <el-icon><Camera /></el-icon>
            </div>
          </div>
          <div v-if="showAvatarInput" class="avatar-url-input">
            <el-input
              v-model="form.avatar"
              placeholder="输入头像URL地址"
              size="small"
              @blur="showAvatarInput = false"
              @keyup.enter="showAvatarInput = false"
            />
          </div>
          <span class="avatar-hint">点击头像修改，输入图片URL</span>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          label-width="80px"
          label-position="left"
          class="profile-form"
          :rules="rules"
        >
          <el-form-item label="用户名">
            <el-input :model-value="form.username" disabled />
            <span class="form-tip">用户名不可修改</span>
          </el-form-item>

          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" placeholder="请输入昵称" maxlength="30" />
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="100" />
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="20" />
          </el-form-item>

          <el-form-item>
            <div class="form-actions">
              <el-button type="primary" :loading="saving" @click="handleSave">
                保存修改
              </el-button>
              <el-button @click="handleCancel">取消</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, UserFilled, Camera } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import { storeToRefs } from 'pinia'

const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()
const { isDark } = storeToRefs(themeStore)

const saving = ref(false)
const showAvatarInput = ref(false)
const formRef = ref(null)

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: ''
})

const rules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

onMounted(() => {
  form.username = userStore.username || ''
  form.nickname = userStore.nickname || ''
  form.email = userStore.email || ''
  form.phone = userStore.phone || ''
  form.avatar = userStore.avatar || ''
})

function goBack() {
  router.back()
}

async function handleSave() {
  saving.value = true
  try {
    const data = {
      nickname: form.nickname,
      email: form.email,
      phone: form.phone,
      avatar: form.avatar
    }
    await userStore.updateProfile(data)
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

function handleCancel() {
  router.back()
}
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding: 24px;
}

.profile-container {
  max-width: 560px;
  margin: 0 auto;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    color: var(--accent-color);
    border-color: var(--accent-color);
  }
}

.page-title {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.profile-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 32px;
  box-shadow: var(--shadow-sm);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-light);
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
  color: #fff;
  font-size: 24px;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-url-input {
  margin-top: 10px;
  width: 260px;
}

.avatar-hint {
  margin-top: 8px;
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
}

.profile-form {
  .form-tip {
    margin-left: 12px;
    font-size: var(--font-size-xs);
    color: var(--text-tertiary);
  }
}

.form-actions {
  display: flex;
  gap: 12px;
}
</style>
