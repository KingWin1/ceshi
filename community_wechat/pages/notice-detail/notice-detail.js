// pages/notice-detail/notice-detail.js —— 公告详情
const icons = require('../../utils/icons')
const noticeApi = require('../../api/notice')

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    detail: null,
    paragraphs: []
  },

  onLoad(options) {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
    if (options.id) this.loadDetail(options.id)
  },

  onBackTap() {
    wx.navigateBack()
  },

  loadDetail(id) {
    noticeApi.getNoticeDetail(id).then(res => {
      const content = res.content || res.summary || ''
      this.setData({
        detail: res,
        // 按换行拆分为段落渲染，避免使用 rich-text
        paragraphs: content.split('\n').filter(p => p.trim() !== '')
      })
    })
  },

  onPreviewImage(e) {
    wx.previewImage({
      current: e.currentTarget.dataset.url,
      urls: this.data.detail.images || []
    })
  }
})
