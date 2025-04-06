<script setup>
import Header from "../components/header.vue";
import {ref, provide, onMounted} from "vue";
import Notification from "../components/Notification.vue";
import replyMe from "../components/replyMe.vue";
import { useRoute } from "vue-router";
import ToMe from "../components/toMe.vue";
import notificationUser from "../../api/notificationUser.js";
import commentsNotification from "../../api/commentsNotification.js";
import commentNotificationCategory from "../assets/commentNotificationCategory.js";
import {Store} from "../store/index.js";

const userStore = Store();
const active = ref('1')
active.value = useRoute().params.active

// 添加未读消息计数
const notificationCount = ref(0)
const replyMeCount = ref(0)
const atMeCount = ref(0)

// 添加对Header组件的引用
const headerRef = ref(null)

// 获取未读通知数量
const fetchUnreadCounts = async () => {
  if (!userStore.usersId) return;
  
  const userId = userStore.usersId
  
  // 获取系统通知未读数量
  try {
    const notificationRes = await notificationUser.unread(userId)
    notificationCount.value = notificationRes.data
  } catch (error) {
    console.error('获取系统通知未读数量失败:', error)
  }
  
  // 获取回复我的未读数量
  try {
    const formData = new FormData()
    formData.append('usersId', userId)
    formData.append('category', commentNotificationCategory.replyMe)
    const replyRes = await commentsNotification.countUnRead(formData)
    replyMeCount.value = replyRes.data
  } catch (error) {
    console.error('获取回复未读数量失败:', error)
  }
  
  // 获取@我的未读数量
  try {
    const atMeFormData = new FormData()
    atMeFormData.append('usersId', userId)
    atMeFormData.append('category', commentNotificationCategory.toMe)
    const atMeRes = await commentsNotification.countUnRead(atMeFormData)
    atMeCount.value = atMeRes.data
  } catch (error) {
    console.error('获取@我未读数量失败:', error)
  }
}

// 提供刷新未读数量的方法给子组件
const refreshUnreadCounts = () => {
  fetchUnreadCounts()
  // 同时刷新header组件的未读数量
  if (headerRef.value) {
    headerRef.value.refreshHeardUnreadCount()
  }
}

// 提供刷新方法给子组件
provide('refreshUnreadCounts', refreshUnreadCounts)
provide('headerRef', headerRef)

onMounted(() => {
  fetchUnreadCounts()
})
</script>

<template>
 <Header ref="headerRef"/>
  <el-container>
  <el-aside width="200px">
    <el-menu
      :default-active="active"
      class="el-menu-vertical-demo"
      @select="(val)=>active=val"
    >
      <el-menu-item index="1">
        <el-badge :value="notificationCount" :max="99" :hidden="notificationCount === 0" class="menu-badge">
          <el-link>通知</el-link>
        </el-badge>
      </el-menu-item>
      <el-menu-item index="2">
        <el-badge :value="replyMeCount" :max="99" :hidden="replyMeCount === 0" class="menu-badge">
          <el-link>回复我的</el-link>
        </el-badge>
      </el-menu-item>
      <el-menu-item index="3">
        <el-badge :value="atMeCount" :max="99" :hidden="atMeCount === 0" class="menu-badge">
          <el-link>@我的</el-link>
        </el-badge>
      </el-menu-item>
    </el-menu>
  </el-aside>
  <el-main>
    <component 
      :is="active === '1' ? Notification :
           active === '2' ? replyMe :
           active === '3' ? ToMe : null"
      @refresh-counts="refreshUnreadCounts"
    />
 </el-main>
</el-container>   
</template>

<style scoped>
.menu-badge {
  width: 100%;
}
.el-menu-item {
  display: flex;
  align-items: center;
}
</style>