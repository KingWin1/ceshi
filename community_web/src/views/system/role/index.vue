<template>
  <div class="role-list">
    <!-- 查询区 -->
    <div class="search-card">
      <span class="search-label">关键词</span>
      <el-input
        v-model="query.roleName"
        class="search-input"
        placeholder="角色名称"
        clearable
        @keyup.enter.native="onSearch"
      />
      <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
      <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增角色</el-button>
        <el-button
          class="btn-danger"
          type="danger"
          icon="el-icon-delete"
          :disabled="selectedIds.length === 0"
          @click="onBatchDelete"
        >批量删除</el-button>
        <el-button icon="el-icon-refresh" @click="fetchList">刷新</el-button>
      </div>

      <el-table
        ref="table"
        v-loading="loading"
        :data="list"
        row-key="roleId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column prop="roleId" label="ID" min-width="80" />
        <el-table-column prop="roleName" label="角色名称" min-width="200" />
        <el-table-column label="操作" width="160" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" icon="el-icon-edit" class="op-edit" @click="onEdit(row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" class="op-del" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="query.pageNum"
        :page-size="query.pageSize"
        :page-sizes="[5, 10, 20, 30]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="onSizeChange"
        @current-change="onPageChange"
      />
    </div>

    <!-- 新增 / 修改弹窗（页面私有子组件） -->
    <add-or-update
      :visible.sync="dialogVisible"
      :is-edit="isEdit"
      :record="editRecord"
      :menu-tree="menuTree"
      :submitting="submitting"
      @submit="onSubmit"
    />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'

export default {
  name: 'RoleIndex',
  components: {
    AddOrUpdate
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: {
        roleName: '',
        pageNum: 1,
        pageSize: 10
      },
      // 权限树数据源（全部启用菜单）
      menuTree: [],
      // 当前勾选的角色ID
      selectedIds: [],
      // 弹窗状态
      dialogVisible: false,
      submitting: false,
      isEdit: false,
      // 编辑时回显的角色数据（含已分配菜单ID）
      editRecord: null
    }
  },
  created() {
    this.fetchMenuTree()
    this.fetchList()
  },
  methods: {
    // 查询全部启用菜单并构建树（权限分配数据源）
    async fetchMenuTree() {
      try {
        const res = await Api.getMenuListAll()
        this.menuTree = this.buildTree(res.data || [])
      } catch (e) {
        // 权限树加载失败不阻塞列表
      }
    },

    // 平铺菜单列表构建为树结构
    buildTree(list) {
      const map = {}
      const roots = []
      list.forEach(item => {
        map[item.menuId] = { ...item, children: [] }
      })
      list.forEach(item => {
        const node = map[item.menuId]
        if (item.parentId !== 0 && map[item.parentId]) {
          map[item.parentId].children.push(node)
        } else {
          roots.push(node)
        }
      })
      return roots
    },

    // 分页+条件查询角色列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getRolePage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        this.$message.error('角色列表查询失败')
      } finally {
        this.loading = false
      }
    },

    onSearch() {
      this.query.pageNum = 1
      this.fetchList()
    },

    onReset() {
      this.query.roleName = ''
      this.query.pageNum = 1
      this.fetchList()
    },

    onSizeChange(size) {
      this.query.pageSize = size
      this.query.pageNum = 1
      this.fetchList()
    },

    onPageChange(page) {
      this.query.pageNum = page
      this.fetchList()
    },

    onSelectionChange(rows) {
      this.selectedIds = rows.map(row => row.roleId)
    },

    // 打开新增弹窗
    onAdd() {
      this.isEdit = false
      this.editRecord = null
      this.dialogVisible = true
    },

    // 编辑：先根据角色ID查询角色信息（含已分配权限）回显
    async onEdit(row) {
      try {
        const res = await Api.getRoleById(row.roleId)
        if (!res.data) {
          this.$message.error('角色信息不存在')
          return
        }
        this.editRecord = res.data
        this.isEdit = true
        this.dialogVisible = true
      } catch (e) {
        this.$message.error('角色信息查询失败')
      }
    },

    // 弹窗提交：新增 / 修改（含权限分配）
    async onSubmit(form) {
      this.submitting = true
      try {
        if (this.isEdit) {
          await Api.updateRoleWithMenu(form)
          this.$message.success('修改成功')
        } else {
          await Api.addRoleWithMenu(form)
          this.$message.success('添加成功')
        }
        this.dialogVisible = false
        this.fetchList()
      } catch (e) {
        // 错误提示已由响应拦截器统一处理
      } finally {
        this.submitting = false
      }
    },

    // 删除单个角色（被管理员占用时后端拒绝删除）
    async onDelete(row) {
      try {
        await this.$confirm(`确定删除角色"${row.roleName}"吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        await Api.deleteRole(row.roleId)
        this.$message.success('删除成功')
        this.fetchList()
      } catch (e) {
        // 请求失败提示由拦截器处理
      }
    },

    // 批量删除角色（仅删除未被占用的角色，被占用的给出提示）
    async onBatchDelete() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要删除的角色')
        return
      }
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedIds.length} 个角色吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        const res = await Api.deleteRoleBatch(this.selectedIds)
        // 后端返回提示信息（可能包含被占用未删除的角色名称）
        if (res.data && res.data.indexOf('未删除') !== -1) {
          this.$message.warning(res.data)
        } else {
          this.$message.success(res.data || '批量删除成功')
        }
        this.$refs.table.clearSelection()
        this.selectedIds = []
        this.fetchList()
      } catch (e) {
        // 请求失败提示由拦截器处理
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/system/role/index.less';
</style>
