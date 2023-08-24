/*
 * @Author: fanciNate
 * @Date: 2023-05-14 15:23:04
 * @LastEditTime: 2023-05-14 15:26:03
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/store/auth.store.ts
 */
export const authStore = {
  namespaced: true,
  state: {
    userInfo: {
    },
    token: '',
    tenantId: '',
    role: '',
    currentMenu: ''
  },
  mutations: {
    setUserInfo(state: any, data: any): void {
      state.userInfo = data
    },
    setToken(state: any, data: string): void {
      state.token = data
    },
    setTenantId(state: any, tenantId: string): void {
      state.tenantId = tenantId
    },
    setRole(state: any, role: string): void {
      state.role = role
    },
    setCurrentMenu(state: any, menu: string): void {
      state.currentMenu = menu
    }
  },
  actions: {
  },
  getters: {
  }
}

// export default {
//     namespaced: true,
//     state: {
//         userInfo: {},
//         token: ''
//     },
//     mutations: {
//         setUserInfo(state, data) {
//             state.userInfo = data
//         },
//         setToken(state, data) {
//             state.token = data
//         }
//     },
//     getters: {},
//     actions: {}
// } as Module<any, any>
