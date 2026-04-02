<template>
  <div class="space-y-10 animate-fade-in-up">
    <div v-if="pending" class="flex flex-col items-center justify-center py-32 space-y-6">
      <div class="w-12 h-12 border-2 border-primary/10 border-t-primary rounded-full animate-spin"></div>
      <p class="text-[9px] font-bold text-slate-400 uppercase tracking-profound animate-pulse">Syncing order history...</p>
    </div>

    <div v-else-if="orders.length === 0" class="glass-card bg-white py-32 rounded-5xl text-center space-y-8 border border-slate-100">
      <div class="w-24 h-24 bg-slate-50 rounded-4xl flex items-center justify-center text-slate-200 mx-auto relative group">
        <div class="absolute inset-0 bg-primary/5 rounded-4xl scale-0 group-hover:scale-125 transition-transform duration-700 opacity-0 group-hover:opacity-100"></div>
        <LucideBox :size="40" class="relative group-hover:text-primary transition-colors duration-500" />
      </div>
      <div class="space-y-3">
        <h3 class="text-2xl font-display text-profound-black tracking-tighter uppercase italic">暂无订单 / Orders</h3>
        <p class="text-slate-400 font-bold uppercase tracking-profound text-[10px]">Your transaction history is empty</p>
      </div>
      <NuxtLink to="/projects" class="btn-premium px-12 py-5 inline-flex">浏览推荐项目 / EXPLORE</NuxtLink>
    </div>

    <div v-else class="space-y-10">
      <div v-for="order in orders" :key="order.id" 
           class="glass-card bg-white p-8 md:p-12 rounded-4xl border border-slate-100 group hover:border-primary/20 transition-all duration-700 relative overflow-hidden">
        
        <div class="flex flex-col md:flex-row gap-12 relative z-10">
          <!-- Status & Basic Info -->
          <div class="space-y-8 min-w-[240px] pb-8 md:pb-0 md:border-r border-slate-50">
            <div class="flex items-center gap-4">
              <span class="px-4 py-1.5 rounded-xl text-[9px] font-bold uppercase tracking-profound border ring-4 ring-white" 
                    :class="statusStyles(order.status)">
                {{ statusText(order.status) }}
              </span>
              <span class="text-[9px] font-bold text-slate-300 uppercase tracking-profound">#ORD-{{ order.orderNo?.slice(-8) || order.id }}</span>
            </div>
            
            <div class="space-y-2">
              <p class="text-[9px] font-bold text-slate-400 uppercase tracking-profound px-1">成交总额 / TOTAL</p>
              <div class="text-5xl font-display text-profound-black tracking-tighter uppercase italic">
                <span class="text-lg align-top mr-1 font-sans not-italic text-primary">¥</span>{{ order.totalAmount }}
              </div>
            </div>
            <div class="flex flex-col gap-1 px-1">
              <p class="text-[9px] font-bold text-slate-300 uppercase tracking-profound">{{ formatDate(order.createTime) }}</p>
              <div class="w-8 h-1 bg-primary/20 rounded-full group-hover:w-16 transition-all duration-700"></div>
            </div>
          </div>

          <!-- Project Details -->
          <div class="flex-1 flex flex-col justify-between py-1">
             <div class="flex items-center gap-8">
               <div class="w-20 h-20 rounded-3xl bg-slate-50 border border-slate-100 flex items-center justify-center text-primary/40 font-display text-2xl group-hover:bg-white group-hover:text-primary transition-all duration-700">
                 GB
               </div>
               <div class="space-y-2">
                 <h4 class="text-2xl font-display text-profound-black tracking-tight group-hover:text-primary transition-colors duration-500">
                   项目检索 ID: {{ order.projectId }}
                 </h4>
                 <div class="flex gap-4">
                    <span class="px-3 py-1 bg-slate-50 text-slate-400 text-[8px] font-bold rounded-lg border border-slate-100 uppercase tracking-widest">Full Source</span>
                    <span class="px-3 py-1 bg-slate-50 text-slate-400 text-[8px] font-bold rounded-lg border border-slate-100 uppercase tracking-widest">Documentation</span>
                 </div>
               </div>
             </div>

             <div class="flex flex-wrap gap-4 pt-10">
               <button v-if="order.status === 2" class="btn-primary-filled px-10 py-4 shadow-primary/20">
                 <LucideDownload :size="16" /> 下载全套资料 / DOWNLOAD
               </button>
               <button class="btn-premium px-10 py-4 bg-white text-slate-500 border border-slate-100 hover:text-profound-black shadow-none">
                 <LucideActivity :size="16" /> 订单详情 / DETAILS
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
  LucideBox, LucideActivity, LucideDownload, LucideFingerprint 
} from 'lucide-vue-next'

const api = useApi()
const { data: res, pending } = await useAsyncData('my-orders-list', () => api('/api/orders/my'))
const orders = computed(() => res.value?.data?.records || [])

const statusText = (s) => {
  const map = { 0: '待支付 / PAY', 1: '待交付 / WAIT', 2: '已交付 / DONE', 3: '已取消 / VOID' }
  return map[s] || '处理中'
}

const statusStyles = (s) => {
  const map = { 
    0: 'bg-orange-50 text-orange-500 border-orange-100', 
    1: 'bg-primary/5 text-primary border-primary/10', 
    2: 'bg-emerald-50 text-emerald-500 border-emerald-100', 
    3: 'bg-slate-50 text-slate-300 border-slate-100' 
  }
  return map[s] || 'bg-slate-50 text-slate-400 border-slate-100'
}

const formatDate = (d) => {
  if (!d) return '--'
  return new Date(d).toLocaleString('zh-CN', {
    month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}
</script>
