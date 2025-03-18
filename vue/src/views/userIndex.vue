<script setup>
import article from "../../api/article.js";
import {reactive, ref, toRefs} from "vue";
import * as loadingInstance from "element-plus/es/components/notification/src/notify";
import {ElLoading} from "element-plus";

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
const loadingInstances = {
  latest: null,
  notice: null,
  admissions: null,
  policy: null,
  guide: null
}

const loadLatest = async () => {
  loadingInstances.latest = ElLoading.service({
    target: '.latestArticle-block',
    text: '加载最新文章...'
  })

  const res = await article.selectArticleList(form)
  latestArticle.value = res.data.records

  loadingInstances.latest?.close()
}

const loadNotice = async () => {
  loadingInstances.notice = ElLoading.service({
    target: '.notice-block',
    text: '加载通知...'
  })
  await article.selectArticleList(noticeForm).then(res => {
    noticeArticle.value=res.data.records
  })
  loadingInstances.notice?.close()
}

const loadAdmissions = async () => {
  loadingInstances.admissions = ElLoading.service({
    target: '.admissionsInformation-block',
    text: '加载招生信息...'
  })
  await article.selectArticleList(admissionsInformationForm).then(res => {
    admissionsInformationArticle.value=res.data.records
  })
  loadingInstances.admissions?.close()
}

const loadPolicy = async () => {
  loadingInstances.policy = ElLoading.service({
    target: '.policy-block',
    text: '加载政策...'
  })
  await article.selectArticleList(policyForm).then(res => {
    policyArticle.value=res.data.records
  })
}

const loadGuide = async () => {
  loadingInstances.guide = ElLoading.service({
    target: '.guide-block',
    text: '加载备考指南...'
  })
  await article.selectArticleList(guideForm).then(res => {
    guideArticle.value=res.data.records
  })
}

loadLatest()
loadNotice()
loadAdmissions()
loadPolicy()
loadGuide()

</script>

<template>
  <el-container>
    <el-header>
      <ul class="menu">
        <li><el-link href="/university">学校查询</el-link></li>
        <li><el-link href="/major">艺术类专业</el-link></li>
      </ul>
    </el-header>
    <el-main>
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
}

/* 方案2：单独设置每个区块 */
.latestArticle-block {
  background-color: #f8f9fa;
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
  justify-content: center;
  align-items: center; /* 垂直居中 */ /* 背景颜色 */
  border-radius: 8px; /* 圆角 */
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); /* 阴影效果 */
  background-color:  rgb(148.6, 212.3, 117.1); /* 背景颜色 */
  padding: 10px 20px; /* 内边距 */
}
.menu li {
  margin: 0 15px; /* 列表项之间的间距 */
}
</style>