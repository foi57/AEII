<script setup>
import { ref,onMounted } from 'vue'
import { Store } from '../store/index.js';
import commentsNotification from '../../api/commentsNotification.js'
import commentNotificationCategory from '../assets/commentNotificationCategory.js';
import commentNotificationVue from './commentNotification.vue';

const pageNum = ref(1)
const pageSize = ref(5)
const userStore = Store();
const data = ref({
  records: [],
  total: 0
}) 

const loadNotifications = async() => {
    try{
        const formData = new FormData()
  // 确保这些值都是有效的
  formData.append('pageNum', pageNum.value)
  formData.append('pageSize', pageSize.value)
  formData.append('usersId', userStore.usersId)
  formData.append("category", commentNotificationCategory.replyMe)
        const res = await commentsNotification.selectCommentsNotificationByUserIdCategory(formData)
        data.value = res.data
    }catch(err){
        console.err(err)
        data.value = { records: [], total: 0 }
    }
}

const refreshData = () => {
    loadNotifications()
}

onMounted(() => {
    loadNotifications()
})

defineExpose({
    refreshData
})
</script>
<template>
<commentNotificationVue v-if="data.total!==0" :data="data"/>
</template>