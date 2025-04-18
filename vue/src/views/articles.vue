<script setup>
import {useRoute} from "vue-router";
import {reactive, ref} from "vue";
import article from "../../api/article.js";
import {ElLoading} from "element-plus";
import Header from "../components/header.vue";
const route = useRoute()

const props = defineProps({
  universityName: String,
  category: String
})

let category = null
let universityName = props.universityName
if (universityName){
  category = props.category
}else {
  category = route.params.category
}
console.log('category',category)
console.log('universityName',universityName)
const articleList = ref([])
const count = ref(0)
const currentPage3 = ref(1);
const pageSize3 = ref(50);
const disabled = ref(false);
const selectName = ref('');
const form = reactive({
  articleTitle: '',
  articleType: category,
  pageNum: currentPage3,
  pageSize: pageSize3,
})
const isLoading = ref(false)
const error = ref(null)
const select=async ()=> {

  let loadingInstance = null // 声明在外部作用域
  try {
    isLoading.value = true
    loadingInstance = ElLoading.service({
      target: '.latestArticle-block',
      text: '正在加载文章...'
    })

    let sCategory
    sCategory = category
    if (category === '最新文章') {
      sCategory = ''
    } else if (category === '考试通知') {
      sCategory = 'notice'
    } else if (category === '招生信息') {
      sCategory = 'admissionsInformation'
    } else if (category === '政策解读') {
      sCategory = 'policy'
    } else if (category === '备考指南') {
      sCategory = 'guide'
    }


    if (universityName) {
      article.selectArticlesByCategoryUniversityName(sCategory, universityName, form.pageNum, form.pageSize).then(res => {
        articleList.value = res.data.records
        count.value = res.data.total
      })
    }

else if (category) {

      form.articleType = sCategory
      await article.selectArticleList(form).then(res => {
        articleList.value = res.data.records;
        count.value = res.data.total;
      })
    }
  }catch (err) {
    console.error('加载失败:', err)
  } finally {
    // 添加空值检查
    if (loadingInstance) {
      loadingInstance.close()
    }
    isLoading.value = false
  }
}
select()
</script>

<template>
<Header/>

  <div>
  <h3>{{category}}</h3>
  <template v-if="!isLoading && !error">
    <div v-for="(item, index) in articleList" :key="index" class="article-block">
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
  <div class="page-block">
    <el-pagination
        v-if="!isLoading &&!error"
        :current-page="currentPage3"
        :page-size="pageSize3"
        :total="count"
        :disabled="disabled"
        @current-change="select"
        @size-change="select"
    />
  </div>
</template>
<style scoped>
.article-block p{
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.article-block p span{
  color: #888;
  font-size: 12px;
}
.page-block{
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>