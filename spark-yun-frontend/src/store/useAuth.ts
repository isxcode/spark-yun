import { defineStore } from "pinia";

interface AuthState {
  userInfo: Record<string, any>
  token: string
  tenantId: string
  role: string
  currentMenu: string
}

export const useAuthStore = defineStore('authStore', {
  state: (): AuthState => ({
    userInfo: {},
    token: '',
    tenantId: '',
    role: '',
    currentMenu: ''
  }),
  actions: {
    setUserInfo(this: AuthState, userInfo: Record<string, any>): void {
      this.userInfo = userInfo
    },
    setToken(this: AuthState, data: string): void {
      this.token = data
    },
    setTenantId(this: AuthState, tenantId: string): void {
      this.tenantId = tenantId
    },
    setRole(this: AuthState, role: string): void {
      this.role = role
    },
    setCurrentMenu(this: AuthState, menu: string): void {
      this.currentMenu = menu
    }
  },
  persist: true
})