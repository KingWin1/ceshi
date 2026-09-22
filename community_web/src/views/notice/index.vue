<template>
  <div class="notice-list">
    <!-- 查询区 -->
    <div class="search-card">
      <div class="search-row">
        <span class="search-label">标题</span>
        <el-input
          v-model="query.title"
          class="w-240"
          placeholder="公告标题"
          clearable
          @keyup.enter.native="onSearch"
        />
        <span class="search-label">状态</span>
        <el-select v-model="query.status" class="w-130" placeholder="全部" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <span class="search-label">是否置顶</span>
        <el-select v-model="query.isTop" class="w-130" placeholder="全部" clearable>
          <el-option v-for="t in topOptions" :key="t.value" :label="t.label" :value="t.value" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
        <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新建公告</el-button>
        <el-button
          class="btn-success"
          :disabled="selectedRows.length === 0"
          @click="onBatchStatus(1)"
        >批量发布</el-button>
        <el-button
          class="btn-warning"
          :disabled="selectedRows.length === 0"
          @click="onBatchStatus(2)"
        >批量下架</el-button>
        <el-button
          type="info"
          :disabled="selectedRows.length === 0"
          @click="onBatchUntop"
        >批量取消置顶</el-button>
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
        row-key="noticeId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column label="标题" min-width="260" show-overflow-tooltip>
          <template slot-scope="{ row }">
            <el-button type="text" class="link-title" @click="onEdit(row)">{{ row.title }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览次数" min-width="100" align="center">
          <template slot-scope="{ row }">{{ row.viewCount == null ? 0 : row.viewCount }}</template>
        </el-table-column>
        <el-table-column label="发布时间" min-width="160">
          <template slot-scope="{ row }">{{ formatTime(row.publishTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button v-if="row.status === 2" type="text" class="op-republish" @click="onUpdateStatus(row, 1, '重新发布')">重新发布</el-button>
            <el-button v-if="row.status === 1" type="text" class="op-offline" @click="onUpdateStatus(row, 2, '下架')">下架</el-button>
            <el-button type="text" icon="el-icon-edit" @click="onEdit(row)">编辑</el-button>
            <el-button type="text" class="op-delete" icon="el-icon-delete" @click="onDelete(row)">删除</el-button>
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

    <!-- 新增/编辑公告弹窗 -->
    <add-or-update :visible.sync="editVisible" :notice-id="editNoticeId" @success="fetchList" />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'

// 查询条件初始值
const emptyQuery = () => ({
  title: '',
  status: null,
  isTop: null,
  pageNum: 1,
  pageSize: 10
})

export default {
  name: 'NoticeIndex',
  components: {
    AddOrUpdate
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: emptyQuery(),
      selectedRows: [],
      // 状态枚举：1-已发布 2-已下架
      statusOptions: [
        { value: 1, label: '已发布' },
        { value: 2, label: '已下架' }
      ],
      // 是否置顶枚举：1-是 2-否
      topOptions: [
        { value: 1, label: '已置顶' },
        { value: 2, label: '未置顶' }
      ],
      // 新增/编辑弹窗
      editVisible: false,
      editNoticeId: null
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    // 分页+条件查询公告列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getNoticePage(this.query)
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
      return { 1: '已发布', 2: '已下架' }[status] || '未知'
    },

    // 状态标签颜色：已发布-绿、已下架-红
    statusTagType(status) {
      if (status === 1) return 'success'
      return 'danger'
    },

    // 时间格式化：2026-07-23 15:07
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
      this.editNoticeId = null
      this.editVisible = true
    },

    // 打开编辑弹窗（点击标题或编辑按钮）
    onEdit(row) {
      this.editNoticeId = row.noticeId
      this.editVisible = true
    },

    // 单条修改公告状态（重新发布/下架）
    async onUpdateStatus(row, status, actionText) {
      try {
        await this.$confirm(`确定要${actionText}该公告吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        await Api.updateNoticeStatus({ noticeId: row.noticeId, status })
        this.$message.success(`${actionText}成功`)
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 根据ID删除公告
    async onDelete(row) {
      try {
        await this.$confirm(`确定删除公告「${row.title}」吗？删除后不可恢复。`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        await Api.deleteNotice(row.noticeId)
        this.$message.success('删除成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 批量删除公告
    async onBatchDelete() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请先选择要删除的公告')
        return
      }
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedRows.length} 条公告吗？删除后不可恢复。`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const ids = this.selectedRows.map(r => r.noticeId)
        await Api.deleteNoticeBatch(ids)
        this.$message.success('批量删除成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 批量修改公告状态（批量发布/批量下架）
    async onBatchStatus(status) {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请先选择要操作的公告')
        return
      }
      const actionText = status === 1 ? '发布' : '下架'
      try {
        await this.$confirm(`确定${actionText}选中的 ${this.selectedRows.length} 条公告吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const ids = this.selectedRows.map(r => r.noticeId)
        await Api.batchUpdateNoticeStatus(ids, status)
        this.$message.success(`批量${actionText}成功`)
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 批量取消置顶
    async onBatchUntop() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请先选择要取消置顶的公告')
        return
      }
      try {
        await this.$confirm(`确定取消选中 ${this.selectedRows.length} 条公告的置顶吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const ids = this.selectedRows.map(r => r.noticeId)
        await Api.batchUpdateNoticeTop(ids, 2)
        this.$message.success('批量取消置顶成功')
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
@import '@/assets/styles/notice/index.less';
</style>
