<template>
  <div class="cs-list">
    <!-- 列表区 -->
    <div class="table-card">
      <div class="card-head">
        <span class="card-title">物业客服信息管理</span>
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">添加客服</el-button>
      </div>

      <el-table v-loading="loading" :data="list" row-key="csId">
        <el-table-column prop="csName" label="客服姓名" min-width="140" />
        <el-table-column prop="position" label="职位" min-width="120" />
        <el-table-column prop="phone" label="联系电话" min-width="150" />
        <el-table-column prop="wechat" label="微信号" min-width="150" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" icon="el-icon-edit" class="op-edit" @click="onEdit(row)">编辑</el-button>
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

    <!-- 添加 / 编辑弹窗（页面私有子组件） -->
    <add-or-update
      :visible.sync="dialogVisible"
      :is-edit="isEdit"
      :record="editRecord"
      :submitting="submitting"
      @submit="onSubmit"
    />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'

export default {
  name: 'CsIndex',
  components: {
    AddOrUpdate
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: {
        pageNum: 1,
        pageSize: 10
      },
      // 弹窗状态
      dialogVisible: false,
      submitting: false,
      isEdit: false,
      // 编辑时回显的客服数据
      editRecord: null
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    // 分页+条件查询客服列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getCustomerServicePage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        this.$message.error('客服列表查询失败')
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

    // 打开添加弹窗
    onAdd() {
      this.isEdit = false
      this.editRecord = null
      this.dialogVisible = true
    },

    // 编辑：先根据客服ID查询客服信息回显
    async onEdit(row) {
      try {
        const res = await Api.getCustomerServiceById(row.csId)
        if (!res.data) {
          this.$message.error('客服信息不存在')
          return
        }
        this.editRecord = res.data
        this.isEdit = true
        this.dialogVisible = true
      } catch (e) {
        this.$message.error('客服信息查询失败')
      }
    },

    // 弹窗提交：添加 / 修改
    async onSubmit(form) {
      this.submitting = true
      try {
        if (this.isEdit) {
          await Api.updateCustomerService(form)
          this.$message.success('修改成功')
        } else {
          await Api.addCustomerService(form)
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

    // 删除客服
    async onDelete(row) {
      try {
        await this.$confirm(`确定删除客服"${row.csName}"吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }

      try {
        await Api.deleteCustomerService(row.csId)
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
@import '@/assets/styles/system/cs/index.less';
</style>
