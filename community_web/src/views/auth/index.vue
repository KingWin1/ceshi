<template>
  <div class="auth-list">
    <!-- 查询区 -->
    <div class="search-card">
      <div class="search-row">
        <!-- 审核状态页签 -->
        <div class="type-tabs">
          <span
            v-for="tab in auditTabs"
            :key="String(tab.value)"
            :class="['type-tab', { active: query.auditStatus === tab.value }]"
            @click="onTabChange(tab.value)"
          >{{ tab.label }}</span>
        </div>
        <span class="search-label">楼栋</span>
        <el-select v-model="query.buildingId" class="w-130" placeholder="请选择" clearable @change="onQueryBuildingChange">
          <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
        </el-select>
        <span class="search-label">单元</span>
        <el-select
          v-model="query.unitNo"
          class="w-120"
          placeholder="请选择"
          clearable
          :disabled="!query.buildingId"
          @change="onQueryUnitChange"
        >
          <el-option v-for="u in queryUnitOptions" :key="u.value" :label="u.label" :value="u.value" />
        </el-select>
        <span class="search-label">房号</span>
        <el-select
          v-model="query.houseNumber"
          class="w-120"
          placeholder="请选择"
          clearable
          :disabled="!query.unitNo"
        >
          <el-option v-for="h in queryHouseOptions" :key="h" :label="h" :value="h" />
        </el-select>
        <span class="search-label">关键词</span>
        <el-input
          v-model="query.keyword"
          class="w-200"
          placeholder="姓名/身份证/电话/备注"
          clearable
          @keyup.enter.native="onSearch"
        />
      </div>
      <div class="search-row">
        <span class="search-label">创建时间</span>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          class="w-260"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
        />
        <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
        <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">添加认证</el-button>
        <el-button
          class="btn-batch"
          type="success"
          :disabled="selectedRows.length === 0"
          @click="onBatchAudit"
        >批量审核</el-button>
        <el-button
          class="btn-batch"
          type="warning"
          :disabled="selectedRows.length === 0"
          @click="onBatchCancelAudit"
        >批量取消审核</el-button>
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
        row-key="authId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column prop="buildingName" label="楼栋名称" min-width="140" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.buildingName || '-' }}</template>
        </el-table-column>
        <el-table-column label="单元名称" min-width="90">
          <template slot-scope="{ row }">{{ row.unitNo ? row.unitNo + '单元' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="houseNumber" label="房间名称" min-width="90" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <!-- 身份证号超长省略，悬浮显示全部 -->
        <el-table-column prop="idCard" label="身份证号" min-width="160" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.idCard || '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" min-width="120" />
        <el-table-column label="关系" min-width="80">
          <template slot-scope="{ row }">
            <el-tag v-if="row.type" size="small" :type="typeTagMap[row.type]">{{ typeMap[row.type] }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="认证材料" min-width="90">
          <template slot-scope="{ row }">
            <el-image
              v-if="firstMaterial(row.authMaterialUrl)"
              class="material-thumb"
              :src="resolveUrl(firstMaterial(row.authMaterialUrl))"
              :preview-src-list="materialList(row.authMaterialUrl)"
              fit="cover"
            >
              <div slot="error" class="material-error"><i class="el-icon-document" /></div>
            </el-image>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" min-width="90">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="auditTagMap[row.auditStatus]">{{ auditMap[row.auditStatus] || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditRemark" label="审核备注" min-width="110" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.auditRemark || '-' }}</template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="150">
          <template slot-scope="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" icon="el-icon-edit" class="op-edit" @click="onEdit(row)">编辑</el-button>
            <!-- 待审核显示"审核"，已审核显示"取消审核" -->
            <el-button v-if="row.auditStatus === 1" type="text" class="op-audit" @click="onAudit(row)">审核</el-button>
            <el-button v-else type="text" class="op-audit" @click="onCancelAudit(row)">取消审核</el-button>
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

    <!-- 添加 / 编辑弹窗 -->
    <add-or-update
      :visible.sync="addVisible"
      :is-edit="isEdit"
      :record="editRecord"
      :buildings="buildings"
      :submitting="submitting"
      @submit="onAddSubmit"
    />

    <!-- 审核 / 批量审核弹窗 -->
    <audit-dialog
      :visible.sync="auditVisible"
      :title="auditTitle"
      :submitting="submitting"
      @submit="onAuditSubmit"
    />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'
import AuditDialog from './audit-dialog.vue'

// 查询条件初始值
const emptyQuery = () => ({
  auditStatus: null,
  buildingId: null,
  unitNo: '',
  houseNumber: '',
  keyword: '',
  startDate: '',
  endDate: '',
  pageNum: 1,
  pageSize: 10
})

export default {
  name: 'AuthIndex',
  components: {
    AddOrUpdate,
    AuditDialog
  },
  data() {
    return {
      loading: false,
      submitting: false,
      list: [],
      total: 0,
      query: emptyQuery(),
      dateRange: null,
      selectedIds: [],
      selectedRows: [],
      // 下拉数据源
      buildings: [],
      queryUnitOptions: [],
      queryHouseOptions: [],
      // 枚举
      auditTabs: [
        { value: null, label: '全部认证' },
        { value: 1, label: '待审核' },
        { value: 2, label: '已通过' },
        { value: 3, label: '已拒绝' }
      ],
      typeMap: { 1: '业主', 2: '家属', 3: '租户' },
      typeTagMap: { 1: 'info', 2: 'warning', 3: 'primary' },
      auditMap: { 1: '待审核', 2: '已通过', 3: '已拒绝' },
      auditTagMap: { 1: 'warning', 2: 'success', 3: 'danger' },
      // 添加/编辑弹窗
      addVisible: false,
      isEdit: false,
      editRecord: null,
      // 审核弹窗
      auditVisible: false,
      auditTitle: '审核',
      // 当前审核目标：{ ids: [], single: row|null }
      auditTarget: null
    }
  },
  created() {
    this.fetchBuildings()
    this.fetchList()
  },
  methods: {
    // 查询全部楼栋（楼栋下拉框数据源）
    async fetchBuildings() {
      try {
        const res = await Api.getBuildingList()
        this.buildings = res.data || []
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 根据楼栋ID生成单元下拉选项：值为 1、2...，显示为 1单元、2单元...
    async loadUnitOptions(buildingId) {
      if (!buildingId) {
        return []
      }
      const res = await Api.getBuildingById(buildingId)
      const count = (res.data && res.data.unitCount) || 0
      return Array.from({ length: count }, (_, i) => ({ value: String(i + 1), label: `${i + 1}单元` }))
    },

    // 查询区楼栋-单元二级联动
    async onQueryBuildingChange(buildingId) {
      this.query.unitNo = ''
      this.query.houseNumber = ''
      this.queryHouseOptions = []
      try {
        this.queryUnitOptions = await this.loadUnitOptions(buildingId)
      } catch (e) {
        this.queryUnitOptions = []
      }
    },

    // 查询区单元-房号三级联动
    async onQueryUnitChange(unitNo) {
      this.query.houseNumber = ''
      if (!unitNo) {
        this.queryHouseOptions = []
        return
      }
      try {
        const res = await Api.getHouseByBuildingAndUnit(this.query.buildingId, unitNo)
        const houses = res.data || []
        // 房号去重
        this.queryHouseOptions = [...new Set(houses.map(h => h.houseNumber))]
      } catch (e) {
        this.queryHouseOptions = []
      }
    },

    // 审核状态页签切换
    onTabChange(auditStatus) {
      this.query.auditStatus = auditStatus
      this.onSearch()
    },

    // 分页+条件查询认证列表
    async fetchList() {
      this.loading = true
      // 日期区间拆分：起始补 00:00:00，结束补 23:59:59，保证包含整日
      this.query.startDate = this.dateRange && this.dateRange[0] ? this.dateRange[0] + ' 00:00:00' : ''
      this.query.endDate = this.dateRange && this.dateRange[1] ? this.dateRange[1] + ' 23:59:59' : ''
      try {
        const res = await Api.getAuthPage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        this.$message.error('认证列表查询失败')
      } finally {
        this.loading = false
      }
    },

    onSearch() {
      this.query.pageNum = 1
      this.fetchList()
    },

    onReset() {
      this.query = emptyQuery()
      this.dateRange = null
      this.queryUnitOptions = []
      this.queryHouseOptions = []
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
      this.selectedRows = rows
      this.selectedIds = rows.map(row => row.authId)
    },

    clearSelection() {
      this.$refs.table.clearSelection()
      this.selectedRows = []
      this.selectedIds = []
    },

    // 当前登录管理员ID（审核操作需要）
    currentAdminId() {
      const admin = this.$store.getters['user/admin'] || {}
      return admin.adminId
    },

    // 认证材料：逗号分隔的多个地址
    materialList(url) {
      if (!url) {
        return []
      }
      return url.split(',').filter(u => u).map(u => this.resolveUrl(u))
    },

    firstMaterial(url) {
      if (!url) {
        return ''
      }
      return url.split(',').filter(u => u)[0] || ''
    },

    resolveUrl(url) {
      if (!url) {
        return ''
      }
      return url.startsWith('http') ? url : window.location.origin + url
    },

    // 时间格式化：2026-07-23 15:19
    formatTime(time) {
      if (!time) {
        return ''
      }
      const d = new Date(time)
      if (isNaN(d.getTime())) {
        return time
      }
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
    },

    // 打开添加弹窗
    onAdd() {
      this.isEdit = false
      this.editRecord = null
      this.addVisible = true
    },

    // 打开编辑弹窗（弹窗内根据 authId 调 getById 回显）
    onEdit(row) {
      this.isEdit = true
      this.editRecord = row
      this.addVisible = true
    },

    // 添加 / 编辑认证提交
    async onAddSubmit(form) {
      this.submitting = true
      try {
        if (this.isEdit) {
          await Api.updateAuth(form)
          this.$message.success('修改成功')
        } else {
          await Api.addAuth(form)
          this.$message.success('添加成功')
        }
        this.addVisible = false
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      } finally {
        this.submitting = false
      }
    },

    // 单条审核：打开审核弹窗
    onAudit(row) {
      this.auditTitle = '审核房屋认证'
      this.auditTarget = { ids: [row.authId], single: row }
      this.auditVisible = true
    },

    // 批量审核：校验选中后打开审核弹窗
    onBatchAudit() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要审核的认证记录')
        return
      }
      this.auditTitle = '批量审核房屋认证'
      this.auditTarget = { ids: [...this.selectedIds], single: null }
      this.auditVisible = true
    },

    // 审核弹窗确认：auditStatus 2-通过 3-拒绝
    async onAuditSubmit({ auditStatus, auditRemark }) {
      if (!this.auditTarget) {
        return
      }
      this.submitting = true
      try {
        const adminId = this.currentAdminId()
        if (this.auditTarget.single) {
          await Api.updateAuthAuditStatus({ authId: this.auditTarget.single.authId, auditStatus, auditRemark, adminId })
        } else {
          await Api.batchUpdateAuthAuditStatus(this.auditTarget.ids, { auditStatus, auditRemark, adminId })
        }
        this.$message.success('审核成功')
        this.auditVisible = false
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      } finally {
        this.submitting = false
      }
    },

    // 取消审核（单条）：确认后状态改回待审核
    async onCancelAudit(row) {
      try {
        await this.$confirm(
          `确定取消"${row.name || ''}"的房屋认证审核结果，恢复为待审核状态吗？`,
          '提示',
          { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
        )
      } catch (e) {
        return
      }
      try {
        await Api.updateAuthAuditStatus({
          authId: row.authId,
          auditStatus: 1,
          auditRemark: '',
          adminId: this.currentAdminId()
        })
        this.$message.success('已取消审核')
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 批量取消审核：确认后状态改回待审核
    async onBatchCancelAudit() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要取消审核的认证记录')
        return
      }
      try {
        await this.$confirm(
          `确定批量取消所选 ${this.selectedIds.length} 条房屋认证的审核结果，恢复为待审核状态吗？`,
          '提示',
          { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
        )
      } catch (e) {
        return
      }
      try {
        await Api.batchUpdateAuthAuditStatus(
          [...this.selectedIds],
          { auditStatus: 1, auditRemark: '', adminId: this.currentAdminId() }
        )
        this.$message.success('批量取消审核成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 删除单条认证
    async onDelete(row) {
      try {
        await this.$confirm(
          `确定删除"${row.name || ''}"对"${row.buildingName || ''}-${row.houseNumber || ''}"的认证吗？`,
          '提示',
          { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
        )
      } catch (e) {
        return
      }
      try {
        await Api.deleteAuth(row.authId)
        this.$message.success('删除成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 批量删除认证
    async onBatchDelete() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要删除的认证记录')
        return
      }
      try {
        await this.$confirm(`确定批量删除所选 ${this.selectedIds.length} 条认证记录吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        await Api.deleteAuthBatch([...this.selectedIds])
        this.$message.success('批量删除成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/auth/index.less';
</style>
