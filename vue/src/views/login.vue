<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import userApi from '../../api/user.js'
import { Store } from '../store/index.js'

const router = useRouter()
const userStore = Store()

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度6-16位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    const res = await userApi.login(form.value.username, form.value.password)

    ElMessage.success('登录成功')
    localStorage.setItem('accessToken', res.data.accessToken)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    userStore.setUserInfo(res.data.user)

    await router.push('/userIndex')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '用户名或密码错误')
  }
}
</script>

<template>
  <div class="login-container">
    <el-card class="login-box">
      <h2 class="login-title">用户登录</h2>
      <el-form
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名/邮箱" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" native-type="submit" class="login-btn">
            立即登录
          </el-button>
        </el-form-item>
        <el-link type="info" href="/register" class="register-link">没有账号？立即注册</el-link>
        <el-link type="info" href="/forgetPassword" class="forget-link">忘记密码</el-link>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.login-box {
  width: 400px;
  padding: 20px;
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
.forget-link {
  float: left;
}
</style>