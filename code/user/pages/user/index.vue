<template>
  <div class="space-y-10 animate-fade-in-up">
    <!-- Welcome Section -->
    <div class="relative overflow-hidden p-10 rounded-[40px] bg-profound-black text-white shadow-2xl shadow-primary/20 group">
      <div class="absolute inset-0 bg-gradient-to-br from-primary/30 via-transparent to-transparent opacity-60"></div>
      <LucideSparkles class="absolute top-8 right-10 text-white/10 animate-pulse" :size="80" />
      
      <div class="relative z-10 space-y-4">
        <div class="inline-flex px-3 py-1 bg-white/10 rounded-full border border-white/10 text-[9px] font-black uppercase tracking-widest text-primary">
          欢迎回来 / Welcome Back
        </div>
        <h2 class="text-4xl font-display tracking-tighter italic">你好, <span class="text-primary">{{ authStore.userInfo?.nickname }}</span></h2>
        <p class="max-w-md text-white/50 text-xs font-bold leading-relaxed uppercase tracking-wide">
          准备好探索更多卓越的设计案例了吗？今天我们为您整理了最新的学术趋势与技术资料。
        </p>
      </div>
    </div>

    <!-- Quick Actions Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
      <div v-for="action in quickActions" :key="action.title" 
           @click="navigateTo(action.to)"
           class="glass-premium p-8 rounded-[36px] border-white/40 shadow-xl shadow-slate-200/40 hover:-translate-y-2 transition-all duration-700 cursor-pointer group relative overflow-hidden">
        <div class="absolute top-0 right-0 w-32 h-32 bg-primary/5 rounded-full blur-3xl -mr-16 -mt-16 group-hover:scale-150 transition-transform duration-1000"></div>
        
        <div class="space-y-6 relative z-10">
          <div class="w-14 h-14 rounded-2xl bg-slate-50 flex items-center justify-center text-slate-400 group-hover:bg-primary group-hover:text-white transition-all duration-500 shadow-inner">
            <component :is="action.icon" :size="24" />
          </div>
          <div class="space-y-1">
            <h4 class="text-lg font-display text-profound-black uppercase italic">{{ action.title }}</h4>
            <p class="text-[10px] text-slate-400 font-bold uppercase tracking-widest">{{ action.description }}</p>
          </div>
          <div class="flex items-center gap-2 text-[9px] font-black text-primary uppercase tracking-widest opacity-0 group-hover:opacity-100 transition-opacity">
            立即前往 <LucideArrowRight :size="12" />
          </div>
        </div>
      </div>
    </div>

    <!-- Support Banner -->
    <div class="glass-premium p-8 rounded-[36px] border-dashed border-slate-200 flex flex-col md:flex-row items-center justify-between gap-8">
      <div class="flex items-center gap-6">
        <div class="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center text-primary border border-primary/20 shadow-xl shadow-primary/5 shrink-0">
          <LucideShieldCheck :size="32" />
        </div>
        <div class="space-y-1 text-center md:text-left">
          <h4 class="text-sm font-display text-profound-black uppercase">学术安全与技术保障</h4>
          <p class="text-[9px] text-slate-400 font-bold uppercase tracking-widest">您的所有咨询与下载记录均受 256 位加密保护</p>
        </div>
      </div>
      <button class="px-8 py-4 bg-slate-50 text-slate-400 rounded-2xl text-[10px] font-black uppercase tracking-widest hover:bg-white hover:text-profound-black border border-slate-100 transition-all">
        联系技术支持
      </button>
    </div>
  </div>
</template>

<script setup>
import { 
  LucideSparkles, LucideHistory, LucideMessageCircle, 
  LucideArrowRight, LucideShieldCheck 
} from 'lucide-vue-next'

const authStore = useAuthStore()

const quickActions = [
  { 
    title: '继续浏览记录', 
    description: '返回您最近查看的案例详情页', 
    icon: LucideHistory,
    to: '/user/history'
  },
  { 
    title: '查看咨询进度', 
    description: '由资深导师为您解答的最新回复', 
    icon: LucideMessageCircle,
    to: '/user/consultations'
  }
]
</script>
