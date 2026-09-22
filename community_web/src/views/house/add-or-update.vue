<template>
  <el-dialog
    :title="isEdit ? '编辑房间' : '新增房间'"
    :visible="visible"
    width="680px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="楼栋" prop="buildingId">
        <el-select
          v-model="form.buildingId"
          placeholder="请选择楼栋"
          class="w-full"
          @change="v => $emit('building-change', v)"
        >
          <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
        </el-select>
      </el-form-item>
      <el-form-item label="单元号">
        <el-select v-model="form.unitNo" placeholder="请选择单元" class="w-full" clearable>
          <el-option v-for="u in unitOptions" :key="u.value" :label="u.label" :value="u.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="房号" prop="houseNumber">
        <el-input v-model="form.houseNumber" class="w-220" placeholder="如: 101" maxlength="20" />
      </el-form-item>
      <el-form-item label="面积(㎡)">
        <el-input-number v-model="form.area" :min="0" :max="99999" :precision="2" :step="1" />
      </el-form-item>
      <el-form-item label="房型">
        <el-radio-group v-model="form.roomType">
          <el-radio :label="1">住宅</el-radio>
          <el-radio :label="2">商铺</el-radio>
          <el-radio :label="3">车位</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="户型">
        <div class="layout-group">
          <span class="layout-label">室</span>
          <el-input-number v-model="form.rooms" :min="0" :max="20" class="w-110" controls-position="right" />
          <span class="layout-label">厅</span>
          <el-input-number v-model="form.hall" :min="0" :max="20" class="w-110" controls-position="right" />
          <span class="layout-label">卫</span>
          <el-input-number v-model="form.toilet" :min="0" :max="20" class="w-110" controls-position="right" />
        </div>
      </el-form-item>
      <el-form-item label="朝向">
        <el-select v-model="form.orientation" placeholder="请选择朝向" class="w-full" clearable>
          <el-option v-for="o in orientationOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">空置</el-radio>
          <el-radio :label="2">已入住</el-radio>
          <el-radio :label="3">出租</el-radio>
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
  houseId: null,
  buildingId: null,
  unitNo: '',
  houseNumber: '',
  area: 0,
  roomType: 1,
  orientation: null,
  status: 1,
  rooms: 0,
  hall: 0,
  toilet: 0
})

export default {
  name: 'AddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    // 编辑时的房屋详情（含室/厅/卫），新增传 null
    record: { type: Object, default: null },
    // 楼栋下拉数据源
    buildings: { type: Array, default: () => [] },
    // 单元下拉数据源（由父组件按楼栋联动生成）
    unitOptions: { type: Array, default: () => [] },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm(),
      orientationOptions: [
        { value: 1, label: '东' },
        { value: 2, label: '南' },
        { value: 3, label: '西' },
        { value: 4, label: '北' },
        { value: 5, label: '东北' },
        { value: 6, label: '东南' },
        { value: 7, label: '西南' },
        { value: 8, label: '西北' }
      ],
      rules: {
        buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
        houseNumber: [{ required: true, message: '请输入房号', trigger: 'blur' }]
      }
    }
  },
  watch: {
    // 弹窗打开时回显数据；编辑时通知父组件按当前楼栋加载单元选项
    visible(val) {
      if (!val) {
        return
      }
      if (this.record) {
        this.form = { ...emptyForm(), ...this.record }
        this.$emit('building-change', this.form.buildingId)
      } else {
        this.form = emptyForm()
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

<style lang="less" scoped>
@import '@/assets/styles/house/add-or-update.less';
</style>
