<template>
  <div class="dept-list">
    <!-- 查询区 -->
    <div class="search-card">
      <span class="search-label">关键词</span>
      <el-input
        v-model="query.deptName"
        class="search-input"
        placeholder="部门名称"
        clearable
        @keyup.enter.native="onSearch"
      />
      <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
      <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增部门</el-button>
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
        row-key="deptId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column prop="deptId" label="ID" min-width="80" />
        <el-table-column prop="deptName" label="部门名称" min-width="150" />
        <el-table-column label="排序号" width="100" align="center">
          <template slot-scope="{ row }">{{ row.sort == null ? 0 : row.sort }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.remark || '-' }}</template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="160">
          <template slot-scope="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
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
      :submitting="submitting"
      @submit="onSubmit"
    />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'

export default {
  name: 'DeptIndex',
  components: {
    AddOrUpdate
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: {
        deptName: '',
        pageNum: 1,
        pageSize: 10
      },
      // 当前勾选的部门ID
      selectedIds: [],
      // 弹窗状态
      dialogVisible: false,
      submitting: false,
      isEdit: false,
      // 编辑时回显的部门数据
      editRecord: null
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    // 分页+条件查询部门列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getDepartmentPage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        this.$message.error('部门列表查询失败')
      } finally {
        this.loading = false
      }
    },

    onSearch() {
      this.query.pageNum = 1
      this.fetchList()
    },

    onReset() {
      this.query.deptName = ''
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
      this.selectedIds = rows.map(row => row.deptId)
    },

    // 时间格式化：2026-06-27 10:15
    formatTime(time) {
      if (!time) {
        return '-'
      }
      const d = new Date(time)
      if (isNaN(d.getTime())) {
        return time
      }
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
    },

    // 打开新增弹窗
    onAdd() {
      this.isEdit = false
      this.editRecord = null
      this.dialogVisible = true
    },

    // 编辑：先根据部门ID查询部门信息回显
    async onEdit(row) {
      try {
        const res = await Api.getDepartmentById(row.deptId)
        if (!res.data) {
          this.$message.error('部门信息不存在')
          return
        }
        this.editRecord = res.data
        this.isEdit = true
        this.dialogVisible = true
      } catch (e) {
        this.$message.error('部门信息查询失败')
      }
    },

    // 弹窗提交：新增 / 修改
    async onSubmit(form) {
      this.submitting = true
      try {
        if (this.isEdit) {
          await Api.updateDepartment(form)
          this.$message.success('修改成功')
        } else {
          await Api.addDepartment(form)
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

    // 删除单个部门：确认 -> 统计岗位占用 -> 有占用二次确认 -> service 联动删除部门+岗位
    async onDelete(row) {
      try {
        await this.$confirm(`确定删除部门「${row.deptName}」吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        // 统计该部门在岗位表中是否存在数据
        const res = await Api.countPositionByDeptId(row.deptId)
        const count = res.data || 0
        if (count > 0) {
          // 存在岗位记录，提示用户是否继续删除
          await this.$confirm(
            `该部门存在 ${count} 条岗位记录，继续删除将同时删除这些岗位，是否继续？`,
            '提示',
            {
              confirmButtonText: '继续',
              cancelButtonText: '取消',
              type: 'warning'
            }
          )
        }
        // 后端 service 中先删除部门信息，再联动删除岗位数据
        await Api.deleteDepartment(row.deptId)
        this.$message.success('删除成功')
        this.fetchList()
      } catch (e) {
        // 用户取消或请求失败（失败提示由拦截器处理）
      }
    },

    // 批量删除：确认 -> 统计各部门岗位占用 -> 有占用二次确认 -> service 联动删除岗位+部门
    async onBatchDelete() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要删除的部门')
        return
      }
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedIds.length} 个部门吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        // 统计每个部门在岗位表中是否被占用
        const res = await Api.countPositionByDeptIds(this.selectedIds)
        const occupied = (res.data || []).filter(item => item.positionCount > 0)
        if (occupied.length > 0) {
          const names = occupied.map(item => item.deptName).join('、')
          // 存在岗位记录，提示用户是否继续删除
          await this.$confirm(
            `选中的部门「${names}」存在岗位记录，继续删除将同时删除这些岗位，是否继续？`,
            '提示',
            {
              confirmButtonText: '继续',
              cancelButtonText: '取消',
              type: 'warning'
            }
          )
        }
        // 后端 service 中先联动删除岗位数据，再批量删除部门信息
        await Api.deleteDepartmentBatch(this.selectedIds)
        this.$message.success('批量删除成功')
        this.$refs.table.clearSelection()
        this.selectedIds = []
        this.fetchList()
      } catch (e) {
        // 用户取消或请求失败（失败提示由拦截器处理）
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/hr/dept/index.less';
</style>
