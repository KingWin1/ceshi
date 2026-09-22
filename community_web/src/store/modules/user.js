import Api from '@/api'

const state = {
  token: localStorage.getItem('token') || '',
  admin: JSON.parse(localStorage.getItem('admin') || '{}'),
  // 当前登录管理员角色已分配的菜单权限ID集合（登录时由后端返回）
  menuIds: JSON.parse(localStorage.getItem('menuIds') || '[]')
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    localStorage.setItem('token', token)
  },
  SET_ADMIN(state, admin) {
    state.admin = admin
    localStorage.setItem('admin', JSON.stringify(admin))
  },
  SET_MENU_IDS(state, menuIds) {
    state.menuIds = menuIds || []
    localStorage.setItem('menuIds', JSON.stringify(state.menuIds))
  },
  LOGOUT(state) {
    state.token = ''
    state.admin = {}
    state.menuIds = []
    localStorage.removeItem('token')
    localStorage.removeItem('admin')
    localStorage.removeItem('menuIds')
  }
}

const actions = {
  // 登录
  async login({ commit }, loginData) {
    const res = await Api.login(loginData)
    const { token, admin, menuIds } = res.data
    commit('SET_TOKEN', token)
    commit('SET_ADMIN', admin)
    commit('SET_MENU_IDS', menuIds)
    return res
  },
  // 退出登录
  logout({ commit }) {
    commit('LOGOUT')
  }
}

const getters = {
  token: state => state.token,
  admin: state => state.admin,
  menuIds: state => state.menuIds,
  isLoggedIn: state => !!state.token,
  // 是否拥有指定菜单权限
  hasMenu: state => menuId => state.menuIds.indexOf(menuId) !== -1
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
