<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 relative overflow-hidden">
    <!-- Mesh Background -->
    <div class="absolute inset-0 pointer-events-none opacity-40">
      <div class="absolute top-[-10%] right-[-10%] w-[50%] h-[50%] bg-blue-400/20 rounded-full blur-[120px] animate-float"></div>
      <div class="absolute bottom-[-10%] left-[-10%] w-[50%] h-[50%] bg-indigo-400/20 rounded-full blur-[120px]"></div>
    </div>

    <div class="max-w-md w-full px-6 relative z-10 animate-fade-in-up">
      <div class="glass-card bg-white/80 p-10 md:p-12 rounded-[48px] shadow-2xl shadow-blue-200/50 border border-white">
        <div class="text-center space-y-4 mb-10">
          <NuxtLink to="/" class="inline-flex items-center gap-2 mb-4 group">
            <div class="p-1 bg-white rounded-2xl shadow-lg group-hover:scale-110 transition-transform overflow-hidden">
              <img src="/logo.png" alt="GradBox Logo" class="w-16 h-16 object-cover" />
            </div>
            <span class="text-2xl font-black text-slate-900 tracking-tight">GradBox</span>
          </NuxtLink>
          <h2 class="text-3xl font-black text-slate-900 tracking-tight">欢迎回来</h2>
          <p class="text-slate-400 font-bold text-xs uppercase tracking-widest leading-loose">
            还没有账号?
            <NuxtLink to="/register" class="text-blue-600 hover:text-indigo-600 underline underline-offset-4 decoration-2 transition-colors">
              立即开启探索之旅
            </NuxtLink>
          </p>
        </div>

        <form class="space-y-8" @submit.prevent="handleLogin">
          <div class="space-y-6">
            <div class="group space-y-2">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest pl-2">登录身份 / 用户名</label>
              <div class="relative">
                <LucideUser class="absolute left-5 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-blue-600 transition-colors" :size="18" />
                <input v-model="form.username" type="text" required
                       class="w-full pl-14 pr-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-blue-100 focus:border-blue-600 transition-all outline-none font-bold text-slate-800"
                       placeholder="Username">
              </div>
            </div>
            
            <div class="group space-y-2">
              <div class="flex justify-between items-center px-2">
                <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest">访问密钥 / 密码</label>
                <NuxtLink to="/help" class="text-[10px] font-black text-blue-600 hover:text-indigo-600 transition-colors">忘记密码?</NuxtLink>
              </div>
              <div class="relative">
                <LucideLock class="absolute left-5 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-blue-600 transition-colors" :size="18" />
                <input v-model="form.password" type="password" required
                       class="w-full pl-14 pr-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-blue-100 focus:border-blue-600 transition-all outline-none font-bold text-slate-800"
                       placeholder="Password">
              </div>
            </div>
          </div>

          <div v-if="errorMsg" class="p-4 bg-red-50 text-red-600 rounded-2xl text-xs font-bold text-center animate-shake">
            <LucideAlertCircle class="inline-block mr-2" :size="14" /> {{ errorMsg }}
          </div>

          <button type="submit" :disabled="loading" class="btn-premium w-full py-5 text-xl font-black tracking-wide">
            <LucideLogIn v-if="!loading" :size="24" />
            <span v-else class="animate-spin inline-block w-6 h-6 border-4 border-white/30 border-t-white rounded-full"></span>
            {{ loading ? '身份验证中...' : '立即登录' }}
          </button>
        </form>
        
        <div class="mt-12 pt-8 border-t border-slate-100 flex justify-center gap-6">
           <div v-for="i in 3" :key="i" class="w-10 h-10 rounded-full bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-300 hover:text-blue-600 hover:border-blue-100 transition-all cursor-pointer">
              <LucideGithub v-if="i===1" :size="20" />
              <LucideTwitter v-if="i===2" :size="20" />
              <LucideChrome v-if="i===3" :size="20" />
           </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { 
  LucideGraduationCap, LucideUser, LucideLock, 
  LucideAlertCircle, LucideLogIn, LucideGithub, 
  LucideTwitter, LucideChrome 
} from 'lucide-vue-next'

const api = useApi()
const authStore = useAuthStore()

const form = reactive({
  username: '',
  password: ''
})

const loading = ref(false)
const errorMsg = ref('')

const handleLogin = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await api('/api/auth/login', {
      method: 'POST',
      body: form
    })
    
    if (res.code === 200) {
      authStore.setToken(res.data.token, res.data.tokenHead)
      navigateTo('/')
    } else {
      errorMsg.value = res.message || '凭据错误，请重试'
    }
  } catch (e) {
    console.error('Login Error:', e)
    errorMsg.value = '登录服务异常，请稍后再试'
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
