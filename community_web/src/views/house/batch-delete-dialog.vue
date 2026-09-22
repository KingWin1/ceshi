<template>
  <el-dialog
    title="批量删除房屋"
    :visible="visible"
    width="640px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
  >
    <div class="batch-tip">
      请勾选需要删除的房屋。已认证的房屋删除时会同步解除其与居民的认证关系。
    </div>
    <el-table
      ref="table"
      :data="rows"
      row-key="houseId"
      max-height="360"
      @selection-change="onSelectionChange"
    >
      <el-table-column type="selection" width="45" reserve-selection :selectable="row => true" />
      <el-table-column prop="houseId" label="ID" width="80" />
      <el-table-column prop="buildingName" label="楼栋" min-width="120" />
      <el-table-column label="单元号" width="90">
        <template slot-scope="{ row }">{{ row.unitNo ? row.unitNo + '单元' : '' }}</template>
      </el-table-column>
      <el-table-column prop="houseNumber" label="房号" width="90" />
      <el-table-column label="认证状态" width="110">
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
    // 每个房屋的认证统计：houseId / buildingName / unitNo / houseNumber / authCount
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

    onConfirm() {
      this.$emit('confirm', [...this.selected])
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/house/batch-delete-dialog.less';
</style>
