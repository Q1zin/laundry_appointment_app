import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const APP_TITLE = 'Запись на стирку'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/pages/HomePage.vue'),
    meta: { title: `Главная — ${APP_TITLE}` }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/pages/ProfilePage.vue'),
    meta: { title: `Личный кабинет — ${APP_TITLE}` }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/pages/AdminPage.vue'),
    meta: { title: `Админ-панель — ${APP_TITLE}` }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to) {
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth'
      }
    }
    return { top: 0 }
  }
})

router.afterEach((to) => {
  const title = typeof to.meta.title === 'string' ? to.meta.title : APP_TITLE
  document.title = title
})

export default router
