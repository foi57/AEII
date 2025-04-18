<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import article from '../../api/article.js'
import { ElCard, ElLink, ElMessage } from 'element-plus' // 添加 ElMessage
import serverUrl from "../../serverUrl.js";
import Header from "../components/header.vue";
import Comment from "../components/releaseComment.vue";
import CommentList from "../components/commentList.vue";
import { Store } from "../store/index.js"; // 导入 Store

const route = useRoute()
const router = useRouter() // 添加 router
const userStore = Store() // 添加 userStore
const articleDetail = ref({})
// 添加一个ref用于触发评论列表刷新
const refreshComments = ref(0)

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return !!userStore.usersId && !!localStorage.getItem('accessToken')
})

// 处理需要登录的操作
const handleRequireLogin = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return false
  }
  return true
}

// 处理评论发布成功事件
const handleCommentSuccess = () => {
  // 增加计数器值，触发评论列表组件的更新
  refreshComments.value++
}

// 在script部分导入
import { setTitle } from '../utils/titleManager.js'

// 在onMounted中使用
onMounted(async () => {
  try {
    const res = await article.selectArticleDetail(route.params.id)
    articleDetail.value = res.data
    
    // 设置页面标题
    setTitle(articleDetail.value.articleTitle)
  } catch (err) {
    console.error('获取文章详情失败:', err)
  }
})
</script>

<template>
  <Header/>
  <div class="article-container">
    <h1>{{ articleDetail.articleTitle }}</h1>
    <div class="meta-info">
      <span>作者：{{ articleDetail.userName }}</span>
      <span>发布日期：{{ articleDetail.articleReleased }}</span>
    </div>
    <div class="content" v-html="articleDetail.articleContent"></div>

    <!-- 新增附件展示区块 -->
    <el-card v-if="articleDetail.attachments?.length" class="attachments-card">
      <template #header>
        <div class="card-header">
          <span>相关附件</span>
        </div>
      </template>
      <div v-for="(file, index) in articleDetail.attachments" :key="index" class="attachment-item">
        <el-link
          :href="serverUrl.url + file.url"
          target="_blank"
          type="primary"
          :title="file.name"
        >
          📎 {{ file.name }}
        </el-link>
      </div>
    </el-card>
  </div>
  <div>
    <h3>交流区</h3>
    <div class="comment-section">
      <!-- 添加@comment-success事件监听和登录检查 -->
      <Comment 
        @comment-success="handleCommentSuccess" 
        :check-login="handleRequireLogin"
      />
      <!-- 将refreshComments和登录检查传递给评论列表组件 -->
      <comment-list 
        :refresh-trigger="refreshComments" 
        :check-login="handleRequireLogin"
      />
    </div>
  </div>
</template>

<style scoped>
/* 新增附件样式 */
.attachments-card {
  margin-top: 30px;
}

.attachment-item {
  margin: 8px 0;
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.attachment-item:hover {
  background-color: #e9ecef;
}

.article-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.meta-info {
  color: #666;
  margin-bottom: 20px;
}

.meta-info span {
  margin-right: 20px;
}

.comment-section {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>