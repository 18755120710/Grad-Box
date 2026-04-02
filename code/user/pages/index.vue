<template>
  <div class="relative overflow-hidden bg-white selection:bg-indigo-100 selection:text-indigo-900">
    <!-- Sophisticated Mesh Background -->
    <div class="absolute top-0 left-0 w-full h-[1500px] pointer-events-none opacity-50 z-0">
      <div class="absolute top-[-10%] left-[-10%] w-[60%] h-[60%] bg-blue-100/30 rounded-full blur-[150px] animate-pulse"></div>
      <div class="absolute top-[15%] right-[-10%] w-[50%] h-[50%] bg-indigo-100/30 rounded-full blur-[150px] animate-float"></div>
      <div class="absolute bottom-0 left-[20%] w-[40%] h-[40%] bg-purple-50/20 rounded-full blur-[150px]"></div>
    </div>

    <div class="max-w-7xl mx-auto px-6 sm:px-8 relative z-20">
      <!-- Hero Section (UI-UX Pro Max) -->
      <section class="min-h-[85vh] flex flex-col justify-center pt-20 pb-32 animate-fade-in overflow-visible">
        <div class="grid grid-cols-1 lg:grid-cols-2 items-center gap-12 lg:gap-20">
          <div class="space-y-10">
          <div class="inline-flex items-center gap-2 px-5 py-2 rounded-xl bg-primary/5 backdrop-blur-xl text-primary text-[10px] font-bold uppercase tracking-profound border border-primary/10 shadow-sm">
            <LucideSparkles :size="12" /> {{ homeConfig?.badge || '2024 Showcase · Academic Excellence' }}
          </div>
          
          <h1 class="text-[clamp(2.5rem,8vw,5.5rem)] font-display text-profound-black tracking-tighter-profound leading-[0.9] uppercase italic">
            {{ homeConfig?.title || '成就卓越' }} <br />
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-primary via-indigo-500 to-indigo-900 drop-shadow-sm">
              {{ homeConfig?.highlightTitle || '毕业作品' }}
            </span>
          </h1>
          
          <p class="text-slate-400 text-base md:text-lg max-w-xl font-bold leading-relaxed opacity-90 border-l-4 border-primary/20 pl-8 uppercase tracking-wide">
            {{ homeConfig?.subtitle || '汇集顶级毕业课题，涵盖高质源码与全栈思维指导。每一份交付，都是对技术灵魂的深度刻画。' }}
          </p>

          <div class="flex flex-col sm:flex-row items-center gap-10 pt-6">
            <NuxtLink to="/projects" class="group relative px-12 py-5 overflow-hidden rounded-2xl transition-all hover:scale-105 active:scale-95 shadow-2xl shadow-primary/20">
              <div class="absolute inset-0 bg-primary group-hover:bg-profound-black transition-colors duration-500"></div>
              <span class="relative z-10 text-white text-lg font-bold tracking-widest uppercase flex items-center gap-3">
                开始探索项目 <LucideArrowRight :size="20" class="group-hover:translate-x-1 transition-transform" />
              </span>
            </NuxtLink>
            
            <div class="flex items-center gap-4">
              <div class="flex -space-x-3">
                 <div v-for="i in 4" :key="i" class="w-12 h-12 rounded-full border-2 border-white overflow-hidden bg-slate-50 shadow-sm">
                    <img :src="`https://api.dicebear.com/7.x/avataaars/svg?seed=user${i + 10}`" alt="user" />
                 </div>
                 <div class="w-12 h-12 rounded-full border-2 border-white bg-primary/10 flex items-center justify-center text-[10px] font-bold text-primary shadow-sm uppercase tracking-tighter">
                   +12k
                 </div>
              </div>
              <p class="text-slate-400 font-bold text-[10px] uppercase tracking-profound max-w-[120px] leading-tight">Trusted by elite students globally</p>
            </div>
          </div>
          </div>

          <!-- Hero Right Visual -->
          <div class="hidden lg:block relative">
            <HeroVisual />
          </div>
        </div>
        
        <!-- Refined Scroll Indicator -->
        <div class="absolute bottom-6 left-1/2 -translate-x-1/2 flex flex-col items-center gap-3 opacity-20">
          <span class="text-[9px] font-bold uppercase tracking-profound rotate-90 mb-6">Scroll</span>
          <div class="w-px h-16 bg-gradient-to-b from-primary to-transparent"></div>
        </div>
      </section>

      <!-- Featured Projects (Refined Grid) -->
      

      <!-- Why Choose Us (Theme Aligned) -->
      <section class="pb-48">
        <div class="relative py-24">
          <!-- Subtle Decorative Elements -->
          <div class="absolute top-0 right-0 w-[600px] h-[600px] bg-primary/5 rounded-full blur-[140px] pointer-events-none opacity-50"></div>
          
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-24 relative z-10 items-center">
            <div class="space-y-20">
               <div class="space-y-8">
                 <div class="inline-flex items-center gap-3 px-5 py-2 rounded-xl bg-primary/5 border border-primary/10 text-primary text-[10px] font-black uppercase tracking-[0.25em] italic">
                   Technical Core Excellence · 核心技术卓越
                 </div>
                 <h2 class="text-[clamp(2.5rem,6vw,5rem)] font-display text-profound-black leading-[0.95] tracking-tighter-profound uppercase italic">
                   不仅提供源码<br />
                   <span class="text-transparent bg-clip-text bg-gradient-to-r from-primary via-indigo-600 to-indigo-900 drop-shadow-sm">更注入技术灵魂</span>
                 </h2>
               </div>
               
               <div class="grid grid-cols-1 sm:grid-cols-2 gap-x-20 gap-y-24">
                 <div v-for="(item, idx) in features" :key="item.title" class="space-y-8 group/item relative">
                    <!-- Ranking Number (Subtle Gray) -->
                    <div class="absolute -top-14 -left-10 text-8xl font-black text-slate-100/80 select-none group-hover/item:text-primary/5 transition-colors duration-700 italic">
                      0{{ idx + 1 }}
                    </div>
                    
                    <div class="w-20 h-20 rounded-[32px] bg-slate-50 flex items-center justify-center text-primary group-hover/item:bg-primary group-hover/item:text-white transition-all duration-700 shadow-xl shadow-slate-200/40 border border-slate-100 group-hover/item:scale-110 group-hover/item:shadow-primary/20">
                      <component :is="item.icon" :size="32" />
                    </div>
                    <div class="space-y-4">
                      <h4 class="text-2xl font-display text-profound-black italic uppercase tracking-tight group-hover/item:text-primary transition-colors border-l-4 border-primary/10 pl-6 group-hover/item:border-primary">{{ item.title }}</h4>
                      <p class="text-slate-400 text-[11px] font-bold leading-relaxed uppercase tracking-[0.2em] opacity-80 group-hover/item:opacity-100 transition-opacity">{{ item.desc }}</p>
                    </div>
                 </div>
               </div>
            </div>
            
            <div class="relative group hidden lg:block">
              <div class="absolute inset-0 bg-primary/5 blur-[120px] rounded-full animate-pulse-slow"></div>
              <img src="https://images.unsplash.com/photo-1550751827-4bd374c3f58b?q=80&w=2070&auto=format&fit=crop" 
                   class="relative z-10 rounded-[64px] border-8 border-white shadow-3xl grayscale hover:grayscale-0 transition-all duration-1000 rotate-2 group-hover:rotate-0" 
                   alt="Technical Excellence" />
              <!-- Status Tag -->
              <div class="absolute -bottom-10 -right-8 bg-white/90 backdrop-blur-2xl p-10 rounded-4xl shadow-2xl border border-slate-100 z-20">
                 <LucideGraduationCap class="text-primary opacity-5 absolute -top-8 -right-8" :size="100" />
                 <div class="relative z-10 space-y-2">
                    <div class="text-5xl font-display italic tracking-tighter uppercase leading-none text-profound-black">500+</div>
                    <div class="text-[10px] font-black uppercase tracking-[0.2em] text-primary">优质案例 / Verified Cases</div>
                 </div>
              </div>
            </div>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { 
  LucideSparkles, LucideArrowRight, LucideShieldCheck, 
  LucideSearch, LucideGraduationCap,
  LucideCode, LucideLayout, LucideMonitor, LucideEdit3,
  LucideGlobe, LucideZap, LucideLock, LucideAward
} from 'lucide-vue-next'

const api = useApi()

// Fetch Dynamic Config
const { data: res, pending } = await useAsyncData('home-config', () => 
  api('/api/configs/home')
)

const homeConfig = computed(() => res.value?.data || {})
const featuredProjects = computed(() => homeConfig.value?.recommendedProjects || [])

const features = [
  { icon: LucideCode, title: '生产级源码', desc: '每一个变量名都经过考究，标准工程化结构。' },
  { icon: LucideLayout, title: '画图 / 架构', desc: '提供完整的系统架构图、业务流程图及 UML 设计。' },
  { icon: LucideMonitor, title: '1-on-1 调试', desc: '专业导师远程协助，解决您的环境兼容性阵痛。' },
  { icon: LucideEdit3, title: '定制修改', desc: '针对具体需求提供深度定制，确保项目独一无二。' }
]
</script>
