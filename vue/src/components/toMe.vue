<script setup>
import { ref, onMounted, watch, onBeforeUnmount } from 'vue' // 添加 onBeforeUnmount
import { Store } from '../store/index.js';
import commentsNotification from '../../api/commentsNotification.js'
import commentNotificationCategory from '../assets/commentNotificationCategory.js';
import commentNotificationVue from './commentNotification.vue';

const pageNum = ref(1)
const pageSize = ref(5)
const userStore = Store();
const data = ref({
  records: [],
  total: -1
}) 

// 添加加载状态
const loading = ref(false)
// 添加卸载标记
const isUnmounted = ref(false)

const loadNotifications = async() => {
    if (isUnmounted.value) return
    
    loading.value = true
    try{
        const formData = new FormData()
        // 确保这些值都是有效的
        formData.append('pageNum', pageNum.value)
        formData.append('pageSize', pageSize.value)
        formData.append('usersId', userStore.usersId)
        formData.append("category", commentNotificationCategory.toMe)
        const res = await commentsNotification.selectCommentsNotificationByUserIdCategory(formData)
        if (!isUnmounted.value) {
            data.value = res.data
        }
    } catch(err) {
        if (!isUnmounted.value) {
            console.error(err)
            data.value = { records: [], total: -1 }
        }
    } finally {
        if (!isUnmounted.value) {
            loading.value = false
        }
    }
}

// 添加页码变化处理函数，移除重复加载
const handlePageChange = (newPage) => {
    if (isUnmounted.value) return
    
    pageNum.value = newPage
    loadNotifications()
}

// 移除 watch 监听器中的数据加载，避免重复加载
watch(() => pageNum.value, () => {
    // 不再重复调用 loadNotifications
})

// const refreshData = () => {
//     if (isUnmounted.value) return
    
//     pageNum.value = 1 // 刷新时重置为第一页
//     loadNotifications()
// }

onMounted(() => {
    isUnmounted.value = false
    loadNotifications()
})

onBeforeUnmount(() => {
    isUnmounted.value = true
})

// defineExpose({
//     refreshData
// })
</script>

<template>
  <div>
    <commentNotificationVue 
      v-if="data.total !== -1" 
      :data="data" 
      :loading="loading"
      @refresh="refreshData"
      @page-change="handlePageChange"
    />
    <el-empty v-else-if="!loading" description="暂无@我的消息" />
    <el-skeleton v-else :rows="5" animated />
  </div>
</template>