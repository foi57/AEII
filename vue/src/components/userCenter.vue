<script setup>
import {ref,computed,reactive} from "vue";
import userApi from "../../api/user.js";
import {Store} from "../store/index.js";
import {ElMessage} from "element-plus";
import {UserFilled} from "@element-plus/icons-vue";
import serverUrl from "../../serverUrl.js";
import 'vue-cropper/dist/index.css'
import {VueCropper} from "vue-cropper";
import Header from "./header.vue";

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
  role: '',
  avatar: computed(() => {
    return userStore.usersAvatar
  })
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
  userStore.setAvatar(newAvatarUrl.value)
  console.log('更新后头像',userStore.usersAvatar)
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
const srcList = [
  `${serverUrl.url}/users/avatar/${userForm.value.avatar}`
]

const cropVisible = ref(false)
const cropper = ref(null)
const cropFile = ref(null) // 存储原始文件

// 修改原beforeAvatarUpload方法
const beforeAvatarUpload = (rawFile) => {
  // 暂存原始文件
  cropFile.value = rawFile
  // 打开裁剪弹窗
  cropVisible.value = true
  return false // 阻止自动上传
}

const newAvatarUrl = ref('') // 存储裁剪后的图片URL

// 新增裁剪确认方法
const confirmCrop = () => {
  cropper.value.getCropBlob(async (blob) => {
    const formData = new FormData()
    formData.append('file', new File([blob], cropFile.value.name, {
      type: blob.type,
      lastModified: Date.now()
    }))
    formData.append('id', userForm.value.id)
    console.log('formData',formData)
    userApi.updateAvatar(formData).then(res => {
      ElMessage.success('头像上传成功')
      newAvatarUrl.value = res.data
      console.log('newAvatarUrl',newAvatarUrl.value)
      cropVisible.value = false
    })
  })
}
const createURL = () => {
  if (!cropFile.value || !(cropFile.value instanceof Blob)) {
    console.error('无效的文件对象', cropFile.value);
    return '';
  }
  try {
    return URL.createObjectURL(cropFile.value);
  } catch (error) {
    console.error('URL生成失败:', error);
    return '';
  }
}

const onCropperReady = () => {
  console.log('裁剪器初始化完成')
  cropper.value?.setAspectRatio(1) // 强制设置1:1比例
}
// 在script setup部分添加以下代码
const emailForm = reactive({
  newEmail: '',
  verificationCode: ''
})

const showEmailDialog = ref(false)
const isEmailSending = ref(false)
const emailCountdown = ref(0)
let emailTimer = null

// 发送邮箱验证码
const sendEmailCode = async () => {
  try {
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.newEmail)) {
      ElMessage.error('请输入有效的新邮箱地址')
      return
    }
    
    isEmailSending.value = true
    await userApi.sendVerificationCode(emailForm.newEmail)
    
    ElMessage.success('验证码已发送')
    // 倒计时逻辑（复用原有倒计时组件逻辑）
    emailCountdown.value = 60
    emailTimer = setInterval(() => {
      emailCountdown.value--
      if (emailCountdown.value <= 0) {
        clearInterval(emailTimer)
        isEmailSending.value = false
      }
    }, 1000)
  } catch (error) {
    ElMessage.error(error.response?.data || '发送失败')
    isEmailSending.value = false
  }
}

// 提交邮箱修改
const submitEmailChange = async () => {
  try {
    await userApi.updateEmail({
      userId: userForm.value.id,
      newEmail: emailForm.newEmail,
      code: emailForm.verificationCode
    })
    ElMessage.success('邮箱修改成功')
    showEmailDialog.value = false
    // 刷新用户信息
    user.value = await userApi.selectUserByName(userName)
  } catch (error) {
    ElMessage.error(error.response?.data || '修改失败')
  }
}
</script>

<template>

  <div v-if="user">
    <el-descriptions title="用户信息"
    direction="vertical">
      <el-descriptions-item label="头像" :rowspan="2">
        <el-image :src="`${serverUrl.url}/users/avatar/${userForm.avatar}`" style="max-width: 100px"  :preview-src-list="srcList">
        <template #error>
        <div class="image-slot">
          <el-icon :size="30"><user-filled></user-filled></el-icon>
        </div>
      </template>
        </el-image>
      </el-descriptions-item>
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
  <div v-if="user">
    <el-button @click="() =>{
    handleEditDialog=true;
    userForm.id=user[0].usersId;
    userForm.userName=user[0].usersName;
    userForm.email=user[0].usersEmail;
    userForm.phone=user[0].usersPhone;
    userForm.role=user[0].usersRole;
    console.log(userForm)
    }">编辑
    </el-button>
    <el-button @click="()=>{
    handlePasswordDialog=true;
    handlePasswordDialogOpen();
  }">修改密码
    </el-button>
    
  </div>
  <el-dialog title="编辑用户" v-model="handleEditDialog">
    <el-form  :model="userForm" :rules="rules" ref="formRef">
      <el-form-item>
        <el-image v-if="newAvatarUrl === ''" :src="`${serverUrl.url}/users/avatar/${userForm.avatar}`" style="max-width: 100px"  :preview-src-list="srcList">
          <template #error>
            <div class="image-slot">
              <el-icon :size="30"><user-filled></user-filled></el-icon>
            </div>
          </template>
        </el-image>
        <el-image v-if="newAvatarUrl !== ''" :src="`${serverUrl.url}/users/avatar/${newAvatarUrl}`" style="max-width: 100px"  :preview-src-list="srcList">
          <template #error>
            <div class="image-slot">
              <el-icon :size="30"><user-filled></user-filled></el-icon>
            </div>
          </template>
        </el-image>
        <!-- 新增裁剪弹窗 -->
        <el-dialog v-model="cropVisible" title="裁剪头像" width="800px">
          <vue-cropper
            v-if="cropFile !==null"
            ref="cropper"
            :img="cropFile ? createURL() : ''"
            :autoCrop="true"
            :fixed="true"
            :fixedNumber="[1, 1]"
            :ready="onCropperReady"
            style="width: 100%; height: 600px"
          />
          <template #footer>
            <el-button @click="cropVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmCrop">确认上传</el-button>
          </template>
        </el-dialog>

        <!-- 修改原上传组件 -->
        <el-upload
          class="avatar-uploader"
          :before-upload="beforeAvatarUpload"
          :show-file-list="false"
          accept="image/*"
        >
          <el-button size="small" type="primary">更新头像</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="用户ID" prop="id" v-if="false">
        <el-input placeholder="请输入用户ID" v-model="userForm.id" disabled></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input placeholder="请输入用户名" v-model="userForm.userName"></el-input>
      </el-form-item>
      <el-dialog title="修改邮箱" v-model="showEmailDialog">
        <el-form :model="emailForm" label-width="100px">
          <el-form-item label="新邮箱" prop="newEmail">
            <el-input v-model="emailForm.newEmail" placeholder="请输入新邮箱"/>
          </el-form-item>
          <el-form-item label="验证码" prop="verificationCode">
            <div class="verification-code-container">
              <el-input v-model="emailForm.verificationCode" placeholder="请输入验证码"/>
              <el-button 
                :disabled="isEmailSending"
                @click="sendEmailCode"
                class="send-code-btn">
                {{ isEmailSending ? `重新发送(${emailCountdown}s)` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showEmailDialog = false">取消</el-button>
          <el-button type="primary" @click="submitEmailChange">确认修改</el-button>
        </template>
      </el-dialog>
      
      <!-- 修改原邮箱表单项 -->
      <el-form-item label="邮箱" prop="email">
        <el-input 
          v-model="userForm.email" 
          placeholder="请输入邮箱"
          disabled
        />
        <el-button 
          type="primary" 
          @click="showEmailDialog = true"
          style="margin-left: 10px">
          修改邮箱
        </el-button>
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
