<template>
  <div class="min-h-screen bg-slate-50/30 pt-12 pb-24 selection:bg-primary/10">
    <div class="max-w-7xl mx-auto px-6 sm:px-8">
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-10 items-start">
        <!-- Integrated Sidebar (Pro Max V2) -->
        <aside class="lg:col-span-3 space-y-6 lg:sticky lg:top-32 animate-fade-in-left">
          <!-- User Profile Card -->
          <div class="glass-premium p-8 rounded-[40px] border-white/40 shadow-2xl shadow-slate-200/40 group overflow-hidden relative">
            <div class="absolute top-0 right-0 w-24 h-24 bg-primary/5 rounded-full blur-3xl -mr-12 -mt-12 group-hover:scale-150 transition-transform duration-1000"></div>
            
            <div class="flex flex-col items-center text-center space-y-5 relative z-10">
              <div class="relative group/avatar">
                <div class="absolute -inset-1.5 bg-gradient-to-br from-primary via-indigo-400 to-indigo-600 rounded-full blur-[8px] opacity-20 group-hover/avatar:opacity-40 transition-opacity duration-700"></div>
                <el-avatar :size="88" :src="authStore.userInfo?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" 
                           class="relative border-4 border-white shadow-xl scale-100 group-hover/avatar:scale-[1.02] transition-transform duration-700" />
              </div>
              
              <div class="space-y-1">
                <h1 class="text-xl font-display text-profound-black tracking-tighter uppercase italic leading-tight">{{ authStore.userInfo?.nickname || '尊敬的用户' }}</h1>
                <p class="text-[9px] font-black text-slate-300 uppercase tracking-widest flex items-center justify-center gap-1.5 font-display">
                   用户标识: GB-{{ authStore.userInfo?.id?.toString().padStart(6, '0') }}
                </p>
              </div>
            </div>

            <!-- Mini Stats Grid -->
            <div class="grid grid-cols-3 gap-4 pt-8 mt-8 border-t border-slate-100/60 relative z-10">
              <div class="text-center group/stat">
                <div class="text-lg font-display text-profound-black leading-none group-hover:text-primary transition-colors font-display">{{ statistics.historyCount }}</div>
                <div class="text-[8px] font-black text-slate-400 tracking-widest mt-1">浏览历史</div>
              </div>
              <div class="text-center group/stat">
                <div class="text-lg font-display text-profound-black leading-none group-hover:text-primary transition-colors font-display">{{ statistics.consultationCount }}</div>
                <div class="text-[8px] font-black text-slate-400 tracking-widest mt-1">我的咨询</div>
              </div>
              <div class="text-center group/stat">
                <div class="text-lg font-display text-primary leading-none font-display">{{ statistics.favoriteCount }}</div>
                <div class="text-[8px] font-black text-slate-400 tracking-widest mt-1">精选收藏</div>
              </div>
            </div>
          </div>

          <!-- Navigation Menu -->
          <nav class="space-y-2">
            <NuxtLink v-for="tab in tabs" :key="tab.to" :to="tab.to" 
                      class="flex items-center gap-4 px-6 py-4 rounded-[24px] font-bold transition-all duration-500 group relative overflow-hidden"
                      :class="route.path.startsWith(tab.to) ? 'bg-profound-black text-white shadow-xl shadow-primary/10 scale-[1.02]' : 'bg-white/60 backdrop-blur-md text-slate-500 hover:bg-white hover:text-profound-black border border-slate-100/50 hover:shadow-lg'">
              <component :is="tab.icon" :size="16" class="group-hover:scale-110 transition-transform duration-500" 
                         :class="route.path.startsWith(tab.to) ? 'text-primary' : 'text-slate-300'" />
              <span class="text-[10px] uppercase font-black tracking-widest">{{ tab.text }}</span>
              <LucideChevronRight v-if="route.path.startsWith(tab.to)" :size="12" class="ml-auto opacity-40" />
            </NuxtLink>
            
            <button @click="handleLogout" class="w-full flex items-center gap-4 px-6 py-4 rounded-[24px] font-bold text-red-500/70 hover:bg-red-50/50 transition-all text-[10px] uppercase tracking-profound mt-6">
              <LucideLogOut :size="16" />
              退出账户
            </button>
          </nav>
        </aside>

        <!-- Main Content Area -->
        <main class="lg:col-span-9 min-h-[70vh] animate-fade-in-up glass-premium p-8 md:p-12 rounded-[40px] border-white/40 shadow-2xl shadow-slate-200/40 relative overflow-hidden">
           <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-primary/50 to-indigo-600/50 opacity-20"></div>
           <NuxtPage />
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { 
  LucideFingerprint, LucideHistory, LucideMessageCircle, 
  LucideUser, LucideLogOut, LucideChevronRight 
} from 'lucide-vue-next'

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()
const api = useApi()

// Fetch real statistics
const { data: statsRes, refresh: refreshStats } = await useAsyncData('user-content-stats', () => api('/api/user/stats'))
const statistics = computed(() => statsRes.value?.data || { historyCount: 0, consultationCount: 0, favoriteCount: 0 })

onMounted(() => {
  authStore.fetchUserInfo()
  refreshStats()
  
  // 核心逻辑：如果访问 /user，自动重定向到浏览历史
  if (route.path === '/user' || route.path === '/user/') {
    router.replace('/user/history')
  }
})

const tabs = [
  { text: '浏览历史', to: '/user/history', icon: LucideHistory },
  { text: '我的咨询', to: '/user/consultations', icon: LucideMessageCircle },
  { text: '个人资料', to: '/user/profile', icon: LucideUser }
]

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

definePageMeta({
  middleware: 'auth'
})
</script>
