<template>
  <div class="max-w-7xl mx-auto px-6 sm:px-8 py-8 sm:py-12 space-y-8 relative selection:bg-primary/10">
    <!-- Background Decor -->
    <div class="absolute top-0 right-0 -z-10 w-[300px] h-[300px] bg-primary/5 rounded-full blur-[100px] opacity-40"></div>

    <!-- Compact Header -->
    <header class="flex flex-col gap-6">
      <div class="flex flex-col xl:flex-row xl:items-center justify-between gap-4">
        <!-- Title area -->
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center text-primary shrink-0">
            <GraduationCap :size="24" />
          </div>
          <div>
            <h1 class="text-2xl font-display text-profound-black tracking-tight flex items-center gap-2">
              项目<span class="text-primary italic font-black">案例库</span>
            </h1>
          </div>
        </div>

        <!-- Integrated Search & Filter Group -->
        <div class="flex flex-col md:flex-row items-center gap-3 w-full xl:w-auto">
          <!-- Search -->
          <div class="relative w-full md:w-auto flex-1 md:flex-none">
            <Search :size="16" class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 z-10" />
            <input v-model="searchQuery" type="text" placeholder="搜索关键词、技术栈..." @keyup.enter="triggerSearch"
                   class="w-full md:w-64 pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-primary/20 shadow-sm transition-all text-slate-700" />
          </div>
          
          <div class="flex flex-col sm:flex-row items-center gap-3 w-full md:w-auto">
            <!-- Transparent overlay for dropdowns -->
            <div v-if="techDropdownOpen || majorDropdownOpen" @click="techDropdownOpen = false; majorDropdownOpen = false" class="fixed inset-0 z-40"></div>

            <!-- Tech Filter -->
            <div class="relative w-full sm:w-auto flex-1 sm:flex-none z-50">
              <div @click="techDropdownOpen = !techDropdownOpen; majorDropdownOpen = false" 
                   class="w-full sm:w-44 pl-9 pr-8 py-2.5 bg-white border border-slate-200 rounded-xl text-sm font-medium cursor-pointer shadow-sm text-slate-600 transition-all hover:border-primary/40 flex items-center">
                <span class="truncate">{{ techFilter || '全部技术方向' }}</span>
              </div>
              <Cpu :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
              <ChevronDown :size="14" class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none transition-transform duration-200" :class="{ 'rotate-180': techDropdownOpen }" />
              
              <!-- Dropdown Menu -->
              <transition
                enter-active-class="transition duration-150 ease-out"
                enter-from-class="transform scale-95 opacity-0"
                enter-to-class="transform scale-100 opacity-100"
                leave-active-class="transition duration-100 ease-in"
                leave-from-class="transform scale-100 opacity-100"
                leave-to-class="transform scale-95 opacity-0"
              >
                <div v-show="techDropdownOpen" class="absolute left-0 right-0 sm:min-w-[12rem] mt-2 py-1.5 bg-white border border-slate-100 rounded-xl shadow-2xl max-h-64 overflow-y-auto z-50 origin-top dropdown-scroll">
                  <div @click="techFilter = ''; techDropdownOpen = false; triggerSearch()" 
                       class="px-4 py-2.5 text-sm text-slate-600 hover:bg-slate-50 cursor-pointer transition-colors flex items-center justify-between"
                       :class="{ 'text-primary font-bold bg-primary/5': techFilter === '' }">
                    全部技术方向
                  </div>
                  <div v-for="t in techOptions" :key="t" 
                       @click="techFilter = t; techDropdownOpen = false; triggerSearch()" 
                       class="px-4 py-2.5 text-sm text-slate-600 hover:bg-slate-50 cursor-pointer transition-colors flex items-center justify-between"
                       :class="{ 'text-primary font-bold bg-primary/5': techFilter === t }">
                    {{ t }}
                  </div>
                </div>
              </transition>
            </div>

            <!-- Major Filter -->
            <div class="relative w-full sm:w-auto flex-1 sm:flex-none z-50">
              <div @click="majorDropdownOpen = !majorDropdownOpen; techDropdownOpen = false" 
                   class="w-full sm:w-44 pl-9 pr-8 py-2.5 bg-white border border-slate-200 rounded-xl text-sm font-medium cursor-pointer shadow-sm text-slate-600 transition-all hover:border-primary/40 flex items-center">
                <span class="truncate">{{ majorFilter || '全部专业门类' }}</span>
              </div>
              <GraduationCap :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
              <ChevronDown :size="14" class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none transition-transform duration-200" :class="{ 'rotate-180': majorDropdownOpen }" />
              
              <!-- Dropdown Menu -->
              <transition
                enter-active-class="transition duration-150 ease-out"
                enter-from-class="transform scale-95 opacity-0"
                enter-to-class="transform scale-100 opacity-100"
                leave-active-class="transition duration-100 ease-in"
                leave-from-class="transform scale-100 opacity-100"
                leave-to-class="transform scale-95 opacity-0"
              >
                <div v-show="majorDropdownOpen" class="absolute left-0 right-0 sm:min-w-[12rem] mt-2 py-1.5 bg-white border border-slate-100 rounded-xl shadow-2xl max-h-64 overflow-y-auto z-50 origin-top dropdown-scroll">
                  <div @click="majorFilter = ''; majorDropdownOpen = false; triggerSearch()" 
                       class="px-4 py-2.5 text-sm text-slate-600 hover:bg-slate-50 cursor-pointer transition-colors flex items-center justify-between"
                       :class="{ 'text-primary font-bold bg-primary/5': majorFilter === '' }">
                    全部适用专业
                  </div>
                  <div v-for="m in majorOptions" :key="m" 
                       @click="majorFilter = m; majorDropdownOpen = false; triggerSearch()" 
                       class="px-4 py-2.5 text-sm text-slate-600 hover:bg-slate-50 cursor-pointer transition-colors flex items-center justify-between"
                       :class="{ 'text-primary font-bold bg-primary/5': majorFilter === m }">
                    {{ m }}
                  </div>
                </div>
              </transition>
            </div>

            <button @click="triggerSearch" class="w-full sm:w-auto px-6 py-2.5 bg-profound-black text-white hover:bg-primary transition-colors rounded-xl text-sm font-bold shadow-md uppercase tracking-wide shrink-0 z-0">
               搜索
            </button>
          </div>
        </div>
      </div>

      <!-- Categories row -->
      <div v-if="categories.length > 0" class="flex items-center gap-2 overflow-x-auto pb-2 [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]">
        <button v-for="cat in categories" :key="cat.id"
                @click="activeCategory = cat.id; triggerSearch()"
                class="px-4 py-1.5 rounded-lg text-[13px] font-bold transition-all whitespace-nowrap border"
                :class="activeCategory === cat.id 
                  ? 'bg-primary text-white border-primary shadow-sm' 
                  : 'bg-white text-slate-500 border-slate-200 hover:border-primary/30 hover:bg-slate-50'">
          {{ cat.name }}
        </button>
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

const techDropdownOpen = ref(false)
const majorDropdownOpen = ref(false)

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
const categories = computed(() => catRes.value?.data || [])

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
