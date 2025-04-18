<script setup>
import UniversityList from "../components/university/universityList.vue";
import Header from "../components/header.vue";
import { onMounted } from "vue";
import { setTitle } from "../utils/titleManager";

onMounted(() => {
  setTitle("高校列表");
});
</script>

<template>
<Header/>
<university-list/>
</template>

<style scoped>

</style>