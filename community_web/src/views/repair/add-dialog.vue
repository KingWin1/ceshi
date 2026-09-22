<template>
  <el-dialog
    :visible="visible"
    title="新增报修"
    width="640px"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="联系人" prop="reporterName">
        <el-input v-model="form.reporterName" placeholder="请输入联系人姓名" maxlength="50" clearable />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="20" clearable />
      </el-form-item>
      <el-form-item label="报修地址" prop="repairAddress">
        <el-input v-model="form.repairAddress" placeholder="请输入报修地址（如：1栋2单元301）" maxlength="255" clearable />
      </el-form-item>
      <el-form-item label="报修类型" prop="repairTypeId">
        <el-select v-model="form.repairTypeId" class="w-full" placeholder="请选择报修类型">
          <el-option v-for="t in repairTypes" :key="t.typeId" :label="t.typeName" :value="t.typeId" />
        </el-select>
      </el-form-item>
      <el-form-item label="问题描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          maxlength="500"
          placeholder="请描述报修问题"
        />
      </el-form-item>
      <el-form-item label="现场图片">
        <el-upload
          list-type="picture-card"
          accept="image/jpeg,image/png"
          :file-list="fileList"
          :limit="6"
          :before-upload="beforeUpload"
          :http-request="doUpload"
          :on-remove="onRemove"
          :on-exceed="onExceed"
        >
          <i class="el-icon-plus" />
        </el-upload>
        <div class="upload-tip">最多6张，每张不超过5MB</div>
      </el-form-item>
      <el-form-item label="备注">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="2"
          maxlength="255"
          placeholder="备注信息（选填）"
        />
      </el-form-item>
      <el-form-item label="期望上门时间">
        <el-date-picker
          v-model="form.expectedTime"
          type="datetime"
          class="w-full"
          placeholder="选择期望上门时间"
          value-format="yyyy-MM-dd HH:mm:ss"
        />
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">提交</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/api'

export default {
  name: 'RepairAddDialog',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      submitting: false,
      // 报修类型下拉数据源
      repairTypes: [],
      // 已上传完成的现场图片地址集合
      imageUrls: [],
      fileList: [],
      form: {
        reporterName: '',
        contactPhone: '',
        repairAddress: '',
        repairTypeId: null,
        description: '',
        remark: '',
        expectedTime: ''
      },
      rules: {
        reporterName: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        repairAddress: [{ required: true, message: '请输入报修地址', trigger: 'blur' }],
        repairTypeId: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
        description: [{ required: true, message: '请描述报修问题', trigger: 'blur' }]
      }
    }
  },
  methods: {
    // 弹窗打开时：重置表单并渲染报修类型下拉框
    onOpen() {
      this.form = {
        reporterName: '',
        contactPhone: '',
        repairAddress: '',
        repairTypeId: null,
        description: '',
        remark: '',
        expectedTime: ''
      }
      this.imageUrls = []
      this.fileList = []
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
      this.fetchRepairTypes()
    },

    // 查询全部报修类型信息接口
    async fetchRepairTypes() {
      try {
        const res = await Api.getRepairTypeListAll()
        this.repairTypes = res.data || []
      } catch (e) {
        // 拦截器已提示
      }
    },

    beforeUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].indexOf(file.type) > -1
      if (!isImage) {
        this.$message.warning('只能上传 JPG/PNG 格式的图片')
        return false
      }
      const isLt5M = file.size / 1024 / 1024 <= 5
      if (!isLt5M) {
        this.$message.warning('每张图片不能超过 5MB')
        return false
      }
      return true
    },

    // 自定义上传：调用文件上传接口，成功后记录图片地址
    doUpload({ file }) {
      const formData = new FormData()
      formData.append('files', file)
      return Api.uploadFile(formData)
        .then(res => {
          const paths = res.data || []
          this.imageUrls = this.imageUrls.concat(paths)
        })
        .catch(() => {
          // 拦截器已提示
        })
    },

    onRemove(file) {
      const url = file.url || (file.response && file.response[0])
      const idx = this.imageUrls.indexOf(url)
      if (idx > -1) {
        this.imageUrls.splice(idx, 1)
      }
    },

    onExceed() {
      this.$message.warning('最多上传 6 张图片')
    },

    // 提交：调用添加报修记录接口
    onSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.submitting = true
        Api.addRepair(Object.assign({}, this.form, {
          imageUrl: this.imageUrls.join(',')
        }))
          .then(() => {
            this.$message.success('添加成功')
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
@import '@/assets/styles/repair/add-dialog.less';
</style>
