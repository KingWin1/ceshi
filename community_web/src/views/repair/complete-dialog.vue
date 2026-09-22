<template>
  <el-dialog
    :visible="visible"
    title="完工确认"
    width="640px"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <el-form label-width="90px">
      <el-form-item label="完工备注">
        <el-input
          v-model="form.completeRemark"
          type="textarea"
          :rows="4"
          maxlength="500"
          placeholder="请输入完工备注"
        />
      </el-form-item>
      <el-form-item label="完工图片">
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
  name: 'RepairCompleteDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 待完工的报修行数据（提供 repairId）
    row: { type: Object, default: null }
  },
  data() {
    return {
      submitting: false,
      form: {
        completeRemark: ''
      },
      // 已上传完成的图片地址集合
      imageUrls: [],
      fileList: []
    }
  },
  methods: {
    onOpen() {
      this.form.completeRemark = ''
      this.imageUrls = []
      this.fileList = []
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
      // 移除对应图片地址（file.url 即上传返回的路径）
      const url = file.url || (file.response && file.response[0])
      const idx = this.imageUrls.indexOf(url)
      if (idx > -1) {
        this.imageUrls.splice(idx, 1)
      }
    },

    onExceed() {
      this.$message.warning('最多上传 6 张图片')
    },

    // 确定：调用完工接口
    onSubmit() {
      if (this.submitting) {
        return
      }
      this.submitting = true
      Api.completeRepair({
        repairId: this.row.repairId,
        completeRemark: this.form.completeRemark,
        completeImageUrl: this.imageUrls.join(',')
      })
        .then(() => {
          this.$message.success('完工成功')
          this.$emit('success')
          this.onClose()
        })
        .catch(() => {
          // 拦截器已提示
        })
        .finally(() => {
          this.submitting = false
        })
    },

    onClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/repair/complete-dialog.less';
</style>
