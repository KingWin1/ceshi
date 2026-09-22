// pages/message-center/message-center.js —— 消息中心
const icons = require('../../utils/icons')
const noticeApi = require('../../api/notice')

const PAGE_SIZE = 15

// 消息类型：1-缴费提醒 2-报修进度 3-公告通知 4-系统消息
const TYPE_MAP = {
  1: { key: 'bill', icon: '💰' },
  2: { key: 'repair', icon: '🔧' },
  3: { key: 'notice', icon: '📢' },
  4: { key: 'system', icon: '⚙️' }
}

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    list: [],
    unreadCount: 0,
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
    noticeApi.getMessagePage({
      pageNum: this.data.pageNum,
      pageSize: PAGE_SIZE
    }).then(res => {
      const records = (res && res.records) || res || []
      const rows = records.map(item => {
        const t = TYPE_MAP[item.type] || TYPE_MAP[4]
        return Object.assign({}, item, {
          typeKey: t.key,
          typeIcon: t.icon,
          isRead: !!item.isRead
        })
      })
      // 统计本页未读数（简化处理）
      const unread = this.data.unreadCount + rows.filter(r => !r.isRead).length
      this.setData({
        list: this.data.list.concat(rows),
        unreadCount: unread,
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
    this.setData({ list: [], pageNum: 1, noMore: false, unreadCount: 0 })
    this.loadPage()
    wx.stopPullDownRefresh()
  },

  /**
   * 点击消息：标记已读并按 link 跳转
   */
  onTapMsg(e) {
    const { link } = e.currentTarget.dataset
    const index = e.currentTarget.dataset.index
    if (!this.data.list[index].isRead) {
      this.setData({
        [`list[${index}].isRead`]: true,
        unreadCount: Math.max(0, this.data.unreadCount - 1)
      })
    }
    if (link) wx.navigateTo({ url: link })
  },

  /**
   * 全部标记已读
   */
  onReadAll() {
    noticeApi.readAllMessages().then(() => {
      const list = this.data.list.map(item => Object.assign({}, item, { isRead: true }))
      this.setData({ list: list, unreadCount: 0 })
      wx.showToast({ title: '已全部已读', icon: 'none' })
    })
  }
})
