<template>
  <el-dialog
    :title="isEdit ? '编辑员工' : '新增员工'"
    :visible="visible"
    width="560px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="姓名" prop="employeeName">
        <el-input v-model="form.employeeName" placeholder="请输入姓名" maxlength="50" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="20" />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="form.gender">
          <el-radio :label="1">男</el-radio>
          <el-radio :label="2">女</el-radio>
          <el-radio :label="3">未知</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="部门" prop="deptId">
        <el-select
          v-model="form.deptId"
          class="form-select"
          placeholder="请选择部门"
          clearable
          @change="onDeptChange"
        >
          <el-option
            v-for="item in deptList"
            :key="item.deptId"
            :label="item.deptName"
            :value="item.deptId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="岗位" prop="positionId">
        <el-select
          v-model="form.positionId"
          class="form-select"
          placeholder="请选择岗位"
          clearable
          :disabled="!form.deptId"
        >
          <el-option
            v-for="item in positionList"
            :key="item.positionId"
            :label="item.positionName"
            :value="item.positionId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">在职</el-radio>
          <el-radio :label="2">离职</el-radio>
        </el-radio-group>
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
import Api from '@/api'

// 表单初始值
const emptyForm = () => ({
  employeeId: null,
  employeeName: '',
  phone: '',
  gender: 3,
  deptId: null,
  positionId: null,
  status: 1,
  remark: ''
})

export default {
  name: 'AddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    // 编辑时的员工数据，新增传 null
    record: { type: Object, default: null },
    // 部门下拉数据源（父组件统一加载）
    deptList: { type: Array, default: () => [] },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm(),
      // 岗位下拉（与部门二级联动）
      positionList: [],
      rules: {
        employeeName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
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
        ]
      }
    }
  },
  watch: {
    // 弹窗打开时回显数据，编辑时先加载部门对应岗位再赋值
    async visible(val) {
      if (!val) {
        return
      }
      if (this.record) {
        this.form = { ...emptyForm(), ...this.record }
        await this.loadPositions(this.form.deptId)
      } else {
        this.form = emptyForm()
        this.positionList = []
      }
    }
  },
  methods: {
    // 根据部门ID查询岗位列表（二级联动）
    async loadPositions(deptId) {
      if (!deptId) {
        this.positionList = []
        return
      }
      try {
        const res = await Api.getPositionByDeptId(deptId)
        this.positionList = res.data || []
      } catch (e) {
        this.positionList = []
      }
    },

    // 部门变化：清空已选岗位并重新加载岗位列表
    async onDeptChange(deptId) {
      this.form.positionId = null
      await this.loadPositions(deptId)
    },

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
      this.positionList = []
    }
  }
}
</script>

<style lang="less" scoped>
.form-select {
  width: 100%;
}
</style>
