<template>
  <el-dialog
    :title="isEdit ? '编辑客服' : '添加客服'"
    :visible="visible"
    width="560px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="客服姓名" prop="csName">
        <el-input v-model="form.csName" placeholder="请输入客服姓名" maxlength="50" />
      </el-form-item>
      <el-form-item label="职位" prop="position">
        <el-input v-model="form.position" placeholder="如：物业经理、客服专员" maxlength="50" />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="20" />
      </el-form-item>
      <el-form-item label="微信号" prop="wechat">
        <el-input v-model="form.wechat" placeholder="请输入微信号" maxlength="50" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="100" />
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
  csId: null,
  csName: '',
  position: '',
  phone: '',
  wechat: '',
  email: ''
})

export default {
  name: 'AddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    // 编辑时的客服数据，新增传 null
    record: { type: Object, default: null },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm(),
      rules: {
        csName: [{ required: true, message: '请输入客服姓名', trigger: 'blur' }],
        phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
        email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
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

<style lang="less" scoped></style>
