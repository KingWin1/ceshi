<template>
  <div class="home" v-loading="loading">
    <!-- ========== 统计卡片（2行 x 4列） ========== -->
    <div class="stat-grid" v-if="hasMenu(12)">
      <!-- 本月应收总额 -->
      <div class="stat-card">
        <div class="stat-card__icon" style="background: linear-gradient(135deg, #6a5af9, #8f7bff)">
          <i class="el-icon-s-finance"></i>
        </div>
        <div class="stat-card__body">
          <div class="stat-card__label">本月应收总额</div>
          <div class="stat-card__value">¥{{ formatMoney(statistics.monthReceivable) }}</div>
          <div class="stat-card__extra">
            <span>已收: ¥{{ formatMoney(statistics.monthReceived) }}</span>
            <span class="text-warning">缴费率 {{ statistics.paidRate || 0 }}%</span>
          </div>
          <div class="stat-card__extra">
            <span>较上月</span>
            <span :class="growthClass">{{ formatGrowth(statistics.receivableGrowth) }}</span>
          </div>
        </div>
      </div>

      <!-- 待缴账单 -->
      <div class="stat-card">
        <div class="stat-card__icon" style="background: linear-gradient(135deg, #1e9bf0, #45bdff)">
          <i class="el-icon-document"></i>
        </div>
        <div class="stat-card__body">
          <div class="stat-card__label">待缴账单</div>
          <div class="stat-card__value">{{ statistics.unpaidCount || 0 }} <em>笔</em></div>
          <div class="stat-card__extra">
            <span>逾期: <b class="text-danger">{{ statistics.overdueCount || 0 }}</b> 笔</span>
            <span>本月新增: {{ statistics.unpaidMonthCount || 0 }} 笔</span>
          </div>
        </div>
      </div>

      <!-- 待处理报修 -->
      <div class="stat-card">
        <div class="stat-card__icon" style="background: linear-gradient(135deg, #16c79a, #3ee6b8)">
          <i class="el-icon-setting"></i>
        </div>
        <div class="stat-card__body">
          <div class="stat-card__label">待处理报修</div>
          <div class="stat-card__value">{{ statistics.pendingRepairCount || 0 }} <em>单</em></div>
          <div class="stat-card__extra">
            <span>待派单: {{ statistics.repairDispatchCount || 0 }}</span>
            <span>处理中: {{ statistics.repairHandlingCount || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 待处理投诉 -->
      <div class="stat-card">
        <div class="stat-card__icon" style="background: linear-gradient(135deg, #f5576c, #ff8a9d)">
          <i class="el-icon-chat-dot-round"></i>
        </div>
        <div class="stat-card__body">
          <div class="stat-card__label">待处理投诉</div>
          <div class="stat-card__value">{{ statistics.pendingComplaintCount || 0 }} <em>条</em></div>
          <div class="stat-card__extra">
            <span>待回复: {{ statistics.complaintReplyCount || 0 }}</span>
            <span>处理中: {{ statistics.complaintHandlingCount || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 楼栋/房屋 -->
      <div class="stat-card">
        <div class="stat-card__icon" style="background: linear-gradient(135deg, #5b6cf0, #7d8cff)">
          <i class="el-icon-office-building"></i>
        </div>
        <div class="stat-card__body">
          <div class="stat-card__label">楼栋/房屋</div>
          <div class="stat-card__value">{{ statistics.buildingCount || 0 }} / {{ statistics.houseCount || 0 }}</div>
        </div>
      </div>

      <!-- 房屋入住率 -->
      <div class="stat-card">
        <div class="stat-card__icon" style="background: linear-gradient(135deg, #f78ca0, #f5a623)">
          <i class="el-icon-home"></i>
        </div>
        <div class="stat-card__body">
          <div class="stat-card__label">房屋入住率</div>
          <div class="stat-card__value">{{ statistics.occupancyRate || 0 }}%</div>
          <div class="stat-card__extra">
            <span>已入住 {{ statistics.occupiedCount || 0 }} / 空置 {{ statistics.vacantCount || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 注册居民 -->
      <div class="stat-card">
        <div class="stat-card__icon" style="background: linear-gradient(135deg, #f0529f, #f175c0)">
          <i class="el-icon-user"></i>
        </div>
        <div class="stat-card__body">
          <div class="stat-card__label">注册居民</div>
          <div class="stat-card__value">{{ statistics.residentCount || 0 }}</div>
          <div class="stat-card__extra">
            <span>本月新增: {{ statistics.residentMonthCount || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 本月未收金额 -->
      <div class="stat-card">
        <div class="stat-card__icon" style="background: linear-gradient(135deg, #ff9a3e, #ffc93c)">
          <i class="el-icon-data-analysis"></i>
        </div>
        <div class="stat-card__body">
          <div class="stat-card__label">本月未收金额</div>
          <div class="stat-card__value">¥{{ formatMoney(statistics.unpaidAmount) }}</div>
        </div>
      </div>
    </div>

    <!-- ========== 最新报修 / 最新投诉 ========== -->
    <div v-if="hasMenu(13) || hasMenu(14)" class="panel-row">
      <div v-if="hasMenu(13)" class="panel">
        <div class="panel__head">
          <span class="panel__title">最新报修动态</span>
          <span class="panel__more" @click="$router.push('/repair')">查看更多</span>
        </div>
        <el-scrollbar class="panel__scroll">
          <ul class="feed-list">
            <li v-for="item in latestRepairs" :key="item.repairId" class="feed-item">
              <span class="feed-tag" :class="repairTagClass(item.status)">{{ repairStatusText(item.status) }}</span>
              <span class="feed-item__title">{{ item.description }}</span>
              <span class="feed-item__who">{{ item.reporterName }}</span>
              <span class="feed-item__time">{{ formatTime(item.createTime) }}</span>
            </li>
            <li v-if="!latestRepairs.length" class="feed-empty">暂无数据</li>
          </ul>
        </el-scrollbar>
      </div>

      <div v-if="hasMenu(14)" class="panel">
        <div class="panel__head">
          <span class="panel__title">最新投诉动态</span>
          <span class="panel__more" @click="$router.push('/complaint')">查看更多</span>
        </div>
        <el-scrollbar class="panel__scroll">
          <ul class="feed-list">
            <li v-for="item in latestComplaints" :key="item.complaintId" class="feed-item">
              <span class="feed-tag" :class="complaintTagClass(item.status)">{{ complaintStatusText(item.status) }}</span>
              <span class="feed-item__title">{{ item.title }}</span>
              <span class="feed-item__who">{{ item.contactPerson }}</span>
              <span class="feed-item__time">{{ formatTime(item.submitTime) }}</span>
            </li>
            <li v-if="!latestComplaints.length" class="feed-empty">暂无数据</li>
          </ul>
        </el-scrollbar>
      </div>
    </div>

    <!-- ========== 收费统计 / 工单统计图表 ========== -->
    <div v-if="hasMenu(15) || hasMenu(16)" class="panel-row">
      <div v-if="hasMenu(15)" class="panel">
        <div class="panel__head">
          <span class="panel__title">近6个月收费统计</span>
        </div>
        <div ref="feeChart" class="chart-box"></div>
      </div>

      <div v-if="hasMenu(16)" class="panel">
        <div class="panel__head">
          <span class="panel__title">服务工单统计</span>
        </div>
        <div ref="workChart" class="chart-box"></div>
      </div>
    </div>

    <!-- ========== 待缴账单 / 最近缴费 ========== -->
    <div v-if="hasMenu(17) || hasMenu(18)" class="panel-row">
      <div v-if="hasMenu(17)" class="panel">
        <div class="panel__head">
          <span class="panel__title">待缴账单</span>
          <span class="panel__more" @click="$router.push('/bill')">查看更多</span>
        </div>
        <el-scrollbar class="panel__scroll">
          <ul class="feed-list">
            <li v-for="item in unpaidBills" :key="item.billId" class="feed-item">
              <span class="feed-tag tag-warning">待缴</span>
              <span class="feed-item__title">{{ item.buildingName || '未知' }} - {{ item.feeTypeName }}</span>
              <span class="feed-item__amount text-danger">¥{{ formatMoney(item.payableAmount) }}</span>
              <span class="feed-item__time">{{ formatTime(item.createTime) }}</span>
            </li>
            <li v-if="!unpaidBills.length" class="feed-empty">暂无数据</li>
          </ul>
        </el-scrollbar>
      </div>

      <div v-if="hasMenu(18)" class="panel">
        <div class="panel__head">
          <span class="panel__title">最近缴费</span>
          <span class="panel__more" @click="$router.push('/bill')">查看更多</span>
        </div>
        <el-scrollbar class="panel__scroll">
          <ul class="feed-list">
            <li v-for="item in recentPaidBills" :key="item.billId" class="feed-item">
              <span class="feed-tag tag-success">已缴</span>
              <span class="feed-item__title">{{ item.buildingName || '未知' }} - {{ item.feeTypeName }}</span>
              <span class="feed-item__amount text-success">¥{{ formatMoney(item.paidAmount) }}</span>
              <span class="feed-item__time">{{ formatTime(item.payTime) }}</span>
            </li>
            <li v-if="!recentPaidBills.length" class="feed-empty">暂无数据</li>
          </ul>
        </el-scrollbar>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import Api from '@/api'

export default {
  name: 'Home',
  data() {
    return {
      loading: false,
      statistics: {},
      latestRepairs: [],
      latestComplaints: [],
      unpaidBills: [],
      recentPaidBills: [],
      feeChart: null,
      workChart: null
    }
  },
  computed: {
    growthClass() {
      const g = Number(this.statistics.receivableGrowth || 0)
      return g >= 0 ? 'text-success' : 'text-danger'
    }
  },
  created() {
    this.loadData()
  },
  mounted() {
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    if (this.feeChart) this.feeChart.dispose()
    if (this.workChart) this.workChart.dispose()
  },
  methods: {
    // 判断当前角色是否拥有指定首页模块权限
    hasMenu(menuId) {
      const menuIds = this.$store.getters['user/menuIds'] || []
      return menuIds.indexOf(menuId) !== -1
    },

    // 加载首页聚合数据
    async loadData() {
      this.loading = true
      try {
        const res = await Api.getHomeStatistics(6)
        const data = res.data || {}
        this.statistics = data.statistics || {}
        this.latestRepairs = data.latestRepairs || []
        this.latestComplaints = data.latestComplaints || []
        this.unpaidBills = data.unpaidBills || []
        this.recentPaidBills = data.recentPaidBills || []
        this.$nextTick(() => {
          this.renderFeeChart(data.feeChart || {})
          this.renderWorkChart(data.workOrderChart || {})
        })
      } catch (e) {
        // 请求失败提示已由拦截器统一处理
      } finally {
        this.loading = false
      }
    },

    // 近6个月收费统计柱状图
    renderFeeChart(chartData) {
      if (!this.$refs.feeChart) return
      this.feeChart = echarts.init(this.$refs.feeChart)
      this.feeChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { bottom: 0, data: ['应收总额', '实收金额'] },
        grid: { left: 50, right: 20, top: 20, bottom: 40 },
        xAxis: { type: 'category', data: chartData.months || [] },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: val => (val >= 10000 ? val / 10000 + '万' : val)
          }
        },
        series: [
          { name: '应收总额', type: 'bar', barWidth: 18, itemStyle: { color: '#4da0ff', borderRadius: [3, 3, 0, 0] }, data: chartData.receivable || [] },
          { name: '实收金额', type: 'bar', barWidth: 18, itemStyle: { color: '#5fd35f', borderRadius: [3, 3, 0, 0] }, data: chartData.received || [] }
        ]
      })
    },

    // 服务工单堆叠柱状图
    renderWorkChart(chartData) {
      if (!this.$refs.workChart) return
      this.workChart = echarts.init(this.$refs.workChart)
      this.workChart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { bottom: 0, data: ['待处理', '已派单', '处理中'] },
        grid: { left: 40, right: 20, top: 20, bottom: 40 },
        xAxis: { type: 'category', data: chartData.categories || [] },
        yAxis: { type: 'value' },
        series: [
          { name: '待处理', type: 'bar', stack: 'total', barWidth: 60, itemStyle: { color: '#f26d6d' }, data: chartData.pending || [] },
          { name: '已派单', type: 'bar', stack: 'total', itemStyle: { color: '#e6a23c' }, data: chartData.dispatched || [] },
          { name: '处理中', type: 'bar', stack: 'total', itemStyle: { color: '#4da0ff', borderRadius: [3, 3, 0, 0] }, data: chartData.handling || [] }
        ]
      })
    },

    resizeCharts() {
      if (this.feeChart) this.feeChart.resize()
      if (this.workChart) this.workChart.resize()
    },

    formatMoney(val) {
      return Number(val || 0).toFixed(2)
    },

    formatGrowth(val) {
      const g = Number(val || 0)
      return (g > 0 ? '+' : '') + g + '%'
    },

    // 时间格式：M/D HH:mm
    formatTime(val) {
      if (!val) return ''
      const d = new Date(val)
      return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
    },

    repairStatusText(status) {
      return { 1: '待派单', 2: '已派单', 3: '处理中', 4: '已完成', 5: '已取消' }[status] || '未知'
    },

    repairTagClass(status) {
      return { 1: 'tag-danger', 2: 'tag-warning', 3: 'tag-primary', 4: 'tag-success', 5: 'tag-success' }[status] || ''
    },

    complaintStatusText(status) {
      return { 1: '待处理', 2: '处理中', 3: '已回复', 4: '已关闭' }[status] || '未知'
    },

    complaintTagClass(status) {
      return { 1: 'tag-danger', 2: 'tag-primary', 3: 'tag-success', 4: 'tag-success' }[status] || ''
    }
  }
}
</script>

<style lang="less" scoped>
@import '@/assets/styles/home/HomeView.less';
</style>
