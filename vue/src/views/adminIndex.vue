<script setup>
import {ref} from "vue";
import adminMange from "../components/userManage.vue";
import router from "../router/index.js";
import {Store} from "../store/index.js";
import userCenter from "../components/userCenter.vue";
import majorMange from "../components/majorMange.vue";
import universityMange from "../components/university/universityMange.vue";
import articlePublish from "../components/articlePublish.vue";
import articleMange from "../components/articleMange.vue";
import carousel from "../components/carousel.vue"; // 导入轮播图组件
import hotArticleManage from "../components/hotArticleManage.vue"; // 导入热门文章管理组件
const userStore = Store();
const userName = userStore.usersName;
console.log(userName)
const activeIndex = ref('0') // 初始可以设为 '0' 或其他默认项
const outLogin = () => {
  router.push('/adminLogin')
  localStorage.removeItem('token')
  userStore.$reset()
  localStorage.removeItem('userStore')
}
</script>

<template>
  <div>
    <el-container>
      <el-aside width="200px">

        <el-menu
        :default-active="activeIndex"
        @select="(index) => activeIndex = index">
          <el-sub-menu>
            <template #title>
              <span>{{userName}}</span>
            </template>
            <el-menu-item index="1">个人中心</el-menu-item>
            <el-menu-item index="2" @click="outLogin">退出登录</el-menu-item>
          </el-sub-menu>
        </el-menu>
        <el-menu
            :default-active="activeIndex"
            @select="(index) => activeIndex = index">
          <el-sub-menu>
            <template #title>
              <span>资讯</span>
            </template>
            <el-menu-item index="3">发布资讯</el-menu-item>
            <el-menu-item index="6">管理资讯</el-menu-item>
          </el-sub-menu>
        </el-menu>
        <el-menu
        :default-active="activeIndex"
        @select="(index) => activeIndex = index">

          <el-menu-item index="4">专业</el-menu-item>
          <el-menu-item index="5">学院</el-menu-item>
          <el-menu-item index="7">用户</el-menu-item>
        </el-menu>
        <el-menu
        :default-active="activeIndex"
        @select="(index) => activeIndex = index">
          <el-sub-menu>
            <template #title>
              <span>主页管理</span>
            </template>
            <el-menu-item index="8">轮播图</el-menu-item>
            <el-menu-item index="9">热点信息</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main>
        <component :is="
                        activeIndex === '1'? userCenter :
                        activeIndex === '3' ? articlePublish :
                        activeIndex === '6'?  articleMange:
                        activeIndex === '4'? majorMange :
                        activeIndex === '5'? universityMange :
                        activeIndex === '7'? adminMange : 
                        activeIndex === '8'? carousel :
                        activeIndex === '9'? hotArticleManage :
                        null"></component>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>

</style>