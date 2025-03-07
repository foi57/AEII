<script setup>
import {ref} from "vue";
import userApi from "../../api/user.js";
import {Store} from "../store/index.js";
import {ElMessage} from "element-plus";
const userStore = Store();
const userName = userStore.usersName;
const user =ref(null);

userApi.selectUserByName(userName).then(res => {
  user.value=res.data;
  console.log(user.value)
})
const userForm = ref({
  id: '',
  userName: '',
  password: '',
  email: '',
  phone: '',
  role: ''
})
const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 16, message: '长度在 3 到 16 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phone: [
    {  message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }
  ]
}
const handleEditDialog = ref(false);
let handlePasswordDialog = ref(false);
const updateUser = () => {
  userApi.updateUser(userForm.value).then(res => {
    ElMessage.success('编辑成功')
    handleEditDialog.value = false;
  })
}
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const formRef = ref(null)
const validatePassword = (rule, value, callback) => {
  if (value === passwordForm.value.oldPassword) {
    callback(new Error('新密码不能与旧密码相同'));
  } else {
    callback();
  }
};
const validateConfirm = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' },
    {validator: validatePassword, trigger: 'blur'}
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' },
    {validator: validateConfirm, trigger: 'blur'}
  ],
}

const handlePasswordDialogOpen=()=> {
  userApi.selectPassword(user.value[0].usersName).then(res => {
    passwordForm.oldPassword=res.data;
  })
}
const updatePassword = () => {
  userApi.updatePassword(user.value[0].usersName,passwordForm.value.confirmPassword).then(res => {
    ElMessage.success('修改成功')
    handlePasswordDialog.value = false;
  }).catch(err => ElMessage.error('修改失败'))
}
</script>

<template>
  <div v-if="user">
    <el-descriptions title="用户信息">
      <el-descriptions-item label="用户名">{{user[0].usersName}}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{user[0].usersEmail}}</el-descriptions-item>
      <el-descriptions-item label="电话">{{user[0].usersPhone}}</el-descriptions-item>
      <el-descriptions-item label="角色">{{user[0].usersRole}}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{user[0].userCreationTime}}</el-descriptions-item>
    </el-descriptions>
  </div>
<div v-else>
  <el-skeleton :rows="5"></el-skeleton>
</div>
  <div v-if="user"><el-button @click="() =>{
    handleEditDialog=true;
    userForm.id=user[0].usersId;
    userForm.userName=user[0].usersName;
    userForm.email=user[0].usersEmail;
    userForm.phone=user[0].usersPhone;
    userForm.role=user[0].usersRole;
    console.log(userForm)
  }">编辑</el-button><el-button @click="()=>{
    handlePasswordDialog=true;
    handlePasswordDialogOpen();
  }">修改密码</el-button></div>
  <el-dialog title="编辑用户" v-model="handleEditDialog">
    <el-form  :model="userForm" :rules="rules" ref="formRef">
      <el-form-item label="用户ID" prop="id" v-if="false">
        <el-input placeholder="请输入用户ID" v-model="userForm.id" disabled></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input placeholder="请输入用户名" v-model="userForm.userName"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input placeholder="请输入邮箱" v-model="userForm.email"></el-input>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input placeholder="请输入手机号" v-model="userForm.phone"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleEditDialog=false">取 消</el-button>
      <el-button type="primary" @click="updateUser">确 定</el-button>
    </span>
  </el-dialog>
  <el-dialog title="修改密码" v-model="handlePasswordDialog">
    <el-form  :model="passwordForm" :rules="passwordRules" ref="formRef">
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input placeholder="请输入旧密码"  v-model="passwordForm.oldPassword" type="password" show-password ></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input placeholder="请输入新密码"  v-model="passwordForm.newPassword" type="password" show-password ></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input placeholder="请再次输入新密码" v-model="passwordForm.confirmPassword" type="password" show-password ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handlePasswordDialog=false">取 消</el-button>
      <el-button type="primary" @click="()=>{handlePasswordDialog=false; updatePassword();}">确 定</el-button>
    </span>
  </el-dialog>
</template>

<style scoped>

</style>