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
                <el-input v-model="form.title" type="textarea" :autosize="{ minRows: 1, maxRows: 3 }"
                  placeholder="请输入资产标题..." class="headline-input-v3" />
              </el-form-item>

              <el-form-item prop="description" class="no-label-item description-item">
                <div class="description-header-lite">
                  <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入资产简要描述（显示在列表页卡片中）..."
                    class="description-input-v3" />
                  <el-button link type="primary" @click="handleAiSummary" :loading="aiLoading" class="btn-ai-magic">
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
              <el-upload :auto-upload="false" :show-file-list="false" accept=".md" @change="handlePrdImport"
                class="prd-import-uploader">
                <el-button link type="primary" class="btn-import-prd">
                  <lucide-file-text :size="14" /> <span>导入需求文档 (Markdown)</span>
                </el-button>
              </el-upload>
              <span class="word-count-chip">2026.04 修订版</span>
            </div>
          </div>
          <div class="seamless-editor-wrapper">
            <md-editor v-model="form.contentHtml" placeholder="Start drafting..." class="distilled-editor"
              language="zh-CN" :toolbars="[
                'bold', 'italic', 'strikeThrough', '-', 'title', 'sub', 'sup', 'quote', 'unorderedList', 'orderedList', 'task', '-', 'codeRow', 'code', 'link', 'image', 'table', 'mermaid', 'katex', '-', 'revoke', 'next', 'save', '=', 'pageFullscreen', 'fullscreen', 'preview', 'htmlPreview'
              ]" />
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
                        <el-upload :show-file-list="false"
                          :http-request="(options: any) => handleMediaUpload(options, media, index)">
                          <el-button link :disabled="isMediaUploadBusy(media)" class="upload-trigger-btn">
                            <lucide-cloud-upload :size="14" />
                          </el-button>
                        </el-upload>
                      </template>
                    </el-input>

                    <!-- Slim integrated progress for media items -->
                    <div v-if="shouldShowMediaOverlay(media)" class="asset-progress-wrap">
                      <div class="asset-progress-track">
                        <div class="asset-progress-bar" :style="{ width: getMediaProgress(media) + '%' }"></div>
                      </div>
                      <div class="asset-progress-meta">
                        <span class="p-status">
                          {{
                            getMediaStatus(media) === 'calculating' ? '校验中' :
                              (getMediaStatus(media) === 'merging' ? '合并中' :
                                (getMediaStatus(media) === 'paused' ? '暂停' : '正在上传'))
                          }}
                        </span>
                        <span class="p-num">{{ getMediaProgress(media) }}%</span>
                        <el-button v-if="getMediaStatus(media) === 'paused'" link class="p-btn resume"
                          @click.stop="triggerMediaReSelect(media, index)">
                          <lucide-play :size="12" />
                        </el-button>
                        <el-button v-else link class="p-btn pause" @click.stop="pauseMediaUpload(media)">
                          <lucide-pause :size="12" />
                        </el-button>
                      </div>
                    </div>

                    <div v-else-if="getMediaResult(media)" class="upload-result-badge fade-in">
                      <lucide-check-circle :size="10" />
                      <span>{{ formatFileSize(getMediaResult(media)!.size) }}</span>
                      <span class="sep">/</span>
                      <span>{{ formatDuration(getMediaResult(media)!.duration) }}</span>
                    </div>
                  </div>
                </div>

                <div class="asset-preview-box" :class="{ 'has-url': !!media.mediaUrl }" @click="handlePreview(media)">
                  <template v-if="media.mediaUrl">
                    <img v-if="media.mediaType === 1" :src="media.mediaUrl" class="thumb-img-static" />
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
            <input type="file" ref="relinkInput" style="display: none" @change="handleRelinkFile">
          </div>
        </section>

        <div class="canvas-divider"></div>

        <!-- Section: Service Features -->
        <section class="canvas-section feature-section">
          <div class="section-indicator">04 . 服务特色</div>
          <div class="section-header-seamless flex-between">
            <div class="title-group">
              <h3 class="section-heading-text">服务特色配置</h3>
              <p class="section-muted-text">配置展示在用户端详情页总览中的特色卡片</p>
            </div>
            <el-button link type="primary" @click="addServiceFeature" class="btn-seamless-add">
              <lucide-plus-circle :size="16" /> <span>追加新特色</span>
            </el-button>
          </div>

          <TransitionGroup name="feat-stagger" tag="div" class="feature-grid-premium">
            <div v-for="(feat, index) in form.serviceFeatures" :key="index" class="feature-glass-card">
              <div class="glass-card-inner">
                <div class="glass-card-header">
                  <div class="feat-status-badge">
                    <span class="feat-ord">FEATURE {{ index + 1 }}</span>
                    <div class="feat-dot"></div>
                  </div>
                  <div class="feat-ops-inline">
                    <el-button circle class="btn-glass-delete" @click="removeServiceFeature(index)">
                      <lucide-trash-2 :size="14" />
                    </el-button>
                  </div>
                </div>

                <div class="glass-card-body">
                  <div class="feat-visual-row">
                    <div class="feat-icon-ring">
                      <component :is="serviceFeatureIcons.find(i => i.value === feat.icon)?.icon || LucidePuzzle"
                        :size="20" class="feat-icon-glyph" />
                      <div class="icon-ring-glow"></div>
                    </div>
                    <el-select v-model="feat.icon" placeholder="ICO" class="glass-icon-selector">
                      <el-option v-for="icon in serviceFeatureIcons" :key="icon.value" :label="icon.name"
                        :value="icon.value">
                        <div class="icon-option-v2">
                          <component :is="icon.icon" :size="14" />
                          <span>{{ icon.name }}</span>
                        </div>
                      </el-option>
                    </el-select>
                  </div>

                  <div class="feat-form-content">
                    <el-input v-model="feat.title" placeholder="输入特色核心标题..." class="glass-input-title" />
                    <el-input v-model="feat.desc" type="textarea" :rows="2" placeholder="描绘该特色的深度价值与执行细节..."
                      class="glass-input-desc" />
                  </div>
                </div>
              </div>
              <div class="glass-card-border"></div>
            </div>
          </TransitionGroup>

          <div v-if="form.serviceFeatures.length === 0" class="canvas-empty-state">
            <div class="empty-icon-ring">
              <lucide-puzzle :size="24" />
            </div>
            <div class="empty-text">
              <span class="primary-msg">尚未配置服务特色</span>
              <span class="secondary-msg">点击上方追加特色卡片</span>
            </div>
          </div>
        </section>

        <div class="canvas-divider"></div>

        <!-- Section: Resource Contents -->
        <section class="canvas-section resource-section">
          <div class="section-indicator">05 . 资源清单</div>
          <div class="section-header-seamless flex-between">
            <div class="title-group">
              <h3 class="section-heading-text">下载资源目录结构</h3>
              <p class="section-muted-text">配置展示在用户端详情页中的下载包内目录结构</p>
            </div>
            <el-button link type="primary" @click="addRootResourceNode" class="btn-seamless-add">
              <lucide-folder-plus :size="16" /> <span>追加顶级类目</span>
            </el-button>
          </div>

          <div class="resource-tree-wrapper">
            <el-tree :data="resourceTree" draggable node-key="id" default-expand-all :expand-on-click-node="false"
              class="premium-resource-tree">
              <template #default="{ node, data }">
                <div class="custom-tree-node">
                  <div class="node-main">
                    <lucide-folder v-if="data.children && data.children.length > 0" :size="14"
                      class="node-icon folder" />
                    <lucide-file-text v-else :size="14" class="node-icon file" />
                    <el-input v-model="data.label" placeholder="节点名称" class="node-label-input" size="small" />
                  </div>
                  <div class="node-ops">
                    <el-button link type="primary" @click="appendResourceNode(data)" class="node-btn">
                      <lucide-plus :size="12" />
                    </el-button>
                    <el-button link type="danger" @click="removeResourceNode(node, data)" class="node-btn delete">
                      <lucide-trash-2 :size="12" />
                    </el-button>
                  </div>
                </div>
              </template>
            </el-tree>

            <div v-if="resourceTree.length === 0" class="canvas-empty-state">
              <div class="empty-icon-ring">
                <lucide-layers :size="24" />
              </div>
              <div class="empty-text">
                <span class="primary-msg">尚未梳理资源目录</span>
                <span class="secondary-msg">点击上方追加项目模块或文件项</span>
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
              <el-upload class="uploader-canvas" :show-file-list="false" :http-request="handleCoverUpload">
                <div class="canvas-cover-preview" v-if="form.coverImage">
                  <img :src="form.coverImage" class="full-cover-image" />
                  <div class="cover-overlay-minimal">
                    <div class="overlay-action-btn"
                      @click.stop="handlePreview({ mediaType: 1, mediaUrl: form.coverImage })">
                      <lucide-eye :size="16" />
                      <span>查看预览</span>
                    </div>
                    <div class="overlay-action-btn">
                      <lucide-edit-2 :size="14" />
                      <span>更换封面</span>
                    </div>
                  </div>
                </div>
                <div class="canvas-cover-placeholder" v-else
                  :class="{ 'loading': coverProgress > 0 && coverProgress < 100 }">
                  <div class="placeholder-content">
                    <div class="placeholder-icon-box">
                      <lucide-plus :size="24" />
                    </div>
                    <span class="p-tip">主视图初始化</span>
                  </div>
                </div>

                <!-- Slim Progress Overlay for Cover -->
                <div v-if="shouldShowUploadOverlay(coverProgress, coverStatus)" class="asset-progress-wrap">
                  <div class="asset-progress-track">
                    <div class="asset-progress-bar" :style="{ width: coverProgress + '%' }"></div>
                  </div>
                  <div class="asset-progress-meta">
                    <span class="p-status">{{ coverStatus === 'calculating' ? '校验中' : (coverStatus === 'merging' ? '合并中'
                      :
                      (coverStatus === 'paused' ? '暂停' : '上传中')) }}</span>
                    <span class="p-num">{{ coverProgress }}%</span>
                    <el-button v-if="coverStatus === 'paused'" link class="p-btn resume"
                      @click.stop="triggerCoverReSelect">
                      <lucide-play :size="12" />
                    </el-button>
                    <el-button v-else link class="p-btn pause" @click.stop="pauseCoverUpload">
                      <lucide-pause :size="12" />
                    </el-button>
                  </div>
                </div>
                <input type="file" ref="coverRelinkInput" style="display: none" accept="image/*"
                  @change="(e) => handleRelinkGeneral(e, 'cover')">

                <div v-if="coverResult" class="cover-result-tag fade-in">
                  <lucide-zap :size="10" />
                  <span>{{ formatFileSize(coverResult.size) }} | {{ formatDuration(coverResult.duration) }}</span>
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
                <el-input-number v-model="form.price" :precision="2" :step="100" :min="0" controls-position="right"
                  class="premium-price-input" />
              </div>
            </div>
          </div>

          <div class="support-block tags-block">
            <h4 class="support-title">资产分类标签</h4>
            <div class="seamless-tag-cloud-v2">
              <TransitionGroup name="tag-pop">
                <el-tag v-for="tag in form.tags" :key="tag" closable @close="removeTag(tag)" class="flat-tag-v2">
                  {{ tag }}
                </el-tag>
              </TransitionGroup>
              <div class="tag-input-trigger-v2">
                <el-input v-if="inputVisible" ref="InputRef" v-model="inputValue" size="small"
                  @keyup.enter="handleInputConfirm" @blur="handleInputConfirm" class="input-seamless-tag-v2" />
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
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" size="small"
                class="status-switch-v2" />
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
              <el-upload :show-file-list="false" :http-request="handleAttachmentUpload">
                <div v-if="form.downloadUrl" class="attachment-info-card">
                  <div class="file-icon-box">
                    <lucide-file-archive v-if="isArchive(form.downloadUrl)" :size="16" />
                    <lucide-file v-else :size="16" />
                  </div>
                  <div class="file-details">
                    <span class="file-name">{{ getFileName(form.downloadUrl) }}</span>
                    <div class="file-meta-row" v-if="attachmentResult">
                      <span class="size">{{ formatFileSize(attachmentResult.size) }}</span>
                      <span class="dot">·</span>
                      <span class="time">{{ formatDuration(attachmentResult.duration) }}</span>
                    </div>
                    <el-button link type="danger" @click.stop="handleDeleteAttachment" class="btn-nano-delete">
                      <lucide-trash-2 :size="12" />
                    </el-button>
                  </div>

                  <!-- Slim Progress Overlay for Attachment -->
                  <div v-if="shouldShowUploadOverlay(attachmentProgress, attachmentStatus)"
                    class="asset-progress-wrap attachment-fix">
                    <div class="asset-progress-track">
                      <div class="asset-progress-bar" :style="{ width: attachmentProgress + '%' }"></div>
                    </div>
                    <div class="asset-progress-meta">
                      <span class="p-status">{{ attachmentStatus === 'calculating' ? '校验中' : (attachmentStatus ===
                        'merging' ?
                        '合并中' : (attachmentStatus === 'paused' ? '暂停' : '正在上传')) }}</span>
                      <span class="p-num">{{ attachmentProgress }}%</span>
                      <el-button v-if="attachmentStatus === 'paused'" link class="p-btn resume"
                        @click.stop="triggerAttachmentReSelect">
                        <lucide-play :size="12" />
                      </el-button>
                      <el-button v-else link class="p-btn pause" @click.stop="pauseAttachmentUpload">
                        <lucide-pause :size="12" />
                      </el-button>
                    </div>
                  </div>
                </div>
                <el-button v-else link class="btn-attachment-placeholder">
                  <lucide-paperclip :size="14" />
                  <span class="attachment-text-status">
                    {{ attachmentStatus === 'calculating' ? '计算校验和...' : (attachmentStatus === 'merging' ? '正在合并文件...' :
                      (attachmentProgress > 0 && attachmentProgress < 100 ? `正在上传 ${attachmentProgress}%`
                        : '上传资源附件 (Zip/PDF...)')) }} </span>
                      <div v-if="shouldShowUploadOverlay(attachmentProgress, attachmentStatus)"
                        class="attachment-ops-mini">
                        <el-button v-if="attachmentStatus === 'paused'" link class="attachment-resume-btn"
                          @click.stop="handleAttachmentUpload({ file: null })">
                          <lucide-play :size="14" />
                        </el-button>
                        <el-button v-else link class="attachment-pause-btn" @click.stop="pauseAttachmentUpload">
                          <lucide-circle-stop :size="14" />
                        </el-button>
                      </div>
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
import { uploadLargeFile, type UploadResult } from '@/utils/upload'
import { useAuthStore } from '@/stores/auth'
import {
  LucideCheckCircle,
  LucideZap,
  LucidePause,
  LucideMonitorDot,
  LucideCode2,
  LucideBookOpen,
  LucideCpu,
  LucideDatabase,
  LucidePuzzle,
  LucideShieldCheck,
  LucideMessageCircleQuestion,
  LucideCheckCircle2,
  LucideSmartphone,
  LucideVideo,
  LucideLayout,
  LucidePalette,
  LucideSettings,
  LucideSearch,
  LucideTrash2,
  LucidePlusCircle
} from 'lucide-vue-next'
import { MdEditor } from 'md-editor-v3'
import { h } from 'vue'
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
const relinkInput = ref<HTMLInputElement | null>(null)
const activeRelinkIndex = ref<number | null>(null)

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
  downloadUrl: '',
  resourceContents: '',
  serviceFeatures: [
    { icon: 'MonitorDot', title: '1-on-1 真人演示', desc: '购买后可预约远程演示，讲解项目架构与核心链路。' },
    { icon: 'Code2', title: '全套环境搭建', desc: '提供全套环境包及自动部署脚本。' },
    { icon: 'BookOpen', title: '高分论文参考', desc: '涵盖架构设计与算法说明的高质量文档。' },
    { icon: 'Zap', title: '工业级验证', desc: '核心算法已通过压力测试，代码规范。' }
  ]
})

// --- Upload Progress & Status ---
const coverProgress = ref(0)
const coverStatus = ref<string>('')
const coverController = ref<AbortController | null>(null)
const coverFile = ref<File | null>(null)
const coverResult = ref<UploadResult | null>(null)
const coverRelinkInput = ref<HTMLInputElement | null>(null)

const attachmentProgress = ref(0)
const attachmentStatus = ref<string>('')
const attachmentController = ref<AbortController | null>(null)
const attachmentFile = ref<File | null>(null)
const attachmentResult = ref<UploadResult | null>(null)
const attachmentRelinkInput = ref<HTMLInputElement | null>(null)

const mediaProgress = ref<Record<string, number>>({})
const mediaStatus = ref<Record<string, string>>({})
const mediaControllers = ref<Record<string, AbortController | null>>({})
const mediaFiles = ref<Record<string, File | null>>({})
const mediaResults = ref<Record<string, UploadResult | null>>({})

let mediaUploadSeed = 0

const createMediaUploadKey = () => `media-${Date.now()}-${mediaUploadSeed++}`

const ensureMediaUploadKey = (media: any) => {
  if (!media.__uploadKey) {
    media.__uploadKey = createMediaUploadKey()
  }
  return media.__uploadKey as string
}

// --- Utils ---
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

const formatDuration = (seconds: number) => {
  if (seconds < 1) return '< 1s'
  if (seconds < 60) return `${seconds}s`
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}m ${secs}s`
}

const syncMediaUploadKeys = () => {
  form.medias.forEach((media) => ensureMediaUploadKey(media))
}

const shouldShowUploadOverlay = (progress: number, status: string) => {
  return status === 'calculating' || status === 'merging' || status === 'paused' || (progress > 0 && progress < 100)
}

const isUploadBusy = (status: string) => status === 'calculating' || status === 'uploading' || status === 'merging'

const getMediaUploadKey = (media: any) => ensureMediaUploadKey(media)

const getMediaProgress = (media: any) => mediaProgress.value[getMediaUploadKey(media)] ?? 0

const getMediaStatus = (media: any) => mediaStatus.value[getMediaUploadKey(media)] ?? ''

const shouldShowMediaOverlay = (media: any) => {
  return shouldShowUploadOverlay(getMediaProgress(media), getMediaStatus(media))
}

const isMediaUploadBusy = (media: any) => isUploadBusy(getMediaStatus(media))

// --- Persistence Helpers ---
const STORAGE_KEY_PREFIX = 'upload_progress_'

const saveProgress = (key: string, progress: number, status: string) => {
  if (key) {
    localStorage.setItem(`${STORAGE_KEY_PREFIX}${key}`, JSON.stringify({ progress, status, timestamp: Date.now() }))
  }
}

const loadProgress = (key: string) => {
  const data = localStorage.getItem(`${STORAGE_KEY_PREFIX}${key}`)
  if (data) {
    try {
      const parsed = JSON.parse(data)
      // Only recover if fresh enough (within 24h)
      if (Date.now() - parsed.timestamp < 24 * 60 * 60 * 1000) {
        return parsed
      }
    } catch (e) { console.error('Failed to load progress', e) }
  }
  return null
}

const resetMediaUploadState = (mediaKey: string) => {
  const nextProgress = { ...mediaProgress.value }
  delete nextProgress[mediaKey]
  mediaProgress.value = nextProgress

  const nextStatus = { ...mediaStatus.value }
  delete nextStatus[mediaKey]
  mediaStatus.value = nextStatus

  const nextControllers = { ...mediaControllers.value }
  delete nextControllers[mediaKey]
  mediaControllers.value = nextControllers

  const nextFiles = { ...mediaFiles.value }
  delete nextFiles[mediaKey]
  mediaFiles.value = nextFiles
}

const ensureUploadFile = (file: File | null) => {
  if (file) return true
  ElMessage.warning('Please reselect the original file before resuming the upload.')
  return false
}

const isUploading = computed(() => {
  if (isUploadBusy(coverStatus.value)) return true
  if (isUploadBusy(attachmentStatus.value)) return true
  return Object.values(mediaStatus.value).some((status) => isUploadBusy(status))
})

const getMediaResult = (media: any) => mediaResults.value[getMediaUploadKey(media)] ?? null

// --- Attachment Handlers ---
const handleAttachmentUpload = async (options: any) => {
  const { file } = options
  if (file) attachmentFile.value = file
  const currentFile = file || attachmentFile.value
  if (!ensureUploadFile(currentFile)) return

  if (attachmentController.value) attachmentController.value.abort()
  attachmentController.value = new AbortController()

  try {
    const result = await uploadLargeFile(currentFile, {
      onProgress: (p) => {
        attachmentProgress.value = p
        saveProgress('attachment', p, 'uploading')
      },
      onStatusChange: (s) => {
        attachmentStatus.value = s
        saveProgress('attachment', attachmentProgress.value, s)
      },
      signal: attachmentController.value.signal
    })
    form.downloadUrl = result.url
    attachmentResult.value = result
    attachmentProgress.value = 100
    attachmentStatus.value = 'completed'
    localStorage.removeItem(`${STORAGE_KEY_PREFIX}attachment`)
    ElMessage.success('附件上传成功')
  } catch (e: any) {
    if (e.message === 'Upload paused') {
      ElMessage.info('上传已暂停')
    } else {
      attachmentProgress.value = 0
      attachmentStatus.value = 'error'
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
    attachmentStatus.value = ''
    attachmentFile.value = null
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


const serviceFeatureIcons = [
  { name: '显示器', value: 'MonitorDot', icon: LucideMonitorDot },
  { name: '代码', value: 'Code2', icon: LucideCode2 },
  { name: '文档', value: 'BookOpen', icon: LucideBookOpen },
  { name: '极速', value: 'Zap', icon: LucideZap },
  { name: '处理器', value: 'Cpu', icon: LucideCpu },
  { name: '数据库', value: 'Database', icon: LucideDatabase },
  { name: '模块', value: 'Puzzle', icon: LucidePuzzle },
  { name: '安全', value: 'ShieldCheck', icon: LucideShieldCheck },
  { name: '咨询', value: 'MessageCircleQuestion', icon: LucideMessageCircleQuestion },
  { name: '完成', value: 'CheckCircle2', icon: LucideCheckCircle2 },
  { name: '移动端', value: 'Smartphone', icon: LucideSmartphone },
  { name: '视频', value: 'Video', icon: LucideVideo },
  { name: '布局', value: 'Layout', icon: LucideLayout },
  { name: '调色盘', value: 'Palette', icon: LucidePalette },
  { name: '设置', value: 'Settings', icon: LucideSettings },
  { name: '搜索', value: 'Search', icon: LucideSearch }
]

const loadProject = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const res: any = await request.get(`/api/projects/${form.id}`)
    Object.assign(form, res.data)
    syncMediaUploadKeys()
    // 确保 medias 是数组
    if (!form.medias) form.medias = []
    // 转换标签
    if (typeof res.data.tags === 'string' && res.data.tags) {
      form.tags = res.data.tags.split(',')
    } else {
      form.tags = []
    }
    // 转换内容
    if (typeof res.data.resourceContents === 'string' && res.data.resourceContents) {
      try {
        resourceTree.value = JSON.parse(res.data.resourceContents)
      } catch (e) {
        console.error('Failed to parse resourceContents', e)
        resourceTree.value = []
      }
    } else {
      resourceTree.value = []
    }

    // 转换服务特色
    if (typeof res.data.serviceFeatures === 'string' && res.data.serviceFeatures) {
      try {
        form.serviceFeatures = JSON.parse(res.data.serviceFeatures)
      } catch (e) {
        console.error('Failed to parse serviceFeatures', e)
      }
    }

    // 如果加载后 serviceFeatures 为空或 null (Object.assign 覆盖)，恢复默认值
    if (!form.serviceFeatures || !Array.isArray(form.serviceFeatures) || form.serviceFeatures.length === 0) {
      form.serviceFeatures = [
        { icon: 'MonitorDot', title: '1-on-1 真人演示', desc: '购买后可预约远程演示，讲解项目架构与核心链路。' },
        { icon: 'Code2', title: '全套环境搭建', desc: '提供全套环境包及自动部署脚本。' },
        { icon: 'BookOpen', title: '高分论文参考', desc: '涵盖架构设计与算法说明的高质量文档。' },
        { icon: 'Zap', title: '工业级验证', desc: '核心算法已通过压力测试，代码规范。' }
      ]
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
      tags: form.tags.join(','),
      serviceFeatures: JSON.stringify(form.serviceFeatures),
      resourceContents: JSON.stringify(resourceTree.value)
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

const addServiceFeature = () => {
  form.serviceFeatures.push({ icon: 'MonitorDot', title: '', desc: '' })
}

const removeServiceFeature = (index: number) => {
  form.serviceFeatures.splice(index, 1)
}

const addMedia = () => form.medias.push({ mediaType: 1, mediaUrl: '', __uploadKey: createMediaUploadKey() })

const removeMedia = async (index: number) => {
  const media = form.medias[index]
  const mediaKey = getMediaUploadKey(media)
  mediaControllers.value[mediaKey]?.abort()
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
  resetMediaUploadState(mediaKey)
}

const handleDeleteCover = async () => {
  if (!form.coverImage) return
  try {
    await ElMessageBox.confirm('确认删除当前封面图并同步清理云端文件？', '警告', { type: 'warning' })
    await request.delete('/api/file/delete', { params: { url: form.coverImage } })
    form.coverImage = ''
    coverProgress.value = 0
    coverStatus.value = ''
    coverFile.value = null
    ElMessage.success('封面已移除')
  } catch (e) {
    if (e === 'cancel') return
    ElMessage.error('清理失败')
  }
}

const handleCoverUpload = async (options: any) => {
  const { file } = options
  if (file) coverFile.value = file
  const currentFile = file || coverFile.value
  if (!ensureUploadFile(currentFile)) return

  if (coverController.value) coverController.value.abort()
  coverController.value = new AbortController()

  try {
    const result = await uploadLargeFile(currentFile, {
      onProgress: (p) => {
        coverProgress.value = p
        saveProgress('cover', p, 'uploading')
      },
      onStatusChange: (s) => {
        coverStatus.value = s
        saveProgress('cover', coverProgress.value, s)
      },
      signal: coverController.value.signal
    })
    form.coverImage = result.url
    coverResult.value = result
    coverProgress.value = 100
    coverStatus.value = 'completed'
    localStorage.removeItem(`${STORAGE_KEY_PREFIX}cover`)
    ElMessage.success('封面上传成功')
  } catch (e: any) {
    if (e.message === 'Upload paused') {
      ElMessage.info('封面上传已暂停')
    } else {
      coverProgress.value = 0
      coverStatus.value = 'error'
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

const handleMediaUpload = async (options: any, media: any, index: number) => {
  const { file } = options
  const mediaKey = getMediaUploadKey(media)
  if (file) mediaFiles.value = { ...mediaFiles.value, [mediaKey]: file }
  const currentFile = file || mediaFiles.value[mediaKey] || null
  if (!ensureUploadFile(currentFile)) return

  if (mediaControllers.value[mediaKey]) mediaControllers.value[mediaKey]?.abort()
  const controller = new AbortController()
  mediaControllers.value = { ...mediaControllers.value, [mediaKey]: controller }

  try {
    const result = await uploadLargeFile(currentFile, {
      onProgress: (p) => {
        mediaProgress.value = { ...mediaProgress.value, [mediaKey]: p }
        saveProgress(mediaKey, p, 'uploading')
      },
      onStatusChange: (s) => {
        mediaStatus.value = { ...mediaStatus.value, [mediaKey]: s }
        saveProgress(mediaKey, mediaProgress.value[mediaKey] || 0, s)
      },
      signal: controller.signal
    })
    form.medias[index].mediaUrl = result.url
    mediaResults.value = { ...mediaResults.value, [mediaKey]: result }
    mediaProgress.value = { ...mediaProgress.value, [mediaKey]: 100 }
    mediaStatus.value = { ...mediaStatus.value, [mediaKey]: 'completed' }
    localStorage.removeItem(`${STORAGE_KEY_PREFIX}${mediaKey}`)
    ElMessage.success('素材上传成功')
  } catch (e: any) {
    if (e.message === 'Upload paused') {
      ElMessage.info('上传暂停')
    } else {
      resetMediaUploadState(mediaKey)
      ElMessage.error('素材上传失败')
    }
  } finally {
    mediaControllers.value = { ...mediaControllers.value, [mediaKey]: null }
  }
}

const pauseMediaUpload = (media: any) => {
  const mediaKey = getMediaUploadKey(media)
  if (mediaControllers.value[mediaKey]) {
    mediaControllers.value[mediaKey]?.abort()
  }
}

const triggerCoverReSelect = () => {
  if (coverFile.value) handleCoverUpload({ file: null })
  else coverRelinkInput.value?.click()
}

const triggerAttachmentReSelect = () => {
  if (attachmentFile.value) handleAttachmentUpload({ file: null })
  else attachmentRelinkInput.value?.click()
}

const handleRelinkGeneral = (e: Event, mode: 'cover' | 'attachment') => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    const file = target.files[0]
    if (mode === 'cover') handleCoverUpload({ file })
    else handleAttachmentUpload({ file })
    target.value = ''
  }
}

const triggerMediaReSelect = (media: any, index: number) => {
  const mediaKey = getMediaUploadKey(media)
  if (mediaFiles.value[mediaKey]) {
    handleMediaUpload({ file: null }, media, index)
  } else {
    activeRelinkIndex.value = index
    relinkInput.value?.click()
  }
}

const handleRelinkFile = (e: Event) => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0 && activeRelinkIndex.value !== null) {
    const file = target.files[0]
    handleMediaUpload({ file }, form.medias[activeRelinkIndex.value], activeRelinkIndex.value)
    activeRelinkIndex.value = null
    target.value = ''
  }
}
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

onMounted(async () => {
  await Promise.all([loadCategories(), loadProject()])

  // Restore common progress
  const coverRestore = loadProgress('cover')
  if (coverRestore && !form.coverImage) {
    coverProgress.value = coverRestore.progress
    coverStatus.value = coverRestore.status
  }

  const attachRestore = loadProgress('attachment')
  if (attachRestore && !form.downloadUrl) {
    attachmentProgress.value = attachRestore.progress
    attachmentStatus.value = attachRestore.status
  }

  // Restore media progress
  form.medias.forEach((media) => {
    const mediaKey = getMediaUploadKey(media)
    const restore = loadProgress(mediaKey)
    if (restore && !media.mediaUrl) {
      mediaProgress.value[mediaKey] = restore.progress
      mediaStatus.value[mediaKey] = restore.status
    }
  })
})

const resourceTree = ref<any[]>([])

const addRootResourceNode = () => {
  resourceTree.value.push({ id: Date.now(), label: '新建项', children: [] })
}

const appendResourceNode = (data: any) => {
  if (!data.children) data.children = []
  data.children.push({ id: Date.now(), label: '子项', children: [] })
}

const removeResourceNode = (node: any, data: any) => {
  const parent = node.parent
  const children = parent.data.children || parent.data
  const index = children.findIndex((d: any) => d.id === data.id)
  children.splice(index, 1)
}

const LucideFolderPlus = (props: any) => h('svg', { ...props, 'xmlns': 'http://www.w3.org/2000/svg', 'width': '24', 'height': '24', 'viewBox': '0 0 24 24', 'fill': 'none', 'stroke': 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'class': 'lucide lucide-folder-plus' }, [h('path', { 'd': 'M12 10v6' }), h('path', { 'd': 'M9 13h6' }), h('path', { 'd': 'M20 20a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h4l2 3h10a2 2 0 0 1 2 2Z' })])

const LucideFolder = (props: any) => h('svg', { ...props, 'xmlns': 'http://www.w3.org/2000/svg', 'width': '24', 'height': '24', 'viewBox': '0 0 24 24', 'fill': 'none', 'stroke': 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'class': 'lucide lucide-folder' }, [h('path', { 'd': 'M20 20a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h4l2 3h10a2 2 0 0 1 2 2Z' })])

const LucideFileText = (props: any) => h('svg', { ...props, 'xmlns': 'http://www.w3.org/2000/svg', 'width': '24', 'height': '24', 'viewBox': '0 0 24 24', 'fill': 'none', 'stroke': 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'class': 'lucide lucide-file-text' }, [h('path', { 'd': 'M15 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7Z' }), h('path', { 'd': 'M14 2v4a2 2 0 0 0 2 2h4' }), h('path', { 'd': 'M10 9H8' }), h('path', { 'd': 'M16 13H8' }), h('path', { 'd': 'M16 17H8' })])

const LucidePlus = (props: any) => h('svg', { ...props, 'xmlns': 'http://www.w3.org/2000/svg', 'width': '24', 'height': '24', 'viewBox': '0 0 24 24', 'fill': 'none', 'stroke': 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'class': 'lucide lucide-plus' }, [h('path', { 'd': 'M5 12h14' }), h('path', { 'd': 'M12 5v14' })])

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
  color: #7c3aed;
  /* 浅紫色代表 AI */
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
  padding: 0 var(--canvas-padding) 100px;
  max-width: 1400px;
  margin: 0 auto;
  color: var(--admin-text-main);
  transition: padding 0.3s ease;
}

@media (max-width: 768px) {
  .project-detail-wrapper {
    padding: 0 16px 60px;
  }
}

/* --- Header & Navigation --- */
.view-header {
  min-height: var(--header-height);
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--admin-border);
  gap: 16px;
  flex-wrap: wrap;
  padding: 12px 0;
}

@media (max-width: 768px) {
  .view-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .header-ops {
    width: 100%;
    justify-content: flex-end;
  }
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
  gap: var(--canvas-gap);
  position: relative;
  margin-top: 40px;
  transition: all 0.4s ease;
  min-width: 0;
}

@media (max-width: 1400px) {
  .seamless-canvas {
    gap: 40px;
  }
}

@media (max-width: 1280px) {
  .seamless-canvas {
    grid-template-columns: 1fr 300px;
    gap: 32px;
  }
}

@media (max-width: 1100px) {
  .seamless-canvas {
    grid-template-columns: 1fr;
    gap: 48px;
  }

  .canvas-support-stream {
    width: 100%;
    order: 2;
  }

  .canvas-main-flow {
    order: 1;
    min-width: 0;
  }
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
  transition: all 0.3s ease;
  z-index: 10;
}

@media (max-width: 1480px) {
  .section-indicator {
    position: relative;
    left: 0;
    top: 0;
    transform: none;
    margin-bottom: 20px;
    display: block;
    opacity: 0.8;
  }
  
  .canvas-section {
    padding-bottom: 48px;
  }
}

.canvas-divider {
  height: 1px;
  background: linear-gradient(90deg, var(--admin-border) 0%, transparent 100%);
  margin-bottom: 48px;
}

@media (max-width: 1200px) {
  .canvas-divider {
    margin-bottom: 32px;
  }
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
  font-size: clamp(24px, 4vw, 44px);
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
  flex-wrap: wrap;
  gap: 32px 48px;
  margin-top: 32px;
  align-items: flex-end;
}

@media (max-width: 768px) {
  .meta-row-seamless {
    gap: 20px;
  }
  .meta-item {
    flex: 1;
    min-width: 160px;
  }
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meta-item:first-child {
  min-width: 140px;
  /* Ensure category has enough width */
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
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
  overflow: hidden;
}

.distilled-editor {
  height: 520px;
  border: none !important;
  background: var(--admin-surface);
  width: 100%;
}

@media (max-width: 768px) {
  .distilled-editor {
    height: 400px;
  }
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
  position: relative;
  overflow: hidden;
}

@media (max-width: 640px) {
  .asset-strip-item {
    flex-wrap: wrap;
    gap: 12px;
    padding: 16px;
  }
  
  .asset-type-indicator {
    order: 1;
  }
  
  .asset-config-row {
    order: 3;
    width: 100%;
    flex: none !important;
  }
  
  .asset-preview-box {
    order: 2;
    margin-left: auto;
  }
  
  .btn-item-delete {
    position: absolute;
    top: 8px;
    right: 8px;
  }
}

/* --- Upload Overlay & Pause Stats --- */
.upload-overlay,
.asset-upload-overlay {
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

.pause-btn,
.mini-pause-btn,
.attachment-pause-btn {
  color: #ef4444 !important;
  opacity: 0.8;
  transition: all 0.2s;
  pointer-events: auto !important;
  /* Ensure clickable */
  cursor: pointer !important;
}

.pause-btn:hover,
.mini-pause-btn:hover,
.attachment-pause-btn:hover {
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
  min-width: 0;
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
  height: 2px;
  /* Slightly thicker for visibility */
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
  cursor: pointer;
  /* Change cursor for preview */
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
  background: rgba(0, 0, 0, 0.5);
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
  display: flex;
  /* Centering fix */
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
  to {
    transform: rotate(360deg);
  }
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
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.04);
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

/* Media Upload Overlay V2 (Linear Progress) */
.asset-upload-overlay-v2 {
  position: absolute;
  inset: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  overflow: hidden;
  border-radius: 12px;
}

.overlay-blur-bg {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px) saturate(180%);
  z-index: -1;
}

.upload-content-v2 {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.progress-meta-v2 {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-badge {
  font-size: 10px;
  font-weight: 800;
  color: var(--admin-primary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.percentage-v2 {
  font-family: var(--font-data);
  font-size: 12px;
  font-weight: 800;
  color: var(--admin-primary);
}

.linear-progress-v2 {
  width: 100%;
}

.linear-progress-v2 :deep(.el-progress-bar__outer) {
  background-color: rgba(var(--admin-primary-rgb), 0.1) !important;
  overflow: hidden;
}

.linear-progress-v2 :deep(.el-progress-bar__inner) {
  background: linear-gradient(90deg, var(--admin-primary), #a855f7) !important;
  box-shadow: 0 0 10px rgba(124, 58, 237, 0.4);
  transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.actions-row-v2 {
  display: flex;
  justify-content: center;
}

.mini-btn-v2 {
  width: 24px !important;
  height: 24px !important;
  min-height: 24px !important;
  padding: 0 !important;
  background: var(--admin-primary) !important;
  border: none !important;
  color: #fff !important;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.mini-btn-v2:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
}

.pause-btn-v2 {
  background: #ef4444 !important;
}

.asset-progress-wrap.attachment-fix {
  inset: auto 0 0 0;
  height: 2px;
}

.asset-progress-wrap.attachment-fix .asset-progress-meta {
  right: 40px;
  /* Avoid overlapping delete btn */
}

.asset-progress-wrap {
  position: absolute;
  inset: 0;
  z-index: 5;
  pointer-events: none;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.asset-progress-track {
  height: 2px;
  width: 100%;
  background: rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.asset-progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #7c3aed, #a855f7);
  box-shadow: 0 0 8px rgba(124, 58, 237, 0.5);
  transition: width 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.asset-progress-meta {
  pointer-events: auto;
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  padding: 4px 10px;
  border-radius: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(124, 58, 237, 0.1);
}

.p-status {
  font-size: 10px;
  font-weight: 700;
  color: var(--admin-text-muted);
}

.p-num {
  font-family: var(--font-data);
  font-size: 11px;
  font-weight: 800;
  color: var(--admin-primary);
}

.p-btn {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  color: var(--admin-primary);
  background: var(--admin-primary-glow);
  border-radius: 50%;
  transition: all 0.2s;
}

.p-btn:hover {
  transform: scale(1.1);
  background: var(--admin-primary);
  color: #fff;
}

.p-btn.pause {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}

.p-btn.pause:hover {
  background: #ef4444;
  color: #fff;
}

/* Animations */
@keyframes canvas-fade {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stagger-list-enter-active {
  transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

.stagger-list-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.tag-pop-enter-active {
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.tag-pop-enter-from {
  opacity: 0;
  transform: scale(0.5);
}

/* Upload Results & Progress Enhancements */
.upload-result-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 6px;
  padding: 4px 10px;
  background: var(--admin-primary-glow);
  border-radius: 6px;
  color: var(--admin-primary);
  font-size: 10px;
  font-weight: 700;
  width: fit-content;
  border: 1px solid rgba(124, 58, 237, 0.1);
}

.upload-result-badge .sep {
  opacity: 0.4;
}

.cover-result-tag {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(124, 58, 237, 0.9);
  backdrop-filter: blur(8px);
  color: #fff;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 10px;
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 6px;
  z-index: 10;
  box-shadow: 0 4px 15px rgba(124, 58, 237, 0.4);
  pointer-events: none;
}

.file-meta-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  color: var(--admin-text-muted);
  font-weight: 600;
  margin-top: 2px;
}

.file-meta-row .dot {
  opacity: 0.5;
}

.fade-in {
  animation: fadeInV2 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes fadeInV2 {
  from {
    opacity: 0;
    transform: translateY(8px) scale(0.95);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Premium Progress Visuals */
:deep(.el-progress-circle__track) {
  stroke: var(--admin-bg) !important;
}

:deep(.el-progress--circle .el-progress__text) {
  font-family: var(--font-data);
  font-weight: 800;
  font-size: 10px !important;
  color: var(--admin-primary) !important;
}

.attachment-text-status {
  flex: 1;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 12px;
  font-weight: 600;
}


/* --- Refined Liquid Glass Feature System --- */
.feature-grid-premium {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32px;
  margin-top: 40px;
}

.feature-glass-card {
  position: relative;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(20px) saturate(180%);
  transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
  box-shadow:
    0 4px 24px -1px rgba(0, 0, 0, 0.03),
    inset 0 0 0 1px rgba(255, 255, 255, 0.5);
}

.feature-glass-card:hover {
  transform: translateY(-8px) scale(1.02);
  background: rgba(255, 255, 255, 0.6);
  box-shadow:
    0 20px 40px -12px rgba(var(--admin-primary-rgb), 0.12),
    inset 0 0 0 1px rgba(var(--admin-primary-rgb), 0.2);
}

.glass-card-inner {
  padding: 24px;
  position: relative;
  z-index: 1;
}

.glass-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.feat-status-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(0, 0, 0, 0.03);
  padding: 4px 10px;
  border-radius: 8px;
}

.feat-ord {
  font-family: var(--font-data);
  font-size: 10px;
  font-weight: 800;
  color: var(--admin-text-muted);
  letter-spacing: 1px;
}

.feat-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--admin-primary);
  box-shadow: 0 0 8px var(--admin-primary);
}

.btn-glass-delete {
  background: rgba(239, 68, 68, 0.08) !important;
  border: none !important;
  color: #ef4444 !important;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s;
}

.feature-glass-card:hover .btn-glass-delete {
  opacity: 1;
  transform: translateX(0);
}

.glass-card-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feat-visual-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.feat-icon-ring {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(var(--admin-primary-rgb), 0.1), rgba(var(--admin-primary-rgb), 0.05));
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  color: var(--admin-primary);
}

.icon-ring-glow {
  position: absolute;
  inset: -1px;
  border-radius: inherit;
  background: linear-gradient(135deg, rgba(var(--admin-primary-rgb), 0.4), transparent);
  opacity: 0.3;
  z-index: -1;
}

.glass-icon-selector {
  width: 90px;
}

.glass-icon-selector :deep(.el-select__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  border-bottom: 1px dashed var(--admin-border) !important;
  border-radius: 0;
  padding: 0 4px;
}

.glass-icon-selector :deep(.el-select__placeholder) {
  font-family: var(--font-data);
  font-size: 11px;
  font-weight: 800;
  color: var(--admin-text-muted);
}

.feat-form-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.glass-input-title :deep(.el-input__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  padding: 0;
}

.glass-input-title :deep(.el-input__inner) {
  font-size: 18px;
  font-weight: 700;
  color: var(--admin-text-main);
  padding: 0;
}

.glass-input-desc :deep(.el-textarea__inner) {
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
  padding: 0;
  font-size: 13px;
  line-height: 1.6;
  color: var(--admin-text-secondary);
  resize: none;
}

.glass-card-border {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, var(--admin-primary), transparent);
  opacity: 0.2;
}

.feature-glass-card:hover .glass-card-border {
  opacity: 0.6;
}

/* Animations */
.feat-stagger-move,
.feat-stagger-enter-active,
.feat-stagger-leave-active {
  transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

.feat-stagger-enter-from,
.feat-stagger-leave-to {
  opacity: 0;
  transform: translateY(30px) scale(0.9);
}

.feat-stagger-leave-active {
  position: absolute;
}

@media (max-width: 1024px) {
  .feature-grid-premium {
    grid-template-columns: 1fr;
  }
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 复用一些媒体资产的空状态样式 */
.canvas-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  border: 2px dashed var(--admin-border);
  border-radius: 16px;
  background: rgba(var(--admin-primary-rgb), 0.02);
  margin-top: 20px;
}

.empty-icon-ring {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
  color: var(--admin-text-muted);
}

.empty-text {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.primary-msg {
  font-size: 15px;
  font-weight: 600;
  color: var(--admin-text-secondary);
}

.secondary-msg {
  font-size: 13px;
  color: var(--admin-text-muted);
}

.btn-seamless-add {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  background: rgba(var(--admin-primary-rgb), 0.05);
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s;
}

.btn-seamless-add:hover {
  background: rgba(var(--admin-primary-rgb), 0.1);
  transform: translateY(-2px);
}

/* --- Resource Tree Management --- */
.resource-section {
  padding-bottom: 120px;
}

.premium-resource-tree {
  background: transparent !important;
  margin-top: 24px;
}

.premium-resource-tree :deep(.el-tree-node__content) {
  height: auto !important;
  padding: 8px 0;
  background: transparent !important;
}

.premium-resource-tree :deep(.el-tree-node__content:hover) {
  background: rgba(var(--admin-primary-rgb), 0.03) !important;
  border-radius: 12px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
  min-width: 0;
}

@media (max-width: 640px) {
  .node-main {
    flex: 1;
    min-width: 0;
  }
  
  .node-label-input {
    width: 100% !important;
  }
}

.node-main {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.node-icon {
  color: var(--admin-text-muted);
}

.node-icon.folder {
  color: #fbbf24;
}

.node-icon.file {
  color: var(--admin-primary);
}

.node-label-input :deep(.el-input__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  border-bottom: 1px solid var(--admin-border);
  border-radius: 0;
  padding: 2px 0;
  transition: all 0.3s;
}

.node-label-input :deep(.el-input__wrapper.is-focus) {
  border-bottom-color: var(--admin-primary);
}

.node-label-input :deep(.el-input__inner) {
  font-weight: 600;
  color: var(--admin-text-secondary);
}

.node-ops {
  display: flex;
  align-items: center;
  gap: 4px;
  opacity: 0;
  transition: all 0.3s;
}

.custom-tree-node:hover .node-ops {
  opacity: 1;
}

.node-btn {
  padding: 4px;
}

.node-btn.delete {
  color: #ef4444 !important;
}

.node-btn:hover {
  background: rgba(var(--admin-primary-rgb), 0.05);
  border-radius: 4px;
}
</style>
