/*
 * @Author: fanciNate
 * @Date: 2023-04-26 17:01:16
 * @LastEditTime: 2023-05-03 21:36:23
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/services/computer-group.service.ts
 */
import { http } from '@/utils/http'

// 获取数据源驱动
export function GetDriverListData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/datasource/pageDatabaseDriver',
    params: params
  })
}
// 获取默认驱动
export function GetDefaultDriverData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/datasource/getDefaultDatabaseDriver',
    params: params
  })
}

// 删除驱动
export function DeleteDefaultDriverData(params: any): Promise<any> {
    return http.request({
      method: 'post',
      url: '/datasource/deleteDatabaseDriver',
      params: params
    })
}

// 添加驱动
export function AddDefaultDriverData(params: any): Promise<any> {
    return http.uploadFile({
      method: 'post',
      url: '/datasource/uploadDatabaseDriver',
      params: params
    })
}

// 设为默认驱动
export function SetDefaultDriverData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/datasource/settingDefaultDatabaseDriver',
    params: params
  })
}
