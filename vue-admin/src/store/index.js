import Vue from 'vue'
import Vuex from 'vuex'
import { getUserInfo, getRouters } from '../api/login'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    user: null,
    roles: [],
    permissions: [],
    menus: []
  },
  mutations: {
    // 设置Token
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    // 设置当前用户信息
    SET_USER(state, user) {
      state.user = user
    },
    // 设置角色列表
    SET_ROLES(state, roles) {
      state.roles = roles
    },
    // 设置权限标识列表
    SET_PERMISSIONS(state, permissions) {
      state.permissions = permissions
    },
    // 设置菜单列表
    SET_MENUS(state, menus) {
      state.menus = menus
    },
    // 重置所有状态（退出登录）
    RESET_STATE(state) {
      state.token = ''
      state.user = null
      state.roles = []
      state.permissions = []
      state.menus = []
      localStorage.removeItem('token')
    }
  },
  actions: {
    // 登录操作（获取用户信息）
    async login({ commit }, userInfo) {
      const res = await getUserInfo()
      // After login, get user info
    },
    // 获取用户信息及路由菜单
    async getUserInfoAndMenus({ commit }) {
      const infoRes = await getUserInfo()
      const { user, roles, permissions } = infoRes.data
      commit('SET_USER', user)
      commit('SET_ROLES', roles)
      commit('SET_PERMISSIONS', permissions)

      const menuRes = await getRouters()
      commit('SET_MENUS', menuRes.data)
      return { roles, permissions }
    },
    // 重置状态（退出登录）
    resetState({ commit }) {
      commit('RESET_STATE')
    }
  }
})

export default store
