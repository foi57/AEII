<script setup>
import {ref} from "vue";
import adminMange from "../components/userManage.vue";
import router from "../router/index.js";
const userName = localStorage.getItem('userName')
const activeIndex = ref('0')
const outLogin = () => {
  router.push('/adminLogin')
  localStorage.removeItem('token')
  localStorage.removeItem('userName')
}
</script>

<template>
  <div>
    <el-container>
      <el-aside width="200px">

        <el-menu>
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
          <el-menu-item index="1">资讯</el-menu-item>
          <el-menu-item index="2">专业</el-menu-item>
          <el-menu-item index="3">学院</el-menu-item>
          <el-menu-item index="4">考试</el-menu-item>
          <el-menu-item index="5">用户</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <component :is="activeIndex === '1' ? infoMange :
                        activeIndex === '2'? majorMange :
                        activeIndex === '3'? collegeMange :
                        activeIndex === '4'? examMange :
                        activeIndex === '5'? adminMange : null"></component>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>

</style>