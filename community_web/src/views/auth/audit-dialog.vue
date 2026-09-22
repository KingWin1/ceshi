<template>
  <el-dialog
    :visible="visible"
    :title="title"
    width="480px"
    append-to-body
    @close="onClose"
  >
    <el-form label-width="90px">
      <el-form-item label="审核结果" required>
        <el-radio-group v-model="form.auditStatus">
          <el-radio :label="2">通过</el-radio>
          <el-radio :label="3">拒绝</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核备注">
        <el-input
          v-model="form.auditRemark"
          type="textarea"
          :rows="4"
          maxlength="200"
          placeholder="请输入审核备注"
        />
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'AuthAuditDialog',
  props: {
    visible: { type: Boolean, default: false },
    title: { type: String, default: '审核房屋认证' },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: {
        auditStatus: 2,
        auditRemark: ''
      }
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.form = { auditStatus: 2, auditRemark: '' }
      }
    }
  },
  methods: {
    onSubmit() {
      this.$emit('submit', {
        auditStatus: this.form.auditStatus,
        auditRemark: this.form.auditRemark
      })
    },

    onClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
</style>
