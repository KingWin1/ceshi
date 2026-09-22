<template>
  <el-dialog
    :visible="visible"
    title="生成账单"
    width="820px"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <el-form label-width="90px" class="gen-form">
      <!-- 计费周期：按月/按季度/按年，右侧表单随选择变化 -->
      <el-form-item label="计费周期" required :error="periodError">
        <el-radio-group v-model="period" @change="onPeriodChange">
          <el-radio label="month">按月</el-radio>
          <el-radio label="quarter">按季度</el-radio>
          <el-radio label="year">按年</el-radio>
        </el-radio-group>
        <el-date-picker
          v-if="period === 'month'"
          v-model="monthValue"
          type="month"
          class="w-200"
          placeholder="选择月份"
          value-format="yyyy-MM"
        />
        <template v-if="period === 'quarter'">
          <el-date-picker v-model="quarterYear" type="year" class="w-200" placeholder="选择年份" value-format="yyyy" />
          <el-select v-model="quarterValue" class="w-200" placeholder="选择季度">
            <el-option v-for="q in [1, 2, 3, 4]" :key="q" :label="quarterLabel(q)" :value="q" />
          </el-select>
        </template>
        <el-date-picker
          v-if="period === 'year'"
          v-model="yearValue"
          type="year"
          class="w-200"
          placeholder="选择年份"
          value-format="yyyy"
        />
      </el-form-item>

      <!-- 费用类型：复选框，数据来自查询全部费用类型接口 -->
      <el-form-item label="费用类型" required :error="feeError">
        <el-checkbox-group v-model="checkedFeeIds" @change="onFeeCheckChange">
          <el-checkbox v-for="f in feeTypes" :key="f.feeTypeId" :label="f.feeTypeId">
            {{ f.feeTypeName }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <!-- 生成范围：所有楼栋/指定楼栋/指定房间 -->
      <el-form-item label="生成范围" required :error="scopeError">
        <el-radio-group v-model="scope" @change="onScopeChange">
          <el-radio label="all">所有楼栋</el-radio>
          <el-radio label="building">指定楼栋</el-radio>
          <el-radio label="house">指定房间</el-radio>
        </el-radio-group>
        <el-select
          v-if="scope === 'building'"
          v-model="scopeBuildingId"
          class="w-360"
          placeholder="请选择楼栋"
          clearable
        >
          <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
        </el-select>
        <el-button v-if="scope === 'house'" type="primary" class="w-360" @click="houseDialogVisible = true">
          选择房间 ({{ scopeHouses.length }})
        </el-button>
      </el-form-item>

      <!-- 计费设置：勾选费用类型后显示对应单价，可修改并同步后端 -->
      <template v-if="priceItems.length > 0">
        <div class="price-divider">计费设置</div>
        <el-form-item v-for="item in priceItems" :key="item.feeTypeId" :label="item.feeTypeName + '单价'">
          <el-input-number
            v-model="item.price"
            :min="0"
            :precision="2"
            :step="0.5"
            class="w-240"
            @change="val => onPriceChange(item, val)"
          />
          <span class="price-unit">{{ item.billingMethod === 1 ? '元/㎡' : '元' }}</span>
        </el-form-item>
      </template>
    </el-form>

    <!-- 预览结果 -->
    <div v-if="previewList.length > 0" class="preview-wrap">
      <div class="preview-title">账单预览（共 {{ previewList.length }} 条）</div>
      <el-table :data="previewList" max-height="260" border size="mini">
        <el-table-column prop="buildingName" label="楼栋" min-width="120" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.buildingName || '-' }}</template>
        </el-table-column>
        <el-table-column label="单元" min-width="70">
          <template slot-scope="{ row }">{{ row.unitNo ? row.unitNo + '单元' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="houseNumber" label="房号" min-width="70" />
        <el-table-column prop="feeTypeName" label="费用类型" min-width="90" />
        <el-table-column label="应付金额(元)" min-width="100">
          <template slot-scope="{ row }">
            <span class="amount">¥{{ Number(row.payableAmount).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="计费周期" min-width="90">
          <template slot-scope>{{ periodText }}</template>
        </el-table-column>
      </el-table>
    </div>

    <div slot="footer">
      <el-button type="primary" :loading="previewing" @click="onPreview">预览</el-button>
      <el-button class="btn-confirm" type="success" :disabled="!canConfirm" :loading="submitting" @click="onConfirm">
        确认生成
      </el-button>
      <el-button @click="onClose">取消</el-button>
    </div>

    <!-- 选择房间弹窗 -->
    <select-house-dialog
      :visible.sync="houseDialogVisible"
      :selected="scopeHouses"
      @confirm="onHouseConfirm"
    />
  </el-dialog>
</template>

<script>
import Api from '@/api'
import SelectHouseDialog from './select-house-dialog.vue'

// 计费周期字段名映射
const PRICE_FIELD = { month: 'monthlyPrice', quarter: 'quarterlyPrice', year: 'yearlyPrice' }

export default {
  name: 'BillGenerateDialog',
  components: {
    SelectHouseDialog
  },
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      previewing: false,
      submitting: false,
      // 计费周期
      period: 'month',
      monthValue: '',
      quarterYear: '',
      quarterValue: null,
      yearValue: '',
      periodError: '',
      // 费用类型
      feeTypes: [],
      checkedFeeIds: [],
      feeError: '',
      // 生成范围
      scope: 'all',
      buildings: [],
      scopeBuildingId: null,
      scopeHouses: [],
      scopeError: '',
      houseDialogVisible: false,
      // 计费设置（勾选的费用类型对应的单价）
      priceItems: [],
      // 预览结果（确认生成前禁用按钮）
      previewList: [],
      canConfirm: false
    }
  },
  computed: {
    // 计费周期文案：2026-09 / 2026-Q3 / 2026
    periodText() {
      if (this.period === 'month' && this.monthValue) {
        return this.monthValue
      }
      if (this.period === 'quarter' && this.quarterYear && this.quarterValue) {
        return `${this.quarterYear}-Q${this.quarterValue}`
      }
      if (this.period === 'year' && this.yearValue) {
        return this.yearValue
      }
      return '-'
    }
  },
  methods: {
    // 弹窗打开：加载费用类型、楼栋数据
    onOpen() {
      this.fetchFeeTypes()
      this.fetchBuildings()
    },

    async fetchFeeTypes() {
      try {
        const res = await Api.getFeeTypeList()
        this.feeTypes = (res.data || []).filter(f => f.status === 1)
      } catch (e) {
        // 拦截器已提示
      }
    },

    async fetchBuildings() {
      try {
        const res = await Api.getBuildingList()
        this.buildings = res.data || []
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 季度选项文案：2026年 第一季度(1-3月)
    quarterLabel(value) {
      const names = ['', '第一', '第二', '第三', '第四']
      const year = this.quarterYear || new Date().getFullYear()
      return `${year}年 ${names[value]}季度(${(value - 1) * 3 + 1}-${value * 3}月)`
    },

    // 周期切换：单价字段随周期变化，重新拉取已勾选费用类型的单价
    onPeriodChange() {
      this.monthValue = ''
      this.quarterYear = ''
      this.quarterValue = null
      this.yearValue = ''
      this.periodError = ''
      this.refreshPriceItems()
      this.invalidatePreview()
    },

    // 费用类型勾选变化：重新查询已勾选费用类型对应周期的单价
    onFeeCheckChange() {
      this.feeError = ''
      this.refreshPriceItems()
      this.invalidatePreview()
    },

    onScopeChange() {
      this.scopeBuildingId = null
      this.scopeHouses = []
      this.scopeError = ''
      this.invalidatePreview()
    },

    // 根据勾选的费用类型刷新计费设置：调 getById 查询对应周期的单价
    async refreshPriceItems() {
      const items = []
      for (const feeTypeId of this.checkedFeeIds) {
        const fee = this.feeTypes.find(f => f.feeTypeId === feeTypeId)
        if (!fee) {
          continue
        }
        try {
          const res = await Api.getFeeTypeById(feeTypeId)
          const info = res.data || fee
          items.push({
            feeTypeId,
            feeTypeName: info.feeTypeName,
            billingMethod: info.billingMethod,
            price: Number(info[PRICE_FIELD[this.period]] || 0)
          })
        } catch (e) {
          items.push({
            feeTypeId,
            feeTypeName: fee.feeTypeName,
            billingMethod: fee.billingMethod,
            price: Number(fee[PRICE_FIELD[this.period]] || 0)
          })
        }
      }
      this.priceItems = items
    },

    // 管理员修改单价：调 updatePrice 同步修改费用类型价格
    async onPriceChange(item, val) {
      if (val == null) {
        return
      }
      const params = { feeTypeId: item.feeTypeId }
      params[PRICE_FIELD[this.period]] = val
      try {
        await Api.updateFeeTypePrice(params)
        this.$message.success(`${item.feeTypeName}单价已更新`)
      } catch (e) {
        // 拦截器已提示
      }
      this.invalidatePreview()
    },

    onHouseConfirm(houses) {
      this.scopeHouses = houses
      this.invalidatePreview()
    },

    // 任何条件变化后需重新预览
    invalidatePreview() {
      this.canConfirm = false
      this.previewList = []
    },

    // 表单校验：周期、费用类型、生成范围
    validateForm() {
      this.periodError = ''
      this.feeError = ''
      this.scopeError = ''
      if (this.period === 'month' && !this.monthValue) {
        this.periodError = '请选择计费周期'
      } else if (this.period === 'quarter' && (!this.quarterYear || !this.quarterValue)) {
        this.periodError = '请选择计费周期'
      } else if (this.period === 'year' && !this.yearValue) {
        this.periodError = '请选择计费周期'
      }
      if (this.checkedFeeIds.length === 0) {
        this.feeError = '请至少选择一项费用类型'
      }
      if (this.scope === 'building' && !this.scopeBuildingId) {
        this.scopeError = '请选择楼栋'
      }
      if (this.scope === 'house' && this.scopeHouses.length === 0) {
        this.scopeError = '请选择房间'
      }
      return !this.periodError && !this.feeError && !this.scopeError
    },

    // 解析计费起止日期：按月-当月，按季度-当季，按年-全年
    resolveDateRange() {
      if (this.period === 'month') {
        const [y, m] = this.monthValue.split('-').map(Number)
        const start = new Date(y, m - 1, 1)
        const end = new Date(y, m, 0)
        return { start, end, periodValue: m }
      }
      if (this.period === 'quarter') {
        const y = Number(this.quarterYear)
        const startMonth = (this.quarterValue - 1) * 3
        const start = new Date(y, startMonth, 1)
        const end = new Date(y, startMonth + 3, 0)
        return { start, end, periodValue: this.quarterValue }
      }
      const y = Number(this.yearValue)
      return { start: new Date(y, 0, 1), end: new Date(y, 11, 31), periodValue: null }
    },

    // 目标房屋集合：所有楼栋-全部房屋，指定楼栋-该楼栋房屋，指定房间-所选房屋
    async resolveHouses() {
      if (this.scope === 'house') {
        return this.scopeHouses
      }
      if (this.scope === 'building') {
        const res = await Api.getHouseListByCondition({ buildingId: this.scopeBuildingId })
        return res.data || []
      }
      const res = await Api.getHouseListByCondition({})
      return res.data || []
    },

    // 单条账单金额：按面积 = 单价 × 面积，固定金额 = 单价
    calcAmount(item, area) {
      const base = item.billingMethod === 1 ? Number(area || 0) : 1
      return Number((item.price * base).toFixed(2))
    },

    // 预览：每个房屋 × 每个费用类型 单独生成一条账单
    async onPreview() {
      if (!this.validateForm()) {
        return
      }
      this.previewing = true
      try {
        const houses = await this.resolveHouses()
        if (houses.length === 0) {
          this.$message.warning('未查询到符合条件的房屋')
          return
        }
        const { start, end, periodValue } = this.resolveDateRange()
        const list = []
        houses.forEach(h => {
          this.priceItems.forEach(item => {
            const amount = this.calcAmount(item, h.area)
            list.push({
              houseId: h.houseId,
              buildingName: h.buildingName,
              unitNo: h.unitNo,
              houseNumber: h.houseNumber,
              feeTypeId: item.feeTypeId,
              feeTypeName: item.feeTypeName,
              billAmount: amount,
              payableAmount: amount,
              startDate: start,
              endDate: end,
              periodValue
            })
          })
        })
        this.previewList = list
        this.canConfirm = true
        this.$message.success(`预览成功，共 ${list.length} 条账单`)
      } catch (e) {
        // 拦截器已提示
      } finally {
        this.previewing = false
      }
    },

    // 确认生成：调用批量添加账单接口
    async onConfirm() {
      if (!this.canConfirm || this.previewList.length === 0) {
        return
      }
      this.submitting = true
      try {
        const bills = this.previewList.map(row => ({
          billNo: this.genBillNo(),
          houseId: row.houseId,
          feeTypeId: row.feeTypeId,
          billAmount: row.billAmount,
          discountAmount: 0,
          payableAmount: row.payableAmount,
          billingPeriod: this.period,
          billingPeriodValue: row.periodValue,
          startDate: this.fmtDate(row.startDate),
          endDate: this.fmtDate(row.endDate),
          status: 2
        }))
        await Api.batchAddBill(bills)
        this.$message.success(`生成成功，共 ${bills.length} 条账单`)
        this.$emit('success')
        this.onClose()
      } catch (e) {
        // 拦截器已提示
      } finally {
        this.submitting = false
      }
    },

    // 账单编号：B + 时间戳 + 4位随机数
    genBillNo() {
      return 'B' + Date.now() + Math.floor(1000 + Math.random() * 9000)
    },

    fmtDate(d) {
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
    },

    // 关闭时重置全部表单
    onClose() {
      this.period = 'month'
      this.monthValue = ''
      this.quarterYear = ''
      this.quarterValue = null
      this.yearValue = ''
      this.checkedFeeIds = []
      this.priceItems = []
      this.scope = 'all'
      this.scopeBuildingId = null
      this.scopeHouses = []
      this.previewList = []
      this.canConfirm = false
      this.periodError = ''
      this.feeError = ''
      this.scopeError = ''
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/bill/generate-dialog.less';
</style>
