<template>
  <div class="ad-list">
    <!-- 查询区 -->
    <div class="search-card">
      <div class="search-row">
        <span class="search-label">广告类型</span>
        <el-select v-model="query.adType" class="w-130" placeholder="全部" clearable>
          <el-option v-for="t in adTypeOptions" :key="t.value" :label="t.label" :value="t.value" />
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
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增广告</el-button>
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
        row-key="adId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column label="广告图片" width="100">
          <template slot-scope="{ row }">
            <el-image
              v-if="row.imageUrl"
              class="ad-image"
              :src="row.imageUrl"
              :preview-src-list="[row.imageUrl]"
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="广告标题" min-width="180" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.title || '-' }}</template>
        </el-table-column>
        <el-table-column label="广告类型" min-width="120">
          <template slot-scope="{ row }">{{ adTypeText(row.adType) }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center">
          <template slot-scope="{ row }">{{ row.sort == null ? 0 : row.sort }}</template>
        </el-table-column>
        <el-table-column prop="showCount" label="展示次数" width="100" align="center">
          <template slot-scope="{ row }">{{ row.showCount == null ? 0 : row.showCount }}</template>
        </el-table-column>
        <el-table-column prop="clickCount" label="点击次数" width="100" align="center">
          <template slot-scope="{ row }">{{ row.clickCount == null ? 0 : row.clickCount }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="90">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="190" fixed="right">
          <template slot-scope="{ row }">
            <el-button v-if="row.status === 2" type="text" class="op-enable" @click="onUpdateStatus(row, 1, '启用')">启用</el-button>
            <el-button v-if="row.status === 1" type="text" class="op-disable" @click="onUpdateStatus(row, 2, '停用')">停用</el-button>
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

    <!-- 新增/编辑广告弹窗 -->
    <add-or-update :visible.sync="editVisible" :ad-id="editAdId" @success="fetchList" />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'

// 查询条件初始值
const emptyQuery = () => ({
  adType: null,
  status: null,
  pageNum: 1,
  pageSize: 10
})

export default {
  name: 'AdvertisementIndex',
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
      // 广告类型枚举：1-启动页 2-首页轮播图 3-弹窗广告
      adTypeOptions: [
        { value: 1, label: '启动页' },
        { value: 2, label: '首页轮播图' },
        { value: 3, label: '弹窗广告' }
      ],
      // 状态枚举：1-启用 2-停用
      statusOptions: [
        { value: 1, label: '启用' },
        { value: 2, label: '停用' }
      ],
      // 新增/编辑弹窗
      editVisible: false,
      editAdId: null
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    // 分页+条件查询广告列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getAdvertisementPage(this.query)
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

    adTypeText(adType) {
      return { 1: '启动页', 2: '首页轮播图', 3: '弹窗广告' }[adType] || '-'
    },

    statusText(status) {
      return { 1: '启用', 2: '停用' }[status] || '未知'
    },

    // 状态标签颜色：启用-绿、停用-红
    statusTagType(status) {
      if (status === 1) return 'success'
      return 'danger'
    },

    // 打开新增弹窗
    onAdd() {
      this.editAdId = null
      this.editVisible = true
    },

    // 打开编辑弹窗（详情由弹窗内调接口回显）
    onEdit(row) {
      this.editAdId = row.adId
      this.editVisible = true
    },

    // 启用/停用：根据ID修改广告状态
    async onUpdateStatus(row, status, actionText) {
      try {
        await this.$confirm(`确定要${actionText}该广告吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        await Api.updateAdvertisementStatus({ adId: row.adId, status })
        this.$message.success(`${actionText}成功`)
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 根据ID删除广告
    async onDelete(row) {
      try {
        await this.$confirm(`确定删除广告「${row.title}」吗？删除后不可恢复。`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        await Api.deleteAdvertisement(row.adId)
        this.$message.success('删除成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 批量删除广告
    async onBatchDelete() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请先选择要删除的广告')
        return
      }
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedRows.length} 条广告吗？删除后不可恢复。`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const ids = this.selectedRows.map(r => r.adId)
        await Api.deleteAdvertisementBatch(ids)
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
@import '@/assets/styles/advertisement/index.less';
</style>
