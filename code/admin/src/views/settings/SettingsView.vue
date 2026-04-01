<template>
  <div class="settings-view-wrapper">
    <!-- Header -->
    <div class="view-header">
      <div class="title-area">
        <h2 class="view-title">全局核心预设</h2>
        <p class="view-subtitle">在 OLED 环境下配置平台主权、品牌标识及底层安全共识</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" class="oled-btn-primary" @click="handleSave" :loading="saving">
          <lucide-save :size="16" /> 应用全局变更
        </el-button>
      </div>
    </div>

    <!-- Settings Grid -->
    <div class="settings-grid">
      <!-- Branding Section -->
      <div class="glass-card settings-card">
        <div class="card-header">
          <lucide-palette :size="20" class="header-icon" />
          <span>品牌主权与视觉标识</span>
        </div>
        
        <div class="branding-content">
          <div class="logo-config">
            <label>平台官方 LOGO</label>
            <div class="logo-preview-area">
              <div class="logo-vessel">
                <img :src="config.logoUrl || '@/assets/logo.jpg'" alt="Logo" />
              </div>
              <div class="logo-actions">
                <el-button class="oled-btn-outline sm" @click="triggerUpload">更换标识资产</el-button>
                <p class="hint">建议上传 512x512 透明背景 PNG 资产</p>
              </div>
            </div>
          </div>

          <el-form label-position="top">
            <el-form-item label="平台主标题 (Title)">
              <el-input v-model="config.siteTitle" placeholder="例如：GradBox | 毕业设计一站式服务" />
            </el-form-item>
            <el-form-item label="系统子标语 (Tagline)">
              <el-input v-model="config.tagline" placeholder="让创意无处遁形..." />
            </el-form-item>
          </el-form>
        </div>
      </div>

      <!-- Security Section -->
      <div class="glass-card settings-card">
        <div class="card-header">
          <lucide-shield-alert :size="20" class="header-icon" />
          <span>系统安全与准入机制</span>
        </div>
        
        <div class="security-content">
          <div class="setting-item">
            <div class="item-meta">
              <span class="label">全站维护模式</span>
              <p class="desc">激活后，用户端将显示 503 维护看板，仅管理员可见后台</p>
            </div>
            <el-switch v-model="config.maintenanceMode" active-color="var(--admin-primary)" />
          </div>

          <div class="setting-item">
            <div class="item-meta">
              <span class="label">新用户准入审计</span>
              <p class="desc">开启后，新注册用户需等待管理员手动激活权限</p>
            </div>
            <el-switch v-model="config.userApproval" active-color="var(--admin-primary)" />
          </div>

          <div class="setting-item">
            <div class="item-meta">
              <span class="label">会话生命周期 (分钟)</span>
              <p class="desc">管理员 Token 在不活动状态下的存活时长</p>
            </div>
            <el-input-number v-model="config.sessionTimeout" :min="15" :max="1440" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Save as LucideSave,
  Palette as LucidePalette,
  ShieldAlert as LucideShieldAlert
} from 'lucide-vue-next'

const saving = ref(false)

// --- Mock Config ---
const config = reactive({
  siteTitle: 'GradBox Admin',
  tagline: 'High Performance Project Hub',
  logoUrl: '',
  maintenanceMode: false,
  userApproval: true,
  sessionTimeout: 120
})

const handleSave = () => {
  saving.value = true
  // Mock API call
  setTimeout(() => {
    saving.value = false
    ElMessage.success('全局预设同步成功，部分变更将在下次刷新生效')
  }, 800)
}

const triggerUpload = () => {
  ElMessage.info('媒体中心升级中，暂不支持动态覆盖')
}
</script>

<style scoped>
.settings-view-wrapper {
  animation: fade-in 0.5s ease-out;
}

/* --- Header --- */
.view-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

.view-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--admin-text-main);
}

.view-subtitle {
  color: var(--admin-text-muted);
  font-size: 14px;
}

/* --- Grid --- */
.settings-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.settings-card {
  padding: 32px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 700;
  color: var(--admin-text-main);
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--admin-border);
}

.header-icon { color: var(--admin-primary); }

/* --- Branding --- */
.logo-config {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.logo-config label { font-size: 12px; color: var(--admin-text-muted); font-weight: 700; text-transform: uppercase; }

.logo-preview-area { display: flex; align-items: center; gap: 24px; }

.logo-vessel {
  width: 80px;
  height: 80px;
  background: var(--admin-surface-light);
  border: 1px solid var(--admin-border);
  border-radius: 20px;
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-vessel img { max-width: 100%; max-height: 100%; border-radius: 8px; }

.logo-actions { display: flex; flex-direction: column; gap: 8px; }
.hint { font-size: 11px; color: var(--admin-text-muted); }

/* --- Security Item --- */
.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: var(--admin-surface-light);
  border-radius: 16px;
  margin-bottom: 16px;
  border: 1px solid var(--admin-border);
}

.item-meta { display: flex; flex-direction: column; gap: 4px; }
.item-meta .label { font-weight: 600; color: var(--admin-text-main); font-size: 14px; }
.item-meta .desc { font-size: 12px; color: var(--admin-text-muted); max-width: 240px; line-height: 1.4; }

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
