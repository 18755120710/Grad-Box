<template>
  <div class="relative min-h-screen bg-slate-50 overflow-hidden pt-20">
    <!-- Background Accents -->
    <div class="absolute top-0 right-0 w-[500px] h-[500px] bg-blue-100/50 rounded-full blur-[120px] -z-10 animate-pulse"></div>
    <div class="absolute bottom-0 left-0 w-[400px] h-[400px] bg-indigo-100/30 rounded-full blur-[100px] -z-10"></div>

    <div class="max-w-5xl mx-auto px-4">
      <div v-if="!submitted" class="grid grid-cols-1 lg:grid-cols-5 gap-12 items-start">
        <!-- Sidebar Info -->
        <div class="lg:col-span-2 space-y-10 animate-fade-in-up">
          <div class="space-y-4">
            <h1 class="text-5xl font-black text-slate-900 leading-tight">量身定制<br /><span class="text-blue-600 underline decoration-blue-200 underline-offset-8">专属方案</span></h1>
            <p class="text-slate-500 font-medium text-lg leading-relaxed">没找到心仪的项目？告诉我们您的选题、技术偏好或难点，顶级导师团队为您提供一对一定制开发服务。</p>
          </div>

          <div class="space-y-6">
            <div v-for="step in process" :key="step.title" class="flex gap-4">
              <div class="w-10 h-10 rounded-xl bg-white shadow-sm border border-slate-100 flex items-center justify-center text-blue-600 font-black">
                {{ step.id }}
              </div>
              <div>
                <h4 class="font-bold text-slate-800">{{ step.title }}</h4>
                <p class="text-xs text-slate-400 font-bold">{{ step.desc }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Form Card -->
        <div class="lg:col-span-3">
          <div class="glass-card bg-white p-10 md:p-14 rounded-[48px] shadow-2xl shadow-slate-200/50 relative overflow-hidden">
            <div class="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-blue-600 to-indigo-600"></div>
            
            <form class="space-y-10" @submit.prevent="handleSubmit">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div class="group space-y-2">
                  <label class="text-xs font-black text-slate-400 uppercase tracking-widest pl-2">您的称呼</label>
                  <input v-model="form.contactName" type="text" placeholder="张同学" required 
                         class="w-full px-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-blue-100 focus:border-blue-600 transition-all outline-none font-bold" />
                </div>
                <div class="group space-y-2">
                  <label class="text-xs font-black text-slate-400 uppercase tracking-widest pl-2">联系方式</label>
                  <input v-model="form.contactPhone" type="text" placeholder="手机或微信号" required 
                         class="w-full px-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-blue-100 focus:border-blue-600 transition-all outline-none font-bold" />
                </div>
              </div>

              <div class="group space-y-2">
                <label class="text-xs font-black text-slate-400 uppercase tracking-widest pl-2">需求详述</label>
                <textarea v-model="form.content" required rows="6" 
                          placeholder="请描述您的选题、功能模块要求、所需技术栈及期望交付时间..."
                          class="w-full px-6 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-blue-100 focus:border-blue-600 transition-all outline-none resize-none font-bold leading-relaxed"></textarea>
              </div>

              <div v-if="errorMsg" class="p-4 bg-red-50 text-red-600 rounded-2xl text-sm font-bold animate-shake">
                <LucideAlertCircle class="inline-block mr-2" :size="16" /> {{ errorMsg }}
              </div>

              <button type="submit" :disabled="loading"
                      class="btn-premium w-full py-5 text-xl font-black tracking-wide">
                <LucideSend v-if="!loading" :size="24" />
                <span v-else class="animate-spin inline-block w-6 h-6 border-4 border-white/30 border-t-white rounded-full"></span>
                {{ loading ? '正在提交...' : '提交定制申请' }}
              </button>
              
              <p class="text-center text-[10px] text-slate-400 font-bold uppercase tracking-widest">提交即代表同意 GradBox 隐私服务协议</p>
            </form>
          </div>
        </div>
      </div>

      <!-- Success State -->
      <div v-else class="max-w-2xl mx-auto py-20 text-center space-y-8 animate-fade-in-up">
        <div class="w-32 h-32 bg-blue-600 rounded-[40px] flex items-center justify-center text-white mx-auto shadow-2xl shadow-blue-200 animate-float">
          <LucideCheckCircle2 :size="64" />
        </div>
        <div class="space-y-4">
          <h2 class="text-5xl font-black text-slate-900">申请已送达</h2>
          <p class="text-slate-500 text-xl font-medium">我们的技术顾问将在 24 小时内（通常为 1 小时内）通过微信或电话与您核实需求并提供报价方案。</p>
        </div>
        <div class="pt-8">
          <NuxtLink to="/" class="px-10 py-4 bg-slate-900 text-white rounded-full font-black hover:bg-black transition-all">
            返回首页
          </NuxtLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const api = useApi()
const authStore = useAuthStore()

const form = reactive({
  contactName: '',
  contactPhone: '',
  content: ''
})

const loading = ref(false)
const submitted = ref(false)
const errorMsg = ref('')

const process = [
  { id: '01', title: '需求提交', desc: '填写简单的表单，告诉我们您的核心诉求。' },
  { id: '02', title: '导师对接', desc: '分派对应技术栈的专家进行 1-on-1 需求梳理。' },
  { id: '03', title: '方案报价', desc: '提供详细的技术实现路线图与透明的梯度报价。' },
  { id: '04', title: '启动开发', desc: '分阶段交付源码，定期演示进度，确保不跑偏。' }
]

const handleSubmit = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录后提交咨询')
    navigateTo('/login')
    return
  }

  loading.value = true
  errorMsg.value = ''
  try {
    const res = await api('/api/consultations', {
      method: 'POST',
      body: form
    })
    if (res.code === 200) {
      submitted.value = true
    } else {
      errorMsg.value = res.message || '提交失败'
    }
  } catch (e) {
    errorMsg.value = '连接服务器失败，请检查网络设置'
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
