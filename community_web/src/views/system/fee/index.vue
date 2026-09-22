<template>
  <div class="fee-list">
    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增费用类型</el-button>
      </div>

      <el-table v-loading="loading" :data="list" row-key="feeTypeId">
        <el-table-column prop="feeTypeName" label="费用类型名称" min-width="180" />
        <el-table-column prop="code" label="编码" min-width="140" />
        <el-table-column label="计费方式" min-width="160">
          <template slot-scope="{ row }">{{ billingMethodText(row.billingMethod) }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" icon="el-icon-edit" class="op-edit" @click="onEdit(row)">编辑</el-button>
            <el-button type="text" icon="el-icon-s-operation" class="op-price" @click="onPricing(row)">计价设置</el-button>
            <el-button type="text" icon="el-icon-delete" class="op-del" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="query.pageNum"
        :page-size="query.pageSize"
        :page-sizes="[5, 10, 20, 30]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="onSizeChange"
        @current-change="onPageChange"
      />
    </div>

    <!-- 新增 / 修改弹窗（页面私有子组件） -->
    <add-or-update
      :visible.sync="dialogVisible"
      :is-edit="isEdit"
      :record="editRecord"
      :submitting="submitting"
      @submit="onSubmit"
    />

    <!-- 计价设置弹窗（页面私有子组件） -->
    <pricing-dialog
      :visible.sync="pricingVisible"
      :record="pricingRecord"
      :submitting="pricingSubmitting"
      @submit="onPricingSubmit"
    />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'
import PricingDialog from './pricing-dialog.vue'

export default {
  name: 'FeeIndex',
  components: {
    AddOrUpdate,
    PricingDialog
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: {
        feeTypeName: '',
        pageNum: 1,
        pageSize: 10
      },
      // 新增/编辑弹窗状态
      dialogVisible: false,
      submitting: false,
      isEdit: false,
      editRecord: null,
      // 计价设置弹窗状态
      pricingVisible: false,
      pricingSubmitting: false,
      pricingRecord: null
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    // 分页+条件查询费用类型列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getFeeTypePage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        this.$message.error('费用类型列表查询失败')
      } finally {
        this.loading = false
      }
    },

    onSizeChange(size) {
      this.query.pageSize = size
      this.query.pageNum = 1
      this.fetchList()
    },

    onPageChange(page) {
      this.query.pageNum = page
      this.fetchList()
    },

    // 计费方式文案：1-按面积 2-固定金额
    billingMethodText(method) {
      if (method === 1) {
        return '按面积(元/㎡)'
      }
      if (method === 2) {
        return '固定金额(元)'
      }
      return '-'
    },

    // 打开新增弹窗
    onAdd() {
      this.isEdit = false
      this.editRecord = null
      this.dialogVisible = true
    },

    // 编辑：先根据费用类型ID查询费用类型信息回显
    async onEdit(row) {
      try {
        const res = await Api.getFeeTypeById(row.feeTypeId)
        if (!res.data) {
          this.$message.error('费用类型信息不存在')
          return
        }
        this.editRecord = res.data
        this.isEdit = true
        this.dialogVisible = true
      } catch (e) {
        this.$message.error('费用类型信息查询失败')
      }
    },

    // 弹窗提交：新增 / 修改
    async onSubmit(form) {
      this.submitting = true
      try {
        if (this.isEdit) {
          await Api.updateFeeType(form)
          this.$message.success('修改成功')
        } else {
          await Api.addFeeType(form)
          this.$message.success('添加成功')
        }
        this.dialogVisible = false
        this.fetchList()
      } catch (e) {
        // 错误提示已由响应拦截器统一处理
      } finally {
        this.submitting = false
      }
    },

    // 打开计价设置弹窗
    onPricing(row) {
      this.pricingRecord = row
      this.pricingVisible = true
    },

    // 计价设置提交：根据费用类型ID修改单价信息
    async onPricingSubmit(form) {
      this.pricingSubmitting = true
      try {
        await Api.updateFeeTypePrice(form)
        this.$message.success('计价设置保存成功')
        this.pricingVisible = false
        this.fetchList()
      } catch (e) {
        // 请求失败提示由拦截器处理
      } finally {
        this.pricingSubmitting = false
      }
    },

    // 删除费用类型（被账单占用时后端拒绝删除）
    async onDelete(row) {
      try {
        await this.$confirm(`确定删除费用类型"${row.feeTypeName}"吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        await Api.deleteFeeType(row.feeTypeId)
        this.$message.success('删除成功')
        this.fetchList()
      } catch (e) {
        // 请求失败提示由拦截器处理
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/system/fee/index.less';
</style>
