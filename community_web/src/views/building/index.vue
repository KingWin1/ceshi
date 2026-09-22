<template>
  <div class="building-list">
    <!-- 查询区 -->
    <div class="search-card">
      <span class="search-label">关键词</span>
      <el-input
        v-model="query.buildingName"
        class="search-input"
        placeholder="楼栋名称"
        clearable
        @keyup.enter.native="onSearch"
      />
      <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
      <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增楼栋</el-button>
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
        row-key="buildingId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column prop="buildingId" label="ID" min-width="80" />
        <el-table-column prop="buildingName" label="楼栋名称" min-width="150" />
        <el-table-column prop="unitCount" label="单元数" min-width="90" />
        <el-table-column prop="floorCount" label="楼层数" min-width="90" />
        <el-table-column prop="totalRooms" label="房间总数" min-width="100" />
        <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
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
  name: 'BuildingIndex',
  components: {
    AddOrUpdate
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: {
        buildingName: '',
        pageNum: 1,
        pageSize: 10
      },
      // 当前勾选的楼栋ID
      selectedIds: [],
      // 弹窗状态
      dialogVisible: false,
      submitting: false,
      isEdit: false,
      // 编辑时回显的楼栋数据
      editRecord: null
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    // 分页+条件查询楼栋列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getBuildingPage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        this.$message.error('楼栋列表查询失败')
      } finally {
        this.loading = false
      }
    },

    onSearch() {
      this.query.pageNum = 1
      this.fetchList()
    },

    onReset() {
      this.query.buildingName = ''
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
      this.selectedIds = rows.map(row => row.buildingId)
    },

    // 打开新增弹窗
    onAdd() {
      this.isEdit = false
      this.editRecord = null
      this.dialogVisible = true
    },

    // 编辑：先根据ID查询楼栋信息回显
    async onEdit(row) {
      try {
        const res = await Api.getBuildingById(row.buildingId)
        if (!res.data) {
          this.$message.error('楼栋信息不存在')
          return
        }
        this.editRecord = res.data
        this.isEdit = true
        this.dialogVisible = true
      } catch (e) {
        this.$message.error('楼栋信息查询失败')
      }
    },

    // 弹窗提交：新增 / 修改
    async onSubmit(form) {
      this.submitting = true
      try {
        if (this.isEdit) {
          await Api.updateBuilding(form)
          this.$message.success('修改成功')
        } else {
          await Api.addBuilding(form)
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

    // 删除单个楼栋：确认框 -> 后端校验占用，被占用时拦截器提示"该楼栋被房屋占用不能删除"
    onDelete(row) {
      this.$confirm(`确定删除楼栋「${row.buildingName}」吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(async () => {
          await Api.deleteBuilding(row.buildingId)
          this.$message.success('删除成功')
          this.fetchList()
        })
        .catch(() => {})
    },

    // 批量删除：后端自动跳过被房屋占用的楼栋，前端提示占用明细
    onBatchDelete() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要删除的楼栋')
        return
      }
      this.$confirm(`确定删除选中的 ${this.selectedIds.length} 个楼栋吗？被房屋占用的楼栋不会被删除。`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(async () => {
          const res = await Api.deleteBuildingBatch(this.selectedIds)
          const data = res.data || {}
          const deletedCount = data.deletedCount || 0
          const occupiedNames = data.occupiedNames || []
          if (occupiedNames.length > 0) {
            // 存在被占用未删除的楼栋，明确提示用户
            this.$message({
              type: deletedCount > 0 ? 'warning' : 'error',
              message: `已删除 ${deletedCount} 个楼栋，以下楼栋被房屋占用不能删除：${occupiedNames.join('、')}`,
              duration: 5000
            })
          } else {
            this.$message.success(`成功删除 ${deletedCount} 个楼栋`)
          }
          this.$refs.table.clearSelection()
          this.selectedIds = []
          this.fetchList()
        })
        .catch(() => {})
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/building/index.less';
</style>
