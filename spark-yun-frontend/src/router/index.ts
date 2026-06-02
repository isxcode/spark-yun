/*
 * @Author: fanciNate
 * @Date: 2023-04-17 09:43:00
 * @LastEditTime: 2023-04-27 17:12:59
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/router/index.ts
 */
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVipLicenseEnabled, isVipMenuCode } from '@/utils/vip-license'

import HomeChildren from './home-children'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: {
      name: 'home'
    }
  },
  {
    path: '/ssoauth',
    name: 'ssoauth',
    component: () => import('../views/login/ssoauth')
  },
  {
    path: '/auth',
    name: 'login',
    component: () => import('../views/login/login')
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('../views/home/home'),
    children: HomeChildren
  },
  {
    path: '/share/:shareParam',
    name: 'share',
    component: () => import('../views/share-form/index.vue')
  },
  {
    path: '/dashboard/:shareParam',
    name: 'share-report',
    component: () => import('../views/report-views/share-report/index.vue')
  }
  // {
  //   path: '/about',
  //   name: 'About',
  //   // route level code-splitting
  //   // this generates a separate chunk (about.[hash].js) for this route
  //   // which is lazy-loaded when the route is visited.
  //   component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  // }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_VUE_APP_PUBLIC_PATH),
  routes
})

router.beforeEach(async (to) => {
  const routeName = typeof to.name === 'string' ? to.name : ''
  const openRouteName = new Set(['login', 'ssoauth', 'share', 'share-report'])

  if (!routeName || openRouteName.has(routeName) || !isVipMenuCode(routeName)) {
    return true
  }

  const vipEnabled = await getVipLicenseEnabled()
  if (vipEnabled) {
    return true
  }

  ElMessage.error('许可证未启用，无法访问商业版菜单')
  return {
    name: 'index'
  }
})

export default router
