<template>
  <div class="space-y-12">
    <!-- Header (Refined) -->
    <div class="flex items-center justify-between gap-6 pb-6 border-b border-slate-100">
      <div class="space-y-1">
        <h2 class="text-2xl font-display text-profound-black tracking-tighter uppercase italic leading-none">账户设置 / PROFILE</h2>
        <p class="text-[9px] text-slate-400 font-black uppercase tracking-widest opacity-60 italic">安全优先 · 卓越服务 · 智能驱动</p>
      </div>
    </div>

    <form class="space-y-12" @submit.prevent="handleUpdate">
      <!-- Form Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
        <div class="space-y-2">
          <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest px-1">展示昵称 / Nickname</label>
          <input v-model="form.nickname" type="text" class="input-premium py-4" placeholder="输入您的公开昵称" />
        </div>
        <div class="space-y-2">
          <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest px-1">联系手机 / Phone Number</label>
          <div class="relative">
            <input v-model="form.phone" type="text" readonly
                   class="input-premium py-4 opacity-50 bg-slate-100/50 cursor-not-allowed border-dashed" />
            <span class="absolute right-4 top-1/2 -translate-y-1/2 text-[8px] font-black text-primary uppercase tracking-widest">已验证 / VERIFIED</span>
          </div>
        </div>
      </div>

      <div class="space-y-2">
        <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest px-1">个人简介 / Biography</label>
        <textarea v-model="form.bio" rows="6" 
                  placeholder="简单介绍一下您的技术背景、毕业设计选题或研究方向..."
                  class="input-premium min-h-[160px] resize-none leading-relaxed py-6"></textarea>
      </div>

      <div class="pt-4 flex items-center justify-between gap-6">
        <div class="text-[9px] text-slate-300 font-bold uppercase tracking-widest italic max-w-xs leading-loose">
          提示: 修改昵称后将在全站同步生效。您的手机号作为核心身份验证凭据，暂不支持自助修改。
        </div>
        <button type="submit" :disabled="loading"
                class="btn-premium px-12 py-4 shadow-primary/20 shrink-0">
          <LucideSave v-if="!loading" :size="16" />
          <span v-else class="w-3 h-3 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
          {{ loading ? '正在保存...' : '立即更新个人资料' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { LucideCamera, LucideSave } from 'lucide-vue-next'

const authStore = useAuthStore()
const api = useApi()
const loading = ref(false)

const form = reactive({
  nickname: authStore.userInfo?.nickname || '',
  avatar: authStore.userInfo?.avatar || '',
  phone: authStore.userInfo?.phone || '',
  bio: authStore.userInfo?.bio || ''
})

watch(() => authStore.userInfo, (newVal) => {
  if (newVal) {
    form.nickname = newVal.nickname
    form.avatar = newVal.avatar
    form.phone = newVal.phone
    form.bio = newVal.bio
  }
}, { immediate: true })

const handleUpdate = async () => {
  if (!form.nickname) {
    ElMessage.warning('昵称不能为空')
    return
  }
  
  loading.value = true
  try {
    const res = await api('/api/user/update', {
      method: 'POST',
      body: form
    })
    
    if (res.code === 200) {
      ElMessage.success('账户信息已更新')
      await authStore.fetchUserInfo()
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (e) {
    console.error('Update Profile Error:', e)
    ElMessage.error('服务连接异常')
  } finally {
    loading.value = false
  }
}
</script>
