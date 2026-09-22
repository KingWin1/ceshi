<template>
  <el-dialog
    :title="isEdit ? '编辑角色' : '新增角色'"
    :visible="visible"
    width="640px"
    :close-on-click-modal="false"
    @update:visible="v => $emit('update:visible', v)"
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="form.roleName" placeholder="请输入角色名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="权限分配">
        <div class="perm-tree-box">
          <el-tree
            ref="tree"
            :data="menuTree"
            :props="treeProps"
            node-key="menuId"
            show-checkbox
            default-expand-all
            :default-checked-keys="checkedLeafIds"
          />
        </div>
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
// 表单初始值
const emptyForm = () => ({
  roleId: null,
  roleName: ''
})

export default {
  name: 'AddOrUpdate',
  props: {
    visible: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    // 编辑时的角色数据（含 menuIds），新增传 null
    record: { type: Object, default: null },
    // 权限树数据源（父组件统一加载）
    menuTree: { type: Array, default: () => [] },
    submitting: { type: Boolean, default: false }
  },
  data() {
    return {
      form: emptyForm(),
      // 回显勾选的叶子节点ID（父节点由 el-tree 自动推导，避免全选联动问题）
      checkedLeafIds: [],
      treeProps: {
        label: 'menuName',
        children: 'children'
      },
      rules: {
        roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
      }
    }
  },
  watch: {
    // 弹窗打开时回显数据
    visible(val) {
      if (val) {
        this.form = this.record ? { roleId: this.record.roleId, roleName: this.record.roleName } : emptyForm()
        this.checkedLeafIds = this.record ? this.filterLeafIds(this.record.menuIds || []) : []
      }
    }
  },
  methods: {
    // 收集树中所有叶子节点ID
    collectLeafIds(nodes, result) {
      nodes.forEach(node => {
        if (node.children && node.children.length) {
          this.collectLeafIds(node.children, result)
        } else {
          result.push(node.menuId)
        }
      })
    },

    // 只保留已分配权限中的叶子节点，用于回显勾选
    filterLeafIds(menuIds) {
      const leafIds = []
      this.collectLeafIds(this.menuTree, leafIds)
      const result = []
      menuIds.forEach(id => {
        if (leafIds.indexOf(id) !== -1) {
          result.push(id)
        }
      })
      return result
    },

    // 校验通过后收集勾选的权限ID（含半选父节点）交给父组件
    onSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const checked = this.$refs.tree.getCheckedKeys()
        const halfChecked = this.$refs.tree.getHalfCheckedKeys()
        const menuIds = checked.concat(halfChecked)
        const data = { ...this.form, menuIds }
        this.$emit('submit', data)
      })
    },

    resetForm() {
      this.$refs.form && this.$refs.form.clearValidate()
      this.form = emptyForm()
      this.checkedLeafIds = []
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/system/role/add-or-update.less';
</style>
