<template>
  <div class="consultation-view-wrapper">
    <!-- Header Area -->
    <div class="view-header">
      <div class="title-area">
        <h2 class="view-title">服务咨询中心</h2>
        <p class="view-subtitle">响应并处理来自用户的商务与技术咨询工单</p>
      </div>
      <div class="header-stats">
        <div class="mini-stat">
          <span class="label">待处理</span>
          <span class="value warning">{{ pendingCount }}</span>
        </div>
        <div class="mini-stat">
          <span class="label">已办结</span>
          <span class="value success">{{ resolvedCount }}</span>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="glass-card table-container">
      <el-table 
        :data="consultations" 
        v-loading="loading" 
        class="oled-table"
        row-class-name="consultation-row"
      >
        <el-table-column label="工单来源" width="180">
          <template #default="{ row }">
            <div class="user-info-cell">
              <div class="avatar-placeholder">
                <lucide-user :size="16" />
              </div>
              <div class="user-meta">
                <span class="name">{{ row.contactName }}</span>
                <span class="phone">{{ row.contactPhone }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="咨询诉求" min-width="300">
          <template #default="{ row }">
            <div class="content-cell">
              <p class="content-text">{{ row.content }}</p>
              <div v-if="row.reply" class="reply-preview">
                <lucide-corner-down-right :size="14" class="reply-icon" />
                <span class="reply-text">Admin: {{ row.reply }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="处理状态" width="140" align="center">
          <template #default="{ row }">
            <div class="status-box" :class="row.status === 1 ? 'resolved' : 'pending'">
              <span class="status-dot"></span>
              {{ row.status === 1 ? '已回复' : '等待中' }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="提交时间" width="180">
          <template #default="{ row }">
            <div class="time-cell">
              <lucide-clock :size="14" />
              <span>{{ formatDate(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button 
              class="oled-btn-primary sm" 
              @click="handleReply(row)"
            >
              <lucide-message-square :size="16" />
              <span>{{ row.status === 1 ? '追加' : '处理' }}</span>
            </el-button>
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

    <!-- Reply Dialog -->
    <el-dialog 
      v-model="dialogVisible" 
      title="工单回复与状态同步" 
      width="600px"
      class="oled-dialog"
      destroy-on-close
    >
      <div class="dialog-content">
        <div class="context-info">
          <label>咨询原文</label>
          <p>{{ selectedRow?.content }}</p>
        </div>
        
        <el-form :model="replyForm" label-position="top">
          <el-form-item label="官方答复内容">
            <el-input 
              v-model="replyForm.reply" 
              type="textarea" 
              :rows="6" 
              placeholder="请输入回复信息，用户将在个人中心收到实时通知..." 
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="oled-btn-text">再想想</el-button>
          <el-button 
            type="primary" 
            class="oled-btn-primary" 
            :loading="submitting" 
            @click="submitReply"
          >
            立即发送回复
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import {
  User as LucideUser,
  MessageSquare as LucideMessageSquare,
  Clock as LucideClock,
  CornerDownRight as LucideCornerDownRight
} from 'lucide-vue-next'

// --- State ---
const consultations = ref<any[]>([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const selectedRow = ref<any>(null)
const total = ref(0)

const page = reactive({
  pageNum: 1,
  pageSize: 10
})

const replyForm = reactive({
  reply: ''
})

// --- Computed ---
const pendingCount = computed(() => consultations.value.filter(c => c.status !== 1).length)
const resolvedCount = computed(() => consultations.value.filter(c => c.status === 1).length)

// --- Actions ---
const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/consultations', { params: page })
    consultations.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error('服务连接异常')
  } finally {
    loading.value = false
  }
}

const formatDate = (val: string) => {
  if (!val) return '-'
  return new Date(val).toLocaleString('zh-CN', {
    month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
  })
}

const handleReply = (row: any) => {
  selectedRow.value = row
  replyForm.reply = row.reply || ''
  dialogVisible.value = true
}

const submitReply = async () => {
  if (!replyForm.reply.trim()) {
    return ElMessage.warning('请输入回复内容')
  }
  
  submitting.value = true
  try {
    await request.put(`/api/consultations/${selectedRow.value.id}/status`, null, {
      params: { status: 1, reply: replyForm.reply }
    })
    ElMessage.success('工单回复已送达')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.consultation-view-wrapper {
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
  letter-spacing: -1px;
}

.view-subtitle {
  color: var(--admin-text-muted);
  font-size: 14px;
}

.header-stats { display: flex; gap: 24px; }
.mini-stat { display: flex; flex-direction: column; align-items: flex-end; }
.mini-stat .label { font-size: 11px; color: var(--admin-text-muted); text-transform: uppercase; letter-spacing: 1px; }
.mini-stat .value { font-size: 20px; font-weight: 800; font-family: var(--font-data); }
.mini-stat .value.warning { color: #f59e0b; }
.mini-stat .value.success { color: #22c55e; }

/* --- Table Styles --- */
.table-container { padding: 0; overflow: hidden; }

.user-info-cell { display: flex; align-items: center; gap: 12px; }
.avatar-placeholder {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--admin-surface-light);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--admin-primary);
  border: 1px solid var(--admin-border);
}

.user-meta { display: flex; flex-direction: column; }
.user-meta .name { font-weight: 600; color: var(--admin-text-main); font-size: 14px; }
.user-meta .phone { font-size: 11px; color: var(--admin-text-muted); font-family: var(--font-data); }

.content-cell { display: flex; flex-direction: column; gap: 8px; padding: 8px 0; }
.content-text { color: var(--admin-text-main); line-height: 1.5; font-size: 14px; }

.reply-preview {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  background: var(--admin-surface-light);
  padding: 8px 12px;
  border-radius: 8px;
  border-left: 3px solid var(--admin-primary);
}
.reply-icon { color: var(--admin-primary); margin-top: 2px; }
.reply-text { font-size: 12px; color: var(--admin-text-secondary); }

.status-box {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}
.status-dot { width: 6px; height: 6px; border-radius: 50%; }

.status-box.pending { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
.status-box.pending .status-dot { background: #f59e0b; animation: pulse 2s infinite; }
.status-box.resolved { background: rgba(34, 197, 94, 0.1); color: #22c55e; }
.status-box.resolved .status-dot { background: #22c55e; }

.time-cell { display: flex; align-items: center; gap: 6px; color: var(--admin-text-muted); font-size: 12px; font-family: var(--font-data); }

.pagination-area {
  padding: 24px;
  border-top: 1px solid var(--admin-border);
  display: flex;
  justify-content: flex-end;
}

/* --- Dialog Improvements --- */
.context-info {
  background: var(--admin-bg);
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 24px;
  border: 1px solid var(--admin-border);
}
.context-info label {
  display: block;
  font-size: 11px;
  color: var(--admin-text-muted);
  text-transform: uppercase;
  margin-bottom: 8px;
  font-weight: 700;
}
.context-info p { color: var(--admin-text-main); font-size: 14px; line-height: 1.6; }

@keyframes pulse {
  0% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
  100% { opacity: 1; transform: scale(1); }
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
