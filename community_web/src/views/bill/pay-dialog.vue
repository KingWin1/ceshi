<template>
  <el-dialog
    :visible="visible"
    title="缴费"
    width="620px"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <!-- 账单信息回显：根据账单ID查询账单详情接口 -->
    <div v-loading="loading" class="pay-descriptions">
      <div class="pay-row">
        <span class="pay-label">账单编号</span><span class="pay-value">{{ bill.billNo || '-' }}</span>
        <span class="pay-label">费用类型</span><span class="pay-value">{{ bill.feeTypeName || '-' }}</span>
      </div>
      <div class="pay-row">
        <span class="pay-label">完整房号</span><span class="pay-value" :span="3">{{ bill.fullHouseNo || '-' }}</span>
      </div>
      <div class="pay-row">
        <span class="pay-label">业主</span><span class="pay-value">{{ bill.residentName || '-' }}</span>
        <span class="pay-label">业主电话</span><span class="pay-value">{{ bill.phone || '-' }}</span>
      </div>
      <div class="pay-row">
        <span class="pay-label">计费周期</span><span class="pay-value">{{ periodText }}</span>
        <span class="pay-label">应付金额</span><span class="pay-value amount">¥{{ formatAmount(bill.payableAmount) }}</span>
      </div>
    </div>

    <el-form ref="form" :model="form" :rules="rules" label-width="90px" class="pay-form">
      <el-form-item label="支付方式" prop="payMethod">
        <el-select v-model="form.payMethod" class="w-full" placeholder="请选择支付方式">
          <el-option v-for="m in payMethods" :key="m.value" :label="m.label" :value="m.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="支付流水号">
        <el-input v-model="form.paySerialNo" placeholder="请输入支付流水号（选填）" clearable />
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">确认缴费</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/api'

export default {
  name: 'BillPayDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 列表行数据（提供 billId）
    row: { type: Object, default: null },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      loading: false,
      detail: {},
      form: {
        payMethod: null,
        paySerialNo: ''
      },
      payMethods: [
        { value: 1, label: '微信支付' },
        { value: 2, label: '支付宝' },
        { value: 3, label: '现金' },
        { value: 4, label: '银行转账' }
      ],
      rules: {
        payMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
      }
    }
  },
  computed: {
    // 弹窗展示数据：详情接口返回优先，未返回时用行数据兜底
    bill() {
      return Object.assign({}, this.row || {}, this.detail)
    },
    periodText() {
      const row = this.bill
      if (!row.billingPeriod) {
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
    }
  },
  methods: {
    formatAmount(amount) {
      return Number(amount || 0).toFixed(2)
    },

    // 弹窗打开时回显账单详情
    onOpen() {
      this.detail = {}
      this.form = { payMethod: null, paySerialNo: '' }
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
      if (!this.row || !this.row.billId) {
        return
      }
      this.loading = true
      Api.getBillDetail(this.row.billId)
        .then(res => {
          this.detail = res.data || {}
        })
        .catch(() => {
          // 拦截器已提示
        })
        .finally(() => {
          this.loading = false
        })
    },

    onSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.$emit('submit', {
          billId: this.row.billId,
          payMethod: this.form.payMethod,
          paySerialNo: this.form.paySerialNo
        })
      })
    },

    onClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/bill/pay-dialog.less';
</style>
