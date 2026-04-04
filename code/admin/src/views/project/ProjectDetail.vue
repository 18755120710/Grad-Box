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
        <h2 class="view-title">{{ isEdit ? '编辑项目资产' : '录入新资产' }}</h2>
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
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
            <el-form-item label="项目标题 (Title)" prop="title">
              <el-input v-model="form.title" placeholder="输入极具吸引力的项目名称..." class="large-input" />
            </el-form-item>
            
            <div class="row-flex">
              <el-form-item label="所属分类" prop="categoryId" class="flex-1">
                <el-select v-model="form.categoryId" placeholder="选择分类路径" class="full-width">
                  <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="展示类型" class="flex-1">
                <el-input v-model="form.type" placeholder="如: 全栈开发, 组件库" />
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
          <div class="section-title">展示资源流 (Gallery & Media)</div>
          <div class="media-section">
            <div class="media-grid">
              <div v-for="(media, index) in form.medias" :key="index" class="media-row">
                <el-select v-model="media.mediaType" style="width: 120px">
                  <el-option label="展示图片" :value="1" />
                  <el-option label="视频演示" :value="2" />
                </el-select>
                <div class="media-input-wrapper">
                  <el-input v-model="media.mediaUrl" placeholder="媒体直链 URL (CDN)">
                    <template #append>
                      <el-upload
                        :action="uploadUrl"
                        :headers="uploadHeaders"
                        :show-file-list="false"
                        :on-success="(res: any) => handleMediaUploadSuccess(res, index)"
                        :on-progress="(evt: any) => handleMediaProgress(evt, index)"
                      >
                        <el-button link type="primary" :disabled="mediaProgress[index] !== undefined && mediaProgress[index] < 100">
                          <lucide-upload :size="14" />
                        </el-button>
                      </el-upload>
                    </template>
                  </el-input>
                  <el-progress 
                    v-if="mediaProgress[index] !== undefined && mediaProgress[index] < 100"
                    :percentage="mediaProgress[index]" 
                    :show-text="false"
                    stroke-width="2"
                    class="upload-progress-bar"
                  />
                </div>
                <el-button circle plain type="danger" @click="removeMedia(index)" class="delete-media-btn">
                  <lucide-trash-2 :size="14" />
                </el-button>
              </div>
              <div v-if="form.medias.length === 0" class="empty-media">
                <lucide-layers :size="24" />
                <span>暂无追加展示资源</span>
              </div>
            </div>
            <el-button link type="primary" @click="addMedia" class="mt-4 add-media-btn">
              <lucide-plus :size="16" class="mr-1" /> 追加展示资源
            </el-button>
          </div>
        </div>
      </div>

      <!-- Right Panel: Media & Status -->
      <div class="panel-right">
        <div class="glass-card form-section sticky-section">
          <div class="section-title">封面与配置</div>
          
          <div class="media-upload-area">
            <label>项目主封面 (Cover Image)</label>
            <div class="cover-input-box">
              <el-upload
                class="cover-uploader"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleCoverSuccess"
                :on-progress="handleCoverProgress"
              >
                <div class="cover-preview" v-if="form.coverImage">
                  <img :src="form.coverImage" />
                  <div class="cover-mask">
                    <lucide-upload :size="24" />
                    <span>更换封面图</span>
                  </div>
                </div>
                <div class="cover-placeholder" v-else>
                  <template v-if="coverProgress > 0 && coverProgress < 100">
                    <el-progress type="circle" :percentage="coverProgress" :width="60" />
                    <span class="mt-2">正在同步云端...</span>
                  </template>
                  <template v-else>
                    <lucide-image :size="32" />
                    <span>上传项目封面图</span>
                  </template>
                </div>
              </el-upload>
              <div class="cover-input-group mt-4">
                <el-input v-model="form.coverImage" placeholder="或手动输入封面直链..." class="flex-1" />
                <el-button v-if="form.coverImage" type="danger" plain @click="handleDeleteCover">
                  <lucide-trash-2 :size="14" />
                </el-button>
              </div>
            </div>
          </div>

          <div class="form-item-group">
            <label>价格定位 (Price)</label>
            <el-input-number v-model="form.price" :precision="2" :step="10" class="full-width" controls-position="right" />
          </div>

          <div class="tag-manager">
            <label>特性标签 (Tags)</label>
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
            <label>发布流通状态 (Status)</label>
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

        <div class="glass-card form-section mt-6">
          <div class="section-title">外部链路</div>
          <el-form-item label="演示地址 (Demo URL)">
            <el-input v-model="form.demoUrl" placeholder="https://..." />
          </el-form-item>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type ElInput } from 'element-plus'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import { 
  LucideChevronLeft,
  LucideLayers,
  LucidePlus,
  LucideTrash2,
  LucideImage,
  LucideUpload,
  LucideCloudUpload,
  LucideSave
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isEdit = ref(!!route.params.id && route.params.id !== 'new')
const loading = ref(false)
const submitting = ref(false)
const categories = ref<any[]>([])
const formRef = ref()

const uploadUrl = 'http://localhost:8080/api/file/upload'
const uploadHeaders = {
  Authorization: (authStore.tokenHead || '') + (authStore.token || '')
}

// --- Form State ---
const form = reactive({
  id: route.params.id === 'new' ? undefined : Number(route.params.id),
  title: '',
  type: '',
  categoryId: null,
  techStack: '',
  contentHtml: '',
  coverImage: '',
  demoUrl: '',
  price: 0,
  status: 1,
  tags: [] as string[],
  medias: [] as any[]
})

// --- Upload Progress State ---
const coverProgress = ref(0)
const mediaProgress = reactive<Record<number, number>>({})
const isUploading = computed(() => {
  if (coverProgress.value > 0 && coverProgress.value < 100) return true
  return Object.values(mediaProgress).some(p => p > 0 && p < 100)
})

const rules = {
  title: [{ required: true, message: '资产标题不可为空', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择业务分类', trigger: 'change' }]
}

// --- Tag Input State ---
const inputValue = ref('')
const inputVisible = ref(false)
const InputRef = ref<InstanceType<typeof ElInput>>()

// --- Actions ---
const loadCategories = async () => {
  try {
    const res: any = await request.get('/api/categories/list')
    categories.value = res.data
  } catch (_err) {
    console.error('分类加载失败')
  }
}

const loadProject = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const res: any = await request.get(`/api/projects/${form.id}`)
    Object.assign(form, res.data)
    // 确保 medias 是数组
    if (!form.medias) form.medias = []
    if (!form.tags) form.tags = []
  } catch (_e) {
    ElMessage.error('无法调取资产快照')
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (_e) {
    return ElMessage.warning('请完善表单必填项')
  }
  
  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put(`/api/projects/${form.id}`, form)
    } else {
      await request.post('/api/projects', form)
    }
    ElMessage.success('资产数据已成功同步至云端')
    router.push('/projects')
  } catch (_e) {
    ElMessage.error('持久化链路异常')
  } finally {
    submitting.value = false
  }
}

const addMedia = () => form.medias.push({ mediaType: 1, mediaUrl: '' })

const removeMedia = async (index: number) => {
  const media = form.medias[index]
  if (media.mediaUrl) {
    try {
      await ElMessageBox.confirm('确认从云端永久删除此素材？', '警告', { type: 'warning' })
      await request.delete('/api/file/delete', { params: { url: media.mediaUrl } })
      ElMessage.success('云端文件已清理')
    } catch (e) {
      if (e === 'cancel') return
      console.error('Delete failed:', e)
    }
  }
  form.medias.splice(index, 1)
  delete mediaProgress[index]
}

const handleDeleteCover = async () => {
  if (!form.coverImage) return
  try {
    await ElMessageBox.confirm('确认删除当前封面图并同步清理云端文件？', '警告', { type: 'warning' })
    await request.delete('/api/file/delete', { params: { url: form.coverImage } })
    form.coverImage = ''
    coverProgress.value = 0
    ElMessage.success('封面已移除')
  } catch (e) {
    if (e === 'cancel') return
    ElMessage.error('清理失败')
  }
}

const handleCoverProgress = (evt: any) => {
  coverProgress.value = Math.round(evt.percent)
}

const handleCoverSuccess = (res: any) => {
  form.coverImage = res.data
  coverProgress.value = 100
  ElMessage.success('封面上传成功')
}

const handleMediaProgress = (evt: any, index: number) => {
  mediaProgress[index] = Math.round(evt.percent)
}

const handleMediaUploadSuccess = (res: any, index: number) => {
  form.medias[index].mediaUrl = res.data
  mediaProgress[index] = 100
  ElMessage.success('素材上传成功')
}

// --- Tag Logic ---
const showInput = () => {
  inputVisible.value = true
  nextTick(() => InputRef.value!.input!.focus())
}

const handleInputConfirm = () => {
  if (inputValue.value && !form.tags.includes(inputValue.value)) {
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
  padding-bottom: 60px;
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
  grid-template-columns: 1fr 360px;
  gap: 24px;
  align-items: start;
}

.form-section {
  padding: 32px;
}

.mt-6 { margin-top: 24px; }

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

/* --- Core Form Elements --- */
.large-input :deep(.el-input__wrapper) {
  font-size: 20px;
  padding: 10px 16px;
  font-weight: 700;
  background: var(--admin-bg) !important;
}

.row-flex { display: flex; gap: 20px; flex-wrap: wrap; }
.flex-1 { flex: 1; min-width: 150px; }
.flex-2 { flex: 2; min-width: 200px; }
.full-width { width: 100%; }

.monochrome-textarea :deep(.el-textarea__inner) {
  background: var(--admin-surface-light) !important;
  border: 1px solid var(--admin-border);
  font-family: var(--font-data);
  line-height: 1.8;
  padding: 20px;
  border-radius: 12px;
}

/* --- Media Section --- */
.media-section {
  display: flex;
  flex-direction: column;
}

.media-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.media-row {
  display: flex;
  gap: 12px;
  align-items: center;
  background: var(--admin-bg);
  padding: 8px;
  border-radius: 12px;
  border: 1px solid var(--admin-border);
}

.media-input-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
}

.upload-progress-bar {
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  padding: 0 4px;
}

.delete-media-btn:hover {
  background: rgba(239,68,68,0.1) !important;
}

.empty-media {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: var(--admin-text-muted);
  gap: 12px;
  border: 2px dashed var(--admin-border);
  border-radius: 16px;
}

.add-media-btn {
  align-self: flex-start;
  font-weight: 600;
}

/* --- Right Panel Elements --- */
.media-upload-area { margin-bottom: 24px; }
.media-upload-area label { display: block; font-size: 12px; color: var(--admin-text-muted); margin-bottom: 12px; font-weight: 700; }

.cover-uploader {
  width: 100%;
}

.cover-uploader :deep(.el-upload) {
  width: 100%;
  display: block;
}

.cover-input-box {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cover-input-group {
  display: flex;
  gap: 8px;
}

.cover-preview {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  aspect-ratio: 16/9;
  border: 1px solid var(--admin-border);
  box-shadow: var(--admin-shadow-sm);
  background: var(--admin-bg);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.cover-preview:hover {
  transform: translateY(-4px);
  box-shadow: var(--admin-shadow-lg);
  border-color: var(--admin-primary);
}

.cover-preview img { width: 100%; height: 100%; object-fit: cover; }
.cover-mask {
  position: absolute; inset: 0; 
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 12px; opacity: 0; transition: all 0.3s; color: #fff;
}
.cover-preview:hover .cover-mask { opacity: 1; }

.cover-placeholder {
  aspect-ratio: 16/9;
  border: 2px dashed var(--admin-border);
  background: var(--admin-surface-light);
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  color: var(--admin-text-muted);
  cursor: pointer;
  transition: all 0.4s ease;
}

.cover-placeholder:hover {
  border-color: var(--admin-primary);
  color: var(--admin-primary);
  background: var(--admin-primary-glow);
  transform: scale(1.02);
}

.cover-placeholder .lucide-image {
  transform: scale(1.2);
  transition: transform 0.4s;
}

.cover-placeholder:hover .lucide-image {
  transform: scale(1.4) rotate(5deg);
}

.cover-placeholder span {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.form-item-group { margin-bottom: 24px; }
.form-item-group label { display: block; font-size: 12px; color: var(--admin-text-muted); margin-bottom: 12px; font-weight: 700; }

.tag-manager { margin-bottom: 24px; }
.tag-manager label { display: block; font-size: 12px; color: var(--admin-text-muted); margin-bottom: 12px; font-weight: 700; }
.tag-inputs { display: flex; flex-wrap: wrap; gap: 8px; }

.status-config label { display: block; font-size: 12px; color: var(--admin-text-muted); margin-bottom: 12px; font-weight: 700; }
.status-toggle {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--admin-surface-light);
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

:deep(.el-input-number.full-width) {
  width: 100%;
}
</style>
