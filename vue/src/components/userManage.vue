<script setup>
import user from "../../api/user.js";
import {ElMessage} from "element-plus";
import router from "../router/index.js";
import {onMounted, ref} from "vue";
let users=ref([]);
onMounted(() => {
   user.selectUser("admin",currentPage3.value,pageSize3.value).then(res => {
    users.value=res.data;
  })
       .catch(error => {
         ElMessage.error('数据获取失败');
       });
})
const selectUserType = (userType) => {
   user.selectUser(userType,currentPage3.value,pageSize3.value).then(res => {
    users.value=res.data;
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
const size = ref('medium');
const disabled = ref(false);
</script>
<template>
  <el-row justify="center">
    <el-col>
      <label>用户类型:</label>
      <el-select v-model="userType" placeholder="Select" @change="selectUserType(userType)">
        <el-option
            v-for="item in userTypes"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>
    </el-col>
  </el-row>
  <div>
    <el-table :data="users">
      <el-table-column prop="usersName" label="用户名"></el-table-column>
      <el-table-column prop="usersEmail" label="邮箱"></el-table-column>
      <el-table-column prop="usersPhone" label="手机号"></el-table-column>
      <el-table-column prop="userCreationTime" label="创建时间"></el-table-column>
      <el-table-column prop="usersRole" label="用户类型"></el-table-column>
    </el-table>
  </div>
  <div class="pagination-block">
    <el-pagination
        v-model:current-page="currentPage3"
        v-model:page-size="pageSize3"
        :size="size"
        :disabled="disabled"
        layout="prev, pager, next, jumper"
        :total="1000"
        @size-change=""
        @current-change=""
    />
  </div>
</template>

<style scoped>
.el-select {
  --el-select-width: 100px;
}
.pagination-block {

}
</style>