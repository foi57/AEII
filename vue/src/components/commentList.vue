<script setup>
import {useRoute} from "vue-router";
import {ref, onMounted, onBeforeUnmount, watch} from "vue"; // 添加watch
import comments from "../../api/comments.js";
import { UserFilled } from '@element-plus/icons-vue'
import serverUrl from "../../serverUrl.js"
import { Store } from '../store/index.js'
import user from "../../api/user.js";
import {ElMessage} from "element-plus";
import ReleaseComment from "./releaseComment.vue";

const props = defineProps({
  refreshTrigger: {
    type: Number,
    default: 0
  }
});

watch(() => props.refreshTrigger, () => {
  // 当refreshTrigger变化时，重置页码并刷新评论列表
  currentPage.value = 1;
  getcomments();
});

const userStore = Store()

const formatTime = (timeStr) => {
  return new Date(timeStr).toLocaleString()
}

// 根据用户ID获取用户名（需要根据实际数据结构调整）
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

// 添加分页相关的响应式变量
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 修改获取评论的方法
const getcomments = async (isLoadMore = false) => {
  if (!articleId.value || loading.value) {
    return
  }

  loading.value = true
  try {
    const formData = new FormData()
    formData.append('articleId', articleId.value)
    formData.append('pageNum', currentPage.value)
    formData.append('pageSize', pageSize.value)
    formData.append('usersId', userStore.usersId)
    
    const res = await comments.selectCommentList(formData)
    console.log(res.data)
    total.value = res.data.total
    
    if (isLoadMore) {
      // 加载更多时，将新数据追加到现有列表
      commentList.value = [...commentList.value, ...res.data.records]
    } else {
      // 首次加载或刷新时，直接替换数据
      commentList.value = res.data.records
    }
    await fetchUserNames()
  } catch (err) {
    console.error(err)
    ElMessage.error('获取失败')
  } finally {
    loading.value = false
  }
}

// 添加滚动监听函数
// 修改滚动监听函数，添加一个标志位来控制是否应该响应滚动事件
const isUserTyping = ref(false)

// 添加一个变量来保存页面滚动位置
const scrollPosition = ref(0)

// 修改滚动监听函数
const handleScroll = async () => {
  
  // 保存当前滚动位置
  scrollPosition.value = window.scrollY
  
  // 如果用户正在输入，不触发加载更多
  if (isUserTyping.value) return
  
  const scrollHeight = document.documentElement.scrollHeight
  const scrollTop = document.documentElement.scrollTop
  const clientHeight = document.documentElement.clientHeight
  
  if (scrollHeight - scrollTop - clientHeight < 100 && 
      !loading.value && 
      commentList.value.length < total.value) {
    currentPage.value++
    await getcomments(true)
  }
}

const handleUserTyping = (typing) => {
  isUserTyping.value = typing
  
  if (typing) {
    // 用户开始输入时，记录当前滚动位置
    scrollPosition.value = window.scrollY
  }
}

// 添加一个监听器来监视输入框内容变化
watch(() => isUserTyping.value, (newVal) => {
  if (newVal) {
    // 当用户开始输入时，添加一个MutationObserver来监视DOM变化
    const observer = new MutationObserver(() => {
      // 当DOM变化时，将页面滚动回之前的位置
      window.scrollTo(0, scrollPosition.value)
    })
    
    // 监视整个文档的变化
    observer.observe(document.body, { 
      childList: true, 
      subtree: true,
      attributes: true,
      characterData: true 
    })
    
    // 将observer保存到ref中，以便后续清除
    window.currentObserver = observer
  } else {
    // 用户停止输入时，移除MutationObserver
    if (window.currentObserver) {
      window.currentObserver.disconnect()
      window.currentObserver = null
    }
  }
})

// 在组件挂载时添加滚动监听
onMounted(() => {
  getcomments()
  window.addEventListener('scroll', handleScroll)
})

// 在组件卸载前移除滚动监听
onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})
const replyId = ref(null)
const replyName = ref('')
const toUserId = ref(null)
const isReply=ref(false)

const isSubComment= ref(false)

const handleReplay = (id,name,UserId)=>{
  
  replyId.value = id
  replyName.value = name
  toUserId.value = UserId
  isReply.value = true
  isUserTyping.value = true // 设置标志位，表示用户将要输入
  console.log('回复', id, name, isReply.value, 'toUserId', toUserId.value)
  
  scrollPosition.value = window.scrollY
  handleUserTyping(true)
  
  // 300ms后重置标志位，避免长时间禁用滚动加载
  setTimeout(() => {
    isUserTyping.value = false
  }, 300)
}

// 在handleCommentSuccess中也需要重置标志位
const handleCommentSuccess = (newComment) => {
  // 关闭评论输入框
  console.log('关闭评论输入框')
  isReply.value = false
  handleUserTyping(false)
  
  // 不再刷新整个列表，而是将新评论添加到列表中
  if (newComment) {
    if (newComment.replyId) {
      // 如果是回复评论，找到对应的主评论并添加到其子评论中
      const parentComment = commentList.value.find(c => c.commentsId === newComment.replyId)
      if (parentComment) {
        // 确保子评论数组存在
        if (!parentComment.subComments) {
          parentComment.subComments = []
        }
        // 添加到子评论列表的开头（最新的显示在前面）
        parentComment.subComments.unshift(newComment)
      }
    } else {
      // 如果是主评论，添加到主评论列表的开头
      commentList.value.unshift(newComment)
    }
    
    // 更新用户名缓存
    if (newComment.usersId && !userNames.value[newComment.usersId]) {
      user.selectById(newComment.usersId).then(res => {
        userNames.value[newComment.usersId] = res.data.usersName
      })
    }
  }
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
    formData.append("usersId", userStore.usersId)
    let res
    if (isThumbsUp) {
      res = await comments.cancelThumbsUp(formData)
    } else {
      res = await comments.thumbsUp(formData)
    }
    
    if (res.data) {
      // 不需要立即刷新整个列表，而是直接更新本地状态
      const updateComment = (comment) => {
        if (comment.commentsId === commentId) {
          comment.isThumbsUp = !isThumbsUp
          comment.thumb = isThumbsUp ? comment.thumb - 1 : comment.thumb + 1
          return true
        }
        return false
      }

      // 更新主评论
      const mainComment = commentList.value.find(comment => updateComment(comment))
      if (!mainComment) {
        // 如果主评论中没找到，查找子评论
        commentList.value.forEach(comment => {
          if (comment.subComments) {
            comment.subComments.forEach(subComment => updateComment(subComment))
          }
        })
      }
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('点赞操作失败')
  }
}

const isUserThumbsUp = (comment) => {
  return comment.isThumbsUp
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
      @click="handleThumbsUp(comment.commentsId, comment.isThumbsUp)"
    >
      <el-icon :color="comment.isThumbsUp ? '#409EFF' : ''">
        <svg v-if="!comment.isThumbsUp" t="1743331261866" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1654" width="200" height="200"
        
        ><path d="M190.193225 471.411583c14.446014 0 26.139334-11.718903 26.139334-26.13831 0-14.44499-11.69332-26.164916-26.139334-26.164916-0.271176 0-0.490164 0.149403-0.73678 0.149403l-62.496379 0.146333c-1.425466-0.195451-2.90005-0.295735-4.373611-0.295735-19.677155 0-35.621289 16.141632-35.621289 36.114522L86.622358 888.550075c0 19.949354 15.96767 35.597753 35.670407 35.597753 1.916653 0 3.808746 0.292666 5.649674 0l61.022819 0.022513c0.099261 0 0.148379 0.048095 0.24764 0.048095 0.097214 0 0.146333-0.048095 0.24457-0.048095l0.73678 0 0-0.148379c13.413498-0.540306 24.174586-11.422144 24.174586-24.960485 0-13.55983-10.760065-24.441669-24.174586-24.981974l0-0.393973-50.949392 0 1.450025-402.275993L190.193225 471.409536z" fill="#5D5D5D" p-id="1655"></path><path d="M926.52241 433.948343c-19.283182-31.445176-47.339168-44.172035-81.289398-45.546336-1.77032-0.246617-3.536546-0.39295-5.380544-0.39295l-205.447139-0.688685c13.462616-39.059598 22.698978-85.58933 22.698978-129.317251 0-28.349675-3.193739-55.962569-9.041934-82.542948l-0.490164 0.049119c-10.638291-46.578852-51.736315-81.31498-100.966553-81.31498-57.264215 0-95.466282 48.15065-95.466282 106.126063 0 3.241834-0.294712 6.387477 0 9.532097-2.996241 108.386546-91.240027 195.548698-196.23636 207.513194l0 54.881958-0.785899 222.227314 0 229.744521 10.709923 0 500.025271 0.222057 8.746198-0.243547c19.35686 0.049119 30.239721-4.817726 47.803749-16.116049 16.682961-10.761088 29.236881-25.50079 37.490869-42.156122 2.260483-3.341095 4.028757-7.075139 5.106298-11.20111l77.018118-344.324116c1.056052-4.053316 1.348718-8.181333 1.056052-12.160971C943.643346 476.446249 938.781618 453.944769 926.52241 433.948343zM893.82573 486.837924l-82.983993 367.783411-0.099261-0.049119c-2.555196 6.141884-6.879688 11.596106-12.872169 15.427364-4.177136 2.727111-8.773827 4.351098-13.414521 4.964058-1.49812-0.195451-3.046383 0-4.620227 0l-477.028511-0.540306-0.171915-407.408897c89.323375-40.266076 154.841577-79.670527 188.596356-173.661202 0.072655 0.024559 0.124843 0.049119 0.195451 0.072655 2.99931-9.137101 6.313799-20.73423 8.697079-33.164331 5.551436-29.185716 5.258771-58.123792 5.258771-58.123792-4.937452-37.98001 25.940812-52.965306 44.364417-52.965306 25.304316 0.860601 50.263777 33.656541 50.263777 52.326762 0 0 5.600555 27.563776 5.649674 57.190537 0.048095 37.366026-4.6673 56.847729-4.6673 56.847729l-0.466628 0c-5.872754 30.879288-16.214287 60.138682-30.464849 86.964654l0.36839 0.342808c-2.358721 4.815679-3.709485 10.220782-3.709485 15.943111 0 19.922748 19.088754 21.742187 38.765909 21.742187l238.761895 0.270153c0 0 14.666024 0.465604 14.690584 0.465604l0 0.100284c12.132318-0.638543 24.221658 5.207605 31.100322 16.409738 5.504364 9.016351 6.437619 19.6045 3.486404 28.988218L893.82573 486.837924z" fill="#5D5D5D" p-id="1656"></path><path d="M264.827039 924.31872c0.319272 0.024559 0.441045 0.024559 0.295735-0.024559 0.243547-0.048095 0.367367-0.074701-0.295735-0.074701s-0.539282 0.026606-0.271176 0.074701C264.43409 924.343279 264.532327 924.343279 264.827039 924.31872z" fill="#5D5D5D" p-id="1657"></path></svg>
        <svg v-else t="1743415729664" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1511" width="200" height="200"
        ><path d="M249 460.2h-71.4c-20.2 0-36.6 16.4-36.6 36.6v372.6c0 20.2 16.4 36.6 36.6 36.6H249c20.2 0 36.6-16.4 36.6-36.6V496.8c-0.1-20.2-16.4-36.6-36.6-36.6zM868.9 462.4c-11.6-15.1-29.2-23.7-48.2-23.7H641.9c-6.9 0-10.4-4.6-11.6-6.5-1.2-2-3.7-7.4-0.2-13.8 13.6-25.1 22-50.1 25.1-74.3 4.5-35.5 8.6-123.2-8.6-170.3-5.4-15-18-50.1-66.8-53.3-1-0.1-2 0-3 0-56.1 4.2-66 44.4-69.2 57.6-2 8.2-4.1 17.7-6.4 28-8.3 37.9-19.7 89.7-39.7 124.6-38.9 67.9-132.5 132.7-133.1 133.1-0.6 0.4-0.9 1-1.5 1.4-7.7 2.7-13.3 9.8-13.3 18.4v400.9c0 6.1 3 11.4 7.4 15 3.9 4 9.3 6.5 15.4 6.5h393.1c25.8 0 48.3-17.4 54.8-42.4L879.6 515c4.8-18.4 0.9-37.5-10.7-52.6z" fill="#3259CE" p-id="1512"></path></svg>
      </el-icon>
      <span>{{ comment.thumb || 0 }}</span>
    </span>
  </div>

      <el-row style="justify-content: right">
        <el-col :span="2"><span class="Reply-button" @click="()=>{handleReplay(comment.commentsId,userNames[comment.usersId],comment.usersId);
          isSubComment=false
        }">回复</span></el-col>
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
      @click="() => {handleThumbsUp(subComment.commentsId, isUserThumbsUp(subComment))
      }"
        
    >
      <el-icon :color="subComment.isThumbsUp ? '#409EFF' : ''">
        <svg v-if="!subComment.isThumbsUp" t="1743331261866" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1654" width="200" height="200" ><path d="M190.193225 471.411583c14.446014 0 26.139334-11.718903 26.139334-26.13831 0-14.44499-11.69332-26.164916-26.139334-26.164916-0.271176 0-0.490164 0.149403-0.73678 0.149403l-62.496379 0.146333c-1.425466-0.195451-2.90005-0.295735-4.373611-0.295735-19.677155 0-35.621289 16.141632-35.621289 36.114522L86.622358 888.550075c0 19.949354 15.96767 35.597753 35.670407 35.597753 1.916653 0 3.808746 0.292666 5.649674 0l61.022819 0.022513c0.099261 0 0.148379 0.048095 0.24764 0.048095 0.097214 0 0.146333-0.048095 0.24457-0.048095l0.73678 0 0-0.148379c13.413498-0.540306 24.174586-11.422144 24.174586-24.960485 0-13.55983-10.760065-24.441669-24.174586-24.981974l0-0.393973-50.949392 0 1.450025-402.275993L190.193225 471.409536z" fill="#5D5D5D" p-id="1655"></path><path d="M926.52241 433.948343c-19.283182-31.445176-47.339168-44.172035-81.289398-45.546336-1.77032-0.246617-3.536546-0.39295-5.380544-0.39295l-205.447139-0.688685c13.462616-39.059598 22.698978-85.58933 22.698978-129.317251 0-28.349675-3.193739-55.962569-9.041934-82.542948l-0.490164 0.049119c-10.638291-46.578852-51.736315-81.31498-100.966553-81.31498-57.264215 0-95.466282 48.15065-95.466282 106.126063 0 3.241834-0.294712 6.387477 0 9.532097-2.996241 108.386546-91.240027 195.548698-196.23636 207.513194l0 54.881958-0.785899 222.227314 0 229.744521 10.709923 0 500.025271 0.222057 8.746198-0.243547c19.35686 0.049119 30.239721-4.817726 47.803749-16.116049 16.682961-10.761088 29.236881-25.50079 37.490869-42.156122 2.260483-3.341095 4.028757-7.075139 5.106298-11.20111l77.018118-344.324116c1.056052-4.053316 1.348718-8.181333 1.056052-12.160971C943.643346 476.446249 938.781618 453.944769 926.52241 433.948343zM893.82573 486.837924l-82.983993 367.783411-0.099261-0.049119c-2.555196 6.141884-6.879688 11.596106-12.872169 15.427364-4.177136 2.727111-8.773827 4.351098-13.414521 4.964058-1.49812-0.195451-3.046383 0-4.620227 0l-477.028511-0.540306-0.171915-407.408897c89.323375-40.266076 154.841577-79.670527 188.596356-173.661202 0.072655 0.024559 0.124843 0.049119 0.195451 0.072655 2.99931-9.137101 6.313799-20.73423 8.697079-33.164331 5.551436-29.185716 5.258771-58.123792 5.258771-58.123792-4.937452-37.98001 25.940812-52.965306 44.364417-52.965306 25.304316 0.860601 50.263777 33.656541 50.263777 52.326762 0 0 5.600555 27.563776 5.649674 57.190537 0.048095 37.366026-4.6673 56.847729-4.6673 56.847729l-0.466628 0c-5.872754 30.879288-16.214287 60.138682-30.464849 86.964654l0.36839 0.342808c-2.358721 4.815679-3.709485 10.220782-3.709485 15.943111 0 19.922748 19.088754 21.742187 38.765909 21.742187l238.761895 0.270153c0 0 14.666024 0.465604 14.690584 0.465604l0 0.100284c12.132318-0.638543 24.221658 5.207605 31.100322 16.409738 5.504364 9.016351 6.437619 19.6045 3.486404 28.988218L893.82573 486.837924z" fill="#5D5D5D" p-id="1656"></path><path d="M264.827039 924.31872c0.319272 0.024559 0.441045 0.024559 0.295735-0.024559 0.243547-0.048095 0.367367-0.074701-0.295735-0.074701s-0.539282 0.026606-0.271176 0.074701C264.43409 924.343279 264.532327 924.343279 264.827039 924.31872z" fill="#5D5D5D" p-id="1657"></path></svg>
        <svg v-else t="1743415729664" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1511" width="200" height="200"
        ><path d="M249 460.2h-71.4c-20.2 0-36.6 16.4-36.6 36.6v372.6c0 20.2 16.4 36.6 36.6 36.6H249c20.2 0 36.6-16.4 36.6-36.6V496.8c-0.1-20.2-16.4-36.6-36.6-36.6zM868.9 462.4c-11.6-15.1-29.2-23.7-48.2-23.7H641.9c-6.9 0-10.4-4.6-11.6-6.5-1.2-2-3.7-7.4-0.2-13.8 13.6-25.1 22-50.1 25.1-74.3 4.5-35.5 8.6-123.2-8.6-170.3-5.4-15-18-50.1-66.8-53.3-1-0.1-2 0-3 0-56.1 4.2-66 44.4-69.2 57.6-2 8.2-4.1 17.7-6.4 28-8.3 37.9-19.7 89.7-39.7 124.6-38.9 67.9-132.5 132.7-133.1 133.1-0.6 0.4-0.9 1-1.5 1.4-7.7 2.7-13.3 9.8-13.3 18.4v400.9c0 6.1 3 11.4 7.4 15 3.9 4 9.3 6.5 15.4 6.5h393.1c25.8 0 48.3-17.4 54.8-42.4L879.6 515c4.8-18.4 0.9-37.5-10.7-52.6z" fill="#3259CE" p-id="1512"></path></svg>
      </el-icon>
      <span>{{ subComment.thumb || 0 }}</span>
    </span>
  </div>

          <el-row style="justify-content: right">
        <el-col :span="2"><span class="Reply-button" @click="()=>{handleReplay(comment.commentsId, userNames[subComment.usersId],subComment.usersId)
          isSubComment = true;
        }">回复</span></el-col>
      </el-row>
      <!-- 在模板中添加事件监听 -->
    
        </div>
        <release-comment 
        v-if="isReply && replyId === comment.commentsId && isSubComment===true" 
        :replyId="replyId" 
        :replyName="replyName" 
        :toUserId="toUserId"
        @comment-success="handleCommentSuccess"
        @user-typing="handleUserTyping"
      />
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
      </div>
      <release-comment v-if="isReply && replyId === comment.commentsId && isSubComment===false" :replyId="replyId" :replyName="replyName"  @comment-success="handleCommentSuccess"/>
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
  text-align: left;
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
  text-align: left;
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

.thumbs-up-button {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 15px;
}

.thumbs-up-button:hover svg path {
  fill: #409EFF;
}

.thumbs-up-button span {
  margin-left: 4px;
}

.icon {
  width: 1em;
  height: 1em;
}

.loading-more {
  text-align: center;
  padding: 10px 0;
  color: #999;
}

.loading-more .el-icon {
  margin-right: 5px;
}
</style>