// api/family.js —— 家庭成员相关接口统一管理
const { get, post } = require('../utils/request')

module.exports = {
  // 查询我的家庭成员列表
  getFamilyList: () => get('/api/family/list'),
  // 添加家庭成员
  addFamilyMember: (data) => post('/api/family/add', data),
  // 删除家庭成员
  removeFamilyMember: (id) => post(`/api/family/remove?id=${id}`)
}
