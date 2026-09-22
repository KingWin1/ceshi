<template>
  <div class="employee-list">
    <!-- 查询区 -->
    <div class="search-card">
      <span class="search-label">关键词</span>
      <el-input
        v-model="query.keyword"
        class="search-input"
        placeholder="姓名/手机号"
        clearable
        @keyup.enter.native="onSearch"
      />
      <span class="search-label">部门</span>
      <el-select
        v-model="query.deptId"
        class="search-select"
        placeholder="全部"
        clearable
        @change="onQueryDeptChange"
      >
        <el-option
          v-for="item in deptList"
          :key="item.deptId"
          :label="item.deptName"
          :value="item.deptId"
        />
      </el-select>
      <span class="search-label">岗位</span>
      <el-select v-model="query.positionId" class="search-select" placeholder="全部" clearable>
        <el-option
          v-for="item in queryPositionList"
          :key="item.positionId"
          :label="item.positionName"
          :value="item.positionId"
        />
      </el-select>
      <span class="search-label">状态</span>
      <el-select v-model="query.status" class="search-select status-select" placeholder="全部" clearable>
        <el-option label="在职" :value="1" />
        <el-option label="离职" :value="2" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
      <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增员工</el-button>
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
        row-key="employeeId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column prop="employeeId" label="ID" min-width="70" />
        <el-table-column prop="employeeName" label="姓名" min-width="110" />
        <el-table-column prop="phone" label="手机号" min-width="140">
          <template slot-scope="{ row }">{{ row.phone || '-' }}</template>
        </el-table-column>
        <el-table-column label="性别" width="80" align="center">
          <template slot-scope="{ row }">{{ genderText(row.gender) }}</template>
        </el-table-column>
        <el-table-column label="部门" min-width="120">
          <template slot-scope="{ row }">{{ deptName(row.deptId) }}</template>
        </el-table-column>
        <el-table-column label="岗位" min-width="120">
          <template slot-scope="{ row }">{{ positionName(row.positionId) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
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
      :dept-list="deptList"
      :submitting="submitting"
      @submit="onSubmit"
    />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'

export default {
  name: 'EmployeeIndex',
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
        deptId: null,
        positionId: null,
        status: null,
        pageNum: 1,
        pageSize: 10
      },
      // 全部部门（下拉框数据源 + 表格名称映射）
      deptList: [],
      // 全部岗位（表格名称映射）
      allPositionList: [],
      // 查询区岗位下拉（与部门二级联动）
      queryPositionList: [],
      // 当前勾选的员工ID
      selectedIds: [],
      // 弹窗状态
      dialogVisible: false,
      submitting: false,
      isEdit: false,
      // 编辑时回显的员工数据
      editRecord: null
    }
  },
  created() {
    this.fetchDeptList()
    this.fetchAllPositionList()
    this.fetchList()
  },
  methods: {
    // 查询全部部门信息（部门下拉框数据源）
    async fetchDeptList() {
      try {
        const res = await Api.getDepartmentListAll()
        this.deptList = res.data || []
      } catch (e) {
        // 下拉数据加载失败不阻塞页面
      }
    },

    // 查询全部岗位信息（表格岗位名称映射）
    async fetchAllPositionList() {
      try {
        const res = await Api.getPositionListAll()
        this.allPositionList = res.data || []
        // 未选部门时岗位下拉展示全部
        if (!this.query.deptId) {
          this.queryPositionList = this.allPositionList
        }
      } catch (e) {
        // 下拉数据加载失败不阻塞页面
      }
    },

    // 分页+条件查询员工列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getEmployeePage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        this.$message.error('员工列表查询失败')
      } finally {
        this.loading = false
      }
    },

    // 查询区部门变化：二级联动，按部门ID查询岗位列表；未选部门时展示全部岗位
    async onQueryDeptChange(deptId) {
      this.query.positionId = null
      if (!deptId) {
        this.queryPositionList = this.allPositionList
        return
      }
      try {
        const res = await Api.getPositionByDeptId(deptId)
        this.queryPositionList = res.data || []
      } catch (e) {
        this.queryPositionList = []
      }
    },

    onSearch() {
      this.query.pageNum = 1
      this.fetchList()
    },

    onReset() {
      this.query.keyword = ''
      this.query.deptId = null
      this.query.positionId = null
      this.query.status = null
      this.query.pageNum = 1
      this.queryPositionList = this.allPositionList
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
      this.selectedIds = rows.map(row => row.employeeId)
    },

    // 表格列文本映射
    genderText(gender) {
      const map = { 1: '男', 2: '女', 3: '未知' }
      return map[gender] || '未知'
    },

    deptName(deptId) {
      const item = this.deptList.find(d => d.deptId === deptId)
      return item ? item.deptName : '-'
    },

    positionName(positionId) {
      const item = this.allPositionList.find(p => p.positionId === positionId)
      return item ? item.positionName : '-'
    },

    // 时间格式化：2026-06-27 14:06
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

    // 编辑：先根据员工ID查询员工信息回显
    async onEdit(row) {
      try {
        const res = await Api.getEmployeeById(row.employeeId)
        if (!res.data) {
          this.$message.error('员工信息不存在')
          return
        }
        this.editRecord = res.data
        this.isEdit = true
        this.dialogVisible = true
      } catch (e) {
        this.$message.error('员工信息查询失败')
      }
    },

    // 弹窗提交：新增 / 修改
    async onSubmit(form) {
      this.submitting = true
      try {
        if (this.isEdit) {
          await Api.updateEmployee(form)
          this.$message.success('修改成功')
        } else {
          await Api.addEmployee(form)
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

    // 删除单个员工
    async onDelete(row) {
      try {
        await this.$confirm(`确定删除员工「${row.employeeName}」吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        await Api.deleteEmployee(row.employeeId)
        this.$message.success('删除成功')
        this.fetchList()
      } catch (e) {
        // 请求失败提示由拦截器处理
      }
    },

    // 批量删除员工
    async onBatchDelete() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要删除的员工')
        return
      }
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedIds.length} 名员工吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        await Api.deleteEmployeeBatch(this.selectedIds)
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
@import '@/assets/styles/hr/employee/index.less';
</style>
