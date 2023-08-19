/*
 * @Author: fanciNate
 * @Date: 2023-04-26 17:01:16
 * @LastEditTime: 2023-06-18 13:07:27
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/src/services/computer-group.service.ts
 */
import { http } from '@/utils/http'
interface SerchParams {
  page: number;
  pageSize: number;
  searchKeyWord: string;
}

interface AddParams {
  remark: string;
  name: string;
  id?: string;
}

export function GetComputerGroupList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster/queryCluster',
    params: params
  })
}

// 添加集群
export function AddComputerGroupData(params: AddParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster/addCluster',
    params: params
  })
}

// 更新集群
export function UpdateComputerGroupData(params: AddParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster/updateEngine',
    params: params
  })
}

// 检测
export function CheckComputerGroupData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/cluster/checkCluster',
    params: params
  })
}

// 删除
export function DeleteComputerGroupData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster/deleteCluster',
    params: params
  })
}

// 节点页面查询数据
export function GetComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/eno/queryNode',
    params: params
  })
}

// 检测节点页面查询数据
export function CheckComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/eno/checkAgent',
    params: params
  })
}

// 编辑节点页面查询数据
export function UpdateComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/eno/updateNode',
    params: params
  })
}

// 添加节点数据
export function AddComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/eno/addNode',
    params: params
  })
}

// 编辑节点数据
export function EditComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/eno/updateNode',
    params: params
  })
}

// 添加节点数据
export function DeleteComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/eno/delNode',
    params: params
  })
}

// 安装节点数据
export function InstallComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/eno/installAgent',
    params: params
  })
}

// 卸载节点数据
export function UninstallComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/eno/removeAgent',
    params: params
  })
}

// 停止节点数据
export function StopComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/eno/stopAgent',
    params: params
  })
}

// 激活节点数据
export function StartComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/eno/startAgent',
    params: params
  })
}