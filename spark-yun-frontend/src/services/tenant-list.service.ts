/*
 * @Author: fanciNate
 * @Date: 2023-04-26 17:01:16
 * @LastEditTime: 2023-05-03 21:36:23
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/services/computer-group.service.ts
 */
import { http } from '@/utils/http'

interface SerchParams {
  page: number;
  pageSize: number;
  searchKeyWord: string;
}

interface TenantParam {
  adminUserId?: string;
  maxMemberNum: string;
  maxWorkflowNum: string;
  name: string;
  remark: string;
  id?: string;
}

interface TenantIdParam {
  tenantId: string;
}

// 租户列表-查询租户
export function GetTenantList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant/pageTenant',
    params: params
  })
}

// 租户列表-添加租户
export function AddTenantData(params: TenantParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant/addTenant',
    params: params
  })
}

// 租户列表-更新租户
export function UpdateTenantData(params: TenantParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant/updateTenantForSystemAdmin',
    params: params
  })
}

// 租户列表-同步
export function CheckTenantData(params: TenantIdParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant/checkTenant',
    params: params
  })
}

// 租户列表-禁用
export function DisableTenantData(params: TenantIdParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant/disableTenant',
    params: params
  })
}

// 租户列表-启用
export function EnableTenantData(params: TenantIdParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant/enableTenant',
    params: params
  })
}

// 租户列表-删除租户
export function DeleteTenantData(params: TenantIdParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant/deleteTenant',
    params: params
  })
}
