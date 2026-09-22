<template>
  <div class="resident-list">
    <!-- 查询区 -->
    <div class="search-card">
      <div class="search-row">
        <!-- 居民类型页签 -->
        <div class="type-tabs">
          <span
            v-for="tab in typeTabs"
            :key="tab.value"
            :class="['type-tab', { active: query.type === tab.value }]"
            @click="onTabChange(tab.value)"
          >{{ tab.label }}</span>
        </div>
        <span class="search-label">关键词</span>
        <el-input
          v-model="query.keyword"
          class="w-200"
          placeholder="姓名/手机号/身份证"
          clearable
          @keyup.enter.native="onSearch"
        />
        <span class="search-label">性别</span>
        <el-select v-model="query.gender" class="w-110" placeholder="全部" clearable>
          <el-option v-for="g in genderOptions" :key="g.value" :label="g.label" :value="g.value" />
        </el-select>
        <span class="search-label">楼栋</span>
        <el-select v-model="query.buildingId" class="w-130" placeholder="请选择" clearable @change="onQueryBuildingChange">
          <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
        </el-select>
        <span class="search-label">单元</span>
        <el-select
          v-model="query.unitNo"
          class="w-120"
          placeholder="请选择"
          clearable
          :disabled="!query.buildingId"
          @change="onQueryUnitChange"
        >
          <el-option v-for="u in queryUnitOptions" :key="u.value" :label="u.label" :value="u.value" />
        </el-select>
        <span class="search-label">房间号</span>
        <el-select
          v-model="query.houseNumber"
          class="w-120"
          placeholder="请选择"
          clearable
          :disabled="!query.unitNo"
        >
          <el-option v-for="h in queryHouseOptions" :key="h" :label="h" :value="h" />
        </el-select>
      </div>
      <div class="search-row">
        <span class="search-label">注册方式</span>
        <el-select v-model="query.registerWay" class="w-120" placeholder="全部" clearable>
          <el-option v-for="r in registerWayOptions" :key="r.value" :label="r.label" :value="r.value" />
        </el-select>
        <span class="search-label">状态</span>
        <el-select v-model="query.status" class="w-120" placeholder="全部" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
        <el-button icon="el-icon-refresh-left" @click="onReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="table-card">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="onAdd">新增居民</el-button>
        <el-button
          class="btn-danger"
          type="danger"
          icon="el-icon-delete"
          :disabled="selectedIds.length === 0"
          @click="onBatchDelete"
        >批量删除</el-button>
        <el-button icon="el-icon-refresh" @click="fetchList">刷新</el-button>
      </div>

      <el-table
        ref="table"
        v-loading="loading"
        :data="list"
        row-key="residentId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="45" reserve-selection />
        <el-table-column label="姓名" min-width="150">
          <template slot-scope="{ row }">
            <div class="cell-name">
              <el-image class="avatar" :src="resolveUrl(row.avatarUrl)" fit="cover">
                <div slot="error" class="avatar-placeholder"><i class="el-icon-user" /></div>
              </el-image>
              <span>{{ row.name || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" min-width="120" />
        <el-table-column prop="idCard" label="身份证" min-width="170">
          <template slot-scope="{ row }">{{ row.idCard || '' }}</template>
        </el-table-column>
        <el-table-column label="性别" min-width="70">
          <template slot-scope="{ row }">{{ genderMap[row.gender] != null ? genderMap[row.gender] : '未知' }}</template>
        </el-table-column>
        <el-table-column label="房屋名称" min-width="180">
          <template slot-scope="{ row }">
            <!-- 多个认证房屋只显示第一个，悬浮显示全部 -->
            <el-popover v-if="row.houseName" placement="top" trigger="hover" :content="row.houseName">
              <span slot="reference" class="house-name">{{ firstHouse(row.houseName) }}</span>
            </el-popover>
            <span v-else></span>
          </template>
        </el-table-column>
        <el-table-column prop="houseCount" label="房屋数" min-width="80" />
        <el-table-column label="类型" min-width="80">
          <template slot-scope="{ row }">{{ typeMap[row.type] || '-' }}</template>
        </el-table-column>
        <el-table-column label="注册方式" min-width="90">
          <template slot-scope="{ row }">{{ registerWayMap[row.registerWay] || '-' }}</template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="150">
          <template slot-scope="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
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

    <!-- 新增 / 编辑弹窗 -->
    <add-or-update
      :visible.sync="dialogVisible"
      :is-edit="isEdit"
      :record="editRecord"
      :buildings="buildings"
      :submitting="submitting"
      @submit="onSubmit"
    />

    <!-- 批量删除确认弹窗 -->
    <batch-delete-dialog
      :visible.sync="batchVisible"
      :rows="batchRows"
      :submitting="submitting"
      @confirm="onBatchConfirm"
    />
  </div>
</template>

<script>
import Api from '@/api'
import AddOrUpdate from './add-or-update.vue'
import BatchDeleteDialog from './batch-delete-dialog.vue'

// 查询条件初始值
const emptyQuery = () => ({
  type: null,
  keyword: '',
  gender: null,
  buildingId: null,
  unitNo: '',
  houseNumber: '',
  registerWay: null,
  status: null,
  pageNum: 1,
  pageSize: 10
})

export default {
  name: 'ResidentIndex',
  components: {
    AddOrUpdate,
    BatchDeleteDialog
  },
  data() {
    return {
      loading: false,
      submitting: false,
      list: [],
      total: 0,
      query: emptyQuery(),
      selectedIds: [],
      // 下拉数据源
      buildings: [],
      queryUnitOptions: [],
      queryHouseOptions: [],
      // 枚举
      typeTabs: [
        { value: null, label: '全部居民' },
        { value: 1, label: '业主' },
        { value: 2, label: '家属' },
        { value: 3, label: '租户' }
      ],
      genderOptions: [
        { value: 0, label: '男' },
        { value: 1, label: '女' },
        { value: 2, label: '未知' }
      ],
      registerWayOptions: [
        { value: 1, label: 'App注册' },
        { value: 2, label: '后台注册' }
      ],
      statusOptions: [
        { value: 1, label: '正常' },
        { value: 2, label: '停用' }
      ],
      genderMap: { 0: '男', 1: '女', 2: '未知' },
      typeMap: { 1: '业主', 2: '家属', 3: '租户' },
      registerWayMap: { 1: 'App注册', 2: '后台注册' },
      // 新增/编辑弹窗
      dialogVisible: false,
      isEdit: false,
      editRecord: null,
      // 批量删除弹窗
      batchVisible: false,
      batchRows: []
    }
  },
  created() {
    this.fetchBuildings()
    this.fetchList()
  },
  methods: {
    // 查询全部楼栋（楼栋下拉框数据源）
    async fetchBuildings() {
      try {
        const res = await Api.getBuildingList()
        this.buildings = res.data || []
      } catch (e) {
        // 拦截器已提示
      }
    },

    // 根据楼栋ID生成单元下拉选项：值为 1、2...，显示为 1单元、2单元...
    async loadUnitOptions(buildingId) {
      if (!buildingId) {
        return []
      }
      const res = await Api.getBuildingById(buildingId)
      const count = (res.data && res.data.unitCount) || 0
      return Array.from({ length: count }, (_, i) => ({ value: String(i + 1), label: `${i + 1}单元` }))
    },

    // 查询区楼栋-单元二级联动
    async onQueryBuildingChange(buildingId) {
      this.query.unitNo = ''
      this.query.houseNumber = ''
      this.queryHouseOptions = []
      try {
        this.queryUnitOptions = await this.loadUnitOptions(buildingId)
      } catch (e) {
        this.queryUnitOptions = []
      }
    },

    // 查询区单元-房间号三级联动
    async onQueryUnitChange(unitNo) {
      this.query.houseNumber = ''
      if (!unitNo) {
        this.queryHouseOptions = []
        return
      }
      try {
        const res = await Api.getHouseByBuildingAndUnit(this.query.buildingId, unitNo)
        const houses = res.data || []
        // 房间号去重
        this.queryHouseOptions = [...new Set(houses.map(h => h.houseNumber))]
      } catch (e) {
        this.queryHouseOptions = []
      }
    },

    // 类型页签切换
    onTabChange(type) {
      this.query.type = type
      this.onSearch()
    },

    // 分页+条件查询居民列表
    async fetchList() {
      this.loading = true
      try {
        const res = await Api.getResidentPage(this.query)
        const page = res.data || {}
        this.list = page.records || []
        this.total = page.total || 0
      } catch (e) {
        this.$message.error('居民列表查询失败')
      } finally {
        this.loading = false
      }
    },

    onSearch() {
      this.query.pageNum = 1
      this.fetchList()
    },

    onReset() {
      this.query = emptyQuery()
      this.queryUnitOptions = []
      this.queryHouseOptions = []
      this.fetchList()
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

    onSelectionChange(rows) {
      this.selectedIds = rows.map(row => row.residentId)
    },

    clearSelection() {
      this.$refs.table.clearSelection()
      this.selectedIds = []
    },

    // 多个认证房屋只显示第一个
    firstHouse(houseName) {
      return houseName.split(',')[0].trim() + (houseName.split(',').length > 1 ? '...' : '')
    },

    // 头像地址转可访问 URL
    resolveUrl(url) {
      if (!url) {
        return ''
      }
      return url.startsWith('http') ? url : window.location.origin + url
    },

    // 时间格式化：2026-07-23 15:19
    formatTime(time) {
      if (!time) {
        return ''
      }
      const d = new Date(time)
      if (isNaN(d.getTime())) {
        return time
      }
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
    },

    // 打开新增弹窗
    onAdd() {
      this.isEdit = false
      this.editRecord = null
      this.dialogVisible = true
    },

    // 编辑：查询居民信息和该居民的房屋信息集合回显
    async onEdit(row) {
      try {
        const res = await Api.getResidentForUpdate(row.residentId)
        if (!res.data || !res.data.resident) {
          this.$message.error('居民信息不存在')
          return
        }
        this.editRecord = res.data
        this.isEdit = true
        this.dialogVisible = true
      } catch (e) {
        this.$message.error('居民信息查询失败')
      }
    },

    // 弹窗提交：新增 / 修改
    async onSubmit(form) {
      this.submitting = true
      try {
        if (this.isEdit) {
          await Api.updateResident(form)
          this.$message.success('修改成功')
        } else {
          await Api.addResident(form)
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

    // 删除单个：确认 -> 统计认证 -> 存在认证则二次确认 -> 后端删除居民并同步删除认证记录
    async onDelete(row) {
      try {
        await this.$confirm(`确定删除居民「${row.name || row.phone || ''}」吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        return
      }
      try {
        const res = await Api.countAuthByResidentId(row.residentId)
        const count = res.data || 0
        if (count > 0) {
          await this.$confirm(
            `该用户存在认证房屋记录（${count} 条），继续删除将同时删除其认证记录，是否继续？`,
            '提示',
            { confirmButtonText: '继续', cancelButtonText: '取消', type: 'warning' }
          )
        }
        await Api.deleteResident(row.residentId)
        this.$message.success('删除成功')
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 取消或拦截器已提示
      }
    },

    // 批量删除：先统计每个居民的认证情况，弹窗展示供用户勾选
    async onBatchDelete() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要删除的居民')
        return
      }
      try {
        const res = await Api.countAuthByResidentIds(this.selectedIds)
        this.batchRows = res.data || []
        this.batchVisible = true
      } catch (e) {
        this.$message.error('居民认证信息查询失败')
      }
    },

    // 批量删除弹窗确认：勾选的居民中存在认证记录时二次确认
    onBatchConfirm(rows) {
      if (rows.length === 0) {
        this.$message.warning('请至少勾选一条要删除的居民')
        return
      }
      const ids = rows.map(r => r.residentId)
      const hasAuth = rows.some(r => r.authCount > 0)
      if (hasAuth) {
        this.$confirm('被删除信息中存在认证记录，继续将同时删除其认证记录，是否继续？', '提示', {
          confirmButtonText: '继续',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => this.doBatchDelete(ids))
          .catch(() => {})
      } else {
        this.doBatchDelete(ids)
      }
    },

    // 后端批量删除时会同步删除认证记录
    async doBatchDelete(ids) {
      this.submitting = true
      try {
        await Api.deleteResidentBatch(ids)
        this.$message.success('批量删除成功')
        this.batchVisible = false
        this.clearSelection()
        this.fetchList()
      } catch (e) {
        // 拦截器已提示
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/resident/index.less';
</style>
