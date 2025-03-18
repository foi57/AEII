<script setup>
import {ref} from 'vue'
import majorApi from '../../api/major.js'
import router from '../router/index.js'

// 专业列表数据
const majorList = ref([])
const total = ref(0)
const pageSize = ref(12)
const currentPage = ref(1)
const searchName = ref('')

// 加载专业数据
const loadMajors = async () => {
  const res = await majorApi.selectMajorListByName(
    searchName.value,
    currentPage.value,
    pageSize.value
  )
  majorList.value = res.data.records
  total.value = res.data.total
}

// 跳转详情页
const toDetail = (majorName) => {
  const roueData = router.resolve({
    path: `/major/detail/${majorName}`
  })
  window.open(roueData.href, '_blank')
}

// 初始加载
loadMajors()
</script>

<template>
  <div class="major-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchName"
        placeholder="输入专业名称搜索"
        @change="loadMajors"
        style="width: 300px; margin-bottom: 20px"
      >
        <template #append>
          <el-button @click="loadMajors">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 专业列表 -->
    <el-row :gutter="20">
      <el-col
        v-for="major in majorList"
        :key="major.majorId"
        :span="6"
        class="major-card"
      >
        <el-card @click="toDetail(major.majorName)">
          <h3 class="major-name">{{ major.majorName }}</h3>
          <div class="major-info">
            <p>专业代码：{{ major.majorCode }}</p>
            <p>专业类型：{{ major.majorCategory}}</p>
            <p>学制年限：{{ major.duration }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分页 -->
    <div class="page-block">
      <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          v-model:current-page="currentPage"
          :page-size="pageSize"
          @current-change="loadMajors"
          style="margin-top: 20px"
      />
    </div>
    </div>

</template>

<style scoped>
.major-container {
  padding: 20px;
}

.major-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.major-card:hover {
  transform: translateY(-5px);
}

.major-name {
  color: #409EFF;
  margin-bottom: 15px;
}

.major-info p {
  text-align: left;
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}
.page-block {
  display: flex;
  justify-content: center;
}
</style>