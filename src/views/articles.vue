<script setup>
// 添加props声明
const props = defineProps({
  universityName: String,
  category: String
})

// 替换原route.params.category为props.category
const sCategory = props.category || route.params.category
// 其他代码保持原样...
</script>
