<template>
  <div class="space-y-8">
    <div v-if="pending" class="flex items-center justify-center py-24">
      <div class="w-12 h-12 border-4 border-blue-600/20 border-t-blue-600 rounded-full animate-spin"></div>
    </div>

    <div v-else-if="consultations.length === 0" class="glass-card bg-white p-24 rounded-[48px] text-center space-y-6">
      <div class="w-24 h-24 bg-slate-50 rounded-[32px] flex items-center justify-center text-slate-200 mx-auto">
        <LucideMessageSquareX :size="48" />
      </div>
      <div>
        <h3 class="text-2xl font-black text-slate-800">暂无通话</h3>
        <p class="text-slate-400 font-bold">提交咨询申请后，会有专属导师在这里为您回复</p>
      </div>
      <NuxtLink to="/services" class="btn-premium inline-block">立即发起咨询</NuxtLink>
    </div>

    <div v-else class="space-y-12">
      <div v-for="item in consultations" :key="item.id" 
           class="space-y-6 animate-fade-in-up">
        
        <!-- User Inquiry Bubble -->
        <div class="flex justify-start pr-12">
          <div class="glass-card bg-white p-8 md:p-10 rounded-[40px] rounded-bl-none shadow-xl shadow-slate-200/30 max-w-2xl relative">
            <span class="absolute -top-3 left-8 px-3 py-1 bg-slate-900 text-white text-[10px] font-black rounded-lg">MY REQUEST</span>
            <p class="text-lg font-bold text-slate-800 leading-relaxed">{{ item.content }}</p>
            <div class="mt-4 flex items-center gap-4 text-[10px] font-bold text-slate-300">
               <span>ID: GB-CS-{{ item.id }}</span>
               <span class="w-1 h-1 rounded-full bg-slate-200"></span>
               <span>{{ formatDate(item.createTime) }}</span>
            </div>
          </div>
        </div>

        <!-- Official Reply Bubble -->
        <div v-if="item.reply" class="flex justify-end pl-12">
          <div class="relative max-w-2xl bg-gradient-to-br from-blue-600 to-indigo-700 p-8 md:p-10 rounded-[40px] rounded-br-none shadow-2xl shadow-blue-200/50 group overflow-hidden">
            <div class="absolute top-0 left-0 w-full h-full bg-mesh opacity-20 pointer-events-none"></div>
            <LucideSparkles class="absolute top-6 right-6 text-white/20" :size="48" />
            
            <div class="relative z-10 space-y-4">
              <div class="flex items-center gap-2">
                <div class="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center text-white">
                  <LucideUserCheck :size="16" />
                </div>
                <span class="text-[10px] font-black text-white uppercase tracking-[0.2em]">OFFICIAL GUIDANCE</span>
              </div>
              <p class="text-lg font-bold text-white leading-relaxed">{{ item.reply }}</p>
              <div class="flex justify-between items-center pt-4 border-t border-white/10 text-[10px] font-black text-white/60">
                 <span>TUTOR: SENIOR DEV.</span>
                 <span class="px-2 py-1 bg-white/10 rounded-lg">VERIFIED BY GRADBOX</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Pending State -->
        <div v-else class="flex justify-end pl-12 translate-y-[-10px]">
          <div class="px-8 py-4 bg-white border border-dashed border-slate-200 rounded-3xl text-sm font-bold text-slate-400 animate-pulse">
            <LucideClock class="inline-block mr-2" :size="16" /> 官方导师正在深度审核需求，请保持关注...
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { 
  LucideMessageSquareX, LucideSparkles, LucideUserCheck, LucideClock 
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
.bg-mesh {
  background-image: radial-gradient(at 0% 0%, hsla(253,16%,7%,1) 0, transparent 50%), 
                    radial-gradient(at 100% 0%, hsla(225,39%,30%,1) 0, transparent 50%);
}
</style>
