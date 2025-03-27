<script setup>
import {ref} from "vue";
import universityCollection from "../../../api/universityCollection.js";
import {Store} from "../../store/index.js"
import serverUrl from "../../../serverUrl.js";
import universityApi from "../../../api/university.js";
const userStore = Store()
const page = ref(1)
const size = ref(30)
const count = ref(0)
const universityList = ref([])
const universityName = ref('')
const hoverUniversityId = ref(null);
const selectCollection = async () => {
  const formData = new FormData()
  formData.append('page', page.value)
  formData.append('size', size.value)
  formData.append('usersId', userStore.usersId)
  formData.append('universityName', universityName.value)
  await universityCollection.selectByUserIdAndUniversity(formData).then((res)=>{
    universityList.value = res.data.records
    count.value = res.data.total
  })

}

selectCollection()
const clickUniversity = (university) => {
  window.open(`${window.location.origin}/university/detail/${university.universityId}`, '_blank')
}
const suggestions = ref([]);
const querySearchAsync = ()=>  debounce(async (queryString, cb) => {
  if (queryString) {
    try {

      const formData = new FormData()
      formData.append('page', page.value)
      formData.append('size', size.value)
      formData.append('usersId', userStore.usersId)
      formData.append('universityName', universityName.value)


      const res =  await universityCollection.selectByUserIdAndUniversity(formData)
      suggestions.value = res.data.records.map(item => {
        return {value: item.universityName,
          id:item.universityId,
          ...item};
      });
      cb(suggestions.value);

    } catch (error) {
      console.error('获取建议失败:', error);
      cb([]);
    }
  } else {
    cb([]);
  }
}, 500);



// 防抖函数
function debounce(fn, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
}

</script>

<template>

  <el-autocomplete
      v-model="universityName"
      :fetch-suggestions="querySearchAsync('')"
      placeholder="请输入院校名称"
      @select="selectCollection"
      style="width: 300px; margin: 20px"
  ></el-autocomplete>
  <el-button @click="selectCollection">搜索</el-button>
  <div v-if="universityList" class="university-block">
    <div v-for="university in universityList" class="university-block-children" @click="clickUniversity(university)"
         @mouseenter="hoverUniversityId = university.universityId"
         @mouseleave="hoverUniversityId = null"
    >
      <img :src="serverUrl.url + '/images/university/' + university.universitySchoolbadge"
           alt="校徽"
           style="width: 100px; height: 100px"/> <br>
      {{university.universityName}} <br>
      {{university.universityArea}} <br>
      <p>{{university.universityLevel}}  {{university.universityType}} </p>
      {{university.universityFeatures}}

    </div>
  </div>

  <div v-else>
    <el-skeleton :rows="50"></el-skeleton>
  </div>
  <el-pagination
      v-model:current-page="page"
      v-model:page-size="size"
      :size="'default'"
      :disabled="false"
      layout="prev, pager, next, jumper"
      :total="count"
      @size-change="selectCollection"
      @current-change="selectCollection"
  />
</template>

<style scoped>
.university-block {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  padding: 10px;
}
.university-block-children {
  width: 220px;
  height: 250px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-bottom: 10px;
  margin-right: 10px;
  padding: 10px;
  box-sizing: border-box;
  transition: transform 0.3s ease-in-out; /* 新增过渡属性 */
  position: relative;
}
.university-block-children:hover {
  cursor: pointer; /* 新增指针样式 */
  transform: scale(1.10); /* 调整缩放值为更合理的幅度 */
  z-index: 1;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1); /* 可选：添加阴影增强立体感 */
}
.el-pagination {
  justify-content: center;
}
</style>