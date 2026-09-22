<template>
  <el-dialog
    :visible="visible"
    :title="isEdit ? '修改房屋认证' : '添加房屋认证'"
    width="560px"
    append-to-body
    @close="onClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <!-- 居民：远程搜索，显示 姓名(手机号) -->
      <el-form-item label="居民" prop="residentId">
        <el-select
          v-model="form.residentId"
          class="w-full"
          placeholder="请搜索居民姓名或电话"
          filterable
          remote
          reserve-keyword
          clearable
          :remote-method="searchResident"
          :loading="residentLoading"
        >
          <el-option
            v-for="r in residentOptions"
            :key="r.residentId"
            :label="r.name + ' (' + r.phone + ')'"
            :value="r.residentId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="楼栋" prop="buildingId">
        <el-select
          v-model="form.buildingId"
          class="w-full"
          placeholder="请选择楼栋"
          clearable
          @change="onBuildingChange"
        >
          <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
        </el-select>
      </el-form-item>

      <el-form-item label="单元" prop="unitNo">
        <el-select
          v-model="form.unitNo"
          class="w-full"
          placeholder="请选择单元"
          clearable
          :disabled="!form.buildingId"
          @change="onUnitChange"
        >
          <el-option v-for="u in unitOptions" :key="u.value" :label="u.label" :value="u.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="房间" prop="houseId">
        <el-select
          v-model="form.houseId"
          class="w-full"
          placeholder="请选择房间"
          clearable
          :disabled="!form.unitNo"
        >
          <el-option v-for="h in houseOptions" :key="h.houseId" :label="h.houseNumber" :value="h.houseId" />
        </el-select>
      </el-form-item>

      <el-form-item label="关系类型" prop="type">
        <el-select v-model="form.type" class="w-full" placeholder="请选择关系类型">
          <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="主房屋">
        <el-switch v-model="form.isMain" :active-value="1" :inactive-value="2" active-text="是" inactive-text="否" />
      </el-form-item>

      <el-form-item label="认证材料">
        <el-upload
          class="material-upload"
          action="/api/file/upload"
          name="files"
          accept=".jpg,.jpeg,.png,.pdf"
          multiple
          :limit="10"
          :headers="uploadHeaders"
          :file-list="fileList"
          :before-upload="beforeUpload"
          :on-success="onUploadSuccess"
          :on-remove="onUploadRemove"
        >
          <i class="el-icon-plus upload-icon" />
        </el-upload>
        <div class="upload-tip">支持JPG/PNG/PDF，最多10个文件</div>
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

// 表单初始值
const emptyForm = () => ({
  authId: null,
  residentId: null,
  buildingId: null,
  unitNo: '',
  houseId: null,
  type: 1,
  isMain: 2,
  authMaterialUrl: '',
  // 编辑时保留原审核信息，新增默认待审核
  auditStatus: 1,
  auditRemark: '',
  adminId: null
})

export default {
  name: 'AuthAddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    // 编辑时传入的列表行（含居民姓名/电话，用于回显搜索框文案）
    record: { type: Object, default: null },
    // 楼栋下拉数据源（父组件统一加载）
    buildings: { type: Array, default: () => [] },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm(),
      residentLoading: false,
      residentOptions: [],
      unitOptions: [],
      houseOptions: [],
      fileList: [],
      typeOptions: [
        { value: 1, label: '业主' },
        { value: 2, label: '家属' },
        { value: 3, label: '租户' }
      ],
      rules: {
        residentId: [{ required: true, message: '请选择居民', trigger: 'change' }],
        buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
        unitNo: [{ required: true, message: '请选择单元', trigger: 'change' }],
        houseId: [{ required: true, message: '请选择房间', trigger: 'change' }],
        type: [{ required: true, message: '请选择关系类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    // 上传请求头：携带 token
    uploadHeaders() {
      return { Authorization: 'Bearer ' + (localStorage.getItem('token') || '') }
    }
  },
  watch: {
    visible(val) {
      if (!val) {
        return
      }
      this.resetForm()
      if (this.isEdit && this.record) {
        this.loadDetail()
      }
    }
  },
  methods: {
    resetForm() {
      this.form = emptyForm()
      this.residentOptions = []
      this.unitOptions = []
      this.houseOptions = []
      this.fileList = []
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },

    // 编辑回显：查询认证信息 + 组装居民选项 + 三级联动定位房间 + 材料文件列表
    async loadDetail() {
      try {
        const res = await Api.getAuthById(this.record.authId)
        const auth = res.data || {}
        this.form.authId = auth.authId
        this.form.residentId = auth.residentId
        this.form.type = auth.type || 1
        this.form.isMain = auth.isMain || 2
        this.form.authMaterialUrl = auth.authMaterialUrl || ''
        this.form.auditStatus = auth.auditStatus
        this.form.auditRemark = auth.auditRemark
        this.form.adminId = auth.adminId
        // 居民下拉放入当前认证对应居民，显示"姓名 (电话)"
        this.residentOptions = [{
          residentId: auth.residentId,
          name: this.record.name,
          phone: this.record.phone
        }]
        // 已有材料回显为文件列表
        this.fileList = (auth.authMaterialUrl || '')
          .split(',')
          .filter(u => u)
          .map(u => ({ name: u.substring(u.lastIndexOf('/') + 1), url: u }))
        if (auth.houseId) {
          await this.locateHouse(auth.houseId)
        }
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 由房屋ID反查楼栋/单元，逐级加载下拉选项并选中
    async locateHouse(houseId) {
      const res = await Api.getHouseById(houseId)
      const house = res.data || {}
      this.form.buildingId = house.buildingId
      await this.loadUnitOptions()
      this.form.unitNo = house.unitNo
      await this.loadHouseOptions()
      this.form.houseId = house.houseId
    },

    // 加载单元下拉选项（不清空已选值，供回显使用）
    async loadUnitOptions() {
      if (!this.form.buildingId) {
        this.unitOptions = []
        return
      }
      const res = await Api.getBuildingById(this.form.buildingId)
      const count = (res.data && res.data.unitCount) || 0
      this.unitOptions = Array.from({ length: count }, (_, i) => ({
        value: String(i + 1),
        label: `${i + 1}单元`
      }))
    },

    // 加载房间下拉选项（不清空已选值，供回显使用）
    async loadHouseOptions() {
      if (!this.form.buildingId || !this.form.unitNo) {
        this.houseOptions = []
        return
      }
      const res = await Api.getHouseByBuildingAndUnit(this.form.buildingId, this.form.unitNo)
      this.houseOptions = res.data || []
    },

    // 异步搜索居民：输入姓名或电话，实时返回结果
    async searchResident(keyword) {
      if (!keyword) {
        this.residentOptions = []
        return
      }
      this.residentLoading = true
      try {
        const res = await Api.searchResident({ name: keyword, phone: keyword })
        this.residentOptions = res.data || []
      } catch (e) {
        this.residentOptions = []
      } finally {
        this.residentLoading = false
      }
    },

    // 楼栋-单元二级联动（用户手动切换时清空下级已选值）
    async onBuildingChange(buildingId) {
      this.form.unitNo = ''
      this.form.houseId = ''
      this.unitOptions = []
      this.houseOptions = []
      if (!buildingId) {
        return
      }
      try {
        await this.loadUnitOptions()
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 单元-房间三级联动（用户手动切换时清空下级已选值）
    async onUnitChange(unitNo) {
      this.form.houseId = ''
      this.houseOptions = []
      if (!unitNo) {
        return
      }
      try {
        await this.loadHouseOptions()
      } catch (e) {
        // 拦截器已提示
      }
    },

    beforeUpload(file) {
      const allow = /\.(jpg|jpeg|png|pdf)$/i.test(file.name)
      if (!allow) {
        this.$message.error('只支持 JPG/PNG/PDF 格式')
        return false
      }
      if (file.size > 10 * 1024 * 1024) {
        this.$message.error('单个文件不能超过10MB')
        return false
      }
      return true
    },

    // 上传成功：收集文件路径，逗号拼接
    onUploadSuccess(res, file, fileList) {
      if (!res || !res.data || !res.data.length) {
        this.$message.error('上传失败')
        const idx = fileList.indexOf(file)
        if (idx > -1) {
          fileList.splice(idx, 1)
        }
        return
      }
      this.fileList = fileList
      this.syncMaterialUrl()
    },

    onUploadRemove(file, fileList) {
      this.fileList = fileList
      this.syncMaterialUrl()
    },

    syncMaterialUrl() {
      const paths = this.fileList
        .map(f => (f.response && f.response.data ? f.response.data[0] : f.url))
        .filter(p => p)
      this.form.authMaterialUrl = paths.join(',')
    },

    onSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const payload = {
          residentId: this.form.residentId,
          houseId: this.form.houseId,
          type: this.form.type,
          isMain: this.form.isMain,
          authMaterialUrl: this.form.authMaterialUrl
        }
        if (this.isEdit) {
          // 编辑：保留原审核状态/备注/审核人
          payload.authId = this.form.authId
          payload.auditStatus = this.form.auditStatus
          payload.auditRemark = this.form.auditRemark
          payload.adminId = this.form.adminId
        } else {
          payload.auditStatus = 1
        }
        this.$emit('submit', payload)
      })
    },

    onClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/auth/add-or-update.less';
</style>
