<template>
  <div class="placeholder">
    <i class="el-icon-s-tools"></i>
    <p>{{ title }} 功能开发中…</p>
  </div>
</template>

<script>
export default {
  name: 'Placeholder',
  computed: {
    title() {
      return this.$route.meta.title || '该页面'
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/common/Placeholder.less';
</style>
