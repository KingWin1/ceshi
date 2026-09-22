<template>
  <el-dialog
    :title="isEdit ? '编辑管理员' : '新增管理员'"
    :visible="visible"
    width="560px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="form.username"
          :disabled="isEdit"
          placeholder="请输入用户名"
          maxlength="50"
        />
      </el-form-item>
      <el-form-item v-if="!isEdit" label="密码" prop="password">
        <el-input v-model="form.password" placeholder="请输入密码（默认 123456）" maxlength="50" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="form.name" placeholder="请输入姓名" maxlength="50" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="20" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="100" />
      </el-form-item>
      <el-form-item label="角色" prop="roleId">
        <el-select v-model="form.roleId" class="form-select" placeholder="请选择角色" clearable>
          <el-option
            v-for="item in roleList"
            :key="item.roleId"
            :label="item.roleName"
            :value="item.roleId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">正常</el-radio>
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
  adminId: null,
  username: '',
  password: '',
  name: '',
  phone: '',
  email: '',
  roleId: null,
  status: 1
})

export default {
  name: 'AddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    // 编辑时的管理员数据，新增传 null
    record: { type: Object, default: null },
    // 角色下拉数据源（父组件统一加载）
    roleList: { type: Array, default: () => [] },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm(),
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        phone: [
          {
            // 手机号非必填，填写时校验格式
            validator: (rule, value, callback) => {
              if (value && !/^1[3-9]\d{9}$/.test(value)) {
                callback(new Error('手机号格式不正确'))
                return
              }
              callback()
            },
            trigger: 'blur'
          }
        ],
        email: [
          {
            // 邮箱非必填，填写时校验格式
            validator: (rule, value, callback) => {
              if (value && !/^[\w.-]+@[\w-]+(\.[\w-]+)+$/.test(value)) {
                callback(new Error('邮箱格式不正确'))
                return
              }
              callback()
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  watch: {
    // 弹窗打开时回显数据
    visible(val) {
      if (val) {
        this.form = this.record ? { ...emptyForm(), ...this.record, password: '' } : emptyForm()
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
        const data = { ...this.form }
        // 编辑时不提交密码字段；新增时密码留空则后端使用默认值
        if (this.isEdit) {
          delete data.password
        } else if (!data.password) {
          data.password = '123456'
        }
        this.$emit('submit', data)
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
.form-select {
  width: 100%;
}
</style>
