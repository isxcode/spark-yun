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
  tenantId?: string
}

interface FormUser {
  isTenantAdmin: boolean;
  userId: string;
  tenantId: string
}

interface DeleteParam {
  tenantUserId: string;
}

// 查询当前租户成员
export function GetUserList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant-user/pageTenantUser',
    params: params
  })
}

// 查询系统成员池信息
export function GetUserInfoList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/user/pageEnableUser',
    params: params
  })
}

// 添加成员
export function AddTenantUserData(params: FormUser): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant-user/addTenantUser',
    params: params
  })
}

// 编辑成员
export function EditTenantUserData(params: FormUser): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant-user/addTenantUser',
    params: params
  })
}

// 删除
export function DeleteTenantUser(params: DeleteParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant-user/removeTenantUser',
    params: params
  })
}

// 授权
export function GiveAuth(params: DeleteParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant-user/setTenantAdmin',
    params: params
  })
}

// 取消授权
export function RemoveAuth(params: DeleteParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/tenant-user/removeTenantAdmin',
    params: params
  })
}
