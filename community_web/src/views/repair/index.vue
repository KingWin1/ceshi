<template>
  <div class="repair-list">
    <!-- 查询区 -->
    <div class="search-card">
      <div class="search-row">
        <span class="search-label">关键词</span>
        <el-input
          v-model="query.keyword"
          class="w-240"
          placeholder="报修单号/联系人/电话/地址/备注"
          clearable
          @keyup.enter.native="onSearch"
        />
        <span class="search-label">报修类型</span>
        <el-select v-model="query.repairTypeId" class="w-130" placeholder="全部" clearable>
          <el-option v-for="t in repairTypes" :key="t.typeId" :label="t.typeName" :value="t.typeId" />
        </el-select>
        <span class="search-label">状态</span>
        <el-select v-model="query.status" class="w-130" placeholder="全部" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
        <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增报修</el-button>
        <el-button
          type="danger"
          icon="el-icon-delete"
          :disabled="selectedRows.length === 0"
          @click="onBatchDelete"
        >批量删除</el-button>
        <el-button icon="el-icon-refresh" @click="fetchList">刷新</el-button>
      </div>

      <el-table
        ref="table"
        v-loading="loading"
        :data="list"
        row-key="repairId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column label="报修单号" min-width="130" show-overflow-tooltip>
          <template slot-scope="{ row }">
            <el-button type="text" class="link-no" @click="onDetail(row)">{{ row.repairNo }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="reporterName" label="报修人" min-width="90">
          <template slot-scope="{ row }">{{ row.reporterName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" min-width="120">
          <template slot-scope="{ row }">{{ row.contactPhone || '-' }}</template>
        </el-table-column>
        <el-table-column prop="repairAddress" label="报修地址" min-width="140" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.repairAddress || '-' }}</template>
        </el-table-column>
        <el-table-column prop="repairTypeName" label="报修类型" min-width="90">
          <template slot-scope="{ row }">{{ row.repairTypeName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="description" label="问题描述" min-width="120" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.description || '-' }}</template>
        </el-table-column>
        <el-table-column prop="employeeName" label="派单员" min-width="90">
          <template slot-scope="{ row }">{{ row.employeeName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="evaluation" label="评价" min-width="90" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.evaluation || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="90">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报修时间" min-width="150">
          <template slot-scope="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="{ row }">
            <el-button v-if="row.status === 1" type="text" class="op-dispatch" @click="onDispatch(row)">派单</el-button>
            <el-button v-if="row.status === 2 || row.status === 3" type="text" class="op-complete" @click="onComplete(row)">完工</el-button>
            <el-button type="text" @click="onDetail(row)">详情</el-button>
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

    <!-- 派单弹窗 -->
    <dispatch-dialog
      :visible.sync="dispatchVisible"
      :row="dispatchRow"
      @success="fetchList"
    />

    <!-- 完工弹窗 -->
    <complete-dialog
      :visible.sync="completeVisible"
      :row="completeRow"
      @success="fetchList"
    />

    <!-- 报修单详情弹窗 -->
    <detail-dialog :visible.sync="detailVisible" :row="detailRow" />

    <!-- 新增报修弹窗 -->
    <add-dialog :visible.sync="addVisible" @success="fetchList" />
  </div>
</template>

<script>
import Api from '@/api'
import DispatchDialog from './dispatch-dialog.vue'
import DetailDialog from './detail-dialog.vue'
import CompleteDialog from './complete-dialog.vue'
import AddDialog from './add-dialog.vue'

// 查询条件初始值
const emptyQuery = () => ({
  keyword: '',
  repairTypeId: null,
  status: null,
  pageNum: 1,
  pageSize: 10
})

export default {
  name: 'RepairIndex',
  components: {
    DispatchDialog,
    DetailDialog,
    CompleteDialog,
    AddDialog
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: emptyQuery(),
      selectedRows: [],
      // 报修类型下拉数据源
      repairTypes: [],
      // 状态枚举：1-待派单 2-已派单 3-处理中 4-已完成 5-已取消
      statusOptions: [
        { value: 1, label: '待派单' },
        { value: 2, label: '已派单' },
        { value: 3, label: '处理中' },
        { value: 4, label: '已完成' },
        { value: 5, label: '已取消' }
      ],
      // 派单弹窗
      dispatchVisible: false,
      dispatchRow: null,
      // 完工弹窗
      completeVisible: false,
      completeRow: null,
      // 详情弹窗
      detailVisible: false,
      detailRow: null,
      // 新增报修弹窗
      addVisible: false
    }
  },
  created() {
    this.fetchRepairTypes()
    this.fetchList()
  },
  methods: {
    // 查询全部报修类型信息（报修类型下拉框数据源）
    async fetchRepairTypes() {
      try {
        const res = await Api.getRepairTypeListAll()
        this.repairTypes = res.data || []
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 分页+条件查询报修记录
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getRepairPage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        // 拦截器已提示
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
    },

    clearSelection() {
      this.$refs.table.clearSelection()
      this.selectedRows = []
    },

    statusText(status) {
      return { 1: '待派单', 2: '已派单', 3: '处理中', 4: '已完成', 5: '已取消' }[status] || '未知'
    },

    // 状态标签颜色：已完成-绿、已取消-红、待派单-橙、其他-灰
    statusTagType(status) {
      if (status === 4) return 'success'
      if (status === 5) return 'danger'
      if (status === 1) return 'warning'
      return 'info'
    },

    // 时间格式化：2026-08-17 15:24
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

    // 打开新增报修弹窗
    onAdd() {
      this.addVisible = true
    },

    // 打开完工弹窗
    onComplete(row) {
      this.completeRow = row
      this.completeVisible = true
    },

    // 批量删除报修记录
    async onBatchDelete() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请先选择要删除的报修记录')
        return
      }
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedRows.length} 条报修记录吗？删除后不可恢复。`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const ids = this.selectedRows.map(r => r.repairId)
        await Api.deleteRepairBatch(ids)
        this.$message.success('批量删除成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 打开派单弹窗
    onDispatch(row) {
      this.dispatchRow = row
      this.dispatchVisible = true
    },

    // 打开详情弹窗：报修数据由弹窗内调详情接口回显
    onDetail(row) {
      this.detailRow = row
      this.detailVisible = true
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/repair/index.less';
</style>
