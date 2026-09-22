<template>
  <el-dialog
    :visible="visible"
    title="门店详情"
    width="800px"
    top="6vh"
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <div v-loading="loading" class="detail-body">
      <!-- 基本信息 -->
      <div class="detail-descriptions">
        <div class="detail-row">
          <span class="detail-label">门店名称</span><span class="detail-value">{{ detail.storeName || '-' }}</span>
          <span class="detail-label">门店分类</span><span class="detail-value">{{ detail.categoryName || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">联系电话</span><span class="detail-value">{{ detail.contactPhone || '-' }}</span>
          <span class="detail-label">营业时间</span><span class="detail-value">{{ businessTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">详细地址</span><span class="detail-value detail-value-full">{{ detail.address || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">经度</span><span class="detail-value">{{ detail.longitude == null ? '-' : detail.longitude }}</span>
          <span class="detail-label">纬度</span><span class="detail-value">{{ detail.latitude == null ? '-' : detail.latitude }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态</span>
          <span class="detail-value">
            <el-tag size="small" :type="detail.status === 1 ? 'success' : 'danger'">{{ statusText(detail.status) }}</el-tag>
          </span>
          <span class="detail-label">排序</span><span class="detail-value">{{ detail.sort == null ? 0 : detail.sort }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">浏览次数</span><span class="detail-value">{{ detail.viewCount == null ? 0 : detail.viewCount }}</span>
          <span class="detail-label">创建时间</span><span class="detail-value">{{ formatFullTime(detail.createTime) || '-' }}</span>
        </div>
      </div>

      <!-- 门店Logo -->
      <div class="detail-section">
        <div class="section-title">门店Logo</div>
        <div class="image-body">
          <el-image
            v-if="detail.logoUrl"
            class="detail-image"
            :src="detail.logoUrl"
            :preview-src-list="[detail.logoUrl]"
            fit="cover"
          />
          <span v-else class="image-empty">暂无Logo</span>
        </div>
      </div>

      <!-- 门店图片 -->
      <div class="detail-section">
        <div class="section-title">门店图片</div>
        <div class="image-body">
          <template v-if="images.length">
            <el-image
              v-for="(img, i) in images"
              :key="i"
              class="detail-image"
              :src="img"
              :preview-src-list="images"
              fit="cover"
            />
          </template>
          <span v-else class="image-empty">暂无图片</span>
        </div>
      </div>

      <!-- 门店介绍 -->
      <div class="detail-section">
        <div class="section-title">门店介绍</div>
        <div class="intro-body" v-html="introHtml" />
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
  name: 'StoreDetailDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 门店ID
    storeId: { type: Number, default: null }
  },
  data() {
    return {
      loading: false,
      detail: {}
    }
  },
  computed: {
    // 营业时间：15:31-15:31
    businessTime() {
      if (!this.detail.openTime || !this.detail.closeTime) {
        return '-'
      }
      return `${this.detail.openTime.substring(0, 5)}-${this.detail.closeTime.substring(0, 5)}`
    },
    // 门店图片地址集合（逗号分隔）
    images() {
      if (!this.detail.imageUrl) {
        return []
      }
      return this.detail.imageUrl.split(',').filter(s => s)
    },
    // 门店介绍（去除HTML标签后以纯文本展示，避免直接使用 v-html）
    introText() {
      if (!this.detail.introduction) {
        return '暂无介绍'
      }
      return this.detail.introduction.replace(/<[^>]+>/g, '')
    }
  },
  methods: {
    // 弹窗打开：根据门店ID查询门店详情
    onOpen() {
      this.detail = {}
      if (!this.storeId) {
        return
      }
      this.loading = true
      Api.getStoreDetail(this.storeId)
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
      return { 1: '营业中', 2: '已下架' }[status] || '未知'
    },

    // 完整时间格式化：2026-07-23 14:33:27
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
@import '@/assets/styles/store/detail-dialog.less';
</style>
