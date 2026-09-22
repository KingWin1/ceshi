<template>
  <el-dialog
    :visible="visible"
    title="选择房间"
    width="1080px"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <!-- 查询条件：楼栋、单元、关键字 -->
    <div class="sh-search">
      <span class="sh-label">楼栋</span>
      <el-select v-model="query.buildingId" class="w-150" placeholder="全部" clearable @change="onBuildingChange">
        <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
      </el-select>
      <span class="sh-label">单元</span>
      <el-select v-model="query.unitNo" class="w-120" placeholder="全部" clearable :disabled="!query.buildingId">
        <el-option v-for="u in unitOptions" :key="u.value" :label="u.label" :value="u.value" />
      </el-select>
      <span class="sh-label">关键词</span>
      <el-input
        v-model="query.keyword"
        class="w-220"
        placeholder="业主姓名/电话/房号"
        clearable
        @keyup.enter.native="onSearch"
      />
    </div>
    <div class="sh-actions">
      <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
      <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
    </div>

    <!-- 房屋列表（多选） -->
    <el-table
      ref="table"
      v-loading="loading"
      :data="pagedList"
      row-key="houseId"
      max-height="420"
      @selection-change="onSelectionChange"
    >
      <el-table-column type="selection" width="45" reserve-selection />
      <el-table-column prop="buildingName" label="楼栋" min-width="150" show-overflow-tooltip>
        <template slot-scope="{ row }">{{ row.buildingName || '-' }}</template>
      </el-table-column>
      <el-table-column label="单元" min-width="80">
        <template slot-scope="{ row }">{{ row.unitNo ? row.unitNo + '单元' : '-' }}</template>
      </el-table-column>
      <el-table-column prop="houseNumber" label="房号" min-width="80" />
      <el-table-column label="面积(㎡)" min-width="90">
        <template slot-scope="{ row }">{{ row.area != null ? row.area : '-' }}</template>
      </el-table-column>
      <el-table-column prop="ownerName" label="业主" min-width="90">
        <template slot-scope="{ row }">{{ row.ownerName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="ownerPhone" label="电话" min-width="130">
        <template slot-scope="{ row }">{{ row.ownerPhone || '-' }}</template>
      </el-table-column>
    </el-table>

    <!-- 前端分页 -->
    <el-pagination
      class="sh-pagination"
      background
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      :total="houseList.length"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="onSizeChange"
      @current-change="onPageChange"
    />

    <div slot="footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" @click="onConfirm">确定 ({{ selectedRows.length }})</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/api'

export default {
  name: 'SelectHouseDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 已选房屋（打开时回显勾选）
    selected: { type: Array, default: () => [] }
  },
  data() {
    return {
      loading: false,
      buildings: [],
      unitOptions: [],
      houseList: [],
      selectedRows: [],
      query: {
        buildingId: null,
        unitNo: '',
        keyword: ''
      },
      pageNum: 1,
      pageSize: 10
    }
  },
  computed: {
    // 后端接口返回完整集合，弹窗内做前端分页
    pagedList() {
      const start = (this.pageNum - 1) * this.pageSize
      return this.houseList.slice(start, start + this.pageSize)
    }
  },
  methods: {
    // 弹窗打开：加载楼栋数据、回显已选房屋并查询
    onOpen() {
      this.fetchBuildings()
      this.$nextTick(() => {
        this.$refs.table.clearSelection()
        this.selectedRows = []
        // 回显外部已选中的房屋
        const preset = this.selected || []
        preset.forEach(h => {
          this.$refs.table.toggleRowSelection({ ...h }, true)
        })
      })
      this.onSearch()
    },

    async fetchBuildings() {
      try {
        const res = await Api.getBuildingList()
        this.buildings = res.data || []
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 楼栋-单元二级联动：单元下拉显示 1单元、2单元...
    async onBuildingChange(buildingId) {
      this.query.unitNo = ''
      if (!buildingId) {
        this.unitOptions = []
        return
      }
      try {
        const res = await Api.getBuildingById(buildingId)
        const count = (res.data && res.data.unitCount) || 0
        this.unitOptions = Array.from({ length: count }, (_, i) => ({
          value: String(i + 1),
          label: `${i + 1}单元`
        }))
      } catch (e) {
        this.unitOptions = []
      }
    },

    // 根据楼栋ID、单元号、关键字查询房屋信息
    async onSearch() {
      this.loading = true
      this.pageNum = 1
      try {
        const res = await Api.getHouseListByCondition({
          buildingId: this.query.buildingId || undefined,
          unitNo: this.query.unitNo || undefined,
          keyword: this.query.keyword || undefined
        })
        this.houseList = res.data || []
      } catch (e) {
        this.houseList = []
      } finally {
        this.loading = false
      }
    },

    onReset() {
      this.query = { buildingId: null, unitNo: '', keyword: '' }
      this.unitOptions = []
      this.onSearch()
    },

    onSizeChange(size) {
      this.pageSize = size
      this.pageNum = 1
    },

    onPageChange(page) {
      this.pageNum = page
    },

    onSelectionChange(rows) {
      this.selectedRows = rows
    },

    onConfirm() {
      this.$emit('confirm', this.selectedRows.slice())
      this.onClose()
    },

    onClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/bill/select-house-dialog.less';
</style>
