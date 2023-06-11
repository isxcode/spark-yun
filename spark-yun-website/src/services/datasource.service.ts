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

export function GetDatasourceList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/das/queryDatasource',
    params: params
  })
}

// 添加
export function AddDatasourceData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/das/addDatasource',
    params: params
  })
}

// 更新
export function UpdateDatasourceData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/das/updateDatasource',
    params: params
  })
}

// 检测
export function CheckDatasourceData(params: any): Promise<any> {
  return http.request({
    method: "get",
    url: "/das/testConnect",
    params: params,
  });
}

// 删除
export function DeleteDatasourceData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/das/delDatasource',
    params: params
  })
}
