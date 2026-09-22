<template>
  <div class="admin-list">
    <!-- 查询区 -->
    <div class="search-card">
      <span class="search-label">关键词</span>
      <el-input
        v-model="query.keyword"
        class="search-input"
        placeholder="用户名/姓名/手机号"
        clearable
        @keyup.enter.native="onSearch"
      />
      <span class="search-label">角色</span>
      <el-select v-model="query.roleId" class="search-select" placeholder="全部" clearable>
        <el-option
          v-for="item in roleList"
          :key="item.roleId"
          :label="item.roleName"
          :value="item.roleId"
        />
      </el-select>
      <span class="search-label">状态</span>
      <el-select v-model="query.status" class="search-select status-select" placeholder="全部" clearable>
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="2" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
      <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增管理员</el-button>
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
        row-key="adminId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column prop="adminId" label="ID" min-width="60" />
        <el-table-column prop="username" label="用户名" min-width="110" />
        <el-table-column prop="name" label="姓名" min-width="100">
          <template slot-scope="{ row }">{{ row.name || '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120">
          <template slot-scope="{ row }">{{ row.phone || '-' }}</template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.email || '-' }}</template>
        </el-table-column>
        <el-table-column label="角色" min-width="110">
          <template slot-scope="{ row }">{{ roleName(row.roleId) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后登录时间" min-width="160">
          <template slot-scope="{ row }">{{ row.lastLoginTime ? formatTime(row.lastLoginTime) : '从未登录' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" icon="el-icon-edit" class="op-edit" @click="onEdit(row)">编辑</el-button>
            <el-button type="text" icon="el-icon-key" class="op-reset" @click="onResetPassword(row)">重置密码</el-button>
            <el-button
              v-if="row.status === 1"
              type="text"
              icon="el-icon-circle-close"
              class="op-disable"
              @click="onUpdateStatus(row, 2)"
            >禁用</el-button>
            <el-button
              v-else
              type="text"
              icon="el-icon-circle-check"
              class="op-enable"
              @click="onUpdateStatus(row, 1)"
            >启用</el-button>
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
      :role-list="roleList"
      :submitting="submitting"
      @submit="onSubmit"
    />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'

export default {
  name: 'AdminIndex',
  components: {
    AddOrUpdate
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: {
        keyword: '',
        roleId: null,
        status: null,
        pageNum: 1,
        pageSize: 10
      },
      // 全部角色（下拉框数据源 + 表格名称映射）
      roleList: [],
      // 当前勾选的管理员ID
      selectedIds: [],
      // 弹窗状态
      dialogVisible: false,
      submitting: false,
      isEdit: false,
      // 编辑时回显的管理员数据
      editRecord: null
    }
  },
  created() {
    this.fetchRoleList()
    this.fetchList()
  },
  methods: {
    // 查询全部角色信息（角色下拉框数据源）
    async fetchRoleList() {
      try {
        const res = await Api.getRoleListAll()
        this.roleList = res.data || []
      } catch (e) {
        // 下拉数据加载失败不阻塞页面
      }
    },

    // 分页+条件查询管理员列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getAdminPage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        this.$message.error('管理员列表查询失败')
      } finally {
        this.loading = false
      }
    },

    onSearch() {
      this.query.pageNum = 1
      this.fetchList()
    },

    onReset() {
      this.query.keyword = ''
      this.query.roleId = null
      this.query.status = null
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
      this.selectedIds = rows.map(row => row.adminId)
    },

    // 表格角色名称映射
    roleName(roleId) {
      const item = this.roleList.find(r => r.roleId === roleId)
      return item ? item.roleName : '-'
    },

    // 时间格式化：2026-09-08 16:51:30
    formatTime(time) {
      if (!time) {
        return '-'
      }
      const d = new Date(time)
      if (isNaN(d.getTime())) {
        return time
      }
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    },

    // 打开新增弹窗
    onAdd() {
      this.isEdit = false
      this.editRecord = null
      this.dialogVisible = true
    },

    // 编辑：先根据管理员ID查询管理员信息回显
    async onEdit(row) {
      try {
        const res = await Api.getAdminById(row.adminId)
        if (!res.data) {
          this.$message.error('管理员信息不存在')
          return
        }
        this.editRecord = res.data
        this.isEdit = true
        this.dialogVisible = true
      } catch (e) {
        this.$message.error('管理员信息查询失败')
      }
    },

    // 弹窗提交：新增 / 修改
    async onSubmit(form) {
      this.submitting = true
      try {
        if (this.isEdit) {
          await Api.updateAdmin(form)
          this.$message.success('修改成功')
        } else {
          await Api.addAdmin(form)
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

    // 重置密码：确认后调重置密码接口
    async onResetPassword(row) {
      try {
        await this.$confirm(`确定要重置管理员"${row.username}"的密码为 123456 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        await Api.resetAdminPassword(row.adminId)
        this.$message.success('重置密码成功')
      } catch (e) {
        // 请求失败提示由拦截器处理
      }
    },

    // 修改状态：启用 / 禁用
    async onUpdateStatus(row, status) {
      const action = status === 1 ? '启用' : '禁用'
      try {
        await this.$confirm(`确定${action}管理员"${row.username}"吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        await Api.updateAdminStatus(row.adminId, status)
        this.$message.success(`${action}成功`)
        this.fetchList()
      } catch (e) {
        // 请求失败提示由拦截器处理
      }
    },

    // 删除单个管理员
    async onDelete(row) {
      try {
        await this.$confirm(`确定删除管理员"${row.username}"吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        await Api.deleteAdmin(row.adminId)
        this.$message.success('删除成功')
        this.fetchList()
      } catch (e) {
        // 请求失败提示由拦截器处理
      }
    },

    // 批量删除管理员
    async onBatchDelete() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要删除的管理员')
        return
      }
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedIds.length} 个管理员吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        await Api.deleteAdminBatch(this.selectedIds)
        this.$message.success('批量删除成功')
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
@import '@/assets/styles/system/admin/index.less';
</style>
