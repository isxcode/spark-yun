/*
 * @Author: fanciNate
 * @Date: 2023-04-17 09:43:00
 * @LastEditTime: 2023-04-27 17:12:59
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/router/index.ts
 */
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import Home from '../views/home/home'
import Login from '../views/login/login'
import Ssoauth from '../views/login/ssoauth'
import ShareForm from '../views/share-form/index.vue'
import ShareReport from '../views/report-views/share-report/index.vue'

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
    component: Ssoauth
  },
  {
    path: '/auth',
    name: 'login',
    component: Login
  },
  {
    path: '/home',
    name: 'home',
    component: Home,
    children: HomeChildren
  },
  {
    path: '/share/:shareParam',
    name: 'share',
    component: ShareForm
  },
  {
    path: '/dashboard/:shareParam',
    name: 'share-report',
    component: ShareReport
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

export default router
