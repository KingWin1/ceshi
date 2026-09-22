import { get, post, put, del, request } from '@/utils/request'

/**
 * API 接口管理类
 * 统一管理所有后端接口调用
 */
class Api {
  // ==================== 楼栋模块 ====================

  // 分页+条件查询楼栋列表
  getBuildingPage(params) {
    return get('/api/building/page', params)
  }

  // 添加楼栋信息
  addBuilding(data) {
    return post('/api/building/add', data)
  }

  // 删除楼栋信息
  deleteBuilding(buildingId) {
    return del('/api/building/delete', { buildingId })
  }

  // 批量删除楼栋信息（后端为 POST，请求体为 ID 数组）
  deleteBuildingBatch(ids) {
    return request({ method: 'post', url: '/api/building/batchDelete', data: ids })
  }

  // 根据ID查询楼栋信息
  getBuildingById(buildingId) {
    return get('/api/building/getById', { buildingId })
  }

  // 修改楼栋信息
  updateBuilding(data) {
    return post('/api/building/update', data)
  }

  // 查询全部楼栋信息
  getBuildingList() {
    return get('/api/building/list')
  }

  // ==================== 房屋模块 ====================

  // 分页+条件查询房屋列表
  getHousePage(params) {
    return get('/api/house/page', params)
  }

  // 添加房屋信息
  addHouse(data) {
    return post('/api/house/add', data)
  }

  // 删除房屋信息
  deleteHouse(houseId) {
    return del('/api/house/delete', { houseId })
  }

  // 批量删除房屋信息（请求体为 ID 数组）
  deleteHouseBatch(ids) {
    return request({ method: 'post', url: '/api/house/batchDelete', data: ids })
  }

  // 根据ID查询房屋信息（含户型室/厅/卫）
  getHouseById(houseId) {
    return get('/api/house/getById', { houseId })
  }

  // 修改房屋信息
  updateHouse(data) {
    return post('/api/house/update', data)
  }

  // ==================== 认证模块（房屋删除联动） ====================

  // 根据房屋ID统计该房屋在认证表中出现的次数
  countAuthByHouseId(houseId) {
    return get('/api/auth/countByHouseId', { houseId })
  }

  // 根据房屋ID数组统计每个房屋在认证表中出现的次数
  countAuthByHouseIds(houseIds) {
    return request({ method: 'post', url: '/api/auth/countByHouseIds', data: houseIds })
  }

  // ==================== 居民模块 ====================

  // 分页+条件查询居民列表
  getResidentPage(params) {
    return get('/api/resident/page', params)
  }

  // 添加居民
  addResident(data) {
    return post('/api/resident/add', data)
  }

  // 删除居民信息
  deleteResident(residentId) {
    return del('/api/resident/delete', { residentId })
  }

  // 批量删除居民信息（请求体为 ID 数组）
  deleteResidentBatch(ids) {
    return request({ method: 'post', url: '/api/resident/batchDelete', data: ids })
  }

  // 修改回显：查询居民信息和该居民的房屋信息集合
  getResidentForUpdate(residentId) {
    return get('/api/resident/getForUpdate', { residentId })
  }

  // 修改居民信息
  updateResident(data) {
    return post('/api/resident/update', data)
  }

  // 根据楼栋ID和单元号查询对应房屋集合（房间号三级联动）
  getHouseByBuildingAndUnit(buildingId, unitNo) {
    return get('/api/house/listByBuildingIdAndUnitNo', { buildingId, unitNo })
  }

  // 根据楼栋ID、单元号、居民姓名/电话/房间号查询对应房屋信息（选择房间弹窗）
  getHouseListByCondition(params) {
    return get('/api/house/listByCondition', params)
  }

  // ==================== 认证模块（居民删除联动） ====================

  // 根据居民ID统计该居民在认证表中出现的次数
  countAuthByResidentId(residentId) {
    return get('/api/auth/countByResidentId', { residentId })
  }

  // 根据居民ID数组统计每个居民在认证表中出现的次数
  countAuthByResidentIds(residentIds) {
    return request({ method: 'post', url: '/api/auth/countByResidentIds', data: residentIds })
  }

  // ==================== 认证模块（房屋认证管理） ====================

  // 分页+条件查询认证列表
  getAuthPage(params) {
    return get('/api/auth/page', params)
  }

  // 根据认证ID删除认证信息
  deleteAuth(authId) {
    return del('/api/auth/delete', { authId })
  }

  // 根据认证ID数组批量删除认证信息（请求体为 ID 数组）
  deleteAuthBatch(ids) {
    return request({ method: 'post', url: '/api/auth/batchDelete', data: ids })
  }

  // 根据认证ID修改审核状态（参数为 query 形式：authId/auditStatus/auditRemark/adminId）
  updateAuthAuditStatus(params) {
    return request({ method: 'post', url: '/api/auth/updateAuditStatus', params })
  }

  // 根据认证ID数组批量修改审核状态（请求体为 ID 数组，query 携带状态参数）
  batchUpdateAuthAuditStatus(ids, params) {
    return request({ method: 'post', url: '/api/auth/batchUpdateAuditStatus', data: ids, params })
  }

  // 添加认证信息
  addAuth(data) {
    return post('/api/auth/add', data)
  }

  // 根据认证ID查询指定认证信息（编辑回显）
  getAuthById(authId) {
    return get('/api/auth/getById', { authId })
  }

  // 根据认证ID修改指定认证信息
  updateAuth(data) {
    return post('/api/auth/update', data)
  }

  // 根据姓名或电话查询指定居民信息（居民搜索）
  searchResident(params) {
    return get('/api/resident/search', params)
  }

  // ==================== 文件上传 ====================

  // 上传文件（头像等），formData 需含 files 字段
  uploadFile(formData) {
    return post('/api/file/upload', formData)
  }

  // ==================== 验证码模块 ====================

  // 生成图形验证码
  getCaptcha() {
    return get('/api/captcha/generate')
  }

  // ==================== 管理员登录模块 ====================

  // 管理员登录
  login(data) {
    return post('/api/admin/login', data)
  }

  // ==================== 菜单模块 ====================

  // 根据父级ID查询菜单列表
  getMenuListByParentId(parentId) {
    return get('/api/menu/listByParentId', { parentId })
  }

  // 查询全部启用菜单（权限树数据源）
  getMenuListAll() {
    return get('/api/menu/listAll')
  }

  // 根据角色ID查询已分配的菜单列表
  getMenuListByRoleId(roleId) {
    return get('/api/menu/listByRoleId', { roleId })
  }

  // ==================== 首页统计模块 ====================

  // 首页聚合数据（统计卡片+图表+列表）
  getHomeStatistics(limit) {
    return get('/api/statistics/home', { limit })
  }

  // ==================== 投诉记录模块 ====================

  // 分页+条件查询投诉记录
  getComplaintRecordPage(params) {
    return get('/api/complaintRecord/page', params)
  }

  // 根据投诉记录ID查询指定投诉记录信息
  getComplaintRecordById(complaintId) {
    return get('/api/complaintRecord/detail', { complaintId })
  }

  // 添加投诉记录（请求体为投诉对象）
  addComplaintRecord(data) {
    return post('/api/complaintRecord/add', data)
  }

  // 根据投诉ID数组批量删除投诉记录（请求体为 ID 数组）
  deleteComplaintRecordBatch(ids) {
    return request({ method: 'post', url: '/api/complaintRecord/batchDelete', data: ids })
  }

  // 根据投诉ID修改投诉状态（参数为 query 形式：complaintId/status）
  updateComplaintStatus(params) {
    return request({ method: 'post', url: '/api/complaintRecord/updateStatus', params })
  }

  // ==================== 回复信息模块 ====================

  // 根据投诉记录ID、回复人类型、回复人ID查询回复信息集合
  getReplyInfoList(params) {
    return get('/api/replyInfo/list', params)
  }

  // 添加回复信息（请求体为回复对象）
  addReplyInfo(data) {
    return post('/api/replyInfo/add', data)
  }

  // 根据回复ID删除回复信息（参数为 query 形式：id）
  deleteReplyInfo(id) {
    return request({ method: 'post', url: '/api/replyInfo/delete', params: { id } })
  }

  // ==================== 公告模块 ====================

  // 分页+条件查询公告列表
  getNoticePage(params) {
    return get('/api/notice/page', params)
  }

  // 添加公告（请求体为公告对象）
  addNotice(data) {
    return post('/api/notice/add', data)
  }

  // 根据ID查询公告信息（编辑回显）
  getNoticeById(noticeId) {
    return get('/api/notice/getById', { noticeId })
  }

  // 根据ID修改公告信息（请求体为公告对象）
  updateNotice(data) {
    return post('/api/notice/update', data)
  }

  // 根据ID删除公告（参数为 query 形式：noticeId）
  deleteNotice(noticeId) {
    return del('/api/notice/delete', { noticeId })
  }

  // 批量删除公告（请求体为 ID 数组）
  deleteNoticeBatch(ids) {
    return request({ method: 'post', url: '/api/notice/batchDelete', data: ids })
  }

  // 根据ID修改公告状态（参数为 query 形式：noticeId/status）
  updateNoticeStatus(params) {
    return request({ method: 'post', url: '/api/notice/updateStatus', params })
  }

  // 批量修改公告状态（请求体为 ID 数组，query 携带状态参数）
  batchUpdateNoticeStatus(ids, status) {
    return request({ method: 'post', url: '/api/notice/batchUpdateStatus', data: ids, params: { status } })
  }

  // 批量修改公告置顶状态（请求体为 ID 数组，query 携带置顶参数：1-是 2-否）
  batchUpdateNoticeTop(ids, isTop) {
    return request({ method: 'post', url: '/api/notice/batchUpdateTop', data: ids, params: { isTop } })
  }

  // ==================== 广告模块 ====================

  // 分页+条件查询广告列表
  getAdvertisementPage(params) {
    return get('/api/advertisement/page', params)
  }

  // 添加广告（请求体为广告对象）
  addAdvertisement(data) {
    return post('/api/advertisement/add', data)
  }

  // 根据ID查询广告信息（编辑回显）
  getAdvertisementById(adId) {
    return get('/api/advertisement/getById', { adId })
  }

  // 根据ID修改广告信息（请求体为广告对象）
  updateAdvertisement(data) {
    return post('/api/advertisement/update', data)
  }

  // 根据ID删除广告（参数为 query 形式：adId）
  deleteAdvertisement(adId) {
    return del('/api/advertisement/delete', { adId })
  }

  // 批量删除广告（请求体为 ID 数组）
  deleteAdvertisementBatch(ids) {
    return request({ method: 'post', url: '/api/advertisement/batchDelete', data: ids })
  }

  // 根据ID修改广告状态（参数为 query 形式：adId/status，1-启用 2-停用）
  updateAdvertisementStatus(params) {
    return request({ method: 'post', url: '/api/advertisement/updateStatus', params })
  }

  // ==================== 门店模块 ====================

  // 分页+条件查询门店列表
  getStorePage(params) {
    return get('/api/store/page', params)
  }

  // 添加门店信息（请求体为门店对象）
  addStore(data) {
    return post('/api/store/add', data)
  }

  // 根据门店ID查询门店信息（编辑回显）
  getStoreById(storeId) {
    return get('/api/store/getById', { storeId })
  }

  // 根据门店ID查询门店详情（含分类名称）
  getStoreDetail(storeId) {
    return get('/api/store/detail', { storeId })
  }

  // 根据门店ID修改门店信息（请求体为门店对象）
  updateStore(data) {
    return post('/api/store/update', data)
  }

  // 根据门店ID删除门店信息（参数为 query 形式：storeId）
  deleteStore(storeId) {
    return del('/api/store/delete', { storeId })
  }

  // 批量删除门店信息（请求体为 ID 数组）
  deleteStoreBatch(ids) {
    return request({ method: 'post', url: '/api/store/batchDelete', data: ids })
  }

  // 查询全部的门店分类信息
  getStoreCategoryListAll() {
    return get('/api/storeCategory/listAll')
  }

  // ==================== 部门模块 ====================

  // 分页+条件查询部门列表
  getDepartmentPage(params) {
    return get('/api/department/page', params)
  }

  // 添加部门信息（请求体为部门对象）
  addDepartment(data) {
    return post('/api/department/add', data)
  }

  // 根据部门ID查询部门信息（编辑回显）
  getDepartmentById(deptId) {
    return get('/api/department/getById', { deptId })
  }

  // 根据部门ID修改部门信息（请求体为部门对象）
  updateDepartment(data) {
    return post('/api/department/update', data)
  }

  // 根据部门ID删除部门信息（联动删除岗位，参数为 query 形式：deptId）
  deleteDepartment(deptId) {
    return del('/api/department/delete', { deptId })
  }

  // 根据部门ID数组批量删除部门信息（请求体为 ID 数组）
  deleteDepartmentBatch(ids) {
    return request({ method: 'post', url: '/api/department/batchDelete', data: ids })
  }

  // 根据部门ID统计该部门在岗位表中出现的次数
  countPositionByDeptId(deptId) {
    return get('/api/department/countPositionByDeptId', { deptId })
  }

  // 根据部门ID数组统计每个部门在岗位表中出现的次数（请求体为 ID 数组）
  countPositionByDeptIds(deptIds) {
    return request({ method: 'post', url: '/api/department/countPositionByDeptIds', data: deptIds })
  }

  // ==================== 岗位模块 ====================

  // 分页+条件查询岗位列表
  getPositionPage(params) {
    return get('/api/position/page', params)
  }

  // 添加岗位信息（请求体为岗位对象）
  addPosition(data) {
    return post('/api/position/add', data)
  }

  // 根据岗位ID查询岗位信息（编辑回显）
  getPositionById(positionId) {
    return get('/api/position/getById', { positionId })
  }

  // 根据岗位ID修改岗位信息（请求体为岗位对象）
  updatePosition(data) {
    return post('/api/position/update', data)
  }

  // 根据岗位ID删除岗位信息（联动删除员工，参数为 query 形式：positionId）
  deletePosition(positionId) {
    return del('/api/position/delete', { positionId })
  }

  // 根据岗位ID数组批量删除岗位信息（请求体为 ID 数组）
  deletePositionBatch(ids) {
    return request({ method: 'post', url: '/api/position/batchDelete', data: ids })
  }

  // 根据岗位ID统计该岗位在员工表中出现的次数
  countEmployeeByPositionId(positionId) {
    return get('/api/position/countEmployeeByPositionId', { positionId })
  }

  // 根据岗位ID数组统计每个岗位在员工表中出现的次数（请求体为 ID 数组）
  countEmployeeByPositionIds(positionIds) {
    return request({ method: 'post', url: '/api/position/countEmployeeByPositionIds', data: positionIds })
  }

  // 根据部门ID查询岗位列表
  getPositionByDeptId(deptId) {
    return get('/api/position/listByDeptId', { deptId })
  }

  // 查询全部岗位信息（下拉框数据源）
  getPositionListAll() {
    return get('/api/position/listAll')
  }

  // ==================== 员工模块 ====================

  // 分页+条件查询员工列表
  getEmployeePage(params) {
    return get('/api/employee/page', params)
  }

  // 添加员工信息（请求体为员工对象）
  addEmployee(data) {
    return post('/api/employee/add', data)
  }

  // 根据员工ID查询员工信息（编辑回显）
  getEmployeeById(employeeId) {
    return get('/api/employee/getById', { employeeId })
  }

  // 根据员工ID修改员工信息（请求体为员工对象）
  updateEmployee(data) {
    return post('/api/employee/update', data)
  }

  // 根据员工ID删除员工信息（参数为 query 形式：employeeId）
  deleteEmployee(employeeId) {
    return del('/api/employee/delete', { employeeId })
  }

  // 根据员工ID数组批量删除员工信息（请求体为 ID 数组）
  deleteEmployeeBatch(ids) {
    return request({ method: 'post', url: '/api/employee/batchDelete', data: ids })
  }

  // 查询全部员工信息（派单下拉框数据源）
  getEmployeeListAll() {
    return get('/api/employee/listAll')
  }

  // ==================== 报修模块 ====================

  // 查询全部报修类型信息
  getRepairTypeListAll() {
    return get('/api/repairType/listAll')
  }

  // 分页+条件查询报修记录
  getRepairPage(params) {
    return get('/api/repairRecord/page', params)
  }

  // 根据报修ID查询报修记录详情
  getRepairDetail(repairId) {
    return get('/api/repairRecord/detail', { repairId })
  }

  // 派单（参数为 query 形式）
  dispatchRepair(params) {
    return request({ method: 'post', url: '/api/repairRecord/dispatch', params })
  }

  // 完工（参数为 query 形式，完工图片多张以逗号分隔）
  completeRepair(params) {
    return request({ method: 'post', url: '/api/repairRecord/complete', params })
  }

  // 添加报修记录（请求体为报修对象）
  addRepair(data) {
    return post('/api/repairRecord/add', data)
  }

  // 根据报修ID数组批量删除报修记录（请求体为 ID 数组）
  deleteRepairBatch(ids) {
    return request({ method: 'post', url: '/api/repairRecord/batchDelete', data: ids })
  }

  // ==================== 管理员模块 ====================

  // 分页+条件查询管理员列表
  getAdminPage(params) {
    return get('/api/admin/page', params)
  }

  // 添加管理员信息（请求体为管理员对象）
  addAdmin(data) {
    return post('/api/admin/add', data)
  }

  // 根据管理员ID查询管理员信息（编辑回显）
  getAdminById(adminId) {
    return get('/api/admin/getById', { adminId })
  }

  // 根据管理员ID修改管理员信息（请求体为管理员对象）
  updateAdmin(data) {
    return post('/api/admin/update', data)
  }

  // 根据管理员ID删除管理员信息（参数为 query 形式：adminId）
  deleteAdmin(adminId) {
    return del('/api/admin/delete', { adminId })
  }

  // 根据管理员ID数组批量删除管理员信息（请求体为 ID 数组）
  deleteAdminBatch(ids) {
    return request({ method: 'post', url: '/api/admin/batchDelete', data: ids })
  }

  // 根据管理员ID修改状态（启用/禁用，参数为 query 形式：adminId/status）
  updateAdminStatus(adminId, status) {
    return request({ method: 'post', url: '/api/admin/updateStatus', params: { adminId, status } })
  }

  // 根据管理员ID重置密码（重置为默认密码 123456）
  resetAdminPassword(adminId) {
    return request({ method: 'post', url: '/api/admin/resetPassword', params: { adminId } })
  }

  // ==================== 角色模块 ====================

  // 分页+条件查询角色
  getRolePage(params) {
    return get('/api/role/page', params)
  }

  // 查询全部角色信息（下拉框数据源）
  getRoleListAll() {
    return get('/api/role/listAll')
  }

  // 根据角色ID查询角色信息（含已分配菜单权限，编辑回显）
  getRoleById(roleId) {
    return get('/api/role/getById', { roleId })
  }

  // 添加角色并分配权限（请求体为角色对象 + menuIds）
  addRoleWithMenu(data) {
    return post('/api/role/add', data)
  }

  // 修改角色并重新分配权限（请求体为角色对象 + menuIds）
  updateRoleWithMenu(data) {
    return post('/api/role/update', data)
  }

  // 删除角色（被管理员占用时不可删除，参数为 query 形式：roleId）
  deleteRole(roleId) {
    return del('/api/role/delete', { roleId })
  }

  // 批量删除角色（仅删除未被管理员占用的角色，请求体为 ID 数组）
  deleteRoleBatch(ids) {
    return request({ method: 'post', url: '/api/role/batchDelete', data: ids })
  }

  // ==================== 客服模块 ====================

  // 分页+条件查询客服
  getCustomerServicePage(params) {
    return get('/api/customerService/page', params)
  }

  // 根据客服ID查询客服信息（编辑回显）
  getCustomerServiceById(csId) {
    return get('/api/customerService/getById', { csId })
  }

  // 添加客服信息（请求体为客服对象）
  addCustomerService(data) {
    return post('/api/customerService/add', data)
  }

  // 根据客服ID修改客服信息（请求体为客服对象）
  updateCustomerService(data) {
    return post('/api/customerService/update', data)
  }

  // 根据客服ID删除客服信息（参数为 query 形式：csId）
  deleteCustomerService(csId) {
    return del('/api/customerService/delete', { csId })
  }

  // 批量删除客服（请求体为 ID 数组）
  deleteCustomerServiceBatch(ids) {
    return request({ method: 'post', url: '/api/customerService/batchDelete', data: ids })
  }

  // ==================== 费用类型模块 ====================

  // 查询全部费用类型
  getFeeTypeList() {
    return get('/api/feeType/listAll')
  }

  // 分页+条件查询费用类型列表
  getFeeTypePage(params) {
    return get('/api/feeType/page', params)
  }

  // 根据费用类型ID查询费用类型信息（编辑回显）
  getFeeTypeById(feeTypeId) {
    return get('/api/feeType/getById', { feeTypeId })
  }

  // 添加费用类型信息（请求体为费用类型对象）
  addFeeType(data) {
    return post('/api/feeType/add', data)
  }

  // 根据费用类型ID修改费用类型信息（请求体为费用类型对象）
  updateFeeType(data) {
    return post('/api/feeType/update', data)
  }

  // 根据费用类型ID删除费用类型信息（被账单占用时不可删除，参数为 query 形式：feeTypeId）
  deleteFeeType(feeTypeId) {
    return del('/api/feeType/delete', { feeTypeId })
  }

  // 根据费用类型ID修改单价信息（计价设置，参数为 query 形式）
  updateFeeTypePrice(params) {
    return request({ method: 'post', url: '/api/feeType/updatePrice', params })
  }

  // ==================== 账单模块 ====================

  // 分页+条件查询账单列表
  getBillPage(params) {
    return get('/api/bill/page', params)
  }

  // 根据账单ID查询账单详情
  getBillDetail(billId) {
    return get('/api/bill/detail', { billId })
  }

  // 根据账单ID缴费（后端为 POST，参数为 query 形式）
  payBill(params) {
    return request({ method: 'post', url: '/api/bill/pay', params })
  }

  // 根据账单ID数组批量删除账单（请求体为 ID 数组）
  deleteBillBatch(ids) {
    return request({ method: 'post', url: '/api/bill/batchDelete', data: ids })
  }

  // 导出账单信息（返回文件流，responseType 必须为 blob）
  exportBill(data) {
    return request({ method: 'post', url: '/api/bill/export', data, responseType: 'blob' })
  }

  // 下载账单导入Excel模板（返回文件流）
  downloadBillTemplate() {
    return request({ method: 'get', url: '/api/bill/downloadTemplate', responseType: 'blob' })
  }

  // 导入账单信息（FormData：file + overwrite）
  importBill(formData) {
    return request({
      method: 'post',
      url: '/api/bill/import',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' },
      timeout: 60000
    })
  }

  // ==================== 消息模块 ====================

  // 添加消息（催缴）
  addMessage(params) {
    return request({ method: 'post', url: '/api/message/add', params })
  }

  // 批量添加消息（批量催缴，请求体为消息对象集合）
  batchAddMessage(list) {
    return request({ method: 'post', url: '/api/message/batchAdd', data: list })
  }
}

// 导出 Api 实例
export default new Api()
