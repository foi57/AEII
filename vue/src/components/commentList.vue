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

const route = useRoute()
const articleId = ref(route.params.id)  // 将articleId保存为响应式数据

const getcomments = async () => {
  if (!articleId.value) {
    console.error('缺少文章ID参数');
    return;
  }

  const formData = new FormData();
  formData.append('articleId', articleId.value)
  formData.append('pageNum', 1)
  formData.append('pageSize', 20)
  comments.selectCommentList(formData).then(res => {
    console.log('评论', res)
    commentList.value = res.data.records
    fetchUserNames()
  }).catch(err => {
    console.log(err)
    ElMessage.error('获取失败')
  })
}

onMounted(()=>{
  getcomments()
})

const replyId = ref(null)
const replyName = ref('')
const toUserId = ref(null)
const isReply=ref(false)
const handleReplay = (id,name,UserId)=>{
  replyId.value = id
  replyName.value = name
  toUserId.value = UserId
  isReply.value = !isReply.value
}

const handleCommentSuccess = () => {
  // 关闭评论输入框
  isReply.value = false
  // 刷新评论列表
  getcomments()
}
const subCommentPageSize = 5  // 子评论每页显示数量
const subCommentPages = ref({})  // 存储每个主评论的子评论当前页码

const handleDelete = async (commentId, userId) => {
  // 检查是否是自己的评论
  if (userId !== userStore.usersId) {
    ElMessage.warning('只能删除自己发布的评论')
    return
  }

  try {
    const formData = new FormData()
    formData.append('commentId', commentId)
    const res = await comments.deleteComment(formData)
    if (res.data) {
      ElMessage.success('删除成功')
      getcomments() // 刷新评论列表
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

// 添加点赞功能
const handleThumbsUp = async (commentId, isThumbsUp) => {
  try {
    const formData = new FormData()
    formData.append('commentId', commentId)
    
    let res
    if (isThumbsUp) {
      // 已点赞，取消点赞
      res = await comments.cancelThumbsUp(formData)
    } else {
      // 未点赞，添加点赞
      res = await comments.thumbsUp(formData)
    }
    
    if (res.data) {
      getcomments() // 刷新评论列表
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('点赞操作失败')
  }
}

// 判断是否已点赞（这里可以根据实际情况调整逻辑）
const isUserThumbsUp = (comment) => {
  // 这里需要根据后端返回的数据结构判断当前用户是否已点赞
  // 如果后端没有提供此信息，可能需要额外的接口或在评论中添加点赞用户列表
  return false
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
        <!-- 添加删除按钮 -->
        <el-button
            v-if="comment.usersId === userStore.usersId"
            type="danger"
            link
            @click="handleDelete(comment.commentsId, comment.usersId)"
        >
          删除
        </el-button>
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

      <div class="sub-comment-actions">
    <span 
      class="thumbs-up-button" 
      @click="handleThumbsUp(subComment.commentsId, isUserThumbsUp(subComment))"
    >
      <el-icon :color="isUserThumbsUp(subComment) ? '#409EFF' : ''">
        <svg t="1743331261866" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1654" width="200" height="200"><path d="M190.193225 471.411583c14.446014 0 26.139334-11.718903 26.139334-26.13831 0-14.44499-11.69332-26.164916-26.139334-26.164916-0.271176 0-0.490164 0.149403-0.73678 0.149403l-62.496379 0.146333c-1.425466-0.195451-2.90005-0.295735-4.373611-0.295735-19.677155 0-35.621289 16.141632-35.621289 36.114522L86.622358 888.550075c0 19.949354 15.96767 35.597753 35.670407 35.597753 1.916653 0 3.808746 0.292666 5.649674 0l61.022819 0.022513c0.099261 0 0.148379 0.048095 0.24764 0.048095 0.097214 0 0.146333-0.048095 0.24457-0.048095l0.73678 0 0-0.148379c13.413498-0.540306 24.174586-11.422144 24.174586-24.960485 0-13.55983-10.760065-24.441669-24.174586-24.981974l0-0.393973-50.949392 0 1.450025-402.275993L190.193225 471.409536z" fill="#5D5D5D" p-id="1655"></path><path d="M926.52241 433.948343c-19.283182-31.445176-47.339168-44.172035-81.289398-45.546336-1.77032-0.246617-3.536546-0.39295-5.380544-0.39295l-205.447139-0.688685c13.462616-39.059598 22.698978-85.58933 22.698978-129.317251 0-28.349675-3.193739-55.962569-9.041934-82.542948l-0.490164 0.049119c-10.638291-46.578852-51.736315-81.31498-100.966553-81.31498-57.264215 0-95.466282 48.15065-95.466282 106.126063 0 3.241834-0.294712 6.387477 0 9.532097-2.996241 108.386546-91.240027 195.548698-196.23636 207.513194l0 54.881958-0.785899 222.227314 0 229.744521 10.709923 0 500.025271 0.222057 8.746198-0.243547c19.35686 0.049119 30.239721-4.817726 47.803749-16.116049 16.682961-10.761088 29.236881-25.50079 37.490869-42.156122 2.260483-3.341095 4.028757-7.075139 5.106298-11.20111l77.018118-344.324116c1.056052-4.053316 1.348718-8.181333 1.056052-12.160971C943.643346 476.446249 938.781618 453.944769 926.52241 433.948343zM893.82573 486.837924l-82.983993 367.783411-0.099261-0.049119c-2.555196 6.141884-6.879688 11.596106-12.872169 15.427364-4.177136 2.727111-8.773827 4.351098-13.414521 4.964058-1.49812-0.195451-3.046383 0-4.620227 0l-477.028511-0.540306-0.171915-407.408897c89.323375-40.266076 154.841577-79.670527 188.596356-173.661202 0.072655 0.024559 0.124843 0.049119 0.195451 0.072655 2.99931-9.137101 6.313799-20.73423 8.697079-33.164331 5.551436-29.185716 5.258771-58.123792 5.258771-58.123792-4.937452-37.98001 25.940812-52.965306 44.364417-52.965306 25.304316 0.860601 50.263777 33.656541 50.263777 52.326762 0 0 5.600555 27.563776 5.649674 57.190537 0.048095 37.366026-4.6673 56.847729-4.6673 56.847729l-0.466628 0c-5.872754 30.879288-16.214287 60.138682-30.464849 86.964654l0.36839 0.342808c-2.358721 4.815679-3.709485 10.220782-3.709485 15.943111 0 19.922748 19.088754 21.742187 38.765909 21.742187l238.761895 0.270153c0 0 14.666024 0.465604 14.690584 0.465604l0 0.100284c12.132318-0.638543 24.221658 5.207605 31.100322 16.409738 5.504364 9.016351 6.437619 19.6045 3.486404 28.988218L893.82573 486.837924z" fill="#5D5D5D" p-id="1656"></path><path d="M264.827039 924.31872c0.319272 0.024559 0.441045 0.024559 0.295735-0.024559 0.243547-0.048095 0.367367-0.074701-0.295735-0.074701s-0.539282 0.026606-0.271176 0.074701C264.43409 924.343279 264.532327 924.343279 264.827039 924.31872z" fill="#5D5D5D" p-id="1657"></path></svg>
      </el-icon>
      <span>{{ comment.thumb || 0 }}</span>
    </span>
  </div>

      <el-row style="justify-content: right">
        <el-col :span="2"><span class="Reply-button" @click="handleReplay(comment.commentsId,userNames[comment.usersId])">回复</span></el-col>
      </el-row>

      <!-- 显示子评论 -->
      <div v-if="comment.subComments && comment.subComments.length" class="sub-comments">
        <el-divider>回复</el-divider>
        <div v-for="(subComment, subIndex) in comment.subComments.slice(
          ((subCommentPages[comment.commentsId] || 1) - 1) * subCommentPageSize,
          (subCommentPages[comment.commentsId] || 1) * subCommentPageSize
        )" 
             :key="subIndex" 
             class="sub-comment-item">
          <div class="sub-comment-header">
            <el-avatar
                :src="`${serverUrl.url}/users/avatar/${subComment.usersAvatar}`"
                v-if="subComment.usersAvatar"
                :size="30"
            />
            <el-avatar v-else :icon="UserFilled" :size="30" />
            <span class="username">{{ userNames[subComment.usersId] }}</span>
            <span class="time">{{ formatTime(subComment.createTime) }}</span>
            <!-- 添加删除按钮 -->
            <el-button
                v-if="subComment.usersId === userStore.usersId"
                type="danger"
                link
                @click="handleDelete(subComment.commentsId, subComment.usersId)"
            >
              删除
            </el-button>
          </div>
          <div class="sub-comment-content">
            {{ subComment.content }}
          </div>

         

          <!-- 显示子评论的图片 -->
          <el-row v-if="subComment.picture && subComment.picture.length" :gutter="10">
            <el-col
                v-for="(img, imgIndex) in subComment.picture"
                :key="imgIndex"
                :span="2"
            >
              <el-image
                  :src="`${serverUrl.url}/comments/images/${img}`"
                  :preview-src-list="subComment.picture.map(i => `${serverUrl.url}/comments/images/${i}`)"
                  fit="cover"
              />
            </el-col>
          </el-row>

          <div class="sub-comment-actions">
    <span 
      class="thumbs-up-button" 
      @click="handleThumbsUp(subComment.commentsId, isUserThumbsUp(subComment))"
    >
      <el-icon :color="isUserThumbsUp(subComment) ? '#409EFF' : ''">
        <svg t="1743331261866" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1654" width="200" height="200"><path d="M190.193225 471.411583c14.446014 0 26.139334-11.718903 26.139334-26.13831 0-14.44499-11.69332-26.164916-26.139334-26.164916-0.271176 0-0.490164 0.149403-0.73678 0.149403l-62.496379 0.146333c-1.425466-0.195451-2.90005-0.295735-4.373611-0.295735-19.677155 0-35.621289 16.141632-35.621289 36.114522L86.622358 888.550075c0 19.949354 15.96767 35.597753 35.670407 35.597753 1.916653 0 3.808746 0.292666 5.649674 0l61.022819 0.022513c0.099261 0 0.148379 0.048095 0.24764 0.048095 0.097214 0 0.146333-0.048095 0.24457-0.048095l0.73678 0 0-0.148379c13.413498-0.540306 24.174586-11.422144 24.174586-24.960485 0-13.55983-10.760065-24.441669-24.174586-24.981974l0-0.393973-50.949392 0 1.450025-402.275993L190.193225 471.409536z" fill="#5D5D5D" p-id="1655"></path><path d="M926.52241 433.948343c-19.283182-31.445176-47.339168-44.172035-81.289398-45.546336-1.77032-0.246617-3.536546-0.39295-5.380544-0.39295l-205.447139-0.688685c13.462616-39.059598 22.698978-85.58933 22.698978-129.317251 0-28.349675-3.193739-55.962569-9.041934-82.542948l-0.490164 0.049119c-10.638291-46.578852-51.736315-81.31498-100.966553-81.31498-57.264215 0-95.466282 48.15065-95.466282 106.126063 0 3.241834-0.294712 6.387477 0 9.532097-2.996241 108.386546-91.240027 195.548698-196.23636 207.513194l0 54.881958-0.785899 222.227314 0 229.744521 10.709923 0 500.025271 0.222057 8.746198-0.243547c19.35686 0.049119 30.239721-4.817726 47.803749-16.116049 16.682961-10.761088 29.236881-25.50079 37.490869-42.156122 2.260483-3.341095 4.028757-7.075139 5.106298-11.20111l77.018118-344.324116c1.056052-4.053316 1.348718-8.181333 1.056052-12.160971C943.643346 476.446249 938.781618 453.944769 926.52241 433.948343zM893.82573 486.837924l-82.983993 367.783411-0.099261-0.049119c-2.555196 6.141884-6.879688 11.596106-12.872169 15.427364-4.177136 2.727111-8.773827 4.351098-13.414521 4.964058-1.49812-0.195451-3.046383 0-4.620227 0l-477.028511-0.540306-0.171915-407.408897c89.323375-40.266076 154.841577-79.670527 188.596356-173.661202 0.072655 0.024559 0.124843 0.049119 0.195451 0.072655 2.99931-9.137101 6.313799-20.73423 8.697079-33.164331 5.551436-29.185716 5.258771-58.123792 5.258771-58.123792-4.937452-37.98001 25.940812-52.965306 44.364417-52.965306 25.304316 0.860601 50.263777 33.656541 50.263777 52.326762 0 0 5.600555 27.563776 5.649674 57.190537 0.048095 37.366026-4.6673 56.847729-4.6673 56.847729l-0.466628 0c-5.872754 30.879288-16.214287 60.138682-30.464849 86.964654l0.36839 0.342808c-2.358721 4.815679-3.709485 10.220782-3.709485 15.943111 0 19.922748 19.088754 21.742187 38.765909 21.742187l238.761895 0.270153c0 0 14.666024 0.465604 14.690584 0.465604l0 0.100284c12.132318-0.638543 24.221658 5.207605 31.100322 16.409738 5.504364 9.016351 6.437619 19.6045 3.486404 28.988218L893.82573 486.837924z" fill="#5D5D5D" p-id="1656"></path><path d="M264.827039 924.31872c0.319272 0.024559 0.441045 0.024559 0.295735-0.024559 0.243547-0.048095 0.367367-0.074701-0.295735-0.074701s-0.539282 0.026606-0.271176 0.074701C264.43409 924.343279 264.532327 924.343279 264.827039 924.31872z" fill="#5D5D5D" p-id="1657"></path></svg>
      </el-icon>
      <span>{{ subComment.thumbs || 0 }}</span>
    </span>
  </div>

          <el-row style="justify-content: right">
        <el-col :span="2"><span class="Reply-button" @click="handleReplay(subComment.commentsId, userNames[subComment.usersId],subComment.usersId)">回复</span></el-col>
      </el-row>

        </div>

        <el-pagination
        v-if="comment.subComments.length > subCommentPageSize"
        :current-page="subCommentPages[comment.commentsId] || 1"
        :page-size="subCommentPageSize"
        :total="comment.subComments.length"
        layout="prev, pager, next"
        @current-change="(page) => subCommentPages[comment.commentsId] = page"
        small
        class="sub-comment-pagination"
    />

        <release-comment v-if="isReply" :replyId="replyId" :replyName="replyName" :to-user-id="toUserId" @comment-success="handleCommentSuccess"/>
      </div>

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

.sub-comments {
  margin-top: 15px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.sub-comment-item {
  margin-bottom: 15px;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.sub-comment-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.sub-comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.sub-comment-header .username {
  margin-left: 8px;
  font-size: 14px;
  font-weight: bold;
}

.sub-comment-header .time {
  margin-left: auto;
  color: #999;
  font-size: 12px;
}

.sub-comment-content {
  margin-left: 38px;
  font-size: 14px;
  line-height: 1.5;
}
.comment-header .el-button,
.sub-comment-header .el-button {
  margin-left: 10px;
  padding: 0;
  font-size: 12px;
}

.comment-actions,
.sub-comment-actions {
  margin-top: 8px;
  display: flex;
  align-items: center;
}

.thumbs-up-button {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 15px;
}

.thumbs-up-button:hover {
  color: #409EFF;
}

.thumbs-up-button span {
  margin-left: 4px;
}
</style>