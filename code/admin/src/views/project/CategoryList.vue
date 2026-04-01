<template>
  <div class="category-view-wrapper">
    <!-- Header Section -->
    <div class="view-header">
      <div class="title-area">
        <h2 class="view-title">分类体系架构</h2>
        <p class="view-subtitle">构建并维护平台项目的逻辑分层与检索维度</p>
      </div>
      <div class="header-ops">
        <el-button type="primary" class="oled-btn-primary" @click="handleAdd">
          <lucide-plus :size="16" /> 新增核心分类
        </el-button>
      </div>
    </div>

    <!-- Main List Area -->
    <div class="glass-card table-container">
      <el-table 
        :data="categories" 
        v-loading="loading" 
        class="oled-table"
      >
        <el-table-column label="节点标识" width="120" align="center">
          <template #default="{ row }">
            <span class="uid-tag">#{{ row.id?.toString().padStart(3, '0') }}</span>
          </template>
        </el-table-column>

        <el-table-column label="分类维度名称" min-width="200">
          <template #default="{ row }">
            <div class="category-info">
              <lucide-folder :size="16" class="folder-icon" />
              <span class="category-name">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="排序权重" width="150" align="center">
          <template #default="{ row }">
            <div class="sort-badge">
              <lucide-layers :size="12" />
              <span>{{ row.sort }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="治理操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div class="op-actions">
              <el-button circle class="action-btn" @click="handleEdit(row)">
                <lucide-edit :size="16" />
              </el-button>
              <el-button circle class="action-btn danger" @click="handleDelete(row)">
                <lucide-trash-2 :size="16" />
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Category Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类维度' : '定义新分类'"
      width="450px"
      class="oled-dialog"
      destroy-on-close
    >
      <div class="dialog-content">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="显示名称" prop="name">
            <el-input v-model="form.name" placeholder="例如：人工智能 / 嵌入式系统" />
          </el-form-item>
          <el-form-item label="优先级权重 (数值越大越靠前)" prop="sort">
            <el-input-number v-model="form.sort" :min="0" class="full-width" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="oled-btn-text">取消</el-button>
          <el-button 
            type="primary" 
            class="oled-btn-primary" 
            :loading="submitting" 
            @click="submitForm"
          >
            同步至数据库
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus as LucidePlus, 
  Edit as LucideEdit, 
  Trash2 as LucideTrash2, 
  Folder as LucideFolder,
  Layers as LucideLayers
} from 'lucide-vue-next'

const categories = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()

const form = reactive({
  id: undefined,
  name: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: '必须定义分类名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/categories/list')
    categories.value = res.data
  } catch (err) {
    ElMessage.error('无法同步分类图谱')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  Object.assign(form, { id: undefined, name: '', sort: 0 })
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(form, row)
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`此操作将永久移除分类「${row.name}」，关联项目可能失去分类引用。确定继续?`, '分类销毁警示', {
    confirmButtonText: '强制删除',
    cancelButtonText: '保留',
    type: 'warning',
    customClass: 'oled-message-box'
  }).then(async () => {
    try {
      await request.delete(`/api/categories/${row.id}`)
      ElMessage.success('节点已从拓扑结构中移除')
      loadData()
    } catch (err) {
      ElMessage.error('删除指令执行失败')
    }
  })
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put('/api/categories', form)
      ElMessage.success('维度数据已更新')
    } else {
      await request.post('/api/categories', form)
      ElMessage.success('新维度已注入')
    }
    dialogVisible.value = false
    loadData()
  } catch (err) {
    ElMessage.error('持久化链路异常')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.category-view-wrapper {
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
  letter-spacing: -1px;
}

.view-subtitle {
  color: var(--admin-text-muted);
  font-size: 14px;
}

/* --- Table --- */
.table-container { padding: 0; overflow: hidden; }

.uid-tag { font-family: var(--font-data); color: var(--admin-primary); background: var(--admin-primary-glow); padding: 2px 8px; border-radius: 4px; font-size: 12px; }

.category-info { display: flex; align-items: center; gap: 12px; }
.folder-icon { color: var(--admin-primary); opacity: 0.8; }
.category-name { font-weight: 700; color: var(--admin-text-main); font-size: 15px; }

.sort-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: var(--admin-surface-light);
  padding: 4px 10px;
  border-radius: 8px;
  color: var(--admin-text-secondary);
  font-family: var(--font-data);
}

/* Op Actions */
.op-actions { display: flex; gap: 8px; justify-content: center; }
.action-btn {
  background: var(--admin-surface-light) !important;
  border: 1px solid var(--admin-border) !important;
  color: var(--admin-text-muted) !important;
  transition: all 0.3s;
}
.action-btn:hover { background: var(--admin-primary-glow) !important; color: var(--admin-primary) !important; border-color: var(--admin-primary) !important; }
.action-btn.danger:hover { background: rgba(239, 68, 68, 0.1) !important; color: #ef4444 !important; border-color: #ef4444 !important; }

.full-width { width: 100%; }

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
