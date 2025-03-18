import { createRouter, createWebHistory } from 'vue-router'
import administratorLogin from '../views/administratorLogin.vue'
import { Store } from '../store/index.js'

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
    component: () => import('../components/university/universityList.vue')
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

  // 修复2：增加已登录时访问登录页的重定向
  if (to.path === '/adminLogin' && isAuthenticated) {
    next('/adminIndex')
  } else if (to.path === '/adminIndex' && !isAuthenticated) {
    next('/adminLogin')
  } else {
    next()
  }
})

export default router;