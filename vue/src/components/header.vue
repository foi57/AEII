<script setup>
import {Store} from "../store/index.js";
import serverUrl from "../../serverUrl.js";
import {Notification, UserFilled} from "@element-plus/icons-vue";
import notificationUser from "../../api/notificationUser.js";
import {onMounted, ref,reactive} from "vue";
import {useRouter} from 'vue-router'; // 引入 useRouter
import comments from "../../api/comments.js";
import commentsNotification from "../../api/commentsNotification.js";
import commentNotificationCategory from "../assets/commentNotificationCategory.js";
import token from "../assets/token.js";
import article from '../../api/article'; // 引入 article API

const userStore = Store();
const userAvatar = userStore.usersAvatar;
const userName = userStore.usersName;
const router = useRouter(); // 获取 router 实例

const handleCommand = (command) => {
  if (command === 'c') {
    localStorage.removeItem(token.token)
    localStorage.removeItem(token.refreshToken)
    userStore.$reset()
    window.location.href = '/login'
  }
  else if (command === 'd') {
    window.location.href = '/login'
  }
  else if (command === 'a') {
    window.location.href = '/user/1'
  }else if (command === 'b') {
    window.location.href = '/user/2'
  }
}
const unreadCounts = ref(0)
const notificationCount = ref(0)
const replyMeCount = ref(0)
const atMeCount = ref(0)

// 获取未读通知数量
const unreadCount = () => {
  if (!userStore.usersId) return;
  
  const userId = userStore.usersId
  // 获取系统通知未读数量
  notificationUser.unread(userId).then(res => {
    notificationCount.value = res.data
    updateTotalCount()
  })
  
  // 获取回复我的未读数量
  const formData = new FormData()
  formData.append('usersId', userId)
  formData.append('category', commentNotificationCategory.replyMe)
  commentsNotification.countUnRead(formData).then(res => {
    replyMeCount.value = res.data
    updateTotalCount()
  }).catch(err => {
    console.log(err)
  })
  
  // 获取@我的未读数量
  const atMeFormData = new FormData()
  atMeFormData.append('usersId', userId)
  atMeFormData.append('category', commentNotificationCategory.toMe)
  commentsNotification.countUnRead(atMeFormData).then(res => {
    atMeCount.value = res.data
    updateTotalCount()
  }).catch(err => {
    console.log(err)
  })
}

// 更新总未读数
const updateTotalCount = () => {
  unreadCounts.value = notificationCount.value + replyMeCount.value + atMeCount.value
}
onMounted(unreadCount)

// 导出刷新方法，供其他组件调用
defineExpose({
  refreshHeardUnreadCount: unreadCount
})


const searchForm = reactive({
  articleTitle: '', // 搜索关键词
  // 可以保留其他搜索条件，但 header 中可能只用到标题
  // articleType: '',
  pageNum: 1,
  pageSize: 5, // 建议数量可以少一些
});

// 防抖函数 (如果全局没有，可以在这里定义或引入)
function debounce(fn, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
}

let searchResults = ref([]);
// 搜索建议函数 (与 userIndex.vue 类似)
const querySearchAsync = debounce(async (queryString, cb) => {
  if (queryString) {
    searchForm.articleTitle = queryString; // 更新搜索表单中的标题
    try {
      // 调用文章列表接口获取建议
      const res = await article.selectArticleList(searchForm);
      searchResults.value = res.data.records.map(item => {
        // 返回符合 el-autocomplete 格式的对象
        // 确保包含 articleId 用于跳转
        return { value: item.articleTitle, articleId: item.articleId, ...item };
      });
      cb(searchResults.value);
    } catch (error) {
      console.error('获取搜索建议失败:', error);
      cb([]);
    }
  } else {
    cb([]);
  }
}, 300); // 300ms 防抖

// 处理选中建议项 (与 userIndex.vue 类似)
const handleSelect = (item) => {
  if (item && item.articleId) {
    router.push(`/article/detail/${item.articleId}`); // 跳转到文章详情页
    searchForm.articleTitle = ''; // 清空搜索框 (可选)
  }
};

// 执行完整搜索 (跳转到搜索结果页, 与 userIndex.vue 类似)
const performSearch = () => {
  if (searchForm.articleTitle.trim()) {
    router.push({
      path: '/articleSearch',
      query: { keyword: searchForm.articleTitle.trim() } // 传递关键词
    });
    searchForm.articleTitle = ''; // 清空搜索框 (可选)
  }
};

const goToAdvancedSearch = () => {
  router.push('/articleSearch?advanced=true'); 
  // 或者直接跳转到高级搜索组件所在的路由（如果它是一个独立页面）
  // router.push('/advanced-search');
}
</script>

<template>
  <el-row class="header" align="middle">
  
    <el-col :span="10">
      <ul class="menu">
        <li><el-link href="/userIndex">首页</el-link></li>
        <li><el-link href="/university">学校查询</el-link></li>
        <li><el-link href="/major">艺术类专业查询</el-link></li>
      </ul>
    </el-col>
    <el-col :span="8" class="search-area">
      <el-autocomplete
        v-model="searchForm.articleTitle"
        :fetch-suggestions="querySearchAsync"
        placeholder="搜索文章"
        @select="handleSelect"
        @keyup.enter="performSearch"
        clearable
        class="header-search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
        <template #suffix>
          <el-button @click="performSearch" :loading="isLoading">搜索</el-button>
        </template>
      </el-autocomplete>
      <el-button @click="goToAdvancedSearch">高级搜索</el-button>
    </el-col>


    <el-col :span="3" class="right-align">
      <el-dropdown @command="handleCommand">
        <el-avatar :src="`${serverUrl.url}/users/avatar/${userAvatar}`" v-if="userAvatar !== null" />
        <el-avatar v-if="userAvatar === null" :icon="UserFilled" />
        <template #dropdown>
          <el-dropdown-menu v-if="userName!==null">
            <el-dropdown-item command="a">个人中心</el-dropdown-item>
            <el-dropdown-item command="b">收藏</el-dropdown-item>
            <el-dropdown-item command="c">退出登录</el-dropdown-item>
          </el-dropdown-menu>
          <el-dropdown-menu v-else>
            <el-dropdown-item command="d">登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-col>

    <el-col :span="3" class="right-align"> 
   
      <el-badge :value="unreadCounts" :max="99" :hidden="unreadCounts === 0" class="notification-badge">
        <el-dropdown>
          <div class="notification-icon">
            <el-icon size="24"><Notification /></el-icon>
            <span class="notification-text">消息</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <!-- 通知未读徽章 -->
              <el-dropdown-item>
                <el-badge :value="notificationCount" :max="99" :hidden="notificationCount === 0" class="item-badge">
                  <el-link href="/notification/1">通知</el-link>
                </el-badge>
              </el-dropdown-item>

              <!-- 回复我的未读徽章 -->
              <el-dropdown-item>
                <el-badge :value="replyMeCount" :max="99" :hidden="replyMeCount === 0" class="item-badge">
                  <el-link href="/notification/2">回复我的</el-link>
                </el-badge>
              </el-dropdown-item>

              <!-- @我的未读徽章 -->
              <el-dropdown-item>
                <el-badge :value="atMeCount" :max="99" :hidden="atMeCount === 0" class="item-badge">
                  <el-link href="/notification/3">@我的</el-link>
                </el-badge>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-badge>
    </el-col>
  </el-row>
</template>

<style scoped>
.menu {
  list-style: none;
  margin: 0;
  display: flex;
  justify-content: center;
}

.header {
  display: flex;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
  margin-bottom: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: #f5f7fa; /* 调整背景色 */
  padding: 0 20px; /* 添加左右内边距 */
  height: 60px; /* 固定高度以便对齐 */
}
.menu li {
  margin: 0 15px;
  display: flex;
  align-items: center;
}
.menu li .el-link { /* 确保链接颜色在背景下可见 */
  color: #303133; /* 或其他合适的颜色 */
  font-weight: 500;
}
.menu li .el-link:hover {
  color: #409EFF; /* 悬停颜色 */
}

/* 新增搜索区域样式 */
.search-area {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}
.header-search-input {
  width: 100%; /* 占据分配的宽度 */
  max-width: 400px; /* 可以设置最大宽度 */
}

/* 调整头像和通知列的对齐 */
.right-align {
  display: flex;
  justify-content: flex-end; /* 内容右对齐 */
  align-items: center; /* 垂直居中 */
}

.avatar svg{
  height: 40px;
  width: 40px;
}

.notification-icon {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.notification-text {
  font-size: 12px;
  margin-top: 2px;
}

.item-badge {
  width: 100%;
}
</style>