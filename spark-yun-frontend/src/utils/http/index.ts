/*
 * @Author: fanciNate
 * @Date: 2023-05-23 17:00:06
 * @LastEditTime: 2023-06-05 22:32:18
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/src/utils/http/index.ts
 */
import { createAxios } from '@/plugins/http-request'
import router from '@/router'
import { merge } from '../checkType'
import { ElMessage } from 'element-plus'
import * as process from 'process'
import { useAuthStore } from '@/store/useAuth'

const message = ElMessage

const whiteList = ['/vip/auth/open/querySsoAuth', '/vip/license/open/checkLicense']
let isRefreshingLicense = false
let refreshTokenRequest: Promise<string> | null = null

function clearAuthAndRedirect(): void {
  const authStore = useAuthStore()
  authStore.setUserInfo({})
  authStore.setToken('')
  authStore.setRefreshToken('')
  authStore.setTenantId('')
  authStore.setRole('')
  authStore.setCurrentMenu('')
  router.push({
    name: 'login'
  })
}

function isRefreshTokenRequest(response: any): boolean {
  return Boolean(response?.config?.url?.includes('/user/open/refreshToken'))
}

async function refreshAccessToken(): Promise<string> {
  const authStore = useAuthStore()
  if (!authStore.refreshToken) {
    throw new Error('refreshToken is empty')
  }

  if (!refreshTokenRequest) {
    const urlPrefix = import.meta.env.VITE_VUE_APP_BASE_DOMAIN || ''
    refreshTokenRequest = fetch(`${urlPrefix}/user/open/refreshToken`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        refreshToken: authStore.refreshToken
      })
    })
      .then(async response => {
        const result = await response.json()
        const code = String(result.code)
        if (!response.ok || (code !== '200' && code !== '100200')) {
          throw new Error(result.msg || '刷新token失败')
        }
        authStore.setUserInfo(result.data || authStore.userInfo)
        authStore.setToken(result.data?.token || '')
        authStore.setRefreshToken(result.data?.refreshToken || '')
        authStore.setTenantId(result.data?.tenantId || '')
        authStore.setRole(result.data?.role || '')
        return result.data?.token || ''
      })
      .finally(() => {
        refreshTokenRequest = null
      })
  }

  return refreshTokenRequest
}

function isLicenseMissingError(msg: string): boolean {
  return typeof msg === 'string' && msg.includes('请上传许可证')
}

async function refreshLicenseAndReload(): Promise<void> {
  if (isRefreshingLicense || typeof window === 'undefined') {
    return
  }
  isRefreshingLicense = true

  try {
    const authStore = useAuthStore()
    const urlPrefix = import.meta.env.VITE_VUE_APP_BASE_DOMAIN || ''
    await fetch(`${urlPrefix}/vip/license/open/checkLicense`, {
      method: 'GET',
      headers: {
        authorization: authStore.token || ''
      }
    })
  } catch (error) {
    console.error('refresh license error', error)
  } finally {
    window.location.reload()
  }
}

export const httpOption = {
  transform: {
    requestInterceptors: (config: any) => {
      const authStore = useAuthStore()
      config.headers['authorization'] = config.headers['authorization'] || authStore.token

      return config
    },
    responseInterceptors: (config: any): any => {
      // getTokenFromResponse(config);

      return config
    }
  },
  requestOptions: {
    // urlPrefix: 'http://isxcode.com:30211',
    urlPrefix: import.meta.env.VITE_VUE_APP_BASE_DOMAIN,
    showSuccessMessage: (msg: string): void => {
      message.success(msg)
    },
    showErrorMessage: (msg: string): void => {
      message.error(msg)
      if (isLicenseMissingError(msg)) {
        refreshLicenseAndReload()
      }
    },
    checkStatus: (status: number, msg: string, showMsg: any, response: any): void => {
      try {
        if (status == 401) {
          if (isRefreshTokenRequest(response)) {
            clearAuthAndRedirect()
          }
        } else if (status == 403) {
          message.error('许可证无效，请联系管理员')
        } else if (status == 404) {
          if (response.config.url.match('/vip/')) {
            if (!whiteList.some(url => response.config.url.match(url))) {
              message.error('请升级到企业版')
            }
          } else {
            showMsg(msg)
          }
        } else {
          showMsg(msg)
        }
      } catch (error) {
        console.error('error', error)
        // console.log('err', error);
        // router.replace({
        //     name: LOGIN_NAME,
        //     query: {
        //         redirect: '/home'
        //     }
        // });
      }
    },
    refreshToken: async (response: any): Promise<string> => {
      if (isRefreshTokenRequest(response)) {
        clearAuthAndRedirect()
        throw new Error('刷新token失败')
      }

      try {
        return await refreshAccessToken()
      } catch (error) {
        clearAuthAndRedirect()
        throw error
      }
    }
  },
  timeout: 30 * 1e3
}

export const createHttp = (option = {
}) => {
  return createAxios(
    merge(
      {
      },
      {
        ...httpOption
      },
      {
        ...option
      }
    )
  )
}

export const http = createHttp()
