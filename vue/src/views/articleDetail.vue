<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import article from '../../api/article.js'

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
  </div>
</template>
