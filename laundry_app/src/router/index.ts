import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Login',
    component: () => import('@/pages/LoginPage.vue')
  }
  // Здесь будут добавляться новые страницы
  // {
  //   path: '/dashboard',
  //   name: 'Dashboard',
  //   component: () => import('@/pages/DashboardPage.vue')
  // }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
