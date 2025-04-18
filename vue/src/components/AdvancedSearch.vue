<script setup>
import { ref, reactive } from 'vue'
import { Search, Calendar } from '@element-plus/icons-vue'
import article from '../../api/article'
import { useRouter } from 'vue-router'

const router = useRouter()
const emit = defineEmits(['search-results'])

const searchForm = reactive({
  keyword: '',
  articleType: '',
  startDate: '',
  endDate: '',
  pageNum: 1,
  pageSize: 10
})

const articleTypes = [
  { label: '全部', value: '' },
  { label: '招生信息', value: 'admissionsInformation' },
  { label: '考试通知', value: 'notice' },
  { label: '政策解读', value: 'policy' },
  { label: '备考指南', value: 'guide' }
]

const isLoading = ref(false)
const searchResults = ref([])
const total = ref(0)

// 执行高级搜索
const performSearch = async () => {
  if (!searchForm.keyword && !searchForm.articleType && !searchForm.startDate && !searchForm.endDate) {
    ElMessage.warning('请至少输入一个搜索条件')
    return
  }
  
  isLoading.value = true
  
  try {
    const res = await article.advancedSearchArticles(searchForm)
    searchResults.value = res.data.records
    total.value = res.data.total
    
    // 向父组件发送搜索结果
    emit('search-results', {
      results: searchResults.value,
      total: total.value
    })
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

// 重置搜索表单
const resetForm = () => {
  searchForm.keyword = ''
  searchForm.articleType = ''
  searchForm.startDate = ''
  searchForm.endDate = ''
  searchForm.pageNum = 1
}

// 跳转到文章详情
const goToArticleDetail = (articleId) => {
  router.push(`/article/detail/${articleId}`)
}

// 格式化文章类型
const formatArticleType = (type) => {
  const typeMap = {
    'admissionsInformation': '招生信息',
    'notice': '考试通知',
    'policy': '政策解读',
    'guide': '备考指南'
  }
  return typeMap[type] || '其他'
}

// 处理页码变化
const handleCurrentChange = (val) => {
  searchForm.pageNum = val
  performSearch()
}
</script>

<template>
  <div class="advanced-search">
    <el-card class="search-card">
      <template #header>
        <div class="card-header">
          <span>高级搜索</span>
        </div>
      </template>
      
      <el-form :model="searchForm" label-width="100px">
        <el-form-item label="关键词">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索标题或内容"
            clearable
            @keyup.enter="performSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="文章类型">
          <el-select 
            v-model="searchForm.articleType" 
            placeholder="选择文章类型"
            clearable
            style="width: 100%"
          >
            <el-option 
              v-for="item in articleTypes" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="searchForm.startDate"
            type="date"
            placeholder="开始日期"
            style="width: 48%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
          <span style="margin: 0 4%">至</span>
          <el-date-picker
            v-model="searchForm.endDate"
            type="date"
            placeholder="结束日期"
            style="width: 48%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="performSearch" :loading="isLoading">
            搜索
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 搜索结果 -->
    <div v-if="searchResults.length > 0" class="search-results">
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
          v-if="total > searchForm.pageSize"
          background
          layout="prev, pager, next"
          :current-page="searchForm.pageNum"
          :page-size="searchForm.pageSize"
          :total="total"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    
    <!-- 无结果提示 -->
    <el-empty v-if="searchResults.length === 0 && !isLoading && searchForm.keyword" description="未找到相关资讯" />
  </div>
</template>

<style scoped>
.advanced-search {
  margin-bottom: 30px;
}

.search-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
</style>