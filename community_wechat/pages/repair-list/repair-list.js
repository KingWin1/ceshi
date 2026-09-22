// pages/repair-list/repair-list.js —— 报修列表
const icons = require('../../utils/icons')
const repairApi = require('../../api/repair')

// 报修状态映射：0-待审核 1-待维修 2-已完成 3-已取消
const STATUS_MAP = {
  0: { name: '待审核', key: 'pending' },
  1: { name: '待维修', key: 'doing' },
  2: { name: '已完成', key: 'done' },
  3: { name: '已取消', key: 'cancel' }
}

const PAGE_SIZE = 10

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    tabs: [
      { label: '全部', value: '' },
      { label: '待审核', value: '0' },
      { label: '待维修', value: '1' },
      { label: '已完成', value: '2' }
    ],
    activeTab: '',
    list: [],
    pageNum: 1,
    loading: false,
    noMore: false
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
  },

  onShow() {
    // 从提交报修/详情页返回时刷新列表
    this.refreshList()
  },

  onBackTap() {
    wx.navigateBack()
  },

  /**
   * 切换状态筛选
   */
  onTabTap(e) {
    const value = e.currentTarget.dataset.value
    if (value === this.data.activeTab) return
    this.setData({ activeTab: value })
    this.refreshList()
  },

  /**
   * 重置分页并加载第一页
   */
  refreshList() {
    this.setData({ list: [], pageNum: 1, noMore: false })
    this.loadPage()
  },

  /**
   * 加载分页数据
   */
  loadPage() {
    if (this.data.loading || this.data.noMore) return
    this.setData({ loading: true })
    repairApi.getRepairPage({
      pageNum: this.data.pageNum,
      pageSize: PAGE_SIZE,
      status: this.data.activeTab
    }).then(res => {
      // 兼容后端分页结构 { records, total } 或直接返回数组
      const records = (res && res.records) || res || []
      const rows = records.map(item => {
        const st = STATUS_MAP[item.status] || { name: '未知', key: 'pending' }
        return Object.assign({}, item, {
          typeName: item.typeName || '维修',
          statusName: st.name,
          statusKey: st.key
        })
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
    this.refreshList()
    wx.stopPullDownRefresh()
  },

  onGoDetail(e) {
    wx.navigateTo({ url: `/pages/repair-detail/repair-detail?id=${e.currentTarget.dataset.id}` })
  },

  onGoAdd() {
    wx.navigateTo({ url: '/pages/repair-add/repair-add' })
  }
})
