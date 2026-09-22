// pages/home/home.js —— 首页占位（后续业务模块在此基础上扩展）
const icons = require('../../utils/icons')

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
    // 每次显示刷新登录态
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
    // 统一进入登录页：登录页内提供微信一键登录与账号登录，
    // 未绑定居民账号时由登录页弹窗引导去绑定页
    wx.navigateTo({ url: '/pages/login/login' })
  },

  onLogoutTap() {
    wx.removeStorageSync('token')
    wx.removeStorageSync('resident')
    getApp().globalData.needBind = false
    this.setData({ resident: null, residentMasked: '' })
    wx.showToast({ title: '已退出登录', icon: 'none' })
  }
})
