<template>
  <div class="bill-list">
    <!-- 查询区 -->
    <div class="search-card">
      <div class="search-row">
        <!-- 状态页签 -->
        <div class="type-tabs">
          <span
            v-for="tab in statusTabs"
            :key="String(tab.value)"
            :class="['type-tab', { active: query.status === tab.value }]"
            @click="onTabChange(tab.value)"
          >{{ tab.label }}</span>
        </div>
        <span class="search-label">关键字</span>
        <el-input
          v-model="query.keyword"
          class="w-180"
          placeholder="业主名/电话/备注"
          clearable
          @keyup.enter.native="onSearch"
        />
        <span class="search-label">楼栋</span>
        <el-select v-model="query.buildingId" class="w-130" placeholder="全部" clearable @change="onQueryBuildingChange">
          <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
        </el-select>
        <span class="search-label">单元</span>
        <el-select
          v-model="query.unitNo"
          class="w-120"
          placeholder="全部"
          clearable
          :disabled="!query.buildingId"
          @change="onQueryUnitChange"
        >
          <el-option v-for="u in queryUnitOptions" :key="u.value" :label="u.label" :value="u.value" />
        </el-select>
        <span class="search-label">房间号</span>
        <el-select
          v-model="query.houseNumber"
          class="w-120"
          placeholder="全部"
          clearable
          :disabled="!query.unitNo"
        >
          <el-option v-for="h in queryHouseOptions" :key="h" :label="h" :value="h" />
        </el-select>
        <span class="search-label">费用类型</span>
        <el-select v-model="query.feeTypeId" class="w-130" placeholder="全部" clearable>
          <el-option v-for="f in feeTypes" :key="f.feeTypeId" :label="f.feeTypeName" :value="f.feeTypeId" />
        </el-select>
      </div>
      <div class="search-row">
        <span class="search-label">计费周期</span>
        <el-select v-model="periodType" class="w-110" @change="onPeriodTypeChange">
          <el-option label="按月" value="month" />
          <el-option label="按季度" value="quarter" />
          <el-option label="按年" value="year" />
        </el-select>
        <!-- 按月：选择月份 -->
        <el-date-picker
          v-if="periodType === 'month'"
          v-model="monthValue"
          type="month"
          class="w-160"
          placeholder="选择月份"
          value-format="yyyy-MM"
        />
        <!-- 按季度：选择年份 + 季度 -->
        <template v-if="periodType === 'quarter'">
          <el-date-picker
            v-model="quarterYear"
            type="year"
            class="w-130"
            placeholder="选择年份"
            value-format="yyyy"
          />
          <el-select v-model="quarterValue" class="w-180" placeholder="选择季度">
            <el-option
              v-for="q in quarterOptions"
              :key="q.value"
              :label="quarterLabel(q.value)"
              :value="q.value"
            />
          </el-select>
        </template>
        <!-- 按年：选择年份 -->
        <el-date-picker
          v-if="periodType === 'year'"
          v-model="yearValue"
          type="year"
          class="w-160"
          placeholder="选择年份"
          value-format="yyyy"
        />
        <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
        <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="generateVisible = true">生成账单</el-button>
        <el-button
          class="btn-batch"
          type="warning"
          icon="el-icon-bell"
          :disabled="selectedRows.length === 0"
          @click="onBatchRemind"
        >批量催缴</el-button>
        <el-button class="btn-import" type="success" icon="el-icon-upload2" @click="importVisible = true">导入账单</el-button>
        <el-button class="btn-export" icon="el-icon-download" @click="exportVisible = true">导出账单</el-button>
        <el-button
          class="btn-delete"
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
        row-key="billId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column prop="buildingName" label="楼栋名" min-width="130" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.buildingName || '-' }}</template>
        </el-table-column>
        <el-table-column label="单元" min-width="80">
          <template slot-scope="{ row }">{{ row.unitNo ? row.unitNo + '单元' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="houseNumber" label="房间号" min-width="80" />
        <el-table-column prop="residentName" label="业主" min-width="90">
          <template slot-scope="{ row }">{{ row.residentName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="feeTypeName" label="费用类型" min-width="100">
          <template slot-scope="{ row }">{{ row.feeTypeName || '-' }}</template>
        </el-table-column>
        <el-table-column label="金额(元)" min-width="100">
          <template slot-scope="{ row }">
            <span class="amount">¥{{ formatAmount(row.payableAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="计费周期" min-width="100">
          <template slot-scope="{ row }">{{ periodText(row) }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="80">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '已缴' : '未缴' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="150">
          <template slot-scope="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" @click="onDetail(row)">详情</el-button>
            <el-button type="text" class="op-pay" :disabled="row.status === 1" @click="onPay(row)">缴费</el-button>
            <el-button type="text" class="op-remind" :disabled="row.status === 1" @click="onRemind(row)">催缴</el-button>
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

    <!-- 费用详情弹窗 -->
    <el-dialog :visible.sync="detailVisible" title="费用详情" width="760px" append-to-body>
      <div v-loading="detailLoading" class="detail-descriptions">
        <div class="detail-row">
          <span class="detail-label">账单编号</span><span class="detail-value">{{ detail.billNo || '-' }}</span>
          <span class="detail-label">状态</span>
          <span class="detail-value">
            <el-tag size="small" :type="detail.status === 1 ? 'success' : 'danger'">
              {{ detail.status === 1 ? '已缴' : '未缴' }}
            </el-tag>
          </span>
        </div>
        <div class="detail-row">
          <span class="detail-label">楼栋名称</span><span class="detail-value">{{ detail.buildingName || '-' }}</span>
          <span class="detail-label">单元</span><span class="detail-value">{{ detail.unitNo || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">房间号</span><span class="detail-value">{{ detail.houseNumber || '-' }}</span>
          <span class="detail-label">完整房号</span><span class="detail-value">{{ detail.fullHouseNo || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">业主姓名</span><span class="detail-value">{{ detail.residentName || '-' }}</span>
          <span class="detail-label">业主电话</span><span class="detail-value">{{ detail.phone || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">费用类型</span><span class="detail-value">{{ detail.feeTypeName || '-' }}</span>
          <span class="detail-label">费用名称</span><span class="detail-value">{{ detail.feeTypeName || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">计费周期</span><span class="detail-value">{{ periodText(detail) }}</span>
          <span class="detail-label">计费面积</span><span class="detail-value">{{ detail.area ? detail.area + ' ㎡' : '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">计费开始日期</span><span class="detail-value">{{ formatDate(detail.startDate) }}</span>
          <span class="detail-label">计费结束日期</span><span class="detail-value">{{ formatDate(detail.endDate) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">单价</span><span class="detail-value">¥{{ formatAmount(detail.unitPrice) }}</span>
          <span class="detail-label">账单金额</span><span class="detail-value amount">¥{{ formatAmount(detail.billAmount) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">优惠金额</span><span class="detail-value">¥{{ formatAmount(detail.discountAmount) }}</span>
          <span class="detail-label">应付金额</span><span class="detail-value amount-blue">¥{{ formatAmount(detail.payableAmount) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">已付金额</span><span class="detail-value">¥{{ formatAmount(detail.paidAmount) }}</span>
          <span class="detail-label">支付方式</span><span class="detail-value">{{ payMethodText(detail.payMethod) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">支付时间</span><span class="detail-value">{{ formatFullTime(detail.payTime) }}</span>
          <span class="detail-label">支付流水号</span><span class="detail-value">{{ detail.paySerialNo || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">催缴次数</span><span class="detail-value">{{ detail.remindCount || 0 }} 次</span>
          <span class="detail-label">最后催缴时间</span><span class="detail-value">{{ formatFullTime(detail.lastRemindTime) || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">创建时间</span><span class="detail-value">{{ formatFullTime(detail.createTime) }}</span>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 缴费弹窗 -->
    <pay-dialog
      :visible.sync="payVisible"
      :row="payBillRow"
      :submitting="submitting"
      @submit="onPaySubmit"
    />

    <!-- 生成账单弹窗 -->
    <generate-dialog :visible.sync="generateVisible" @success="fetchList" />

    <!-- 导出账单弹窗 -->
    <export-dialog :visible.sync="exportVisible" :query="query" />

    <!-- 导入账单弹窗 -->
    <import-dialog :visible.sync="importVisible" @success="fetchList" />
  </div>
</template>

<script>
import Api from '@/api'
import PayDialog from './pay-dialog.vue'
import GenerateDialog from './generate-dialog.vue'
import ExportDialog from './export-dialog.vue'
import ImportDialog from './import-dialog.vue'

// 查询条件初始值
const emptyQuery = () => ({
  status: null,
  keyword: '',
  buildingId: null,
  unitNo: '',
  houseNumber: '',
  feeTypeId: null,
  billingPeriod: '',
  billingPeriodValue: null,
  billingYear: null,
  pageNum: 1,
  pageSize: 10
})

export default {
  name: 'BillIndex',
  components: {
    PayDialog,
    GenerateDialog,
    ExportDialog,
    ImportDialog
  },
  data() {
    return {
      loading: false,
      submitting: false,
      list: [],
      total: 0,
      query: emptyQuery(),
      selectedRows: [],
      // 下拉数据源
      buildings: [],
      feeTypes: [],
      queryUnitOptions: [],
      queryHouseOptions: [],
      // 计费周期表单
      periodType: 'month',
      monthValue: '',
      quarterYear: '',
      quarterValue: null,
      yearValue: '',
      quarterOptions: [
        { value: 1 },
        { value: 2 },
        { value: 3 },
        { value: 4 }
      ],
      // 枚举
      statusTabs: [
        { value: null, label: '全部账单' },
        { value: 2, label: '未缴' },
        { value: 1, label: '已缴' }
      ],
      // 详情弹窗
      detailVisible: false,
      detailLoading: false,
      detail: {},
      // 缴费弹窗
      payVisible: false,
      payBillRow: null,
      // 生成账单弹窗
      generateVisible: false,
      // 导出/导入弹窗
      exportVisible: false,
      importVisible: false
    }
  },
  created() {
    this.fetchBuildings()
    this.fetchFeeTypes()
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

    // 查询全部费用类型（费用类型下拉框数据源）
    async fetchFeeTypes() {
      try {
        const res = await Api.getFeeTypeList()
        this.feeTypes = res.data || []
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

    // 状态页签切换
    onTabChange(status) {
      this.query.status = status
      this.onSearch()
    },

    // 计费周期类型切换：清空各类型已选值
    onPeriodTypeChange() {
      this.monthValue = ''
      this.quarterYear = ''
      this.quarterValue = null
      this.yearValue = ''
    },

    // 季度选项文案：2026年 第一季度(1-3月)
    quarterLabel(value) {
      const names = ['', '第一', '第二', '第三', '第四']
      const year = this.quarterYear || new Date().getFullYear()
      return `${year}年 ${names[value]}季度(${(value - 1) * 3 + 1}-${value * 3}月)`
    },

    // 组装计费周期查询参数：按月-年月，按季度-年+季度，按年-年份
    applyPeriodParams() {
      this.query.billingPeriod = ''
      this.query.billingPeriodValue = null
      this.query.billingYear = null
      if (this.periodType === 'month' && this.monthValue) {
        const [year, month] = this.monthValue.split('-')
        this.query.billingPeriod = 'month'
        this.query.billingYear = Number(year)
        this.query.billingPeriodValue = Number(month)
      } else if (this.periodType === 'quarter' && this.quarterYear && this.quarterValue) {
        this.query.billingPeriod = 'quarter'
        this.query.billingYear = Number(this.quarterYear)
        this.query.billingPeriodValue = this.quarterValue
      } else if (this.periodType === 'year' && this.yearValue) {
        this.query.billingPeriod = 'year'
        this.query.billingYear = Number(this.yearValue)
      }
    },

    // 分页+条件查询账单列表
    async fetchList() {
      this.loading = true
      this.applyPeriodParams()
      try {
        const res = await Api.getBillPage(this.query)
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
      this.periodType = 'month'
      this.monthValue = ''
      this.quarterYear = ''
      this.quarterValue = null
      this.yearValue = ''
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
    },

    clearSelection() {
      this.$refs.table.clearSelection()
      this.selectedRows = []
    },

    formatAmount(amount) {
      return Number(amount || 0).toFixed(2)
    },

    // 计费周期文案：month-2026-09 / quarter-2026-Q3 / year-2026
    periodText(row) {
      if (!row || !row.billingPeriod) {
        return '-'
      }
      const year = row.startDate ? new Date(row.startDate).getFullYear() : ''
      if (row.billingPeriod === 'month') {
        const m = row.billingPeriodValue
        return m ? `${year}-${String(m).padStart(2, '0')}` : String(year)
      }
      if (row.billingPeriod === 'quarter') {
        return `${year}-Q${row.billingPeriodValue || ''}`
      }
      return String(year)
    },

    // 时间格式化：2026-09-03 15:19
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

    // 日期格式化：2026-07-01
    formatDate(time) {
      if (!time) {
        return '-'
      }
      const d = new Date(time)
      if (isNaN(d.getTime())) {
        return time
      }
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
    },

    // 完整时间格式化：2026-08-31 17:18:39
    formatFullTime(time) {
      if (!time) {
        return ''
      }
      const d = new Date(time)
      if (isNaN(d.getTime())) {
        return time
      }
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    },

    // 支付方式文案
    payMethodText(payMethod) {
      const map = { 1: '微信支付', 2: '支付宝', 3: '现金', 4: '银行转账' }
      return map[payMethod] || '-'
    },

    // 批量删除：根据账单ID数组批量删除账单信息
    async onBatchDelete() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请先选择要删除的账单')
        return
      }
      try {
        await this.$confirm(`确定删除选中的 ${this.selectedRows.length} 条账单吗？删除后不可恢复。`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const ids = this.selectedRows.map(r => r.billId)
        await Api.deleteBillBatch(ids)
        this.$message.success('批量删除成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 打开详情弹窗：根据账单ID查询账单详情
    async onDetail(row) {
      this.detail = {}
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await Api.getBillDetail(row.billId)
        this.detail = res.data || {}
      } catch (e) {
        // 拦截器已提示
      } finally {
        this.detailLoading = false
      }
    },

    // 组装催缴消息内容：费用类型 + 应付金额 + 账单详细信息
    buildRemindContent(bill) {
      return (
        `您有一笔${bill.feeTypeName || '物业'}费用待缴费，` +
        `房号：${bill.fullHouseNo || '-'}，` +
        `账单编号：${bill.billNo || '-'}，` +
        `计费周期：${this.periodText(bill)}，` +
        `应付金额：¥${this.formatAmount(bill.payableAmount)}，` +
        `请及时缴费。`
      )
    },

    // 催缴（单条）：查询账单详情后调用添加消息（催缴）接口
    async onRemind(row) {
      if (!row.residentId) {
        this.$message.warning('该账单未关联业主，无法催缴')
        return
      }
      try {
        await this.$confirm(`确定向"${row.residentName || ''}"催缴该笔账单吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const res = await Api.getBillDetail(row.billId)
        const bill = res.data || row
        await Api.addMessage({
          billId: bill.billId,
          residentId: bill.residentId,
          title: '您有待缴费需要处理',
          content: this.buildRemindContent(bill)
        })
        this.$message.success('催缴成功')
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 批量催缴：逐条查询账单详情组装消息集合，调用批量添加消息接口
    async onBatchRemind() {
      const rows = this.selectedRows.filter(r => r.status !== 1)
      if (rows.length === 0) {
        this.$message.warning('所选账单均已缴费，无需催缴')
        return
      }
      const noResident = rows.filter(r => !r.residentId)
      if (noResident.length === rows.length) {
        this.$message.warning('所选账单均未关联业主，无法催缴')
        return
      }
      try {
        await this.$confirm(`确定批量催缴所选 ${rows.length - noResident.length} 条未缴账单吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const messages = []
        for (const row of rows) {
          if (!row.residentId) {
            continue
          }
          const res = await Api.getBillDetail(row.billId)
          const bill = res.data || row
          messages.push({
            billId: bill.billId,
            residentId: bill.residentId,
            title: '您有待缴费需要处理',
            content: this.buildRemindContent(bill)
          })
        }
        await Api.batchAddMessage(messages)
        this.$message.success('批量催缴成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 打开缴费弹窗：账单数据由弹窗内调详情接口回显
    onPay(row) {
      this.payBillRow = row
      this.payVisible = true
    },

    // 缴费提交：根据账单ID缴费接口
    async onPaySubmit({ billId, payMethod, paySerialNo }) {
      this.submitting = true
      try {
        await Api.payBill({ billId, payMethod, paySerialNo })
        this.$message.success('缴费成功')
        this.payVisible = false
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
@import '@/assets/styles/bill/index.less';
</style>
