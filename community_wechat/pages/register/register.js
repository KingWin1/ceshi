// pages/register/register.js —— 两步注册
const memberApi = require('../../api/member')
const icons = require('../../utils/icons')

const PHONE_REGEX = /^1[3-9]\d{9}$/

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    step: 1,
    phone: '',
    code: '',
    password: '',
    confirmPassword: '',
    pwdVisible: false,
    confirmVisible: false,
    agreed: false,
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
    // 第二步时先回第一步，第一步时退出页面
    if (this.data.step === 2) {
      this.setData({ step: 1 })
      return
    }
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

  onConfirmInput(e) {
    this.setData({ confirmPassword: e.detail.value })
  },

  onTogglePwd() {
    this.setData({ pwdVisible: !this.data.pwdVisible })
  },

  onToggleConfirm() {
    this.setData({ confirmVisible: !this.data.confirmVisible })
  },

  onToggleAgree() {
    this.setData({ agreed: !this.data.agreed })
  },

  onAgreementTap() {
    wx.showToast({ title: '请查看小程序内协议说明', icon: 'none' })
  },

  onPrivacyTap() {
    wx.showToast({ title: '请查看小程序内隐私政策说明', icon: 'none' })
  },

  /**
   * 获取注册验证码（type=1）
   */
  onSendCodeTap() {
    if (this.data.countdown > 0) return
    if (!PHONE_REGEX.test(this.data.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    memberApi.sendSmsCode(this.data.phone, 1).then(res => {
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
   * 第一步：校验验证码通过进入第二步；第二步：提交注册
   */
  onSubmitTap() {
    if (this.data.loading) return
    if (!this.data.agreed) {
      wx.showToast({ title: '请先阅读并同意用户协议', icon: 'none' })
      return
    }
    if (this.data.step === 1) {
      this.verifyAndNext()
    } else {
      this.submitRegister()
    }
  },

  verifyAndNext() {
    if (!PHONE_REGEX.test(this.data.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    if (!this.data.code) {
      wx.showToast({ title: '请输入验证码', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    memberApi.verifySmsCode(this.data.phone, this.data.code).then(pass => {
      this.setData({ loading: false })
      if (!pass) {
        wx.showToast({ title: '验证码错误', icon: 'none' })
        return
      }
      this.setData({ step: 2 })
    }).catch(() => {
      this.setData({ loading: false })
    })
  },

  submitRegister() {
    const { phone, code, password, confirmPassword } = this.data
    if (password.length < 6) {
      wx.showToast({ title: '密码不能少于6位', icon: 'none' })
      return
    }
    if (password !== confirmPassword) {
      wx.showToast({ title: '两次输入的密码不一致', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    memberApi.register({ phone: phone, code: code, password: password }).then(() => {
      this.setData({ loading: false })
      wx.showToast({ title: '注册成功', icon: 'success' })
      // 注册成功后携带手机号跳转登录页，方便直接登录
      setTimeout(() => {
        wx.redirectTo({ url: '/pages/account-login/account-login?phone=' + phone })
      }, 800)
    }).catch(() => {
      this.setData({ loading: false })
    })
  }
})
