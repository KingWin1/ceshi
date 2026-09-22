// pages/settings/settings.js —— 设置页
const icons = require('../../utils/icons')

const NOTIFY_KEY = 'setting_notify'

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    notifyOn: true
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    // 通知开关首次进入默认开启
    const saved = wx.getStorageSync(NOTIFY_KEY)
    this.setData({
      statusBarHeight: info.statusBarHeight || 20,
      notifyOn: saved === '' ? true : !!saved
    })
  },

  onBackTap() {
    wx.navigateBack()
  },

  onNotifyChange(e) {
    const on = e.detail.value
    wx.setStorageSync(NOTIFY_KEY, on)
    this.setData({ notifyOn: on })
    wx.showToast({ title: on ? '已开启通知' : '已关闭通知', icon: 'none' })
  },

  onGoChangePwd() {
    wx.navigateTo({ url: '/pages/change-password/change-password' })
  },

  /**
   * 清除缓存：保留登录态，仅清理业务缓存
   */
  onClearCache() {
    wx.showModal({
      title: '提示',
      content: '将清除本地业务缓存（不影响登录状态），确定继续？',
      success: res => {
        if (!res.confirm) return
        const keep = ['token', 'resident', 'openid', NOTIFY_KEY]
        const all = wx.getStorageInfoSync().keys || []
        all.forEach(k => {
          if (keep.indexOf(k) === -1) wx.removeStorageSync(k)
        })
        wx.showToast({ title: '缓存已清除', icon: 'success' })
      }
    })
  },

  /**
   * 检查更新：小程序版本由微信后台管理，此处走 updateManager
   */
  onCheckUpdate() {
    if (!wx.getUpdateManager) {
      wx.showToast({ title: '当前环境不支持', icon: 'none' })
      return
    }
    const manager = wx.getUpdateManager()
    manager.onUpdateReady(() => {
      wx.showModal({
        title: '更新提示',
        content: '新版本已准备好，重启后生效',
        success: r => {
          if (r.confirm) manager.applyUpdate()
        }
      })
    })
    manager.onUpdateFailed(() => {
      wx.showToast({ title: '检查更新失败', icon: 'none' })
    })
    wx.showToast({ title: '正在检查更新', icon: 'none' })
  },

  onGoAbout() {
    wx.navigateTo({ url: '/pages/about/about' })
  }
})
