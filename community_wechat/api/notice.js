// api/notice.js —— 公告通知相关接口统一管理
const { get } = require('../utils/request')

module.exports = {
  // 分页查询公告列表
  getNoticePage: (params) => get('/api/notice/page', params),
  // 公告详情
  getNoticeDetail: (id) => get(`/api/notice/detail?id=${id}`),
  // 分页查询我的消息（缴费提醒/报修进度等）
  getMessagePage: (params) => get('/api/notice/messages', params),
  // 全部消息标记已读
  readAllMessages: () => get('/api/notice/readAll')
}
