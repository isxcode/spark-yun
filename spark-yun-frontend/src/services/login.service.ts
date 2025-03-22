import { http } from '@/utils/http'

interface LoginParam {
  account: string;
  passwd: string;
}

interface TenantParams {
  tenantId: string;
}

// 登录接口
export function LoginUserInfo(params: LoginParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/user/open/login',
    params: params
  })
}

// 查询租户信息
export function QueryTenantList(): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant/queryUserTenant'
  })
}

// 切换租户
export function ChangeTenantData(params: TenantParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant/chooseTenant',
    params: params
  })
}

// 单点登录模块 -----
// 单点配置列表查询
export function OauthQueryList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/auth/pageSsoAuth',
    params: params
  })
}

// 单点配置创建
export function OauthCreateData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/auth/addSsoAuth',
    params: params
  })
}
// 单点配置更新
export function OauthUpdateData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/auth/updateSsoAuth',
    params: params
  })
}
// 单点配置删除
export function OauthDeleteData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/auth/deleteSsoAuth',
    params: params
  })
}

// 单点配置启用
export function OauthEnableData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/auth/enableSsoAuth',
    params: params
  })
}

// 单点配置禁用
export function OauthDisableData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/auth/disableSsoAuth',
    params: params
  })
}

// 单点地址复制
export function OauthUrlCopy(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/auth/getSsoInvokeUrl',
    params: params
  })
}

// 登录页面获取第三方单点登录地址
export function OauthUrlList(): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/auth/open/querySsoAuth'
  })
}


// 单点登录验证
export function OauthLogin(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/auth/open/ssoLogin',
    params: params
  })
}