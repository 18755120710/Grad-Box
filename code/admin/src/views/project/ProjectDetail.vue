<template>
  <div class="project-detail-wrapper" v-loading="loading">
    <!-- Breadcrumb & Header -->
    <div class="view-header">
      <div class="breadcrumb-nav">
        <el-button link @click="$router.push('/projects')" class="back-btn">
          <lucide-chevron-left :size="18" /> 返回档案库
        </el-button>
      </div>
      <div class="header-main">
        <h2 class="view-title">{{ isEdit ? '编辑项目资产' : '创建全新项目' }}</h2>
        <div class="header-ops">
          <el-button class="oled-btn-outline" @click="$router.push('/projects')">放弃变更</el-button>
          <el-button type="primary" class="oled-btn-primary" @click="handleSave" :loading="submitting">
            <lucide-save :size="16" /> 固化资产数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- Main Form Grid -->
    <div class="detail-grid">
      <!-- Left Panel: Core Content -->
      <div class="panel-left">
        <div class="glass-card form-section">
          <div class="section-title">核心属性</div>
          <el-form :model="form" label-position="top">
            <el-form-item label="项目标题 (Title)">
              <el-input v-model="form.name" placeholder="输入极具吸引力的项目名称..." class="large-input" />
            </el-form-item>
            
            <div class="row-flex">
              <el-form-item label="所属分类" class="flex-1">
                <el-select v-model="form.categoryId" placeholder="选择分类路径" class="full-width">
                  <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="技术栈" class="flex-2">
                <el-input v-model="form.techStack" placeholder="Vue3, SpringBoot, MyBatis..." />
              </el-form-item>
            </div>

            <el-form-item label="项目深度描述 (Rich Content)">
              <el-input 
                v-model="form.contentHtml" 
                type="textarea" 
                :rows="12" 
                placeholder="在此编写详细的项目介绍、功能点及技术实现逻辑..." 
                class="monochrome-textarea"
              />
            </el-form-item>
          </el-form>
        </div>

        <div class="glass-card form-section">
          <div class="section-title">外部链路</div>
          <div class="row-flex">
            <el-form-item label="演示地址 (Demo)" class="flex-1">
              <el-input v-model="form.demoUrl" placeholder="https://..." />
            </el-form-item>
            <el-form-item label="价格定位 (Amount)" class="flex-1">
              <el-input-number v-model="form.price" :precision="2" :step="100" class="full-width" />
            </el-form-item>
          </div>
        </div>
      </div>

      <!-- Right Panel: Media & Status -->
      <div class="panel-right">
        <div class="glass-card form-section sticky-section">
          <div class="section-title">封面与媒体流</div>
          
          <div class="media-upload-area">
            <label>项目封面 (Cover)</label>
            <div class="cover-preview" v-if="form.coverImage">
              <img :src="form.coverImage" />
              <div class="cover-mask" @click="handleUploadCover">
                <lucide-upload :size="24" />
                <span>更换封面</span>
              </div>
            </div>
            <div class="cover-placeholder" v-else @click="handleUploadCover">
              <lucide-image :size="32" />
              <span>上传高清封面</span>
            </div>
          </div>

          <div class="tag-manager">
            <label>管理标签</label>
            <div class="tag-inputs">
              <el-tag 
                v-for="tag in form.tags" 
                :key="tag" 
                closable 
                @close="removeTag(tag)"
                class="oled-tag"
              >
                {{ tag }}
              </el-tag>
              <el-input
                v-if="inputVisible"
                ref="InputRef"
                v-model="inputValue"
                size="small"
                @keyup.enter="handleInputConfirm"
                @blur="handleInputConfirm"
                class="mini-tag-input"
              />
              <el-button v-else size="small" @click="showInput" class="oled-btn-text">+ 新增</el-button>
            </div>
          </div>

          <div class="status-config">
            <label>发布流通状态</label>
            <div class="status-toggle">
              <span :class="{ active: form.status === 1 }">公开上架</span>
              <el-switch 
                v-model="form.status" 
                :active-value="1" 
                :inactive-value="0" 
                active-color="var(--admin-primary)"
              />
              <span :class="{ active: form.status === 0 }">仓库封存</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage, ElInput } from 'element-plus'
import {
  ChevronLeft as LucideChevronLeft,
  Save as LucideSave,
  Image as LucideImage,
  Upload as LucideUpload
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const isEdit = ref(!!route.params.id && route.params.id !== 'new')
const loading = ref(false)
const submitting = ref(false)
const categories = ref<any[]>([])

// --- Form State ---
const form = reactive({
  id: route.params.id === 'new' ? null : Number(route.params.id),
  name: '',
  categoryId: null,
  techStack: '',
  contentHtml: '',
  coverImage: '',
  demoUrl: '',
  price: 0,
  status: 1,
  tags: [] as string[]
})

// --- Tag Input State ---
const inputValue = ref('')
const inputVisible = ref(false)
const InputRef = ref<InstanceType<typeof ElInput>>()

// --- Actions ---
const loadCategories = async () => {
  const res: any = await request.get('/api/categories/list')
  categories.value = res.data
}

const loadProject = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const res: any = await request.get(`/api/projects/${form.id}`)
    Object.assign(form, res.data)
  } catch (e) {
    ElMessage.error('无法调取资产快照')
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (!form.name) return ElMessage.warning('资产标题不能为空')
  
  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put('/api/projects', form)
    } else {
      await request.post('/api/projects', form)
    }
    ElMessage.success('资产数据已固化至云端')
    router.push('/projects')
  } catch (e) {
    ElMessage.error('持久化链路异常')
  } finally {
    submitting.value = false
  }
}

const handleUploadCover = () => {
  ElMessage.info('媒体溯源模块正在升级，请等待系统通知')
}

// --- Tag Logic ---
const showInput = () => {
  inputVisible.value = true
  nextTick(() => InputRef.value!.input!.focus())
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    form.tags.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}

const removeTag = (tag: string) => {
  form.tags = form.tags.filter(t => t !== tag)
}

onMounted(() => {
  loadCategories()
  loadProject()
})
</script>

<style scoped>
.project-detail-wrapper {
  animation: slide-up 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

/* --- Header --- */
.view-header {
  margin-bottom: 32px;
}

.back-btn {
  color: var(--admin-text-muted);
  font-size: 14px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.view-title {
  font-size: 32px;
  font-weight: 900;
  color: var(--admin-text-main);
  letter-spacing: -1.5px;
}

.header-ops { display: flex; gap: 16px; }

/* --- Grid Layout --- */
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 24px;
}

.form-section {
  padding: 32px;
  margin-bottom: 24px;
}

.section-title {
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 2px;
  color: var(--admin-primary);
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--admin-border);
}

/* --- Elements --- */
.large-input :deep(.el-input__wrapper) {
  font-size: 20px;
  padding: 8px 16px;
  font-weight: 700;
}

.row-flex { display: flex; gap: 20px; }
.flex-1 { flex: 1; }
.flex-2 { flex: 2; }
.full-width { width: 100%; }

.monochrome-textarea :deep(.el-textarea__inner) {
  background: var(--admin-surface-light) !important;
  border: 1px solid var(--admin-border);
  font-family: var(--font-data);
  line-height: 1.8;
  padding: 20px;
}

/* --- Right Panel --- */
.media-upload-area { margin-bottom: 32px; }
.media-upload-area label { display: block; font-size: 12px; color: var(--admin-text-muted); margin-bottom: 12px; font-weight: 700; }

.cover-preview {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  aspect-ratio: 16/9;
  border: 1px solid var(--admin-border);
}

.cover-preview img { width: 100%; height: 100%; object-fit: cover; }
.cover-mask {
  position: absolute; inset: 0; background: rgba(0,0,0,0.6);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 10px; opacity: 0; transition: 0.3s; cursor: pointer; color: #fff;
}
.cover-preview:hover .cover-mask { opacity: 1; }

.cover-placeholder {
  aspect-ratio: 16/9;
  border: 2px dashed var(--admin-border);
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: var(--admin-text-muted);
  cursor: pointer;
  transition: 0.3s;
}
.cover-placeholder:hover { border-color: var(--admin-primary); color: var(--admin-primary); background: var(--admin-primary-glow); }

.tag-manager { margin-bottom: 32px; }
.tag-manager label { display: block; font-size: 12px; color: var(--admin-text-muted); margin-bottom: 12px; font-weight: 700; }
.tag-inputs { display: flex; flex-wrap: wrap; gap: 8px; }

.status-config label { display: block; font-size: 12px; color: var(--admin-text-muted); margin-bottom: 12px; font-weight: 700; }
.status-toggle {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--admin-bg);
  padding: 12px 20px;
  border-radius: 12px;
  border: 1px solid var(--admin-border);
}

.status-toggle span { font-size: 13px; color: var(--admin-text-muted); transition: 0.3s; }
.status-toggle span.active { color: var(--admin-text-main); font-weight: 700; }

@keyframes slide-up {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
