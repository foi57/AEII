<script setup>
import article from "../../api/article.js";
import {reactive, ref, toRefs, onMounted, onBeforeUnmount} from "vue";
import {Store} from "../store/index.js";
import Header from "../components/header.vue";
import { useRouter } from 'vue-router'; // 导入路由
import { Search } from '@element-plus/icons-vue'; // 导入搜索图标
import ArtExamCalendar from "../components/ArtExamCalendar.vue"; // 导入艺考月历组件

const router = useRouter(); // 使用路由
const userStore = Store();
const form = reactive({
  articleTitle: '',
  articleType: '',
  pageNum: 1,
  pageSize: 10,
})

// 添加加载状态
const isLoading = ref(false)
const error = ref(null)
let latestArticle = ref([])
let noticeArticle = ref([])
let admissionsInformationArticle = ref([])
let policyArticle = ref([])
let guideArticle = ref([])
const noticeForm = reactive({
  ...toRefs(form),
  articleType: 'notice'
})
const admissionsInformationForm = reactive({
  ...toRefs(form),
  articleType: 'admissionsInformation'
})
const policyForm = reactive({
 ...toRefs(form),
  articleType: 'policy'
})
const guideForm = reactive({
  ...toRefs(form),
  articleType: 'guide'
})


const loadLatest = async () => {


  const res = await article.selectArticleList(form)
  latestArticle.value = res.data.records

}

const loadNotice = async () => {

  await article.selectArticleList(noticeForm).then(res => {
    noticeArticle.value=res.data.records
  })

}

const loadAdmissions = async () => {

  await article.selectArticleList(admissionsInformationForm).then(res => {
    admissionsInformationArticle.value=res.data.records
  })

}

const loadPolicy = async () => {

  await article.selectArticleList(policyForm).then(res => {
    policyArticle.value=res.data.records
  })
}

const loadGuide = async () => {

  await article.selectArticleList(guideForm).then(res => {
    guideArticle.value=res.data.records
  })
}

loadLatest()
loadNotice()
loadAdmissions()
loadPolicy()
loadGuide()
const handleCommand = (command) => {
  if (command === 'c') {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    userStore.$reset()
    window.location.href = '/login'
  }
  else if (command === 'd') {
    window.location.href = '/login'
  }
  else if (command === 'a') {
    window.location.href = '/user'
  }
}

const articleForm = reactive({
  articleTitle: '',
  articleType: '',
  pageNum: 1, 
  pageSize: 10,
})

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
    try {
      const res = await article.selectArticleList(articleForm)
       searchResults.value = res.data.records.map(item => {
        return { value: item.articleTitle, ...item };
      });
      cb(searchResults.value);
    } catch (error) {
      cb([]);
    }
  } else {
    cb([]);
  }
},500)

// 处理搜索结果选择
const handleSelect = (item) => {
  router.push(`/article/detail/${item.articleId}`);
}

// 执行搜索并跳转到搜索页面
const performSearch = () => {

    router.push({
      path: '/articleSearch',
      query: { keyword: articleForm.articleTitle }
    });
  
}

</script>

<template>
  <el-container>
    <el-header>
      <Header/>
    </el-header>
    <el-main>
      <el-row>
        <el-col :span="16" :offset="4" class="search-container">
          <el-autocomplete
            v-model="articleForm.articleTitle"
            placeholder="搜索文章"
            :fetch-suggestions="querySearch"
            @select="handleSelect"
            @keyup.enter="performSearch"
            class="search-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="performSearch">搜索</el-button>
            </template>
          </el-autocomplete>
          <el-button @click="performSearch">高级搜索</el-button>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <div class="banner">
            <div class="banner-content">
              <h1>欢迎来到中国艺考信息网</h1>
              <p>中国艺考信息网是一个为中国艺考生生提供考试信息、招生信息、政策解读、备考指南、院校信息、艺术专业等服务的网站。</p>
            </div>
          </div>
        </el-col>
      </el-row>
      
    

      <div class="latestArticle">
      <div class="latestArticle-block">
        <h3>最新文章<span class="moreArticle"><el-link :href="`/articles/最新文章`">》更多</el-link></span></h3>
        <template v-if="!isLoading && !error">
          <div v-for="(item, index) in latestArticle" :key="index">
            <p>
              <el-link
                  :href="`/article/detail/${item.articleId}`"
                  target="_blank"
              >{{ item.articleTitle }}</el-link>
              <span>{{item.articleReleased}}</span>
            </p>
          </div>
        </template>
        <div v-else-if="isLoading" class="loading-text">数据加载中...</div>
        <div v-else class="error-text">数据加载失败，请稍后重试</div>
      </div>
    </div>
      <div class="categoryArticle">
        <div class="notice-block">
          <template v-if="!isLoading && !error">
            <h3>考试通知<span class="moreArticle"><el-link :href="`/articles/考试通知`">》更多</el-link></span></h3>
            <div v-for="(item, index) in noticeArticle" :key="index">
              <p>
                <el-link
                    :href="`/article/detail/${item.articleId}`"
                    target="_blank"
                >{{ item.articleTitle }}</el-link>
                <span>{{item.articleReleased}}</span>
              </p>
            </div>
          </template>
          <div v-else-if="isLoading" class="loading-text">数据加载中...</div>
          <div v-else class="error-text">数据加载失败，请稍后重试</div>
        </div>
        <div class="admissionsInformation-block">
          <template v-if="!isLoading &&!error">
            <h3>招生信息<span class="moreArticle"><el-link :href="`/articles/招生信息`">》更多</el-link></span></h3>
            <div v-for="(item, index) in admissionsInformationArticle" :key="index">
              <p>
                <el-link
                    :href="`/article/detail/${item.articleId}`"
                    target="_blank"
                >{{ item.articleTitle }}</el-link>
                <span>{{item.articleReleased}}</span>
              </p>
            </div>
          </template>
          <div v-else-if="isLoading" class="loading-text">数据加载中...</div>
          <div v-else class="error-text">数据加载失败，请稍后重试</div>
        </div>
        <div class="policy-block">
          <template v-if="!isLoading && !error">
            <h3>政策解读<span class="moreArticle"><el-link :href="`/articles/政策解读`">》更多</el-link></span></h3>
            <div v-for="(item, index) in policyArticle" :key="index">
              <p>
                <el-link
                    :href="`/article/detail/${item.articleId}`"
                    target="_blank"
                >{{ item.articleTitle }}</el-link>
                <span>{{item.articleReleased}}</span>
              </p>
            </div>
          </template>
          <div v-else-if="isLoading" class="loading-text">数据加载中...</div>
          <div v-else class="error-text">数据加载失败，请稍后重试</div>
        </div>
        <div class="guide-block">
          <template v-if="!isLoading &&!error">
            <h3>备考指南<span class="moreArticle"><el-link :href="`/articles/备考指南`">》更多</el-link></span></h3>
            <div v-for="(item, index) in guideArticle" :key="index">
              <p>
                <el-link
                    :href="`/article/detail/${item.articleId}`"
                    target="_blank"
                >{{ item.articleTitle }}</el-link>
                <span>{{item.articleReleased}}</span>
              </p>
            </div>
          </template>
          <div v-else-if="isLoading" class="loading-text">数据加载中...</div>
          <div v-else class="error-text">数据加载失败，请稍后重试</div>
        </div>
      </div>

        <!-- 添加艺考月历组件 -->
        <el-row>
        <el-col :span="24">
          <ArtExamCalendar />
        </el-col>
      </el-row>
      
    </el-main>
  </el-container>

</template>

<style scoped>
[class$="-block"] {
  padding: 20px;
  margin: 15px 0;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  height: 500px;
  width: 500px;
  box-sizing: border-box;
  position: relative; /* 新增定位属性 */
}

/* 方案2：单独设置每个区块 */
.latestArticle-block {
  border: 1px solid #e9ecef;
}

.notice-block {
  background-color: #fff3cd;
  border: 1px solid #ffeeba;
}

.admissionsInformation-block {
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
}

.policy-block {
  background-color: #d1ecf1;
  border: 1px solid #bee5eb;
}

.guide-block {
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
}

[class$="-block"] div p {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

[class$="-block"] div p span{
  font-size: 12px;
  color: #888;
}
.latestArticle{
  display: flex;
  justify-content: center;
}
.moreArticle{
  position: relative;
  left: 100px;

}
.categoryArticle {
  display: flex;
  flex-wrap: wrap; /* 允许换行 */
  gap: 20px; /* 添加间距 */
}

/* 子元素设置 */
.categoryArticle > div {
  flex: 1; /* 等分剩余空间 */
  min-width: calc(50% - 10px); /* 最小宽度保持一行两个 */
  box-sizing: border-box; /* 包含padding和border */
}
.menu {
  list-style: none;
  margin: 0;
  display: flex;
  justify-content: center; /* 改为左对齐 */
  align-items: center; /* 垂直居中 */
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: rgb(148.6, 212.3, 117.1);
  padding: 10px 20px;
  flex-grow: 1; /* 允许菜单项扩展 */
}


.el-header {
  display: flex; /* 关键属性 */
  justify-content: space-between; /* 左右分布 */
  align-items: center; /* 垂直居中 */
  padding: 0 30px; /* 添加水平内边距 */
}
.menu li {
  margin: 0 15px; /* 列表项之间的间距 */
}
.avatar svg{
  height: 40px;
  width: 40px;
}
.avatar-dropdown {
  position: absolute;
  top: 60px;
  right: 20px;
}
</style>