<template>
  <el-dialog
    title="计价设置"
    :visible="visible"
    width="560px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
    @closed="resetForm"
  >
    <div class="pricing-title">费用类型：<span>{{ record ? record.feeTypeName : '' }}</span></div>

    <el-form ref="form" :model="form" label-width="110px" class="pricing-form">
      <el-form-item label="按月单价">
        <el-input-number v-model="form.monthlyPrice" :min="0" :precision="2" :step="0.5" class="price-input" />
        <span class="price-unit">{{ unitText }}</span>
      </el-form-item>
      <el-form-item label="按季度单价">
        <el-input-number v-model="form.quarterlyPrice" :min="0" :precision="2" :step="0.5" class="price-input" />
        <span class="price-unit">{{ unitText }}</span>
      </el-form-item>
      <el-form-item label="按年单价">
        <el-input-number v-model="form.yearlyPrice" :min="0" :precision="2" :step="0.5" class="price-input" />
        <span class="price-unit">{{ unitText }}</span>
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
// 表单初始值
const emptyForm = () => ({
  monthlyPrice: 0,
  quarterlyPrice: 0,
  yearlyPrice: 0
})

export default {
  name: 'PricingDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 当前设置的费用类型行数据（含已有单价）
    record: { type: Object, default: null },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm()
    }
  },
  computed: {
    // 单价单位：按面积显示 元/㎡，固定金额显示 元
    unitText() {
      return this.record && this.record.billingMethod === 1 ? '元/㎡' : '元'
    }
  },
  watch: {
    // 弹窗打开时回显已有单价
    visible(val) {
      if (val && this.record) {
        this.form = {
          monthlyPrice: Number(this.record.monthlyPrice) || 0,
          quarterlyPrice: Number(this.record.quarterlyPrice) || 0,
          yearlyPrice: Number(this.record.yearlyPrice) || 0
        }
      }
    }
  },
  methods: {
    // 把单价参数交给父组件发起请求
    onSubmit() {
      const data = {
        feeTypeId: this.record.feeTypeId,
        monthlyPrice: this.form.monthlyPrice,
        quarterlyPrice: this.form.quarterlyPrice,
        yearlyPrice: this.form.yearlyPrice
      }
      this.$emit('submit', data)
    },

    resetForm() {
      this.form = emptyForm()
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/system/fee/pricing-dialog.less';
</style>
