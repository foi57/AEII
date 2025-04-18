<script setup>
import { ref, reactive, onMounted } from 'vue'
import article from '../../api/article';
import { useRouter,useRoute } from 'vue-router';
import { Search } from '@element-plus/icons-vue';
import Header from "../components/header.vue";
import AdvancedSearch from "../components/AdvancedSearch.vue";

const router = useRouter();
const articleForm = reactive({
  articleTitle: '',
  articleType: '',
  pageNum: 1, 
  pageSize: 10,
})

// 添加搜索结果和加载状态
const searchResults = ref([]);
const isLoading = ref(false);
const total = ref(0);
const noResults = ref(false);
const showAdvancedSearch = ref(false);

function debounce(fn, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
}

const querySearch = debounce(async (queryString, cb) => {
  if (queryString) {
    try {
      articleForm.articleTitle = queryString;
      const res = await article.selectArticleList(articleForm)
      searchResults.value = res.data.records.map(item => {
        return { value: item.articleTitle, ...item };
      });
      total.value = res.data.total;
      cb(searchResults.value);
    } catch (error) {
      cb([]);
    }
  } else {
    cb([]);
  }
},500)

// 处理选择搜索结果
const handleSelect = (item) => {
  router.push(`/article/detail/${item.articleId}`);
}

// 执行搜索
const performSearch = async () => {
  if (!articleForm.articleTitle.trim()) return;
  
  isLoading.value = true;
  noResults.value = false;
  
  try {
    const res = await article.selectArticleList(articleForm);
    searchResults.value = res.data.records;
    total.value = res.data.total;
    
    if (searchResults.value.length === 0) {
      noResults.value = true;
    }
  } catch (error) {
    console.error('搜索失败:', error);
  } finally {
    isLoading.value = false;
  }
}

// 处理高级搜索结果
const handleAdvancedSearchResults = (data) => {
  searchResults.value = data.results;
  total.value = data.total;
  noResults.value = data.results.length === 0;
}

// 切换高级搜索显示
const toggleAdvancedSearch = () => {
  showAdvancedSearch.value = !showAdvancedSearch.value;
}

// 处理页码变化
const handleCurrentChange = (val) => {
  articleForm.pageNum = val;
  performSearch();
}

// 跳转到文章详情
const goToArticleDetail = (articleId) => {
  router.push(`/article/detail/${articleId}`);
}

// 格式化文章类型
const formatArticleType = (type) => {
  const typeMap = {
    'admissionsInformation': '招生信息',
    'notice': '考试通知',
    'policy': '政策解读',
    'guide': '备考指南'
  };
  return typeMap[type] || '其他';
}

// 初始加载时执行搜索
onMounted(() => {
  // 从 URL 查询参数中获取关键词
  const route = useRoute();
  if (route.query.keyword) {
    articleForm.articleTitle = route.query.keyword;
    performSearch();
  } else if (articleForm.articleTitle) {
    performSearch();
  }
});
</script>

<template>
  <Header />
  <div class="search-container">
    <h2 class="search-title">艺考资讯搜索</h2>
    
    <div class="search-box">
      <el-autocomplete
        v-model="articleForm.articleTitle"
        placeholder="输入搜索内容"
        :fetch-suggestions="querySearch"
        @select="handleSelect"
        @keyup.enter="performSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-autocomplete>
      <el-button type="primary" @click="performSearch" :loading="isLoading">搜索</el-button>
      <el-button @click="toggleAdvancedSearch">{{ showAdvancedSearch ? '隐藏高级搜索' : '高级搜索' }}</el-button>
    </div>
    
    <!-- 高级搜索组件 -->
    <div v-if="showAdvancedSearch" class="advanced-search-container">
      <AdvancedSearch @search-results="handleAdvancedSearchResults" />
    </div>
    
    <!-- 搜索结果展示 -->
    <div class="search-results" v-if="searchResults.length > 0 && !showAdvancedSearch">
      <h3>搜索结果 (共 {{ total }} 条)</h3>
      
      <el-card v-for="(item, index) in searchResults" :key="index" class="result-item" @click="goToArticleDetail(item.articleId)">
        <div class="result-title">{{ item.articleTitle }}</div>
        <div class="result-meta">
          <span class="result-type" v-if="item.articleType">{{ formatArticleType(item.articleType) }}</span>
          <span class="result-date">{{ item.articleReleased }}</span>
        </div>
        <div class="result-content" v-if="item.articleContent">
          {{ item.articleContent.replace(/<[^>]+>/g, '').substring(0, 150) + '...' }}
        </div>
      </el-card>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-if="total > articleForm.pageSize"
          background
          layout="prev, pager, next"
          :current-page="articleForm.pageNum"
          :page-size="articleForm.pageSize"
          :total="total"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    
    <!-- 无结果提示 -->
    
    <!-- 加载中 -->
    <div v-if="isLoading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>
  </div>
</template>

<style scoped>
.search-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.search-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.search-box {
  display: flex;
  margin-bottom: 15px;
}

.search-input {
  flex: 1;
  margin-right: 10px;
}

.advanced-search-container {
  margin-bottom: 30px;
}

.search-results h3 {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  color: #303133;
}

.result-item {
  margin-bottom: 15px;
  cursor: pointer;
  transition: transform 0.3s;
}

.result-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.result-title {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.result-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
  color: #909399;
}

.result-type {
  background-color: #ecf5ff;
  color: #409EFF;
  padding: 2px 8px;
  border-radius: 4px;
}

.result-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.loading-container {
  margin-top: 20px;
}
</style>