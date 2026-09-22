<template>
  <el-dialog
    :visible="visible"
    title="投诉建议详情"
    width="820px"
    top="6vh"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <div v-loading="loading" class="detail-body">
      <!-- 基本信息 -->
      <div class="section-card">
        <div class="section-title">基本信息</div>
        <div class="detail-descriptions">
          <div class="detail-row">
            <span class="detail-label">编号</span><span class="detail-value">{{ detail.complaintNo || '-' }}</span>
            <span class="detail-label">状态</span>
            <span class="detail-value">
              <el-tag size="small" :type="statusTagType(detail.status)">{{ statusText(detail.status) }}</el-tag>
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">提交时间</span><span class="detail-value">{{ formatFullTime(detail.submitTime) || '-' }}</span>
            <span class="detail-label">标题</span><span class="detail-value">{{ detail.title || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">内容</span><span class="detail-value">{{ detail.content || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">联系电话</span><span class="detail-value">{{ detail.contactPhone || '-' }}</span>
            <span class="detail-label">联系人</span><span class="detail-value">{{ detail.contactPerson || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 附件图片 -->
      <div class="section-card">
        <div class="section-title">附件图片</div>
        <div class="attachment-body">
          <template v-if="attachments.length">
            <el-image
              v-for="(img, i) in attachments"
              :key="i"
              class="detail-image"
              :src="img"
              :preview-src-list="attachments"
              fit="cover"
            />
          </template>
          <template v-else>
            <span class="attachment-empty">暂无附件</span>
          </template>
        </div>
      </div>

      <!-- 回复记录 -->
      <div class="section-card">
        <div class="section-title">回复记录（{{ replies.length }}条）</div>
        <div class="reply-body">
          <div v-for="r in replies" :key="r.id" class="reply-card">
            <div class="reply-head">
              <el-tag size="mini">{{ replyTypeText(r.replyType) }}</el-tag>
              <span class="reply-time">{{ formatFullTime(r.createTime) }}</span>
              <el-button type="text" class="reply-delete" @click="onDeleteReply(r)">删除</el-button>
            </div>
            <div class="reply-content">{{ r.replyMessage }}</div>
            <div v-if="splitUrls(r.replyImageUrl).length" class="reply-images">
              <el-image
                v-for="(img, i) in splitUrls(r.replyImageUrl)"
                :key="i"
                class="reply-image"
                :src="img"
                :preview-src-list="splitUrls(r.replyImageUrl)"
                fit="cover"
              />
            </div>
          </div>
          <div v-if="!replies.length" class="reply-empty">暂无回复记录</div>
        </div>

        <!-- 添加回复 -->
        <div class="add-reply">
          <div class="add-reply-title">添加回复</div>
          <el-input
            v-model="replyForm.replyMessage"
            type="textarea"
            :rows="4"
            maxlength="500"
            placeholder="请输入回复内容..."
          />
          <el-upload
            class="reply-upload"
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
          <div class="upload-tip">最多5张</div>
          <div class="add-reply-footer">
            <el-button type="primary" :loading="submitting" @click="onSubmitReply">提交回复</el-button>
          </div>
        </div>
      </div>
    </div>

    <div slot="footer">
      <el-button @click="onClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/api'

export default {
  name: 'ComplaintDetailDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 列表行数据（提供 complaintId）
    row: { type: Object, default: null }
  },
  data() {
    return {
      loading: false,
      submitting: false,
      detail: {},
      // 当前管理员的回复记录集合
      replies: [],
      replyForm: {
        replyMessage: ''
      },
      // 已上传完成的回复图片地址集合
      imageUrls: [],
      fileList: []
    }
  },
  computed: {
    // 附件图片地址集合（逗号分隔）
    attachments() {
      return this.splitUrls(this.detail.attachmentUrl)
    }
  },
  methods: {
    // 弹窗打开时：回显详情 + 查询回复记录
    onOpen() {
      this.detail = {}
      this.replies = []
      this.replyForm.replyMessage = ''
      this.imageUrls = []
      this.fileList = []
      if (!this.row || !this.row.complaintId) {
        return
      }
      this.loading = true
      Promise.all([this.fetchDetail(), this.fetchReplies()]).finally(() => {
        this.loading = false
      })
    },

    // 根据投诉记录ID查询指定投诉记录信息
    fetchDetail() {
      return Api.getComplaintRecordById(this.row.complaintId)
        .then(res => {
          this.detail = res.data || {}
        })
        .catch(() => {
          // 拦截器已提示
        })
    },

    // 根据投诉记录ID、回复人类型、回复人ID查询回复信息集合
    fetchReplies() {
      const admin = this.$store.getters['user/admin'] || {}
      return Api.getReplyInfoList({
        complaintId: this.row.complaintId,
        replyType: 2,
        replyerId: admin.adminId
      })
        .then(res => {
          this.replies = res.data || []
        })
        .catch(() => {
          // 拦截器已提示
        })
    },

    // 逗号分隔的地址串转数组
    splitUrls(str) {
      if (!str) {
        return []
      }
      return str.split(',').filter(s => s)
    },

    statusText(status) {
      return { 1: '待处理', 2: '处理中', 3: '已回复', 4: '已关闭' }[status] || '未知'
    },

    statusTagType(status) {
      if (status === 3) return 'success'
      if (status === 1 || status === 2) return 'warning'
      return 'info'
    },

    replyTypeText(replyType) {
      return replyType === 2 ? '物业' : '用户'
    },

    // 完整时间格式化：2026-09-04 09:52:10
    formatFullTime(time) {
      if (!time) {
        return ''
      }
      const d = new Date(time)
      if (isNaN(d.getTime())) {
        return time
      }
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
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

    // 提交回复：添加回复信息 → 重新查询回复记录 → 修改投诉状态为已回复
    async onSubmitReply() {
      if (!this.replyForm.replyMessage) {
        this.$message.warning('请输入回复内容')
        return
      }
      if (this.submitting) {
        return
      }
      this.submitting = true
      try {
        const admin = this.$store.getters['user/admin'] || {}
        await Api.addReplyInfo({
          complaintId: this.row.complaintId,
          replyType: 2,
          replyerId: admin.adminId,
          replyMessage: this.replyForm.replyMessage,
          replyImageUrl: this.imageUrls.join(',')
        })
        // 刷新回复记录，展示刚才回复的内容
        await this.fetchReplies()
        // 修改投诉状态为已回复（3）
        await Api.updateComplaintStatus({ complaintId: this.row.complaintId, status: 3 })
        this.detail.status = 3
        this.replyForm.replyMessage = ''
        this.imageUrls = []
        this.fileList = []
        this.$message.success('回复成功')
        this.$emit('success')
      } catch (e) {
        // 拦截器已提示
      } finally {
        this.submitting = false
      }
    },

    // 删除回复记录
    async onDeleteReply(reply) {
      try {
        await this.$confirm('确定删除这条回复记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        await Api.deleteReplyInfo(reply.id)
        this.$message.success('删除成功')
        this.fetchReplies()
      } catch (e) {
        // 拦截器已提示
      }
    },

    onClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/complaint/detail-dialog.less';
</style>
