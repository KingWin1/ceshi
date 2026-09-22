// pages/repair-add/repair-add.js —— 提交报修
const icons = require('../../utils/icons')
const repairApi = require('../../api/repair')

const TYPE_LIST = ['水电维修', '门窗维修', '墙面地面', '电梯故障', '公共设施', '其他']

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    types: TYPE_LIST,
    repairType: '',
    description: '',
    images: [],
    contactPhone: '',
    submitting: false
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    const resident = wx.getStorageSync('resident') || {}
    this.setData({
      statusBarHeight: info.statusBarHeight || 20,
      // 默认带出登录居民手机号
      contactPhone: resident.phone || ''
    })
  },

  onBackTap() {
    wx.navigateBack()
  },

  onTypeTap(e) {
    this.setData({ repairType: e.currentTarget.dataset.type })
  },

  onDescInput(e) {
    this.setData({ description: e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ contactPhone: e.detail.value })
  },

  /**
   * 选择图片（本地暂存，提交时仅记录路径占位）
   */
  onChooseImage() {
    const remain = 3 - this.data.images.length
    wx.chooseMedia({
      count: remain,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success: res => {
        const paths = res.tempFiles.map(f => f.tempFilePath)
        this.setData({ images: this.data.images.concat(paths).slice(0, 3) })
      }
    })
  },

  onPreviewImage(e) {
    const index = e.currentTarget.dataset.index
    wx.previewImage({
      current: this.data.images[index],
      urls: this.data.images
    })
  },

  onDeleteImage(e) {
    const index = e.currentTarget.dataset.index
    const images = this.data.images.slice()
    images.splice(index, 1)
    this.setData({ images: images })
  },

  /**
   * 表单校验
   */
  validate() {
    if (!this.data.repairType) {
      wx.showToast({ title: '请选择报修类型', icon: 'none' })
      return false
    }
    if (this.data.description.trim().length < 10) {
      wx.showToast({ title: '问题描述至少10个字', icon: 'none' })
      return false
    }
    if (!/^1[3-9]\d{9}$/.test(this.data.contactPhone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return false
    }
    return true
  },

  onSubmit() {
    if (this.data.submitting || !this.validate()) return
    this.setData({ submitting: true })
    repairApi.submitRepair({
      type: this.data.repairType,
      description: this.data.description.trim(),
      images: this.data.images,
      contactPhone: this.data.contactPhone
    }).then(() => {
      wx.showToast({ title: '提交成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 800)
    }).catch(() => {
      this.setData({ submitting: false })
    })
  }
})
