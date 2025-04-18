<script setup>
import {ref} from "vue";
import CollectionUniversity from "./university/collectionUniversity.vue";

const activeIndex = ref(1)
const handleSelect = (index) => {
  activeIndex.value = index
}
</script>

<template>

  <el-header>
    <el-menu
      :default-active="activeIndex"
      class="el-menu-demo"
      mode="horizontal"
      @select="handleSelect"
      style="border-radius: 5px">
      <el-menu-item index="1">收藏院校</el-menu-item>
    </el-menu>
  </el-header>
  <el-main>
    <template v-if="activeIndex === 1">
      <CollectionUniversity/>
    </template>
  </el-main>
</template>

<style scoped>

</style>