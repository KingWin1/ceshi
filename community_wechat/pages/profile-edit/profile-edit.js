// pages/profile-edit/profile-edit.js —— 个人资料编辑
const memberApi = require('../../api/member')
const icons = require('../../utils/icons')

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    avatar: '',
    name: '',
    phone: '',
    phoneMasked: '',
    gender: 0,
    loading: false
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    const resident = wx.getStorageSync('resident') || {}
    this.setData({
      statusBarHeight: info.statusBarHeight || 20,
      name: resident.name || '',
      phone: resident.phone || '',
      phoneMasked: this.maskPhone(resident.phone),
      gender: resident.gender || 0
    })
  },

  /**
   * 手机号脱敏：138****8888
   */
  maskPhone(phone) {
    if (!phone || phone.length !== 11) return phone || ''
    return phone.substring(0, 3) + '****' + phone.substring(7)
  },

  onBackTap() {
    wx.navigateBack()
  },

  /**
   * 选择微信头像（chooseAvatar 开放能力），存本地临时路径
   */
  onChooseAvatar(e) {
    const url = e.detail && e.detail.avatarUrl
    if (url) this.setData({ avatar: url })
  },

  onNameInput(e) {
    this.setData({ name: e.detail.value })
  },

  onGenderChange(e) {
    this.setData({ gender: Number(e.detail.value) })
  },

  onSubmitTap() {
    if (this.data.loading) return
    if (!this.data.name.trim()) {
      wx.showToast({ title: '请输入姓名', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    memberApi.updateProfile({
      name: this.data.name.trim(),
      gender: this.data.gender
    }).then(() => {
      // 同步本地缓存，页面返回后"我的"页即时生效
      const resident = wx.getStorageSync('resident') || {}
      resident.name = this.data.name.trim()
      resident.gender = this.data.gender
      wx.setStorageSync('resident', resident)
      this.setData({ loading: false })
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 800)
    }).catch(() => {
      this.setData({ loading: false })
    })
  }
})
