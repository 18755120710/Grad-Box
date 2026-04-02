<template>
  <div class="min-h-screen bg-white transition-colors duration-1000 selection:bg-primary/10">
    <!-- Navbar (Pro Max V2) -->
    <nav class="sticky top-0 z-50 bg-white/80 backdrop-blur-3xl border-b border-slate-200/40">
      <div class="max-w-7xl mx-auto px-6 sm:px-10">
        <div class="flex justify-between h-20 items-center">
          <NuxtLink to="/" class="flex items-center gap-3 group">
            <div class="p-1.5 bg-white rounded-2xl shadow-xl group-hover:scale-105 transition-all duration-700 overflow-hidden border border-slate-100">
              <img src="/logo.png" alt="GradBox Logo" class="w-10 h-10 object-cover" />
            </div>
            <span class="text-xl font-display text-profound-black tracking-tighter uppercase italic">Grad<span class="text-primary italic not-italic font-black">Box</span></span>
          </NuxtLink>

          <!-- Desktop Center Nav -->
          <div class="hidden md:flex items-center gap-10">
            <NuxtLink v-for="link in navLinks" :key="link.to" :to="link.to" 
                      class="text-xs font-bold text-slate-400 hover:text-profound-black tracking-profound uppercase transition-all flex items-center gap-2 group relative py-1">
              {{ link.text }}
              <span class="absolute -bottom-1 left-0 w-0 h-0.5 bg-primary group-hover:w-full transition-all duration-500 rounded-full"></span>
            </NuxtLink>
            
            <!-- Command Palette Trigger (UI-UX Pro Max) -->
            <button @click="toggleSearch" 
                    class="flex items-center gap-3 px-4 py-2 bg-slate-50 border border-slate-100 rounded-xl text-slate-400 hover:bg-white hover:shadow-xl hover:shadow-slate-200/40 transition-all duration-500 group ml-4 relative">
              <LucideSearch :size="14" class="group-hover:text-primary transition-colors" />
              <span class="text-[10px] uppercase font-black tracking-widest">全局搜索 / ⌘K</span>
            </button>
          </div>

          <div class="flex items-center gap-6">
            <template v-if="authStore.isLoggedIn">
              <ClientOnly>
                <el-dropdown trigger="click">
                  <div class="flex items-center gap-3 cursor-pointer p-1 rounded-full bg-slate-50 border border-slate-100 hover:bg-white hover:shadow-xl hover:shadow-slate-200/40 transition-all duration-700 group">
                    <el-avatar :size="36" :src="authStore.userInfo?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" class="border-2 border-white shadow-sm" />
                    <div class="hidden sm:flex flex-col pr-4 gap-0.5">
                      <span class="text-[11px] font-bold text-profound-black uppercase tracking-wide leading-tight">{{ authStore.userInfo?.nickname || 'MEMBER' }}</span>
                    </div>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu class="profound-dropdown">
                      <el-dropdown-item @click="navigateTo('/user/profile')">
                        <LucideUser :size="14" class="mr-2" /> 个人资料
                      </el-dropdown-item>
                      <el-dropdown-item @click="navigateTo('/user/history')">
                        <LucideHistory :size="14" class="mr-2" /> 浏览历史
                      </el-dropdown-item>
                      <el-dropdown-item divided @click="handleLogout" class="text-red-500 font-bold">
                        <LucideLogOut :size="14" class="mr-2" /> 安全退出
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </ClientOnly>
            </template>
            <template v-else>
              <div class="flex items-center gap-5">
                <a @click.prevent="navigateTo('/login')" 
                   class="text-[11px] font-black text-slate-400 hover:text-profound-black uppercase tracking-widest transition-all cursor-pointer select-none">
                  登录
                </a>
                <div class="w-px h-3 bg-slate-200"></div>
                <a @click.prevent="navigateTo('/register')" 
                   class="text-[11px] font-black text-slate-400 hover:text-profound-black uppercase tracking-widest transition-all cursor-pointer select-none">
                  立即加入
                </a>
              </div>
            </template>
          </div>
        </div>
      </div>
    </nav>
    
    <!-- Search Command Palette (Pro Max) -->
    <ClientOnly>
      <el-dialog v-model="searchVisible" 
                 :show-close="false"
                 title="" 
                 width="600px" 
                 class="search-palette-dialog"
                 destroy-on-close>
        <div class="space-y-6 -mt-4">
          <div class="relative group">
            <div class="absolute inset-y-0 left-5 flex items-center pointer-events-none text-slate-400 group-focus-within:text-primary transition-colors">
              <LucideSearch :size="20" />
            </div>
            <input v-model="searchQuery"
                   @keyup.enter="handleSearch"
                   type="text"
                   placeholder="搜索案例、技术栈、核心功能..."
                   class="w-full pl-14 pr-6 py-6 bg-slate-50 rounded-2xl border-none focus:ring-2 focus:ring-primary/20 text-lg font-display text-profound-black placeholder:text-slate-300 transition-all shadow-inner"
                   autofocus />
          </div>
          <div class="flex items-center justify-between px-2">
            <div class="flex items-center gap-4 text-[9px] font-bold text-slate-300 uppercase tracking-profound">
               <span class="flex items-center gap-1"><kbd class="px-1.5 py-0.5 rounded bg-slate-100 border border-slate-200 text-slate-400 text-[8px]">ENTER</kbd> 确认搜索</span>
               <span class="flex items-center gap-1"><kbd class="px-1.5 py-0.5 rounded bg-slate-100 border border-slate-200 text-slate-400 text-[8px]">ESC</kbd> 关闭面板</span>
            </div>
            <LucideArrowRight :size="14" class="text-primary animate-pulse" />
          </div>
        </div>
      </el-dialog>
    </ClientOnly>

    <!-- Main Content -->
    <main class="page-container relative z-10 pb-20">
      <slot />
    </main>

    <!-- Footer (Pro Max) -->
    <footer class="bg-white border-t border-slate-100 py-32">
      <div class="max-w-7xl mx-auto px-10 grid grid-cols-1 md:grid-cols-4 gap-24">
        <div class="col-span-1 md:col-span-2 space-y-10">
          <div class="flex items-center gap-3 text-profound-black">
            <img src="/logo.png" alt="GradBox Logo" class="w-14 h-14 object-cover rounded-2xl bg-white shadow-xl p-1 border border-slate-50" />
            <span class="text-3xl font-display tracking-tighter italic">Grad<span class="text-primary">Box</span></span>
          </div>
          <p class="max-w-md text-slate-400 font-bold text-base leading-relaxed uppercase tracking-wide opacity-80 decoration-primary decoration-2">
            追求卓越的毕业设计案例中枢。我们不仅提供源码，更提供深度思维与技术灵魂的闭环支持。
          </p>
          <div class="flex gap-4 pt-4">
             <div v-for="i in 4" :key="i" class="w-10 h-10 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-300 hover:text-primary transition-all cursor-pointer">
                <LucideArrowRight :size="16" />
             </div>
          </div>
        </div>
        <div>
          <h4 class="text-profound-black font-display text-[10px] uppercase tracking-profound mb-12 italic border-b border-slate-50 pb-4 inline-block">资源中心 / Resources</h4>
          <ul class="space-y-5">
            <li v-for="link in navLinks" :key="link.to">
              <NuxtLink :to="link.to" class="text-[11px] font-bold text-slate-400 hover:text-primary transition-all uppercase tracking-wide flex items-center gap-2 group">
                <span class="w-1.5 h-1.5 rounded-full bg-slate-100 group-hover:bg-primary transition-colors"></span>
                {{ link.text }}
              </NuxtLink>
            </li>
          </ul>
        </div>
        <div>
          <h4 class="text-profound-black font-display text-[10px] uppercase tracking-profound mb-12 italic border-b border-slate-50 pb-4 inline-block">即刻咨询 / Contact</h4>
          <div class="space-y-8">
            <div class="space-y-1">
              <p class="text-2xl font-display text-profound-black tracking-tighter">400-888-GRAD</p>
              <p class="text-[9px] font-bold text-slate-300 uppercase tracking-profound">Global Service Hotline</p>
            </div>
            <p class="text-slate-400 text-[10px] font-bold leading-loose uppercase tracking-wide">
              卓越服务 · 终身质保<br />
              <span class="text-slate-300">9:00 - 21:00 (Mon-Sun)</span>
            </p>
            <div class="pt-2">
               <div class="w-14 h-14 rounded-3xl bg-primary/5 flex items-center justify-center text-primary border border-primary/10 shadow-inner">
                  <LucideShieldCheck :size="28" />
               </div>
            </div>
          </div>
        </div>
      </div>
      <div class="max-w-7xl mx-auto px-10 mt-32 pt-12 border-t border-slate-50 text-[9px] font-bold text-slate-300 uppercase tracking-profound text-center italic">
        © 2024 <span class="text-profound-black">GradBox Intelligence</span>. Elevating Academic Excellence through Design.
      </div>
    </footer>
  </div>
</template>

<script setup>
import { 
  LucideUser, LucideLogOut, LucideArrowRight, 
  LucideShieldCheck, LucideSearch 
} from 'lucide-vue-next'

const authStore = useAuthStore()
const router = useRouter()

const navLinks = [
  { text: '门户首页', to: '/' },
  { text: '全部案例', to: '/projects' }
]

const searchVisible = ref(false)
const searchQuery = ref('')

const toggleSearch = () => {
  searchVisible.value = !searchVisible.value
}

const handleSearch = () => {
  if (!searchQuery.value.trim()) return
  navigateTo(`/projects?q=${encodeURIComponent(searchQuery.value.trim())}`)
  searchVisible.value = false
  searchQuery.value = ''
}

const handleLogout = () => {
  authStore.logout()
  navigateTo('/login')
}

// Add ⌘K shortcut
const handleKeyDown = (e) => {
  if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
    e.preventDefault()
    toggleSearch()
  }
}

onMounted(() => {
  authStore.initialize()
  if (authStore.isLoggedIn) {
    authStore.fetchUserInfo()
  }
  window.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
})
</script>

<style>
.profound-dropdown {
  border-radius: 24px !important;
  padding: 8px !important;
  border: 1px solid #f1f5f9 !important;
  box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.08) !important;
}
.el-dropdown-menu__item {
  border-radius: 12px !important;
  padding: 10px 16px !important;
  font-size: 11px !important;
  font-weight: 700 !important;
  letter-spacing: 0.05em !important;
  text-transform: uppercase !important;
  color: #64748b !important;
}
.el-dropdown-menu__item:hover {
  background-color: #f8fafc !important;
  color: #020617 !important;
}
</style>

