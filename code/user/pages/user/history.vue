<template>
  <div class="space-y-8">
    <div class="flex items-center justify-between">
      <div class="flex gap-2 p-1.5 bg-white border border-slate-100 rounded-[28px] shadow-sm">
        <button v-for="tab in filterTabs" :key="tab.id"
                @click="activeFilter = tab.id"
                class="px-8 py-3 rounded-[20px] text-sm font-black transition-all duration-300"
                :class="activeFilter === tab.id ? 'bg-slate-900 text-white shadow-lg' : 'text-slate-400 hover:text-slate-600 hover:bg-slate-50'">
          {{ tab.label }}
        </button>
      </div>
      
      <button @click="handleClearHistory" v-if="historyRecords.length > 0"
              class="flex items-center gap-2 px-6 py-3 text-red-500 font-black text-sm hover:bg-red-50 rounded-2xl transition-all">
        <LucideTrash2 :size="16" /> 清空历史
      </button>
    </div>

    <div v-if="pending" class="flex items-center justify-center py-24">
      <div class="w-12 h-12 border-4 border-blue-600/20 border-t-blue-600 rounded-full animate-spin"></div>
    </div>

    <div v-else-if="historyRecords.length === 0" class="glass-card bg-white p-24 rounded-[48px] text-center space-y-6 border border-slate-100">
      <div class="w-24 h-24 bg-slate-50 rounded-[32px] flex items-center justify-center text-slate-200 mx-auto">
        <LucideHistory :size="48" />
      </div>
      <div>
        <h3 class="text-2xl font-black text-slate-800">暂无浏览记录</h3>
        <p class="text-slate-400 font-bold">快去看看那些令人惊叹的项目吧</p>
      </div>
      <NuxtLink to="/projects" class="px-10 py-5 bg-slate-900 text-white font-black rounded-3xl hover:bg-blue-600 transition-all inline-block">去浏览项目</NuxtLink>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-8 items-start">
      <div v-for="item in historyRecords" :key="item.id" 
           class="glass-card bg-white p-6 rounded-[40px] border border-slate-100 shadow-xl shadow-slate-200/30 group hover:shadow-blue-100/50 transition-all duration-500 relative overflow-hidden">
        
        <div class="flex gap-6">
          <div class="w-24 h-24 rounded-3xl overflow-hidden flex-shrink-0 bg-slate-100 border border-slate-50">
            <img :src="item.project?.coverImage || 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?q=80&w=2070&auto=format&fit=crop'" class="w-full h-full object-cover group-hover:scale-110 transition-duration-700" />
          </div>
          
          <div class="flex-1 min-w-0 space-y-2">
            <h4 class="text-lg font-black text-slate-900 truncate group-hover:text-blue-600 transition-colors">
              {{ item.project?.title || '未知项目' }}
            </h4>
            <p class="text-slate-400 text-sm font-bold line-clamp-2">
              {{ item.project?.description || '暂无描述' }}
            </p>
            <div class="flex items-center justify-between pt-2">
              <span class="text-[10px] font-black text-slate-300 uppercase tracking-widest flex items-center gap-2">
                <LucideClock :size="12" /> {{ formatRelativeTime(item.viewTime) }}
              </span>
              <div class="flex gap-2">
                 <button @click="handleDeleteRecord(item.id)" class="p-2 text-slate-300 hover:text-red-500 transition-colors">
                   <LucideX :size="16" />
                 </button>
                 <NuxtLink :to="`/projects/${item.projectId}`" class="p-2 text-slate-300 hover:text-blue-600 transition-colors">
                    <LucideExternalLink :size="16" />
                 </NuxtLink>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex justify-center pt-8">
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
  LucideHistory, LucideTrash2, LucideClock, LucideX, LucideExternalLink 
} from 'lucide-vue-next'

const api = useApi()
const activeFilter = ref('all')
const pageNum = ref(1)
const pageSize = ref(10)

const filterTabs = [
  { id: 'all', label: '全部' },
  { id: 'day', label: '今日' },
  { id: 'week', label: '本周' },
  { id: 'month', label: '本月' }
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
  if (!confirm('确定要删除这条浏览记录吗？')) return
  try {
    await api(`/api/history/${id}`, { method: 'DELETE' })
    refresh()
  } catch (e) {
    console.error('Delete history failed:', e)
  }
}

const handleClearHistory = async () => {
  if (!confirm('确定要清空所有浏览记录吗？')) return
  try {
    await api('/api/history/clear', { method: 'DELETE' })
    refresh()
  } catch (e) {
    console.error('Clear history failed:', e)
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
  background-color: #0f172a;
}
</style>
