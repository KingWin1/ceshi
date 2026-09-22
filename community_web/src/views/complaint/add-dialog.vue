<template>
  <el-dialog
    :visible="visible"
    title="新增投诉建议"
    width="640px"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入标题" maxlength="100" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input v-model="form.contactPerson" placeholder="请输入联系人姓名" maxlength="50" clearable />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="20" clearable />
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="5"
          maxlength="1000"
          show-word-limit
          placeholder="请输入投诉/建议内容"
        />
      </el-form-item>
      <el-form-item label="图片附件">
        <el-upload
          list-type="picture-card"
          accept="image/jpeg,image/png"
          :file-list="fileList"
          :limit="5"
          :before-upload="beforeUpload"
          :http-request="doUpload"
          :on-remove="onRemove"
          :on-exceed="onExceed"
        >
          <i class="el-icon-plus" />
        </el-upload>
        <div class="upload-tip">最多5张，每张不超过5MB</div>
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

// 表单初始值
const emptyForm = () => ({
  title: '',
  contactPerson: '',
  contactPhone: '',
  content: ''
})

export default {
  name: 'ComplaintAddDialog',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      submitting: false,
      form: emptyForm(),
      // 已上传完成的附件地址集合
      imageUrls: [],
      fileList: [],
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        contactPerson: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        content: [{ required: true, message: '请输入投诉/建议内容', trigger: 'blur' }]
      }
    }
  },
  methods: {
    onOpen() {
      this.form = emptyForm()
      this.imageUrls = []
      this.fileList = []
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },

    // 上传前校验类型与大小
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

    // 自定义上传：调用文件上传接口，成功后记录附件地址
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
      this.$message.warning('最多上传 5 张图片')
    },

    // 提交：调用添加投诉记录接口
    onSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid || this.submitting) {
          return
        }
        this.submitting = true
        Api.addComplaintRecord(Object.assign({}, this.form, {
          attachmentUrl: this.imageUrls.join(',')
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
@import '@/assets/styles/complaint/add-dialog.less';
</style>
