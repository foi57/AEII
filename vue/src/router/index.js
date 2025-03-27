import { createRouter, createWebHistory } from 'vue-router'
import administratorLogin from '../views/administratorLogin.vue'
import { Store } from '../store/index.js'
import {ElMessage} from "element-plus";

const routes = [
  {
    path: '/adminLogin',
    name: 'adminLogin',
    component:administratorLogin
  },
  {
    path: '/adminIndex',
    name: 'adminIndex',
    component: () => import('../views/adminIndex.vue')
  },
  {
    path: '/article/detail/:id',
    name: 'ArticleDetail',
    component: () => import('../views/ArticleDetail.vue'),
    props: true
  },
  {
    path: '/article/edit/:id',
    name: 'ArticleEdit',
    component: () => import('../components/articlePublish.vue'),
    props: true
  },
  {
    path: '/major/university/:id/:name',
    name: 'universityList',
    component: () => import('../components/university/universityList.vue'),
    props: true
  },
  {
    path: '/userIndex'
    ,name: 'userIndex',
    component: () => import('../views/userIndex.vue')
  },
  {
    path: '/articles/:category',
    name: 'Articles',
    component: () => import('../views/Articles.vue'),
    props: true
  },
  {
    path: '/university'
   ,name: 'university',
    component: () => import('../views/university.vue')
  },
  {
    path: '/university/detail/:id',
    name: 'universityDetail',
    component: () => import('../views/universityDetail.vue'),
    props: true
  },
  {
    path: '/major/detail/:name',
    component: () => import('../views/majorDetail.vue'),
    props: true
  },
  {
    path: '/major',
    component: () => import('../views/major.vue')
  },
  {
    path: '/university/articles/:universityName/:category',
    name: 'UniversityArticles',
    component: () => import('../views/articles.vue'),
    props: true
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/register.vue')
  },
  {
    path: '/user',
    name: 'user',
    component: () => import('../views/user.vue')
  },
  {
    path: '/collection',
    name: 'collection',
    component: () => import('../components/collection.vue')
  },
  {
    path: '/notification',
    name: 'notification',
    component: () => import('../views/Notification.vue')
  }
];

const router= createRouter({
  history:createWebHistory("/"),
  routes,
});

// 添加路由守卫
router.beforeEach((to, from, next) => {
  const userStore = Store()
  const isAuthenticated = localStorage.getItem('accessToken') && userStore.usersName
  const userRole = userStore.usersRole // 从 store 获取用户角色

  // 需要管理员权限的路由
  const adminRoutes = ['/adminIndex', '/adminLogin'] // 添加其他需要控制的管理路由

  if (adminRoutes.includes(to.path)) {
    if (!isAuthenticated) {
      return next('/adminLogin')
    }
    // 新增角色校验
    if (!['admin', 'seniorAdmin'].includes(userRole)) {
      ElMessage.error('权限不足')
      return next('/userIndex') // 保持当前页面
    }
  }

  // 原有登录状态校验保持不变
  if (to.path === '/adminLogin' && isAuthenticated) {
    next('/adminIndex')
  } else {
    next()
  }
})

export default router;