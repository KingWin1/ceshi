<template>
  <div class="store-list">
    <!-- 查询区 -->
    <div class="search-card">
      <div class="search-row">
        <span class="search-label">关键字</span>
        <el-input
          v-model="query.keyword"
          class="w-240"
          placeholder="名称/地址/电话/介绍"
          clearable
          @keyup.enter.native="onSearch"
        />
        <span class="search-label">门店分类</span>
        <el-select v-model="query.categoryId" class="w-130" placeholder="全部分类" clearable>
          <el-option v-for="c in categoryList" :key="c.categoryId" :label="c.categoryName" :value="c.categoryId" />
        </el-select>
        <span class="search-label">状态</span>
        <el-select v-model="query.status" class="w-130" placeholder="全部" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <span class="search-label">推荐</span>
        <el-select v-model="query.isRecommend" class="w-130" placeholder="全部" clearable>
          <el-option v-for="r in recommendOptions" :key="r.value" :label="r.label" :value="r.value" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
        <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增门店</el-button>
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
        row-key="storeId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column label="门店Logo" width="100">
          <template slot-scope="{ row }">
            <el-image
              v-if="row.logoUrl"
              class="store-logo"
              :src="row.logoUrl"
              :preview-src-list="[row.logoUrl]"
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="门店名称" min-width="150">
          <template slot-scope="{ row }">
            <el-button type="text" class="link-title" @click="onDetail(row)">{{ row.storeName }}</el-button>
          </template>
        </el-table-column>
        <el-table-column label="门店分类" min-width="120">
          <template slot-scope="{ row }">{{ getCategoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" min-width="130">
          <template slot-scope="{ row }">{{ row.contactPhone || '-' }}</template>
        </el-table-column>
        <el-table-column prop="address" label="详细地址" min-width="200" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.address || '-' }}</template>
        </el-table-column>
        <el-table-column label="营业时间" min-width="140">
          <template slot-scope="{ row }">{{ formatBusinessTime(row.openTime, row.closeTime) }}</template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览次数" width="100" align="center">
          <template slot-scope="{ row }">{{ row.viewCount == null ? 0 : row.viewCount }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="90">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="推荐" min-width="90">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="row.isRecommend === 1 ? 'warning' : 'info'">{{ recommendText(row.isRecommend) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" @click="onEdit(row)">编辑</el-button>
            <el-button type="text" @click="onDetail(row)">查看</el-button>
            <el-button type="text" class="op-delete" @click="onDelete(row)">删除</el-button>
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

    <!-- 新增/编辑门店弹窗 -->
    <add-or-update :visible.sync="editVisible" :store-id="editStoreId" @success="fetchList" />

    <!-- 门店详情弹窗 -->
    <detail-dialog :visible.sync="detailVisible" :store-id="detailStoreId" />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'
import DetailDialog from './detail-dialog.vue'

// 查询条件初始值
const emptyQuery = () => ({
  keyword: '',
  categoryId: null,
  status: null,
  isRecommend: null,
  pageNum: 1,
  pageSize: 10
})

export default {
  name: 'StoreIndex',
  components: {
    AddOrUpdate,
    DetailDialog
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: emptyQuery(),
      selectedRows: [],
      categoryList: [],
      // 状态枚举：1-营业中 2-已下架
      statusOptions: [
        { value: 1, label: '营业中' },
        { value: 2, label: '已下架' }
      ],
      // 推荐枚举：1-推荐 2-普通
      recommendOptions: [
        { value: 1, label: '推荐' },
        { value: 2, label: '普通' }
      ],
      // 新增/编辑弹窗
      editVisible: false,
      editStoreId: null,
      // 详情弹窗
      detailVisible: false,
      detailStoreId: null
    }
  },
  created() {
    this.fetchList()
    this.fetchCategoryList()
  },
  methods: {
    // 分页+条件查询门店列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getStorePage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        // 拦截器已提示
      } finally {
        this.loading = false
      }
    },

    // 查询全部的门店分类信息
    async fetchCategoryList() {
      try {
        const res = await Api.getStoreCategoryListAll()
        this.categoryList = res.data || []
      } catch (e) {
        // 拦截器已提示
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

    getCategoryName(categoryId) {
      const cat = this.categoryList.find(c => c.categoryId === categoryId)
      return cat ? cat.categoryName : '-'
    },

    statusText(status) {
      return { 1: '营业中', 2: '已下架' }[status] || '未知'
    },

    // 状态标签颜色：营业中-绿、已下架-红
    statusTagType(status) {
      if (status === 1) return 'success'
      return 'danger'
    },

    recommendText(isRecommend) {
      return isRecommend === 1 ? '推荐' : '普通'
    },

    // 营业时间格式化：15:31-15:31
    formatBusinessTime(openTime, closeTime) {
      if (!openTime || !closeTime) {
        return '-'
      }
      const open = openTime.substring(0, 5)
      const close = closeTime.substring(0, 5)
      return `${open}-${close}`
    },

    // 打开新增弹窗
    onAdd() {
      this.editStoreId = null
      this.editVisible = true
    },

    // 打开编辑弹窗
    onEdit(row) {
      this.editStoreId = row.storeId
      this.editVisible = true
    },

    // 打开详情弹窗
    onDetail(row) {
      this.detailStoreId = row.storeId
      this.detailVisible = true
    },

    // 根据门店ID删除门店信息
    async onDelete(row) {
      try {
        await this.$confirm(`确定删除门店「${row.storeName}」吗？删除后不可恢复。`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        await Api.deleteStore(row.storeId)
        this.$message.success('删除成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 批量删除门店信息
    async onBatchDelete() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请先选择要删除的门店')
        return
      }
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedRows.length} 个门店吗？删除后不可恢复。`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const ids = this.selectedRows.map(r => r.storeId)
        await Api.deleteStoreBatch(ids)
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
@import '@/assets/styles/store/index.less';
</style>
