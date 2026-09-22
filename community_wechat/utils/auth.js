// utils/auth.js —— 登录态统一处理
/**
 * 需登录的跳转统一入口：未登录跳登录页
 * @param {String} url 目标页面路径
 */
function requireLogin(url) {
  if (!wx.getStorageSync('token')) {
    wx.navigateTo({ url: '/pages/login/login' })
    return false
  }
  wx.navigateTo({ url: url })
  return true
}

/**
 * 退出登录：清除登录态缓存
 */
function logout() {
  wx.removeStorageSync('token')
  wx.removeStorageSync('resident')
  getApp().globalData.needBind = false
}

/**
 * 是否已登录
 */
function isLogin() {
  return !!wx.getStorageSync('token')
}

module.exports = { requireLogin, logout, isLogin }
