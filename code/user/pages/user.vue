<template>
  <div class="min-h-screen bg-slate-50/50 pt-12 pb-24">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Premium User Header -->
      <header class="relative mb-12 group">
        <div class="absolute inset-0 bg-gradient-to-r from-blue-600 to-indigo-700 rounded-[40px] opacity-10 blur-3xl group-hover:opacity-20 transition-opacity"></div>
        <div class="glass-card bg-white p-10 md:p-14 rounded-[48px] border border-slate-100 flex flex-col md:flex-row items-center justify-between gap-10">
          <div class="flex flex-col md:flex-row items-center gap-8">
            <div class="relative">
              <div class="absolute -inset-1 bg-gradient-to-br from-blue-600 to-indigo-600 rounded-full blur opacity-25"></div>
              <el-avatar :size="100" :src="authStore.userInfo?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" class="relative border-4 border-white shadow-xl" />
            </div>
            <div class="text-center md:text-left space-y-2">
              <div class="flex items-center justify-center md:justify-start gap-3">
                <h1 class="text-3xl font-black text-slate-900">{{ authStore.userInfo?.nickname || '尊敬的用户' }}</h1>
                <span class="px-3 py-1 bg-blue-100 text-blue-600 text-[10px] font-black rounded-lg uppercase tracking-widest">VIP Member</span>
              </div>
              <p class="text-slate-400 font-bold flex items-center justify-center md:justify-start gap-2">
                <LucideFingerprint :size="14" /> ID: GB-{{ authStore.userInfo?.id?.toString().padStart(6, '0') }}
              </p>
            </div>
          </div>

          <!-- Quick Stats -->
          <div class="flex gap-4 md:gap-12">
            <div class="text-center space-y-1">
              <div class="text-3xl font-black text-slate-900 leading-none">15</div>
              <div class="text-[10px] uppercase font-black text-slate-400 tracking-widest leading-none">浏览历史</div>
            </div>
            <div class="w-px h-10 bg-slate-100 hidden md:block"></div>
            <div class="text-center space-y-1">
              <div class="text-3xl font-black text-slate-900 leading-none">01</div>
              <div class="text-[10px] uppercase font-black text-slate-400 tracking-widest leading-none">咨询工单</div>
            </div>
            <div class="w-px h-10 bg-slate-100 hidden md:block"></div>
            <div class="text-center space-y-1">
              <div class="text-3xl font-black text-blue-600 leading-none">12</div>
              <div class="text-[10px] uppercase font-black text-slate-400 tracking-widest leading-none">收藏项目</div>
            </div>
          </div>
        </div>
      </header>

      <div class="grid grid-cols-1 lg:grid-cols-4 gap-12 items-start">
        <!-- Sidebar Navigation -->
        <aside class="space-y-4">
          <NuxtLink v-for="tab in tabs" :key="tab.to" :to="tab.to" 
                    class="flex items-center gap-4 px-8 py-5 rounded-[24px] font-black transition-all duration-300 group"
                    :class="route.path === tab.to ? 'bg-slate-900 text-white shadow-2xl shadow-slate-300 -translate-y-1' : 'bg-white text-slate-500 hover:bg-slate-50'">
            <component :is="tab.icon" :size="20" class="group-hover:scale-110 transition-transform" />
            {{ tab.text }}
            <LucideChevronRight v-if="route.path === tab.to" :size="16" class="ml-auto" />
          </NuxtLink>
          
          <button @click="handleLogout" class="w-full flex items-center gap-4 px-8 py-5 rounded-[24px] font-black text-red-500 hover:bg-red-50 transition-all">
            <LucideLogOut :size="20" />
            退出登录
          </button>
        </aside>

        <!-- Main Content area -->
        <main class="lg:col-span-3 min-h-[60vh] animate-fade-in-up">
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

onMounted(() => {
  authStore.fetchUserInfo()
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
