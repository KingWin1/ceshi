// pages/visitor-invite/visitor-invite.js —— 访客邀请
const icons = require('../../utils/icons')
const visitorApi = require('../../api/visitor')

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    visitorName: '',
    visitorPhone: '',
    reason: '',
    dayOptions: [
      { label: '今天', value: 1 },
      { label: '三天内', value: 3 },
      { label: '一周内', value: 7 }
    ],
    validDays: 1,
    submitting: false,
    invite: null
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
  },

  onBackTap() {
    wx.navigateBack()
  },

  onNameInput(e) {
    this.setData({ visitorName: e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ visitorPhone: e.detail.value })
  },

  onReasonInput(e) {
    this.setData({ reason: e.detail.value })
  },

  onDayTap(e) {
    this.setData({ validDays: e.currentTarget.dataset.value })
  },

  validate() {
    if (!this.data.visitorName.trim()) {
      wx.showToast({ title: '请输入访客姓名', icon: 'none' })
      return false
    }
    if (!/^1[3-9]\d{9}$/.test(this.data.visitorPhone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return false
    }
    return true
  },

  onSubmit() {
    if (this.data.submitting || !this.validate()) return
    this.setData({ submitting: true })
    visitorApi.createInvite({
      visitorName: this.data.visitorName.trim(),
      visitorPhone: this.data.visitorPhone,
      reason: this.data.reason.trim(),
      validDays: this.data.validDays
    }).then(res => {
      this.setData({
        invite: res || { visitorName: this.data.visitorName, code: '888888', expireTime: '' },
        submitting: false
      })
    }).catch(() => {
      this.setData({ submitting: false })
    })
  },

  /**
   * 重置表单，继续邀请下一位
   */
  onReset() {
    this.setData({
      invite: null,
      visitorName: '',
      visitorPhone: '',
      reason: ''
    })
  },

  onGoRecord() {
    wx.navigateTo({ url: '/pages/visitor-record/visitor-record' })
  }
})
