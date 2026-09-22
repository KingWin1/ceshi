<template>
  <el-dialog
    :title="isEdit ? '编辑费用类型' : '新增费用类型'"
    :visible="visible"
    width="560px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="名称" prop="feeTypeName">
        <el-input v-model="form.feeTypeName" placeholder="请输入费用类型名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="编码" prop="code">
        <el-input
          v-model="form.code"
          :disabled="isEdit"
          placeholder="请输入编码(如: PROPERTY)"
          maxlength="50"
        />
      </el-form-item>
      <el-form-item label="计费方式" prop="billingMethod">
        <el-radio-group v-model="form.billingMethod">
          <el-radio :label="1">按面积(元/㎡)</el-radio>
          <el-radio :label="2">固定金额(元)</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="form.sort" :min="0" :max="9999" class="sort-input" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="2">禁用</el-radio>
        </el-radio-group>
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
  feeTypeId: null,
  feeTypeName: '',
  code: '',
  billingMethod: 2,
  sort: 0,
  status: 1
})

export default {
  name: 'AddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    // 编辑时的费用类型数据，新增传 null
    record: { type: Object, default: null },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm(),
      rules: {
        feeTypeName: [{ required: true, message: '请输入费用类型名称', trigger: 'blur' }],
        code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
        billingMethod: [{ required: true, message: '请选择计费方式', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
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
        if (!valid) {
          return
        }
        this.$emit('submit', { ...this.form })
      })
    },

    resetForm() {
      this.$refs.form && this.$refs.form.clearValidate()
      this.form = emptyForm()
    }
  }
}
</script>

<style lang="less" scoped>
.sort-input {
  width: 100%;
}
</style>
