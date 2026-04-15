<template>
  <div class="dashboard-wrapper">
    <!-- Top Stats Row -->
    <el-row :gutter="24" class="stats-row">
      <el-col :xs="24" :sm="12" :md="8" v-for="item in statsConfig" :key="item.title">
        <div class="glass-card stat-card">
          <div class="stat-icon" :style="{ color: item.color, background: item.glow }">
            <component :is="item.icon" :size="24" />
          </div>
          <div class="stat-info">
            <p class="stat-label">{{ item.title }}</p>
            <h3 class="stat-value counter-text">{{ item.value }}</h3>
            <div class="stat-trend" :class="item.trend > 0 ? 'up' : 'down'">
              <lucide-trending-up v-if="item.trend > 0" :size="14" />
              <lucide-trending-down v-else :size="14" />
              <span>{{ Math.abs(item.trend) }}% {{ item.trend > 0 ? '增长' : '下降' }}</span>
            </div>
          </div>
          <div class="card-glow" :style="{ background: item.glow }"></div>
        </div>
      </el-col>
    </el-row>

    <!-- Main Charts Row -->
    <el-row :gutter="24" class="charts-row">
      <el-col :xs="24" :lg="12">
        <div class="glass-card main-chart-card">
          <div class="card-header">
            <h4 class="card-title">业务流量态势 (实时)</h4>
            <div class="header-actions">
              <el-radio-group v-model="timeRange" size="small" class="oled-radio">
                <el-radio-button label="24h">24h</el-radio-button>
                <el-radio-button label="7d">7d</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="chart-container">
            <v-chart class="chart" :option="trafficOption" :theme="isDark ? 'dark' : 'light'" autoresize />
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="glass-card sub-chart-card">
          <div class="card-header">
            <h4 class="card-title">资源分布比例</h4>
          </div>
          <div class="chart-container">
            <v-chart class="chart" :option="distributionOption" :theme="isDark ? 'dark' : 'light'" autoresize />
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <div class="glass-card sub-chart-card">
          <div class="card-header">
            <h4 class="card-title">咨询状态分布</h4>
          </div>
          <div class="chart-container">
            <v-chart class="chart" :option="consultationOption" :theme="isDark ? 'dark' : 'light'" autoresize />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Bottom Lists Row -->
    <el-row :gutter="24" class="bottom-row">
      <el-col :span="24">
        <div class="glass-card list-card">
          <div class="card-header">
            <h4 class="card-title">系统操作审计</h4>
            <div class="status-indicator">
              <span class="pulse-dot"></span>
              Secure
            </div>
          </div>
          <div class="activity-feed">
            <div v-for="log in activityLogs" :key="log.id" class="activity-item">
              <div class="activity-time">{{ log.time }}</div>
              <div class="activity-dot"></div>
              <div class="activity-content">
                <span class="activity-user">{{ log.user }}</span>
                <span class="activity-action">{{ log.action }}</span>
                <span class="activity-target">{{ log.target }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { 
  Users as LucideUsers, 
  Files as LucideFiles, 
  MessageSquare as LucideConsult,
  TrendingUp as LucideTrendingUp,
  TrendingDown as LucideTrendingDown
} from 'lucide-vue-next'

// --- ECharts Setup ---
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
])

// --- Theme Monitor ---
const isDark = ref(document.documentElement.classList.contains('dark'))
const observer = new MutationObserver(() => {
  isDark.value = document.documentElement.classList.contains('dark')
})

onMounted(() => {
  observer.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })
  loadData()
})

// --- State ---
const loading = ref(false)
const timeRange = ref('7d')
const dashboardStats = ref({
  userCount: 0,
  projectCount: 0,
  consultationCount: 0,
  totalRevenue: 0,
  userTrend: 0,
  projectTrend: 0,
  revenueTrend: 0,
  categoryStats: [] as any[],
  consultationPie: [] as any[],
  trafficData: { dates: [], values: [] } as any,
  recentActivities: [] as any[]
})

const statsConfig = computed(() => [
  { 
    title: '用户总数', 
    value: dashboardStats.value.userCount, 
    trend: dashboardStats.value.userTrend, 
    icon: LucideUsers, 
    color: 'var(--admin-info)', 
    glow: 'rgba(59, 130, 246, 0.1)' 
  },
  { 
    title: '物料库存', 
    value: dashboardStats.value.projectCount, 
    trend: dashboardStats.value.projectTrend, 
    icon: LucideFiles, 
    color: 'var(--admin-success)', 
    glow: 'rgba(34, 197, 94, 0.1)' 
  },
  { 
    title: '订单总额', 
    value: '￥' + (dashboardStats.value.totalRevenue || 0).toLocaleString(), 
    trend: dashboardStats.value.revenueTrend, 
    icon: LucideConsult, 
    color: 'var(--admin-error)', 
    glow: 'rgba(236, 72, 153, 0.1)' 
  }
])

const activityLogs = computed(() => dashboardStats.value.recentActivities)

// --- Chart Options ---
const trafficOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: { 
    trigger: 'axis',
    backgroundColor: isDark.value ? '#0f172a' : '#fff',
    borderColor: 'rgba(34, 197, 94, 0.2)',
    textStyle: { color: isDark.value ? '#fff' : '#334155' }
  },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: dashboardStats.value.trafficData.dates.length > 0 ? dashboardStats.value.trafficData.dates : ['-', '-', '-', '-', '-', '-', '-'],
    axisLine: { lineStyle: { color: isDark.value ? 'rgba(255,255,255,0.1)' : '#e2e8f0' } },
    axisLabel: { color: isDark.value ? '#94a3b8' : '#64748b' }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: isDark.value ? 'rgba(255,255,255,0.05)' : '#f1f5f9' } },
    axisLabel: { color: isDark.value ? '#94a3b8' : '#64748b' }
  },
  series: [
    {
      name: '活跃度',
      type: 'line',
      smooth: true,
      data: dashboardStats.value.trafficData.values.length > 0 ? dashboardStats.value.trafficData.values : [0, 0, 0, 0, 0, 0, 0],
      itemStyle: { color: 'oklch(65% 0.15 150)' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(34, 197, 94, 0.3)' },
            { offset: 1, color: 'transparent' }
          ]
        }
      }
    }
  ]
}))

const consultationOption = computed(() => {
  const chartColors = [
    'oklch(65% 0.15 150)', // Success-ish
    'oklch(60% 0.18 250)', // Info-ish
    'oklch(65% 0.2 330)'   // Error-ish (for hidden? Or just variety)
  ]
  
  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item' },
    legend: { 
      bottom: '2%', 
      icon: 'circle', 
      textStyle: { color: isDark.value ? '#94a3b8' : '#64748b', fontSize: 10 } 
    },
    series: [
      {
        name: '咨询状态',
        type: 'pie',
        radius: ['35%', '60%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: { 
          borderRadius: 8, 
          borderColor: isDark.value ? '#020617' : '#fff', 
          borderWidth: 2 
        },
        label: { show: false },
        data: dashboardStats.value.consultationPie.length > 0 
          ? dashboardStats.value.consultationPie.map((item, index) => ({
              ...item,
              itemStyle: { color: chartColors[index % chartColors.length] }
            }))
          : [{ value: 0, name: '暂无数据' }]
      }
    ]
  }
})

const distributionOption = computed(() => {
  const chartColors = [
    'oklch(65% 0.15 150)', // Success
    'oklch(60% 0.18 250)', // Info
    'oklch(65% 0.2 330)',  // Error
    'oklch(75% 0.14 80)',  // Warning
    'oklch(55% 0.18 200)'  // Indigo
  ]
  
  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item' },
    legend: { 
      bottom: '2%', 
      icon: 'circle', 
      textStyle: { color: isDark.value ? '#94a3b8' : '#64748b', fontSize: 10 } 
    },
    series: [
      {
        name: '资源分布',
        type: 'pie',
        radius: ['35%', '60%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: { 
          borderRadius: 8, 
          borderColor: isDark.value ? '#020617' : '#fff', 
          borderWidth: 2 
        },
        label: { show: false },
        data: dashboardStats.value.categoryStats.length > 0 
          ? dashboardStats.value.categoryStats.map((item, index) => ({
              ...item,
              itemStyle: { color: chartColors[index % chartColors.length] }
            }))
          : [{ value: 0, name: '暂无数据' }]
      }
    ]
  }
})

// --- Actions ---
const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/admin/stats/overview')
    if (res.code === 200 && res.data) {
      dashboardStats.value = res.data
    }
  } catch (e) {
    console.error('Failed to load dashboard stats:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dashboard-wrapper {
  animation: fade-in 0.6s ease-out;
}

/* --- Stats Row --- */
.stats-row { margin-bottom: 24px; }

.stat-card {
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  overflow: hidden;
  height: 120px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
}

.stat-info { position: relative; z-index: 2; }
.stat-label { color: var(--admin-text-muted); font-size: 13px; margin-bottom: 4px; font-weight: 500; }
.stat-value { font-size: 28px; font-weight: 800; color: var(--admin-text-main); font-family: var(--font-data); }

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  margin-top: 4px;
}
.stat-trend.up { color: var(--admin-success); }
.stat-trend.down { color: var(--admin-error); }

.card-glow {
  position: absolute;
  top: -30%;
  right: -10%;
  width: 150px;
  height: 150px;
  filter: blur(60px);
  opacity: 0.3;
  z-index: 1;
}

/* --- Charts Row --- */
.charts-row { margin-bottom: 24px; }
.chart-container { height: 350px; width: 100%; }

.main-chart-card, .sub-chart-card {
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--admin-text-main);
  letter-spacing: -0.5px;
}

/* --- Bottom Lists --- */
.list-card { padding: 24px; height: 100%; }

.activity-feed {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  position: relative;
}

.activity-time {
  font-size: 12px;
  color: var(--admin-text-muted);
  width: 45px;
  font-family: var(--font-data);
}

.activity-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--admin-border);
  margin-top: 6px;
  position: relative;
  z-index: 2;
}

.activity-content {
  font-size: 13px;
  color: var(--admin-text-main);
  line-height: 1.4;
}

.activity-user { font-weight: 600; margin-right: 6px; }
.activity-action { color: var(--admin-text-secondary); margin-right: 6px; }
.activity-target { color: var(--admin-primary); font-weight: 500; }

.activity-item::after {
  content: '';
  position: absolute;
  left: 57px;
  top: 14px;
  width: 1px;
  height: calc(100% + 16px);
  background: var(--admin-border);
  z-index: 1;
}

.activity-item:last-child::after { display: none; }

/* Status Indicator */
.status-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: var(--admin-success);
  background: var(--admin-primary-glow);
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 700;
  text-transform: uppercase;
}

.pulse-dot {
  width: 6px;
  height: 6px;
  background: var(--admin-success);
  border-radius: 50%;
  animation: pulse-small 2s infinite;
}

@keyframes pulse-small {
  0% { transform: scale(1); opacity: 1; }
  100% { transform: scale(2.5); opacity: 0; }
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.mono-text { font-family: var(--font-data); color: var(--admin-text-secondary); }
</style>
