// components/empty/empty.js —— 通用空状态组件
Component({
  properties: {
    // 提示文案
    text: { type: String, value: '暂无数据' },
    // 操作按钮文案，为空则不显示按钮
    btnText: { type: String, value: '' }
  },

  methods: {
    onBtnTap() {
      this.triggerEvent('btntap')
    }
  }
})
