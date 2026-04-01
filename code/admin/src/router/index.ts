import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory('/'),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/auth/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/auth/RegisterView.vue')
    },
    {
      path: '/',
      component: () => import('../layout/AdminLayout.vue'),
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('../views/dashboard/DashboardView.vue')
        },
        {
          path: 'projects',
          name: 'project-list',
          component: () => import('../views/project/ProjectList.vue')
        },
        {
          path: 'projects/:id',
          name: 'project-detail',
          component: () => import('../views/project/ProjectDetail.vue'),
          meta: { hidden: true }
        },
        {
          path: 'categories',
          name: 'category-list',
          component: () => import('../views/project/CategoryList.vue')
        },
        {
          path: 'services',
          name: 'consultation-list',
          component: () => import('../views/consultation/ConsultationList.vue')
        },
        {
          path: 'users',
          name: 'user-list',
          component: () => import('../views/user/UserList.vue')
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('../views/settings/SettingsView.vue')
        }
      ]
    }
  ]
})

import { useAuthStore } from '@/stores/auth'

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  // 允许通过登录和注册页面，以及无需登录的路径
  const whiteList = ['/login', '/register']

  if (!whiteList.includes(to.path) && !authStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
