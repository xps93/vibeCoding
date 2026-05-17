import router, { constantRoutes, dynamicRouteMap } from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login']
let routesAdded = false

// 根据后端菜单树生成前端扁平路由
function generateRoutes(menus) {
  const routes = []
  // 遍历菜单项生成路由配置
  const walk = (items) => {
    for (const menu of items) {
      if (menu.menuType === 'F') continue
      if (menu.status === 1) continue

      if (menu.menuType === 'C' && (!menu.children || menu.children.length === 0 || menu.children.every(c => c.menuType === 'F'))) {
        // Leaf page menu: create a route
        const route = {
          path: menu.path.startsWith('/') ? menu.path.substring(1) : menu.path,
          name: menu.name,
          meta: { title: menu.name, icon: menu.icon }
        }
        const componentPath = menu.component
        if (componentPath && dynamicRouteMap[componentPath]) {
          route.component = dynamicRouteMap[componentPath]
        } else {
          route.component = { render(h) { return h('div', '页面开发中: ' + (menu.component || menu.path)) } }
        }
        routes.push(route)
      }

      if (menu.children) walk(menu.children)
    }
  }
  walk(menus)
  return routes
}

// 添加动态路由
function addDynamicRoutes() {
  const menus = store.state.menus
  const dynamicRoutes = generateRoutes(menus)
  router.addRoutes([
    {
      path: '/',
      component: () => import('./layout/index.vue'),
      redirect: '/dashboard',
      children: dynamicRoutes.length > 0 ? dynamicRoutes : [{ path: '', redirect: '/dashboard' }]
    }
  ])
  routesAdded = true
}

// 全局路由前置守卫：校验登录状态并动态加载路由
router.beforeEach(async (to, from, next) => {
  NProgress.start()

  if (to.path === '/login') {
    next()
    NProgress.done()
    return
  }

  const token = localStorage.getItem('token')
  if (!token) {
    next('/login')
    NProgress.done()
    return
  }

  // Fast path: user info and routes both loaded
  if (store.state.user && routesAdded) {
    next()
    NProgress.done()
    return
  }

  // Need to load user info and/or generate routes
  try {
    if (!store.state.user) {
      await store.dispatch('getUserInfoAndMenus')
    }
    if (!routesAdded) {
      addDynamicRoutes()
    }
    next({ ...to, replace: true })
  } catch (error) {
    store.dispatch('resetState')
    Message.error('获取用户信息失败，请重新登录')
    next('/login')
  }
  NProgress.done()
})

// 全局路由后置守卫：结束进度条
router.afterEach(() => {
  NProgress.done()
})
