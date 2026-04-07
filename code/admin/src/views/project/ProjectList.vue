<template>
  <div class="project-view-wrapper" v-loading="loading">
    <!-- View Header (Aligned with Detail page) -->
    <div class="view-header-seamless">
      <div class="header-left">
        <div class="header-breadcrumb-lite">
          <span class="crumb-root">归档系统</span>
          <span class="crumb-separator">/</span>
          <span class="crumb-current">资产主仓库</span>
        </div>
      </div>
      <div class="header-ops">
        <el-button type="primary" class="oled-btn-primary" @click="$router.push('/projects/new')">
          <lucide-plus :size="16" /> <span>录入新项目</span>
        </el-button>
      </div>
    </div>

    <div class="seamless-canvas">
      <!-- Main Content Stream -->
      <div class="canvas-main-flow">
        <section class="canvas-section">
          <div class="section-indicator">PROJECT STREAM</div>
          <div class="stream-header-seamless">
            <div class="title-group">
              <h3 class="section-heading-text">资产流</h3>
              <p class="section-muted-text">按时间顺序排列的数字资产存档</p>
            </div>
            <div class="stream-meta">
              <span class="count-chip">{{ total }} 项资产</span>
            </div>
          </div>

          <div class="asset-stream-container">
            <TransitionGroup name="stagger-list" tag="div" class="asset-list-flow">
              <div v-for="row in projects" :key="row.id" class="asset-row-item">
                <!-- Preview Indicator -->
                <div class="asset-row-preview" @click="$router.push(`/projects/${row.id}`)">
                  <img :src="row.coverImage" v-if="row.coverImage" class="row-thumb" />
                  <div class="row-thumb-placeholder" v-else>
                    <lucide-image :size="20" />
                  </div>
                  <div class="row-preview-overlay">
                    <lucide-arrow-up-right :size="18" />
                  </div>
                </div>

                <!-- Primary Info -->
                <div class="asset-row-info" @click="$router.push(`/projects/${row.id}`)">
                  <div class="info-headline">
                    <span class="asset-name">{{ row.title }}</span>
                    <el-tag size="small" effect="plain" class="flat-tag-mini">{{ row.categoryName }}</el-tag>
                  </div>
                  <div class="info-sub">
                    <span class="tech-stack-text">{{ row.techStack }}</span>
                  </div>
                </div>

                <!-- Logistics / Meta -->
                <div class="asset-row-meta">
                  <div class="meta-block">
                    <span class="meta-label">定价</span>
                    <span class="meta-value price-font">¥{{ row.price }}</span>
                  </div>
                  <div class="meta-block">
                    <span class="meta-label">状态</span>
                    <el-switch
                      v-model="row.status"
                      :active-value="1"
                      :inactive-value="0"
                      size="small"
                      @change="handleStatusChange(row)"
                      class="mini-switch"
                    />
                  </div>
                </div>

                <!-- Quick Actions (Hover Only) -->
                <div class="asset-row-actions">
                  <el-button circle class="action-btn-lite edit" @click="$router.push(`/projects/${row.id}`)">
                    <lucide-edit-3 :size="14" />
                  </el-button>
                  <el-button circle class="action-btn-lite copy" @click="handleCopy(row)">
                    <lucide-link :size="14" />
                  </el-button>
                  <el-button circle class="action-btn-lite delete" @click="handleDelete(row)">
                    <lucide-trash-2 :size="14" />
                  </el-button>
                </div>
              </div>
            </TransitionGroup>

            <!-- Empty State -->
            <div v-if="projects.length === 0 && !loading" class="canvas-empty-state">
              <div class="empty-icon-ring">
                <lucide-inbox :size="24" />
              </div>
              <div class="empty-text">
                <span class="primary-msg">暂无匹配资产</span>
                <span class="secondary-msg">尝试调整过滤器或录入新资产</span>
              </div>
            </div>
          </div>

          <div class="pagination-flow">
            <el-pagination
              v-model:current-page="page.pageNum"
              v-model:page-size="page.pageSize"
              :total="total"
              layout="prev, pager, next"
              @current-change="loadData"
              class="seamless-pagination"
            />
          </div>
        </section>
      </div>

      <!-- Support Stream (Filters) -->
      <aside class="canvas-support-stream">
        <div class="sticky-support-panel">
          
          <div class="support-block filter-main">
            <h4 class="support-title">按索引筛选</h4>
            <div class="filter-group-v2">
              <div class="filter-item-v2">
                <label>关键词</label>
                <el-input 
                  v-model="filterForm.title" 
                  placeholder="项目名称 / 简介" 
                  clearable 
                  @input="handleSearch"
                  class="input-seamless-v2"
                >
                  <template #prefix><lucide-search :size="12" /></template>
                </el-input>
              </div>

              <div class="filter-item-v2">
                <label>所属分类</label>
                <el-select v-model="filterForm.categoryId" placeholder="全部范畴" clearable @change="handleSearch" class="seamless-select">
                  <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
                </el-select>
              </div>

              <div class="filter-item-v2">
                <label>发布状态</label>
                <el-select v-model="filterForm.status" placeholder="所有状态" clearable @change="handleSearch" class="seamless-select">
                  <el-option label="公开发布" :value="1" />
                  <el-option label="仓库封存" :value="0" />
                </el-select>
              </div>
            </div>
            <div class="filter-actions-v2">
              <el-button link class="btn-clear-v2" @click="resetFilters">
                <lucide-rotate-ccw :size="12" /> <span>重置索引</span>
              </el-button>
            </div>
          </div>

          <div class="support-block stats-block">
            <h4 class="support-title">概括统计</h4>
            <div class="mini-stats-v2">
              <div class="stat-card-lite">
                <span class="s-label">总估值</span>
                <span class="s-value price-font">¥{{ totalValuation }}</span>
              </div>
              <div class="stat-card-lite">
                <span class="s-label">在线率</span>
                <span class="s-value">{{ onlineRate }}%</span>
              </div>
            </div>
          </div>

        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus as LucidePlus,
  Layers as LucideLayers,
  ZoomIn as LucideZoomIn,
  Edit3 as LucideEdit3,
  Trash2 as LucideTrash2,
  Link as LucideLink,
  Box as LucideBox,
  Image as LucideImage,
  ChevronRight as LucideChevronRight,
  Search as LucideSearch,
  Filter as LucideFilter,
  RotateCcw as LucideRotateCcw,
  ArrowUpRight as LucideArrowUpRight,
  Inbox as LucideInbox
} from 'lucide-vue-next'

// --- State ---
const projects = ref<any[]>([])
const categories = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

const totalValuation = computed(() => {
  return projects.value.reduce((acc, p) => acc + (p.price || 0), 0).toFixed(2)
})

const onlineRate = computed(() => {
  if (projects.value.length === 0) return 0
  const online = projects.value.filter(p => p.status === 1).length
  return Math.round((online / projects.value.length) * 100)
})

const page = reactive({
  pageNum: 1,
  pageSize: 10
})

const filterForm = reactive({
  title: '',
  categoryId: null,
  status: null
})


// --- Actions ---
const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/projects', { 
      params: { ...page, ...filterForm } 
    })
    projects.value = res.data.records
    total.value = res.data.total
  } catch (_err) {
    ElMessage.error('列表同步失败，请检查网络')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res: any = await request.get('/api/categories/list')
    categories.value = res.data
  } catch (_err) {
    console.error('分类加载失败')
  }
}

const handleSearch = () => {
  page.pageNum = 1
  loadData()
}

const resetFilters = () => {
  filterForm.title = ''
  filterForm.categoryId = null
  filterForm.status = null
  handleSearch()
}



const handleDelete = (row: any) => {
  ElMessageBox.confirm(`即将永久移除项目 "${row.title}"，此操作不可撤销。`, '安全确认', {
    confirmButtonText: '确定移除',
    cancelButtonText: '点错了',
    type: 'warning',
    customClass: 'oled-message-box'
  }).then(async () => {
    try {
      await request.delete(`/api/projects/${row.id}`)
      ElMessage.success('资产已移除')
      loadData()
    } catch (_err) {
      ElMessage.error('移除操作失败')
    }
  })
}

const handleStatusChange = async (row: any) => {
  try {
    await request.put('/api/projects', row)
    ElMessage({
      message: `项目已${row.status === 1 ? '上线发布' : '临时下架'}`,
      type: row.status === 1 ? 'success' : 'warning'
    })
  } catch (_err) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态同步失败')
  }
}

const handleCopy = (row: any) => {
  navigator.clipboard.writeText(`https://gradbox.com/project/${row.id}`)
  ElMessage.success('外链已拷贝至剪贴板')
}



onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.project-view-wrapper {
  animation: canvas-fade 0.8s cubic-bezier(0.16, 1, 0.3, 1);
  padding: 0 40px 100px;
  max-width: 1400px;
  margin: 0 auto;
}

/* --- View Header (Seamless) --- */
.view-header-seamless {
  height: 80px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--admin-border);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-breadcrumb-lite {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: var(--admin-text-muted);
}

.crumb-separator {
  color: var(--admin-border);
  font-weight: 300;
}

.crumb-current {
  color: var(--admin-text-secondary);
  font-weight: 700;
}

/* --- Canvas Layout --- */
.seamless-canvas {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 60px;
  align-items: start;
}

.canvas-main-flow {
  display: flex;
  flex-direction: column;
  gap: 60px;
}

.canvas-section {
  position: relative;
}

.section-indicator {
  position: absolute;
  left: -120px;
  top: 0;
  width: 100px;
  font-family: var(--font-data);
  font-size: 10px;
  font-weight: 800;
  color: var(--admin-border);
  letter-spacing: 1px;
  transform: rotate(-90deg) translateX(-100%);
  transform-origin: left top;
  text-align: right;
  white-space: nowrap;
  pointer-events: none;
}

.stream-header-seamless {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--admin-border);
}

.section-heading-text {
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.5px;
  margin: 0;
}

.section-muted-text {
  font-size: 13px;
  color: var(--admin-text-muted);
  margin: 4px 0 0 0;
}

.count-chip {
  font-family: var(--font-data);
  font-size: 11px;
  font-weight: 700;
  background: var(--admin-surface-light);
  color: var(--admin-text-secondary);
  padding: 4px 12px;
  border-radius: 20px;
  border: 1px solid var(--admin-border);
}

/* --- Asset Stream Flow --- */
.asset-list-flow {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.asset-row-item {
  display: grid;
  grid-template-columns: 80px 1fr 180px 140px;
  align-items: center;
  gap: 24px;
  padding: 12px 16px;
  background: var(--admin-surface-light);
  border: 1px solid var(--admin-border);
  border-radius: 14px;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
}

.asset-row-item:hover {
  background: var(--admin-surface);
  border-color: var(--admin-primary-glow);
  transform: translateX(8px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.03);
}

.asset-row-preview {
  width: 80px;
  height: 54px;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  background: var(--admin-bg);
}

.row-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.row-thumb-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--admin-border);
}

.row-preview-overlay {
  position: absolute;
  inset: 0;
  background: rgba(var(--admin-primary-rgb), 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: 0.3s;
  color: var(--admin-primary);
  backdrop-filter: blur(2px);
}

.asset-row-preview:hover .row-preview-overlay { opacity: 1; }
.asset-row-preview:hover .row-thumb { transform: scale(1.1); }

.asset-name {
  font-weight: 700;
  font-size: 15px;
  color: var(--admin-text-main);
  margin-right: 12px;
}

.flat-tag-mini {
  font-size: 10px;
  font-weight: 600;
  border-radius: 4px;
  background: var(--admin-surface) !important;
  border: 1px solid var(--admin-border) !important;
  color: var(--admin-text-muted) !important;
}

.info-sub {
  margin-top: 4px;
}

.tech-stack-text {
  font-family: var(--font-data);
  font-size: 11px;
  color: var(--admin-text-muted);
  opacity: 0.8;
}

.asset-row-meta {
  display: flex;
  gap: 32px;
}

.meta-block {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.meta-label {
  font-size: 9px;
  font-weight: 700;
  color: var(--admin-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.meta-value {
  font-size: 14px;
  font-weight: 700;
  color: var(--admin-text-secondary);
}

.price-font {
  font-family: var(--font-data);
  color: #facc15;
}

/* Actions (Hover) */
.asset-row-actions {
  display: flex;
  gap: 6px;
  opacity: 0;
  transition: 0.3s;
  justify-content: flex-end;
}

.asset-row-item:hover .asset-row-actions {
  opacity: 1;
}

.action-btn-lite {
  background: var(--admin-surface) !important;
  border: 1px solid var(--admin-border) !important;
  color: var(--admin-text-muted) !important;
}

.action-btn-lite:hover {
  transform: translateY(-2px);
}

.action-btn-lite.edit:hover { background: var(--admin-primary-glow) !important; color: var(--admin-primary) !important; border-color: var(--admin-primary) !important; }
.action-btn-lite.delete:hover { background: rgba(239, 68, 68, 0.1) !important; color: #ef4444 !important; border-color: #ef4444 !important; }

/* --- Sidebar Support Stream --- */
.canvas-support-stream {
  padding-top: 0;
}

.sticky-support-panel {
  position: sticky;
  top: 40px;
  display: flex;
  flex-direction: column;
  gap: 48px;
}

.support-block {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.support-title {
  font-family: var(--font-data);
  font-size: 10px;
  font-weight: 800;
  text-transform: uppercase;
  color: var(--admin-text-muted);
  letter-spacing: 1.5px;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.support-title::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--admin-border);
}

.filter-group-v2 {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-item-v2 {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-item-v2 label {
  font-size: 11px;
  font-weight: 700;
  color: var(--admin-text-secondary);
}

.input-seamless-v2 :deep(.el-input__wrapper) {
  background: var(--admin-surface-light) !important;
  box-shadow: none !important;
  border: 1px solid var(--admin-border) !important;
  border-radius: 10px;
}

.seamless-select {
  width: 100%;
}

.btn-clear-v2 {
  font-size: 10px;
  font-weight: 700;
  color: var(--admin-text-muted);
}

/* Stats */
.mini-stats-v2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.stat-card-lite {
  background: var(--admin-surface-light);
  padding: 12px;
  border-radius: 12px;
  border: 1px solid var(--admin-border);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.s-label {
  font-size: 9px;
  font-weight: 700;
  color: var(--admin-text-muted);
}

.s-value {
  font-size: 16px;
  font-weight: 800;
  color: var(--admin-text-main);
}

/* --- Empty state & Pagination --- */
.canvas-empty-state {
  padding: 80px 40px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.empty-icon-ring {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: var(--admin-surface-light);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--admin-border);
  border: 1px solid var(--admin-border);
}

.empty-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.primary-msg { font-size: 16px; font-weight: 700; color: var(--admin-text-secondary); }
.secondary-msg { font-size: 12px; color: var(--admin-text-muted); }

.pagination-flow {
  margin-top: 48px;
  display: flex;
  justify-content: center;
}

.seamless-pagination :deep(.el-pager li) {
  background: transparent !important;
  border-radius: 8px;
  color: var(--admin-text-muted);
}

.seamless-pagination :deep(.el-pager li.is-active) {
  background: var(--admin-primary) !important;
  color: #fff !important;
}

@keyframes canvas-fade {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.stagger-list-enter-active { transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.stagger-list-enter-from { opacity: 0; transform: translateY(10px); }

@media (max-width: 1200px) {
  .seamless-canvas { grid-template-columns: 1fr; gap: 40px; }
  .canvas-support-stream { order: -1; }
  .section-indicator { display: none; }
}
</style>
