<template>
  <div class="user-view-wrapper">
    <!-- Header Area -->
    <div class="view-header">
      <div class="title-area">
        <h2 class="view-title">账户与权限治理</h2>
        <p class="view-subtitle">在 OLED 环境下全方位审视并管理平台用户的活跃度与访问权限</p>
      </div>
      <div class="header-actions">
        <el-button-group class="oled-btn-group">
          <el-button type="primary" class="oled-btn-primary">全部用户</el-button>
          <el-button class="oled-btn-outline">管理员</el-button>
        </el-button-group>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="glass-card filter-bar">
      <el-form :inline="true" :model="queryForm" class="oled-filter-form">
        <el-form-item label="身份标识">
          <el-input v-model="queryForm.username" placeholder="UID / 手机号 / 账号名" clearable @input="handleSearch" />
        </el-form-item>
        <el-form-item label="信用状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="正常" :value="1" />
            <el-option label="封禁中" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- User Grid/List -->
    <div class="glass-card table-container">
      <el-table 
        :data="users" 
        v-loading="loading" 
        class="oled-table"
      >
        <el-table-column label="数字身份" width="220">
          <template #default="{ row }">
            <div class="user-profile-cell">
              <div class="avatar-glow">
                <el-avatar :size="40" :src="row.avatar" />
              </div>
              <div class="user-info">
                <span class="user-name">{{ row.username }}</span>
                <span class="user-id">UID-{{ row.id?.toString().padStart(6, '0') }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="联系方式" width="160">
          <template #default="{ row }">
            <div class="contact-cell">
              <lucide-phone :size="12" />
              <span>{{ row.phone || '未绑定' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="授权级别" width="140">
          <template #default="{ row }">
            <div class="role-pills">
              <el-tag :type="row.role === 'admin' ? 'danger' : 'info'" effect="dark" round size="small">
                {{ row.role === 'admin' ? 'ROOT' : 'USER' }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="最近活动" min-width="180">
          <template #default="{ row }">
            <div class="activity-cell">
              <span class="ip-addr">182.1.2.{{ Math.floor(Math.random() * 255) }}</span>
              <p class="last-seen">{{ formatDate(row.createTime) }}</p>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="风险控制" width="120" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              active-color="var(--admin-primary)"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>

        <el-table-column label="治理操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <div class="governance-actions">
              <el-tooltip content="重置密码" placement="top">
                <el-button circle class="action-btn" @click="handleResetPwd(row)">
                  <lucide-key :size="16" />
                </el-button>
              </el-tooltip>
              <el-tooltip content="强制离线" placement="top">
                <el-button circle class="action-btn warning" @click="handleKick(row)">
                  <lucide-log-out :size="16" />
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
import { ref, onMounted, reactive } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Phone as LucidePhone,
  Key as LucideKey,
  LogOut as LucideLogOut,
  ShieldCheck as LucideShield
} from 'lucide-vue-next'

// --- State ---
const users = ref<any[]>([])
const loading = ref(false)
const total = ref(0)

const page = reactive({
  pageNum: 1,
  pageSize: 10
})

const queryForm = reactive({
  username: '',
  status: null
})

// --- Actions ---
const formatDate = (val: string) => {
  if (!val) return '-'
  return new Date(val).toLocaleString('zh-CN', {
    month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
  })
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/user/list', { params: { ...page, ...queryForm } })
    users.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error('无法同步用户目录')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.pageNum = 1
  loadData()
}

const handleStatusChange = async (row: any) => {
  try {
    await request.put('/api/user/status', null, { 
      params: { id: row.id, status: row.status } 
    })
    ElMessage({
      message: `账户 [${row.username}] 信用状态已同步`,
      type: row.status === 1 ? 'success' : 'warning'
    })
  } catch (e) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态溯源失败')
  }
}

const handleResetPwd = (row: any) => {
  ElMessageBox.prompt('请输入重置后的新密码', '账户治理', {
    confirmButtonText: '立即变更',
    cancelButtonText: '取消',
    inputPattern: /^[a-zA-Z0-9]{6,}$/,
    inputErrorMessage: '密码格式不正确 (至少6位)',
    customClass: 'oled-message-box'
  }).then(({ value }) => {
    ElMessage.success(`密码变更指令已送达服务器`)
  })
}

const handleKick = (row: any) => {
  ElMessageBox.confirm(`强制用户 [${row.username}] 断开链接并清除所有 Token?`, '安全控制', {
    confirmButtonText: '执行踢出',
    cancelButtonText: '放弃',
    type: 'error',
    customClass: 'oled-message-box'
  }).then(() => {
    ElMessage.error('该用户具备高级别保护，操作已拦截')
  })
}

onMounted(loadData)
</script>

<style scoped>
.user-view-wrapper {
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
  margin-top: 4px;
}

/* --- Filter --- */
.filter-bar { padding: 20px 32px; margin-bottom: 24px; }

/* --- Table Area --- */
.table-container { padding: 0; overflow: hidden; }

.user-profile-cell { display: flex; align-items: center; gap: 16px; }
.avatar-glow {
  position: relative;
  padding: 2px;
  border-radius: 50%;
  background: var(--admin-primary-glow);
  box-shadow: 0 0 15px var(--admin-primary-glow);
}

.user-info { display: flex; flex-direction: column; gap: 4px; }
.user-name { font-weight: 700; color: var(--admin-text-main); font-size: 15px; }
.user-id { font-family: var(--font-data); font-size: 10px; color: var(--admin-text-muted); text-transform: uppercase; }

.contact-cell { display: flex; align-items: center; gap: 6px; color: var(--admin-text-secondary); font-size: 13px; font-family: var(--font-data); }

.activity-cell { display: flex; flex-direction: column; gap: 4px; }
.ip-addr { font-size: 12px; color: var(--admin-text-secondary); font-family: var(--font-data); }
.last-seen { font-size: 11px; color: var(--admin-text-muted); }

.governance-actions { display: flex; gap: 8px; justify-content: center; }
.action-btn {
  background: var(--admin-surface-light) !important;
  border: 1px solid var(--admin-border) !important;
  color: var(--admin-text-muted) !important;
  transition: all 0.3s;
}
.action-btn:hover { background: var(--admin-primary-glow) !important; color: var(--admin-primary) !important; border-color: var(--admin-primary) !important; }
.action-btn.warning:hover { background: rgba(245, 158, 11, 0.1) !important; color: #f59e0b !important; border-color: #f59e0b !important; }

.pagination-area {
  padding: 24px;
  border-top: 1px solid var(--admin-border);
  display: flex;
  justify-content: flex-end;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
