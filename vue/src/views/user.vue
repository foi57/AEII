<script setup>
import {ref} from "vue";
import userCenter from "../components/userCenter.vue";
import Header from "../components/header.vue";
import Collection from "../components/collection.vue";
import { useRoute } from "vue-router";
import token from "../assets/token";
import { Store } from "../store/index";
import FeedbackForm from "../components/feedback/FeedbackForm.vue"; // 引入反馈组件
const userStore = Store()

const route = useRoute()
const activeIndex = ref('0')
if(route.params.active){
  console.log('active',route.params.active)
  activeIndex.value=String(route.params.active)
}

const outLogin = () => {
  localStorage.removeItem(token.token)
    localStorage.removeItem(token.refreshToken)
    userStore.$reset()
    window.location.href = '/login'
}
</script>

<template>

  <Header/>

  <div class="common-layout">
    <el-container>
      <el-aside width="100px">
        <el-menu
            :default-active="activeIndex"
            @select="(index) => activeIndex = index"
        >
          <el-menu-item index="1">个人中心</el-menu-item>
          <el-menu-item index="2">我的收藏</el-menu-item>
          <el-menu-item index="3">退出登录</el-menu-item>
          <el-menu-item index="4">意见反馈</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <Component :is="activeIndex === '1' ? userCenter : activeIndex === '2' ? Collection : activeIndex=== '3' ? outLogin() : activeIndex === '4' ? FeedbackForm : null"></Component>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>

</style>