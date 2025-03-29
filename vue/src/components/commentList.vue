<script setup>
import {useRoute} from "vue-router";
import {ref,onMounted} from "vue";
import comments from "../../api/comments.js";
import { UserFilled } from '@element-plus/icons-vue'
import serverUrl from "../../serverUrl.js"
import { Store } from '../store/index.js'
import user from "../../api/user.js";
import {ElMessage} from "element-plus";
import ReleaseComment from "./releaseComment.vue";
const userStore = Store()

const formatTime = (timeStr) => {
  return new Date(timeStr).toLocaleString()
}

const userName = ref('')
// 根据用户ID获取用户名（需要根据实际数据结构调整）
const getUserNameById = async (userId) => {
  const res = await user.selectById(userId)
  console.log('用户名',res.data.usersName)
  return res.data.usersName
}
const commentList = ref([])

const userNames = ref({}) // 新增响应式对象存储用户名

// 获取所有评论中的用户名称
const fetchUserNames = async () => {
  const userIds = [...new Set(commentList.value.map(c => c.usersId))];
  for (const userId of userIds) {
    const res = await user.selectById(userId);
    userNames.value[userId] = res.data.usersName;
  }
}

const getcomments =  async ()=>{
const articleId = useRoute().params.id;

  if (!articleId) {
    console.error('缺少文章ID参数');
    return;
  }

const formData = new FormData();
formData.append('articleId',articleId)
formData.append('pageNum',1)
formData.append('pageSize',20)
  comments.selectCommentList(formData).then(res=>{
    commentList.value = res.data.records
    fetchUserNames()
  }).catch(err=>{
    console.log(err)
    ElMessage.error('获取失败')
  })
}

onMounted(()=>{
  getcomments()
})

const isReply=ref(false)
const handleReplay = ()=>{
  isReply.value = !isReply.value
}
</script>

<template>
  <div class="comment-container">
    <el-card
        v-for="(comment, index) in commentList"
        :key="index"
        class="comment-item"
    >
      <div class="comment-header">

        <el-avatar
            :src="`${serverUrl.url}/users/avatar/${comment.usersAvatar}`"
            v-if="comment.usersAvatar"
        />
        <el-avatar v-else :icon="UserFilled" />
        <span>{{ userNames[comment.usersId]}}</span>
        <span class="time">{{ formatTime(comment.createTime) }}</span>
      </div>

      <div class="comment-content">
        {{ comment.content }}
      </div>

      <!-- 显示评论图片 -->
      <el-row v-if="comment.picture && comment.picture.length" :gutter="10">
        <el-col
            v-for="(img, imgIndex) in comment.picture"
            :key="imgIndex"
            :span="2"
        >
          <el-image
              :src="`${serverUrl.url}/comments/images/${img}`"
              :preview-src-list="comment.picture.map(i => `${serverUrl.url}/comments/images/${i}`)"
              fit="cover"
          />
        </el-col>
      </el-row>
      <el-row v-if="comment.replyId === comment.commentsId">

      </el-row>
      <el-row style="justify-content: right">
        <el-col :span="2"><span class="Reply-button" @click="handleReplay">回复</span></el-col>
      </el-row>
      <release-comment v-if="isReply" :replyId="comment.commentsId" />
    </el-card>
  </div>
</template>

<style scoped>
.comment-container {
  margin-top: 20px;
}

.comment-item {
  margin-bottom: 15px;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.comment-header .username {
  margin-left: 10px;
  font-weight: bold;
}

.comment-header .time {
  margin-left: auto;
  color: #999;
  font-size: 0.9em;
}

.comment-content {
  margin: 10px 0;
  line-height: 1.6;
}

.el-image {
  border-radius: 4px;
  margin-top: 10px;
}
.Reply-button{
  cursor: pointer;
}
.Reply-button:hover{
  color: #409eff;
}
</style>