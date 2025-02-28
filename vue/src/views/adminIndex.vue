<script setup>
import {ref} from "vue";
import adminMange from "../components/userManage.vue";
import router from "../router/index.js";
import {Store} from "../store/index.js";
import userCenter from "../components/userCenter.vue";
const userStore = Store();
const userName = userStore.usersName;
console.log(userName)
const activeIndex = ref('0')
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
          <el-menu-item index="3">资讯</el-menu-item>
          <el-menu-item index="4">专业</el-menu-item>
          <el-menu-item index="5">学院</el-menu-item>
          <el-menu-item index="6">考试</el-menu-item>
          <el-menu-item index="7">用户</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <component :is="
                        activeIndex === '1'? userCenter :
                        activeIndex === '3' ? infoMange :
                        activeIndex === '4'? majorMange :
                        activeIndex === '5'? collegeMange :
                        activeIndex === '6'? examMange :
                        activeIndex === '7'? adminMange : null"></component>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>

</style>