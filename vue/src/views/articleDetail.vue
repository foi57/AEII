<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import article from '../../api/article.js'
import { ElCard, ElLink } from 'element-plus' // 新增导入组件
import serverUrl from "../../serverUrl.js"; // 新增服务器地址配置

const route = useRoute()
const articleDetail = ref({})

onMounted(async () => {
  try {
    const res = await article.selectArticleDetail(route.params.id)
    articleDetail.value = res.data
  } catch (err) {
    console.error('获取文章详情失败:', err)
  }
})
</script>

<template>
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
</style>