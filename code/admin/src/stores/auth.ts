import { defineStore } from 'pinia'

interface AuthState {
    token: string | null
    tokenHead: string | null
    userInfo: any | null
}

export const useAuthStore = defineStore('auth', {
    state: (): AuthState => ({
        token: localStorage.getItem('admin_token'),
        tokenHead: localStorage.getItem('admin_token_head'),
        userInfo: JSON.parse(localStorage.getItem('admin_user_info') || 'null'),
    }),
    getters: {
        isLoggedIn: (state) => !!state.token,
    },
    actions: {
        setToken(token: string, tokenHead: string) {
            this.token = token
            this.tokenHead = tokenHead
            localStorage.setItem('admin_token', token)
            localStorage.setItem('admin_token_head', tokenHead)
        },
        setUserInfo(userInfo: any) {
            this.userInfo = userInfo
            localStorage.setItem('admin_user_info', JSON.stringify(userInfo))
        },
        logout() {
            this.token = null
            this.tokenHead = null
            this.userInfo = null
            localStorage.removeItem('admin_token')
            localStorage.removeItem('admin_token_head')
            localStorage.removeItem('admin_user_info')
        }
    }
})
