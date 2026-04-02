<template>
  <div class="space-y-12 animate-fade-in-up">
    <!-- Header (Refined) -->
    <div class="flex items-center justify-between gap-6 pb-6 border-b border-slate-100">
      <div class="space-y-1">
        <h2 class="text-2xl font-display text-profound-black tracking-tighter uppercase italic leading-none">账户设置 / Profile</h2>
        <p class="text-[9px] text-slate-400 font-black uppercase tracking-widest opacity-60">Security First · Curated Excellence</p>
      </div>
    </div>

    <form class="space-y-12" @submit.prevent="handleUpdate">
      <!-- Avatar Section (Pro Max) -->
      <div class="flex flex-col md:flex-row items-center gap-10 pb-10 border-b border-slate-50">
        <div class="relative group cursor-pointer">
          <div class="absolute -inset-2 bg-gradient-to-br from-primary to-indigo-600 rounded-full blur opacity-10 group-hover:opacity-30 transition-opacity"></div>
          <el-avatar :size="100" :src="form.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" class="relative border-4 border-white shadow-xl" />
          <div class="absolute inset-0 bg-profound-black/60 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-500 backdrop-blur-sm">
            <LucideCamera class="text-white" :size="24" />
          </div>
        </div>
        <div class="text-center md:text-left space-y-2">
          <h4 class="font-display text-profound-black uppercase text-xs tracking-tight">个人头像 / Avatar</h4>
          <p class="text-[8px] text-slate-400 font-bold uppercase tracking-profound">JPG, PNG MAX 2MB · 400x400</p>
          <button type="button" class="mt-3 px-5 py-2 bg-slate-50 text-slate-500 rounded-xl text-[9px] font-bold border border-slate-100 hover:bg-white hover:border-primary hover:text-primary transition-all uppercase tracking-profound">
            更改头像
          </button>
        </div>
      </div>

      <!-- Form Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
        <div class="space-y-2">
          <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest px-1">展示昵称 / Nickname</label>
          <input v-model="form.nickname" type="text" class="input-premium py-4" placeholder="输入您的公开昵称" />
        </div>
        <div class="space-y-2">
          <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest px-1">联系手机 / Phone</label>
          <div class="relative">
            <input v-model="form.phone" type="text" readonly
                   class="input-premium py-4 opacity-50 bg-slate-100/50 cursor-not-allowed border-dashed" />
            <span class="absolute right-4 top-1/2 -translate-y-1/2 text-[8px] font-black text-primary uppercase tracking-widest">VERIFIED</span>
          </div>
        </div>
      </div>

      <div class="space-y-2">
        <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest px-1">个人简介 / Bio</label>
        <textarea v-model="form.bio" rows="4" 
                  placeholder="简单介绍一下您的技术背景或毕业设计选题..."
                  class="input-premium min-h-[140px] resize-none leading-relaxed py-5"></textarea>
      </div>

      <div class="pt-4">
        <button type="submit" :disabled="loading"
                class="btn-premium px-12 py-4 shadow-primary/20">
          <LucideSave v-if="!loading" :size="16" />
          <span v-else class="w-3 h-3 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
          {{ loading ? 'SAVING...' : 'UPDATE PROFILE' }}
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
