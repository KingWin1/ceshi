<template>
  <el-dialog
    :visible="visible"
    title="导入账单"
    width="560px"
    append-to-body
    @close="onClose"
  >
    <el-form label-width="90px" class="import-form">
      <el-form-item label="下载模板">
        <el-button type="text" icon="el-icon-download" :loading="downloading" @click="onDownloadTemplate">
          Excel模板下载
        </el-button>
      </el-form-item>
      <el-form-item label="上传文件">
        <el-upload
          ref="upload"
          action="#"
          accept=".xlsx,.xls"
          :limit="1"
          :auto-upload="false"
          :file-list="fileList"
          :on-change="onFileChange"
          :on-remove="onFileRemove"
          :on-exceed="onExceed"
        >
          <el-button type="primary" icon="el-icon-upload2">选择文件</el-button>
          <div slot="tip" class="upload-tip">只能上传 xlsx/xls 文件，且不超过 5MB</div>
        </el-upload>
      </el-form-item>
      <el-form-item label="导入规则">
        <el-checkbox v-model="overwrite">覆盖重复账单</el-checkbox>
        <div class="upload-tip">校验房号：自动匹配系统房间</div>
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">开始导入</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/api'

export default {
  name: 'BillImportDialog',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      loading: false,
      downloading: false,
      overwrite: false,
      fileList: [],
      file: null
    }
  },
  methods: {
    // 下载账单导入Excel模板
    onDownloadTemplate() {
      this.downloading = true
      Api.downloadBillTemplate()
        .then(blob => {
          if (!blob || blob.size === 0) {
            this.$message.error('模板下载失败')
            return
          }
          const url = window.URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = '账单导入模板.xlsx'
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          window.URL.revokeObjectURL(url)
        })
        .catch(() => {
          // 拦截器已提示
        })
        .finally(() => {
          this.downloading = false
        })
    },

    onFileChange(file, fileList) {
      const isExcel = /\.(xlsx|xls)$/i.test(file.name)
      const isLt5M = file.size / 1024 / 1024 <= 5
      if (!isExcel) {
        this.$message.warning('只能上传 xlsx/xls 格式的文件')
        this.fileList = []
        this.file = null
        return
      }
      if (!isLt5M) {
        this.$message.warning('文件大小不能超过 5MB')
        this.fileList = []
        this.file = null
        return
      }
      this.fileList = fileList.slice(-1)
      this.file = file.raw
    },

    onFileRemove() {
      this.fileList = []
      this.file = null
    },

    onExceed(files) {
      const file = files[0]
      const isExcel = /\.(xlsx|xls)$/i.test(file.name)
      if (!isExcel) {
        this.$message.warning('只能上传 xlsx/xls 格式的文件')
        return
      }
      // 超出限制时用新文件替换旧文件
      this.fileList = [{ name: file.name, raw: file }]
      this.file = file
    },

    // 提交导入：上传文件并解析，后端完成匹配后调用批量添加
    onSubmit() {
      if (!this.file) {
        this.$message.warning('请先选择要导入的文件')
        return
      }
      const formData = new FormData()
      formData.append('file', this.file)
      formData.append('overwrite', this.overwrite)
      this.loading = true
      Api.importBill(formData)
        .then(res => {
          this.$alert(res.data || '导入完成', '导入结果', { confirmButtonText: '确定' })
          this.$emit('success')
          this.onClose()
        })
        .catch(() => {
          // 拦截器已提示
        })
        .finally(() => {
          this.loading = false
        })
    },

    onClose() {
      this.file = null
      this.fileList = []
      this.overwrite = false
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/bill/import-dialog.less';
</style>
