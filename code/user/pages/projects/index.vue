<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 space-y-16 relative">
    <!-- Background Decor -->
    <div class="absolute top-0 right-0 -z-10 w-96 h-96 bg-blue-100 rounded-full blur-[100px] opacity-50"></div>

    <!-- Header & Categories -->
    <header class="space-y-10 text-center max-w-4xl mx-auto">
      <div class="space-y-4 animate-fade-in-up">
        <h1 class="text-6xl font-black text-slate-900 tracking-tighter italic">探索毕业设计案例库</h1>
        <p class="text-slate-500 text-xl font-medium max-w-2xl mx-auto">汇集 50+ 热门技术方向，1000+ 真实案例，助您轻松开题，稳健通关。</p>
      </div>
      
      <!-- Search Card -->
      <div class="glass-card p-2 rounded-[32px] flex items-center gap-2 group max-w-2xl mx-auto shadow-2xl shadow-blue-900/5 transition-all focus-within:ring-4 focus-within:ring-blue-100 bg-white">
        <Search class="ml-6 text-slate-400" :size="24" />
        <input v-model="searchQuery" type="text" placeholder="输入技术栈或课题关键词..." @keyup.enter="triggerSearch"
               class="flex-1 px-4 py-4 bg-transparent text-lg font-bold text-slate-800 outline-none placeholder:text-slate-300" />
        <button @click="triggerSearch" class="bg-blue-600 text-white px-8 py-4 rounded-[24px] font-black hover:bg-blue-700 transition-all shadow-lg shadow-blue-200 active:scale-95 flex items-center gap-2">
           快速检索 <ArrowRight :size="18" />
        </button>
      </div>

      <!-- Advanced Filter Group -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-3xl mx-auto pt-8">
        <div class="space-y-3 text-left">
          <label class="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] ml-2 flex items-center gap-2">
            <Cpu :size="12" /> 技术架构
          </label>
          <div class="relative group/sel">
            <select v-model="techFilter" @change="triggerSearch"
                   class="w-full bg-white border border-slate-100 rounded-[20px] px-6 py-4 text-sm font-black text-slate-800 appearance-none shadow-sm hover:border-blue-400 transition-all cursor-pointer focus:outline-none focus:ring-4 focus:ring-blue-50">
              <option value="">全部技术方向</option>
              <option v-for="t in techOptions" :key="t" :value="t">{{ t }}</option>
            </select>
            <ChevronDown class="absolute right-6 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none group-hover/sel:text-blue-500 transition-colors" :size="18" />
          </div>
        </div>
        <div class="space-y-3 text-left">
          <label class="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] ml-2 flex items-center gap-2">
            <GraduationCap :size="12" /> 适用专业
          </label>
          <div class="relative group/sel">
            <select v-model="majorFilter" @change="triggerSearch"
                   class="w-full bg-white border border-slate-100 rounded-[20px] px-6 py-4 text-sm font-black text-slate-800 appearance-none shadow-sm hover:border-blue-400 transition-all cursor-pointer focus:outline-none focus:ring-4 focus:ring-blue-50">
              <option value="">全部专业门类</option>
              <option v-for="m in majorOptions" :key="m" :value="m">{{ m }}</option>
            </select>
            <ChevronDown class="absolute right-6 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none group-hover/sel:text-blue-500 transition-colors" :size="18" />
          </div>
        </div>
      </div>

      <!-- Bento Categories -->
      <div class="flex flex-wrap gap-3 justify-center pt-10">
        <button v-for="cat in categories" :key="cat.id"
                @click="activeCategory = cat.id; triggerSearch()"
                class="px-8 py-3 rounded-2xl text-xs font-black transition-all duration-300 transform border"
                :class="activeCategory === cat.id ? 'bg-slate-900 text-white shadow-xl scale-110 border-slate-900' : 'bg-white text-slate-500 border-slate-100 hover:border-blue-600 hover:bg-slate-50'">
          {{ cat.name }}
        </button>
      </div>
    </header>

    <!-- Project Masonry-like Grid -->
    <div v-if="pending" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-10">
      <div v-for="i in 9" :key="i" class="h-[450px] bg-white/50 rounded-[40px] border border-slate-100 animate-pulse overflow-hidden">
        <div class="h-64 bg-slate-100"></div>
        <div class="p-8 space-y-4">
          <div class="h-8 w-3/4 bg-slate-100 rounded-lg"></div>
          <div class="h-4 w-1/2 bg-slate-100 rounded-lg"></div>
        </div>
      </div>
    </div>
    
    <div v-else-if="filteredProjects.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-10">
      <ProjectCard v-for="(project, index) in filteredProjects" 
                   :key="project.id" 
                   :project="project"
                   class="reveal-item"
                   :style="{ animationDelay: `${(index % 3) * 100}ms` }" />
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-40 glass-card rounded-[48px] border-dashed border-slate-200 flex flex-col items-center justify-center space-y-8 bg-white/40">
      <div class="w-32 h-32 bg-slate-50 rounded-full flex items-center justify-center text-slate-200 shadow-inner">
        <SearchX :size="64" />
      </div>
      <div>
        <h3 class="text-3xl font-black text-slate-800 tracking-tighter">未发现匹配项</h3>
        <p class="text-slate-400 font-bold text-lg">尝试调整搜索词或选择其他过滤器</p>
      </div>
      <button @click="resetFilters" class="px-12 py-5 bg-slate-900 text-white rounded-[24px] font-black text-lg hover:bg-black transition-all active:scale-95 shadow-2xl flex items-center gap-3">
        <RefreshCcw :size="20" /> 重置所有过滤器
      </button>
    </div>

    <!-- Help Banner -->
    <section class="mt-32 p-16 bg-gradient-to-br from-slate-900 to-indigo-950 rounded-[64px] text-center space-y-8 relative overflow-hidden group">
       <div class="absolute inset-0 opacity-10 pointer-events-none">
         <div class="grid grid-cols-10 h-full">
            <div v-for="i in 100" :key="i" class="border-[0.5px] border-white/20"></div>
         </div>
       </div>
       <div class="absolute -top-24 -right-24 w-64 h-64 bg-blue-600 rounded-full blur-[120px] opacity-20 group-hover:opacity-40 transition-opacity"></div>
       
       <h2 class="text-5xl font-black text-white relative z-10 tracking-tighter italic">还没找到心仪的项目？</h2>
       <p class="text-slate-400 text-xl font-medium relative z-10 max-w-2xl mx-auto">联系我们的毕业设计顶级导师，为您定制专属的技术方案，从开题到答辩一站式护航。</p>
       <div class="relative z-10 pt-4">
         <NuxtLink to="/services" class="inline-flex items-center gap-3 px-12 py-6 bg-blue-600 hover:bg-blue-700 text-white rounded-[32px] font-black text-xl shadow-2xl shadow-blue-500/20 transition-all hover:-translate-y-1">
           开启定制之旅 <ArrowRight :size="24" />
         </NuxtLink>
       </div>
    </section>
  </div>
</template>

<script setup>
import { Search, ArrowRight, Cpu, GraduationCap, ChevronDown, SearchX, RefreshCcw } from 'lucide-vue-next'

const api = useApi()
const route = useRoute()
const searchQuery = ref(route.query.q || '')
const activeCategory = ref(null)
const techFilter = ref('')
const majorFilter = ref('')

const techOptions = ['Spring Boot', 'Vue.js', 'Python', 'Machine Learning', 'React', 'Mobile App', '小程序', 'Microservices', 'Node.js']
const majorOptions = ['计算机科学与技术', '软件工程', '信息管理与信息系统', '人工智能', '大数据', '物联网', '电子商务']

const { data: projRes, pending, refresh } = await useAsyncData('projects-all', () => 
  api('/api/projects', { 
    params: { 
      keyword: searchQuery.value,
      categoryId: activeCategory.value,
      techStack: techFilter.value,
      major: majorFilter.value,
      pageSize: 50
    } 
  }), {
    watch: [activeCategory]
  }
)

const { data: catRes } = await useAsyncData('categories-all', () => api('/api/categories'))

const projects = computed(() => projRes.value?.data?.records || [])
const categories = computed(() => [{ id: null, name: '全部领域' }, ...(catRes.value?.data || [])])

const filteredProjects = computed(() => projects.value)

const triggerSearch = () => {
  refresh()
}

const resetFilters = () => {
  searchQuery.value = ''
  activeCategory.value = null
  techFilter.value = ''
  majorFilter.value = ''
  refresh()
}
</script>
