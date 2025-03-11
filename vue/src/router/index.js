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