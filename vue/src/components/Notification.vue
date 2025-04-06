<script setup>
import notification from "../../api/notification.js";
import { Store } from "../store/index.js";
import { onMounted, ref, inject } from 'vue'
import {ElEmpty, ElMessage, ElPagination} from 'element-plus'
import notificationUser from "../../api/notificationUser.js";
import Header from "../components/header.vue";

const userStore = Store();
const notificationList = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)

// 注入父组件提供的刷新方法和header引用
const refreshUnreadCounts = inject('refreshUnreadCounts')
const headerRef = inject('headerRef')

// 定义事件
const emit = defineEmits(['refresh-counts'])

const fetchNotifications = async () => {
  try {
    loading.value = true
    const res = await notification.selectNotification(
      userStore.usersId,
      page.value,
      size.value
    )
    notificationList.value = res.data.records
    console.log('通知加载成功:', res.data.records)
    total.value = res.data.total
  } catch (error) {
    console.error('通知加载失败:', error)
    ElMessage.error('通知加载失败')
  } finally {
    loading.value = false
  }
}
onMounted(fetchNotifications)

const read = async (notificationListId) => {
  const formData = new FormData()
  formData.append('userId', userStore.usersId)
  formData.append("notificationId", notificationListId)
  try {
    await notificationUser.read(formData)
    // 标记已读成功后，通知父组件更新未读数量
    if (refreshUnreadCounts) {
      refreshUnreadCounts()
    }
    
    // 直接调用header组件的刷新方法
    if (headerRef && headerRef.value) {
      headerRef.value.refreshHeardUnreadCount()
    }
    
    // 同时触发事件
    emit('refresh-counts')
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}
</script>

<template>
 
  <div class="notification-container">
    <h2 class="page-title">我的消息通知</h2>

    <div v-loading="loading">
      <div v-if="notificationList.length" class="notification-list">
        <div
          v-for="item in notificationList"
          :key="item.notificationId"
          class="notification-item"
          :class="{ 'unread': !item.isRead }"
        >
          <div class="notification-content">
            <span class="category-tag">{{ item.category }}</span>
            <p>{{ item.content }}</p>
            <div class="notification-footer">
              <time>{{ item.articleReleased }}</time>
              <router-link
                  :to="item.link"
                  custom
                  v-slot="{ href }"
              >
              <el-link
                :href="item.link"
                type="primary"
                class="detail-link"
                @click="()=>{
                  if(!item.isRead)
                    item.isRead = true
                  read(item.notificationId)
                }"
                target="_blank"
              >
                查看详情
              </el-link>
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无通知消息" />

      <el-pagination
        v-if="total > size"
        v-model:current-page="page"
        v-model:page-size="size"
        layout="prev, pager, next"
        :total="total"
        @current-change="fetchNotifications"
        class="pagination"
      />
    </div>
  </div>
</template>

<style scoped>
.notification-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.page-title {
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
  margin-bottom: 24px;
}

.notification-list {
  margin-top: 20px;
}

.notification-item {
  padding: 16px;
  margin-bottom: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  transition: all 0.3s;
}

.notification-item.unread {
  background: #e6f7ff;
  border-left: 4px solid #1890ff;
}

.notification-item:hover {
  transform: translateX(5px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-tag {
  display: inline-block;
  padding: 2px 8px;
  background: #1890ff;
  color: white;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 8px;
}

.notification-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  color: #666;
  font-size: 12px;
}

.detail-link {
  font-size: 12px;
}

.pagination {
  margin-top: 24px;
  justify-content: center;
}
</style>