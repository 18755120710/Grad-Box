<template>
  <el-container class="admin-layout" :class="{ 'is-sidebar-collapsed': isCollapse }">
    <el-aside :width="asideWidth" class="admin-aside" v-if="!isMobile || drawerVisible">
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
          :collapse="isCollapse"
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
          <span class="status-text">系统运行正常</span>
        </div>
      </div>
    </el-aside>

    <!-- Mobile Sidebar Drawer -->
    <el-drawer
      v-model="drawerVisible"
      direction="ltr"
      size="260px"
      :with-header="false"
      class="mobile-aside-drawer"
      v-if="isMobile"
    >
      <div class="admin-aside is-drawer">
        <div class="logo-container">
          <img src="@/assets/logo.jpg" alt="GradBox Logo" class="admin-logo-img" />
          <span class="logo-text">GradBox</span>
        </div>
        <el-scrollbar>
          <el-menu :default-active="activeMenu" router class="admin-menu" @select="drawerVisible = false">
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
          </el-menu>
        </el-scrollbar>
      </div>
    </el-drawer>
    
    <el-container class="main-container">
      <el-header class="admin-header">
        <div class="header-left">
          <div class="menu-toggle-btn" @click="toggleSidebar">
            <el-icon><LucideMenu /></el-icon>
          </div>
          <el-breadcrumb separator="/" class="hidden-mobile">
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
  ArrowDown as LucideArrowDown,
  Menu as LucideMenu
} from 'lucide-vue-next'
import { onMounted, ref, onUnmounted } from 'vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isCollapse = ref(localStorage.getItem('admin-sidebar-collapsed') === 'true')
const drawerVisible = ref(false)
const screenWidth = ref(window.innerWidth)
const isMobile = computed(() => screenWidth.value < 1024)
const isDark = ref(localStorage.getItem('admin-theme') === 'dark')

const toggleTheme = () => {
  isDark.value = !isDark.value
  const mode = isDark.value ? 'dark' : 'light'
  document.documentElement.classList.toggle('dark', isDark.value)
  localStorage.setItem('admin-theme', mode)
}

const asideWidth = computed(() => {
  if (isMobile.value) return '0px'
  return isCollapse.value ? '80px' : '260px'
})

const toggleSidebar = () => {
  if (isMobile.value) {
    drawerVisible.value = !drawerVisible.value
  } else {
    isCollapse.value = !isCollapse.value
    localStorage.setItem('admin-sidebar-collapsed', isCollapse.value.toString())
  }
}

const handleResize = () => {
  screenWidth.value = window.innerWidth
}

onMounted(() => {
  document.documentElement.classList.toggle('dark', isDark.value)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
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
  box-shadow: 10px 0 30px rgba(0, 0, 0, 0.02);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.admin-aside.is-drawer {
  height: 100%;
  width: 100% !important;
  border-right: none;
}

.logo-container {
  height: 100px;
  display: flex;
  align-items: center;
  padding: 0 32px;
  gap: 16px;
  position: relative;
  transition: all 0.3s;
}

.is-sidebar-collapsed .logo-container {
  padding: 0;
  justify-content: center;
}

.logo-glow {
  position: absolute;
  width: 60px;
  height: 60px;
  background: var(--brand-primary);
  filter: blur(40px);
  opacity: 0.1;
  left: 20px;
}

.admin-logo-img {
  width: 48px;
  height: 48px;
  object-fit: cover;
  border-radius: 16px;
  border: 1px solid var(--admin-border);
  position: relative;
  z-index: 2;
  background: white;
  padding: 2px;
}

.logo-text {
  font-size: 20px;
  font-weight: 900;
  letter-spacing: -1px;
  color: var(--admin-text-main);
  font-family: var(--font-main);
  text-transform: uppercase;
  italic: true;
  transition: opacity 0.3s;
}

.is-sidebar-collapsed .logo-text,
.is-sidebar-collapsed .admin-badge,
.is-sidebar-collapsed .logo-glow {
  opacity: 0;
  display: none;
}

.admin-badge {
  font-size: 9px;
  background: var(--brand-primary);
  color: white;
  padding: 2px 8px;
  border-radius: 20px;
  vertical-align: middle;
  margin-left: 6px;
  font-weight: 900;
  letter-spacing: 1px;
}

.admin-menu {
  padding: 20px 16px;
  background-color: transparent !important;
  border-right: none !important;
}

.menu-divider {
  padding: 32px 20px 12px;
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 3px;
  color: var(--admin-text-muted);
  font-weight: 900;
  opacity: 0.5;
  transition: opacity 0.3s;
}

.is-sidebar-collapsed .menu-divider {
  opacity: 0;
  height: 0;
  padding: 0;
  margin: 0;
  overflow: hidden;
}

:deep(.el-menu-item) {
  height: 54px;
  line-height: 54px;
  border-radius: 16px;
  margin-bottom: 8px;
  transition: all 0.4s cubic-bezier(0.2, 1, 0.3, 1);
  background-color: transparent !important;
  color: var(--admin-text-muted) !important;
  border: 1px solid transparent;
}

:deep(.el-menu-item .el-icon) {
  font-size: 20px;
  margin-right: 14px;
}

:deep(.el-menu-item.is-active) {
  color: white !important;
  background: var(--brand-primary) !important;
  box-shadow: 0 10px 25px -5px rgba(79, 70, 229, 0.4);
}

:deep(.el-menu-item:hover:not(.is-active)) {
  background-color: var(--admin-surface-light) !important;
  color: var(--admin-text-main) !important;
  transform: translateX(4px);
  border-color: var(--admin-border);
}

.aside-footer {
  padding: 24px 32px;
  border-top: 1px solid var(--admin-border);
  transition: all 0.3s;
}

.is-sidebar-collapsed .aside-footer {
  padding: 24px 0;
  display: flex;
  justify-content: center;
}

.is-sidebar-collapsed .aside-footer .status-text {
  display: none;
}

/* --- Header --- */
.admin-header {
  background: var(--admin-glass);
  backdrop-filter: blur(24px);
  border-bottom: 1px solid var(--admin-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: var(--header-height) !important;
  position: sticky;
  top: 0;
  z-index: 90;
}

.menu-toggle-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 10px;
  color: var(--admin-text-main);
  transition: all 0.3s;
  background: var(--admin-surface-light);
  margin-right: 12px;
}

.menu-toggle-btn:hover {
  background: var(--admin-primary-glow);
  color: var(--admin-primary);
}

@media (max-width: 768px) {
  .hidden-mobile {
    display: none;
  }
  .admin-header {
    padding: 0 16px;
  }
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
  padding: var(--canvas-padding);
  overflow-x: hidden;
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
  background: var(--admin-success);
  border-radius: 50%;
  position: relative;
}

.pulse-dot::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: var(--admin-success);
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
