// app.js —— 小程序入口：静默登录
const memberApi = require('./api/member')
const { APPID } = require('./config')

App({
  onLaunch() {
    // 启动即静默登录：wx.login 换 openid，已绑定居民则自动登录
    this.silentLogin()
  },

  /**
   * 静默登录（对页面透明，失败不影响游客浏览）
   */
  silentLogin() {
    wx.login({
      success: res => {
        memberApi.wxLogin({ code: res.code, appid: APPID }).then(data => {
          if (data && !data.needBind && data.token) {
            // 已绑定：存token与用户信息，下次进入全程无感
            wx.setStorageSync('token', data.token)
            wx.setStorageSync('resident', data.resident)
            wx.setStorageSync('openid', data.openid)
            this.globalData.openid = data.openid
            this.globalData.needBind = false
          } else {
            // 未绑定：仅记录openid，等待用户主动绑定
            wx.setStorageSync('openid', data.openid)
            this.globalData.openid = data.openid
            this.globalData.needBind = true
          }
        }).catch(() => {
          // 静默登录失败不阻塞使用
        })
      }
    })
  },

  globalData: {
    userInfo: null,
    openid: '',
    needBind: false
  }
})
