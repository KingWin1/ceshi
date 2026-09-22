// pages/bill-detail/bill-detail.js —— 账单详情（含微信支付）
const icons = require('../../utils/icons')
const billApi = require('../../api/bill')

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    billId: '',
    detail: null,
    paying: false
  },

  onLoad(options) {
    const info = wx.getSystemInfoSync()
    this.setData({
      statusBarHeight: info.statusBarHeight || 20,
      billId: options.id || ''
    })
    if (options.id) this.loadDetail(options.id)
  },

  onBackTap() {
    wx.navigateBack()
  },

  loadDetail(id) {
    billApi.getBillDetail(id).then(res => {
      const detail = Object.assign({}, res, {
        amount: Number(res.amount || 0).toFixed(2),
        statusName: res.status === 1 ? '已缴清' : '待缴费',
        // 无明细时兜底展示总额
        feeItems: (res.feeItems || []).map(f => Object.assign({}, f, {
          amount: Number(f.amount || 0).toFixed(2)
        }))
      })
      if (detail.feeItems.length === 0) {
        detail.feeItems = [{ name: detail.itemName || '物业费', amount: detail.amount }]
      }
      this.setData({ detail: detail })
    })
  },

  /**
   * 发起缴费：后端下单 → requestPayment 唤起微信支付
   */
  onPayTap() {
    if (this.data.paying) return
    this.setData({ paying: true })
    billApi.payBill({ billId: this.data.billId }).then(payParams => {
      // 后端返回微信支付参数；mock环境返回 null 时直接提示成功
      if (!payParams || !payParams.prepayId) {
        this.onPaySuccess()
        return
      }
      wx.requestPayment({
        timeStamp: payParams.timeStamp,
        nonceStr: payParams.nonceStr,
        package: 'prepay_id=' + payParams.prepayId,
        signType: payParams.signType || 'RSA',
        paySign: payParams.paySign,
        success: () => this.onPaySuccess(),
        fail: () => {
          this.setData({ paying: false })
          wx.showToast({ title: '支付已取消', icon: 'none' })
        }
      })
    }).catch(() => {
      this.setData({ paying: false })
    })
  },

  onPaySuccess() {
    this.setData({ paying: false })
    wx.showToast({ title: '缴费成功', icon: 'success' })
    // 刷新详情为已缴状态
    this.loadDetail(this.data.billId)
  }
})
