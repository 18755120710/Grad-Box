<template>
  <div class="relative w-full h-[400px] lg:h-[500px] flex items-center justify-center perspective-1000 overflow-visible scale-90 lg:scale-100 translate-y-12 lg:translate-y-20">
    <!-- Background Glow System -->
    <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[120%] h-[120%] bg-primary/10 blur-[120px] rounded-full animate-pulse-slow"></div>
    <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[80%] h-[80%] bg-indigo-400/5 blur-[80px] rounded-full animate-float" style="animation-duration: 8s"></div>
    
    <!-- Floating "Grad Box" Core -->
    <div class="relative z-10 w-64 h-64 group perspective-1000">
      <!-- 3D Box Representation -->
      <div class="absolute inset-0 bg-white/40 backdrop-blur-3xl border border-white/40 rounded-[48px] shadow-[0_32px_64px_-16px_rgba(79,70,229,0.15)] rotate-6 group-hover:rotate-0 group-hover:scale-105 transition-all duration-1000 ease-[cubic-bezier(0.34,1.56,0.64,1)] flex items-center justify-center overflow-hidden">
        <!-- Shimmer Effect -->
        <div class="absolute -inset-full bg-gradient-to-tr from-transparent via-white/30 to-transparent rotate-45 animate-shimmer pointer-events-none"></div>
        
        <!-- Subtle Icon Pattern -->
        <LucideGraduationCap :size="120" class="text-primary/5 absolute -bottom-6 -right-6 rotate-12" />
        
        <!-- Content -->
        <div class="relative z-10 text-center space-y-3">
            <div class="inline-flex px-3 py-1 rounded-full bg-primary/10 text-[9px] font-black uppercase tracking-[0.4em] text-primary mb-2">技术平台 / Platform</div>
            <div class="text-5xl font-display italic text-profound-black uppercase leading-none tracking-tighter-profound">Grad</div>
            <div class="text-5xl font-display italic text-transparent bg-clip-text bg-gradient-to-r from-primary to-indigo-900 uppercase leading-none tracking-tighter-profound">Box</div>
        </div>
      </div>
      
      <!-- Orbiting Elements -->
      <div v-for="(item, index) in orbitItems" :key="index" 
           class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-full animate-orbit"
           :style="{ 
             animationDelay: `-${index * 3}s`, 
             '--orbit-radius': isMobile ? '140px' : '220px',
             '--orbit-duration': '12s'
           }">
        <div class="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-1/2 p-4 lg:p-5 rounded-3xl bg-white/90 backdrop-blur-2xl shadow-xl border border-white/50 flex items-center gap-3 hover:scale-110 hover:shadow-2xl hover:shadow-primary/20 transition-all cursor-default group/item">
          <div class="w-10 h-10 rounded-2xl bg-slate-50 flex items-center justify-center text-primary group-hover/item:bg-primary group-hover/item:text-white transition-colors">
            <component :is="item.icon" :size="20" class="group-hover/item:rotate-12 transition-transform duration-500" />
          </div>
          <div class="flex flex-col">
            <span class="text-[8px] font-black uppercase tracking-[0.2em] text-primary/40 leading-none mb-1">{{ item.tag }}</span>
            <span class="text-[11px] font-bold uppercase tracking-wider text-profound-black whitespace-nowrap">{{ item.label }}</span>
          </div>
        </div>
      </div>
      
      <!-- Orbit Rings (Visual Guide) -->
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[440px] h-[440px] border border-primary/5 rounded-full pointer-events-none hidden lg:block"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  LucideGraduationCap, LucideCode, LucideCpu, 
  LucideGlobe, LucideSmartphone, LucideLayers
} from 'lucide-vue-next'

const isMobile = ref(false)

onMounted(() => {
  isMobile.value = window.innerWidth < 1024
  window.addEventListener('resize', () => {
    isMobile.value = window.innerWidth < 1024
  })
})

const orbitItems = [
  { icon: LucideCode, label: '全栈开发', tag: '技术工程' },
  { icon: LucideCpu, label: '人工智能', tag: '算法核心' },
  { icon: LucideGlobe, label: '网页交互', tag: '视觉设计' },
  { icon: LucideLayers, label: '分层架构', tag: '系统架构' }
]
</script>

<style scoped>
.perspective-1000 {
  perspective: 1000px;
}

@keyframes orbit {
  from { transform: translate(-50%, -50%) rotate(0deg) translateX(var(--orbit-radius)) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(360deg) translateX(var(--orbit-radius)) rotate(-360deg); }
}

@keyframes shimmer {
  0% { transform: translateX(-150%) rotate(45deg); opacity: 0; }
  20% { opacity: 1; }
  80% { opacity: 1; }
  100% { transform: translateX(150%) rotate(45deg); opacity: 0; }
}

@keyframes pulse-slow {
  0%, 100% { opacity: 0.3; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 0.6; transform: translate(-50%, -50%) scale(1.1); }
}

.animate-orbit {
  animation: orbit var(--orbit-duration) linear infinite;
}

.animate-shimmer {
  animation: shimmer 4s infinite cubic-bezier(0.4, 0, 0.2, 1);
}

.animate-pulse-slow {
  animation: pulse-slow 8s ease-in-out infinite;
}

/* Float already defined in tailwind but we can refine here */
.animate-float {
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(-50%, -50%) translateY(0); }
  50% { transform: translate(-50%, -50%) translateY(-20px); }
}
</style>
