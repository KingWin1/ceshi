// pages/forgot-password/forgot-password.js
const memberApi = require('../../api/member')
const icons = require('../../utils/icons')

const PHONE_REGEX = /^1[3-9]\d{9}$/

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    phone: '',
    code: '',
    password: '',
    pwdVisible: false,
    countdown: 0,
    loading: false
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
  },

  onUnload() {
    if (this.timer) {
      clearInterval(this.timer)
      this.timer = null
    }
  },

  onBackTap() {
    wx.navigateBack({
      fail: () => wx.reLaunch({ url: '/pages/login/login' })
    })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onCodeInput(e) {
    this.setData({ code: e.detail.value })
  },

  onPwdInput(e) {
    this.setData({ password: e.detail.value })
  },

  onTogglePwd() {
    this.setData({ pwdVisible: !this.data.pwdVisible })
  },

  /**
   * 获取找回密码验证码（type=3）
   */
  onSendCodeTap() {
    if (this.data.countdown > 0) return
    if (!PHONE_REGEX.test(this.data.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    memberApi.sendSmsCode(this.data.phone, 3).then(res => {
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
   * 下一步：校验验证码通过后重置密码
   */
  onSubmitTap() {
    if (this.data.loading) return
    if (!PHONE_REGEX.test(this.data.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    if (!this.data.code) {
      wx.showToast({ title: '请输入验证码', icon: 'none' })
      return
    }
    if (this.data.password.length < 6 || this.data.password.length > 12) {
      wx.showToast({ title: '请输入6-12位的新密码', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    memberApi.verifySmsCode(this.data.phone, this.data.code).then(pass => {
      if (!pass) {
        this.setData({ loading: false })
        wx.showToast({ title: '验证码错误', icon: 'none' })
        return Promise.reject(new Error('验证码错误'))
      }
      // 验证通过后按手机号重置居民密码
      return memberApi.resetPassword({
        phone: this.data.phone,
        code: this.data.code,
        password: this.data.password
      })
    }).then(() => {
      this.setData({ loading: false })
      wx.showToast({ title: '密码重置成功', icon: 'success' })
      setTimeout(() => {
        wx.redirectTo({ url: '/pages/account-login/account-login?phone=' + this.data.phone })
      }, 800)
    }).catch(() => {
      this.setData({ loading: false })
    })
  }
})
