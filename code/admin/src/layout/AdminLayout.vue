<template>
  <el-container class="admin-layout">
    <el-aside width="260px" class="admin-aside">
      <div class="logo-container">
        <div class="logo-glow"></div>
        <img src="@/assets/logo.jpg" alt="GradBox Logo" class="admin-logo-img" />
        <span class="logo-text">GradBox <span class="admin-badge">Admin</span></span>
      </div>
      
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          router
          class="admin-menu"
        >
          <el-menu-item index="/">
            <el-icon><LucideLayoutDashboard /></el-icon>
            <span>概览仪表盘</span>
          </el-menu-item>
          
          <div class="menu-divider">资产治理</div>
          
          <el-menu-item index="/projects">
            <el-icon><LucideFolderArchive /></el-icon>
            <span>项目档案库</span>
          </el-menu-item>
          
          <el-menu-item index="/categories">
            <el-icon><LucideTags /></el-icon>
            <span>分类拓扑树</span>
          </el-menu-item>
          
          <el-menu-item index="/services">
            <el-icon><LucideMessageCircle /></el-icon>
            <span>工单咨询室</span>
          </el-menu-item>

          <div class="menu-divider">底层基座</div>
          
          <el-menu-item index="/users">
            <el-icon><LucideUsers /></el-icon>
            <span>用户准入中心</span>
          </el-menu-item>
          
          <el-menu-item index="/settings">
            <el-icon><LucideSettings /></el-icon>
            <span>全局配置项</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>

      <div class="aside-footer">
        <div class="status-indicator">
          <span class="pulse-dot"></span>
          系统运行正常
        </div>
      </div>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">后台</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPathTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <div class="action-icons">
            <div class="theme-toggle" @click="toggleTheme" title="切换视觉模式">
              <el-icon v-if="isDark"><LucideMoon /></el-icon>
              <el-icon v-else><lucide-sun /></el-icon>
            </div>
            <el-icon><LucideBell /></el-icon>
          </div>
          
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="username">Admin</span>
              <el-icon><LucideArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>安全退出</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessageBox } from 'element-plus'
import {
  LayoutDashboard as LucideLayoutDashboard,
  FolderArchive as LucideFolderArchive,
  Tags as LucideTags,
  CreditCard as LucideCreditCard,
  MessageCircle as LucideMessageCircle,
  Users as LucideUsers,
  Settings as LucideSettings,
  Bell as LucideBell,
  Moon as LucideMoon,
  Sun as LucideSun,
  ArrowDown as LucideArrowDown
} from 'lucide-vue-next'
import { onMounted, ref } from 'vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
const isDark = ref(localStorage.getItem('admin-theme') === 'dark')

const toggleTheme = () => {
  isDark.value = !isDark.value
  const mode = isDark.value ? 'dark' : 'light'
  document.documentElement.classList.toggle('dark', isDark.value)
  localStorage.setItem('admin-theme', mode)
}

onMounted(() => {
  document.documentElement.classList.toggle('dark', isDark.value)
})

const currentPathTitle = computed(() => {
  const titles: Record<string, string> = {
    '/': '概览',
    '/projects': '档案',
    '/categories': '拓扑',
    '/orders': '交易',
    '/services': '咨询',
    '/users': '准入',
    '/settings': '配置'
  }
  return titles[route.path] || '概览'
})

const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要中断当前管理会话吗？', '系统注销确认', {
      confirmButtonText: '确定退出',
      cancelButtonText: '继续巡航',
      type: 'warning',
      customClass: 'oled-message-box'
    }).then(() => {
      authStore.logout()
      router.push('/login')
    })
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  background-color: var(--admin-bg);
}

/* --- Aside Sidebar --- */
.admin-aside {
  background-color: var(--admin-surface);
  border-right: 1px solid var(--admin-border);
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 100;
}

.logo-container {
  height: 80px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 12px;
  position: relative;
  border-bottom: 1px solid var(--admin-border);
}

.logo-glow {
  position: absolute;
  width: 40px;
  height: 40px;
  background: var(--admin-primary);
  filter: blur(30px);
  opacity: 0.2;
  left: 20px;
}

.admin-logo-img {
  width: 42px;
  height: 42px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid var(--admin-border);
  position: relative;
  z-index: 2;
}

.logo-text {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: -0.5px;
  color: var(--admin-text-main);
  font-family: var(--font-main);
}

.admin-badge {
  font-size: 10px;
  background: var(--admin-primary-glow);
  color: var(--admin-primary);
  padding: 2px 6px;
  border-radius: 4px;
  vertical-align: middle;
  margin-left: 4px;
}

.admin-menu {
  padding: 16px 12px;
  background-color: transparent !important;
  border-right: none !important;
}

.menu-divider {
  padding: 24px 16px 8px;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  color: var(--admin-text-muted);
  font-weight: 700;
}

:deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  border-radius: 12px;
  margin-bottom: 4px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background-color: transparent !important;
  color: var(--admin-text-muted) !important;
}

:deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 12px;
}

:deep(.el-menu-item.is-active) {
  color: var(--admin-primary) !important;
  background: linear-gradient(90deg, var(--admin-primary-glow), transparent) !important;
  box-shadow: inset 2px 0 0 var(--admin-primary);
}

:deep(.el-menu-item:hover) {
  background-color: var(--admin-border) !important;
  color: var(--admin-text-main) !important;
}

.aside-footer {
  padding: 20px 24px;
  border-top: 1px solid var(--admin-border);
}

/* --- Header --- */
.admin-header {
  background: var(--admin-glass);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--admin-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 70px !important;
}

:deep(.el-breadcrumb__inner) {
  color: var(--admin-text-muted) !important;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--admin-text-main) !important;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.action-icons {
  display: flex;
  align-items: center;
  gap: 20px;
  color: var(--admin-text-muted);
  font-size: 18px;
}

.theme-toggle {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--admin-surface-light);
  transition: all 0.3s;
}

.theme-toggle:hover {
  background: var(--admin-primary-glow);
  color: var(--admin-primary);
  transform: rotate(15deg);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 10px;
  transition: background 0.3s;
}

.user-info:hover {
  background: var(--admin-surface-light);
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: var(--admin-text-main);
}

/* --- Main Content --- */
.admin-main {
  background-color: var(--admin-bg);
  padding: 32px;
}

/* Status Indicator */
.status-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--admin-text-muted);
}

.pulse-dot {
  width: 8px;
  height: 8px;
  background: var(--admin-primary);
  border-radius: 50%;
  position: relative;
}

.pulse-dot::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: var(--admin-primary);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 0.8; }
  100% { transform: scale(3); opacity: 0; }
}

/* Transitions */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>
