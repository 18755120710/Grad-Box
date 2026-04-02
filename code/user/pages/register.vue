<script setup>
import { 
  LucideUser, LucideUserCircle, 
  LucideLock, LucideAlertCircle, LucideStar, LucideGraduationCap
} from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

definePageMeta({
  layout: 'auth'
})

const api = useApi()

const form = reactive({
  username: '',
  nickname: '',
  password: ''
})

const loading = ref(false)
const errorMsg = ref('')

const handleRegister = async () => {
  if (form.password.length < 6) {
    errorMsg.value = '密码长度至少为 6 位'
    return
  }

  loading.value = true
  errorMsg.value = ''
  try {
    const res = await api('/api/auth/register', {
      method: 'POST',
      body: form
    })
    
    if (res.code === 200) {
      ElMessage.success('注册成功！正在为您跳转...')
      setTimeout(() => {
        navigateTo('/login')
      }, 1500)
    } else {
      errorMsg.value = res.message || '注册请求被拒绝'
    }
  } catch (e) {
    console.error('Register Error:', e)
    errorMsg.value = '连接服务器失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="animate-fade-in-up">
    <!-- Liquid Glass Card (Pro Max V2) -->
    <div class="glass-container relative p-8 md:p-12 rounded-[2.5rem] border border-white/40 shadow-[0_32px_64px_-16px_rgba(0,0,0,0.08)] overflow-hidden group">
      <!-- Sophisticated Glass Background -->
      <div class="absolute inset-0 bg-white/60 backdrop-blur-2xl z-0"></div>
      
      <!-- Content Wrapper -->
      <div class="relative z-10">
        <!-- Logo & Brand Header -->
        <div class="text-center space-y-8 mb-12">
          <NuxtLink to="/" class="inline-flex flex-col items-center gap-4 group/logo">
            <div class="relative">
              <div class="absolute inset-0 bg-primary/20 blur-2xl rounded-full group-hover/logo:bg-primary/30 transition-all duration-700"></div>
              <div class="relative w-20 h-20 bg-white rounded-3xl shadow-xl border border-slate-100 flex items-center justify-center p-2 group-hover/logo:scale-105 transition-all duration-700">
                <img src="/logo.png" alt="GradBox Logo" class="w-full h-full object-contain rounded-2xl" />
              </div>
            </div>
            <div class="space-y-1">
              <span class="block text-4xl font-heading text-slate-900 tracking-tighter italic lowercase">Grad<span class="text-primary italic font-black">Box</span></span>
              <div class="flex items-center justify-center gap-2">
                <div class="h-[1px] w-4 bg-slate-200"></div>
                <LucideGraduationCap :size="12" class="text-primary opacity-50" />
                <div class="h-[1px] w-4 bg-slate-200"></div>
              </div>
            </div>
          </NuxtLink>
          
          <div class="space-y-3">
            <h1 class="text-4xl font-heading text-slate-900 tracking-tight leading-tight">加入我们<br/><span class="text-slate-400 font-light italic">Join the Excellence</span></h1>
            <p class="text-[10px] font-bold text-slate-400 uppercase tracking-[0.25em] flex items-center justify-center gap-2">
              <span>已有账号?</span>
              <NuxtLink to="/login" class="text-primary hover:text-slate-900 transition-all duration-300">立即登录</NuxtLink>
            </p>
          </div>
        </div>

        <!-- Auth Form -->
        <form class="space-y-8" @submit.prevent="handleRegister">
          <div class="space-y-6">
            <!-- Username Input -->
            <div class="space-y-2 group/input">
              <label class="block text-[10px] font-bold text-slate-400 uppercase tracking-widest px-2">设定用户名 / USERNAME</label>
              <div class="relative">
                <LucideUserCircle class="absolute left-6 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within/input:text-primary transition-colors" :size="16" />
                <input v-model="form.username" type="text" required
                       class="w-full bg-white/40 border border-white/60 rounded-2xl pl-16 pr-6 py-5 text-sm focus:bg-white focus:border-primary/30 focus:shadow-[0_0_0_4px_rgba(79,70,229,0.05)] outline-none transition-all placeholder:text-slate-300 text-slate-600 font-body"
                       placeholder="Unique ID">
              </div>
            </div>

            <!-- Nickname Input -->
            <div class="space-y-2 group/input">
              <label class="block text-[10px] font-bold text-slate-400 uppercase tracking-widest px-2">您的昵称 / NICKNAME</label>
              <div class="relative">
                <LucideUser class="absolute left-6 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within/input:text-primary transition-colors" :size="16" />
                <input v-model="form.nickname" type="text" required
                       class="w-full bg-white/40 border border-white/60 rounded-2xl pl-16 pr-6 py-5 text-sm focus:bg-white focus:border-primary/30 focus:shadow-[0_0_0_4px_rgba(79,70,229,0.05)] outline-none transition-all placeholder:text-slate-300 text-slate-600 font-body"
                       placeholder="Display Name">
              </div>
            </div>
            
            <!-- Password Input -->
            <div class="space-y-2 group/input">
              <label class="block text-[10px] font-bold text-slate-400 uppercase tracking-widest px-2">设置密码 / PASSWORD</label>
              <div class="relative">
                <LucideLock class="absolute left-6 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within/input:text-primary transition-colors" :size="16" />
                <input v-model="form.password" type="password" required
                       class="w-full bg-white/40 border border-white/60 rounded-2xl pl-16 pr-6 py-5 text-sm focus:bg-white focus:border-primary/30 focus:shadow-[0_0_0_4px_rgba(79,70,229,0.05)] outline-none transition-all placeholder:text-slate-300 text-slate-600 font-body"
                       placeholder="Min 6 characters">
              </div>
            </div>
          </div>

          <!-- Error Feedback -->
          <div v-if="errorMsg" class="p-4 bg-red-50/50 backdrop-blur-md text-red-500 rounded-2xl text-[10px] font-bold text-center animate-shake border border-red-100/50 flex items-center justify-center gap-2 uppercase tracking-wide">
            <LucideAlertCircle :size="14" /> {{ errorMsg }}
          </div>

          <!-- Action Button -->
          <button type="submit" :disabled="loading" 
                  class="group/btn relative w-full py-5 bg-primary text-white rounded-2xl overflow-hidden shadow-xl shadow-primary/20 hover:shadow-2xl hover:shadow-primary/30 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300">
            <div class="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent -translate-x-full group-hover/btn:translate-x-full transition-transform duration-1000"></div>
            <div class="relative flex items-center justify-center gap-3">
              <LucideStar v-if="!loading" :size="18" class="text-white" />
              <span v-else class="animate-spin inline-block w-4 h-4 border-2 border-white/30 border-t-white rounded-full"></span>
              <span class="text-xs font-black uppercase tracking-[0.2em] font-body">{{ loading ? '正在同步账号...' : '开启探索之旅' }}</span>
            </div>
          </button>
        </form>
        
        <p class="mt-10 text-center text-[9px] text-slate-400 font-bold uppercase tracking-[0.2em] leading-relaxed opacity-60 font-body">
          注册即代表您同意 GradBox 的 <br /> 
          <NuxtLink to="/help" class="text-primary hover:text-slate-900 transition-colors underline-offset-4 hover:underline">服务协议</NuxtLink> 与 <NuxtLink to="/help" class="text-primary hover:text-slate-900 transition-colors underline-offset-4 hover:underline">隐私权政策</NuxtLink>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes fade-in-up {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-up {
  animation: fade-in-up 1.2s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-4px); }
  40%, 80% { transform: translateX(4px); }
}
.animate-shake {
  animation: shake 0.5s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}
</style>
