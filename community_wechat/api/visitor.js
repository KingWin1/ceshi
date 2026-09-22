// api/visitor.js —— 访客管理相关接口统一管理
const { get, post } = require('../utils/request')

module.exports = {
  // 创建访客邀请，返回邀请码/通行凭证
  createInvite: (data) => post('/api/visitor/invite', data),
  // 分页查询我的访客记录
  getVisitorPage: (params) => get('/api/visitor/page', params)
}
