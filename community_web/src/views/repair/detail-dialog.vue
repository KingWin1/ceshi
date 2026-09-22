<template>
  <el-dialog
    :visible="visible"
    title="报修单详情"
    width="820px"
    top="6vh"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <div v-loading="loading" class="detail-body">
      <!-- 报修信息 -->
      <div class="section-card">
        <div class="section-title">报修信息</div>
        <div class="detail-descriptions">
          <div class="detail-row">
            <span class="detail-label">报修单号</span><span class="detail-value">{{ detail.repairNo || '-' }}</span>
            <span class="detail-label">状态</span>
            <span class="detail-value">
              <el-tag size="small" :type="statusTagType(detail.status)">{{ statusText(detail.status) }}</el-tag>
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">报修居民</span><span class="detail-value">{{ detail.reporterName || '-' }}</span>
            <span class="detail-label">联系人</span><span class="detail-value">{{ detail.reporterName || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">联系电话</span><span class="detail-value">{{ detail.contactPhone || '-' }}</span>
            <span class="detail-label">报修地址</span><span class="detail-value">{{ detail.repairAddress || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">报修类型</span><span class="detail-value">{{ detail.repairTypeName || '-' }}</span>
            <span class="detail-label">期望上门时间</span><span class="detail-value">{{ formatFullTime(detail.expectedTime) || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">问题描述</span><span class="detail-value">{{ detail.description || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">派单员</span><span class="detail-value">{{ detail.employeeName || '-' }}</span>
            <span class="detail-label">派单时间</span><span class="detail-value">{{ formatFullTime(detail.dispatchTime) || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">报修时间</span><span class="detail-value">{{ formatFullTime(detail.createTime) || '-' }}</span>
            <span class="detail-label">更新时间</span><span class="detail-value">{{ updateTimeText || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 完工信息 -->
      <div class="section-card">
        <div class="section-title">
          完工信息
          <el-tag size="mini" type="success" effect="plain">{{ detail.completeTime ? '已完工' : '未完工' }}</el-tag>
        </div>
        <div class="detail-descriptions">
          <div class="detail-row">
            <span class="detail-label">完成时间</span><span class="detail-value">{{ formatFullTime(detail.completeTime) || '-' }}</span>
            <span class="detail-label">完工备注</span><span class="detail-value">{{ detail.completeRemark || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">完工图片</span>
            <span class="detail-value">
              <template v-if="completeImages.length">
                <el-image
                  v-for="(img, i) in completeImages"
                  :key="i"
                  class="detail-image"
                  :src="img"
                  :preview-src-list="completeImages"
                  fit="cover"
                />
              </template>
              <template v-else>暂无</template>
            </span>
          </div>
        </div>
      </div>

      <!-- 评价信息 -->
      <div class="section-card">
        <div class="section-title">
          评价信息
          <el-tag size="mini" :type="detail.ratingScore ? 'success' : 'info'" effect="plain">
            {{ detail.ratingScore ? '已评价' : '未评价' }}
          </el-tag>
        </div>
        <div class="detail-descriptions">
          <div class="detail-row">
            <span class="detail-label">评价分数</span>
            <span class="detail-value">
              <el-rate v-if="detail.ratingScore" disabled :value="detail.ratingScore" />
              <template v-else>-</template>
            </span>
            <span class="detail-label">评价时间</span><span class="detail-value">{{ formatFullTime(detail.ratingTime) || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">评价内容</span><span class="detail-value">{{ detail.ratingContent || '-' }}</span>
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
  name: 'RepairDetailDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 列表行数据（提供 repairId）
    row: { type: Object, default: null }
  },
  data() {
    return {
      loading: false,
      detail: {}
    }
  },
  computed: {
    // 完工图片地址集合（逗号分隔）
    completeImages() {
      if (!this.detail.completeImageUrl) {
        return []
      }
      return this.detail.completeImageUrl.split(',').filter(s => s)
    },
    // 更新时间：取派单/完工/评价时间中最新的一个
    updateTimeText() {
      const times = [this.detail.dispatchTime, this.detail.completeTime, this.detail.ratingTime]
        .filter(t => t)
        .map(t => new Date(t).getTime())
      if (times.length === 0) {
        return ''
      }
      return this.formatFullTime(new Date(Math.max(...times)))
    }
  },
  methods: {
    // 弹窗打开时：根据报修ID查询报修记录详情
    onOpen() {
      this.detail = {}
      if (!this.row || !this.row.repairId) {
        return
      }
      this.loading = true
      Api.getRepairDetail(this.row.repairId)
        .then(res => {
          this.detail = res.data || {}
        })
        .catch(() => {
          // 拦截器已提示
        })
        .finally(() => {
          this.loading = false
        })
    },

    statusText(status) {
      return { 1: '待派单', 2: '已派单', 3: '处理中', 4: '已完成', 5: '已取消' }[status] || '未知'
    },

    statusTagType(status) {
      if (status === 4) return 'success'
      if (status === 5) return 'danger'
      if (status === 1) return 'warning'
      return 'info'
    },

    // 完整时间格式化：2026-09-07 11:22:44
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

    onClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/repair/detail-dialog.less';
</style>
