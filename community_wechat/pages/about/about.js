// pages/about/about.js —— 关于我们
const icons = require('../../utils/icons')

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    version: '1.0.0',
    contactPhone: '400-888-0000'
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
    // 读取小程序当前版本（体验版/开发版可能取不到，取不到则保持默认）
    try {
      const accountInfo = wx.getAccountInfoSync()
      const ver = accountInfo && accountInfo.miniProgram && accountInfo.miniProgram.version
      if (ver) this.setData({ version: ver })
    } catch (e) {
      // 低版本基础库不支持，忽略
    }
  },

  onBackTap() {
    wx.navigateBack()
  },

  onGoAgreement() {
    wx.navigateTo({ url: '/pages/agreement/agreement?type=user' })
  },

  onGoPrivacy() {
    wx.navigateTo({ url: '/pages/agreement/agreement?type=privacy' })
  },

  onCopyContact() {
    wx.setClipboardData({
      data: this.data.contactPhone,
      success: () => wx.showToast({ title: '客服电话已复制', icon: 'none' })
    })
  }
})
