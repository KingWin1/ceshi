<template>
  <el-dialog
    :visible="visible"
    title="派单"
    width="520px"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="选择员工" prop="employeeId">
        <el-select
          v-model="form.employeeId"
          class="w-full"
          placeholder="请选择员工"
          filterable
          :loading="loading"
        >
          <el-option
            v-for="e in employees"
            :key="e.employeeId"
            :label="`${e.employeeName}(${e.phone})`"
            :value="e.employeeId"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/api'

export default {
  name: 'RepairDispatchDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 待派单的报修行数据（提供 repairId）
    row: { type: Object, default: null }
  },
  data() {
    return {
      loading: false,
      submitting: false,
      employees: [],
      form: {
        employeeId: null
      },
      rules: {
        employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }]
      }
    }
  },
  methods: {
    // 弹窗打开时重置表单并加载全部员工（姓名(手机号)）
    onOpen() {
      this.form.employeeId = null
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
      this.fetchEmployees()
    },

    // 查询全部员工信息接口
    fetchEmployees() {
      this.loading = true
      Api.getEmployeeListAll()
        .then(res => {
          this.employees = res.data || []
        })
        .catch(() => {
          // 拦截器已提示
        })
        .finally(() => {
          this.loading = false
        })
    },

    // 派单提交：调用派单接口
    onSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.submitting = true
        Api.dispatchRepair({
          repairId: this.row.repairId,
          employeeId: this.form.employeeId
        })
          .then(() => {
            this.$message.success('派单成功')
            this.$emit('success')
            this.onClose()
          })
          .catch(() => {
            // 拦截器已提示
          })
          .finally(() => {
            this.submitting = false
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
@import '@/assets/styles/repair/dispatch-dialog.less';
</style>
