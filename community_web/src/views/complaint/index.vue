<template>
  <div class="complaint-list">
    <!-- 查询区 -->
    <div class="search-card">
      <div class="search-row">
        <span class="search-label">关键词</span>
        <el-input
          v-model="query.keyword"
          class="w-240"
          placeholder="标题/居民/房号"
          clearable
          @keyup.enter.native="onSearch"
        />
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
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增</el-button>
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
        row-key="complaintId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column prop="complaintNo" label="编号" min-width="180" show-overflow-tooltip />
        <el-table-column label="标题" min-width="140" show-overflow-tooltip>
          <template slot-scope="{ row }">
            <el-button type="text" class="link-title" @click="onDetail(row)">{{ row.title }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="contactPerson" label="联系人" min-width="100">
          <template slot-scope="{ row }">{{ row.contactPerson || '-' }}</template>
        </el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" min-width="130">
          <template slot-scope="{ row }">{{ row.contactPhone || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" min-width="160">
          <template slot-scope="{ row }">{{ formatTime(row.submitTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" @click="onReply(row)">回复</el-button>
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

    <!-- 回复弹窗 -->
    <reply-dialog :visible.sync="replyVisible" :row="replyRow" @success="fetchList" />

    <!-- 详情弹窗（含回复记录与添加回复） -->
    <detail-dialog :visible.sync="detailVisible" :row="detailRow" @success="fetchList" />

    <!-- 新增投诉建议弹窗 -->
    <add-dialog :visible.sync="addVisible" @success="fetchList" />
  </div>
</template>

<script>
import Api from '@/api'
import ReplyDialog from './reply-dialog.vue'
import DetailDialog from './detail-dialog.vue'
import AddDialog from './add-dialog.vue'

// 查询条件初始值
const emptyQuery = () => ({
  keyword: '',
  status: null,
  pageNum: 1,
  pageSize: 10
})

export default {
  name: 'ComplaintIndex',
  components: {
    ReplyDialog,
    DetailDialog,
    AddDialog
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: emptyQuery(),
      selectedRows: [],
      // 状态枚举：1-待处理 2-处理中 3-已回复 4-已关闭
      statusOptions: [
        { value: 1, label: '待处理' },
        { value: 2, label: '处理中' },
        { value: 3, label: '已回复' },
        { value: 4, label: '已关闭' }
      ],
      // 回复弹窗
      replyVisible: false,
      replyRow: null,
      // 详情弹窗
      detailVisible: false,
      detailRow: null,
      // 新增弹窗
      addVisible: false
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    // 分页+条件查询投诉记录
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getComplaintRecordPage(this.query)
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
      return { 1: '待处理', 2: '处理中', 3: '已回复', 4: '已关闭' }[status] || '未知'
    },

    // 状态标签颜色：已回复-绿、待处理/处理中-橙、已关闭-灰
    statusTagType(status) {
      if (status === 3) return 'success'
      if (status === 1 || status === 2) return 'warning'
      return 'info'
    },

    // 时间格式化：2026-07-27 11:12
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
      this.addVisible = true
    },

    // 打开回复弹窗
    onReply(row) {
      this.replyRow = row
      this.replyVisible = true
    },

    // 打开详情弹窗：详情与回复记录由弹窗内调接口回显
    onDetail(row) {
      this.detailRow = row
      this.detailVisible = true
    },

    // 根据投诉ID数组批量删除投诉记录
    async onBatchDelete() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请先选择要删除的投诉建议')
        return
      }
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedRows.length} 条投诉建议吗？删除后不可恢复。`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const ids = this.selectedRows.map(r => r.complaintId)
        await Api.deleteComplaintRecordBatch(ids)
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
@import '@/assets/styles/complaint/index.less';
</style>
