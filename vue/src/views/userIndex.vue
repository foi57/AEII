<script setup>
// ... 现有 imports ...
import { ElCard, ElSkeleton, ElRow, ElCol, ElEmpty, ElResult, ElLink } from 'element-plus'; // 引入所需组件
import Header from "../components/header.vue";
import ArtExamCalendar from "../components/ArtExamCalendar.vue";
import serverUrl from '../../serverUrl';
import article from "../../api/article.js";
import carousel from "../../api/carousel.js";
import hotArticle from "../../api/hotArticle.js";
import { reactive, ref, nextTick, onMounted } from "vue";
import { useRouter } from 'vue-router';
import { Search } from '@element-plus/icons-vue';
import * as echarts from 'echarts'; // 引入 ECharts
import majorApi from '../../api/major.js'; // 确保路径正确

const router = useRouter();

// --- 加载状态和错误处理 ---
const isLoading = ref(true); // 初始设为 true
const error = ref(null); // 存储错误信息

// --- 数据引用 ---
const carouselItems = ref([]);
const HotArticles = ref([]);
const latestArticle = ref([]);
const noticeArticle = ref([]);
const admissionsInformationArticle = ref([]);
const policyArticle = ref([]);
const guideArticle = ref([]);

// 新增：图表容器的 ref
const majorChartContainer = ref(null);

// --- 表单定义 ---
const baseForm = {
  articleTitle: '',
  articleType: '',
  pageNum: 1,
  pageSize: 10, // 首页可以适当减少 pageSize，比如 5 或 6
};

const articleForm = reactive({ ...baseForm }); // 用于搜索框
const latestForm = reactive({ ...baseForm }); // 最新文章
const noticeForm = reactive({ ...baseForm, articleType: 'notice' });
const admissionsInformationForm = reactive({ ...baseForm, articleType: 'admissionsInformation' });
const policyForm = reactive({ ...baseForm, articleType: 'policy' });
const guideForm = reactive({ ...baseForm, articleType: 'guide' });

// --- 数据加载函数 ---
const loadData = async (apiCall, form, targetRef) => {
  try {
    const res = await apiCall(form);
    targetRef.value = res.data.records || res.data; // 兼容不同接口返回格式
  } catch (err) {
    console.error(`加载 ${form.articleType || '数据'} 失败:`, err);
    // 可以选择性地为每个部分设置错误状态，或统一处理
    error.value = error.value || "部分数据加载失败"; // 记录第一个发生的错误
    targetRef.value = []; // 清空数据
  }
};

const loadLatest = () => loadData(article.selectArticleList, latestForm, latestArticle);
const loadNotice = () => loadData(article.selectArticleList, noticeForm, noticeArticle);
const loadAdmissions = () => loadData(article.selectArticleList, admissionsInformationForm, admissionsInformationArticle);
const loadPolicy = () => loadData(article.selectArticleList, policyForm, policyArticle);
const loadGuide = () => loadData(article.selectArticleList, guideForm, guideArticle);
const loadCarousel = () => loadData(carousel.getCarouselList, {}, carouselItems); // 无需表单
const loadHotArticles = () => loadData(hotArticle.getHotArticleList, {}, HotArticles); // 无需表单


// --- 生命周期钩子 ---
onMounted(async () => {
  isLoading.value = true;
  error.value = null; // 重置错误状态
  try {
    await Promise.all([ // 并行加载所有数据
      loadLatest(),
      loadNotice(),
      loadAdmissions(),
      loadPolicy(),
      loadGuide(),
      loadCarousel(),
      loadHotArticles(),
      await nextTick(),
    ]);
  } catch (err) {
    // Promise.all 如果有任何一个 reject，会进入 catch
    // 具体的错误已在 loadData 中处理，这里可以再记录一个总体错误
    console.error("首页数据加载过程中发生错误:", err);
    if (!error.value) { // 如果 loadData 内部没有设置错误信息
        error.value = "首页数据加载失败，请稍后重试";
    }
  } finally {
    isLoading.value = false; // 结束加载
  }
});

const toDetail = (articleId) => {
  // ... existing code ...
};

// --- 搜索相关 ---
function debounce(fn, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
}

let searchResults = ref([]);
const querySearch = debounce(async (queryString, cb) => {
  if (queryString) {
    articleForm.articleTitle = queryString; // 更新搜索表单
    try {
      // 注意：这里的 articleForm 可能没有指定 articleType，会搜索所有类型
      const res = await article.selectArticleList(articleForm);
      searchResults.value = res.data.records.map(item => {
        return { value: item.articleTitle, ...item }; // 返回 Autocomplete 需要的格式
      });
      cb(searchResults.value);
    } catch (error) {
      console.error('获取搜索建议失败:', error);
      cb([]);
    }
  } else {
    cb([]);
  }
}, 500);

const handleSelect = (item) => {
  if (item && item.articleId) {
    router.push(`/article/detail/${item.articleId}`);
  }
};

const performSearch = () => {
  if (articleForm.articleTitle.trim()) {
    router.push({
      path: '/articleSearch',
      query: { keyword: articleForm.articleTitle.trim() }
    });
  }
};

// 跳转到高级搜索页面的函数
const goToAdvancedSearch = () => {
  router.push('/articleSearch?advanced=true'); // 假设用查询参数区分
  // 或者直接跳转到高级搜索组件所在的路由（如果它是一个独立页面）
  // router.push('/advanced-search');
}

</script>

<template>
  <el-container>
    <el-header>
      <Header/>
    </el-header>
    <el-main>
  
      <!-- 轮播图和热门文章 -->
      <el-row :gutter="20">
        <el-col :xs="24" :sm="16"> <!-- 响应式布局 -->
          <div class="carousel-container">
            <el-skeleton :loading="isLoading" animated>
              <template #template>
                <el-skeleton-item variant="image" style="width: 100%; height: 400px;" />
              </template>
              <template #default>
                <el-carousel :interval="5000"  height="400px" v-if="carouselItems.length > 0">
                  <el-carousel-item v-for="item in carouselItems" :key="item.id">
                    <div class="carousel-content">
                      <a :href="item.link" target="_blank" v-if="item.link">
                        <img :src="`${serverUrl.url}/carousel/images/${item.picture}`" alt="轮播图" class="carousel-image">
                      </a>
                      <img v-else :src="`${serverUrl.url}/carousel/images/${item.picture}`" alt="轮播图" class="carousel-image">
                    </div>
                  </el-carousel-item>
                </el-carousel>
                <el-empty description="暂无轮播内容" v-else />
              </template>
            </el-skeleton>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8"> <!-- 响应式布局 -->
          <el-card  class="hot-article-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>热门文章</span>
                <!-- 可以添加图标或链接 -->
              </div>
            </template>
            <el-skeleton :rows="5" animated :loading="isLoading" />
            <template v-if="!isLoading">
              <div v-if="HotArticles.length > 0">
                <div v-for="(item, index) in HotArticles" :key="item.articleId" class="hot-article-item">
                   <!-- 使用 el-link 增加样式统一性 -->
                   <el-link :href="`/article/detail/${item.articleId}`" target="_blank" class="hot-index">
                     <!-- 可以考虑添加序号 -->
                     <!-- <span class="hot-index">{{ index + 1 }}</span> -->
                     {{ item.articleTitle }}
                   </el-link>
                </div>
              </div>
              <el-empty description="暂无热门文章" v-else />
            </template>
          </el-card>
        </el-col>
      </el-row>

 

      <!-- 最新文章 -->
      <el-card class="article-section-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>最新文章</span>
            <el-link :href="`/articles/最新文章`" type="primary" class="more-link">更多 &gt;</el-link>
          </div>
        </template>
        <el-skeleton :rows="5" animated :loading="isLoading" />
        <template v-if="!isLoading">
          <div v-if="latestArticle.length > 0">
            <div v-for="(item, index) in latestArticle" :key="index" class="article-item">
              <el-link :href="`/article/detail/${item.articleId}`" target="_blank">{{ item.articleTitle }}</el-link>
              <span class="article-date">{{ item.articleReleased }}</span>
            </div>
          </div>
          <el-empty description="暂无最新文章" v-else-if="!error" />
          <!-- 显示统一的错误提示 -->
          <el-result v-if="error && latestArticle.length === 0" status="error" title="加载失败" :sub-title="error">
          </el-result>
        </template>
      </el-card>

      <!-- 分类文章 - 使用栅格布局 -->
      <el-row :gutter="20" class="category-row">
        <!-- 考试通知 -->
        <el-col :xs="24" :sm="12" :md="12">
          <el-card class="article-section-card notice-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>考试通知</span>
                <el-link :href="`/articles/考试通知`" type="primary" class="more-link">更多 &gt;</el-link>
              </div>
            </template>
            <el-skeleton :rows="5" animated :loading="isLoading" />
            <template v-if="!isLoading">
              <div v-if="noticeArticle.length > 0">
                <div v-for="(item, index) in noticeArticle" :key="index" class="article-item">
                  <el-link :href="`/article/detail/${item.articleId}`" target="_blank">{{ item.articleTitle }}</el-link>
                  <span class="article-date">{{ item.articleReleased }}</span>
                </div>
              </div>
              <el-empty description="暂无考试通知" v-else-if="!error" />
              <el-result v-if="error && noticeArticle.length === 0" status="error" title="加载失败" :sub-title="error"></el-result>
            </template>
          </el-card>
        </el-col>

        <!-- 招生信息 -->
        <el-col :xs="24" :sm="12" :md="12">
          <el-card class="article-section-card admissions-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>招生信息</span>
                <el-link :href="`/articles/招生信息`" type="primary" class="more-link">更多 &gt;</el-link>
              </div>
            </template>
            <el-skeleton :rows="5" animated :loading="isLoading" />
             <template v-if="!isLoading">
               <div v-if="admissionsInformationArticle.length > 0">
                 <div v-for="(item, index) in admissionsInformationArticle" :key="index" class="article-item">
                   <el-link :href="`/article/detail/${item.articleId}`" target="_blank">{{ item.articleTitle }}</el-link>
                   <span class="article-date">{{ item.articleReleased }}</span>
                 </div>
               </div>
               <el-empty description="暂无招生信息" v-else-if="!error" />
               <el-result v-if="error && admissionsInformationArticle.length === 0" status="error" title="加载失败" :sub-title="error"></el-result>
             </template>
          </el-card>
        </el-col>

        <!-- 政策解读 -->
        <el-col :xs="24" :sm="12" :md="12">
          <el-card class="article-section-card policy-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>政策解读</span>
                <el-link :href="`/articles/政策解读`" type="primary" class="more-link">更多 &gt;</el-link>
              </div>
            </template>
            <el-skeleton :rows="5" animated :loading="isLoading" />
             <template v-if="!isLoading">
               <div v-if="policyArticle.length > 0">
                 <div v-for="(item, index) in policyArticle" :key="index" class="article-item">
                   <el-link :href="`/article/detail/${item.articleId}`" target="_blank">{{ item.articleTitle }}</el-link>
                   <span class="article-date">{{ item.articleReleased }}</span>
                 </div>
               </div>
               <el-empty description="暂无政策解读" v-else-if="!error" />
               <el-result v-if="error && policyArticle.length === 0" status="error" title="加载失败" :sub-title="error"></el-result>
             </template>
          </el-card>
        </el-col>

        <!-- 备考指南 -->
        <el-col :xs="24" :sm="12" :md="12">
          <el-card class="article-section-card guide-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>备考指南</span>
                <el-link :href="`/articles/备考指南`" type="primary" class="more-link">更多 &gt;</el-link>
              </div>
            </template>
            <el-skeleton :rows="5" animated :loading="isLoading" />
             <template v-if="!isLoading">
               <div v-if="guideArticle.length > 0">
                 <div v-for="(item, index) in guideArticle" :key="index" class="article-item">
                   <el-link :href="`/article/detail/${item.articleId}`" target="_blank">{{ item.articleTitle }}</el-link>
                   <span class="article-date">{{ item.articleReleased }}</span>
                 </div>
               </div>
               <el-empty description="暂无备考指南" v-else-if="!error" />
               <el-result v-if="error && guideArticle.length === 0" status="error" title="加载失败" :sub-title="error"></el-result>
             </template>
          </el-card>
        </el-col>
      </el-row>

      <!-- 艺考月历 -->
      <el-row>
        <el-col :span="24">
          <!-- 也可以给日历加个 Card 包裹 -->
          <el-card shadow="hover">
             <template #header>
               <div class="card-header">
                 <span>艺考月历</span>
               </div>
             </template>
             <ArtExamCalendar />
          </el-card>
        </el-col>
      </el-row>

    </el-main>
  </el-container>
</template>

<style scoped>
/* --- 基础布局和间距 --- */
.el-main {
  padding: 20px;
}

.el-row {
  margin-bottom: 20px;
}
.el-row:last-child {
  margin-bottom: 0;
}

.search-row {
  margin-bottom: 30px; /* 搜索行与其他内容间距更大 */
}
.search-container {
  display: flex;
  justify-content: center;
  align-items: center;
}
.search-input {
  margin-right: 10px; /* 搜索框和按钮间距 */
}

/* --- 卡片通用样式 --- */
.el-card {
  border: none; /* 移除默认边框，靠阴影区分 */
  border-radius: 8px; /* 圆角 */
}
.hot-article-card,
.article-section-card {
  height: 100%; /* 让卡片在栅格中高度一致 */
  min-height: 350px; /* 保证一定最小高度 */
  display: flex; /* 使用 flex 布局 */
  flex-direction: column; /* 垂直排列 */
}
.el-card :deep(.el-card__header) { /* 深度选择器修改内部组件样式 */
  background-color: #f8f9fa; /* 头部背景色 */
  border-bottom: 1px solid #e9ecef;
  padding: 10px 15px; /* 调整头部内边距 */
}
.el-card :deep(.el-card__body) {
  padding: 15px; /* 调整内容区内边距 */
  flex-grow: 1; /* 让内容区填充剩余空间 */
  display: flex; /* 再次使用 flex */
  flex-direction: column; /* 垂直排列 */
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px; /* 调整标题字号 */
  font-weight: 600; /* 加粗 */
  color: #343a40;
}
.more-link {
  font-size: 14px;
  font-weight: normal;
}

/* --- 文章列表项样式 --- */
.article-item, .hot-article-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px dashed #eee; /* 虚线分隔 */
  font-size: 14px; /* 统一字号 */
}
.article-item:last-child, .hot-article-item:last-child {
   border-bottom: none;
}
.article-item .el-link, .hot-article-item .el-link {
  flex-grow: 1;
  margin-right: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #303133; /* 默认链接颜色 */
}
.article-item .el-link:hover, .hot-article-item .el-link:hover {
  color: #409EFF; /* 悬停颜色 */
}
.article-date {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
}
.hot-article-item .hot-index {
  display: inline-block;
  width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 30%;
  background-color: #f2f6fc;
  color: #909399;
  margin-right: 8px;
  font-size: 12px;
}
.hot-article-item:nth-child(-n+3) .hot-index { /* 前三名不同样式 */
  background-color: #fef0f0;
  color: #f56c6c;
}

/* --- 分类卡片顶部颜色条 --- */
.notice-card :deep(.el-card__header) { border-top: 3px solid #E6A23C; }
.admissions-card :deep(.el-card__header) { border-top: 3px solid #67C23A; }
.policy-card :deep(.el-card__header) { border-top: 3px solid #409EFF; }
.guide-card :deep(.el-card__header) { border-top: 3px solid #F56C6C; }

/* --- 轮播图样式 --- */
.carousel-container {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.carousel-content {
  position: relative;
  width: 100%;
  height: 100%;
}
.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 覆盖方式填充 */
  display: block; /* 消除图片底部空隙 */
}


/* --- 骨架屏和空状态/错误状态 --- */
.el-skeleton {
  width: 100%;
}
.el-empty {
  flex-grow: 1; /* 让空状态占据剩余空间 */
  padding: 20px 0; /* 增加上下内边距 */
}
.el-result {
  flex-grow: 1; /* 让错误状态占据剩余空间 */
  padding: 20px 0;
}


</style>