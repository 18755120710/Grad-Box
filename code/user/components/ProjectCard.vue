<template>
  <div class="group relative bg-white/80 backdrop-blur-2xl rounded-[40px] overflow-hidden border border-slate-100 shadow-2xl shadow-slate-200/40 hover:shadow-blue-500/10 transition-all duration-700 hover:-translate-y-3 flex flex-col h-full">
    <!-- Image Section -->
    <div class="relative aspect-[16/10] overflow-hidden m-3 rounded-[32px] group-hover:scale-[0.98] transition-transform duration-500">
      <img :src="project.coverImage || 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?q=80&w=2070&auto=format&fit=crop'" 
           :alt="project.title"
           class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" />
      <div class="absolute inset-0 bg-gradient-to-t from-slate-900/40 via-transparent to-transparent"></div>
      
      <!-- Tech Badges Group -->
      <div class="absolute top-4 left-4 flex flex-wrap gap-2 max-w-[80%]">
        <span v-for="tag in project.techStack?.split(',').slice(0, 2)" :key="tag"
              class="px-4 py-1.5 bg-slate-900/60 backdrop-blur-md text-white border border-white/20 text-[10px] uppercase tracking-widest font-black rounded-full whitespace-nowrap">
          {{ tag }}
        </span>
      </div>

      <!-- Quick Action Overlay -->
      <div class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-500 bg-blue-600/10 backdrop-blur-[2px]">
        <NuxtLink :to="`/projects/${project.id}`" 
                  class="px-10 py-4 bg-white text-slate-900 rounded-[20px] text-sm font-black shadow-2xl hover:bg-slate-900 hover:text-white transition-all transform hover:scale-110">
          探索详情
        </NuxtLink>
      </div>
    </div>

    <!-- Content Section -->
    <div class="p-8 pt-4 flex flex-col flex-grow space-y-4">
      <div class="space-y-2">
        <div class="flex items-center gap-2">
          <span v-if="project.major" class="text-[10px] font-black text-blue-600 uppercase tracking-widest bg-blue-50 px-2 py-0.5 rounded-lg">
            {{ project.major }}
          </span>
          <span v-else class="text-[10px] font-black text-slate-400 uppercase tracking-widest">{{ project.categoryName || '毕业设计' }}</span>
        </div>
        <h3 class="text-2xl font-black text-slate-900 line-clamp-1 group-hover:text-blue-600 transition-colors leading-tight tracking-tight">
          {{ project.title }}
        </h3>
      </div>

      <p class="text-slate-500 text-sm line-clamp-2 font-medium leading-relaxed opacity-80 h-10">
        {{ project.description }}
      </p>
      
      <div class="mt-auto flex items-end justify-between border-t border-slate-50 pt-6">
        <div class="flex flex-col">
          <span class="text-[10px] uppercase tracking-[0.2em] text-slate-400 font-black mb-1">参考标价</span>
          <div class="flex items-baseline gap-1">
            <span class="text-sm font-black text-blue-600">¥</span>
            <span class="text-3xl font-black text-slate-900 tracking-tighter">{{ project.price }}</span>
          </div>
        </div>
        
        <div class="flex flex-col items-end gap-2">
          <div class="flex -space-x-2.5">
            <div v-for="i in 3" :key="i" class="w-8 h-8 rounded-full border-2 border-white bg-slate-100 flex items-center justify-center overflow-hidden shadow-sm">
               <img :src="`https://i.pravatar.cc/150?u=${i+project.id}`" class="w-full h-full object-cover" />
            </div>
            <div class="w-8 h-8 rounded-full border-2 border-white bg-blue-600 flex items-center justify-center text-[10px] text-white font-black shadow-sm">
              +{{ (project.id % 20) + 5 }}
            </div>
          </div>
          <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest">
            {{ project.viewCount >= 1000 ? (project.viewCount / 1000).toFixed(1) + 'k' : project.viewCount }} 浏览量
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  project: {
    type: Object,
    required: true
  }
})
</script>
