<script setup>
import Header from "../components/header.vue";
import {ref} from "vue";
import Notification from "../components/Notification.vue";
import replyMe from "../components/replyMe.vue";
import { useRoute } from "vue-router";
const active=ref('1')
active.value=useRoute().params.active

</script>

<template>
 <Header/>
  <el-container>
  <el-aside width="200px">
    <el-menu
      default-active="1"
      class="el-menu-vertical-demo"
      @select="(val)=>active=val"
    >
      <el-menu-item index="1">
          <el-link>通知</el-link>
      </el-menu-item>
      <el-menu-item index="2">
          <el-link>回复我的</el-link>
      </el-menu-item>
    </el-menu>
  </el-aside>
  <el-main>
    <component :is="active === '1' ? Notification :
                    active === '2' ? replyMe : null"/>
 </el-main>
</el-container>   
</template>