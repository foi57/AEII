<script setup>
import universityApi from "../../api/university.js";
import {useRoute} from "vue-router";
import {ref,onMounted} from "vue";
import serverUrl from "../../serverUrl.js";
import {Connection, Iphone, Link, Location, Message, OfficeBuilding, StarFilled, Trophy} from "@element-plus/icons-vue";
import article from "../../api/article.js";
import major from "../../api/major.js";
import {ElLoading, ElMessage} from "element-plus";
import Header from "../components/header.vue";
import universityCollection from "../../api/universityCollection.js";
import {Store} from "../store/index.js";
import {setTitle} from "../utils/titleManager.js";

const userStore = Store()
const route = useRoute();
const universityId = route.params.id;
const university = ref({
  universitySchoolbadge: '',
  universityName: '加载中...',
  universityArea: '',
  universityLevel: '',
  universityType: '',
  universityFeatures: '',
  universityIntroduction: '',
  majors: [] // 新增专业列表字段
})
const articleList = ref([])
const getUniversity = async () => {
  await universityApi.selectUniversityById(universityId).then(res => {
    university.value = res.data;
    // 处理特色字段
    if(university.value.universityFeatures) {
      university.value.featureList = university.value.universityFeatures.split(',')
    }

    major.selectMajorListByUniversityId(universityId).then(res => {
      university.value.majors = res.data
    })

   handleChange()
   setTitle(university.value.universityName)
  }).catch(err => {
    console.error('获取院校信息失败:', err)
  })

}

onMounted(() => {
  getUniversity()
})


const categories = [
  {label: '最新文章', value: ''},
  { label: '招生信息', value: 'admissionsInformation' },
  { label: '考试通知', value: 'notice' },
  { label: '政策解读', value: 'policy' },
  { label: '备考指南', value: 'guide' }
]
const articleType = ref('')
const handleChange = async () => {
  const loadingInstance = ElLoading.service({
    target: '.university-detail-container',
    text: '正在加载文章...'
  })
  await article.selectArticlesByCategoryUniversityName(articleType.value,university.value.universityName,1,20).then(res => {
    articleList.value = res.data.records
  })
  loadingInstance.close()
}

const isLoading = ref(false)
const error = ref(null)

const hadCollect = ref(false)

const collect = async () => {
  if(hadCollect.value) {
    const collectFormData = new FormData()
    collectFormData.append('usersId',userStore.usersId)
    collectFormData.append('universityId',universityId)
    await universityCollection.delete(collectFormData).then(res => {
      hadCollect.value = false
    }).catch(err => {
      ElMessage.error('取消收藏失败')
    })
  }else {
    const collectFormData = new FormData()
    collectFormData.append('usersId',userStore.usersId)
    collectFormData.append('universityId',universityId)
    await universityCollection.collect(collectFormData).then(res => {
      hadCollect.value = true
      ElMessage.success('收藏成功,当院校有新消息会通知你')
    }).catch(err => {
      ElMessage.error('收藏失败')
    })
  }
}



const selectCollection = async () => {
  const collectFormData = new FormData()
  collectFormData.append('usersId',userStore.usersId)
  collectFormData.append('universityId',universityId)
  await universityCollection.selectCollectionByUserIdUniversityId(collectFormData).then(res => {
    if(res.data) {
     hadCollect.value = true
    }
  })
}
selectCollection()
</script>

<template>
  <Header></Header>
  <div class="university-detail-container">
    <el-row :gutter="20">
      <!-- 校徽和基本信息 -->
      <el-col :span="6">
        <el-card class="info-card">
          <img
            :src="serverUrl.url + '/images/university/' + university.universitySchoolbadge"
            alt="校徽"
            class="school-badge"
          />
          <h1 class="university-name">{{ university.universityName }}</h1>
          <div class="basic-info">
            <p><el-icon><Location /></el-icon> 地区：{{ university.universityArea }}</p>
            <p><el-icon><Trophy /></el-icon> 等级：{{ university.universityLevel }}</p>
            <p><el-icon><OfficeBuilding /></el-icon> 类型：{{ university.universityType }}</p>

            <el-button
              @click="collect"
              :type="hadCollect ? 'warning' : 'primary'"
              :icon="StarFilled"
            >
              {{ hadCollect ? '已收藏' : '收藏' }}
            </el-button>

            <!-- 新增联系信息 -->
            <div class="contact-info" v-if="university.universityPhone || university.universityEmail || university.universityWeb">
              <h3><el-icon><Connection /></el-icon> 联系方式</h3>
              <p v-if="university.universityPhone">
                <el-icon><Iphone /></el-icon>
                电话：<a :href="'tel:' + university.universityPhone">{{ university.universityPhone }}</a>
              </p>
              <p v-if="university.universityEmail">
                <el-icon><Message /></el-icon>
                邮箱：<a :href="'mailto:' + university.universityEmail">{{ university.universityEmail }}</a>
              </p>
              <p v-if="university.universityWeb">
                <el-icon><Link/></el-icon>
                官网：<a :href="university.universityWeb" target="_blank">{{ university.universityWeb }}</a>
              </p>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 详细信息 -->
      <el-col :span="18">
        <el-card class="detail-card">

          <!-- 新增专业展示模块 -->
          <h2>开设艺术类专业</h2>
          <div class="majors">
            <el-tag
                v-for="major in university.majors"
                :key="major.majorId"
                type="success"
                class="major-tag"
            >
              {{ major.majorName }}
            </el-tag>
          </div>

          <h2>院校特色</h2>
          <div class="features">
            <el-tag
              v-for="(feature, index) in university.featureList"
              :key="index"
              type="info"
              class="feature-tag"
            >
              {{ feature }}
            </el-tag>
          </div>

          <h2>院校介绍</h2>
          <div class="introduction">
            <el-scrollbar height="400px">
              <pre>{{ university.universityIntroduction }}</pre>
            </el-scrollbar>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="article-block">
          <el-select v-model="articleType" @change="handleChange" style="width: 120px">
            <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            >
            </el-option>
          </el-select>
          <h3>院校资讯<span><el-link :href="`/university/articles/${university.universityName}/${
      categories.find(item => item.value === articleType)?.label}`">>>更多</el-link></span></h3>
          <template v-if="!isLoading && !error">
            <div v-for="(item, index) in articleList" :key="index">
              <p>
                <el-link
                    :href="`/article/detail/${item.articleId}`"
                    target="_blank"
                >{{ item.articleTitle }}</el-link>
                <span>{{item.articleReleased}}</span>
              </p>
            </div>
            <div v-if="articleList.length === 0">暂无数据</div>
          </template>
          <template v-else>
            <div v-if="isLoading">加载中...</div>
            <div v-if="error">加载失败: {{ error }}</div>
          </template>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.university-detail-container {
  padding: 20px;
}

.info-card {
  text-align: center;
  position: sticky;
  top: 20px;
}

.school-badge {
  width: 200px;
  height: 200px;
  margin-bottom: 20px;
}

.university-name {
  color: #2c3e50;
  margin-bottom: 20px;
}

.basic-info p {
  text-align: left;
  margin: 15px 0;
  font-size: 14px;
}

.basic-info i {
  margin-right: 5px;
}

.detail-card {
  margin-left: 20px;
  min-height: 600px;
}

.features {
  margin: 20px 0;
}

.feature-tag {
  margin-right: 10px;
  margin-bottom: 10px;
}

.introduction pre {
  white-space: pre-wrap;
  font-family: inherit;
  line-height: 1.6;
}


.contact-info {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.contact-info h3 {
  color: #666;
  margin-bottom: 12px;
}

.contact-info p {
  margin: 8px 0;
  font-size: 14px;
}

.contact-info a {
  color: #409EFF;
  text-decoration: none;
}

.contact-info a:hover {
  text-decoration: underline;
}

.contact-info .el-icon {
  vertical-align: middle;
  margin-right: 5px;
}

/* 新增专业样式 */
.majors {
  margin: 20px 0;
}
.major-tag {
  margin-right: 10px;
  margin-bottom: 10px;
  font-size: 14px;
  padding: 8px 12px;
}
.article-block p{
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.article-block .moreArticle{
  color: #409EFF;
  text-decoration: none;
}
.article-block p span{
  color: #666;
  font-size: 12px;
}
.article-block h3 span{
  position: relative;
  left: 100px;
}
.el-row{
  margin-bottom: 20px;
}

.el-button--warning {
  background-color: #ffc107;
  border-color: #ffc107;
}

</style>