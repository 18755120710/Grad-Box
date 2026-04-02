<template>
  <div class="space-y-10 animate-fade-in-up">
    <div class="flex flex-col md:flex-row items-center justify-between gap-6 pb-2 border-b border-slate-100/50">
      <div class="flex p-1.5 bg-slate-100/50 border border-slate-200/50 rounded-2xl gap-1">
        <button v-for="tab in filterTabs" :key="tab.id"
                @click="activeFilter = tab.id"
                class="px-6 py-2.5 rounded-xl text-[10px] font-black transition-all duration-500 uppercase tracking-widest whitespace-nowrap"
                :class="activeFilter === tab.id ? 'bg-white text-profound-black shadow-lg scale-[1.02] border-subtle' : 'text-slate-400 hover:text-slate-600'">
          {{ tab.label }}
        </button>
      </div>
      
      <button @click="handleClearHistory" v-if="historyRecords.length > 0"
              class="flex items-center gap-2 px-6 py-3 text-red-500/60 font-black text-[9px] uppercase tracking-widest hover:bg-red-50/50 rounded-xl transition-all group">
        <LucideTrash2 :size="14" class="opacity-40 group-hover:opacity-100 transition-opacity" /> 清空全部记录
      </button>
    </div>

    <div v-if="pending" class="flex flex-col items-center justify-center py-32 space-y-6">
      <div class="w-12 h-12 border-2 border-primary/10 border-t-primary rounded-full animate-spin"></div>
      <p class="text-[9px] font-black text-slate-400 uppercase tracking-profound animate-pulse">正在加载您的足迹...</p>
    </div>

    <div v-else-if="historyRecords.length === 0" class="py-32 text-center space-y-8">
      <div class="w-24 h-24 bg-slate-50 rounded-4xl flex items-center justify-center text-slate-200 mx-auto relative group">
        <div class="absolute inset-0 bg-primary/5 rounded-4xl scale-0 group-hover:scale-125 transition-transform duration-700 opacity-0 group-hover:opacity-100"></div>
        <LucideHistory :size="40" class="relative group-hover:text-primary transition-colors duration-500" />
      </div>
      <div class="space-y-3">
        <h3 class="text-2xl font-display text-profound-black tracking-tighter uppercase italic">暂无浏览历史 / EMPTY</h3>
        <p class="text-slate-400 font-bold uppercase tracking-profound text-[9px]">您在 GradBox 探索的每一部作品都将记录于此</p>
      </div>
      <NuxtLink to="/projects" class="btn-premium px-12 py-5 inline-flex font-black text-[10px]">继续探索项目</NuxtLink>
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 items-start">
      <div v-for="item in historyRecords" :key="item.id" 
           class="glass-premium p-5 rounded-[32px] group hover:-translate-y-2 transition-all duration-700 relative overflow-hidden border-white/40 shadow-2xl shadow-slate-200/30">
        
        <div class="space-y-4">
          <div class="relative aspect-[16/10] rounded-2xl overflow-hidden bg-slate-50 border border-slate-100">
            <div class="absolute inset-0 bg-profound-black/0 group-hover:bg-profound-black/10 transition-colors duration-700 z-10"></div>
            <img :src="item.project?.coverImage || 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?q=80&w=2070&auto=format&fit=crop'" 
                 class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-1000" />
            <button @click="handleDeleteRecord(item.id)" class="absolute top-3 right-3 z-20 w-8 h-8 rounded-full bg-white/80 backdrop-blur-md flex items-center justify-center text-slate-400 hover:text-red-500 hover:bg-white transition-all shadow-sm opacity-0 group-hover:opacity-100">
              <LucideX :size="14" />
            </button>
          </div>
          
          <div class="space-y-2">
            <h4 class="text-base font-display text-profound-black tracking-tight group-hover:text-primary transition-colors duration-500 truncate leading-tight uppercase">
              {{ item.project?.title || '未知项目' }}
            </h4>
            <p class="text-slate-400 text-[10px] font-bold line-clamp-2 leading-relaxed uppercase tracking-wider opacity-70 group-hover:opacity-90 transition-opacity">
              {{ item.project?.description || 'No description available for this masterpiece.' }}
            </p>
          </div>

          <div class="flex items-center justify-between pt-4 border-t border-slate-50">
            <span class="text-[8px] font-black text-slate-300 uppercase tracking-widest flex items-center gap-1.5 group-hover:text-slate-400 transition-colors">
              <LucideClock :size="10" /> {{ formatRelativeTime(item.viewTime) }}
            </span>
            <NuxtLink :to="`/projects/${item.projectId}`" class="text-[9px] font-black text-primary uppercase tracking-widest hover:text-profound-black transition-colors flex items-center gap-1">
               VIEW <LucideChevronRight :size="10" />
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination (Refined) -->
    <div v-if="totalPages > 1" class="flex justify-center pt-12">
      <el-pagination 
        background 
        layout="prev, pager, next" 
        :total="totalRecords" 
        :page-size="pageSize"
        v-model:current-page="pageNum"
        @current-change="handlePageChange"
        class="premium-pagination"
      />
    </div>
  </div>
</template>

<script setup>
import { 
  LucideHistory, LucideTrash2, LucideClock, LucideX, LucideChevronRight 
} from 'lucide-vue-next'

// ElMessage and ElMessageBox are auto-imported by @element-plus/nuxt

const api = useApi()
const activeFilter = ref('all')
const pageNum = ref(1)
const pageSize = ref(10)

const filterTabs = [
  { id: 'all', label: '全部 / All' },
  { id: 'day', label: '今日 / Today' },
  { id: 'week', label: '本周 / Week' },
  { id: 'month', label: '本月 / Month' }
]

const { data: res, pending, refresh } = await useAsyncData('my-history-list', () => 
  api('/api/history/list', { 
    params: { 
      type: activeFilter.value === 'all' ? undefined : activeFilter.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    } 
  }), {
    watch: [activeFilter, pageNum]
  }
)

const historyRecords = computed(() => res.value?.data?.records || [])
const totalRecords = computed(() => res.value?.data?.total || 0)
const totalPages = computed(() => res.value?.data?.pages || 0)

const handlePageChange = (page) => {
  pageNum.value = page
}

const handleDeleteRecord = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条浏览记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api(`/api/history/${id}`, { method: 'DELETE' })
    ElMessage.success('已删除')
    refresh()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('Delete history failed:', e)
      ElMessage.error('删除失败')
    }
  }
}

const handleClearHistory = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有浏览记录吗？此操作不可撤销。', '警告', {
      confirmButtonText: '清空全部',
      cancelButtonText: '保留',
      type: 'error'
    })
    await api('/api/history/clear', { method: 'DELETE' })
    ElMessage.success('历史记录已清空')
    refresh()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('Clear history failed:', e)
      ElMessage.error('清空失败')
    }
  }
}

const formatRelativeTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`
  
  return date.toLocaleDateString()
}
</script>

<style scoped>
.premium-pagination :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #020617;
  border-radius: 12px;
}
.premium-pagination :deep(.el-pagination.is-background .el-pager li) {
  border-radius: 12px;
  background-color: white;
  border: 1px solid #f1f5f9;
  font-weight: 700;
  color: #64748b;
}
</style>
