// pages/pay-record/pay-record.js —— 缴费记录
const icons = require('../../utils/icons')
const billApi = require('../../api/bill')

const PAGE_SIZE = 15

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
    billApi.getPayRecordPage({
      pageNum: this.data.pageNum,
      pageSize: PAGE_SIZE
    }).then(res => {
      const records = (res && res.records) || res || []
      const rows = records.map(item => Object.assign({}, item, {
        amount: Number(item.amount || 0).toFixed(2),
        payWayName: item.payWay === 1 ? '微信支付' : '其他'
      }))
      this.setData({
        list: this.data.list.concat(rows),
        pageNum: this.data.pageNum + 1,
        noMore: rows.length < PAGE_SIZE,
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
  }
})
