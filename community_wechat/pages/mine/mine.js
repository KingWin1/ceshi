// pages/mine/mine.js —— 个人中心
const icons = require('../../utils/icons')
const auth = require('../../utils/auth')

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    resident: null,
    residentMasked: ''
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
  },

  onShow() {
    // 每次显示刷新登录态，保证从其他页面返回时数据最新
    const resident = wx.getStorageSync('resident') || null
    this.setData({
      resident: resident,
      residentMasked: resident ? this.maskPhone(resident.phone) : ''
    })
  },

  /**
   * 手机号脱敏：138****8888
   */
  maskPhone(phone) {
    if (!phone || phone.length !== 11) return phone || ''
    return phone.substring(0, 3) + '****' + phone.substring(7)
  },

  onGoLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  onGoRepair() {
    auth.requireLogin('/pages/repair-list/repair-list')
  },

  onGoBill() {
    auth.requireLogin('/pages/bill-list/bill-list')
  },

  onGoNotice() {
    wx.navigateTo({ url: '/pages/notice-list/notice-list' })
  },

  onGoProfile() {
    auth.requireLogin('/pages/profile-edit/profile-edit')
  },

  onGoSettings() {
    wx.navigateTo({ url: '/pages/settings/settings' })
  },

  onLogoutTap() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: res => {
        if (!res.confirm) return
        auth.logout()
        this.setData({ resident: null, residentMasked: '' })
        wx.showToast({ title: '已退出登录', icon: 'none' })
      }
    })
  }
})
