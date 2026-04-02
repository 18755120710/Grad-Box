<template>
  <div class="space-y-10 animate-fade-in-up">
    <div v-if="pending" class="flex flex-col items-center justify-center py-32 space-y-6">
      <div class="w-12 h-12 border-2 border-primary/10 border-t-primary rounded-full animate-spin"></div>
      <p class="text-[9px] font-bold text-slate-400 uppercase tracking-profound animate-pulse">Connecting with tutors...</p>
    </div>

    <div v-else-if="consultations.length === 0" class="py-32 text-center space-y-8">
      <div class="w-24 h-24 bg-slate-50 rounded-4xl flex items-center justify-center text-slate-200 mx-auto relative group">
        <div class="absolute inset-0 bg-primary/5 rounded-4xl scale-0 group-hover:scale-125 transition-transform duration-700 opacity-0 group-hover:opacity-100"></div>
        <LucideMessageSquareX :size="40" class="relative group-hover:text-primary transition-colors duration-500" />
      </div>
      <div class="space-y-3">
        <h3 class="text-2xl font-display text-profound-black tracking-tighter uppercase italic">暂无咨询 / Message</h3>
        <p class="text-slate-400 font-bold uppercase tracking-profound text-[10px]">Your consultation requests will appear here</p>
      </div>
      <NuxtLink to="/projects" class="btn-premium px-12 py-5 inline-flex">发起新咨询 / NEW INQUIRY</NuxtLink>
    </div>

    <div v-else class="space-y-16">
      <div v-for="item in consultations" :key="item.id" 
           class="space-y-8 group">
        
        <!-- User Inquiry Bubble (Pro Max V2) -->
        <div class="flex justify-start pr-8 md:pr-24">
          <div class="glass-premium p-6 md:p-8 rounded-[32px] rounded-bl-none shadow-2xl shadow-slate-200/40 max-w-2xl relative border-white/40">
            <span class="absolute -top-3 left-6 px-3 py-1 bg-profound-black text-white text-[8px] font-black rounded-lg uppercase tracking-widest border border-white/10 ring-4 ring-white">MY REQUEST</span>
            <p class="text-base font-display text-profound-black leading-snug italic opacity-90">{{ item.content }}</p>
            <div class="mt-4 flex items-center gap-3 text-[8px] font-black text-slate-300 uppercase tracking-widest border-t border-slate-50 pt-3">
               <span class="flex items-center gap-1"><LucideFingerprint :size="10" /> #{{ item.id }}</span>
               <span class="w-1 h-1 rounded-full bg-slate-200"></span>
               <span>{{ formatDate(item.createTime) }}</span>
            </div>
          </div>
        </div>
 
        <!-- Official Reply Bubble (Pro Max V2) -->
        <div v-if="item.reply" class="flex justify-end pl-8 md:pl-24">
          <div class="relative max-w-2xl bg-profound-black p-6 md:p-10 rounded-[32px] rounded-br-none shadow-3xl shadow-primary/20 group overflow-hidden border border-white/10">
            <div class="absolute inset-0 bg-gradient-to-br from-primary/20 via-transparent to-transparent pointer-events-none opacity-50"></div>
            <LucideSparkles class="absolute top-6 right-6 text-white/5 animate-pulse" :size="48" />
            
            <div class="relative z-10 space-y-5">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-xl bg-primary flex items-center justify-center text-white border border-primary/40 shadow-lg shadow-primary/20">
                  <LucideUserCheck :size="16" />
                </div>
                <div class="space-y-0">
                  <span class="text-[9px] font-display text-white italic uppercase tracking-widest">OFFICIAL MENTOR</span>
                  <p class="text-[7px] font-black text-primary uppercase tracking-[0.2em] opacity-80">Academic Distinction Verified</p>
                </div>
              </div>
              <p class="text-lg font-display text-white leading-tight italic text-glow-primary">{{ item.reply }}</p>
              <div class="flex justify-between items-center pt-5 border-t border-white/10 text-[8px] font-black text-white/30 uppercase tracking-widest">
                 <span class="flex items-center gap-1"><LucideShieldCheck :size="10" class="text-primary/60" /> SECURE CHANNEL</span>
                 <span class="px-2 py-0.5 bg-white/5 rounded-md border border-white/5">ARCHIVED</span>
              </div>
            </div>
          </div>
        </div>
 
        <!-- Pending State (Pro Max V2) -->
        <div v-else class="flex justify-end pl-8 md:pl-24">
          <div class="px-8 py-5 glass-premium bg-slate-50/20 border-dashed border-slate-200/50 rounded-[28px] text-[9px] font-black text-slate-400 uppercase tracking-widest animate-pulse flex items-center gap-3">
            <div class="w-1.5 h-1.5 bg-primary rounded-full animate-ping"></div>
            IN ANALYSIS · SENIOR TUTOR REVIWING
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { 
  LucideMessageSquareX, LucideSparkles, LucideUserCheck, 
  LucideClock, LucideFingerprint, LucideShieldCheck 
} from 'lucide-vue-next'

const api = useApi()
const { data: res, pending } = await useAsyncData('my-consultations-ui', () => api('/api/consultations/my'))
const consultations = computed(() => res.value?.data?.records || [])

const formatDate = (d) => {
  if (!d) return '--'
  return new Date(d).toLocaleString('zh-CN', {
    month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}
</script>

<style scoped>
/* Unified Mesh Removed for a cleaner Pro Max look */
</style>
