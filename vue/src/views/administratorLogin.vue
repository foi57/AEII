<script setup>
import { ref } from 'vue'
import admin from "../../api/user.js";
import {ElMessage} from "element-plus";
import router from "../router/index.js";
import {Store} from "../store/index.js";
const userStore = Store();
const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
  ]
}
const form = ref({
  userName: '',
  password: ''
})
const onSubmit = () => {
  admin.login(form.value.userName,form.value.password).then(res => {
     ElMessage({
       type: 'success',
       message: '登录成功'
     })
   userStore.setUserInfo(res.data.user)
    localStorage.setItem('accessToken',res.data.accessToken)
    localStorage.setItem('refreshToken',res.data.refreshToken)
    router.push('/adminIndex')
  })
      .catch(err => {
     ElMessage({
       type: 'error',
       message: '用户名或密码错误'
     })
     console.log(err)
      })
}
</script>

<template>
  <div class="login">
    <h2>管理员登录</h2>

    <el-form :model="form" :rules="rules" ref="formEl">
      <el-form-item label="用户名" prop="email">
        <el-input v-model="form.userName" ></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="form.password"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.login {
  border: 2px solid #ccc;
  border-radius: 10px;
}
.el-button {
  left: 50%;
  transform: translateX(-50%);
  position: relative;
}
</style>