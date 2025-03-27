<script setup>
import {Store} from "../store/index.js";
import serverUrl from "../../serverUrl.js";
import {Notification, UserFilled} from "@element-plus/icons-vue";
import notificationUser from "../../api/notificationUser.js";
import {onMounted, ref} from "vue";
const userStore = Store();
const userAvatar = userStore.usersAvatar;
const userName = userStore.usersName;
const handleCommand = (command) => {
  if (command === 'c') {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    userStore.$reset()
    window.location.href = '/login'
  }
  else if (command === 'd') {
    window.location.href = '/login'
  }
  else if (command === 'a') {
    window.location.href = '/user'
  }else if (command === 'b') {
    window.location.href = '/collection'
  }
}
const unreadCounts=ref(0)
const unreadCount = () => {
  const userId = userStore.usersId
  notificationUser.unread(userId).then(res => {
    unreadCounts.value = res.data
  })
}
onMounted(unreadCount)
</script>

<template>
  <el-row class="header">
        <el-col :span="21">
        <ul class="menu">
          <li><el-link href="/userIndex">首页</el-link></li>
          <li><el-link href="/university">学校查询</el-link></li>
          <li><el-link href="/major">艺术类专业</el-link></li>
        </ul>
        </el-col>
        <el-col :span="1">
      <el-dropdown @command="handleCommand">
        <el-avatar :src="`${serverUrl.url}/users/avatar/${userAvatar}`" v-if="userAvatar !== null" />
        <el-avatar v-if="userAvatar === null" :icon="UserFilled" />
        <template #dropdown>
          <el-dropdown-menu v-if="userName!==null">
            <el-dropdown-item command="a">个人中心</el-dropdown-item>
            <el-dropdown-item command="b">收藏</el-dropdown-item>
            <el-dropdown-item command="c">退出登录</el-dropdown-item>
          </el-dropdown-menu>
          <el-dropdown-menu v-else>
            <el-dropdown-item command="d">登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
        </el-col>
        <el-link :underline="false" href="/notification">
        <el-col :span="2">
          <el-badge :value="unreadCounts">
          <el-row style=" white-space: nowrap">
            <el-icon size="24"><Notification /></el-icon>
            <span style="font-size: 12px;">消息</span>
          </el-row>
          </el-badge>
        </el-col>
        </el-link>
  </el-row>
</template>

<style scoped>
.menu {
  list-style: none;
  margin: 0;
  display: flex;
  justify-content: center;
}

.header {
  display: flex; /* 关键属性 */
  align-items: center; /* 垂直居中 */
  width: 100%; /* 确保宽度占满父容器 */
  box-sizing: border-box; /* 确保内边距不会增加元素的总宽度 */
  margin-bottom: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: rgb(148.6, 212.3, 117.1);
}
.menu li {
  margin: 0 15px; /* 列表项之间的间距 */
}
.avatar svg{
  height: 40px;
  width: 40px;
}
.avatar-dropdown {
  position: absolute;
  top: 60px;
  right: 20px;
}
</style>