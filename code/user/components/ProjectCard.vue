<script setup>
import { computed } from 'vue'
const props = defineProps({
  project: {
    type: Object,
    required: true
  }
})

// Dynamic categorical color mapping
const getCategoryStyles = (category) => {
  const cat = category?.toLowerCase() || ''
  if (cat.includes('计算机') || cat.includes('软件') || cat.includes('cs')) {
    return { text: 'text-genre-cs', bg: 'bg-genre-cs/10', border: 'border-genre-cs/20', glow: 'shadow-genre-cs/20' }
  }
  if (cat.includes('艺术') || cat.includes('设计') || cat.includes('art')) {
    return { text: 'text-genre-art', bg: 'bg-genre-art/10', border: 'border-genre-art/20', glow: 'shadow-genre-art/20' }
  }
  if (cat.includes('管理') || cat.includes('经管') || cat.includes('mgt')) {
    return { text: 'text-genre-mgt', bg: 'bg-genre-mgt/10', border: 'border-genre-mgt/20', glow: 'shadow-genre-mgt/20' }
  }
  if (cat.includes('理学') || cat.includes('科学') || cat.includes('sci')) {
    return { text: 'text-genre-sci', bg: 'bg-genre-sci/10', border: 'border-genre-sci/20', glow: 'shadow-genre-sci/20' }
  }
  return { text: 'text-genre-other', bg: 'bg-genre-other/10', border: 'border-genre-other/20', glow: 'shadow-genre-other/20' }
}

const catStyles = computed(() => getCategoryStyles(props.project.major || props.project.categoryName))
</script>

<template>
  <NuxtLink :to="`/projects/${project.id}`" :class="['group relative glass-premium rounded-[48px] overflow-hidden transition-all duration-300 flex flex-col h-full hover:shadow-xl hover:border-primary/20', catStyles.glow]">
    <!-- Image Section -->
    <div class="relative aspect-[16/10] overflow-hidden m-4 rounded-[32px]">
      <img :src="project.coverImage || 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?q=80&w=2070&auto=format&fit=crop'" 
           :alt="project.title"
           class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-[1.02]" />
      <div class="absolute inset-0 bg-gradient-to-t from-slate-900/60 via-transparent to-transparent opacity-60"></div>
      
      <!-- Tech Badges Group -->
      <div class="absolute top-4 left-4 flex flex-wrap gap-2 max-w-[80%]">
        <span v-for="tag in project.techStack?.split(',').slice(0, 2)" :key="tag"
              class="px-4 py-1.5 bg-white/20 backdrop-blur-lg text-white border border-white/30 text-[9px] uppercase tracking-profound font-bold rounded-lg whitespace-nowrap">
          {{ tag }}
        </span>
      </div>
    </div>

    <!-- Content Section -->
    <div class="p-8 pt-2 flex flex-col flex-grow space-y-5">
      <div class="space-y-2">
        <div class="flex items-center gap-2">
          <span :class="['text-[9px] font-heading font-bold uppercase tracking-profound px-2.5 py-1 rounded-md border', catStyles.text, catStyles.bg, catStyles.border]">
            {{ project.major || project.categoryName || '毕业设计' }}
          </span>
        </div>
        <h3 class="text-xl font-heading font-bold text-slate-900 line-clamp-2 group-hover:text-primary transition-colors leading-snug tracking-tight uppercase">
          {{ project.title }}
        </h3>
      </div>

      <p class="text-slate-500 text-sm line-clamp-2 font-medium leading-relaxed opacity-70 h-10">
        {{ project.description }}
      </p>
      
      <div class="mt-auto flex items-end justify-between border-t border-slate-50 pt-6">
        <div class="flex flex-col">
          <span class="text-[9px] uppercase tracking-[0.2em] text-slate-400 font-bold mb-1">参考标价 / Price</span>
          <div class="flex items-baseline gap-1">
            <span :class="['text-xs font-bold', catStyles.text]">¥</span>
            <span class="text-3xl font-heading font-bold text-profound-black tracking-tighter">{{ project.price }}</span>
          </div>
        </div>
        
        <div class="flex flex-col items-end gap-3">
          <div class="flex -space-x-3">
            <div v-for="i in 3" :key="i" class="w-10 h-10 rounded-full border-4 border-white bg-slate-100 flex items-center justify-center overflow-hidden shadow-md">
               <img :src="`https://i.pravatar.cc/150?u=${i+project.id}`" class="w-full h-full object-cover" />
            </div>
            <div :class="['w-10 h-10 rounded-full border-4 border-white flex items-center justify-center text-[10px] text-white font-black shadow-md', catStyles.bg.replace('/10', '') || 'bg-indigo-600']">
              +{{ (project.id % 20) + 5 }}
            </div>
          </div>
          <span class="text-[10px] font-black text-slate-300 uppercase tracking-widest">
            {{ project.viewCount >= 1000 ? (project.viewCount / 1000).toFixed(1) + 'k' : project.viewCount }} Views
          </span>
        </div>
      </div>
    </div>
  </NuxtLink>
</template>
