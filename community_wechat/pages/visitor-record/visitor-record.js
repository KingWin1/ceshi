// pages/visitor-record/visitor-record.js —— 访客记录
const icons = require('../../utils/icons')
const visitorApi = require('../../api/visitor')

const PAGE_SIZE = 10

// 状态：0-待到访 1-已到访 2-已离开 3-已过期
const STATUS_MAP = {
  0: { name: '待到访', key: 'pending' },
  1: { name: '已到访', key: 'arrived' },
  2: { name: '已离开', key: 'left' },
  3: { name: '已过期', key: 'expired' }
}

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
    visitorApi.getVisitorPage({
      pageNum: this.data.pageNum,
      pageSize: PAGE_SIZE
    }).then(res => {
      const records = (res && res.records) || res || []
      const rows = records.map(item => {
        const st = STATUS_MAP[item.status] || STATUS_MAP[0]
        return Object.assign({}, item, { statusName: st.name, statusKey: st.key })
      })
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
