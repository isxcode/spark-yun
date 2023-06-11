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

interface FormUser {
  tenantAdmin: boolean;
  userId: string;
}

interface DeleteParam {
  tenantUserId: string;
}

// 查询当前租户成员
export function GetUserList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tur/queryTenantUser',
    params: params
  })
}

// 查询系统成员池信息
export function GetUserInfoList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/usr/queryAllEnableUsers',
    params: params
  })
}

// 添加成员
export function AddTenantUserData(params: FormUser): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tur/addTenantUser',
    params: params
  })
}

// 编辑成员
export function EditTenantUserData(params: FormUser): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tur/addTenantUser',
    params: params
  })
}

// 删除
export function DeleteTenantUser(params: DeleteParam): Promise<any> {
  return http.request({
    method: 'get',
    url: '/tur/removeTenantUser',
    params: params
  })
}

// 授权
export function GiveAuth(params: DeleteParam): Promise<any> {
  return http.request({
    method: 'get',
    url: '/tur/setTenantAdmin',
    params: params
  })
}

// 取消授权
export function RemoveAuth(params: DeleteParam): Promise<any> {
  return http.request({
    method: 'get',
    url: '/tur/removeTenantAdmin',
    params: params
  })
}
