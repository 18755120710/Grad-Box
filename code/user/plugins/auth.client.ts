export default defineNuxtPlugin((nuxtApp) => {
    const authStore = useAuthStore()

    // Initialize auth state from localStorage on client side
    if (process.client) {
        authStore.initialize()
    }
})
