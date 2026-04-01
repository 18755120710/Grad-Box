<template>
  <div class="monolith-wrapper">
    <!-- Left: Branding & Artistic Area -->
    <div class="brand-section">
      <div class="orb x-alpha"></div>
      <div class="orb x-beta"></div>
      <div class="orb x-gamma"></div>
      
      <div class="brand-content">
        <div class="logo-box">
          <img src="@/assets/logo.jpg" alt="GradBox Logo" class="logo-img" />
        </div>
        
        <div class="carousel-container">
          <transition name="slide-fade" mode="out-in">
            <div :key="currentSlide" class="slide-item">
              <h1 class="hero-title" v-html="slides[currentSlide]?.title"></h1>
              <p class="hero-desc">{{ slides[currentSlide]?.desc }}</p>
            </div>
          </transition>
        </div>
      </div>

      <div class="stat-dots">
        <div 
          v-for="(s, index) in slides" 
          :key="index"
          class="dot" 
          :class="{ active: currentSlide === index }"
          @click="currentSlide = index"
        ></div>
      </div>

      <!-- Background Decoration -->
      <svg class="data-weave" viewBox="0 0 800 600">
        <path d="M-100,300 C150,100 350,500 800,300" stroke="rgba(255,255,255,0.05)" fill="none" class="path-anim" />
        <path d="M-100,350 C200,150 400,550 850,350" stroke="rgba(255,255,255,0.03)" fill="none" class="path-anim delay-1" />
      </svg>
    </div>

    <!-- Right: Login Form Area -->
    <div class="form-section">
      <div class="form-card">
        <div class="form-header">
          <h2 class="form-title">身份核验</h2>
          <p class="form-subtitle">请输入您的管理员凭证以访问系统</p>
        </div>

        <el-form :model="loginForm" :rules="loginRules" ref="loginRef" label-position="top">
          <div class="input-group">
            <el-form-item prop="username" label="管理账号">
              <el-input 
                v-model="loginForm.username" 
                placeholder="ID / Username" 
                size="large"
                class="monolith-input"
              >
                <template #prefix><lucide-user :size="18" /></template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="password" label="安全密码">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="Password" 
                size="large"
                show-password
                class="monolith-input"
                @keyup.enter="handleLogin"
              >
                <template #prefix><lucide-lock :size="18" /></template>
              </el-input>
            </el-form-item>
          </div>

          <div class="form-actions">
            <el-button 
              type="primary" 
              class="submit-btn" 
              :loading="loading"
              @click="handleLogin"
            >
              <span>授权登录</span>
              <lucide-arrow-right class="btn-icon" :size="20" />
            </el-button>
          </div>

          <div class="form-footer">
            <p>暂无账号？ <router-link to="/register" class="link-btn">申请成为管理员</router-link></p>
          </div>
        </el-form>
      </div>

      <!-- Floating decoration for depth -->
      <div class="floating-ring"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { 
  Box as LucideBox, 
  User as LucideUser, 
  Lock as LucideLock, 
  ArrowRight as LucideArrowRight 
} from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()
const loginRef = ref()
const loading = ref(false)
const currentSlide = ref(0)

const slides = [
  { title: 'GradBox <br/><span class="accent-text">Admin Hub</span>', desc: '极简、纯粹、高效。专为毕业项目管理打造的数字中枢。' },
  { title: 'Real-time <br/><span class="accent-text">Analytics</span>', desc: '实时监控项目动态，深度洞察毕业设计流行趋势与技术栈分布。' },
  { title: 'Cloud-Based <br/><span class="accent-text">Security</span>', desc: '分布式加密存储，确保每一份学术成果的知识产权都得到严密保护。' }
]

let timer: any = null
onMounted(() => {
  timer = setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % slides.length
  }, 5000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '身份凭证必填', trigger: 'blur' }],
  password: [{ required: true, message: '安全密码必填', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginRef.value) return
  await loginRef.value.validate()
  
  loading.value = true
  try {
    const res: any = await request.post('/api/auth/admin/login', loginForm)
    if (res.code === 200) {
      authStore.setToken(res.data.token, res.data.tokenHead)
      ElMessage.success('授权验证成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '凭证验证失败')
    }
  } catch (err: any) {
    ElMessage.error(err.response?.data?.message || '账号或密码错误，请检查权限')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.monolith-wrapper {
  display: flex;
  height: 100vh;
  background-color: var(--admin-bg);
  overflow: hidden;
  color: var(--admin-text-main);
  font-family: 'Outfit', 'Inter', -apple-system, sans-serif;
  transition: all 0.3s;
}

/* --- Brand Section --- */
.brand-section {
  flex: 1.4;
  position: relative;
  display: flex;
  align-items: center;
  padding: 80px;
  background: radial-gradient(circle at 0% 0%, #0f172a 0%, #020617 100%);
  overflow: hidden;
}

.brand-content {
  position: relative;
  z-index: 10;
  max-width: 600px;
  animation: fade-in-left 1s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.carousel-container {
  min-height: 320px;
  display: flex;
  align-items: center;
}

.logo-box {
  width: 140px;
  height: 140px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 48px;
  overflow: hidden;
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s ease;
}

.logo-box:hover {
  transform: scale(1.05);
}

.logo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-title {
  font-size: 80px;
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: -0.04em;
  margin-bottom: 24px;
}

.accent-text {
  background: linear-gradient(to right, var(--admin-primary), #4ade80);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-desc {
  font-size: 20px;
  color: var(--admin-text-muted);
  line-height: 1.6;
  max-width: 480px;
  margin-bottom: 48px;
}

.stat-dots { 
  display: flex; 
  gap: 12px; 
  position: absolute;
  bottom: 80px;
  left: 80px;
  z-index: 20;
}
.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255,255,255,0.05);
  cursor: pointer;
  transition: all 0.3s ease;
}
.dot:hover {
  background-color: rgba(255,255,255,0.2);
}
.dot.active {
  width: 32px;
  border-radius: 6px;
  background-color: var(--admin-primary);
  box-shadow: 0 0 15px var(--admin-primary-glow);
}

/* --- Form Section --- */
.form-section {
  flex: 1;
  background-color: var(--admin-bg);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  padding: 40px;
}

.form-card {
  width: 100%;
  max-width: 440px;
  padding: 48px;
  background: var(--admin-glass);
  backdrop-filter: blur(40px);
  border: 1px solid var(--admin-border);
  border-radius: 32px;
  position: relative;
  z-index: 10;
  animation: float-up 0.8s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 50px 100px -20px rgba(0, 0, 0, 0.8);
}

.form-header { margin-bottom: 40px; }
.form-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  color: var(--admin-text-main);
  font-family: var(--font-main);
}
.form-subtitle { color: var(--admin-text-muted); font-size: 15px; }

/* --- Monolith Input Styling --- */
:deep(.el-form-item__label) {
  color: var(--admin-text-muted);
  font-weight: 500;
  margin-bottom: 12px !important;
}

:deep(.monolith-input .el-input__wrapper) {
  background: rgba(2, 6, 23, 0.6);
  box-shadow: none;
  border: 1px solid var(--admin-border);
  border-radius: 16px;
  padding: 12px 16px;
  transition: all 0.3s ease;
}

:deep(.monolith-input .el-input__wrapper.is-focus) {
  border-color: var(--admin-primary);
  background: rgba(2, 6, 23, 0.8);
  box-shadow: 0 0 20px var(--admin-primary-glow);
}

:deep(.monolith-input .el-input__inner) {
  color: var(--admin-text-main);
  height: 48px;
  font-family: var(--font-data);
}

.submit-btn {
  width: 100%;
  height: 60px;
  background: linear-gradient(135deg, var(--admin-primary) 0%, #16a34a 100%);
  border: none;
  border-radius: 18px;
  font-size: 18px;
  font-weight: 600;
  margin-top: 24px;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  display: flex !important;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #000; /* Contrast against green */
}

.submit-btn:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 20px 40px var(--admin-primary-glow);
  background: linear-gradient(135deg, #4ade80 0%, #22c55e 100%);
}

.form-footer {
  margin-top: 32px;
  text-align: center;
  color: var(--admin-text-muted);
  font-size: 14px;
}

.link-btn {
  color: var(--admin-primary);
  text-decoration: none;
  font-weight: 600;
  margin-left: 6px;
  transition: color 0.3s;
}

.link-btn:hover { 
  color: #4ade80; 
  text-shadow: 0 0 8px var(--admin-primary-glow);
}

/* --- Decorative Orbs --- */
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  z-index: 1;
}
.x-alpha { width: 400px; height: 400px; background: rgba(34, 197, 94, 0.1); top: -100px; left: -100px; }
.x-beta { width: 300px; height: 300px; background: rgba(22, 163, 74, 0.05); bottom: -50px; right: 10%; }
.x-gamma { width: 250px; height: 250px; background: rgba(6, 78, 59, 0.1); top: 20%; right: -50px; }

.data-weave {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 2;
  pointer-events: none;
}

.path-anim {
  stroke-dasharray: 1000;
  stroke-dashoffset: 1000;
  animation: draw 6s infinite alternate ease-in-out;
}
.delay-1 { animation-delay: 2s; }

@keyframes draw { to { stroke-dashoffset: 0; } }
@keyframes fade-in-left { from { opacity: 0; transform: translateX(-40px); } to { opacity: 1; transform: translateX(0); } }
@keyframes float-up { from { opacity: 0; transform: translateY(40px); } to { opacity: 1; transform: translateY(0); } }

.floating-ring {
  position: absolute;
  width: 500px;
  height: 500px;
  border: 1px solid rgba(255,255,255,0.03);
  border-radius: 50%;
  z-index: 1;
  animation: spin 30s linear infinite;
}

.slide-fade-enter-active {
  transition: all 0.6s ease-out;
}
.slide-fade-leave-active {
  transition: all 0.5s cubic-bezier(1, 0.5, 0.8, 1);
}
.slide-fade-enter-from {
  transform: translateX(30px);
  opacity: 0;
}
.slide-fade-leave-to {
  transform: translateX(-30px);
  opacity: 0;
}
</style>
