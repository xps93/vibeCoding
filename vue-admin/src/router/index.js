import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

// 公开路由（无需登录）
export const constantRoutes = [{
    path: '/login', component: () => import('../views/login/index.vue'), hidden: true
}]

// 动态路由映射（后端菜单对应前端组件）
export const dynamicRouteMap = {
    'dashboard/index': () => import('../views/dashboard/index.vue'),
    'system/user/index': () => import('../views/system/user/index.vue'),
    'system/role/index': () => import('../views/system/role/index.vue'),
    'system/menu/index': () => import('../views/system/menu/index.vue'),
    'system/dept/index': () => import('../views/system/dept/index.vue'),
    'system/post/index': () => import('../views/system/post/index.vue'),
    'system/dict/index': () => import('../views/system/dict/index.vue'),
    'system/config/index': () => import('../views/system/config/index.vue'),
    'system/notice/index': () => import('../views/system/notice/index.vue'),
    'monitor/operlog/index': () => import('../views/monitor/operlog/index.vue'),
    'monitor/loginlog/index': () => import('../views/monitor/loginlog/index.vue'),
    'monitor/online/index': () => import('../views/monitor/online/index.vue'),
    'monitor/job/index': () => import('../views/monitor/job/index.vue'),
    'monitor/server/index': () => import('../views/monitor/server/index.vue'),
    'monitor/cache/index': () => import('../views/monitor/cache/index.vue'),
    'monitor/druid/index': () => import('../views/monitor/druid/index.vue'),
    'tool/gen/index': () => import('../views/tool/gen/index.vue'),
    'tool/swagger/index': () => import('../views/tool/swagger/index.vue'),
    'tool/build/index': () => import('../views/tool/build/index.vue')
}

const router = new Router({
    mode: 'hash', base: '/', scrollBehavior: () => ({y: 0}), routes: constantRoutes
})

export default router
