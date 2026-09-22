<template>
  <el-dialog
    title="批量删除居民"
    :visible="visible"
    width="680px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
  >
    <div class="batch-tip">
      请勾选需要删除的居民。存在认证记录的居民删除时会同步删除其与房屋的认证关系。
    </div>
    <el-table
      ref="table"
      :data="rows"
      row-key="residentId"
      max-height="360"
      @selection-change="onSelectionChange"
    >
      <el-table-column type="selection" width="45" reserve-selection />
      <el-table-column prop="residentId" label="ID" width="70" />
      <el-table-column label="姓名" min-width="100">
        <template slot-scope="{ row }">{{ row.name || '-' }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column label="房屋名称" min-width="150">
        <template slot-scope="{ row }">
          <el-popover v-if="row.houseName" placement="top" trigger="hover" :content="row.houseName">
            <span slot="reference" class="house-name">{{ firstHouse(row.houseName) }}</span>
          </el-popover>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="认证状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="row.authCount > 0 ? 'warning' : 'success'">
            {{ row.authCount > 0 ? '已认证' : '未认证' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <div slot="footer">
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onConfirm">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'BatchDeleteDialog',
  props: {
    visible: { type: Boolean, default: false },
    // 每个居民的认证统计：residentId / name / phone / houseName / authCount
    rows: { type: Array, default: () => [] },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      selected: []
    }
  },
  watch: {
    // 弹窗打开时默认全选
    visible(val) {
      if (val) {
        this.selected = [...this.rows]
        this.$nextTick(() => {
          this.$refs.table && this.$refs.table.toggleAllSelection()
        })
      }
    }
  },
  methods: {
    onSelectionChange(selection) {
      this.selected = selection
    },

    // 多个认证房屋只显示第一个
    firstHouse(houseName) {
      return houseName.split(',')[0].trim() + (houseName.split(',').length > 1 ? '...' : '')
    },

    onConfirm() {
      this.$emit('confirm', [...this.selected])
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/resident/batch-delete-dialog.less';
</style>
