<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 relative overflow-hidden">
    <!-- Mesh Background -->
    <div class="absolute inset-0 pointer-events-none opacity-40">
      <div class="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] bg-indigo-400/20 rounded-full blur-[120px] animate-float"></div>
      <div class="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] bg-blue-400/20 rounded-full blur-[120px]"></div>
    </div>

    <div class="max-w-md w-full px-6 relative z-10 animate-fade-in-up">
      <div class="glass-card bg-white/80 p-10 md:p-12 rounded-[48px] shadow-2xl shadow-indigo-200/50 border border-white">
        <div class="text-center space-y-4 mb-10">
          <NuxtLink to="/" class="inline-flex items-center gap-2 mb-4 group">
            <div class="p-1 bg-white rounded-2xl shadow-lg group-hover:scale-110 transition-transform overflow-hidden">
              <img src="/logo.png" alt="GradBox Logo" class="w-16 h-16 object-cover" />
            </div>
            <span class="text-2xl font-black text-slate-900 tracking-tight">GradBox</span>
          </NuxtLink>
          <h2 class="text-3xl font-black text-slate-900 tracking-tight">加入我们</h2>
          <p class="text-slate-400 font-bold text-xs uppercase tracking-widest leading-loose">
            已有账号?
            <NuxtLink to="/login" class="text-indigo-600 hover:text-blue-600 underline underline-offset-4 decoration-2 transition-colors">
              立即登录
            </NuxtLink>
          </p>
        </div>

        <form class="space-y-8" @submit.prevent="handleRegister">
          <div class="space-y-6">
            <div class="group space-y-2">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest pl-2">设定用户名</label>
              <div class="relative">
                <LucideUserCircle class="absolute left-5 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-indigo-600 transition-colors" :size="18" />
                <input v-model="form.username" type="text" required
                       class="w-full pl-14 pr-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-indigo-100 focus:border-indigo-600 transition-all outline-none font-bold text-slate-800"
                       placeholder="Username">
              </div>
            </div>

            <div class="group space-y-2">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest pl-2">您的昵称</label>
              <div class="relative">
                <LucideUser class="absolute left-5 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-indigo-600 transition-colors" :size="18" />
                <input v-model="form.nickname" type="text" required
                       class="w-full pl-14 pr-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-indigo-100 focus:border-indigo-600 transition-all outline-none font-bold text-slate-800"
                       placeholder="Your Name">
              </div>
            </div>
            
            <div class="group space-y-2">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest pl-2">设置密码</label>
              <div class="relative">
                <LucideShieldLock class="absolute left-5 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-indigo-600 transition-colors" :size="18" />
                <input v-model="form.password" type="password" required
                       class="w-full pl-14 pr-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-indigo-100 focus:border-indigo-600 transition-all outline-none font-bold text-slate-800"
                       placeholder="Min 6 characters">
              </div>
            </div>
          </div>

          <div v-if="errorMsg" class="p-4 bg-red-50 text-red-600 rounded-2xl text-xs font-bold text-center animate-shake">
            <LucideAlertCircle class="inline-block mr-2" :size="14" /> {{ errorMsg }}
          </div>

          <button type="submit" :disabled="loading" class="w-full py-5 bg-indigo-600 hover:bg-slate-900 text-white rounded-[24px] font-black text-xl tracking-wide shadow-xl shadow-indigo-200/60 transition-all active:scale-95 flex items-center justify-center gap-2">
            <LucideStar v-if="!loading" :size="24" />
            <span v-else class="animate-spin inline-block w-6 h-6 border-4 border-white/30 border-t-white rounded-full"></span>
            {{ loading ? '同步账户中...' : '开启免费注册' }}
          </button>
        </form>
        
        <p class="mt-8 text-center text-[10px] text-slate-400 font-bold uppercase tracking-widest leading-loose">
          注册即代表您同意 GradBox 的 <br /> 
          <NuxtLink to="/help" class="text-indigo-600">服务协议</NuxtLink> 与 <NuxtLink to="/help" class="text-indigo-600">隐私权政策</NuxtLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { 
  LucideGraduationCap, LucideUser, LucideUserCircle, 
  LucideShieldLock, LucideAlertCircle, LucideStar 
} from 'lucide-vue-next'

const api = useApi()

const form = reactive({
  username: '',
  nickname: '',
  password: ''
})

const loading = ref(false)
const errorMsg = ref('')

const handleRegister = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await api('/api/auth/register', {
      method: 'POST',
      body: form
    })
    
    if (res.code === 200) {
      navigateTo('/login')
    } else {
      errorMsg.value = res.message || '注册遇到问题，请重试'
    }
  } catch (e) {
    console.error('Register Error:', e)
    errorMsg.value = '连接服务器失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}
.animate-shake {
  animation: shake 0.4s ease-in-out;
}
</style>
