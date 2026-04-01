<template>
  <div class="glass-card bg-white p-10 md:p-14 rounded-[48px] shadow-2xl shadow-slate-200/50 space-y-12 relative overflow-hidden">
    <div class="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-blue-600 to-indigo-600"></div>
    
    <div class="space-y-2">
      <h2 class="text-3xl font-black text-slate-900">账户设置</h2>
      <p class="text-slate-400 font-bold uppercase tracking-widest text-xs">Profile & Account Security</p>
    </div>

    <form class="space-y-10" @submit.prevent="handleUpdate">
      <!-- Avatar Section -->
      <div class="flex flex-col md:flex-row items-center gap-8 pb-8 border-b border-slate-50">
        <div class="relative group cursor-pointer">
          <div class="absolute -inset-1 bg-gradient-to-br from-blue-500 to-indigo-500 rounded-full blur opacity-25 group-hover:opacity-50 transition-opacity"></div>
          <el-avatar :size="100" :src="form.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" class="relative border-4 border-white shadow-xl" />
          <div class="absolute inset-0 bg-black/40 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
            <LucideCamera class="text-white" :size="24" />
          </div>
        </div>
        <div class="text-center md:text-left space-y-1">
          <h4 class="font-black text-slate-800">个人头像</h4>
          <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">JPG, PNG MAX 2MB</p>
          <button type="button" class="mt-2 px-4 py-2 bg-slate-50 text-slate-600 rounded-xl text-xs font-black border border-slate-100 hover:bg-white hover:border-blue-600 transition-all">更换图片</button>
        </div>
      </div>

      <!-- Form Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div class="space-y-2">
          <label class="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] px-2">用户昵称</label>
          <input v-model="form.nickname" type="text" 
                 class="w-full px-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-blue-100 focus:border-blue-600 transition-all outline-none font-bold text-slate-800" />
        </div>
        <div class="space-y-2">
          <label class="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] px-2">联系手机</label>
          <input v-model="form.phone" type="text" readonly
                 class="w-full px-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 opacity-60 font-bold text-slate-500 cursor-not-allowed" />
          <p class="text-[10px] text-blue-600 font-bold px-2">如需更换绑定手机，请联系客服</p>
        </div>
      </div>

      <div class="space-y-2">
        <label class="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] px-2">个人简介</label>
        <textarea v-model="form.bio" rows="4" 
                  placeholder="简单介绍一下您的技术背景或毕业设计选题..."
                  class="w-full px-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-blue-100 focus:border-blue-600 transition-all outline-none resize-none font-bold text-slate-800 leading-relaxed"></textarea>
      </div>

      <div class="pt-6">
        <button type="submit" :loading="loading"
                class="btn-premium px-12 py-5 font-black tracking-widest text-lg">
          <LucideSave :size="24" /> 保存账户设置
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

// Watch for store changes to populate form
watch(() => authStore.userInfo, (newVal) => {
  if (newVal) {
    form.nickname = newVal.nickname
    form.avatar = newVal.avatar
    form.phone = newVal.phone
    form.bio = newVal.bio
  }
}, { immediate: true })

const handleUpdate = async () => {
  loading.value = true
  try {
    const res = await api('/api/user/update', {
      method: 'POST',
      body: form
    })
    
    if (res.code === 200) {
      // Show success notification - using a custom alert if ElMessage is missing or use native alert for safety
      alert('个人账户信息更新成功！')
      // Refresh local user info
      await authStore.fetchUserInfo()
    } else {
      alert(res.message || '更新失败')
    }
  } catch (e) {
    console.error('Update Profile Error:', e)
    alert('连接服务器失败')
  } finally {
    loading.value = false
  }
}
</script>
