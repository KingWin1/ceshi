<template>
  <div class="house-list">
    <!-- 查询区 -->
    <div class="search-card">
      <div class="search-row">
        <span class="search-label">楼栋</span>
        <el-select v-model="query.buildingId" class="w-140" placeholder="全部" clearable @change="onQueryBuildingChange">
          <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
        </el-select>
        <span class="search-label">单元</span>
        <el-select v-model="query.unitNo" class="w-120" placeholder="全部" clearable :disabled="!query.buildingId">
          <el-option v-for="u in queryUnitOptions" :key="u.value" :label="u.label" :value="u.value" />
        </el-select>
        <span class="search-label">状态</span>
        <el-select v-model="query.status" class="w-120" placeholder="全部" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <span class="search-label">面积(㎡)</span>
        <el-input v-model="query.minArea" class="w-90" placeholder="最小" clearable />
        <span class="area-split">-</span>
        <el-input v-model="query.maxArea" class="w-90" placeholder="最大" clearable />
        <span class="search-label">户型</span>
        <el-select v-model="query.rooms" class="w-90" placeholder="室" clearable>
          <el-option v-for="r in layoutOptions" :key="r" :label="r + '室'" :value="r" />
        </el-select>
        <el-select v-model="query.hall" class="w-90" placeholder="厅" clearable>
          <el-option v-for="h in layoutOptions" :key="h" :label="h + '厅'" :value="h" />
        </el-select>
        <el-select v-model="query.toilet" class="w-90" placeholder="卫" clearable>
          <el-option v-for="t in layoutOptions" :key="t" :label="t + '卫'" :value="t" />
        </el-select>
      </div>
      <div class="search-row">
        <span class="search-label">朝向</span>
        <el-select v-model="query.orientation" class="w-120" placeholder="全部" clearable>
          <el-option v-for="o in orientationOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
        <span class="search-label">关键字</span>
        <el-input
          v-model="query.keyword"
          class="w-220"
          placeholder="业主姓名/电话/房号"
          clearable
          @keyup.enter.native="onSearch"
        />
        <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
        <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增房间</el-button>
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
        row-key="houseId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column prop="houseId" label="ID" min-width="70" />
        <el-table-column prop="buildingName" label="楼栋" min-width="110" />
        <el-table-column label="单元号" min-width="80">
          <template slot-scope="{ row }">{{ row.unitNo ? row.unitNo + '单元' : '' }}</template>
        </el-table-column>
        <el-table-column prop="houseNumber" label="房号" min-width="80" />
        <el-table-column label="面积" min-width="90">
          <template slot-scope="{ row }">{{ row.area != null ? row.area + '㎡' : '' }}</template>
        </el-table-column>
        <el-table-column prop="houseTypeDesc" label="户型" min-width="100" />
        <el-table-column label="朝向" min-width="70">
          <template slot-scope="{ row }">{{ orientationMap[row.orientation] || '' }}</template>
        </el-table-column>
        <el-table-column prop="ownerName" label="业主姓名" min-width="90" />
        <el-table-column prop="ownerPhone" label="业主电话" min-width="120" />
        <el-table-column label="状态" min-width="80">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="statusTagType[row.status] || 'info'">{{ statusMap[row.status] || '' }}</el-tag>
          </template>
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

    <!-- 新增 / 编辑弹窗 -->
    <add-or-update
      :visible.sync="dialogVisible"
      :is-edit="isEdit"
      :record="editRecord"
      :buildings="buildings"
      :unit-options="dialogUnitOptions"
      :submitting="submitting"
      @building-change="onDialogBuildingChange"
      @submit="onSubmit"
    />

    <!-- 批量删除确认弹窗 -->
    <batch-delete-dialog
      :visible.sync="batchVisible"
      :rows="batchRows"
      :submitting="submitting"
      @confirm="onBatchConfirm"
    />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'
import BatchDeleteDialog from './batch-delete-dialog.vue'

// 查询条件初始值
const emptyQuery = () => ({
  buildingId: null,
  unitNo: '',
  status: null,
  minArea: '',
  maxArea: '',
  rooms: null,
  hall: null,
  toilet: null,
  orientation: null,
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

export default {
  name: 'HouseIndex',
  components: {
    AddOrUpdate,
    BatchDeleteDialog
  },
  data() {
    return {
      loading: false,
      submitting: false,
      list: [],
      total: 0,
      query: emptyQuery(),
      selectedIds: [],
      // 下拉数据源
      buildings: [],
      queryUnitOptions: [],
      // 户型室/厅/卫可选数量（0-9）
      layoutOptions: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
      // 枚举映射
      orientationMap: { 1: '东', 2: '南', 3: '西', 4: '北', 5: '东北', 6: '东南', 7: '西南', 8: '西北' },
      statusMap: { 1: '空置', 2: '已入住', 3: '出租' },
      statusTagType: { 1: 'info', 2: 'success', 3: 'warning' },
      statusOptions: [
        { value: 1, label: '空置' },
        { value: 2, label: '已入住' },
        { value: 3, label: '出租' }
      ],
      orientationOptions: [
        { value: 1, label: '东' },
        { value: 2, label: '南' },
        { value: 3, label: '西' },
        { value: 4, label: '北' },
        { value: 5, label: '东北' },
        { value: 6, label: '东南' },
        { value: 7, label: '西南' },
        { value: 8, label: '西北' }
      ],
      // 新增/编辑弹窗
      dialogVisible: false,
      isEdit: false,
      editRecord: null,
      dialogUnitOptions: [],
      // 批量删除弹窗
      batchVisible: false,
      batchRows: []
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
      this.queryUnitOptions = await this.loadUnitOptions(buildingId)
    },

    // 弹窗内楼栋-单元二级联动
    async onDialogBuildingChange(buildingId) {
      this.dialogUnitOptions = await this.loadUnitOptions(buildingId)
    },

    // 分页+条件查询房屋列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getHousePage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        this.$message.error('房屋列表查询失败')
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
      this.queryUnitOptions = []
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
      this.selectedIds = rows.map(row => row.houseId)
    },

    clearSelection() {
      this.$refs.table.clearSelection()
      this.selectedIds = []
    },

    // 打开新增弹窗
    async onAdd() {
      this.isEdit = false
      this.editRecord = null
      this.dialogUnitOptions = []
      this.dialogVisible = true
    },

    // 编辑：根据ID查询房屋信息回显（含室/厅/卫）
    async onEdit(row) {
      try {
        const res = await Api.getHouseById(row.houseId)
        if (!res.data) {
          this.$message.error('房屋信息不存在')
          return
        }
        this.editRecord = res.data
        this.isEdit = true
        this.dialogUnitOptions = await this.loadUnitOptions(res.data.buildingId)
        this.dialogVisible = true
      } catch (e) {
        this.$message.error('房屋信息查询失败')
      }
    },

    // 弹窗提交：新增 / 修改
    async onSubmit(form) {
      this.submitting = true
      try {
        if (this.isEdit) {
          await Api.updateHouse(form)
          this.$message.success('修改成功')
        } else {
          await Api.addHouse(form)
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

    // 删除单个：先统计认证次数，后端删除时会同步解除认证关系
    async onDelete(row) {
      try {
        const res = await Api.countAuthByHouseId(row.houseId)
        const count = res.data || 0
        const tip = count > 0
          ? `该房屋已认证（${count} 条认证记录），删除时会同步解除房屋与居民的认证关系，是否继续？`
          : `确定删除房屋「${row.buildingName || ''} ${row.houseNumber || ''}」吗？`
        await this.$confirm(tip, '提示', {
          confirmButtonText: count > 0 ? '继续删除' : '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await Api.deleteHouse(row.houseId)
        this.$message.success('删除成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 取消或拦截器已提示
      }
    },

    // 批量删除：先统计每个房屋的认证情况，弹窗展示供用户勾选
    async onBatchDelete() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要删除的房屋')
        return
      }
      try {
        const res = await Api.countAuthByHouseIds(this.selectedIds)
        this.batchRows = res.data || []
        this.batchVisible = true
      } catch (e) {
        this.$message.error('房屋认证信息查询失败')
      }
    },

    // 批量删除弹窗确认：勾选的记录中存在认证房屋时二次确认
    onBatchConfirm(rows) {
      if (rows.length === 0) {
        this.$message.warning('请至少勾选一条要删除的房屋')
        return
      }
      const ids = rows.map(r => r.houseId)
      const hasAuth = rows.some(r => r.authCount > 0)
      if (hasAuth) {
        this.$confirm('被删除信息中存在已认证房屋，继续将同时删除其认证记录，是否继续？', '提示', {
          confirmButtonText: '继续',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => this.doBatchDelete(ids))
          .catch(() => {})
      } else {
        this.doBatchDelete(ids)
      }
    },

    // 后端批量删除时会同步删除认证记录
    async doBatchDelete(ids) {
      this.submitting = true
      try {
        await Api.deleteHouseBatch(ids)
        this.$message.success('批量删除成功')
        this.batchVisible = false
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/house/index.less';
</style>
