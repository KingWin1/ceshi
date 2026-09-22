// pages/bill-list/bill-list.js —— 账单列表
const icons = require('../../utils/icons')
const billApi = require('../../api/bill')

const PAGE_SIZE = 10

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    tabs: [
      { label: '全部', value: '' },
      { label: '未缴', value: '0' },
      { label: '已缴', value: '1' }
    ],
    activeTab: '',
    list: [],
    unpaidTotal: '0.00',
    pageNum: 1,
    loading: false,
    noMore: false
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
  },

  onShow() {
    this.refreshList()
  },

  onBackTap() {
    wx.navigateBack()
  },

  onTabTap(e) {
    const value = e.currentTarget.dataset.value
    if (value === this.data.activeTab) return
    this.setData({ activeTab: value })
    this.refreshList()
  },

  refreshList() {
    this.setData({ list: [], pageNum: 1, noMore: false })
    this.loadPage()
  },

  /**
   * 加载分页数据并汇总待缴金额
   */
  loadPage() {
    if (this.data.loading || this.data.noMore) return
    this.setData({ loading: true })
    billApi.getBillPage({
      pageNum: this.data.pageNum,
      pageSize: PAGE_SIZE,
      status: this.data.activeTab
    }).then(res => {
      const records = (res && res.records) || res || []
      const rows = records.map(item => Object.assign({}, item, {
        amount: Number(item.amount || 0).toFixed(2),
        statusName: item.status === 1 ? '已缴清' : '待缴费'
      }))
      // 汇总未缴金额
      let total = 0
      rows.forEach(r => {
        if (r.status === 0) total += Number(r.amount || 0)
      })
      this.setData({
        list: this.data.list.concat(rows),
        unpaidTotal: total.toFixed(2),
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
    this.refreshList()
    wx.stopPullDownRefresh()
  },

  onGoDetail(e) {
    wx.navigateTo({ url: `/pages/bill-detail/bill-detail?id=${e.currentTarget.dataset.id}` })
  },

  /**
   * 立即缴费：跳转详情页完成支付
   */
  onPayTap(e) {
    wx.navigateTo({ url: `/pages/bill-detail/bill-detail?id=${e.currentTarget.dataset.id}&pay=1` })
  },

  onGoRecord() {
    wx.navigateTo({ url: '/pages/pay-record/pay-record' })
  }
})
