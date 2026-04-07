<template>
  <div class="min-h-screen bg-white selection:bg-primary/10">
    <div v-if="project" class="animate-fade-in">
      <!-- Immersive Header Section -->
      <section class="relative h-[65vh] overflow-hidden bg-profound-black group">
        <div class="absolute inset-0">
          <el-carousel v-if="mediaItems.length > 0" 
                       trigger="click" 
                       height="65vh" 
                       :autoplay="false"
                       arrow="always" 
                       class="w-full h-full opacity-95 transition-opacity duration-500 custom-carousel">
            <el-carousel-item v-for="(item, index) in mediaItems" :key="index">
              <div class="w-full h-full relative group cursor-pointer overflow-hidden" 
                   @click="handleMediaClick(item, index)">
                <video v-if="item.mediaType === 2" 
                       :src="item.mediaUrl" 
                       autoplay 
                       muted 
                       loop 
                       playsinline
                       class="w-full h-full object-cover pointer-events-none" />
                <img v-else 
                     :src="item.mediaUrl" 
                     class="w-full h-full object-cover pointer-events-none" />
                
                <!-- Overlay Indicators -->
                <div class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity bg-profound-black/30">
                  <PlayCircle v-if="item.mediaType === 2" :size="80" class="text-white drop-shadow-2xl animate-pulse pointer-events-none" />
                  <Maximize2 v-else :size="56" class="text-white drop-shadow-2xl pointer-events-none" />
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
          <div class="absolute inset-0 bg-profound-black/30 pointer-events-none"></div>
        </div>

        <div class="relative h-full max-w-7xl mx-auto px-6 sm:px-8 flex flex-col justify-center space-y-8 pointer-events-none">
          <div class="flex items-center gap-3 animate-fade-in pointer-events-auto">
            <span class="px-5 py-2 bg-primary text-white text-[9px] font-bold uppercase tracking-profound rounded-lg shadow-xl shadow-primary/20">
              {{ project.categoryName || '精品项目' }}
            </span>
            <span v-if="project.major" class="px-5 py-2 bg-white/10 backdrop-blur-md text-white text-[9px] font-bold uppercase tracking-profound rounded-lg border border-white/20">
              {{ project.major }}
            </span>
            <span class="text-white/20 font-mono text-[10px] tracking-widest uppercase">REF_ID: #{{ project.id.toString().padStart(6, '0') }}</span>
          </div>
          
          <h1 class="text-5xl md:text-7xl font-display text-white tracking-tighter-profound leading-[1.0] max-w-5xl animate-fade-in-up uppercase pointer-events-auto">
            {{ project.title }}
          </h1>

          <div class="flex flex-wrap gap-3 items-center animate-fade-in pointer-events-auto" style="animation-delay: 200ms">
            <div v-for="tag in techTags" :key="tag" 
                 class="px-5 py-2.5 bg-white/5 backdrop-blur-xl border border-white/10 text-white rounded-xl text-xs font-bold hover:bg-white/20 transition-all cursor-default">
              {{ tag }}
            </div>
          </div>

          <!-- Classification Tags -->
          <div v-if="project.tags" class="flex flex-wrap gap-2 animate-fade-in" style="animation-delay: 150ms">
            <span v-for="tag in project.tags.split(',')" :key="tag" 
                  class="px-3 py-1 bg-primary/10 border border-primary/20 text-primary rounded-lg text-[10px] font-bold uppercase tracking-wider">
              # {{ tag }}
            </span>
          </div>
          
          <!-- Quick Stats Overlay -->
          <div class="flex gap-10 pt-6 text-white/40 font-bold uppercase tracking-profound text-[9px] pointer-events-auto">
            <div class="flex items-center gap-2"><Eye :size="14" class="text-primary" /> {{ project.viewCount }} Views</div>
            <div class="flex items-center gap-2"><Download :size="14" class="text-primary" /> {{ project.downloadCount || 0 }} Deliveries</div>
            <div class="flex items-center gap-2"><Star :size="14" class="text-primary" /> 4.9 Rating</div>
          </div>
        </div>
      </section>

      <!-- Main Content Area -->
      <div class="max-w-7xl mx-auto px-6 sm:px-8 -mt-24 relative z-10 pb-40">
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-12 items-start">
          <div class="lg:col-span-2 space-y-12">
            <div class="bg-white p-2 rounded-5xl shadow-2xl shadow-slate-200/50 overflow-hidden border border-slate-100">
              <!-- Professional Tabs -->
              <div class="flex p-3 border-b border-slate-50 gap-2 overflow-x-auto no-scrollbar">
                <button v-for="tab in tabOptions" :key="tab.id"
                        v-show="tab.show !== false"
                        @click="activeTab = tab.id"
                        class="px-8 py-4 rounded-3xl text-xs font-bold transition-all duration-500 whitespace-nowrap flex items-center gap-2"
                        :class="activeTab === tab.id ? 'bg-profound-black text-white shadow-xl scale-[1.02]' : 'text-slate-400 hover:text-profound-black hover:bg-slate-50'">
                  <component :is="tab.icon" :size="16" />
                  {{ tab.label }}
                </button>
              </div>

              <div class="p-8 md:p-12 min-h-[450px]">
                <!-- Tab contents -->
                <div v-if="activeTab === 'overview'" class="space-y-10 animate-fade-in">
                  <div class="space-y-5">
                    <h3 class="text-2xl font-display text-profound-black uppercase">项目背景与价值</h3>
                    <p class="text-lg text-slate-500 font-medium leading-relaxed italic border-l-2 border-primary pl-6">
                      {{ project.description }}
                    </p>
                  </div>

                  <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div v-for="feat in serviceFeatures" :key="feat.title" 
                         class="p-8 rounded-4xl bg-slate-50 border border-slate-100 space-y-3 group hover:bg-primary transition-all duration-500">
                      <div class="w-12 h-12 rounded-xl bg-white shadow-sm flex items-center justify-center text-primary group-hover:scale-110 transition-all">
                        <component :is="feat.icon" :size="24" />
                      </div>
                      <h4 class="text-xl font-bold text-profound-black group-hover:text-white transition-colors">{{ feat.title }}</h4>
                      <p class="text-slate-500 group-hover:text-white/70 transition-colors text-xs font-medium leading-relaxed">{{ feat.desc }}</p>
                    </div>
                  </div>
                </div>

                <div v-if="activeTab === 'features'" class="space-y-10 animate-fade-in">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
                    <div class="space-y-5">
                      <h4 class="text-xl font-bold text-profound-black flex items-center gap-2">
                        <CheckCircle class="text-emerald-500" /> 核心功能模块
                      </h4>
                      <ul class="space-y-3">
                        <li v-for="item in functionalModules" :key="item" class="flex gap-3 p-4 bg-slate-50 rounded-2xl border border-slate-100 group hover:border-primary/30 transition-all">
                          <div class="w-1.5 h-1.5 rounded-full bg-primary mt-1.5"></div>
                          <span class="text-slate-600 font-bold text-sm">{{ item }}</span>
                        </li>
                      </ul>
                    </div>
                    <div class="space-y-4">
                      <img :src="project.coverImage || 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?q=80&w=2070&auto=format&fit=crop'" class="w-full h-72 object-cover rounded-4xl shadow-xl grayscale hover:grayscale-0 transition-all duration-700" />
                    </div>
                  </div>
                </div>

                <div v-if="activeTab === 'specs'" class="space-y-10 animate-fade-in">
                  <div v-if="project.techDetails && project.techDetails.length > 0" class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div v-for="(group, name) in groupedTechDetails" :key="name" 
                         class="p-8 bg-slate-50 border border-slate-100 rounded-4xl space-y-5">
                      <h4 class="text-sm font-bold text-profound-black flex items-center gap-2 uppercase tracking-tight">
                        <component :is="getTechIcon(name)" :size="16" class="text-primary" />
                        {{ name }}
                      </h4>
                      <div class="flex flex-wrap gap-2">
                        <span v-for="item in group" :key="item.itemName" 
                              class="px-4 py-2 bg-white border border-slate-100 rounded-xl text-[10px] font-bold text-slate-600 shadow-sm">
                          {{ item.itemName }}
                        </span>
                      </div>
                    </div>
                  </div>
                  <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-8 text-center py-10 opacity-30">
                     <Cpu :size="40" class="mx-auto col-span-2" />
                     <p class="font-bold uppercase tracking-profound text-[10px] col-span-2">技术参数收录中</p>
                  </div>
                </div>

                <div v-if="activeTab === 'ask'" class="space-y-8 animate-fade-in">
                  <div class="space-y-3">
                    <h3 class="text-2xl font-display text-profound-black uppercase">业务咨询</h3>
                    <p class="text-sm text-slate-500 font-medium">留下您的问题，我们的专家团队将为您解惑。</p>
                  </div>
                  <form @submit.prevent="submitConsultation" class="grid grid-cols-1 gap-5">
                    <textarea v-model="consultForm.content" required placeholder="请详细描述您的问题..."
                              class="w-full h-32 p-6 bg-slate-50 border border-slate-100 rounded-3xl outline-none focus:ring-2 focus:ring-primary/20 focus:bg-white transition-all text-slate-800 text-sm"></textarea>
                    <input v-model="consultForm.contactWay" type="text" required placeholder="联系方式 (微信/QQ/邮箱)"
                           class="w-full p-6 bg-slate-50 border border-slate-100 rounded-3xl outline-none focus:ring-2 focus:ring-primary/20 focus:bg-white transition-all text-slate-800 text-sm" />
                    <button type="submit" :disabled="submittingConsult"
                            class="px-10 py-5 bg-profound-black text-white rounded-3xl font-bold text-sm hover:bg-primary transition-all flex items-center justify-center gap-2 disabled:opacity-50 group">
                      <MessageCircleQuestion :size="18" class="group-hover:rotate-12 transition-transform" /> {{ submittingConsult ? '正在发送...' : '确认提交' }}
                    </button>
                  </form>
                </div>



                <div v-if="activeTab === 'detail'" class="animate-fade-in">
                  <div v-if="project.contentHtml" class="prose prose-slate max-w-none">
                    <md-preview :modelValue="project.contentHtml" />
                  </div>
                  <div v-else class="py-16 text-center space-y-4">
                     <BookOpen :size="32" class="text-slate-200 mx-auto" />
                     <p class="text-[10px] uppercase tracking-profound text-slate-400 font-bold">深度描述编写中</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Recommended -->
            <div class="space-y-8 pt-10">
              <div class="flex justify-between items-end">
                <div class="space-y-2">
                  <h3 class="text-3xl font-display text-profound-black uppercase italic tracking-tighter-profound">延伸阅读</h3>
                  <p class="text-slate-400 font-bold uppercase tracking-profound text-[9px]">Related academic projects</p>
                </div>
                <NuxtLink to="/projects" class="text-primary text-[10px] font-bold uppercase tracking-profound hover:translate-x-1 transition-transform flex items-center gap-1">查看全部 <ArrowRight :size="12" /></NuxtLink>
              </div>
              <div v-if="similarProjects?.length" class="grid grid-cols-1 md:grid-cols-2 gap-8">
                <ProjectCard v-for="p in similarProjects" :key="p.id" :project="p" />
              </div>
            </div>
          </div>

          <!-- Sidebar -->
          <div class="space-y-8">
            <div class="sticky top-28 space-y-8">
              <div class="bg-profound-black p-10 rounded-5xl shadow-3xl shadow-primary/10 text-white space-y-8 relative overflow-hidden border border-white/5">
                <div class="absolute -top-10 -right-10 w-40 h-40 bg-primary rounded-full blur-[80px] opacity-20"></div>
                
                <div class="space-y-1 relative z-10">
                  <span class="text-slate-500 font-bold uppercase tracking-profound text-[8px]">标准包包含内容 / Ref. Package</span>
                  <div class="flex items-baseline gap-1 pt-1">
                    <span class="text-3xl font-bold opacity-30 tracking-tighter">¥</span>
                    <span class="text-7xl font-display tracking-tighter-profound">{{ project.price }}</span>
                  </div>
                </div>

                <div class="space-y-4 relative z-10">
                  <div v-for="item in inclusions" :key="item" class="flex items-start gap-3 text-[11px] font-bold text-slate-400">
                    <Check :size="14" class="text-primary mt-0.5" />
                    {{ item }}
                  </div>
                </div>

                <button @click="handleContact" 
                        class="w-full py-6 bg-primary hover:bg-white hover:text-profound-black text-white rounded-3xl font-bold text-sm shadow-xl shadow-primary/20 transition-all active:scale-95 flex items-center justify-center gap-2 relative z-10 group">
                   <MessageCircle :size="20" /> 立即咨询获取
                </button>
              </div>

              <div class="p-10 bg-white rounded-5xl border border-slate-100 shadow-xl shadow-slate-200/40 space-y-8">
                <div class="text-center space-y-4">
                  <div class="w-16 h-16 bg-primary/5 rounded-2xl flex items-center justify-center mx-auto">
                    <UserCheck :size="28" class="text-primary" />
                  </div>
                  <h4 class="text-xl font-display text-profound-black uppercase">定制服务</h4>
                  <p class="text-slate-400 font-medium text-[11px] leading-relaxed">导师团队提供个性化功能开发与答辩辅导。</p>
                </div>
                <NuxtLink to="/services" class="w-full py-5 rounded-3xl bg-profound-black text-white font-bold text-xs hover:bg-primary transition-all text-center block uppercase tracking-widest">
                  进入导师中心
                </NuxtLink>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="min-h-screen flex items-center justify-center">
       <div class="w-10 h-10 border-2 border-primary/20 border-t-primary rounded-full animate-spin"></div>
    </div>

    <!-- Media Preview Dialog -->
    <el-dialog v-model="mediaPreviewVisible" 
               width="100%" 
               fullscreen
               destroy-on-close 
               align-center
               append-to-body
               modal-class="media-preview-overlay"
               class="media-preview-dialog custom-glass-dialog">
      <div class="h-screen w-screen bg-transparent flex items-center justify-center relative p-12 overflow-hidden" @click="mediaPreviewVisible = false">
        <div class="max-w-7xl w-full max-h-[85vh] rounded-4xl overflow-hidden shadow-profound border border-white/10 relative group" @click.stop>
          <video v-if="previewItem?.mediaType === 2" :src="previewItem.mediaUrl" controls autoplay class="w-full h-auto bg-black"></video>
          <img v-else-if="previewItem?.mediaType === 1" :src="previewItem.mediaUrl" class="w-full h-auto object-contain bg-black/20" />
          
          <button @click="mediaPreviewVisible = false" 
                  class="absolute top-6 right-6 p-4 bg-white/10 hover:bg-white/20 backdrop-blur-2xl rounded-full text-white transition-all z-50 border border-white/10">
            <X :size="28" />
          </button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { 
  Eye, Download, Star, Telescope, Puzzle, Cpu, MessageCircleQuestion, 
  CheckCircle, Smartphone, Check, Zap, UserCheck, X, ShieldCheck, Lock, CheckCircle2, ArrowRight,
  MonitorDot, Code2, BookOpen, Video, Image, MessageCircle, VideoOff, Database, PlayCircle, Maximize2
} from 'lucide-vue-next'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const route = useRoute()
const api = useApi()
const authStore = useAuthStore()
const activeTab = ref('overview')

const { data: res } = await useAsyncData(`project-${route.params.id}`, () => api(`/api/projects/${route.params.id}`))
const project = computed(() => res.value?.data)

// Add to browsing history
onMounted(async () => {
  if (authStore.isLoggedIn && project.value?.id) {
    try {
      await api(`/api/history/add/${project.value.id}`, { method: 'POST' })
    } catch (e) {
      console.error('Failed to add browsing history:', e)
    }
  }
})

// Fetch similar projects
const recRes = project.value?.categoryId ? await useAsyncData(`similar-${route.params.id}`, () => 
  api('/api/projects', { params: { categoryId: project.value?.categoryId, pageSize: 3 } })
) : { data: ref(null) }
const similarProjects = computed(() => recRes.value?.data?.records?.filter(p => p.id !== project.value?.id).slice(0, 2) || [])

const techTags = computed(() => project.value?.techStack?.split(',') || ['Academic Excellence', 'Cloud Native'])

const mediaItems = computed(() => {
  if (!project.value) return []
  const videos = project.value.medias?.filter(m => m.mediaType === 2) || []
  const images = project.value.medias?.filter(m => m.mediaType === 1) || []
  const results = [...videos, ...images]
  // Fallback to cover image if no media available
  if (results.length === 0) {
    results.push({ 
      mediaType: 1, 
      mediaUrl: project.value.coverImage || 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?q=80&w=2070&auto=format&fit=crop' 
    })
  }
  return results
})

const tabOptions = computed(() => [
  { id: 'overview', label: '项目总览', icon: Telescope },
  { id: 'detail', label: '深度解析', icon: BookOpen },
  { id: 'specs', label: '技术细节', icon: Cpu },
  { id: 'ask', label: '在线咨询', icon: MessageCircleQuestion }
])

const mediaPreviewVisible = ref(false)
const previewItem = ref(null)

const handleMediaClick = (item, index) => {
  previewItem.value = item
  mediaPreviewVisible.value = true
}

const imageUrls = computed(() => mediaItems.value.filter(m => m.mediaType === 1).map(m => m.mediaUrl))
const getImageIndex = (url) => imageUrls.value.indexOf(url)

const groupedTechDetails = computed(() => {
  if (!project.value?.techDetails) return {}
  return project.value.techDetails.reduce((groups, item) => {
    const groupName = item.groupName || '其他技术'
    if (!groups[groupName]) groups[groupName] = []
    groups[groupName].push(item)
    return groups
  }, {})
})

const getTechIcon = (name) => {
  if (name.includes('后')) return Cpu
  if (name.includes('前')) return MonitorDot
  if (name.includes('数据库')) return Database
  if (name.includes('工具')) return Zap
  return Puzzle
}

const consultForm = reactive({
  content: '',
  contactWay: ''
})
const submittingConsult = ref(false)

const submitConsultation = async () => {
  if (!authStore.isLoggedIn) {
    alert('请先登录后提交咨询')
    return
  }
  submittingConsult.value = true
  try {
    await api('/api/consultations', {
      method: 'POST',
      body: {
        projectId: project.value.id,
        content: consultForm.content,
        contactWay: consultForm.contactWay
      }
    })
    alert('咨询已收到，我们将尽快联系您！')
    consultForm.content = ''
    consultForm.contactWay = ''
    activeTab.value = 'overview'
  } catch (e) {
    alert('提交失败，请稍后重试')
  } finally {
    submittingConsult.value = false
  }
}

const serviceFeatures = [
  { icon: MonitorDot, title: '1-on-1 真人演示', desc: '购买后可预约远程演示，讲解项目架构与核心链路。' },
  { icon: Code2, title: '全套环境搭建', desc: '提供全套环境包及自动部署脚本。' },
  { icon: BookOpen, title: '高分论文参考', desc: '涵盖架构设计与算法说明的高质量文档。' },
  { icon: Zap, title: '工业级验证', desc: '核心算法已通过压力测试，代码规范。' }
]

const functionalModules = [
  '精细化权限管理系统',
  '可视化数据看板与报表',
  '异步消息处理集成',
  '响应式管理界面'
]

const inclusions = [
  '全套源码（前后端分离架构）',
  '数据库初始化脚本（MySQL 8）',
  '详细环境部署手册',
  '毕业论文参考文档'
]

const handleContact = () => {
  alert('获取毕设项目内容及源码，请添加站长微信：GradBox_Help\n私信项目ID：' + project.value.id)
}
</script>



<style scoped>
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

:deep(.custom-carousel .el-carousel__arrow) {
  width: auto;
  height: auto;
  background-color: transparent !important;
  backdrop-filter: none;
  border: none;
  color: white !important;
  font-size: 48px; /* Bigger arrow for better visibility */
  transition: opacity 0.3s;
  box-shadow: none;
  opacity: 0.6;
}

:deep(.custom-carousel .el-carousel__arrow:hover) {
  opacity: 1;
}
</style>

<style>
/* Global styles for the media preview dialog overlay to ensure frosted glass effect */
.el-overlay.media-preview-overlay {
  z-index: 9999 !important;
  background-color: rgba(0, 0, 0, 0.6) !important; /* Pure black tint for better contrast */
  backdrop-filter: blur(40px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(40px) saturate(180%) !important;
}

/* Ensure the full-screen dialog itself is transparent to show the overlay's frosted glass effect */
.el-dialog.custom-glass-dialog {
  background: transparent !important;
  box-shadow: none !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.custom-glass-dialog .el-dialog__header,
.custom-glass-dialog .el-dialog__body {
  padding: 0 !important;
  background: transparent !important;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.custom-glass-dialog .el-dialog__headerbtn {
  display: none !important;
}
</style>
