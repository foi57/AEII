<script setup>
import {ref} from "vue";
import userCenter from "../components/userCenter.vue";
import Header from "../components/header.vue";
import Collection from "../components/collection.vue";
const activeIndex = ref('0')
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
        </el-menu>
      </el-aside>
      <el-main>
        <Component :is="activeIndex === '1' ? userCenter : activeIndex === '2' ? Collection : null"></Component>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>

</style>