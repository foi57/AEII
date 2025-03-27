<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import userApi from '../../api/user.js'

const router = useRouter()

const form = ref({
  userName: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  role: 'user'
})

const rules = {
  username: [
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
        if (value !== form.value.password) {
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
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: ['blur', 'change'] }
  ]
}

const handleRegister = async () => {
  try {
    await userApi.insertUser(form.value)

    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  }
}
</script>

<template>
  <div class="login-container">
    <el-card class="login-box">
      <h2 class="login-title">用户注册</h2>
      <el-form
          :model="form"
          :rules="rules"
          label-width="100px"
          @submit.prevent="handleRegister"
      >
        <el-form-item label="用户名" prop="username">
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

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="可选（11位数字）" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" native-type="submit" class="login-btn">
            立即注册
          </el-button>
          <el-link type="info" href="/login" class="register-link">已有账号？立即登录</el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
/* 复用登录页的样式 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.login-box {
  width: 500px;
  padding: 30px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
}

.login-btn {
  width: 100%;
  margin-bottom: 15px;
}

.register-link {
  float: right;
}
</style>