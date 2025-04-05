<script setup>
import {Store} from "../store/index.js";
import serverUrl from "../../serverUrl.js";
import {Notification, UserFilled} from "@element-plus/icons-vue";
import notificationUser from "../../api/notificationUser.js";
import {onMounted, ref} from "vue";
import comments from "../../api/comments.js";
import commentsNotification from "../../api/commentsNotification.js";
import commentNotificationCategory from "../assets/commentNotificationCategory.js";

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
const unreadCounts = ref(0)
const notificationCount = ref(0)
const replyMeCount = ref(0)
const atMeCount = ref(0)

// 获取未读通知数量
const unreadCount = () => {
  if (!userStore.usersId) return;
  
  const userId = userStore.usersId
  // 获取系统通知未读数量
  notificationUser.unread(userId).then(res => {
    notificationCount.value = res.data
    updateTotalCount()
  })
  
  // 获取回复我的未读数量
  // const formData = new FormData()
  // formData.append('usersId', userId)
  // formData.append('category', commentNotificationCategory.replyMe)
  // commentsNotification.countUnread(formData).then(res => {
  //   replyMeCount.value = res.data
  //   updateTotalCount()
  // })
  
  // // 获取@我的未读数量
  // const atMeFormData = new FormData()
  // atMeFormData.append('usersId', userId)
  // atMeFormData.append('category', commentNotificationCategory.toMe)
  // commentsNotification.countUnread(atMeFormData).then(res => {
  //   atMeCount.value = res.data
  //   updateTotalCount()
  // })
}

// 更新总未读数
const updateTotalCount = () => {
  unreadCounts.value = notificationCount.value + replyMeCount.value + atMeCount.value
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
    
    <el-col :span="2">
      <!-- 总未读消息徽章 -->
      <el-badge :value="unreadCounts" :max="99" :hidden="unreadCounts === 0" class="notification-badge">
        <el-dropdown trigger="click">
          <div class="notification-icon">
            <el-icon size="24"><Notification /></el-icon>
            <span class="notification-text">消息</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <!-- 通知未读徽章 -->
              <el-dropdown-item>
                <el-badge :value="notificationCount" :max="99" :hidden="notificationCount === 0" class="item-badge">
                  <el-link href="/notification/1">通知</el-link>
                </el-badge>
              </el-dropdown-item>
              
              <!-- 回复我的未读徽章 -->
              <el-dropdown-item>
                <el-badge :value="replyMeCount" :max="99" :hidden="replyMeCount === 0" class="item-badge">
                  <el-link href="/notification/2">回复我的</el-link>
                </el-badge>
              </el-dropdown-item>
              
              <!-- @我的未读徽章 -->
              <el-dropdown-item>
                <el-badge :value="atMeCount" :max="99" :hidden="atMeCount === 0" class="item-badge">
                  <el-link href="/notification/3">@我的</el-link>
                </el-badge>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-badge>
    </el-col>
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

/* 新增样式 */
.notification-badge {
  margin-right: 20px;
}

.notification-icon {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.notification-text {
  font-size: 12px;
  margin-top: 2px;
}

.item-badge {
  width: 100%;
}
</style>