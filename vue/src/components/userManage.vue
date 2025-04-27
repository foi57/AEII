<script setup>
import user from "../../api/user.js";
import {ElMessage} from "element-plus";
import {onMounted, ref} from "vue";
import {Search, UserFilled} from "@element-plus/icons-vue"; // 添加 UserFilled 图标
import {Store} from "../store/index.js";
import serverUrl from "../../serverUrl.js"; // 导入服务器URL
import token from "../assets/token.js";
// 引入裁剪相关组件
import 'vue-cropper/dist/index.css'
import {VueCropper} from "vue-cropper";

const userStore = Store();
let users=ref([]);
const count = ref(0)
let userRole=null;
onMounted(() => {
  userRole=userStore.usersRole;
   user.selectUser("admin",currentPage3.value,pageSize3.value).then(res => {
     console.log(res.data)
     count.value=res.data.count;
     users.value=res.data.data;
  })
       .catch(error => {
         ElMessage.error('数据获取失败');
       });
})
const selectUserType = (userType) => {
   user.selectUser(userType,currentPage3.value,pageSize3.value).then(res => {
     count.value=res.data.count;
    users.value=res.data.data;
  })
       .catch(error => {
         ElMessage.error('数据获取失败');
       });
}
const userType = ref('admin');
const userTypes = [
  {
    label: '高级管理员',
    value: 'seniorAdmin'
  },
  {
    label: '管理员',
    value: 'admin'
  },
  {
    label: '普通用户',
    value: 'user' 
  }
]
const currentPage3 = ref(1);
const pageSize3 = ref(50);
const disabled = ref(false);
const selectUserByName = (userName) => {
  user.selectUserByName(userName).then(res => {
    console.log("查询成功",res.data)
    users.value=res.data;
    count.value=users.value.length;
  }).catch(error => {
    ElMessage.error('数据获取失败')
    console.log(error)
  })
}
const editUser = (Id) => {
  handleDeleteDialog.value=true;
  userForm.value.id=Id;
}

const handleDialog = ref(false);
const userSelectValue = ref('');
const userForm = ref({
  id: '',
  userName: '',
  password: '',
  email: '',
  phone: '',
  role: '',
  avatar: '' // 添加avatar字段
})
const handleUserForm = (todo) => {
  formRef.value.validate((valid) => {  // 使用表单引用进行验证
    if (valid) {
      if (todo === 'insert') {
        console.log('userForm',userForm.value)
        user.insertUser(userForm.value).then(res => {
          ElMessage.success('添加成功')
          handleDialog.value = false;
          selectUserType(userType.value)
        }).catch(error => {
          ElMessage.error(error.response.data)
        })
      }
      if (todo === 'edit') {
        user.updateUser(userForm.value).then(res => {
          ElMessage.success('编辑成功')
          handleDialog.value = false;
          selectUserType(userType.value)
        })
      }
    } else {
      ElMessage.error('请填写完整表单内容')
      return false
    }
  })
}
const formRef=ref(null);
const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 16, message: '长度在 3 到 16 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
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
const insertOrEdit =ref('');
const handleDeleteDialog=ref(false);
const handleDeleteUser = () => {
  user.deleteUser(userForm.value.id).then(res => {
    ElMessage.success('删除成功')
    selectUserType(userType.value)
  }).catch(error => {
    ElMessage.error('删除失败')
  })
  handleDeleteDialog.value=false;
}

// 添加裁剪相关变量
const cropVisible = ref(false)
const cropper = ref(null)
const cropFile = ref(null) // 存储原始文件
const newAvatarUrl = ref('') // 存储裁剪后的图片URL

// 修改头像上传方法
const beforeAvatarUpload = (rawFile) => {
  // 暂存原始文件
  cropFile.value = rawFile
  // 打开裁剪弹窗
  cropVisible.value = true
  return false // 阻止自动上传
}

// 创建URL方法
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

// 裁剪确认方法
const confirmCrop = () => {
  cropper.value.getCropBlob(async (blob) => {
    const formData = new FormData()
    formData.append('file', new File([blob], cropFile.value.name, {
      type: blob.type,
      lastModified: Date.now()
    }))
    formData.append('id', userForm.value.id)
    
    user.updateAvatar(formData).then(res => {
      ElMessage.success('头像上传成功')
      newAvatarUrl.value = res.data
      cropVisible.value = false
      
      // 更新用户列表中的头像
      const updatedUser = users.value.find(u => u.usersId === userForm.value.id)
      if (updatedUser) {
        updatedUser.usersAvatar = res.data
      }
    }).catch(err => {
      ElMessage.error('头像上传失败')
      console.error(err)
    })
  })
}

// 裁剪器初始化完成回调
const onCropperReady = () => {
  console.log('裁剪器初始化完成')
  cropper.value?.setAspectRatio(1) // 强制设置1:1比例
}

// 修改编辑用户方法，添加头像信息
const editUserWithAvatar = (userData) => {
  insertOrEdit.value = 'edit';
  userForm.value = {
    id: userData.usersId,
    userName: userData.usersName,
    password: '', // 密码通常不显示原值
    email: userData.usersEmail,
    phone: userData.usersPhone,
    role: userData.usersRole,
    avatar: userData.usersAvatar || '' // 添加头像
  };
  handleDialog.value = true;
}

const token1=localStorage.getItem(token.token);
</script>
<template>

<el-input style="width: 240px;" placeholder="请输入用户名"  v-model="userSelectValue">
      <template #append>
        <el-button @click="selectUserByName(userSelectValue)">
          <el-icon><Search /></el-icon>
        </el-button>
      </template>
    </el-input>

  <div class="select-block">

    <label>用户类型:</label>
    <el-select v-model="userType" placeholder="Select" @change="selectUserType(userType)">
      <el-option
          v-for="item in userTypes"
          :key="item.value"
          :label="item.label"
          :value="item.value">
      </el-option>
    </el-select>
    <el-button v-if="userRole === 'seniorAdmin' " style="left: 350px; position: relative;;" @click="() =>{ insertOrEdit='insert';
       userForm = {
      userName: '',
      password: '', // 密码通常不显示原值
      email: '',
      phone: '',
      role: ''
    };
    handleDialog=true;
        }">添加用户</el-button>
  </div>
  <div>
    <el-table :data="users">
      <el-table-column prop="usersId" label="用户ID" v-if="false"></el-table-column>
      <!-- 添加头像列 -->
      <el-table-column label="头像" width="100">
        <template #default="scope">
          <el-avatar 
            :size="50"
            :src="scope.row.usersAvatar ? `${serverUrl.url}/users/avatar/${scope.row.usersAvatar}` : ''"
            @error="() => true"
          >
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="usersName" label="用户名"></el-table-column>
      <el-table-column prop="usersEmail" label="邮箱"></el-table-column>
      <el-table-column prop="usersPhone" label="手机号"></el-table-column>
      <el-table-column prop="userCreationTime" label="创建时间"></el-table-column>
      <el-table-column prop="usersRole" label="用户类型"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <template v-if="userRole === 'seniorAdmin' || (userRole === 'admin' && scope.row.usersRole === 'user')">
            <el-button type="primary" @click="editUserWithAvatar(scope.row)">编辑</el-button>
            <el-button type="danger" @click="editUser(scope.row.usersId)">删除院校</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <div class="pagination-block">
    <el-pagination
        v-model:current-page="currentPage3"
        v-model:page-size="pageSize3"
        :size="'default'"
        :disabled="disabled"
        layout="prev, pager, next, jumper"
        :total="count"
        @size-change=""
        @current-change=""
    />
  </div>
  <el-dialog title="编辑用户" v-model="handleDialog">
    <el-form :model="userForm" :rules="rules" ref="formRef">
      <!-- 添加头像显示和上传 -->
      <el-form-item label="头像">
        <!-- 显示当前头像或新上传的头像 -->
        <el-image 
          v-if="newAvatarUrl === ''" 
          :src="userForm.avatar ? `${serverUrl.url}/users/avatar/${userForm.avatar}` : ''" 
          style="max-width: 100px"
        >
          <template #error>
            <div class="image-slot">
              <el-icon :size="30"><UserFilled /></el-icon>
            </div>
          </template>
        </el-image>
        
        <el-image 
          v-if="newAvatarUrl !== ''" 
          :src="`${serverUrl.url}/users/avatar/${newAvatarUrl}`" 
          style="max-width: 100px"
        >
          <template #error>
            <div class="image-slot">
              <el-icon :size="30"><UserFilled /></el-icon>
            </div>
          </template>
        </el-image>
        
        <!-- 裁剪弹窗 -->
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
        
        <!-- 上传组件 -->
        <el-upload
          class="avatar-uploader"
          :before-upload="beforeAvatarUpload"
          :show-file-list="false"
          accept="image/*"
        >
          <el-button size="small" type="primary">更新头像</el-button>
        </el-upload>
      </el-form-item>
      
      <!-- 其他表单项保持不变 -->
      <el-form-item label="用户名" prop="userName">
        <el-input placeholder="请输入用户名" v-model="userForm.userName"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="insertOrEdit!=='edit'">
        <el-input placeholder="请输入密码" v-model="userForm.password" type="password" show-password></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input placeholder="请输入邮箱" v-model="userForm.email"></el-input>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input placeholder="请输入手机号" v-model="userForm.phone"></el-input>
      </el-form-item>
      <el-form-item label="用户类型">
        <el-select placeholder="请选择用户类型" v-model="userForm.role">
          <el-option label="管理员" value="admin"></el-option>
          <el-option label="高级管理员" value="seniorAdmin"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleDialog=false">取 消</el-button>
      <el-button type="primary" @click="handleUserForm(insertOrEdit)">确 定</el-button>
    </span>
  </el-dialog>
  <el-dialog title="删除用户" v-model="handleDeleteDialog">
    <span>确定删除该用户吗？</span>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleDeleteDialog=false">取 消</el-button>
      <el-button type="primary"  @click="handleDeleteUser">确 定</el-button>
    </span>
  </el-dialog>
  
</template>

<style scoped>
.el-select {
  --el-select-width: 100px;
}
.pagination-block {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}
.select-block {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 添加头像上传样式 */
.avatar-uploader {
  margin-top: 10px;
  margin-left: 10px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100px;
  height: 100px;
  background-color: #f5f7fa;
}
</style>