// pages/notice-list/notice-list.js —— 小区公告列表
const icons = require('../../utils/icons')
const noticeApi = require('../../api/notice')

const PAGE_SIZE = 10

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    list: [],
    pageNum: 1,
    loading: false,
    noMore: false
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
    this.loadPage()
  },

  onBackTap() {
    wx.navigateBack()
  },

  loadPage() {
    if (this.data.loading || this.data.noMore) return
    this.setData({ loading: true })
    noticeApi.getNoticePage({
      pageNum: this.data.pageNum,
      pageSize: PAGE_SIZE
    }).then(res => {
      const records = (res && res.records) || res || []
      this.setData({
        list: this.data.list.concat(records),
        pageNum: this.data.pageNum + 1,
        noMore: records.length < PAGE_SIZE,
        loading: false
      })
    }).catch(() => {
      this.setData({ loading: false })
    })
  },

  onReachBottom() {
    this.loadPage()
  },

  onPullDownRefresh() {
    this.setData({ list: [], pageNum: 1, noMore: false })
    this.loadPage()
    wx.stopPullDownRefresh()
  },

  onGoDetail(e) {
    wx.navigateTo({ url: `/pages/notice-detail/notice-detail?id=${e.currentTarget.dataset.id}` })
  }
})
