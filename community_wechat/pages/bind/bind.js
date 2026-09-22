// pages/bind/bind.js —— 微信身份绑定居民账号
const memberApi = require('../../api/member')
const icons = require('../../utils/icons')

const PHONE_REGEX = /^1[3-9]\d{9}$/

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    openid: '',
    phone: '',
    code: '',
    countdown: 0,
    loading: false
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({
      statusBarHeight: info.statusBarHeight || 20,
      openid: wx.getStorageSync('openid') || getApp().globalData.openid || ''
    })
  },

  onUnload() {
    if (this.timer) {
      clearInterval(this.timer)
      this.timer = null
    }
  },

  onBackTap() {
    wx.navigateBack({
      fail: () => wx.reLaunch({ url: '/pages/home/home' })
    })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onCodeInput(e) {
    this.setData({ code: e.detail.value })
  },

  /**
   * 获取绑定验证码（登录类型2）
   */
  onSendCodeTap() {
    if (this.data.countdown > 0) return
    if (!PHONE_REGEX.test(this.data.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    memberApi.sendSmsCode(this.data.phone, 2).then(res => {
      if (res && res.devCode) {
        this.setData({ code: res.devCode })
      }
      wx.showToast({ title: '验证码已发送', icon: 'none' })
      this.startCountdown()
    }).catch(() => {})
  },

  startCountdown() {
    this.setData({ countdown: 60 })
    this.timer = setInterval(() => {
      const next = this.data.countdown - 1
      if (next <= 0) {
        clearInterval(this.timer)
        this.timer = null
        this.setData({ countdown: 0 })
      } else {
        this.setData({ countdown: next })
      }
    }, 1000)
  },

  /**
   * 立即绑定：手机号+验证码匹配居民后回填 openid 绑定关系并签发token
   */
  onBindTap() {
    if (this.data.loading) return
    if (!PHONE_REGEX.test(this.data.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    if (!this.data.code) {
      wx.showToast({ title: '请输入验证码', icon: 'none' })
      return
    }
    if (!this.data.openid) {
      wx.showToast({ title: '微信身份获取失败，请重启小程序', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    memberApi.wxBind({
      openid: this.data.openid,
      phone: this.data.phone,
      code: this.data.code
    }).then(res => {
      this.setData({ loading: false })
      wx.setStorageSync('token', res.token)
      wx.setStorageSync('resident', res.resident)
      getApp().globalData.needBind = false
      wx.showToast({ title: '绑定成功', icon: 'success' })
      setTimeout(() => {
        wx.reLaunch({ url: '/pages/home/home' })
      }, 800)
    }).catch(() => {
      this.setData({ loading: false })
    })
  }
})
