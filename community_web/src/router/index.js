import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

// vue-router 3.x 中，若导航被守卫重定向（如登录后无首页权限被跳转到有权限的页面），
// push() 返回的 Promise 会 reject 并表现为未捕获错误，这里统一忽略该类导航失败
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    const isNavigationFailure = VueRouter.isNavigationFailure && VueRouter.isNavigationFailure(err)
    const isRedirected = err && err.message && err.message.indexOf('Redirected when going from') !== -1
    const isDuplicated = err && err.name === 'NavigationDuplicated'
    if (isNavigationFailure || isRedirected || isDuplicated) {
      return err
    }
    return Promise.reject(err)
  })
}

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requireAuth: false }
  },
  {
    // 主布局：左侧菜单 + 顶栏 + 内容区
    path: '/',
    component: () => import('@/views/Layout.vue'),
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/HomeView.vue'),
        meta: { title: '首页', requireAuth: true, menuId: 1 }
      },
      // 以下页面待开发，先占位
      { path: 'building', redirect: '/building/list' },
      { path: 'building/list', component: () => import('@/views/building/index.vue'), meta: { title: '楼栋列表', requireAuth: true, menuId: 19 } },
      { path: 'building/house', component: () => import('@/views/house/index.vue'), meta: { title: '房屋列表', requireAuth: true, menuId: 20 } },
      { path: 'resident', component: () => import('@/views/resident/index.vue'), meta: { title: '居民列表', requireAuth: true, menuId: 3 } },
      { path: 'auth', component: () => import('@/views/auth/index.vue'), meta: { title: '房屋认证', requireAuth: true, menuId: 4 } },
      { path: 'bill', component: () => import('@/views/bill/index.vue'), meta: { title: '账单列表', requireAuth: true, menuId: 5 } },
      { path: 'repair', component: () => import('@/views/repair/index.vue'), meta: { title: '报修管理', requireAuth: true, menuId: 6 } },
      { path: 'complaint', component: () => import('@/views/complaint/index.vue'), meta: { title: '投诉建议', requireAuth: true, menuId: 7 } },
      { path: 'info/notice', component: () => import('@/views/notice/index.vue'), meta: { title: '公告管理', requireAuth: true, menuId: 65 } },
      { path: 'info/ad', component: () => import('@/views/advertisement/index.vue'), meta: { title: '广告管理', requireAuth: true, menuId: 66 } },
      { path: 'store', component: () => import('@/views/store/index.vue'), meta: { title: '门店列表', requireAuth: true, menuId: 9 } },
      { path: 'hr/dept', component: () => import('@/views/hr/dept/index.vue'), meta: { title: '部门管理', requireAuth: true, menuId: 89 } },
      { path: 'hr/position', component: () => import('@/views/hr/position/index.vue'), meta: { title: '岗位管理', requireAuth: true, menuId: 90 } },
      { path: 'hr/employee', component: () => import('@/views/hr/employee/index.vue'), meta: { title: '员工管理', requireAuth: true, menuId: 91 } },
      { path: 'system/admin', component: () => import('@/views/system/admin/index.vue'), meta: { title: '管理员管理', requireAuth: true, menuId: 107 } },
      { path: 'system/role', component: () => import('@/views/system/role/index.vue'), meta: { title: '角色管理', requireAuth: true, menuId: 108 } },
      { path: 'system/fee', component: () => import('@/views/system/fee/index.vue'), meta: { title: '费用设置', requireAuth: true, menuId: 109 } },
      { path: 'system/cs', component: () => import('@/views/system/cs/index.vue'), meta: { title: '客服电话', requireAuth: true, menuId: 110 } }
    ]
  }
]

const router = new VueRouter({
  routes
})

// 路由守卫：除登录页外，其他页面需要验证token + 菜单权限
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 小区通` : '小区通'

  const token = store.getters['user/token']

  if (to.meta.requireAuth) {
    // 需要登录的页面
    if (!token) {
      // 未登录，跳转到登录页
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
    // 菜单权限校验：页面绑定了 menuId 且当前角色未分配该权限时禁止访问
    if (to.meta.menuId && !store.getters['user/hasMenu'](to.meta.menuId)) {
      // 跳转到第一个有权限的页面；无任何权限时放行到布局页（菜单为空）
      const first = router.options.routes
        .flatMap(r => r.children || [])
        .find(r => r.meta && r.meta.menuId && store.getters['user/hasMenu'](r.meta.menuId))
      if (first && first.path !== to.path) {
        next({ path: '/' + first.path })
      } else {
        next()
      }
      return
    }
    next()
  } else {
    // 不需要登录的页面（如登录页）
    if (to.path === '/login' && token) {
      // 已登录用户访问登录页，跳转到首页
      next({ path: '/home' })
    } else {
      next()
    }
  }
})

export default router
