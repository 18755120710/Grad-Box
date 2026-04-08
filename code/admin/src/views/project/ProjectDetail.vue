<template>
  <div class="project-detail-wrapper" v-loading="loading">
    <!-- Breadcrumb & Header -->
    <div class="view-header">
      <div class="header-left">
        <el-button link @click="$router.push('/projects')" class="back-btn">
          <lucide-arrow-left :size="16" /> 归档概览
        </el-button>
        <div class="header-breadcrumb-lite">
          <span class="crumb-separator">/</span>
          <span class="crumb-current">{{ isEdit ? '校准资产详情' : '编目新资产' }}</span>
        </div>
      </div>
      <div class="header-ops">
        <el-button class="btn-minimal-outline" @click="$router.push('/projects')">放弃变更</el-button>
        <el-button type="primary" class="oled-btn-primary handle-save-btn" @click="handleSave" :loading="submitting">
          <lucide-save :size="16" /> 固化资产数据
        </el-button>
      </div>
    </div>

    <!-- Seamless Canvas Layout -->
    <div class="seamless-canvas">
      <!-- Unified Main Column -->
      <div class="canvas-main-flow">
        
        <!-- Section: Primary Identity -->
        <section class="canvas-section hero-section">
          <div class="section-indicator">01 . 核心身份</div>
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="borderless-form">
            <div class="title-headline">
              <el-form-item prop="title" class="no-label-item">
                <el-input 
                  v-model="form.title" 
                  type="textarea"
                  :autosize="{ minRows: 1, maxRows: 3 }"
                  placeholder="请输入资产标题..." 
                  class="headline-input-v3"
                />
              </el-form-item>
              
              <el-form-item prop="description" class="no-label-item description-item">
                <div class="description-header-lite">
                  <el-input
                    v-model="form.description"
                    type="textarea"
                    :rows="2"
                    placeholder="请输入资产简要描述（显示在列表页卡片中）..."
                    class="description-input-v3"
                  />
                  <el-button 
                    link 
                    type="primary" 
                    @click="handleAiSummary" 
                    :loading="aiLoading"
                    class="btn-ai-magic"
                  >
                    <lucide-sparkles :size="14" /> AI 智能生成
                  </el-button>
                </div>
              </el-form-item>
            </div>
            
            <div class="meta-row-seamless">
              <div class="meta-item">
                <label>所属分类</label>
                <el-select v-model="form.categoryId" placeholder="选择路径" class="seamless-select">
                  <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
                </el-select>
              </div>
              <div class="meta-item">
                <label>呈现类型</label>
                <el-input v-model="form.type" placeholder="如: Web App" class="seamless-input" />
              </div>
              <div class="meta-item flex-auto">
                <label>核心技术栈</label>
                <el-input v-model="form.techStack" placeholder="主要技术组件..." class="seamless-input tech-font">
                  <template #prefix>
                    <div class="tech-icon-box"><lucide-layers :size="12" /></div>
                  </template>
                </el-input>
              </div>
            </div>
          </el-form>
        </section>

        <div class="canvas-divider"></div>

        <!-- Section: Rich Content -->
        <section class="canvas-section editor-section">
          <div class="section-indicator">02 . 资产档案</div>
          <div class="section-header-seamless">
            <div class="header-main-info">
              <h3 class="section-heading-text">深度解析</h3>
              <p class="section-muted-text">在此沉淀项目的详尽描述与技术亮点</p>
            </div>
            <div class="header-extra">
              <el-upload
                :auto-upload="false"
                :show-file-list="false"
                accept=".md"
                @change="handlePrdImport"
                class="prd-import-uploader"
              >
                <el-button link type="primary" class="btn-import-prd">
                  <lucide-file-text :size="14" /> <span>导入需求文档 (Markdown)</span>
                </el-button>
              </el-upload>
              <span class="word-count-chip">2026.04 修订版</span>
            </div>
          </div>
          <div class="seamless-editor-wrapper">
            <md-editor 
              v-model="form.contentHtml" 
              placeholder="Start drafting..." 
              class="distilled-editor"
              language="zh-CN"
              :toolbars="[
                'bold', 'italic', 'strikeThrough', '-', 'title', 'sub', 'sup', 'quote', 'unorderedList', 'orderedList', 'task', '-', 'codeRow', 'code', 'link', 'image', 'table', 'mermaid', 'katex', '-', 'revoke', 'next', 'save', '=', 'pageFullscreen', 'fullscreen', 'preview', 'htmlPreview'
              ]"
            />
          </div>
        </section>

        <div class="canvas-divider"></div>

        <!-- Section: Gallery -->
        <section class="canvas-section gallery-section-seamless">
          <div class="section-indicator">03 . 媒体资产</div>
          <div class="section-header-seamless flex-between">
            <div class="title-group">
              <h3 class="section-heading-text">媒体资产流</h3>
              <p class="section-muted-text">管理展示图片、演示视频等扩展资源</p>
            </div>
            <el-button link type="primary" @click="addMedia" class="btn-seamless-add">
              <lucide-plus-circle :size="16" /> <span>追加新素材</span>
            </el-button>
          </div>

          <div class="asset-flow-container">
            <TransitionGroup name="stagger-list" tag="div" class="asset-grid-seamless">
              <div v-for="(media, index) in form.medias" :key="index" class="asset-strip-item">
                <div class="asset-type-indicator" :class="{ 'is-video': media.mediaType === 2 }">
                  <div class="indicator-inner">
                    <lucide-image v-if="media.mediaType === 1" :size="14" />
                    <lucide-play v-else :size="14" />
                  </div>
                </div>
                
                <div class="asset-config-row">
                  <el-select v-model="media.mediaType" class="type-mini-selector">
                    <el-option label="图片" :value="1" />
                    <el-option label="视频" :value="2" />
                  </el-select>
                  
                  <div class="asset-url-field">
                    <el-input v-model="media.mediaUrl" placeholder="输入链接或点击右侧上传..." class="input-minimal">
                      <template #append>
                        <el-upload
                          :show-file-list="false"
                          :http-request="(options: any) => handleMediaUpload(options, index)"
                        >
                          <el-button link :disabled="(mediaProgress[index] ?? 0) > 0 && (mediaProgress[index] ?? 0) < 100" class="upload-trigger-btn">
                            <lucide-cloud-upload :size="14" />
                          </el-button>
                        </el-upload>
                      </template>
                    </el-input>
                    <div v-if="(mediaProgress[index] ?? 0) > 0 && (mediaProgress[index] ?? 0) < 100" class="asset-upload-overlay">
                      <el-progress type="circle" :percentage="mediaProgress[index] ?? 0" :width="60" :stroke-width="3" color="#7c3aed" />
                      <div class="mini-status-hint">{{ mediaStatus[index] === 'calculating' ? '校验...' : '传输...' }}</div>
                      <el-button link class="mini-pause-btn" @click.stop="pauseMediaUpload(index)">
                        <lucide-circle-stop :size="16" />
                      </el-button>
                    </div>
                  </div>
                </div>

                <div class="asset-preview-box" :class="{ 'has-url': !!media.mediaUrl }" @click="handlePreview(media)">
                  <template v-if="media.mediaUrl">
                    <img
                      v-if="media.mediaType === 1" 
                      :src="media.mediaUrl" 
                      class="thumb-img-static" 
                    />
                    <div v-else class="video-preview-placeholder">
                      <lucide-monitor-play :size="14" />
                      <span class="preview-label">预览</span>
                    </div>
                  </template>
                  <div v-else class="empty-preview">
                    <lucide-inbox :size="12" />
                  </div>
                </div>

                <el-button circle class="btn-item-delete" @click="removeMedia(index)">
                  <lucide-x :size="14" />
                </el-button>
              </div>
            </TransitionGroup>

            <div v-if="form.medias.length === 0" class="canvas-empty-state">
              <div class="empty-icon-ring">
                <lucide-inbox :size="24" />
              </div>
              <div class="empty-text">
                <span class="primary-msg">尚未收录展示素材</span>
                <span class="secondary-msg">点击上方追加资产资产</span>
              </div>
            </div>
          </div>
        </section>
      </div>

      <!-- Right Support Stream -->
      <aside class="canvas-support-stream">
        <div class="sticky-support-panel">
          
          <div class="support-block cover-block">
            <h4 class="support-title">资产封面视图</h4>
            <div class="seamless-cover-uploader">
              <el-upload
                class="uploader-canvas"
                :show-file-list="false"
                :http-request="handleCoverUpload"
              >
                <div class="canvas-cover-preview" v-if="form.coverImage">
                  <img :src="form.coverImage" class="full-cover-image" />
                  <div class="cover-overlay-minimal">
                    <div class="overlay-action-btn" @click.stop="handlePreview({ mediaType: 1, mediaUrl: form.coverImage })">
                      <lucide-eye :size="16" />
                      <span>查看预览</span>
                    </div>
                    <div class="overlay-action-btn">
                      <lucide-edit-2 :size="14" />
                      <span>更换封面</span>
                    </div>
                  </div>
                </div>
                <div class="canvas-cover-placeholder" v-else :class="{ 'loading': coverProgress > 0 && coverProgress < 100 }">
                  <div class="placeholder-content">
                    <div v-if="coverProgress > 0 && coverProgress < 100" class="upload-overlay">
                      <el-progress type="circle" :percentage="coverProgress" :stroke-width="4" :width="80" color="#7c3aed" />
                      <div class="upload-status-hint">{{ coverStatus === 'calculating' ? '计算校验和...' : '上传中...' }}</div>
                      <el-button link class="pause-btn" @click.stop="pauseCoverUpload">
                        <lucide-circle-stop :size="20" />
                      </el-button>
                    </div>
                    <template v-else>
                      <div class="placeholder-icon-box">
                        <lucide-plus :size="24" />
                      </div>
                      <span class="p-tip">主视图初始化</span>
                    </template>
                  </div>
                </div>
              </el-upload>
            </div>
          </div>

          <div class="support-block config-block">
            <h4 class="support-title">资产定价</h4>
            <div class="price-strip-v3">
              <div class="price-header">
                <span class="price-label">资产净值定价</span>
                <span class="currency-label">CNY</span>
              </div>
              <div class="price-input-wrapper">
                <div class="currency-symbol">¥</div>
                <el-input-number 
                  v-model="form.price" 
                  :precision="2" 
                  :step="100" 
                  :min="0"
                  controls-position="right" 
                  class="premium-price-input" 
                />
              </div>
            </div>
          </div>

          <div class="support-block tags-block">
            <h4 class="support-title">资产分类标签</h4>
            <div class="seamless-tag-cloud-v2">
              <TransitionGroup name="tag-pop">
                <el-tag 
                  v-for="tag in form.tags" 
                  :key="tag" 
                  closable 
                  @close="removeTag(tag)"
                  class="flat-tag-v2"
                >
                  {{ tag }}
                </el-tag>
              </TransitionGroup>
              <div class="tag-input-trigger-v2">
                <el-input
                  v-if="inputVisible"
                  ref="InputRef"
                  v-model="inputValue"
                  size="small"
                  @keyup.enter="handleInputConfirm"
                  @blur="handleInputConfirm"
                  class="input-seamless-tag-v2"
                />
                <el-button v-else link @click="showInput" class="btn-tag-plus-v2">
                  <lucide-plus-circle :size="14" /> <span>追加分类标签</span>
                </el-button>
              </div>
            </div>
          </div>

          <div class="support-block status-block-v2">
            <div class="status-indicator-card" :class="{ 'is-online': form.status === 1 }">
              <div class="status-info-row">
                <div class="status-dot-v2"></div>
                <span class="status-text">{{ form.status === 1 ? '公开发布中' : '仓库封存中' }}</span>
              </div>
              <el-switch 
                v-model="form.status" 
                :active-value="1" 
                :inactive-value="0" 
                size="small"
                class="status-switch-v2"
              />
            </div>
          </div>

          <div class="support-block links-block">
            <h4 class="support-title">外部入口</h4>
            <div class="link-field-v2">
              <el-input v-model="form.demoUrl" placeholder="输入演示地址..." class="input-minimal-link">
                <template #prefix><lucide-link :size="12" /></template>
              </el-input>
            </div>
          </div>

          <div class="support-block attachment-block">
            <h4 class="support-title">资产附件</h4>
            <div class="attachment-upload-v2">
              <el-upload
                :show-file-list="false"
                :http-request="handleAttachmentUpload"
              >
                <div v-if="form.downloadUrl" class="attachment-info-card">
                  <div class="file-icon-box">
                    <lucide-file-archive v-if="isArchive(form.downloadUrl)" :size="16" />
                    <lucide-file v-else :size="16" />
                  </div>
                  <div class="file-details">
                    <span class="file-name">{{ getFileName(form.downloadUrl) }}</span>
                    <el-button link type="danger" @click.stop="handleDeleteAttachment" class="btn-nano-delete">
                      <lucide-trash-2 :size="12" />
                    </el-button>
                  </div>
                </div>
                <el-button v-else link class="btn-attachment-placeholder" :loading="(attachmentProgress > 0 && attachmentProgress < 100) || attachmentStatus === 'calculating'">
                  <lucide-paperclip :size="14" />
                  <span>
                    {{ attachmentStatus === 'calculating' ? '计算校验和...' : (attachmentProgress > 0 && attachmentProgress < 100 ? `上传中 ${attachmentProgress}%` : '上传资源附件 (Zip/PDF...)') }}
                  </span>
                  <el-button v-if="attachmentProgress > 0 && attachmentProgress < 100" link class="attachment-pause-btn" @click.stop="pauseAttachmentUpload">
                    <lucide-circle-stop :size="14" />
                  </el-button>
                </el-button>
              </el-upload>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <!-- Unified Media Preview Dialog -->
    <el-dialog v-model="previewVisible" width="60%" class="media-preview-window" destroy-on-close :show-close="false">
      <div class="preview-container">
        <img v-if="previewMediaType === 1" :src="previewUrl" class="full-media-preview" />
        <video v-else-if="previewMediaType === 2" :src="previewUrl" controls autoplay class="full-video"></video>
        
        <el-button circle class="close-preview-btn" @click="previewVisible = false">
          <lucide-x :size="20" />
        </el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type ElInput } from 'element-plus'
import request from '@/utils/request'
import { uploadLargeFile } from '@/utils/upload'
import { useAuthStore } from '@/stores/auth'
import { 
  LucideImage,
  LucideUpload,
  LucideUploadCloud,
  LucideCloudUpload,
  LucidePlus,
  LucidePlusCircle,
  LucidePlay,
  LucideLink,
  LucideTrash2,
  LucideSave,
  LucideX,
  LucideInbox,
  LucideMonitorPlay,
  LucideEdit2,
  LucideArrowLeft,
  LucideSparkles,
  LucideFileArchive,
  LucideFile,
  LucidePaperclip,
  LucideCircleStop
} from 'lucide-vue-next'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isEdit = ref(!!route.params.id && route.params.id !== 'new')
const loading = ref(false)
const submitting = ref(false)
const aiLoading = ref(false)
const categories = ref<any[]>([])
const formRef = ref()

// --- Unified Media Preview State ---
const previewVisible = ref(false)
const previewUrl = ref('')
const previewMediaType = ref(1) // 1: Image, 2: Video

const handlePreview = (media: any) => {
  if (!media.mediaUrl) return
  previewUrl.value = media.mediaUrl
  previewMediaType.value = media.mediaType
  previewVisible.value = true
}

// --- Form State ---
const form = reactive({
  id: route.params.id === 'new' ? undefined : Number(route.params.id),
  title: '',
  description: '',
  type: '',
  categoryId: null,
  techStack: '',
  contentHtml: '',
  coverImage: '',
  demoUrl: '',
  price: 0,
  status: 1,
  tags: [] as string[],
  medias: [] as any[],
  downloadUrl: ''
})

// --- Upload Progress & Status ---
const coverProgress = ref(0)
const coverStatus = ref<string>('')
const coverController = ref<AbortController | null>(null)

const attachmentProgress = ref(0)
const attachmentStatus = ref<string>('')
const attachmentController = ref<AbortController | null>(null)

const mediaProgress = ref<Record<number, number>>({})
const mediaStatus = ref<Record<number, string>>({})
const mediaControllers = ref<Record<number, AbortController | null>>({})

const isUploading = computed(() => {
  if (coverProgress.value > 0 && coverProgress.value < 100 && coverStatus.value === 'uploading') return true
  if (attachmentProgress.value > 0 && attachmentProgress.value < 100 && attachmentStatus.value === 'uploading') return true
  return Object.values(mediaProgress.value).some((p, i) => p > 0 && p < 100)
})

// --- Attachment Handlers ---
const handleAttachmentUpload = async (options: any) => {
  const { file } = options
  if (attachmentController.value) attachmentController.value.abort()
  attachmentController.value = new AbortController()
  
  try {
    const url = await uploadLargeFile(file, {
      onProgress: (p) => { attachmentProgress.value = p },
      onStatusChange: (s) => { attachmentStatus.value = s },
      signal: attachmentController.value.signal
    })
    form.downloadUrl = url
    attachmentProgress.value = 100
    ElMessage.success('附件上传成功')
  } catch (e: any) {
    if (e.message === 'Upload paused') {
      ElMessage.info('上传已暂停')
    } else {
      attachmentProgress.value = 0
      ElMessage.error('附件上传失败')
    }
  } finally {
    attachmentController.value = null
  }
}

const pauseAttachmentUpload = () => {
  if (attachmentController.value) {
    attachmentController.value.abort()
  }
}

const handleDeleteAttachment = async () => {
  if (!form.downloadUrl) return
  try {
    await ElMessageBox.confirm('确认删除当前附件并同步清理云端文件？', '警告', { type: 'warning' })
    await request.delete('/api/file/delete', { params: { url: form.downloadUrl } })
    form.downloadUrl = ''
    attachmentProgress.value = 0
    ElMessage.success('附件已移除')
  } catch (e) {
    if (e === 'cancel') return
    ElMessage.error('清理失败')
  }
}

const getFileName = (url: string) => {
  if (!url) return ''
  return url.substring(url.lastIndexOf('/') + 1)
}

const isArchive = (url: string) => {
  if (!url) return false
  const ext = url.substring(url.lastIndexOf('.')).toLowerCase()
  return ['.zip', '.rar', '.7z', '.gz', '.tar'].includes(ext)
}

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
    // 转换标签
    if (typeof res.data.tags === 'string' && res.data.tags) {
      form.tags = res.data.tags.split(',')
    } else {
      form.tags = []
    }
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
    // 准备提交数据，将 tags 数组转为逗号分隔字符串
    const payload = {
      ...form,
      tags: form.tags.join(',')
    }
    
    if (isEdit.value) {
      await request.put(`/api/projects/${form.id}`, payload)
    } else {
      await request.post('/api/projects', payload)
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
  delete mediaProgress.value[index]
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

const handleCoverUpload = async (options: any) => {
  const { file } = options
  if (coverController.value) coverController.value.abort()
  coverController.value = new AbortController()

  try {
    const url = await uploadLargeFile(file, {
      onProgress: (p) => { coverProgress.value = p },
      onStatusChange: (s) => { coverStatus.value = s },
      signal: coverController.value.signal
    })
    form.coverImage = url
    coverProgress.value = 100
    ElMessage.success('封面上传成功')
  } catch (e: any) {
    if (e.message === 'Upload paused') {
      ElMessage.info('封面上传已暂停')
    } else {
      coverProgress.value = 0
      ElMessage.error('封面上传失败')
    }
  } finally {
    coverController.value = null
  }
}

const pauseCoverUpload = () => {
  if (coverController.value) {
    coverController.value.abort()
  }
}

const handleMediaUpload = async (options: any, index: number) => {
  const { file } = options
  if (mediaControllers.value[index]) mediaControllers.value[index]?.abort()
  const controller = new AbortController()
  mediaControllers.value[index] = controller
  
  try {
    const url = await uploadLargeFile(file, {
      onProgress: (p) => {
        mediaProgress.value = { ...mediaProgress.value, [index]: p }
      },
      onStatusChange: (s) => {
        mediaStatus.value = { ...mediaStatus.value, [index]: s }
      },
      signal: controller.signal
    })
    form.medias[index].mediaUrl = url
    mediaProgress.value = { ...mediaProgress.value, [index]: 100 }
    ElMessage.success('素材上传成功')
  } catch (e: any) {
    if (e.message === 'Upload paused') {
      ElMessage.info('上传暂停')
    } else {
      const nextProgress = { ...mediaProgress.value }
      delete nextProgress[index]
      mediaProgress.value = nextProgress
      ElMessage.error('素材上传失败')
    }
  } finally {
    mediaControllers.value[index] = null
  }
}

const pauseMediaUpload = (index: number) => {
  if (mediaControllers.value[index]) {
    mediaControllers.value[index]?.abort()
  }
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

// --- PRD Import Logic ---
const handlePrdImport = (file: any) => {
  const rawFile = file.raw
  if (!rawFile) return
  
  if (!rawFile.name.endsWith('.md')) {
    return ElMessage.warning('请上传 Markdown 格式的文档 (.md)')
  }

  const reader = new FileReader()
  reader.onload = (e) => {
    const content = e.target?.result as string
    if (!content) return

    form.contentHtml = content
    ElMessage.success('需求文档已导入内容区域信号')

    // Extract summary using AI if possible, otherwise fallback
    handleAiSummary()
  }
  reader.readAsText(rawFile)
}

const handleAiSummary = async () => {
  if (!form.contentHtml || form.contentHtml.length < 20) {
    return ElMessage.warning('请先输入或导入足够的深度解析内容（至少20字）以供 AI 分析')
  }

  aiLoading.value = true
  try {
    const res: any = await request.post('/api/ai/generate-summary', {
      content: form.contentHtml
    })
    
    if (res.code === 200 || res.data) {
      if (form.description) {
        await ElMessageBox.confirm('AI 已生成新的摘要建议，是否覆盖当前内容？', '智能助手', {
          confirmButtonText: '覆盖',
          cancelButtonText: '保留',
          type: 'info'
        })
      }
      form.description = res.data
      ElMessage.success('摘要已通过 AI 智能提炼')
    }
  } catch (err: any) {
    console.error('AI Summary failed:', err)
    // 如果后台未配置 API KEY，尝试使用本地提取
    const localSummary = extractSummary(form.contentHtml)
    if (localSummary && !form.description) {
      form.description = localSummary
      ElMessage.info('已通过本地算法完成基础摘要提取（AI 调用失败）')
    }
  } finally {
    aiLoading.value = false
  }
}

const extractSummary = (content: string) => {
  // 1. Remove YAML frontmatter if exists
  let text = content.replace(/^---[\s\S]*?---/, '')
  // 2. Remove markdown headers
  text = text.replace(/^#+.*$/gm, '')
  // 3. Remove images and links [text](url) -> text
  text = text.replace(/!\[.*?\]\(.*?\)/g, '')
  text = text.replace(/\[(.*?)\]\(.*?\)/g, '$1')
  // 4. Remove HTML tags
  text = text.replace(/<[^>]*>/g, '')
  // 5. Remove bold/italic markers
  text = text.replace(/[*_]{1,3}/g, '')
  // 6. Remove blockquotes
  text = text.replace(/^\s*>\s*/gm, '')
  // 7. Remove code blocks
  text = text.replace(/```[\s\S]*?```/g, '')
  text = text.replace(/`.*?`/g, '')
  
  // Get first non-empty paragraph
  const paragraphs = text.split('\n')
    .map(p => p.trim())
    .filter(p => p.length > 20) // Require a decent length paragraph
  
  if (paragraphs.length > 0) {
    const firstParagraph = paragraphs[0]
    if (!firstParagraph) return ''
    let summaryText = firstParagraph
    if (firstParagraph.length > 120) {
      summaryText = firstParagraph.substring(0, 117) + '...'
    }
    return summaryText
  }
  return ''
}

onMounted(() => {
  loadCategories()
  loadProject()
})
</script>

<style scoped>
.header-extra {
  display: flex;
  align-items: center;
  gap: 16px;
}

.description-header-lite {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.btn-ai-magic {
  align-self: flex-start;
  font-size: 12px;
  font-weight: 600;
  color: #7c3aed; /* 浅紫色代表 AI */
  background: rgba(124, 58, 237, 0.05);
  padding: 4px 10px;
  border-radius: 4px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.btn-ai-magic:hover {
  background: rgba(124, 58, 237, 0.1);
  border-color: rgba(124, 58, 237, 0.2);
  transform: translateY(-1px);
}

.btn-ai-magic :deep(.lucide) {
  margin-right: 4px;
}

.btn-import-prd {
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--admin-primary);
  background: rgba(var(--admin-primary-rgb), 0.05);
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.3s;
}

.btn-import-prd:hover {
  background: rgba(var(--admin-primary-rgb), 0.1);
  transform: translateY(-1px);
}

.prd-import-uploader :deep(.el-upload) {
  display: flex;
  align-items: center;
}

.project-detail-wrapper {
  animation: canvas-fade 0.8s cubic-bezier(0.16, 1, 0.3, 1);
  padding: 0 40px 100px;
  max-width: 1400px;
  margin: 0 auto;
  color: var(--admin-text-main);
}

/* --- Header & Navigation --- */
.view-header {
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
  gap: 16px;
}

.back-btn {
  color: var(--admin-text-muted);
  font-family: var(--font-data);
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: all 0.3s;
  padding: 0;
}

.back-btn:hover {
  color: var(--admin-primary);
  transform: translateX(-4px);
}

.header-breadcrumb-lite {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--admin-text-muted);
  font-size: 13px;
  font-weight: 500;
}

.crumb-separator {
  opacity: 0.3;
  font-family: var(--font-data);
}

.crumb-current {
  color: var(--admin-text-secondary);
}

.header-ops {
  display: flex;
  gap: 16px;
  align-items: center;
}

.btn-minimal-outline {
  background: transparent !important;
  border: 1px solid var(--admin-border) !important;
  color: var(--admin-text-secondary) !important;
  border-radius: 8px;
  font-weight: 600;
}

.handle-save-btn {
  border-radius: 8px;
  padding: 10px 24px;
}

/* --- Seamless Canvas Layout --- */
.seamless-canvas {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 80px;
  position: relative;
  margin-top: 40px;
}

.canvas-main-flow {
  display: flex;
  flex-direction: column;
}

.canvas-section {
  position: relative;
  padding-bottom: 80px;
}

.section-indicator {
  position: absolute;
  left: -80px;
  top: 10px;
  font-family: var(--font-data);
  font-size: 10px;
  font-weight: 800;
  color: var(--admin-primary);
  opacity: 0.3;
  white-space: nowrap;
  transform: rotate(-90deg) translateX(-100%);
  transform-origin: top left;
  letter-spacing: 1px;
}

.canvas-divider {
  height: 1px;
  background: linear-gradient(90deg, var(--admin-border) 0%, transparent 100%);
  margin-bottom: 80px;
}

/* --- Identity Section --- */
.hero-section {
  padding-top: 0;
}

.no-label-item :deep(.el-form-item__label) {
  display: none;
}

.headline-input :deep(.el-input__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  padding: 0;
}

.headline-input-v3 {
  width: 100%;
}

.headline-input-v3 :deep(.el-textarea__inner) {
  font-size: 44px;
  font-weight: 800;
  letter-spacing: -1.5px;
  line-height: 1.3;
  color: var(--admin-text-main);
  padding: 12px 0;
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
  border-radius: 0;
  resize: none;
  min-height: auto !important;
}

.headline-input-v3 :deep(.el-textarea__inner::placeholder) {
  color: var(--admin-border);
}

.description-input-v3 :deep(.el-textarea__inner) {
  font-size: 16px;
  font-weight: 500;
  line-height: 1.6;
  color: var(--admin-text-secondary);
  padding: 0 0 12px 0;
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
  border-radius: 0;
  resize: none;
  border-bottom: 1px solid var(--admin-border) !important;
  transition: all 0.3s;
}

.description-input-v3 :deep(.el-textarea__inner:focus) {
  border-color: var(--admin-primary) !important;
}

.description-input-v3 :deep(.el-textarea__inner::placeholder) {
  color: var(--admin-border);
  font-weight: 400;
}

.meta-row-seamless {
  display: flex;
  gap: 48px;
  margin-top: 32px;
  align-items: flex-end;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meta-item:first-child {
  min-width: 140px; /* Ensure category has enough width */
}

.meta-item label {
  font-family: var(--font-data);
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  color: var(--admin-text-muted);
  letter-spacing: 0.5px;
}

.meta-item.flex-auto {
  flex: 1;
}

.seamless-select :deep(.el-select__wrapper),
.seamless-input :deep(.el-input__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  border-bottom: 1px solid var(--admin-border) !important;
  border-radius: 0;
  padding: 4px 0;
  transition: all 0.3s;
}

.seamless-select :deep(.el-select__wrapper):hover,
.seamless-input :deep(.el-input__wrapper):hover {
  border-color: var(--admin-primary) !important;
}

.tech-font :deep(.el-input__inner) {
  font-family: var(--font-data);
  color: var(--admin-primary);
  font-size: 13px;
  font-weight: 600;
}

.tech-icon-box {
  color: var(--admin-primary);
  opacity: 0.6;
}

/* --- Documentation Section --- */
.section-header-seamless {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

.section-heading-text {
  font-size: 22px;
  font-weight: 800;
  margin: 0 0 6px 0;
  letter-spacing: -0.5px;
}

.section-muted-text {
  font-size: 13px;
  color: var(--admin-text-muted);
  margin: 0;
}

.word-count-chip {
  font-family: var(--font-data);
  font-size: 9px;
  font-weight: 800;
  background: var(--admin-surface-light);
  color: var(--admin-text-muted);
  padding: 4px 10px;
  border-radius: 20px;
  letter-spacing: 0.5px;
}

.seamless-editor-wrapper {
  background: var(--admin-surface);
  border-radius: 20px;
  padding: 1px;
  border: 1px solid var(--admin-border);
  box-shadow: 0 4px 20px rgba(0,0,0,0.02);
  overflow: hidden;
}

.distilled-editor {
  height: 520px;
  border: none !important;
  --md-bk-color: transparent;
}

/* --- Assets Flow --- */
.btn-seamless-add {
  font-weight: 700;
  font-family: var(--font-data);
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.asset-flow-container {
  margin-top: 32px;
}

.asset-grid-seamless {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.asset-strip-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px 16px;
  border-radius: 14px;
  background: var(--admin-surface-light);
  border: 1px solid var(--admin-border);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

/* --- Upload Overlay & Pause Stats --- */
.upload-overlay, .asset-upload-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 10;
  gap: 12px;
}

.upload-status-hint {
  font-size: 12px;
  color: var(--admin-text-secondary);
  font-weight: 600;
}

.pause-btn, .mini-pause-btn {
  color: #ef4444 !important;
  opacity: 0.8;
  transition: all 0.2s;
}

.pause-btn:hover, .mini-pause-btn:hover {
  opacity: 1;
  transform: scale(1.1);
}

.mini-status-hint {
  font-size: 10px;
  color: var(--admin-text-muted);
}

.attachment-pause-btn {
  margin-left: 8px;
  padding: 0;
  color: #ef4444 !important;
}

.asset-strip-item:hover {
  background: var(--admin-surface);
  border-color: var(--admin-primary-glow);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px var(--admin-primary-glow);
}

.asset-type-indicator {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--admin-bg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.indicator-inner {
  color: var(--admin-text-muted);
}

.is-video .indicator-inner {
  color: var(--admin-warning);
}

.asset-config-row {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
}

.type-mini-selector {
  width: 76px;
}

.type-mini-selector :deep(.el-select__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  font-family: var(--font-data);
  font-weight: 700;
  font-size: 11px;
}

.asset-url-field {
  flex: 1;
  position: relative;
}

.input-minimal :deep(.el-input__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  font-family: var(--font-data);
  font-size: 12px;
  border-bottom: 1px solid var(--admin-border);
  border-radius: 0;
  padding: 0;
}

.upload-trigger-btn {
  color: var(--admin-primary);
  opacity: 0.7;
  transition: 0.3s;
}

.upload-trigger-btn:hover {
  opacity: 1;
}

.progress-bar-nano {
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px; /* Slightly thicker for visibility */
  background: var(--admin-border);
  z-index: 10;
}

.progress-bar-nano .fill {
  height: 100%;
  background: var(--admin-primary);
  box-shadow: 0 0 8px var(--admin-primary-glow);
  transition: width 0.3s ease-out;
}

.asset-preview-box {
  width: 72px;
  height: 40px;
  border-radius: 6px;
  overflow: hidden;
  background: var(--admin-bg);
  border: 1px solid var(--admin-border);
  cursor: pointer; /* Change cursor for preview */
}

.asset-preview-box.has-url {
  border-color: var(--admin-primary-glow);
}

.thumb-img {
  width: 100%;
  height: 100%;
  transition: 0.3s;
}

.thumb-img:hover {
  transform: scale(1.1);
}

.video-preview-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--admin-surface-light);
  color: var(--admin-primary);
  gap: 2px;
}

.preview-label {
  font-family: var(--font-data);
  font-size: 8px;
  font-weight: 800;
}

.empty-preview {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--admin-border);
}

.btn-item-delete {
  border: none !important;
  background: transparent !important;
  color: var(--admin-text-muted);
  opacity: 0.4;
  transition: all 0.3s;
}

.btn-item-delete:hover {
  opacity: 1;
  color: var(--admin-error);
  background: rgba(var(--el-color-danger-rgb), 0.1) !important;
}

/* --- Empty State --- */
.canvas-empty-state {
  padding: 60px 40px;
  border-radius: 20px;
  border: 1px dashed var(--admin-border);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 20px;
}

.empty-icon-ring {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--admin-surface-light);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--admin-border);
}

.empty-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.primary-msg {
  font-size: 15px;
  font-weight: 700;
  color: var(--admin-text-secondary);
}

.secondary-msg {
  font-size: 12px;
  color: var(--admin-text-muted);
}

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

.seamless-cover-uploader :deep(.el-upload) {
  display: block;
  width: 100%;
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

/* Cover Uploader */
.canvas-cover-preview {
  width: 100%;
  aspect-ratio: 1.5;
  border-radius: 16px;
  position: relative;
  overflow: hidden;
  border: 1px solid var(--admin-border);
  transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
  background: var(--admin-surface-light);
}

.full-cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.canvas-cover-preview:hover {
  transform: scale(1.02);
  border-color: var(--admin-primary);
}

.cover-overlay-minimal {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: 0.3s;
  backdrop-filter: blur(4px);
}

.canvas-cover-preview:hover .cover-overlay-minimal {
  opacity: 1;
}

.overlay-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #fff;
}

.overlay-action-btn span {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.canvas-cover-placeholder {
  width: 100%;
  aspect-ratio: 1.5;
  border-radius: 16px;
  border: 1px dashed var(--admin-border);
  background: var(--admin-surface-light);
  display: flex; /* Centering fix */
  align-items: center;
  justify-content: center;
  color: var(--admin-text-muted);
  transition: 0.3s;
  overflow: hidden;
}

.placeholder-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.canvas-cover-placeholder:hover {
  background: var(--admin-surface);
  border-color: var(--admin-primary);
}

.placeholder-icon-box {
  color: var(--admin-border);
}

.p-text {
  font-family: var(--font-data);
  font-weight: 800;
  font-size: 20px;
  color: var(--admin-primary);
}

.p-tip {
  font-family: var(--font-data);
  font-weight: 700;
  font-size: 10px;
  letter-spacing: 1px;
}

.thumb-img-static {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.loading-ring {
  width: 24px;
  height: 24px;
  border: 2px solid var(--admin-primary-glow);
  border-top-color: var(--admin-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Economics V3 */
.price-strip-v3 {
  background: var(--admin-bg);
  padding: 20px;
  border-radius: 18px;
  border: 1px solid var(--admin-border);
  transition: all 0.3s;
}

.price-strip-v3:hover {
  border-color: var(--admin-primary-glow);
  box-shadow: 0 8px 30px rgba(0,0,0,0.04);
}

.price-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.price-label {
  font-size: 12px;
  font-weight: 700;
  color: var(--admin-text-secondary);
}

.currency-label {
  font-family: var(--font-data);
  font-size: 10px;
  font-weight: 800;
  color: var(--admin-primary);
  opacity: 0.8;
  background: var(--admin-primary-glow);
  padding: 2px 8px;
  border-radius: 4px;
}

.price-input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--admin-surface);
  padding: 8px 12px;
  border-radius: 12px;
  border: 1px solid var(--admin-border);
}

.currency-symbol {
  font-size: 20px;
  font-weight: 800;
  color: var(--admin-primary);
}

.premium-price-input {
  flex: 1;
}

.premium-price-input :deep(.el-input__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  padding-left: 0;
}

.premium-price-input :deep(.el-input__inner) {
  font-family: var(--font-data);
  font-size: 18px;
  font-weight: 700;
  color: var(--admin-text-main);
  text-align: right;
}

/* Unified Media Preview Dialog */
.media-preview-window :deep(.el-dialog__header) {
  display: none;
}

.media-preview-window :deep(.el-dialog__body) {
  padding: 0;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
}

.preview-container {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  background: #000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.full-media-preview {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.full-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.close-preview-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.1) !important;
  border: none !important;
  color: #fff !important;
  backdrop-filter: blur(10px);
  z-index: 100;
  transition: all 0.3s;
}

.close-preview-btn:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  transform: scale(1.1);
}

/* Tags V2 */
.seamless-tag-cloud-v2 {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.flat-tag-v2 {
  border-radius: 6px;
  border: 1px solid var(--admin-border) !important;
  background: var(--admin-surface-light) !important;
  color: var(--admin-text-secondary) !important;
  font-weight: 600;
  font-size: 11px;
}

.tag-input-trigger-v2 {
  display: flex;
}

.btn-tag-plus-v2 {
  font-size: 10px;
  font-weight: 700;
  color: var(--admin-primary);
  text-transform: uppercase;
}

/* Status Card V2 */
.status-indicator-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-radius: 14px;
  background: var(--admin-surface-light);
  border: 1px solid var(--admin-border);
  transition: all 0.4s;
}

.status-indicator-card.is-online {
  border-color: var(--admin-success);
  background: rgba(16, 185, 129, 0.02);
}

.status-info-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-dot-v2 {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--admin-text-muted);
  box-shadow: 0 0 0 3px rgba(100, 116, 139, 0.1);
}

.is-online .status-dot-v2 {
  background: var(--admin-success);
  box-shadow: 0 0 10px var(--admin-success), 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.status-text {
  font-size: 12px;
  font-weight: 700;
  color: var(--admin-text-secondary);
}

/* Links V2 */
.link-field-v2 {
  background: var(--admin-surface-light);
  padding: 4px 12px;
  border-radius: 10px;
  border-bottom: 1px solid var(--admin-border);
}

.input-minimal-link :deep(.el-input__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  padding: 0;
}

/* Animations */
@keyframes canvas-fade {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.stagger-list-enter-active { transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.stagger-list-enter-from { opacity: 0; transform: translateX(-20px); }

.tag-pop-enter-active { transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.tag-pop-enter-from { opacity: 0; transform: scale(0.5); }

@media (max-width: 1200px) {
  .seamless-canvas { grid-template-columns: 1fr; gap: 60px; }
  .canvas-support-stream { order: -1; }
  .section-indicator { display: none; }
  .project-detail-wrapper { padding: 0 24px 60px; }
}
</style>
