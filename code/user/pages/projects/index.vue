<template>
  <div class="max-w-7xl mx-auto px-6 sm:px-8 py-24 space-y-20 relative selection:bg-primary/10">
    <!-- Background Decor -->
    <div class="absolute top-0 right-0 -z-10 w-[500px] h-[500px] bg-primary/5 rounded-full blur-[120px] opacity-40"></div>

    <!-- Header & Categories -->
    <header class="space-y-16 text-center max-w-5xl mx-auto">
      <div class="space-y-6 animate-fade-in-up">
        <h1 class="text-[clamp(2.5rem,7vw,5rem)] font-display text-profound-black tracking-tighter-profound uppercase italic leading-[0.9]">
          探索毕业设计<br />
          <span class="text-primary italic not-italic font-black drop-shadow-sm">案例中心</span>
        </h1>
        <p class="text-slate-400 text-lg font-bold max-w-2xl mx-auto leading-relaxed uppercase tracking-wider opacity-80">
          Curated Excellence · Academic Intelligence · Structural Integrity
        </p>
      </div>
      
      <!-- Search Card (Pro Max V2) -->
      <div class="glass-premium p-2 rounded-[32px] flex items-center gap-2 group max-w-2xl mx-auto transition-all focus-within:ring-8 focus-within:ring-primary/5 border border-white/40 shadow-3xl shadow-slate-200/40">
        <div class="ml-6 flex items-center gap-3 text-slate-300 group-focus-within:text-primary transition-colors">
          <Search :size="20" />
        </div>
        <input v-model="searchQuery" type="text" placeholder="Search by tech stack, keywords or major..." @keyup.enter="triggerSearch"
               class="flex-1 px-4 py-4 bg-transparent text-base font-bold text-profound-black outline-none placeholder:text-slate-300 uppercase tracking-tight" />
        
        <!-- Command Indicator -->
        <div class="hidden sm:flex items-center gap-1.5 px-3 py-1.5 bg-slate-50 border border-slate-100 rounded-xl text-[10px] font-black text-slate-300 uppercase tracking-widest mr-2">
          <span class="text-xs">⌘</span> K
        </div>

        <button @click="triggerSearch" class="btn-primary-filled h-14 px-10 rounded-2xl">
           SEARCH
        </button>
      </div>

      <!-- Advanced Filter Group (Pro Max V2) -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-2xl mx-auto pt-4">
        <div class="space-y-2 text-left group">
          <label class="label-premium flex items-center gap-2 group-focus-within:text-primary transition-colors">
            <Cpu :size="12" /> 技术架构 / Architecture
          </label>
          <div class="relative">
            <select v-model="techFilter" @change="triggerSearch"
                   class="input-premium appearance-none pr-12 cursor-pointer shadow-sm">
              <option value="">全部技术方向</option>
              <option v-for="t in techOptions" :key="t" :value="t">{{ t }}</option>
            </select>
            <ChevronDown class="absolute right-6 top-1/2 -translate-y-1/2 text-slate-300 pointer-events-none group-focus-within:text-primary transition-colors" :size="16" />
          </div>
        </div>
        <div class="space-y-2 text-left group">
          <label class="label-premium flex items-center gap-2 group-focus-within:text-primary transition-colors">
            <GraduationCap :size="12" /> 适用专业 / Major
          </label>
          <div class="relative">
            <select v-model="majorFilter" @change="triggerSearch"
                   class="input-premium appearance-none pr-12 cursor-pointer shadow-sm">
              <option value="">全部专业门类</option>
              <option v-for="m in majorOptions" :key="m" :value="m">{{ m }}</option>
            </select>
            <ChevronDown class="absolute right-6 top-1/2 -translate-y-1/2 text-slate-300 pointer-events-none group-focus-within:text-primary transition-colors" :size="16" />
          </div>
        </div>
      </div>

      <!-- Segmented Categories (Pro Max V2) -->
      <div class="flex items-center justify-center pt-8">
        <div class="inline-flex p-1.5 bg-slate-100 border border-slate-200/50 rounded-2xl gap-1">
          <button v-for="cat in categories" :key="cat.id"
                  @click="activeCategory = cat.id; triggerSearch()"
                  class="px-6 py-2.5 rounded-xl text-[11px] font-bold transition-all duration-500 uppercase tracking-widest whitespace-nowrap"
                  :class="activeCategory === cat.id ? 'bg-white text-profound-black shadow-lg scale-[1.02] border-subtle' : 'text-slate-400 hover:text-slate-600'">
            {{ cat.name }}
          </button>
        </div>
      </div>
    </header>

    <!-- Project Grid -->
    <div v-if="pending" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-10">
      <div v-for="i in 6" :key="i" class="h-[400px] bg-white rounded-5xl border border-slate-50 animate-pulse overflow-hidden shadow-sm">
        <div class="h-56 bg-slate-50"></div>
        <div class="p-8 space-y-4">
          <div class="h-6 w-3/4 bg-slate-50 rounded-lg"></div>
          <div class="h-4 w-1/2 bg-slate-50 rounded-lg"></div>
        </div>
      </div>
    </div>
    
    <div v-else-if="projects.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-10">
      <ProjectCard v-for="(project, index) in projects" 
                   :key="project.id" 
                   :project="project"
                   class="reveal-item"
                   :style="{ animationDelay: `${(index % 3) * 100}ms` }" />
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-32 bg-slate-50/50 rounded-5xl border border-dashed border-slate-200 flex flex-col items-center justify-center space-y-6">
      <div class="w-20 h-20 bg-white rounded-full flex items-center justify-center text-slate-200 shadow-sm">
        <SearchX :size="32" />
      </div>
      <div class="space-y-1">
        <h3 class="text-xl font-display text-profound-black uppercase">未发现匹配项</h3>
        <p class="text-slate-400 font-bold text-xs uppercase tracking-profound">Try adjusting filters or keywords</p>
      </div>
      <button @click="resetFilters" class="px-8 py-4 bg-profound-black text-white rounded-2xl font-bold text-xs hover:bg-primary transition-all active:scale-95 shadow-xl flex items-center gap-2 uppercase tracking-profound">
        <RefreshCcw :size="14" /> 重置所有筛选
      </button>
    </div>

    <!-- Help Banner -->
    <section class="mt-24 p-12 sm:p-20 bg-profound-black rounded-6xl text-center space-y-8 relative overflow-hidden group border border-white/5 shadow-3xl shadow-primary/10">
       <div class="absolute inset-0 opacity-[0.03] pointer-events-none">
          <div class="grid grid-cols-12 h-full">
             <div v-for="i in 144" :key="i" class="border-[0.5px] border-white"></div>
          </div>
       </div>
       <div class="absolute -top-32 -right-32 w-80 h-80 bg-primary rounded-full blur-[120px] opacity-20 group-hover:opacity-30 transition-opacity"></div>
       
       <div class="space-y-4 relative z-10">
         <h2 class="text-4xl md:text-5xl font-display text-white tracking-tighter-profound uppercase italic">还没找到心仪的项目？</h2>
         <p class="text-slate-400 text-lg font-medium max-w-2xl mx-auto">联系我们的毕业设计顶级导师，为您定制专属方案。</p>
       </div>
       <div class="relative z-10 pt-4">
         <NuxtLink to="/services" class="inline-flex items-center gap-2 px-10 py-5 bg-primary hover:bg-white hover:text-profound-black text-white rounded-3xl font-bold text-sm shadow-2xl shadow-primary/20 transition-all hover:-translate-y-1 uppercase tracking-profound">
           开启定制之旅 <ArrowRight :size="18" />
         </NuxtLink>
       </div>
    </section>
  </div>
</template>

<script setup>
import { Search, ArrowRight, Cpu, GraduationCap, ChevronDown, SearchX, RefreshCcw } from 'lucide-vue-next'

const api = useApi()
const route = useRoute()
const router = useRouter()

const searchQuery = ref(route.query.q || '')
const activeCategory = ref(route.query.categoryId ? parseInt(route.query.categoryId) : null)
const techFilter = ref(route.query.techStack || '')
const majorFilter = ref(route.query.major || '')

const techOptions = ['Spring Boot', 'Vue.js', 'Python', 'Machine Learning', 'React', 'Mobile App', '小程序', 'Microservices', 'Node.js']
const majorOptions = ['计算机科学与技术', '软件工程', '信息管理与信息系统', '人工智能', '大数据', '物联网', '电子商务']

const { data: projRes, pending, refresh } = await useAsyncData('projects-page', () => 
  api('/api/projects', { 
    params: { 
      keyword: searchQuery.value,
      categoryId: activeCategory.value,
      techStack: techFilter.value,
      major: majorFilter.value,
      pageSize: 50
    } 
  })
)

const { data: catRes } = await useAsyncData('categories-all', () => api('/api/categories'))

const projects = computed(() => projRes.value?.data?.records || [])
const categories = computed(() => [{ id: null, name: '全部' }, ...(catRes.value?.data || [])])

const triggerSearch = () => {
  // Sync URL
  const query = {}
  if (searchQuery.value) query.q = searchQuery.value
  if (activeCategory.value) query.categoryId = activeCategory.value
  if (techFilter.value) query.techStack = techFilter.value
  if (majorFilter.value) query.major = majorFilter.value
  
  router.push({ path: '/projects', query })
  refresh()
}

const resetFilters = () => {
  searchQuery.value = ''
  activeCategory.value = null
  techFilter.value = ''
  majorFilter.value = ''
  router.push({ path: '/projects', query: {} })
  refresh()
}

// Watch route for external changes (like clicking a header link)
watch(() => route.query, (newQuery) => {
  searchQuery.value = newQuery.q || ''
  activeCategory.value = newQuery.categoryId ? parseInt(newQuery.categoryId) : null
  techFilter.value = newQuery.techStack || ''
  majorFilter.value = newQuery.major || ''
  refresh()
}, { deep: true })
</script>
