<script setup>

import serverUrl from "../../serverUrl.js";
import {UserFilled} from "@element-plus/icons-vue";
import {Store} from "../store/index.js";
import {ref, reactive, onBeforeUnmount} from "vue";
import comments from "../../api/comments.js";
import {ElMessage} from "element-plus";
import user from "../../api/user.js";
import { useRoute } from 'vue-router';

const props = defineProps({
  replyId: Number,
  replyName: String,
  toUserId: Number,
  articleId: Number,
})
const articleId = ref(null)
if(useRoute().params.id)
 articleId.value = useRoute().params.id
else{
  articleId.value = props.articleId
}

const userStore = Store()
const userAvatar = userStore.usersAvatar
const commentData = reactive({
  commentContent: '',
  commentImg: [],
  commentUserId: userStore.usersId,
  toUserOptions: [],
  articleId: articleId.value,
  selectedUsersId: [] // 新增选中用户存储
})

if(props.toUserId){
  console.log('toUser',props.toUserId)
  commentData.selectedUsersId.push(props.toUserId)
  commentData.commentContent = `回复 ${props.replyName}:`
}

const beforeUpload = (file) => {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
  const isImage = allowedTypes.includes(file.type);

  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  if (!isImage) {
    ElMessage.error('只能上传图片格式!')
    return false
  }
  return true
}
const handleUploadSuccess = (response, file) => {
  commentData.commentImg.push(response)
  console.log('上传成功',commentData.commentImg)
}
const handleUploadError = () => {
  ElMessage.error('上传失败，请重试')
}
const handleRemove = (file, fileList) => {
}
const token = localStorage.getItem('accessToken')

const isUploadImg=ref(false)
const handleIsUploadImg = () => {
  isUploadImg.value = !isUploadImg.value
  if(isUploadImg.value) {
    commentData.commentImg = []
  }
}


const releaseComment = async () => {
  const formData = new FormData()
  formData.append('content', commentData.commentContent)
  formData.append('usersId', commentData.commentUserId)
  formData.append('picture', commentData.commentImg)
  formData.append('toUsersId', commentData.selectedUsersId)
  formData.append('articleId', commentData.articleId)
  if (props.replyId) {
    formData.append('replyId', props.replyId)
  }   
    try {
      const res = await comments.insertComment(formData)
      if (res.data) {
        ElMessage.success('发布成功')
        // 清空表单
        commentData.commentContent = ''
        commentData.picture = []
        
        // 触发事件并传递新评论数据
        emit('comment-success', res.data)
      }
    } catch (err) {
      console.error('评论发布失败:', err)
      ElMessage.error('评论发布失败')
    }
  }


const loading = ref(false)

let timer = ref(1)
const fetchUsers = debounce(async (query) => {  // 使用防抖函数包裹
  if (!query) return
  try {
    const res = await user.selectUserByNamePage(query,1,10) // 添加 await
     commentData.toUserOptions = res.data.records.map(item => ({
      value: item.usersName, // 与 Users.java 中的字段名保持一致
      label: item.usersName,       // 使用 usersName 而非 userName
      usersId: item.usersId,         // 保留用户ID用于后续处理
    }))

    loading.value = true
    setTimeout(() => {
      loading.value = false
    }, 500)
  } catch (error) {
    console.error('用户加载失败:', error)
    ElMessage.error('用户加载失败')
  }
}, 500) // 500ms 防抖间隔，与工程其他组件保持一致

// 添加防抖函数（与工程中其他组件保持相同实现）
function debounce(fn, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
}

onBeforeUnmount(() => {
  if (timer) clearTimeout(timer)
})
const handleUserSelect = (option) => {
  // 添加用户ID到选中列表
  if (!commentData.selectedUsersId.find(u => u.usersId === option.usersId)) {
    commentData.selectedUsersId.push(option.usersId)
  }
}

// 添加一个方法来通知父组件用户正在输入
const emit = defineEmits(['comment-success', 'userTyping'])

// 在输入框获得焦点或内容变化时触发
const handleInputFocus = () => {
  emit('userTyping', true)
}

const handleInputBlur = () => {
  emit('userTyping', false)
}
</script>

<template>
  <el-row style="justify-content: center">
    <el-col :span="2">
      <el-avatar :src="`${serverUrl.url}/users/avatar/${userAvatar}`" v-if="userAvatar !== null" />
      <el-avatar v-if="userAvatar === null" :icon="UserFilled" />
    </el-col>
    <el-col :span="20">
      <el-mention
          whole
          v-model="commentData.commentContent"
          type="textarea"
          :options="commentData.toUserOptions"
          :placeholder="props.replyName ?  `回复 ${props.replyName}:` : '发表评论'"
          @search="fetchUsers"
          @select="handleUserSelect"
          :loading="loading"
          value-key="value"
          @focus="handleInputFocus"
          @blur="handleInputBlur"
          @input="handleInputFocus"
      />
    </el-col>
  </el-row>
  <el-row>
    <el-upload
        v-if="isUploadImg"
        :action="`${serverUrl.url}/api/comments/upload/img`"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        :headers="{Authorization: `Bearer ${token}`}"
        :on-remove="handleRemove"
        :limit="5"
        list-type="picture-card"
        accept="image/*"
        style="margin-left: 160px"
    >
      <span><el-icon><Picture/></el-icon></span>
    </el-upload>
  </el-row>
  <el-row style="justify-content: space-between;padding-left: 100px; padding-right: 33px">
    <el-col :span="4" class="commentPlus">
      <span @click="handleIsUploadImg"><el-icon><Picture/></el-icon></span>
    </el-col>
    <el-col :span="2"><el-button type="primary" @click="releaseComment">发表</el-button></el-col>
  </el-row>
</template>

<style scoped>
.commentPlus span{
  margin: 0 10px;
}
.commentPlus span:hover{
  cursor: pointer;
  color: #409eff;
}
</style>