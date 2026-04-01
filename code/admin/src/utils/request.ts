import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const service = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000
})

// Request interceptor
service.interceptors.request.use(
    (config) => {
        const authStore = useAuthStore()
        if (authStore.token) {
            config.headers['Authorization'] = authStore.tokenHead + authStore.token
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// Response interceptor
service.interceptors.response.use(
    (response) => {
        const res = response.data
        if (res.code !== 200) {
            ElMessage.error(res.message || 'Error')
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res
        }
    },
    (error) => {
        if (error.response && error.response.status === 401) {
            const authStore = useAuthStore()
            authStore.logout()
            window.location.href = '/login'
        }
        ElMessage.error(error.message)
        return Promise.reject(error)
    }
)

export default service
