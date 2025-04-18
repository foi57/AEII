<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import userApi from '../../api/user.js'

const router = useRouter()

// 表单数据
const form = reactive({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 6, max: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度6-16位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 发送验证码相关
const isSending = ref(false)
const countdown = ref(0)
const timer = ref(null)

// 发送验证码
const sendVerificationCode = async () => {
  try {
    // 验证邮箱格式
    if (!form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      ElMessage.error('请输入正确的邮箱地址')
      return
    }

    isSending.value = true
    ElMessage.info('正在发送验证码，请稍候...') // 添加提示，告知用户正在处理
    
    // 调用发送验证码接口
    await userApi.sendVerificationCode(form.email)
    
    ElMessage.success('验证码已发送，请查收邮件')
    
    // 开始倒计时
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

// 重置密码
const resetPassword = async () => {
  try {
    // 验证表单
    if (!form.email || !form.verificationCode || !form.newPassword || !form.confirmPassword) {
      ElMessage.error('请填写完整信息')
      return
    }
    
    if (form.newPassword !== form.confirmPassword) {
      ElMessage.error('两次输入密码不一致')
      return
    }
    
    // 调用重置密码接口
    await userApi.resetPassword({
      email: form.email,
      verificationCode: form.verificationCode,
      newPassword: form.newPassword
    })
    
    ElMessage.success('密码重置成功')
    router.push('/login')
  } catch (error) {
    console.error(error)
    ElMessage.error(error.response?.data || '密码重置失败')
  }
}
</script>

<template>
  <div class="forget-container">
    <el-card class="forget-box">
      <h2 class="forget-title">找回密码</h2>
      <el-form
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入注册时使用的邮箱" />
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
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="6-16位密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="resetPassword" class="reset-btn">
            重置密码
          </el-button>
        </el-form-item>
        
        <div class="links">
          <el-link type="info" href="/login">返回登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.forget-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.forget-box {
  width: 500px;
  padding: 20px;
}

.forget-title {
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

.reset-btn {
  width: 100%;
  margin-bottom: 15px;
}

.links {
  display: flex;
  justify-content: center;
}
</style>