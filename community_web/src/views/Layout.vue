<template>
  <div class="layout">
    <!-- ========== 第一列：图标菜单栏 ========== -->
    <aside class="layout-sider">
      <el-scrollbar class="sider-menu-wrap">
        <ul class="sider-menu">
          <li
            v-for="menu in menus"
            :key="menu.menuId"
            class="sider-menu__item"
            :class="{ 'is-active': isActiveTop(menu), 'is-home': isHome(menu) }"
            @click="onMenuClick(menu)"
          >
            <div class="menu-inner">
              <i :class="menu.icon || 'el-icon-menu'"></i>
              <span v-if="!isHome(menu)" class="menu-name">{{ shortName(menu.menuName) }}</span>
            </div>
          </li>
        </ul>
      </el-scrollbar>
    </aside>

    <!-- ========== 第二列：二级菜单面板 ========== -->
    <aside v-if="activeParent" class="layout-sub-sider">
      <div class="sub-sider-title">{{ activeParent.menuName }}</div>
      <ul class="sub-menu">
        <li
          v-for="sub in childrenMap[activeParent.menuId]"
          :key="sub.menuId"
          class="sub-menu__item"
          :class="{ 'is-active': $route.path === sub.path }"
          @click="goRoute(sub)"
        >
          {{ sub.menuName }}
        </li>
      </ul>
    </aside>

    <!-- ========== 右侧主体 ========== -->
    <section class="layout-main">
      <!-- 顶栏 -->
      <header class="layout-header">
        <!-- 面包屑 -->
        <el-breadcrumb separator="/">
          <el-breadcrumb-item
            v-for="(item, idx) in breadcrumbs"
            :key="idx"
            :to="item.path ? { path: item.path } : null"
          >
            {{ item.name }}
          </el-breadcrumb-item>
        </el-breadcrumb>

        <el-dropdown trigger="click" @command="onUserCommand">
          <div class="header-user">
            <span class="header-user__name">{{ displayName }}</span>
            <i class="el-icon-arrow-down"></i>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </header>

      <!-- 内容区 -->
      <main class="layout-content">
        <router-view />
      </main>
    </section>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import Api from '@/api'

// 需要展开二级菜单面板的一级菜单名称关键字
const HAS_SUBMENU_KEYS = ['楼栋', '资讯', '人事', '系统']

export default {
  name: 'Layout',
  data() {
    return {
      menus: [],
      childrenMap: {},
      // 当前展开二级面板的一级菜单
      activeParent: null
    }
  },
  computed: {
    admin() {
      return this.$store.getters['user/admin'] || {}
    },
    displayName() {
      return this.admin.name || this.admin.username || '管理员'
    },
    // 面包屑：首页 / 一级菜单 / 二级菜单
    breadcrumbs() {
      const items = [{ name: '首页', path: '/home' }]
      const path = this.$route.path

      if (path === '/home') {
        return items
      }

      // 查找当前路由所属的一级菜单
      const parent = this.menus.find(menu => this.menuContainsPath(menu, path))
      if (parent) {
        items.push({ name: parent.menuName, path: this.hasChildren(parent) ? '' : parent.path })
        const children = this.childrenMap[parent.menuId] || []
        const sub = children.find(c => c.path === path)
        if (sub) {
          items.push({ name: sub.menuName, path: '' })
        }
      }
      return items
    }
  },
  watch: {
    // 路由变化时同步二级面板展开状态
    $route() {
      this.syncActiveParent()
    }
  },
  created() {
    this.loadMenus()
  },
  methods: {
    ...mapActions('user', ['logout']),

    // 加载一级菜单 + 指定菜单的二级菜单（按角色权限过滤）
    async loadMenus() {
      try {
        const res = await Api.getMenuListAll()
        const allMenus = (res.data || []).filter(item => item.type !== 3)
        // 按当前角色已分配的菜单权限ID过滤
        const menuIds = this.$store.getters['user/menuIds'] || []
        const allowed = allMenus.filter(item => menuIds.indexOf(item.menuId) !== -1)
        // 一级菜单：parentId 为 0
        this.menus = allowed.filter(item => item.parentId === 0)
        // 构建 childrenMap（仅楼栋、资讯、人事、系统展开二级面板）
        const map = {}
        allowed.forEach(item => {
          if (item.parentId !== 0) {
            if (!map[item.parentId]) {
              map[item.parentId] = []
            }
            map[item.parentId].push(item)
          }
        })
        this.menus.forEach(menu => {
          const hasSub = HAS_SUBMENU_KEYS.some(key => menu.menuName.indexOf(key) !== -1)
          if (!hasSub) {
            delete map[menu.menuId]
          }
        })
        this.childrenMap = map
        this.syncActiveParent()
      } catch (e) {
        this.$message.error('菜单加载失败')
      }
    },

    hasChildren(menu) {
      const children = this.childrenMap[menu.menuId]
      return !!(children && children.length)
    },

    // 一级菜单是否覆盖该路径（自身或子菜单）
    menuContainsPath(menu, path) {
      if (menu.path === path) {
        return true
      }
      const children = this.childrenMap[menu.menuId] || []
      return children.some(sub => sub.path === path)
    },

    // 根据当前路由同步二级面板展开
    syncActiveParent() {
      const path = this.$route.path
      const parent = this.menus.find(
        menu => this.hasChildren(menu) && (this.childrenMap[menu.menuId] || []).some(sub => sub.path === path)
      )
      this.activeParent = parent || null
    },

    // 首页菜单（只显示图标，不显示文字）
    isHome(menu) {
      return menu.path === '/home'
    },

    // 一级菜单是否高亮
    isActiveTop(menu) {
      return this.menuContainsPath(menu, this.$route.path)
    },

    onMenuClick(menu) {
      if (this.hasChildren(menu)) {
        // 展开二级面板并跳转到第一个子菜单
        this.activeParent = menu
        const first = this.childrenMap[menu.menuId][0]
        if (first && this.$route.path !== first.path) {
          this.$router.push(first.path)
        }
        return
      }
      this.activeParent = null
      this.goRoute(menu)
    },

    goRoute(menu) {
      if (!menu.path) {
        this.$message.info('该功能页面开发中')
        return
      }
      if (this.$route.path !== menu.path) {
        this.$router.push(menu.path)
      }
    },

    // 侧栏文字精简（去掉"管理/列表"等后缀）
    shortName(name) {
      return name.replace(/(管理|列表|建议|设置)$/g, '')
    },

    onUserCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '确定',
          type: 'warning'
        })
          .then(() => {
            this.logout()
            this.$router.push('/login')
          })
          .catch(() => {})
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/layout/Layout.less';
</style>
