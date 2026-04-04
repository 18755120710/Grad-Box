<template>
  <div class="project-view-wrapper">
    <!-- Header Section -->
    <div class="view-header">
      <div class="title-area">
        <h2 class="view-title">项目资产库</h2>
        <p class="view-subtitle">管理与维护平台所有的毕业设计物料</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" class="oled-btn-primary" @click="$router.push('/projects/new')">
          <lucide-plus :size="18" />
          <span>录入新项目</span>
        </el-button>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="glass-card filter-bar">
      <el-form :inline="true" :model="filterForm" class="oled-filter-form">
        <el-form-item label="关键词">
          <el-input v-model="filterForm.title" placeholder="项目名称 / 简介" clearable @input="handleSearch" />
        </el-form-item>
        <el-form-item label="所属分类">
          <el-select v-model="filterForm.categoryId" placeholder="全部" clearable @change="handleSearch">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- Content Area -->
    <div class="glass-card list-card">
      <el-table 
        :data="projects" 
        v-loading="loading" 
        class="oled-table"
        :header-cell-style="{ background: 'transparent' }"
      >
        <el-table-column label="项目预览" width="140">
          <template #default="{ row }">
            <div class="preview-box">
              <el-image 
                :src="row.coverImage" 
                fit="cover"
                class="project-preview-img"
                :preview-src-list="[row.coverImage]"
                preview-teleported
              />
              <div class="preview-overlay">
                <lucide-zoom-in :size="16" />
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="项目信息" min-width="240">
          <template #default="{ row }">
            <div class="info-cell">
              <div class="title-row">
                <span class="project-name">{{ row.title }}</span>
                <el-tag size="small" effect="plain" class="category-tag">{{ row.categoryName }}</el-tag>
              </div>
              <p class="project-tech">{{ row.techStack }}</p>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <div class="type-cell">
              <lucide-box :size="14" />
              <span>{{ row.type || '未定义' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="价格策略" width="140">
          <template #default="{ row }">
            <div class="price-cell">
              <span class="currency">¥</span>
              <span class="amount">{{ row.price }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="市场状态" width="120" align="center">
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

        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div class="op-btns">
              <el-tooltip content="编辑详情" placement="top">
                <el-button circle class="op-btn edit" @click="$router.push(`/projects/${row.id}`)">
                  <lucide-edit-3 :size="16" />
                </el-button>
              </el-tooltip>
              <el-tooltip content="复制链接" placement="top">
                <el-button circle class="op-btn copy" @click="handleCopy(row)">
                  <lucide-link :size="16" />
                </el-button>
              </el-tooltip>
              <el-tooltip content="彻底移除" placement="top">
                <el-button circle class="op-btn delete" @click="handleDelete(row)">
                  <lucide-trash-2 :size="16" />
                </el-button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
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
  Plus as LucidePlus,
  Layers as LucideLayers,
  ZoomIn as LucideZoomIn,
  Edit3 as LucideEdit3,
  Trash2 as LucideTrash2,
  Link as LucideLink,
  Box as LucideBox,
  Image as LucideImage,
  ChevronRight as LucideChevronRight
} from 'lucide-vue-next'

// --- State ---
const projects = ref<any[]>([])
const categories = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

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

/* --- Filter Bar --- */
.filter-bar {
  padding: 20px 32px;
  margin-bottom: 24px;
}

.oled-filter-form :deep(.el-form-item__label) {
  color: var(--admin-text-secondary);
  font-weight: 600;
}

/* --- Table Area --- */
.list-card {
  padding: 0;
  overflow: hidden;
}

.preview-box {
  width: 100px;
  height: 64px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  border: 1px solid var(--admin-border);
}

.project-preview-img {
  width: 100%;
  height: 100%;
  transition: transform 0.4s;
}

.preview-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: #fff;
}

.preview-box:hover .preview-overlay { opacity: 1; }
.preview-box:hover .project-preview-img { transform: scale(1.1); }

.info-cell { display: flex; flex-direction: column; gap: 4px; }
.title-row { display: flex; align-items: center; gap: 8px; }
.project-name { font-weight: 700; color: var(--admin-text-main); }
.project-tech { font-size: 12px; color: var(--admin-text-muted); font-family: var(--font-data); }

.type-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--admin-text-secondary);
  font-size: 13px;
}

.price-cell {
  display: flex;
  align-items: baseline;
  gap: 2px;
  color: #facc15;
  font-family: var(--font-data);
}
.currency { font-size: 12px; font-weight: 600; }
.amount { font-size: 18px; font-weight: 800; }

.op-btns { display: flex; gap: 8px; justify-content: center; }
.op-btn {
  background: var(--admin-surface-light) !important;
  border: 1px solid var(--admin-border) !important;
  color: var(--admin-text-muted) !important;
  transition: all 0.3s;
}
.op-btn:hover { transform: translateY(-2px); }
.op-btn.edit:hover { background: var(--admin-primary-glow) !important; color: var(--admin-primary) !important; border-color: var(--admin-primary) !important; }
.op-btn.delete:hover { background: rgba(239, 68, 68, 0.1) !important; color: #ef4444 !important; border-color: #ef4444 !important; }

.pagination-container {
  padding: 24px;
  border-top: 1px solid var(--admin-border);
  display: flex;
  justify-content: flex-end;
}

/* --- Dialog & Editor --- */
.upload-preview {
  width: 100%;
  aspect-ratio: 16/9;
  background: var(--admin-bg);
  border: 2px dashed var(--admin-border);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  transition: all 0.3s;
}

.upload-preview.has-img { border-style: solid; border-color: var(--admin-primary); }
.upload-preview img { width: 100%; height: 100%; object-fit: cover; }
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: var(--admin-text-muted);
}

.media-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--admin-border);
}

.section-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--admin-text-secondary);
  margin-bottom: 16px;
}

.media-row {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
