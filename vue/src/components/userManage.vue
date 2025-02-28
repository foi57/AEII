<script setup>
import user from "../../api/user.js";
import {ElMessage} from "element-plus";
import {onMounted, ref} from "vue";
import {Search} from "@element-plus/icons-vue";
import {Store} from "../store/index.js";
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
</script>
<template>
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
    <el-input style="width: 240px" placeholder="请输入用户名" :suffix-icon="Search" v-model="userSelectValue"></el-input>
    <el-button type="primary" @click="selectUserByName(userSelectValue)">搜索</el-button>
  </div>
  <div>
    <el-table :data="users">
      <el-table-column prop="usersId" label="用户ID" v-if="false"></el-table-column>
      <el-table-column prop="usersName" label="用户名"></el-table-column>
      <el-table-column prop="usersEmail" label="邮箱"></el-table-column>
      <el-table-column prop="usersPhone" label="手机号"></el-table-column>
      <el-table-column prop="userCreationTime" label="创建时间"></el-table-column>
      <el-table-column prop="usersRole" label="用户类型"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope" v-if="userRole==='seniorAdmin'">
          <el-button type="primary" @click="() =>{insertOrEdit='edit';
       userForm = {
      id:scope.row.usersId,
      userName: scope.row.usersName,
      password: '', // 密码通常不显示原值
      email: scope.row.usersEmail,
      phone: scope.row.usersPhone,
      role: scope.row.usersRole
    };
            console.log('userForm',userForm)
       handleDialog=true;
       }">编辑</el-button>
          <el-button type="danger" @click="editUser(scope.row.usersId)">删除</el-button>
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
    <el-form  :model="userForm" :rules="rules" ref="formRef">
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
  <el-button @click="() =>{ insertOrEdit='insert';
       userForm = {
      userName: '',
      password: '', // 密码通常不显示原值
      email: '',
      phone: '',
      role: ''
    };
    handleDialog=true;
        }">添加用户</el-button>
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

</style>