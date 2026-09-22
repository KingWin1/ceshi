// api/repair.js —— 在线报修相关接口统一管理
const { get, post } = require('../utils/request')

module.exports = {
  // 分页查询我的报修列表 status：空-全部 0-待审核 1-待维修 2-已完成
  getRepairPage: (params) => get('/api/repair/page', params),
  // 提交报修
  submitRepair: (data) => post('/api/repair/submit', data),
  // 报修详情
  getRepairDetail: (id) => get(`/api/repair/detail?id=${id}`),
  // 取消报修（仅待审核可取消）
  cancelRepair: (id) => post(`/api/repair/cancel?id=${id}`)
}
