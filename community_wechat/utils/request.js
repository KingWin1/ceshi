// utils/request.js —— wx.request Promise封装
const { BASE_URL } = require('../config')

/**
 * 统一请求方法
 * @param {Object} options { url, method, data, header }
 */
function request(options) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: Object.assign({
        'Content-Type': 'application/json',
        'Authorization': wx.getStorageSync('token') || ''
      }, options.header || {}),
      timeout: 15000,
      success(res) {
        if (res.statusCode !== 200) {
          wx.showToast({ title: `网络错误(${res.statusCode})`, icon: 'none' })
          reject(res)
          return
        }
        const body = res.data
        // 统一响应结构 { code, msg, data }，code=00000 成功
        if (body.code === '00000') {
          resolve(body.data)
        } else if (body.code === '401') {
          // token失效：清除登录态，跳登录页
          wx.removeStorageSync('token')
          wx.removeStorageSync('resident')
          wx.navigateTo({ url: '/pages/login/login' })
          reject(body)
        } else {
          wx.showToast({ title: body.msg || '请求失败', icon: 'none' })
          reject(body)
        }
      },
      fail(err) {
        wx.showToast({ title: '网络连接失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

const get = (url, data) => request({ url, method: 'GET', data })
const post = (url, data) => request({ url, method: 'POST', data })

module.exports = { request, get, post }
