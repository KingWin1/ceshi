<template>
  <el-dialog
    :visible="visible"
    title="导出账单"
    width="760px"
    append-to-body
    @close="onClose"
  >
    <el-form label-width="90px" class="export-form">
      <el-form-item label="导出范围" required>
        <el-radio-group v-model="form.exportRange">
          <el-radio :label="1">当前页</el-radio>
          <el-radio :label="2">全部数据</el-radio>
          <el-radio :label="3">按筛选条件</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="导出字段">
        <el-checkbox-group v-model="form.exportFields" class="field-grid">
          <el-checkbox
            v-for="f in fieldOptions"
            :key="f.value"
            class="field-item"
            :label="f.value"
          >{{ f.label }}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="文件格式">
        <el-radio-group v-model="form.fileFormat">
          <el-radio :label="1">Excel (.xlsx)</el-radio>
          <el-radio :label="2">CSV</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">确认导出</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/api'

// 默认勾选字段（与原型图一致）
const DEFAULT_FIELDS = [
  'billNo', 'buildingName', 'unitNo', 'houseNumber', 'fullHouseNo',
  'residentName', 'phone', 'feeTypeName', 'feeName', 'billingPeriod',
  'payableAmount', 'paidAmount', 'status', 'createTime'
]

export default {
  name: 'BillExportDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 当前筛选条件（导出范围为 1/3 时使用）
    query: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      loading: false,
      form: {
        exportRange: 2,
        exportFields: [...DEFAULT_FIELDS],
        fileFormat: 1
      },
      // 可选导出字段（与后端 getFieldMap 保持一致）
      fieldOptions: [
        { value: 'billNo', label: '账单编号' },
        { value: 'buildingName', label: '楼栋名称' },
        { value: 'unitNo', label: '单元' },
        { value: 'houseNumber', label: '房间号' },
        { value: 'fullHouseNo', label: '完整房号' },
        { value: 'residentName', label: '业主姓名' },
        { value: 'phone', label: '业主电话' },
        { value: 'feeTypeName', label: '费用类型' },
        { value: 'feeName', label: '费用名称' },
        { value: 'billingPeriod', label: '计费周期' },
        { value: 'startDate', label: '计费开始日期' },
        { value: 'endDate', label: '计费结束日期' },
        { value: 'area', label: '计费面积' },
        { value: 'unitPrice', label: '单价' },
        { value: 'billAmount', label: '账单金额' },
        { value: 'discountAmount', label: '优惠金额' },
        { value: 'payableAmount', label: '应付金额' },
        { value: 'paidAmount', label: '已付金额' },
        { value: 'status', label: '状态' },
        { value: 'payMethod', label: '支付方式' },
        { value: 'payTime', label: '支付时间' },
        { value: 'paySerialNo', label: '支付流水号' },
        { value: 'remindCount', label: '催缴次数' },
        { value: 'lastRemindTime', label: '最后催缴时间' },
        { value: 'remark', label: '备注' },
        { value: 'createTime', label: '生成时间' }
      ]
    }
  },
  methods: {
    // 确认导出：调用导出账单信息接口，接收文件流并触发下载
    onSubmit() {
      if (this.form.exportFields.length === 0) {
        this.$message.warning('请至少选择一个导出字段')
        return
      }
      this.loading = true
      const params = {
        exportRange: this.form.exportRange,
        exportFields: this.form.exportFields,
        fileFormat: this.form.fileFormat,
        queryDTO: this.query,
        pageNum: this.query.pageNum,
        pageSize: this.query.pageSize
      }
      Api.exportBill(params)
        .then(blob => this.handleFile(blob))
        .catch(() => {
          // 拦截器已提示
        })
        .finally(() => {
          this.loading = false
        })
    },

    // 处理文件流响应：后端异常时返回 JSON 而非文件流，需识别提示
    handleFile(blob) {
      if (!blob || blob.size === 0) {
        this.$message.error('导出失败：服务端未返回文件')
        return
      }
      if (blob.type && blob.type.indexOf('json') > -1) {
        const reader = new FileReader()
        reader.onload = () => {
          try {
            const res = JSON.parse(reader.result)
            this.$message.error(res.msg || '导出失败')
          } catch (e) {
            this.$message.error('导出失败')
          }
        }
        reader.readAsText(blob)
        return
      }
      const ext = this.form.fileFormat === 1 ? 'xlsx' : 'csv'
      const filename = `账单信息.${ext}`
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = filename
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      window.URL.revokeObjectURL(url)
      this.$message.success('导出成功')
      this.onClose()
    },

    onClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/bill/export-dialog.less';
</style>
