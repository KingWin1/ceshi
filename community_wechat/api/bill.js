// api/bill.js —— 物业缴费相关接口统一管理
const { get, post } = require('../utils/request')

module.exports = {
  // 分页查询我的账单列表 status：空-全部 0-未缴 1-已缴
  getBillPage: (params) => get('/api/bill/page', params),
  // 账单详情
  getBillDetail: (id) => get(`/api/bill/detail?id=${id}`),
  // 发起缴费（微信支付下单）
  payBill: (data) => post('/api/bill/pay', data),
  // 分页查询缴费记录
  getPayRecordPage: (params) => get('/api/bill/payRecords', params)
}
