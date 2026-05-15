import router, { constantRoutes, dynamicRouteMap } from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login']

// Generate flat routes from backend menu tree (no nested Layout)
function generateRoutes(menus) {
  const routes = []
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

  // Has token, has user info loaded?
  if (store.state.user) {
    next()
    NProgress.done()
    return
  }

  // Fetch user info and generate routes
  try {
    await store.dispatch('getUserInfoAndMenus')
    const menus = store.state.menus
    const dynamicRoutes = generateRoutes(menus)

    // Add routes as children of Layout root (flat structure, no nested Layout)
    router.addRoutes([
      {
        path: '/',
        component: () => import('./layout/index.vue'),
        redirect: '/dashboard',
        children: dynamicRoutes.length > 0 ? dynamicRoutes : [{ path: '', redirect: '/dashboard' }]
      }
    ])
    next({ ...to, replace: true })
  } catch (error) {
    store.dispatch('resetState')
    Message.error('获取用户信息失败，请重新登录')
    next('/login')
  }
  NProgress.done()
})

router.afterEach(() => {
  NProgress.done()
})
