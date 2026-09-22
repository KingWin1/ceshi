// pages/account-login/account-login.js —— 手机号验证码/密码登录
const memberApi = require('../../api/member')
const icons = require('../../utils/icons')

const PHONE_REGEX = /^1[3-9]\d{9}$/

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    activeTab: 'sms',   // sms-验证码登录 pwd-密码登录
    phone: '',
    code: '',
    password: '',
    pwdVisible: false,
    countdown: 0,
    loading: false
  },

  onLoad(options) {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
    // 登录入口页携带tab参数：sms-验证码登录 pwd-密码登录
    if (options && options.tab === 'pwd') {
      this.setData({ activeTab: 'pwd' })
    }
    // 注册/找回密码成功后携带手机号跳转，回填方便直接登录
    if (options && options.phone) {
      this.setData({ phone: options.phone })
    }
  },

  onUnload() {
    // 页面销毁清理倒计时定时器
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

  onTabSms() {
    this.setData({ activeTab: 'sms' })
  },

  onTabPwd() {
    this.setData({ activeTab: 'pwd' })
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
   * 获取验证码（60秒倒计时）
   */
  onSendCodeTap() {
    if (this.data.countdown > 0) return
    if (!this.checkPhone()) return
    memberApi.sendSmsCode(this.data.phone, 2).then(res => {
      // 开发环境后端回显验证码，直接填入方便测试
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
   * 登录
   */
  onLoginTap() {
    if (this.data.loading) return
    if (!this.checkPhone()) return

    if (this.data.activeTab === 'sms') {
      this.loginBySms()
    } else {
      this.loginByPassword()
    }
  },

  loginBySms() {
    const { phone, code } = this.data
    if (!code) {
      wx.showToast({ title: '请输入验证码', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    // 1. 先校验手机号+验证码
    memberApi.verifySmsCode(phone, code).then(pass => {
      if (!pass) {
        this.setData({ loading: false })
        wx.showToast({ title: '验证码错误', icon: 'none' })
        return Promise.reject(new Error('验证码错误'))
      }
      // 2. 验证通过后查询该手机号居民信息与token
      return memberApi.loginBySms({ phone: phone, code: code })
    }).then(res => {
      this.setData({ loading: false })
      this.saveLogin(res)
    }).catch(() => {
      this.setData({ loading: false })
    })
  },

  loginByPassword() {
    const { phone, password } = this.data
    if (password.length < 6) {
      wx.showToast({ title: '密码不能少于6位', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    memberApi.loginByPassword({ phone: phone, password: password }).then(res => {
      this.setData({ loading: false })
      this.saveLogin(res)
    }).catch(() => {
      this.setData({ loading: false })
    })
  },

  /**
   * 保存登录态并跳转首页
   */
  saveLogin(res) {
    wx.setStorageSync('token', res.token)
    wx.setStorageSync('resident', res.resident)
    const app = getApp()
    app.globalData.needBind = false
    wx.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      wx.reLaunch({ url: '/pages/home/home' })
    }, 800)
  },

  checkPhone() {
    if (!PHONE_REGEX.test(this.data.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return false
    }
    return true
  },

  onForgotTap() {
    wx.navigateTo({ url: '/pages/forgot-password/forgot-password' })
  },

  onRegisterTap() {
    wx.navigateTo({ url: '/pages/register/register' })
  }
})
