<script setup>
import {Store} from '../store/index.js';
import commentNotificationCategory from '../assets/commentNotificationCategory.js';
import {onMounted, ref, watch, onBeforeUnmount} from 'vue'; // 添加 onBeforeUnmount
import commentsNotification from '../../api/commentsNotification.js';
import serverUrl from "../../serverUrl.js";
import { UserFilled } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import user from "../../api/user.js";
import comments from "../../api/comments.js";
import ReleaseComment from "./releaseComment.vue"; // 导入发布评论组件
import { inject } from 'vue'; // 添加 inject


// 添加事件定义
const emit = defineEmits(['refresh', 'page-change', 'refresh-counts'])

// 注入父组件提供的刷新方法和header引用
const refreshUnreadCounts = inject('refreshUnreadCounts')
const headerRef = inject('headerRef')

const isUnmounted = ref(false)
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const userStore = Store();
const commentList = ref([])
const notificationList = ref([])
const loading = ref(false)
const userNames = ref({}) // 存储用户名缓存

// 回复相关变量
const showReplyForm = ref(false)
const currentReplyId = ref(null)
const currentReplyName = ref('')
const currentToUserId = ref(null)
const currentArticleId = ref(null)

const props = defineProps({
  data: {
    type: Object,
    default: () => ({ records: [], total: 0 })
  },
  loading: {
    type: Boolean,
    default: false
  },
  isToMe: {
    type: Boolean,
    default: false
  },
})

// 添加一键已读按钮的加载状态
const markingAllAsRead = ref(false)

// 格式化时间
const formatTime = (timeStr) => {
  return new Date(timeStr).toLocaleString()
}

// 获取用户名
const fetchUserNames = async (userIds) => {
  for (const userId of userIds) {
    if (!userNames.value[userId]) {
      try {
        const res = await user.selectById(userId);
        userNames.value[userId] = res.data.usersName;
      } catch (error) {
        console.error('获取用户名失败:', error);
      }
    }
  }
}

// 加载评论详情
const loadCommentDetails = async (commentIds, type = 'reply') => {
  try {
    const promises = commentIds.map(async (commentId) => {
      const formData = new FormData()
      formData.append('id', Number(commentId))
      const res = await comments.selectCommentById(formData)
      return res.data
    })
    
    const results = await Promise.all(promises)
    if (type === 'reply') {
      commentList.value = results.filter(item => item) // 过滤掉可能的null值
    }
    return results.filter(item => item)
  } catch (error) {
    console.error('获取评论详情失败:', error)
    ElMessage.error('获取评论详情失败')
    return []
  }
}

// 加载原评论详情
const loadOriginalCommentDetails = async (commentIds) => {
  try {
    const originalComments = await loadCommentDetails(commentIds, 'original')
    
    // 将原评论与通知关联
    notificationList.value.forEach(notification => {
      const originalComment = originalComments.find(
        comment => comment.commentsId === notification.replyId
      )
      if (originalComment) {
        notification.originalComment = originalComment
      }
    })
  } catch (error) {
    console.error('获取原评论详情失败:', error)
  }
}

// 加载评论通知
const loadComment = async () => {
  if (isUnmounted.value) return // 如果组件已卸载，不执行加载
  
  loading.value = true
  const formData = new FormData()
  // 确保这些值都是有效的
  formData.append('pageNum', pageNum.value)
  formData.append('pageSize', pageSize.value)
  formData.append('usersId', userStore.usersId)
  formData.append("category", commentNotificationCategory.replyMe)
  try {
    // const res = await commentsNotification.selectCommentsNotificationByUserIdCategory(formData)
    console.log(props.data)
    notificationList.value = props.data.records
    total.value = props.data.total
    
    // 获取评论详情
    if (notificationList.value.length > 0) {
      // 获取回复评论的ID
      const replyCommentIds = notificationList.value.map(item => item.commentsId)
      // 获取原评论的ID
      const originalCommentIds = notificationList.value.map(item => item.replyId)
      
      // 加载回复评论
      await loadCommentDetails(replyCommentIds, 'reply')
      // 加载原评论
      await loadOriginalCommentDetails(originalCommentIds)
      
      // 收集所有用户ID
      const userIds = [
        ...commentList.value.map(comment => comment.usersId),
        ...notificationList.value.map(notification => notification.originalComment?.usersId).filter(Boolean)
      ]
      await fetchUserNames(userIds)
    }
  } catch (e) {
    console.log(e)
    ElMessage.error('获取回复通知失败')
  } finally {
    loading.value = false
  }
}

// 监听 props.data 变化
watch(() => props.data, (newData) => {
  if (newData && newData.records) {
    notificationList.value = newData.records
    total.value = newData.total
    loadComment()
  }
}, {  immediate: true })





// 标记通知为已读
const markAsRead = async (notificationId) => {
  try {
    const formData = new FormData()
    formData.append('notificationId', notificationId)
    await commentsNotification.markAsRead(formData)
    
    // 更新本地状态
    const notification = notificationList.value.find(n => n.notificationId === notificationId)
    if (notification) {
      notification.isRead = true
    }
    
    // 通知父组件更新未读数量
    if (refreshUnreadCounts) {
      refreshUnreadCounts()
    }
    
    // 直接调用header组件的刷新方法
    if (headerRef && headerRef.value) {
      headerRef.value.refreshHeardUnreadCount()
    }
    
    emit('refresh-counts')
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 修改处理页码变化的函数
const handleCurrentChange = (val) => {
  if (isUnmounted.value) return
  
  pageNum.value = val
  emit('page-change', val) // 向父组件发送页码变化事件
}

// 跳转到评论所在文章
const goToArticle = (articleId, commentId) => {
  window.location.href = `/article/detail/${articleId}`
}

// 查看评论并标记为已读
const viewComment = async (comment, notification) => {
  if (notification && !notification.isRead) {
    await markAsRead(notification.notificationId)
  }
  goToArticle(comment.articleId, comment.commentsId)
}

// 处理回复
const handleReply = (commentId,replyCommentId, userName, userId,articleId) => {
  if(props.isToMe && replyCommentId === null){
    currentReplyId.value = commentId
  }else{
    currentReplyId.value = replyCommentId
    currentToUserId.value = userId
  }
  currentReplyName.value = userName
  currentArticleId.value = articleId
  showReplyForm.value = true
}

// 评论发布成功后的回调
const onCommentSuccess = () => {
  showReplyForm.value = false
  // emit('refresh') // 通知父组件刷新数据
}

// 取消回复
const cancelReply = () => {
  showReplyForm.value = false
}


onBeforeUnmount(() => {
  isUnmounted.value = true
})

onMounted(() => {
  isUnmounted.value = false
})

// 添加一键已读所有通知的方法
const markAllAsRead = async () => {
  if (notificationList.value.length === 0 || isUnmounted.value) return
  
  // 检查是否有未读通知
  const hasUnread = notificationList.value.some(notification => !notification.isRead)
  if (!hasUnread) {
    ElMessage.info('没有未读通知')
    return
  }
  
  markingAllAsRead.value = true
  try {
    const formData = new FormData()
    formData.append('usersId', userStore.usersId)
    formData.append('category', props.isToMe ? commentNotificationCategory.toMe : commentNotificationCategory.replyMe)
    
    await commentsNotification.markAllAsRead(formData)
    
    // 更新本地状态
    notificationList.value.forEach(notification => {
      notification.isRead = true
    })
    
    ElMessage.success('所有通知已标记为已读')
    // emit('refresh') // 通知父组件刷新数据
    
    // 通知父组件更新未读数量
    if (refreshUnreadCounts) {
      refreshUnreadCounts()
    }
    
    // 直接调用header组件的刷新方法
    if (headerRef && headerRef.value) {
      headerRef.value.refreshHeardUnreadCount()
    }
    
    emit('refresh-counts')
  } catch (error) {
    console.error('一键已读失败:', error)
    ElMessage.error('一键已读失败')
  } finally {
    markingAllAsRead.value = false
  }
}
</script>

<template>
  <div class="reply-container">
    <div class="header-actions">
      <h2>{{ props.isToMe ? '@我的' : '回复我的' }}</h2>
      <el-button 
        type="primary" 
        size="small" 
        @click="markAllAsRead" 
        :loading="markingAllAsRead"
        :disabled="notificationList.length === 0 || notificationList.every(n => n.isRead)"
      >
        一键已读
      </el-button>
    </div>
    
    <el-empty v-if="commentList.length === 0 && !loading" description="暂无回复" />
    
    <el-skeleton :loading="loading" animated :count="3" v-if="loading">
      <template #template>
        <div style="padding: 14px">
          <el-skeleton-item variant="image" style="width: 40px; height: 40px; border-radius: 50%" />
          <div style="display: flex; align-items: center; margin-top: 16px">
            <el-skeleton-item variant="text" style="margin-right: 16px; width: 100px" />
            <el-skeleton-item variant="text" style="width: 300px" />
          </div>
          <div style="margin-top: 16px">
            <el-skeleton-item variant="text" style="width: 100%" />
          </div>
        </div>
      </template>
    </el-skeleton>
    
    <el-card 
      v-for="(comment, index) in commentList" 
      :key="index" 
      class="comment-card" 
      shadow="hover"
      :class="{ 'unread': notificationList[index] && !notificationList[index].isRead }"
    >
      <div class="comment-header">
        <el-avatar
          :src="`${serverUrl.url}/users/avatar/${comment.usersAvatar}`"
          v-if="comment.usersAvatar"
        />
        <el-avatar v-else :icon="UserFilled" />
        <span class="username">{{ userNames[comment.usersId] || '用户' }}</span>
        <span class="time">{{ formatTime(comment.createTime) }}</span>
        <el-tag 
          v-if="notificationList[index] && !notificationList[index].isRead" 
          size="small" 
          type="danger" 
          effect="dark"
          class="unread-tag"
        >
          未读
        </el-tag>
      </div>
      
      <!-- 回复评论内容 -->
      <div class="comment-content" @click="viewComment(comment, notificationList[index])">
        {{ comment.content }}
      </div>

      <!-- 原评论内容 -->
      <div v-if="notificationList[index]?.originalComment" class="original-comment">
        <div class="original-comment-header">
          <span>在该评论下回复你：</span>
        </div>
        <div class="original-comment-content">
          <el-tag size="small" type="info">原评论</el-tag>
          {{ notificationList[index].originalComment.content }}
        </div>
      </div>
      
      <!-- 显示评论图片 -->
      <el-row v-if="comment.picture && comment.picture.length" :gutter="10">
        <el-col
          v-for="(img, imgIndex) in comment.picture"
          :key="imgIndex"
          :span="2"
        >
          <el-image
            :src="`${serverUrl.url}/comments/images/${img}`"
            :preview-src-list="comment.picture.map(i => `${serverUrl.url}/comments/images/${i}`)"
            fit="cover"
          />
        </el-col>
      </el-row>
      
      <div class="comment-actions">
        <el-button 
          v-if="notificationList[index] && !notificationList[index].isRead"
          type="info" 
          size="small" 
          @click="markAsRead(notificationList[index].notificationId)"
        >
          标记为已读
        </el-button>
        <el-button 
          type="primary" 
          size="small"
          @click="handleReply(comment.commentsId,comment.replyId, userNames[comment.usersId], comment.usersId,comment.articleId)"
        >
          回复
        </el-button>
        <el-button type="primary" link @click="viewComment(comment, notificationList[index])">
          查看原文
        </el-button>
      </div>
    </el-card>
    
    <!-- 回复表单 -->
    <el-dialog
      v-model="showReplyForm"
      title="回复评论"
      width="50%"
      :close-on-click-modal="false"
    >
      <ReleaseComment 
        v-if="showReplyForm"
        :reply-id="currentReplyId"
        :reply-name="currentReplyName"
        :to-user-id="currentToUserId"
        :article-id="currentArticleId"
        @commentSuccess="onCommentSuccess"
        @cancel="cancelReply"
      />
    </el-dialog>
    
    <div class="pagination-container">
      <el-pagination
        v-if="total > pageSize"
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
.reply-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.comment-card {
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.comment-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 未读评论样式 */
.unread {
  border-left: 3px solid #f56c6c;
  background-color: #fef0f0;
}

.unread-tag {
  margin-left: 10px;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.username {
  margin-left: 10px;
  font-weight: bold;
}

.time {
  margin-left: auto;
  color: #999;
  font-size: 0.9em;
}

.comment-content {
  margin: 10px 0;
  line-height: 1.6;
  text-align: left;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
  gap: 10px; /* 按钮之间的间距 */
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.el-image {
  border-radius: 4px;
  margin-top: 10px;
  max-height: 100px;
  object-fit: cover;
}

.original-comment {
  margin: 10px 0;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #909399;
}

.original-comment-header {
  color: #606266;
  font-size: 0.9em;
  margin-bottom: 5px;
}

.original-comment-content {
  color: #606266;
  font-size: 0.95em;
  line-height: 1.5;
  margin-top: 5px;
  word-break: break-word;
}
</style>
