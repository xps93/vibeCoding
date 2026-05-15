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
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER(state, user) {
      state.user = user
    },
    SET_ROLES(state, roles) {
      state.roles = roles
    },
    SET_PERMISSIONS(state, permissions) {
      state.permissions = permissions
    },
    SET_MENUS(state, menus) {
      state.menus = menus
    },
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
    async login({ commit }, userInfo) {
      const res = await getUserInfo()
      // After login, get user info
    },
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
    resetState({ commit }) {
      commit('RESET_STATE')
    }
  }
})

export default store
