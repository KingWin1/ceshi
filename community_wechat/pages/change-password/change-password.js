// pages/change-password/change-password.js —— 修改密码（需登录态）
const memberApi = require('../../api/member')
const icons = require('../../utils/icons')

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
    oldVisible: false,
    newVisible: false,
    confirmVisible: false,
    loading: false
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
  },

  onBackTap() {
    wx.navigateBack()
  },

  onOldInput(e) {
    this.setData({ oldPassword: e.detail.value })
  },

  onNewInput(e) {
    this.setData({ newPassword: e.detail.value })
  },

  onConfirmInput(e) {
    this.setData({ confirmPassword: e.detail.value })
  },

  onToggleOld() {
    this.setData({ oldVisible: !this.data.oldVisible })
  },

  onToggleNew() {
    this.setData({ newVisible: !this.data.newVisible })
  },

  onToggleConfirm() {
    this.setData({ confirmVisible: !this.data.confirmVisible })
  },

  /**
   * 表单校验：全部通过返回 true，否则 toast 提示
   */
  validate() {
    const { oldPassword, newPassword, confirmPassword } = this.data
    if (!oldPassword) {
      wx.showToast({ title: '请输入当前密码', icon: 'none' })
      return false
    }
    if (newPassword.length < 6 || newPassword.length > 12) {
      wx.showToast({ title: '新密码需6-12位', icon: 'none' })
      return false
    }
    if (newPassword === oldPassword) {
      wx.showToast({ title: '新密码不能与当前密码相同', icon: 'none' })
      return false
    }
    if (newPassword !== confirmPassword) {
      wx.showToast({ title: '两次输入的新密码不一致', icon: 'none' })
      return false
    }
    return true
  },

  onSubmitTap() {
    if (this.data.loading || !this.validate()) return
    this.setData({ loading: true })
    memberApi.changePassword({
      oldPassword: this.data.oldPassword,
      newPassword: this.data.newPassword
    }).then(() => {
      this.setData({ loading: false })
      wx.showToast({ title: '密码修改成功，请重新登录', icon: 'none' })
      // 密码已变更，旧 token 不再可信，清登录态回登录页
      setTimeout(() => {
        wx.removeStorageSync('token')
        wx.removeStorageSync('resident')
        wx.reLaunch({ url: '/pages/login/login' })
      }, 1200)
    }).catch(() => {
      this.setData({ loading: false })
    })
  }
})
