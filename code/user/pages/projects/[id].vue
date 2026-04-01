<template>
  <div v-if="project" class="min-h-screen bg-white">
    <!-- Immersive Header Section -->
    <section class="relative h-[70vh] overflow-hidden bg-slate-900 group">
      <div class="absolute inset-0">
        <img :src="project.coverImage || 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?q=80&w=2070&auto=format&fit=crop'" 
             class="w-full h-full object-cover opacity-30 group-hover:scale-105 transition-transform duration-[2s]" />
        <div class="absolute inset-0 bg-gradient-to-t from-white via-transparent to-transparent"></div>
        <div class="absolute inset-0 bg-gradient-to-r from-slate-900/90 via-slate-900/40 to-transparent"></div>
      </div>

      <div class="relative h-full max-w-7xl mx-auto px-4 flex flex-col justify-center space-y-10">
        <div class="flex items-center gap-4 animate-fade-in">
          <span class="px-6 py-2 bg-blue-600 text-white text-[10px] font-black uppercase tracking-[0.3em] rounded-full shadow-2xl shadow-blue-500/40">
            {{ project.categoryName || '精品项目' }}
          </span>
          <span v-if="project.major" class="px-6 py-2 bg-white/10 backdrop-blur-md text-white text-[10px] font-black uppercase tracking-[0.3em] rounded-full border border-white/20">
            {{ project.major }}
          </span>
          <span class="text-white/30 font-mono text-xs tracking-widest">REF_ID: #{{ project.id.toString().padStart(6, '0') }}</span>
        </div>
        
        <h1 class="text-7xl md:text-9xl font-black text-white tracking-tighter leading-[0.85] max-w-5xl animate-fade-in-up">
          {{ project.title }}
        </h1>

        <div class="flex flex-wrap gap-4 items-center animate-fade-in" style="animation-delay: 200ms">
          <div v-for="tag in techTags" :key="tag" 
               class="px-6 py-3 bg-white/5 backdrop-blur-xl border border-white/10 text-white rounded-2xl text-sm font-bold hover:bg-white/20 transition-all cursor-default">
            {{ tag }}
          </div>
        </div>

        <!-- Quick Stats Overlay -->
        <div class="flex gap-12 pt-8 text-white/60 font-black uppercase tracking-widest text-[10px]">
          <div class="flex items-center gap-2"><Eye :size="16" class="text-blue-400" /> {{ project.viewCount }} 累计浏览</div>
          <div class="flex items-center gap-2"><Download :size="16" class="text-blue-400" /> {{ project.downloadCount || 0 }} 成功下载</div>
          <div class="flex items-center gap-2"><Star :size="16" class="text-blue-400" /> 4.9 综合评分</div>
        </div>
      </div>
    </section>

    <!-- Main Content Area -->
    <div class="max-w-7xl mx-auto px-4 -mt-32 relative z-10 pb-48">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-12 items-start">
        <!-- Left: Structured Content with Tabs -->
        <div class="lg:col-span-2 space-y-12">
          <div class="glass-card bg-white p-2 rounded-[48px] shadow-2xl shadow-slate-200/60 overflow-hidden border border-slate-100">
            <!-- Professional Custom Tabs -->
            <div class="flex p-4 border-b border-slate-50 gap-2 overflow-x-auto no-scrollbar">
              <button v-for="tab in tabOptions" :key="tab.id"
                      v-show="tab.show !== false"
                      @click="activeTab = tab.id"
                      class="px-10 py-5 rounded-[32px] text-sm font-black transition-all duration-500 whitespace-nowrap flex items-center gap-2"
                      :class="activeTab === tab.id ? 'bg-slate-900 text-white shadow-xl translate-y-[-2px]' : 'text-slate-400 hover:text-slate-900 hover:bg-slate-50'">
                <component :is="tab.icon" :size="18" />
                {{ tab.label }}
              </button>
            </div>

            <div class="p-10 md:p-14 min-h-[500px]">
              <!-- Overview Tab -->
              <div v-if="activeTab === 'overview'" class="space-y-12 animate-fade-in">
                <div class="space-y-6">
                  <h3 class="text-3xl font-black text-slate-900">项目背景与价值</h3>
                  <p class="text-xl text-slate-600 font-medium leading-relaxed italic border-l-4 border-blue-600 pl-8">
                    {{ project.description }}
                  </p>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                  <div v-for="feat in serviceFeatures" :key="feat.title" 
                       class="p-10 rounded-[40px] bg-slate-50 border border-slate-100 space-y-4 group hover:bg-blue-600 transition-all duration-700">
                    <div class="w-14 h-14 rounded-2xl bg-white shadow-sm flex items-center justify-center text-blue-600 group-hover:scale-110 group-hover:bg-white transition-all">
                      <component :is="feat.icon" :size="28" />
                    </div>
                    <h4 class="text-2xl font-black text-slate-900 group-hover:text-white transition-colors">{{ feat.title }}</h4>
                    <p class="text-slate-500 group-hover:text-white/80 transition-colors text-sm font-medium leading-relaxed">{{ feat.desc }}</p>
                  </div>
                </div>
              </div>

              <!-- Features Tab -->
              <div v-if="activeTab === 'features'" class="space-y-12 animate-fade-in">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
                  <div class="space-y-6">
                    <h4 class="text-2xl font-black text-slate-900 flex items-center gap-3">
                      <CheckCircle class="text-green-500" /> 核心功能模块
                    </h4>
                    <ul class="space-y-4">
                      <li v-for="item in functionalModules" :key="item" class="flex gap-4 p-5 bg-slate-50 rounded-3xl border border-slate-100 group hover:border-blue-200 transition-all">
                        <div class="w-2 h-2 rounded-full bg-blue-600 mt-2"></div>
                        <span class="text-slate-700 font-bold">{{ item }}</span>
                      </li>
                    </ul>
                  </div>
                  <div class="space-y-6">
                    <img :src="project.coverImage || 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?q=80&w=2070&auto=format&fit=crop'" class="w-full h-80 object-cover rounded-[40px] shadow-2xl grayscale hover:grayscale-0 transition-all duration-700" />
                    <p class="text-sm font-bold text-slate-400 text-center uppercase tracking-widest">系统业务流程预览</p>
                  </div>
                </div>
              </div>

              <!-- Tech Specs Tab -->
              <div v-if="activeTab === 'specs'" class="space-y-12 animate-fade-in">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
                   <div class="p-10 bg-slate-900 rounded-[48px] text-white space-y-8">
                     <h4 class="text-2xl font-black flex items-center gap-3"><Cpu class="text-blue-400" /> 后端架构</h4>
                     <div class="grid grid-cols-2 gap-4">
                        <div v-for="s in ['SpringBoot 3.x', 'MyBatis Plus', 'Spring Security', 'JWT Token', 'Redis Cache', 'MySQL 8.x']" :key="s" class="p-4 bg-white/5 rounded-2xl text-[10px] uppercase font-black tracking-widest border border-white/10">{{ s }}</div>
                     </div>
                   </div>
                   <div class="p-10 bg-blue-600 rounded-[48px] text-white space-y-8">
                     <h4 class="text-2xl font-black flex items-center gap-3"><Smartphone class="text-white" /> 前端技术</h4>
                     <div class="grid grid-cols-2 gap-4">
                        <div v-for="s in ['Vue 3 (Composition)', 'Nuxt 3 Framework', 'Tailwind CSS 3', 'Pinia State', 'Element Plus', 'Lucide Icons']" :key="s" class="p-4 bg-white/10 rounded-2xl text-[10px] uppercase font-black tracking-widest border border-white/20">{{ s }}</div>
                     </div>
                   </div>
                </div>
              </div>

              <!-- Video Tab -->
              <div v-if="activeTab === 'video'" class="space-y-12 animate-fade-in">
                <div v-if="project.medias?.some(m => m.mediaType === 2)" class="space-y-6">
                  <div v-for="video in project.medias.filter(m => m.mediaType === 2)" :key="video.id" class="aspect-video rounded-[32px] overflow-hidden bg-black shadow-2xl">
                    <video :src="video.mediaUrl" controls class="w-full h-full object-contain"></video>
                  </div>
                </div>
                <div v-else class="py-20 text-center space-y-6">
                   <div class="w-20 h-20 bg-slate-50 rounded-full flex items-center justify-center mx-auto">
                     <VideoOff :size="32" class="text-slate-300" />
                   </div>
                   <p class="text-slate-400 font-bold">暂无演示视频</p>
                </div>
              </div>

              <!-- Media Tab -->
              <div v-if="activeTab === 'media'" class="space-y-12 animate-fade-in">
                <div v-if="project.medias?.some(m => m.mediaType === 1)" class="grid grid-cols-1 md:grid-cols-2 gap-8">
                  <div v-for="image in project.medias.filter(m => m.mediaType === 1)" :key="image.id" class="rounded-[32px] overflow-hidden shadow-xl border border-slate-100">
                    <img :src="image.mediaUrl" class="w-full h-auto object-cover hover:scale-105 transition-transform duration-700" />
                  </div>
                </div>
                <div v-else class="py-20 text-center space-y-6">
                   <div class="w-20 h-20 bg-slate-50 rounded-full flex items-center justify-center mx-auto">
                     <Image :size="32" class="text-slate-300" />
                   </div>
                   <p class="text-slate-400 font-bold">暂无展示图片</p>
                </div>
              </div>

              <!-- Q&A Tab -->
              <div v-if="activeTab === 'qa'" class="space-y-8 animate-fade-in">
                 <div v-for="(q, i) in faqItems" :key="i" class="p-8 bg-slate-50 rounded-[32px] border border-slate-100 space-y-3">
                   <h4 class="text-lg font-black text-slate-900 flex gap-4">
                     <span class="text-blue-600">Q:</span> {{ q.question }}
                   </h4>
                   <p class="text-slate-500 font-medium pl-8 border-l-2 border-slate-200 ml-2">
                     <span class="text-slate-900 mr-2 uppercase text-[10px] font-black">Answer:</span> {{ q.answer }}
                   </p>
                 </div>
              </div>
            </div>
          </div>

          <!-- Bottom: Recommended Projects -->
          <div class="space-y-10 pt-20">
            <div class="flex justify-between items-end">
              <div class="space-y-4">
                <h3 class="text-5xl font-black text-slate-900 tracking-tighter italic">探索类似案例</h3>
                <p class="text-slate-400 font-bold uppercase tracking-widest text-xs">基于您的课题方向深度推荐</p>
              </div>
              <NuxtLink to="/projects" class="text-blue-600 font-black hover:underline flex items-center gap-2">查看全库 <ArrowRight :size="16" /></NuxtLink>
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
              <ProjectCard v-for="p in similarProjects" :key="p.id" :project="p" />
            </div>
          </div>
        </div>

        <!-- Right: Action Sidebar -->
        <div class="space-y-8">
          <div class="sticky top-28 space-y-8">
            <div class="glass-card bg-slate-900 p-12 rounded-[56px] shadow-[0_50px_100px_-20px_rgba(30,41,59,0.5)] text-white space-y-10 relative overflow-hidden">
              <div class="absolute -top-12 -right-12 w-48 h-48 bg-blue-600 rounded-full blur-[100px] opacity-20"></div>
              
              <div class="space-y-2 relative z-10">
                <div class="flex justify-between items-center">
                  <span class="text-slate-500 font-black uppercase tracking-[0.3em] text-[10px]">源码参考包全集</span>
                  <span class="px-4 py-1 bg-green-500/20 text-green-400 text-[10px] font-black rounded-lg border border-green-500/30">现货即发</span>
                </div>
                <div class="flex items-baseline gap-2">
                  <span class="text-4xl font-black opacity-30 tracking-tighter">¥</span>
                  <span class="text-8xl font-black tracking-tighter">{{ project.price }}</span>
                </div>
              </div>

              <div class="space-y-5 pt-4 relative z-10">
                <div v-for="item in inclusions" :key="item" class="flex items-start gap-4 text-sm font-bold text-slate-400 group">
                  <div class="mt-1 w-5 h-5 rounded-full bg-blue-500/20 flex items-center justify-center group-hover:bg-blue-500 transition-colors">
                    <Check :size="12" class="text-blue-500 group-hover:text-white" />
                  </div>
                  {{ item }}
                </div>
              </div>

              <button @click="handleContact" 
                      class="w-full py-7 bg-blue-600 hover:bg-blue-700 text-white rounded-[32px] font-black text-xl shadow-2xl shadow-blue-500/40 transition-all hover:-translate-y-1 active:scale-95 flex items-center justify-center gap-3 relative z-10 group overflow-hidden">
                 <div class="absolute inset-0 bg-white opacity-0 group-hover:opacity-10 transition-opacity"></div>
                 <MessageCircle :size="24" /> 联系站长获取
              </button>

              <p class="text-center text-[10px] font-black text-slate-600 uppercase tracking-widest relative z-10">
                添加微信私信获取本项目源码与文档
              </p>
            </div>

            <!-- Side Card: Consultation (Premium Version) -->
            <div class="p-12 bg-white rounded-[56px] border border-slate-100 shadow-2xl shadow-slate-200/40 space-y-10 group">
              <div class="space-y-4 text-center">
                <div class="w-20 h-20 bg-blue-50 rounded-[28px] flex items-center justify-center mx-auto group-hover:rotate-12 transition-transform">
                  <UserCheck :size="32" class="text-blue-600" />
                </div>
                <div class="space-y-2">
                  <h4 class="text-2xl font-black text-slate-900 leading-tight">需要深度修改？</h4>
                  <p class="text-slate-400 font-bold text-sm leading-relaxed px-4">无论是个性化功能定制、论文润色还是远程线上部署，我们的导师团队随时待命。</p>
                </div>
              </div>
              
              <NuxtLink to="/services" class="w-full py-6 rounded-[32px] bg-slate-900 text-white font-black hover:bg-blue-600 transition-all text-center block shadow-2xl shadow-slate-900/20">
                预约精英导师
              </NuxtLink>

              <div class="flex items-center justify-center gap-4 pt-2">
                <div class="flex -space-x-3">
                  <div v-for="i in 4" :key="i" class="w-10 h-10 rounded-full border-4 border-white bg-slate-100 overflow-hidden shadow-sm">
                    <img :src="`https://i.pravatar.cc/150?u=mentor-${i+project.id}`" class="w-full h-full object-cover" />
                  </div>
                </div>
                <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest leading-none">
                  24/7 在线服务
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Purchase Confirmation Modal (Custom Tailwind) -->
    <div v-if="showConfirm" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="showConfirm = false"></div>
      <div class="relative w-full max-w-lg bg-white rounded-[60px] shadow-2xl overflow-hidden animate-scale-in">
        <div class="p-12 space-y-8 border border-slate-100">
          <div class="flex justify-between items-center">
            <h3 class="text-3xl font-black text-slate-900 tracking-tight">订单确认</h3>
            <button @click="showConfirm = false" class="text-slate-400 hover:text-slate-900 transition-colors"><X :size="24" /></button>
          </div>

          <div class="p-8 bg-slate-50 rounded-[40px] flex items-center gap-6 border border-slate-100">
            <div class="relative w-24 h-24 flex-shrink-0">
              <img :src="project.coverImage" class="w-full h-full rounded-2xl object-cover shadow-lg" />
              <div class="absolute -top-2 -right-2 w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center ring-4 ring-white">
                <Check :size="16" class="text-white" />
              </div>
            </div>
            <div class="flex-1 space-y-1">
              <h4 class="font-black text-slate-900 text-lg line-clamp-2 italic tracking-tighter">{{ project.title }}</h4>
              <div class="flex items-baseline gap-1 pt-1">
                <span class="text-xs font-black text-blue-600 opacity-60 leading-none">¥</span>
                <span class="text-4xl font-black text-blue-600 tracking-tighter leading-none">{{ project.price }}</span>
              </div>
            </div>
          </div>
          
          <div class="space-y-4 px-2">
            <div class="flex items-center gap-4 text-slate-500 text-sm font-bold">
              <div class="w-8 h-8 rounded-xl bg-green-50 flex items-center justify-center flex-shrink-0"><ShieldCheck :size="18" class="text-green-500" /></div>
              严选品质：保证源码完整可运行
            </div>
            <div class="flex items-center gap-4 text-slate-500 text-sm font-bold">
              <div class="w-8 h-8 rounded-xl bg-blue-50 flex items-center justify-center flex-shrink-0"><Lock :size="18" class="text-blue-500" /></div>
              即刻获取：支付完成后立即开通权限
            </div>
          </div>

          <button @click="confirmPurchase" :disabled="buying" 
                  class="w-full py-7 bg-slate-900 text-white rounded-[32px] font-black text-xl hover:bg-black transition-all flex items-center justify-center gap-3 shadow-2xl active:scale-95 disabled:opacity-50">
            <CheckCircle2 :size="24" :class="buying ? 'animate-bounce' : ''" /> {{ buying ? '结算中...' : '提交订单并交付' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { 
  Eye, Download, Star, Telescope, Puzzle, Cpu, MessageCircleQuestion, 
  CheckCircle, Smartphone, Check, Zap, UserCheck, X, ShieldCheck, Lock, CheckCircle2, ArrowRight,
  MonitorDot, Code2, BookOpen, Video, Image, MessageCircle, VideoOff
} from 'lucide-vue-next'

const route = useRoute()
const api = useApi()
const authStore = useAuthStore()
const showConfirm = ref(false)
const buying = ref(false)
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
const { data: recRes } = await useAsyncData(`similar-${route.params.id}`, () => 
  api('/api/projects', { params: { categoryId: project.value?.categoryId, pageSize: 3 } })
)
const similarProjects = computed(() => recRes.value?.data?.records?.filter(p => p.id !== project.value?.id).slice(0, 2) || [])

const techTags = computed(() => project.value?.techStack?.split(',') || ['Digital Yangtze Basin', 'Nuxt 3', 'Spring Boot'])

const tabOptions = computed(() => [
  { id: 'overview', label: '项目总览', icon: Telescope },
  { id: 'video', label: '视频演示', icon: Video, show: project.value?.medias?.some(m => m.mediaType === 2) },
  { id: 'media', label: '图片展示', icon: Image, show: project.value?.medias?.some(m => m.mediaType === 1) },
  { id: 'specs', label: '技术细节', icon: Cpu },
  { id: 'qa', label: '常见问答', icon: MessageCircleQuestion }
])

const serviceFeatures = [
  { icon: MonitorDot, title: '1-on-1 真人演示', desc: '购买后可预约 30 分钟远程演示，讲解项目架构与核心链路。' },
  { icon: Code2, title: '零成本环境搭建', desc: '提供全套环境包 (JDK, MySQL, Redis) 及自动部署脚本。' },
  { icon: BookOpen, title: '万字配套论文', desc: '附赠同课题高分毕业论文一份，涵盖架构设计与算法说明。' },
  { icon: Zap, title: '工业级性能验证', desc: '核心算法已通过高并发压力测试，代码注释率超过 45%。' }
]

const functionalModules = [
  '基于 SpringSecurity 的权限精细化管理系统',
  '可视化数据看板与报表自动生成模块',
  '分布式任务调度与异步消息处理集成',
  '前后端分离的响应式管理界面',
  '系统操作日志全流程审计系统'
]

const faqItems = [
  { question: '购买后的参考包包含哪些内容？', answer: '包含完整前后端源码、数据库初始化脚本、环境搭建手册、万字配套论文文档以及我们的导师在线支持说明。' },
  { question: '我觉得功能不够，可以申请二次开发吗？', answer: '完全可以！您可以点击侧边栏的“预约精英导师”，提交您的具体修改需求，我们的导师会为您正式报价。' },
  { question: '我的电脑环境很复杂，能帮忙部署吗？', answer: '当然。我们提供付费部署服务，由专业技术专家远程为您配置好全套运行环境，保证项目正常流转。' }
]

const inclusions = [
  '全套源码（前后端分离最新架构）',
  'MyBatis Plus 自动代码生成工具',
  'MySQL 8.0 物理模型及初始化数据',
  '万字高分参考论文 (可编辑版)',
  '微信/钉钉 7x12 小时技术解惑'
]

const handleContact = () => {
  // Show WeChat info or redirect to contact page
  alert('获取毕设项目内容及源码，请添加站长微信：[这里替换为您的微信号]\n私信项目ID：' + project.value.id)
}
</script>

<style>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
@keyframes scale-in {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
.animate-scale-in {
  animation: scale-in 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}
</style>
