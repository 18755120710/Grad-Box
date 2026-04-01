import { defineStore } from 'pinia'

interface UserState {
    token: string | null
    tokenHead: string | null
    userInfo: any | null
}

export const useAuthStore = defineStore('auth', {
    state: (): UserState => {
        const t = useCookie('auth_token')
        const h = useCookie('auth_token_head')
        return {
            token: t.value || null,
            tokenHead: h.value || null,
            userInfo: null,
        }
    },
    getters: {
        isLoggedIn: (state) => !!state.token,
        fullToken: (state) => state.tokenHead ? `${state.tokenHead}${state.token}` : state.token,
    },
    actions: {
        setToken(token: string, tokenHead: string) {
            this.token = token
            this.tokenHead = tokenHead
            const t = useCookie('auth_token', { maxAge: 60 * 60 * 24 * 7 })
            const h = useCookie('auth_token_head', { maxAge: 60 * 60 * 24 * 7 })
            t.value = token
            h.value = tokenHead
        },
        setUserInfo(userInfo: any) {
            this.userInfo = userInfo
        },
        logout() {
            this.token = null
            this.tokenHead = null
            this.userInfo = null
            const t = useCookie('auth_token')
            const h = useCookie('auth_token_head')
            t.value = null
            h.value = null
        },
        async fetchUserInfo() {
            if (!this.token) return
            const api = useApi()
            try {
                const res = await api('/api/user/info')
                if (res.code === 200) {
                    this.setUserInfo(res.data)
                }
            } catch (e: any) {
                console.error('Fetch User Info Error:', e)
            }
        },
        initialize() {
            // State is already initialized from cookies
            if (this.token && !this.userInfo) {
                this.fetchUserInfo()
            }
        }
    },
})
