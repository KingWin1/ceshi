<template>
  <el-dialog
    :visible="visible"
    title="回复投诉建议"
    width="640px"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="回复内容" prop="replyMessage">
        <el-input
          v-model="form.replyMessage"
          type="textarea"
          :rows="5"
          maxlength="500"
          placeholder="请输入回复内容"
        />
      </el-form-item>
      <el-form-item label="图片">
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
        <div class="upload-tip">最多上传5张图片</div>
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

export default {
  name: 'ComplaintReplyDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 待回复的投诉行数据（提供 complaintId）
    row: { type: Object, default: null }
  },
  data() {
    return {
      submitting: false,
      form: {
        replyMessage: ''
      },
      // 已上传完成的图片地址集合
      imageUrls: [],
      fileList: [],
      rules: {
        replyMessage: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
      }
    }
  },
  methods: {
    onOpen() {
      this.form.replyMessage = ''
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
      this.$message.warning('最多上传 5 张图片')
    },

    // 提交：先调用添加回复信息接口，再调用修改投诉状态接口置为已回复
    onSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid || this.submitting) {
          return
        }
        this.submitting = true
        try {
          // 回复人类型固定为管理员（2），回复人ID取当前登录管理员
          const admin = this.$store.getters['user/admin'] || {}
          await Api.addReplyInfo({
            complaintId: this.row.complaintId,
            replyType: 2,
            replyerId: admin.adminId,
            replyMessage: this.form.replyMessage,
            replyImageUrl: this.imageUrls.join(',')
          })
          // 添加回复成功后，将投诉状态修改为已回复（3）
          await Api.updateComplaintStatus({ complaintId: this.row.complaintId, status: 3 })
          this.$message.success('回复成功')
          this.$emit('success')
          this.onClose()
        } catch (e) {
          // 拦截器已提示
        } finally {
          this.submitting = false
        }
      })
    },

    onClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/complaint/reply-dialog.less';
</style>
