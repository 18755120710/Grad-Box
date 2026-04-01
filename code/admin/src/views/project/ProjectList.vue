<template>
  <div class="project-view-wrapper">
    <!-- Header Section -->
    <div class="view-header">
      <div class="title-area">
        <h2 class="view-title">项目资产库</h2>
        <p class="view-subtitle">管理与维护平台所有的毕业设计物料</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" class="oled-btn-primary" @click="handleAdd">
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
                <el-button circle class="op-btn edit" @click="handleEdit(row)">
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

    <!-- Professional Editor Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑项目资产' : '录入新资产'"
      width="900px"
      class="oled-dialog"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-row :gutter="24">
          <el-col :span="16">
            <el-form-item label="项目标题" prop="title">
              <el-input v-model="form.title" placeholder="清晰描述项目的核心价值" />
            </el-form-item>
            
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="技术栈" prop="techStack">
                  <el-input v-model="form.techStack" placeholder="Vue, SpringBoot, etc." />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="展示类型" prop="type">
                  <el-input v-model="form.type" placeholder="如: 全栈开发" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="项目深度介绍 (支持 HTML)" prop="contentHtml">
              <el-input 
                v-model="form.contentHtml" 
                type="textarea" 
                :rows="8" 
                placeholder="详细说明技术架构、核心算法或业务流程..." 
              />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="封面预览" prop="coverImage">
              <div class="upload-preview" :class="{ 'has-img': form.coverImage }">
                <img v-if="form.coverImage" :src="form.coverImage" />
                <div v-else class="upload-placeholder">
                  <lucide-image :size="32" />
                  <span>请输入预览图 URL</span>
                </div>
              </div>
              <el-input v-model="form.coverImage" placeholder="封面图片链接" class="mt-2" />
            </el-form-item>

            <el-form-item label="分类归属" prop="categoryId">
              <el-select v-model="form.categoryId" style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>

            <el-form-item label="定价建议">
              <el-input-number v-model="form.price" :min="0" style="width: 100%" controls-position="right" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="media-section">
          <h4 class="section-title">媒体资源拓展</h4>
          <div class="media-grid">
            <div v-for="(media, index) in form.medias" :key="index" class="media-row">
              <el-select v-model="media.type" style="width: 100px">
                <el-option label="图片" :value="1" />
                <el-option label="视频" :value="2" />
              </el-select>
              <el-input v-model="media.url" placeholder="资源直链" />
              <el-button circle plain type="danger" @click="removeMedia(index)">
                <lucide-x :size="14" />
              </el-button>
            </div>
          </div>
          <el-button link type="primary" @click="addMedia" class="mt-2">
            <lucide-plus :size="14" class="mr-1" /> 追加展示资源
          </el-button>
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="oled-btn-text">放弃</el-button>
          <el-button type="primary" class="oled-btn-primary" :loading="submitting" @click="submitForm">
            保存资产变更
          </el-button>
        </div>
      </template>
    </el-dialog>
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
  X as LucideX,
  ChevronRight as LucideChevronRight
} from 'lucide-vue-next'

// --- State ---
const projects = ref<any[]>([])
const categories = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const page = reactive({
  pageNum: 1,
  pageSize: 10
})

const filterForm = reactive({
  title: '',
  categoryId: null,
  status: null
})

const form = reactive({
  id: undefined,
  title: '',
  type: '',
  price: 0,
  techStack: '',
  featureIntro: '',
  contentHtml: '',
  coverImage: '',
  categoryId: null,
  status: 1,
  medias: [] as any[]
})

const rules = {
  title: [{ required: true, message: '资产标题不可为空', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择业务分类', trigger: 'change' }],
  coverImage: [{ required: true, message: '封面图为必填项', trigger: 'blur' }]
}

// --- Actions ---
const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/projects', { 
      params: { ...page, ...filterForm } 
    })
    projects.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    ElMessage.error('列表同步失败，请检查网络')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res: any = await request.get('/api/categories/list')
    categories.value = res.data
  } catch (err) {
    console.error('分类加载失败')
  }
}

const handleSearch = () => {
  page.pageNum = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

const resetForm = () => {
  Object.assign(form, {
    id: undefined,
    title: '',
    type: '',
    price: 0,
    techStack: '',
    featureIntro: '',
    contentHtml: '',
    coverImage: '',
    categoryId: null,
    status: 1,
    medias: []
  })
}

const handleEdit = async (row: any) => {
  isEdit.value = true
  dialogVisible.value = true
  loading.value = true
  try {
    const res: any = await request.get(`/api/projects/${row.id}`)
    Object.assign(form, res.data)
  } catch (err) {
    ElMessage.error('详情检索失败')
  } finally {
    loading.value = false
  }
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
    } catch (err) {
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
  } catch (err) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态同步失败')
  }
}

const handleCopy = (row: any) => {
  navigator.clipboard.writeText(`https://gradbox.com/project/${row.id}`)
  ElMessage.success('外链已拷贝至剪贴板')
}

const addMedia = () => form.medias.push({ type: 1, url: '' })
const removeMedia = (index: number) => form.medias.splice(index, 1)

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put('/api/projects', form)
      ElMessage.success('资产更新成功')
    } else {
      await request.post('/api/projects', form)
      ElMessage.success('新资产已完成录入')
    }
    dialogVisible.value = false
    loadData()
  } catch (err) {
    ElMessage.error('数据提交异常')
  } finally {
    submitting.value = false
  }
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
