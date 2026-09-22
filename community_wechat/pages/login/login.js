// pages/login/login.js —— 登录入口页：微信一键登录 + 其他登录方式路由
const memberApi = require('../../api/member')
const icons = require('../../utils/icons')
const { APPID } = require('../../config')

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    wxAvatar: '',       // 微信头像（chooseAvatar获取）
    wxNickname: '',     // 微信昵称
    wxLoading: false    // 微信登录中
  },

  onLoad() {
    // 沉浸式：状态栏高度撑开渐变区
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
    // 回显上次填写的微信昵称
    const profile = wx.getStorageSync('wxProfile') || {}
    if (profile.nickname) {
      this.setData({ wxNickname: profile.nickname })
    }
  },

  onBackTap() {
    wx.navigateBack({
      fail: () => wx.reLaunch({ url: '/pages/home/home' })
    })
  },

  /**
   * 选择微信头像（open-type="chooseAvatar"回调）
   */
  onChooseAvatar(e) {
    const avatarUrl = e.detail.avatarUrl
    if (!avatarUrl) return
    this.setData({ wxAvatar: avatarUrl })
  },

  /**
   * 填写微信昵称（type="nickname"输入框失焦回调）
   */
  onNickBlur(e) {
    this.setData({ wxNickname: e.detail.value })
    // 头像为临时路径仅本次会话有效，昵称持久化回显
    wx.setStorageSync('wxProfile', { nickname: e.detail.value })
  },

  /**
   * 微信一键登录：code换openid，已绑定直接进首页，未绑定引导去绑定
   */
  onWxLoginTap() {
    if (this.data.wxLoading) return
    this.setData({ wxLoading: true })
    wx.login({
      success: res => {
        const userInfo = JSON.stringify({
          nickname: this.data.wxNickname,
          avatarUrl: this.data.wxAvatar
        })
        memberApi.wxLogin({ code: res.code, appid: APPID, userInfo: userInfo }).then(data => {
          this.setData({ wxLoading: false })
          if (data && !data.needBind && data.token) {
            this.saveLogin(data)
            return
          }
          // 未绑定居民账号：记录openid，引导去绑定
          const openid = (data && data.openid) || wx.getStorageSync('openid') || ''
          wx.setStorageSync('openid', openid)
          getApp().globalData.openid = openid
          getApp().globalData.needBind = true
          wx.showModal({
            title: '需要绑定账号',
            content: '该微信还未绑定居民账号，是否立即绑定？',
            confirmText: '去绑定',
            cancelText: '取消',
            success: r => {
              if (r.confirm) {
                wx.navigateTo({ url: '/pages/bind/bind' })
              }
            }
          })
        }).catch(() => {
          this.setData({ wxLoading: false })
        })
      },
      fail: () => {
        this.setData({ wxLoading: false })
        wx.showToast({ title: '微信授权失败，请重试', icon: 'none' })
      }
    })
  },

  /**
   * 保存登录态并跳转首页
   */
  saveLogin(res) {
    wx.setStorageSync('token', res.token)
    wx.setStorageSync('resident', res.resident)
    getApp().globalData.needBind = false
    wx.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      wx.reLaunch({ url: '/pages/home/home' })
    }, 800)
  },

  /**
   * 跳转验证码登录页
   */
  onSmsLoginTap() {
    wx.navigateTo({ url: '/pages/account-login/account-login?tab=sms' })
  },

  /**
   * 跳转密码登录页
   */
  onPwdLoginTap() {
    wx.navigateTo({ url: '/pages/account-login/account-login?tab=pwd' })
  }
})
