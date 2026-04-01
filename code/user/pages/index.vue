<template>
  <div class="relative overflow-hidden">
    <!-- Sophisticated Mesh Background -->
    <div class="absolute top-0 left-0 w-full h-[1000px] pointer-events-none opacity-40">
      <div class="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-blue-400/20 rounded-full blur-[120px] animate-pulse"></div>
      <div class="absolute top-[20%] right-[-5%] w-[35%] h-[35%] bg-indigo-400/20 rounded-full blur-[120px] animate-float"></div>
      <div class="absolute bottom-0 left-[20%] w-[30%] h-[30%] bg-purple-400/10 rounded-full blur-[120px]"></div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative pt-20 pb-32">
      <!-- Hero Section -->
      <section class="text-center mb-24 space-y-8 animate-fade-in-up">
        <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-blue-50 text-blue-600 text-xs font-black uppercase tracking-widest border border-blue-100 mb-4">
          <LucideSparkles :size="14" /> 2024 毕业设计精选库
        </div>
        
        <h1 class="text-6xl md:text-8xl font-black text-slate-900 tracking-[calc(-0.02em)] leading-[0.95]">
          成就卓越 <br />
          <span class="text-transparent bg-clip-text bg-gradient-to-br from-blue-600 via-indigo-600 to-indigo-800">毕业作品</span>
        </h1>
        
        <p class="text-slate-500 text-xl max-w-2xl mx-auto font-medium leading-relaxed">
          汇集计算机专业 P0 级精选课题，包含从高质源码到全套论文的闭环指导，让每一份交付都超越期待。
        </p>

        <div class="flex flex-col sm:flex-row justify-center items-center gap-4 pt-6">
          <NuxtLink to="/projects" class="btn-premium w-full sm:w-auto">
            立即探索项目库 <LucideArrowRight :size="20" />
          </NuxtLink>
          <NuxtLink to="/services" class="px-8 py-3 rounded-2xl bg-white text-slate-900 font-bold border border-slate-200 hover:border-blue-600 transition-all hover:bg-slate-50 flex items-center gap-2">
            定制开发咨询
          </NuxtLink>
        </div>
        
        <!-- Trust Badges -->
        <div class="pt-12 flex flex-wrap justify-center items-center gap-8 opacity-40 grayscale hover:grayscale-0 transition-all">
          <div v-for="i in 4" :key="i" class="flex items-center gap-2 font-black text-lg">
             <LucideShieldCheck :size="24" /> TRUSTED
          </div>
        </div>
      </section>

      <!-- Search Section -->
      <section class="max-w-4xl mx-auto mb-32 -mt-12 relative z-10">
        <div class="glass-card p-4 rounded-[40px] flex items-center gap-4 group">
          <div class="pl-6 text-slate-400 group-focus-within:text-blue-600 transition-colors">
            <LucideSearch :size="24" />
          </div>
          <input type="text" v-model="searchQuery" placeholder="搜索关键词，如：Java, Python, 大数据, 微信小程序..." 
                 class="flex-1 bg-transparent py-6 text-xl font-bold text-slate-800 outline-none placeholder:text-slate-300" />
          <button @click="handleSearch" class="bg-slate-900 text-white px-8 py-5 rounded-[32px] font-black hover:bg-blue-600 transition-all">
            开始检索
          </button>
        </div>
      </section>

      <!-- Featured Grid -->
      <section class="space-y-12">
        <div class="flex justify-between items-end">
          <div class="space-y-2">
            <h2 class="text-4xl font-black text-slate-900">本周热门推荐</h2>
            <p class="text-slate-400 font-bold">精选近期需求度最高的精品案例</p>
          </div>
          <NuxtLink to="/projects" class="text-blue-600 font-black flex items-center gap-1 hover:gap-2 transition-all">
            查看更多 <LucideChevronRight :size="20" />
          </NuxtLink>
        </div>

        <div v-if="pending" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
          <div v-for="i in 3" :key="i" class="h-96 bg-white/50 rounded-[40px] border border-slate-100 animate-pulse"></div>
        </div>
        
        <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
          <ProjectCard v-for="(project, index) in featuredProjects" 
                       :key="project.id" 
                       :project="project"
                       class="reveal-item"
                       :style="{ animationDelay: `${index * 150}ms` }" />
        </div>
      </section>

      <!-- Services Pulsar -->
      <section class="mt-40 relative group">
        <div class="absolute inset-0 bg-blue-600 rounded-[48px] rotate-1 group-hover:rotate-0 transition-transform duration-500 shadow-2xl shadow-blue-200"></div>
        <div class="relative bg-slate-900 rounded-[48px] p-12 md:p-20 overflow-hidden flex flex-col md:flex-row items-center gap-12 border border-slate-800/50">
          <div class="absolute top-0 right-0 w-96 h-96 bg-blue-500/10 rounded-full blur-[100px]"></div>
          
          <div class="flex-1 space-y-8 relative z-10 text-center md:text-left">
            <h2 class="text-5xl font-black text-white leading-tight">不仅提供源码，<br /><span class="text-blue-400">更贴心的全程辅导</span></h2>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-8 text-left">
              <div v-for="item in features" :key="item.title" class="space-y-2">
                <div class="w-12 h-12 bg-white/10 rounded-2xl flex items-center justify-center text-blue-400">
                  <component :is="item.icon" :size="24" />
                </div>
                <h4 class="text-xl font-bold text-white">{{ item.title }}</h4>
                <p class="text-slate-400 text-sm leading-relaxed">{{ item.desc }}</p>
              </div>
            </div>
            <div class="pt-4">
              <NuxtLink to="/services" class="px-10 py-5 bg-blue-600 text-white rounded-full font-black text-lg inline-block hover:scale-105 active:scale-95 transition-all">
                立即咨询定制服务
              </NuxtLink>
            </div>
          </div>
          
          <div class="relative w-full md:w-[400px] h-[400px] flex-shrink-0">
             <div class="absolute inset-0 bg-white/5 rounded-full animate-float"></div>
             <div class="absolute inset-0 flex items-center justify-center">
                <LucideGraduationCap class="text-white opacity-20" :size="200" />
                <div class="absolute w-40 h-40 bg-blue-600/20 blur-3xl animate-pulse"></div>
             </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
const api = useApi()
const searchQuery = ref('')
const router = useRouter()

const { data: res, pending } = await useAsyncData('featured-projects', () => 
  api('/api/projects', { params: { pageSize: 3, sort: 'views' } })
)

const featuredProjects = computed(() => res.value?.data?.records || [])

const handleSearch = () => {
  if (!searchQuery.value.trim()) return
  router.push({ path: '/projects', query: { q: searchQuery.value } })
}

const features = [
  { icon: resolveComponent('LucideCode'), title: '生产级源码', desc: '每一个变量名都经过考究，标准工程化结构。' },
  { icon: resolveComponent('LucideFileText'), title: '论文极速包', desc: '含开题报告、万字论文、答辩 PPT、演示视频。' },
  { icon: resolveComponent('LucideMonitor'), title: '1-on-1 调试', desc: '专业导师远程协助，解决您的环境兼容性阵痛。' },
  { icon: resolveComponent('LucideShieldCheck'), title: '终身质保', desc: '项目上线后如有架构变动或报错，免费提供修正。' }
]
</script>
