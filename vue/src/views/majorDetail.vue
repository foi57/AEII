<script setup>
import {ref} from "vue";
import major from "../../api/major.js";
import {useRoute} from "vue-router";
import UniversityList from "../components/university/universityList.vue";
import Header from "../components/header.vue";
const route = useRoute()
const majorName = route.params.name
const majorList = ref()
const loadDate =async () => {
   await major.selectMajorListByName(majorName,1,1).then(res => {
    majorList.value = res.data.records[0]
  })
  console.log(majorList.value)
}
loadDate()
</script>

<template>
  <Header></Header>
<div>
  <el-row v-if="majorList">
    <el-col :span="5">
  <el-card class="info-card" >
    <h1>{{ majorList.majorName }}</h1>
    <div class="basic-info">
      <p>专业代码：{{ majorList.majorCode }}</p>
      <p>专业类别：{{ majorList.majorCategory }}</p>
      <p>学制年限：{{ majorList.duration }}</p>
    </div>

    <h2>专业介绍</h2>
    <div class="introduction">
      <el-scrollbar >
        {{ majorList.majorIntroduction }}
      </el-scrollbar>
    </div>
  </el-card>
    </el-col>
    <el-col :span="19">
      <el-card>
        <h2>开设院校</h2>
        <UniversityList  :major-id="majorList.majorId" />
      </el-card>
    </el-col>
  </el-row>
</div>
</template>

<style scoped>

</style>