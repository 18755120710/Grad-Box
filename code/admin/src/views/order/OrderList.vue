<template>
  <div class="order-view-wrapper">
    <!-- Header Section -->
    <div class="view-header">
      <div class="title-area">
        <h2 class="view-title">交易流水审计</h2>
        <p class="view-subtitle">监控平台所有项目的成交状态与资金清算记录</p>
      </div>
      <div class="header-performance">
        <div class="perf-item">
          <lucide-layers :size="16" />
          <span>总成交额: ¥{{ totalRevenue }}</span>
        </div>
      </div>
    </div>

    <!-- Filter/Search Bar -->
    <div class="glass-card filter-bar">
      <el-form :inline="true" :model="queryForm" class="oled-filter-form">
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderNo" placeholder="ORD-XXXXX" clearable @input="handleSearch" />
        </el-form-item>
        <el-form-item label="当前状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="待支付" :value="0" />
            <el-option label="待交付" :value="1" />
            <el-option label="已成交" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button link type="primary" @click="loadData">
            <lucide-refresh-cw :size="14" class="mr-1" /> 同步数据
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Main List Area -->
    <div class="glass-card table-container">
      <el-table 
        :data="orders" 
        v-loading="loading" 
        class="oled-table"
        :header-cell-style="{ background: 'transparent' }"
      >
        <el-table-column label="订单标识" width="240">
          <template #default="{ row }">
            <div class="order-id-cell">
              <lucide-hashtag :size="14" class="hash-icon" />
              <span class="order-no">{{ row.orderNo }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="关联项目资产" min-width="200">
          <template #default="{ row }">
            <div class="project-info">
              <span class="project-id">PID: {{ row.projectId }}</span>
              <p class="project-summary">查看详情 <lucide-external-link :size="12" /></p>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="支付金额" width="160">
          <template #default="{ row }">
            <div class="price-cell">
              <span class="currency">¥</span>
              <span class="amount">{{ row.totalAmount }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="生命周期" width="140" align="center">
          <template #default="{ row }">
            <div class="status-pill" :class="getStatusClass(row.status)">
              <span class="pill-dot"></span>
              {{ getStatusText(row.status) }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="200">
          <template #default="{ row }">
            <div class="time-cell">
              <lucide-calendar :size="14" />
              <span>{{ formatDate(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="工作流操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <div class="op-actions">
              <el-tooltip v-if="row.status === 0" content="确认收款" placement="top">
                <el-button circle class="action-btn pay" @click="handleStatusUpdate(row, 1)">
                  <lucide-credit-card :size="16" />
                </el-button>
              </el-tooltip>
              <el-tooltip v-if="row.status === 1" content="确认交付" placement="top">
                <el-button circle class="action-btn deliver" @click="handleStatusUpdate(row, 2)">
                  <lucide-package :size="16" />
                </el-button>
              </el-tooltip>
              <el-tooltip content="异常处理" placement="top">
                <el-button circle class="action-btn danger" @click="handleDelete(row)">
                  <lucide-trash-2 :size="16" />
                </el-button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-area">
        <el-pagination
          v-model:current-page="page.pageNum"
          v-model:page-size="page.pageSize"
          :total="total"
          layout="prev, pager, next, total"
          @current-change="loadData"
          class="oled-pagination"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Hash as LucideHashtag,
  Calendar as LucideCalendar,
  CreditCard as LucideCreditCard,
  Package as LucidePackage,
  Trash2 as LucideTrash2,
  RefreshCw as LucideRefreshCw,
  Layers as LucideLayers,
  ExternalLink as LucideExternalLink
} from 'lucide-vue-next'

// --- State ---
const orders = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const totalRevenue = ref('0.00')

const page = reactive({
  pageNum: 1,
  pageSize: 10
})

const queryForm = reactive({
  orderNo: '',
  status: null
})

// --- Actions ---
const getStatusText = (s: number) => {
  const map: any = { 0: '待支付', 1: '处理中', 2: '已成交', 3: '订单取消' }
  return map[s] || '未知态'
}

const getStatusClass = (s: number) => {
  const map: any = { 0: 'st-pay', 1: 'st-proc', 2: 'st-done', 3: 'st-cancel' }
  return map[s] || ''
}

const formatDate = (val: string) => {
  if (!val) return '-'
  return new Date(val).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
  })
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/orders', { 
      params: { ...page, ...queryForm } 
    })
    orders.value = res.data.records
    total.value = res.data.total
    
    // Simple revenue calc for summary (this should Ideally come from BE)
    totalRevenue.value = orders.value
      .reduce((sum, o) => sum + (o.status === 2 ? o.totalAmount : 0), 0)
      .toFixed(2)
  } catch (e) {
    ElMessage.error('订单同步链条中断')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.pageNum = 1
  loadData()
}

const handleStatusUpdate = async (row: any, status: number) => {
  const action = status === 1 ? '标记已收款' : '标记已交付'
  ElMessageBox.confirm(`确认执行 "${action}" 操作? 此操作将同步至用户中心并在区块链节点留存记录。`, '流水确认', {
    confirmButtonText: '立即确认',
    cancelButtonText: '取消',
    customClass: 'oled-message-box'
  }).then(async () => {
    try {
      await request.put(`/api/orders/${row.id}/status`, null, { params: { status } })
      ElMessage.success(`订单状态已切换为${getStatusText(status)}`)
      loadData()
    } catch (e) {
      ElMessage.error('状态溯源失败')
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('异常订单仅建议在特殊审计情况下移除，继续?', '异常订单警示', {
    confirmButtonText: '强制删除',
    cancelButtonText: '保留',
    type: 'error',
    customClass: 'oled-message-box'
  }).then(() => {
    ElMessage.error('该订单处于受保护状态，请联系系统架构师')
  })
}

onMounted(loadData)
</script>

<style scoped>
.order-view-wrapper {
  animation: fade-in 0.5s ease-out;
}

/* --- Header --- */
.view-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

.view-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--admin-text-main);
}

.view-subtitle {
  color: var(--admin-text-muted);
  font-size: 14px;
}

.header-performance {
  background: var(--admin-primary-glow);
  padding: 8px 16px;
  border-radius: 12px;
  border: 1px solid var(--admin-primary);
}

.perf-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--admin-primary);
  font-weight: 700;
  font-size: 13px;
  text-transform: uppercase;
}

/* --- Filter --- */
.filter-bar { padding: 20px 32px; margin-bottom: 24px; }

/* --- Table --- */
.table-container { padding: 0; overflow: hidden; }

.order-id-cell { display: flex; align-items: center; gap: 8px; color: var(--admin-text-secondary); }
.hash-icon { color: var(--admin-primary); opacity: 0.6; }
.order-no { font-family: var(--font-data); font-weight: 600; font-size: 13px; }

.project-info { display: flex; flex-direction: column; gap: 4px; }
.project-id { font-size: 11px; color: var(--admin-text-muted); text-transform: uppercase; }
.project-summary { font-size: 13px; color: var(--admin-primary); cursor: pointer; display: flex; align-items: center; gap: 4px; }

.price-cell { display: flex; align-items: baseline; gap: 2px; color: #facc15; font-family: var(--font-data); }
.currency { font-size: 12px; font-weight: 600; }
.amount { font-size: 20px; font-weight: 800; }

/* Status Pills */
.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}
.pill-dot { width: 6px; height: 6px; border-radius: 50%; }

.st-pay { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
.st-pay .pill-dot { background: #f59e0b; animation: pulse 2s infinite; }
.st-proc { background: rgba(59, 130, 246, 0.1); color: #3b82f6; }
.st-proc .pill-dot { background: #3b82f6; }
.st-done { background: rgba(34, 197, 94, 0.1); color: #22c55e; }
.st-done .pill-dot { background: #22c55e; }
.st-cancel { background: rgba(148, 163, 184, 0.1); color: #94a3b8; }
.st-cancel .pill-dot { background: #94a3b8; }

.time-cell { display: flex; align-items: center; gap: 6px; color: var(--admin-text-muted); font-size: 12px; font-family: var(--font-data); }

/* Op Actions */
.op-actions { display: flex; gap: 8px; justify-content: center; }
.action-btn {
  background: var(--admin-surface-light) !important;
  border: 1px solid var(--admin-border) !important;
  color: var(--admin-text-muted) !important;
  transition: all 0.3s;
}
.action-btn:hover { transform: scale(1.1); }
.action-btn.pay:hover { background: rgba(245, 158, 11, 0.1) !important; color: #f59e0b !important; border-color: #f59e0b !important; }
.action-btn.deliver:hover { background: rgba(34, 197, 94, 0.1) !important; color: #22c55e !important; border-color: #22c55e !important; }
.action-btn.danger:hover { background: rgba(239, 68, 68, 0.1) !important; color: #ef4444 !important; border-color: #ef4444 !important; }

.pagination-area {
  padding: 24px;
  border-top: 1px solid var(--admin-border);
  display: flex;
  justify-content: flex-end;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.3); opacity: 0.6; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
