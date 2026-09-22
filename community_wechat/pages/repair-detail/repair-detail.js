// pages/repair-detail/repair-detail.js —— 报修详情（含处理进度时间线）
const icons = require('../../utils/icons')
const repairApi = require('../../api/repair')

// 状态映射：0-待审核 1-待维修 2-已完成 3-已取消
const STATUS_MAP = {
  0: { name: '待审核', key: 'pending' },
  1: { name: '待维修', key: 'doing' },
  2: { name: '已完成', key: 'done' },
  3: { name: '已取消', key: 'cancel' }
}

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    repairId: '',
    detail: null,
    timeline: [],
    loadFailed: false
  },

  onLoad(options) {
    const info = wx.getSystemInfoSync()
    this.setData({
      statusBarHeight: info.statusBarHeight || 20,
      repairId: options.id || ''
    })
    if (options.id) this.loadDetail(options.id)
  },

  onBackTap() {
    wx.navigateBack()
  },

  /**
   * 加载详情并构建进度时间线
   */
  loadDetail(id) {
    repairApi.getRepairDetail(id).then(res => {
      const st = STATUS_MAP[res.status] || STATUS_MAP[0]
      const detail = Object.assign({}, res, {
        typeName: res.typeName || '维修',
        statusName: st.name,
        statusKey: st.key
      })
      this.setData({
        detail: detail,
        timeline: this.buildTimeline(detail)
      })
    }).catch(() => {
      this.setData({ loadFailed: true })
    })
  },

  /**
   * 根据当前状态生成时间线（倒序：最新在前）
   */
  buildTimeline(detail) {
    const steps = [{ title: '提交报修', time: detail.createTime || '' }]
    if (detail.status >= 1 || detail.status === 3) {
      steps.push({ title: '审核通过，等待维修', time: detail.auditTime || '' })
    }
    if (detail.status >= 2) {
      steps.push({
        title: '维修完成',
        time: detail.finishTime || '',
        remark: detail.remark || ''
      })
    }
    if (detail.status === 3) {
      steps.push({ title: '报修已取消', time: detail.cancelTime || '' })
    }
    return steps.reverse()
  },

  onPreviewImage(e) {
    const urls = this.data.detail.images || []
    wx.previewImage({ current: e.currentTarget.dataset.url, urls: urls })
  },

  /**
   * 取消报修（仅待审核状态）
   */
  onCancelTap() {
    wx.showModal({
      title: '提示',
      content: '确定要取消这条报修申请吗？',
      success: res => {
        if (!res.confirm) return
        repairApi.cancelRepair(this.data.repairId).then(() => {
          wx.showToast({ title: '已取消', icon: 'success' })
          this.loadDetail(this.data.repairId)
        })
      }
    })
  }
})
