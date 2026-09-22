// pages/family-member/family-member.js —— 家庭成员列表
const icons = require('../../utils/icons')
const familyApi = require('../../api/family')

// 角色：1-业主 2-家属 3-租户
const ROLE_MAP = { 1: '业主', 2: '家属', 3: '租户' }

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    list: [],
    loading: false
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
  },

  onShow() {
    this.loadList()
  },

  onBackTap() {
    wx.navigateBack()
  },

  loadList() {
    this.setData({ loading: true })
    familyApi.getFamilyList().then(res => {
      const rows = (res || []).map(item => Object.assign({}, item, {
        roleName: ROLE_MAP[item.role] || '家属',
        phoneMasked: this.maskPhone(item.phone)
      }))
      this.setData({ list: rows, loading: false })
    }).catch(() => {
      this.setData({ loading: false })
    })
  },

  /**
   * 手机号脱敏：138****8888
   */
  maskPhone(phone) {
    if (!phone || phone.length !== 11) return phone || ''
    return phone.substring(0, 3) + '****' + phone.substring(7)
  },

  /**
   * 删除成员（业主本人不可删除）
   */
  onDeleteTap(e) {
    const { id, name } = e.currentTarget.dataset
    wx.showModal({
      title: '提示',
      content: `确定要移除成员「${name}」吗？`,
      success: res => {
        if (!res.confirm) return
        familyApi.removeFamilyMember(id).then(() => {
          wx.showToast({ title: '已移除', icon: 'success' })
          this.loadList()
        })
      }
    })
  },

  onGoAdd() {
    wx.navigateTo({ url: '/pages/family-add/family-add' })
  }
})
