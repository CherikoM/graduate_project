import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

import isLogin from '@/common/isLogin'

const toLogin = ()=> {
  if(!isLogin()) {
    return {
      path: "/login"
    }
  } else {
    return true
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/lib',
      name: 'lib',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/LibView/index.vue'),
    },
    {
      path: '/user/:id',
      name: 'user',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/UserView/index.vue'),
      beforeEnter: [toLogin]
    },
    {
      path: '/login',
      name: 'login',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/LoginView/index.vue'),
      beforeEnter: (to, from) => {
        console.log(to, from)
        if(isLogin()) {
          return {
            path: from.path,
            query: from.query,
          }
        } else {
          return true
        }
      },
    },
    {
      path: '/info/:area/:id',
      name: 'info',
      component: ()=> import('@/views/InfoView/index.vue')
    },
    {
      path: '/contribute/:area/:id?',
      name: 'contribute',
      component: ()=> import('@/views/ContributeView/index.vue'),
      beforeEnter: [toLogin]
    },
    {
      path: "/audit/:area?",
      name: "audit",
      component: ()=> import("@/views/AuditView/index.vue")
    },
    {
      path: "/history/:area/:id",
      name: "history",
      component: ()=> import("@/views/HistoryView/index.vue")
    },
    {
      path: "/collection/:id",
      name: "collection",
      component: ()=> import("@/views/CollectionView/index.vue")
    }
  ]
})

export default router
