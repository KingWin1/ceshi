// api/member.js —— 居民认证相关接口统一管理
const { get, post } = require('../utils/request')

module.exports = {
  // 获取手机验证码 type：1-注册 2-登录 3-找回密码
  sendSmsCode: (phone, type) => post(`/api/residentAuth/sendSmsCode?phone=${phone}&type=${type}`),
  // 校验手机号+验证码
  verifySmsCode: (phone, code) => post(`/api/residentAuth/verifySmsCode?phone=${phone}&code=${code}`),
  // 手机号+验证码登录
  loginBySms: (data) => post('/api/residentAuth/loginBySms', data),
  // 手机号+密码登录
  loginByPassword: (data) => post('/api/residentAuth/loginByPassword', data),
  // 注册
  register: (data) => post('/api/residentAuth/register', data),
  // 找回密码（重置密码）
  resetPassword: (data) => post('/api/residentAuth/resetPassword', data),
  // 微信静默登录
  wxLogin: (data) => post('/api/residentAuth/wxLogin', data),
  // 微信绑定居民账号
  wxBind: (data) => post('/api/residentAuth/wxBind', data)
}
