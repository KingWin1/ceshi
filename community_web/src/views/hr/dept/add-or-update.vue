<template>
  <el-dialog
    :title="isEdit ? '编辑部门' : '新增部门'"
    :visible="visible"
    width="560px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="部门名称" prop="deptName">
        <el-input v-model="form.deptName" placeholder="请输入部门名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="排序号" prop="sort">
        <el-input-number v-model="form.sort" :min="0" :max="9999" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="4"
          maxlength="255"
          show-word-limit
          placeholder="请输入备注"
        />
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
// 表单初始值
const emptyForm = () => ({
  deptId: null,
  deptName: '',
  sort: 0,
  remark: ''
})

export default {
  name: 'AddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    // 编辑时的部门数据，新增传 null
    record: { type: Object, default: null },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm(),
      rules: {
        deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
      }
    }
  },
  watch: {
    // 弹窗打开时回显数据
    visible(val) {
      if (val) {
        this.form = this.record ? { ...emptyForm(), ...this.record } : emptyForm()
      }
    }
  },
  methods: {
    // 校验通过后把表单数据交给父组件发起请求
    onSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$emit('submit', { ...this.form })
        }
      })
    },

    resetForm() {
      this.$refs.form && this.$refs.form.clearValidate()
      this.form = emptyForm()
    }
  }
}
</script>
