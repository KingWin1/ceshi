<template>
  <el-dialog
    :title="isEdit ? '编辑楼栋' : '新增楼栋'"
    :visible="visible"
    width="560px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="楼栋名称" prop="buildingName">
        <el-input v-model="form.buildingName" placeholder="请输入楼栋名称，如: 芙蓉苑1号" maxlength="50" />
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="单元数" prop="unitCount">
            <el-input-number v-model="form.unitCount" :min="1" :max="99" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="楼层数" prop="floorCount">
            <el-input-number v-model="form.floorCount" :min="1" :max="99" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="房间总数" prop="totalRooms">
        <el-input-number v-model="form.totalRooms" :min="0" :max="9999" />
      </el-form-item>
      <el-form-item label="楼栋描述">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          maxlength="200"
          show-word-limit
          placeholder="请输入楼栋描述"
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
  buildingId: null,
  buildingName: '',
  unitCount: 1,
  floorCount: 1,
  totalRooms: 0,
  description: ''
})

export default {
  name: 'AddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    // 编辑时的楼栋数据，新增传 null
    record: { type: Object, default: null },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm(),
      rules: {
        buildingName: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
        unitCount: [{ required: true, message: '请输入单元数', trigger: 'blur' }],
        floorCount: [{ required: true, message: '请输入楼层数', trigger: 'blur' }],
        totalRooms: [{ required: true, message: '请输入房间总数', trigger: 'blur' }]
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
