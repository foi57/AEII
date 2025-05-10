<template>
  <el-card>
    <el-table :data="feedbackList" v-loading="loading">
      <el-table-column prop="usersname" label="用户名" width="80"/>
      <el-table-column prop="content" label="反馈内容"/>
      <el-table-column prop="contact" label="联系方式"/>
      <el-table-column label="截图">
        <template #default="{row}">
          <el-image 
            v-for="(img, index) in row.images"
            :key="index"
            :src="serverUrl.url + '/feedback/images/' + img"
            :preview-src-list="row.images.map(i => serverUrl.url + '/feedback/images/' + i)"
            class="feedback-image"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="180"/>
    </el-table>
    
    <el-pagination
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      @current-change="handlePageChange"
      layout="prev, pager, next"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import feedback from '../../../api/feedback'
import serverUrl from '../../../serverUrl'

const loading = ref(false)
const feedbackList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = async () => {
  try {
    loading.value = true
    const res = await feedback.select(pageNum.value, pageSize.value)
    feedbackList.value = res.data.records
    console.log(feedbackList.value)
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  pageNum.value = val
  loadData()
}

onMounted(loadData)
</script>
