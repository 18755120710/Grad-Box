<template>
  <div class="min-h-screen bg-slate-50">
    <!-- Navbar -->
    <nav class="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-bottom border-slate-100">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-20 items-center">
          <NuxtLink to="/" class="flex items-center gap-2">
            <div class="p-1 bg-white rounded-xl shadow-sm overflow-hidden">
              <img src="/logo.png" alt="GradBox Logo" class="w-14 h-14 object-cover" />
            </div>
            <span class="text-xl font-black text-slate-900 tracking-tight">GradBox</span>
          </NuxtLink>

          <div class="hidden md:flex items-center gap-8">
            <NuxtLink v-for="link in navLinks" :key="link.to" :to="link.to" 
                      class="text-sm font-medium text-slate-600 hover:text-blue-600 transition-colors">
              {{ link.text }}
            </NuxtLink>
          </div>

          <div class="flex items-center gap-4">
            <template v-if="authStore.isLoggedIn">
              <ClientOnly>
                <el-dropdown trigger="click">
                  <div class="flex items-center gap-2 cursor-pointer p-1 pr-3 rounded-full hover:bg-slate-50 transition-colors border border-transparent hover:border-slate-100">
                    <el-avatar :size="32" :src="authStore.userInfo?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
                    <span class="text-sm font-bold text-slate-700">{{ authStore.userInfo?.nickname || '用户' }}</span>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu class="w-48">
                      <el-dropdown-item @click="navigateTo('/user/profile')">
                        <LucideUser :size="14" class="mr-2" /> 个人中心
                      </el-dropdown-item>
                      <el-dropdown-item @click="navigateTo('/user/history')">
                        <LucideHistory :size="14" class="mr-2" /> 浏览历史
                      </el-dropdown-item>
                      <el-dropdown-item divided @click="handleLogout">
                        <LucideLogOut :size="14" class="mr-2" /> 退出登录
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </ClientOnly>
            </template>
            <template v-else>
              <NuxtLink to="/login" class="text-sm font-bold text-slate-700 hover:text-blue-600 transition-colors">登录</NuxtLink>
              <NuxtLink to="/register" class="px-5 py-2.5 bg-blue-600 text-white rounded-full text-sm font-bold shadow-lg shadow-blue-100 hover:bg-blue-700 transition-all hover:scale-105 active:scale-95">免费注册</NuxtLink>
            </template>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="page-container">
      <slot />
    </main>

    <!-- Footer -->
    <footer class="bg-slate-900 text-slate-400 py-16 mt-20">
      <div class="max-w-7xl mx-auto px-4 grid grid-cols-1 md:grid-cols-4 gap-12">
        <div class="col-span-1 md:col-span-2 space-y-6">
          <div class="flex items-center gap-2 text-white">
            <img src="/logo.png" alt="GradBox Logo" class="w-16 h-16 object-cover rounded-xl bg-white p-1" />
            <span class="text-2xl font-black">GradBox</span>
          </div>
          <p class="max-w-sm">专业的毕业设计案例展示与定制化服务平台，致力于为毕业生提供最高质量的毕业方案与全方位的技术指导。</p>
        </div>
        <div>
          <h4 class="text-white font-bold mb-6">快速导航</h4>
          <ul class="space-y-4">
            <li v-for="link in navLinks" :key="link.to">
              <NuxtLink :to="link.to" class="hover:text-white transition-colors">{{ link.text }}</NuxtLink>
            </li>
          </ul>
        </div>
        <div>
          <h4 class="text-white font-bold mb-6">联系我们</h4>
          <p>客服热线: 400-XXX-XXXX</p>
          <p>工作时间: 9:00 - 21:00</p>
        </div>
      </div>
      <div class="max-w-7xl mx-auto px-4 mt-16 pt-8 border-t border-slate-800 text-sm text-center">
        © 2024 GradBox. All rights reserved.
      </div>
    </footer>
  </div>
</template>

<script setup>
import { 
  LucideGraduationCap, LucideUser, LucideHistory, 
  LucideLogOut, LucideChevronRight 
} from 'lucide-vue-next'

const authStore = useAuthStore()
const router = useRouter()

const navLinks = [
  { text: '全部项目', to: '/projects' },
  { text: '定制服务', to: '/services' },
  { text: '帮助中心', to: '/help' },
  { text: '关于我们', to: '/about' }
]

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

// Initialize store from localStorage on mount
onMounted(() => {
  authStore.initialize()
  if (authStore.isLoggedIn) {
    authStore.fetchUserInfo()
  }
})
</script>
