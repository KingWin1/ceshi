// pages/family-add/family-add.js —— 添加家庭成员
const icons = require('../../utils/icons')
const familyApi = require('../../api/family')

Page({
  data: {
    icons: icons,
    statusBarHeight: 20,
    name: '',
    phone: '',
    roles: [
      { label: '请选择', value: '' },
      { label: '家属', value: 2 },
      { label: '租户', value: 3 }
    ],
    roleIndex: 0,
    gender: 1,
    submitting: false
  },

  onLoad() {
    const info = wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
  },

  onBackTap() {
    wx.navigateBack()
  },

  onNameInput(e) {
    this.setData({ name: e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onRoleChange(e) {
    this.setData({ roleIndex: Number(e.detail.value) })
  },

  onGenderTap(e) {
    this.setData({ gender: Number(e.currentTarget.dataset.value) })
  },

  validate() {
    if (!this.data.name.trim()) {
      wx.showToast({ title: '请输入姓名', icon: 'none' })
      return false
    }
    if (!/^1[3-9]\d{9}$/.test(this.data.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return false
    }
    if (!this.data.roles[this.data.roleIndex].value) {
      wx.showToast({ title: '请选择身份关系', icon: 'none' })
      return false
    }
    return true
  },

  onSubmit() {
    if (this.data.submitting || !this.validate()) return
    this.setData({ submitting: true })
    familyApi.addFamilyMember({
      name: this.data.name.trim(),
      phone: this.data.phone,
      role: this.data.roles[this.data.roleIndex].value,
      gender: this.data.gender
    }).then(() => {
      wx.showToast({ title: '添加成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 800)
    }).catch(() => {
      this.setData({ submitting: false })
    })
  }
})
