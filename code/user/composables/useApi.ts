export const useApi = () => {
    const config = useRuntimeConfig()
    const authStore = useAuthStore()

    // Base URL for API (Assuming backend runs on 8080)
    const baseURL = 'http://localhost:8080'

    return (url: string, options: any = {}) => {
        const headers = {
            ...options.headers,
        }

        if (authStore.token) {
            headers['Authorization'] = authStore.fullToken
        }

        return $fetch(url, {
            baseURL,
            ...options,
            headers,
            async onResponse({ response }) {
                // Handle 401 Unauthorized
                if (response.status === 401) {
                    authStore.logout()
                    navigateTo('/login')
                }
            },
            async onResponseError({ response }) {
                // Global error handling
                console.error('API Error:', response._data)
            }
        })
    }
}
