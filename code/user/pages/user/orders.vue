<template>
  <div class="space-y-8">
    <div v-if="pending" class="flex items-center justify-center py-24">
      <div class="w-12 h-12 border-4 border-blue-600/20 border-t-blue-600 rounded-full animate-spin"></div>
    </div>

    <div v-else-if="orders.length === 0" class="glass-card bg-white p-24 rounded-[48px] text-center space-y-6">
      <div class="w-24 h-24 bg-slate-50 rounded-[32px] flex items-center justify-center text-slate-200 mx-auto">
        <LucideBox :size="48" />
      </div>
      <div>
        <h3 class="text-2xl font-black text-slate-800">虚位以待</h3>
        <p class="text-slate-400 font-bold">您还没有任何订单，去发现有趣的项目吧</p>
      </div>
      <NuxtLink to="/projects" class="btn-premium inline-block">浏览推荐项目</NuxtLink>
    </div>

    <div v-else class="space-y-8">
      <div v-for="order in orders" :key="order.id" 
           class="glass-card bg-white p-8 md:p-10 rounded-[40px] border border-slate-100 shadow-xl shadow-slate-200/40 group hover:shadow-blue-100/50 transition-all duration-500 overflow-hidden relative">
        <div class="absolute top-0 right-0 p-4 opacity-5 group-hover:opacity-10 transition-opacity">
          <LucideActivity :size="120" />
        </div>
        
        <div class="flex flex-col md:flex-row gap-10">
          <!-- Status & Basic Info -->
          <div class="space-y-6 min-w-[200px]">
            <div class="flex items-center gap-3">
              <span class="px-4 py-1.5 rounded-2xl text-[10px] font-black uppercase tracking-widest border" 
                    :class="statusStyles(order.status)">
                {{ statusText(order.status) }}
              </span>
              <span class="text-[10px] font-mono font-bold text-slate-400">#{{ order.orderNo }}</span>
            </div>
            
            <div class="space-y-1">
              <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest">成交总额</p>
              <div class="text-4xl font-black text-slate-900 font-mono tracking-tighter">¥{{ order.totalAmount }}</div>
            </div>
            <p class="text-[10px] font-bold text-slate-300">{{ formatDate(order.createTime) }}</p>
          </div>

          <!-- Project Details -->
          <div class="flex-1 space-y-6">
             <div class="p-6 bg-slate-50 rounded-[32px] border border-slate-100 flex items-center gap-6 group-hover:bg-white transition-colors duration-500">
               <div class="w-16 h-16 rounded-2xl bg-blue-100 flex items-center justify-center text-blue-600 font-black text-xl">
                 GB
               </div>
               <div class="flex-1">
                 <h4 class="text-xl font-black text-slate-800">项目检索 ID: {{ order.projectId }}</h4>
                 <p class="text-xs text-slate-400 font-bold">参考资料包（包含：源码, 数据库, 论文, 视频）</p>
               </div>
             </div>

             <div class="flex flex-wrap gap-4 pt-2">
               <button v-if="order.status === 2" class="px-8 py-3 bg-blue-600 text-white rounded-full font-black flex items-center gap-2 shadow-lg shadow-blue-200 hover:scale-105 active:scale-95 transition-all">
                 <LucideDownload :size="18" /> 下载资料包
               </button>
               <button class="px-8 py-3 bg-white text-slate-600 border border-slate-200 rounded-full font-black hover:border-blue-600 transition-all">
                 订单详情
               </button>
             </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { 
  LucideBox, LucideActivity, LucideDownload, LucidePackage 
} from 'lucide-vue-next'

const api = useApi()
const { data: res, pending } = await useAsyncData('my-orders-list', () => api('/api/orders/my'))
const orders = computed(() => res.value?.data?.records || [])

const statusText = (s) => {
  const map = { 0: '待支付', 1: '待交付', 2: '已交付', 3: '已取消' }
  return map[s] || '处理中'
}

const statusStyles = (s) => {
  const map = { 
    0: 'bg-orange-50 text-orange-600 border-orange-100', 
    1: 'bg-blue-50 text-blue-600 border-blue-100', 
    2: 'bg-green-50 text-green-600 border-green-100', 
    3: 'bg-slate-50 text-slate-400 border-slate-100' 
  }
  return map[s] || 'bg-slate-50 text-slate-400 border-slate-100'
}

const formatDate = (d) => {
  if (!d) return '--'
  return new Date(d).toLocaleString('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}
</script>
