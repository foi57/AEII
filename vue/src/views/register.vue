<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import userApi from '../../api/user.js'

const router = useRouter()

const form = reactive({
  userName: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  role: 'user',
  verificationCode: '' // 新增验证码字段
})

const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 16, message: '用户名长度4-16位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度6-16位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 6, max: 6, message: '验证码长度为6位', trigger: 'blur' }
  ]
}

// 发送验证码相关
const isSending = ref(false)
const countdown = ref(0)
const timer = ref(null)

// 发送验证码
const sendVerificationCode = async () => {
  try {
    if (!form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      ElMessage.error('请输入正确的邮箱地址')
      return
    }

    isSending.value = true
    ElMessage.info('正在发送验证码，请稍候...')

    await userApi.sendVerificationCode(form.email)

    ElMessage.success('验证码已发送，请查收邮件')

    countdown.value = 60
    timer.value = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer.value)
        isSending.value = false
      }
    }, 1000)
  } catch (error) {
    console.error(error)
    ElMessage.error(error.response?.data || '发送验证码失败，请稍后重试')
    isSending.value = false
  }
}

const handleRegister = async () => {
  try {
    await userApi.insertUser(form)

    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.response?.data || '注册失败')
  }
}
</script>

<template>
  <div class="register-container">
    <el-card class="register-box">
      <h2 class="register-title">用户注册</h2>
      <el-form :model="form" :rules="rules" label-width="100px" @submit.prevent="handleRegister">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="4-16位字母数字组合" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="6-16位密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="示例：user@example.com" />
        </el-form-item>
        <el-form-item label="验证码" prop="verificationCode">
          <div class="verification-code-container">
            <el-input v-model="form.verificationCode" placeholder="请输入验证码" />
            <el-button
              type="primary"
              :disabled="isSending"
              @click="sendVerificationCode"
              class="send-code-btn"
            >
              {{ isSending ? `重新发送(${countdown}s)` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="可选（11位数字）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" class="register-btn">
            立即注册
          </el-button>
<el-link type="info" href="/login" class="register-link">已有账号？立即登录</el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.register-box {
  width: 500px;
  padding: 20px;
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
}

.verification-code-container {
  display: flex;
  gap: 10px;
}

.send-code-btn {
  white-space: nowrap;
}

.register-btn {
  width: 100%;
}
</style>