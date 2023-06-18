/*
 * @Author: fanciNate
 * @Date: 2023-04-26 17:01:16
 * @LastEditTime: 2023-06-18 14:56:21
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/src/services/workflow.service.ts
 */
import { http } from '@/utils/http'
interface SerchParams {
  page: number;
  pageSize: number;
  searchKeyWord: string;
}

export function GetWorkflowList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/wof/queryWorkflow',
    params: params
  })
}

// 添加
export function AddWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/wof/addWorkflow',
    params: params
  })
}

// 更新
export function UpdateWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/wof/updateWorkflow',
    params: params
  })
}

// 发布
// export function PublishWorkflowData(params: any): Promise<any> {
//     return http.request({
//         method: 'post',
//         url: '/wof/testConnect',
//         params: params
//     })
// }

// 删除
export function DeleteWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wof/delWorkflow',
    params: params
  })
}

// 作业-查询
export function GetWorkflowDetailList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/wok/queryWork',
    params: params
  })
}

// 作业-添加
export function AddWorkflowDetailList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/wok/addWork',
    params: params
  })
}

// 作业-更新
export function UpdateWorkflowDetailList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/wok/updateWork',
    params: params
  })
}

// 作业-删除
export function DeleteWorkflowDetailList(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wok/delWork',
    params: params
  })
}

// 作业-发布
export function PublishWorkData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/vip/wok/deployWork',
    params: params
  })
}

// 作业-详情-获取数据
export function GetWorkItemConfig(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wok/getWork',
    params: params
  })
}

// 作业-详情-运行
export function RunWorkItemConfig(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wok/runWork',
    params: params
  })
}

// 作业-详情-终止
export function TerWorkItemConfig(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wok/stopJob',
    params: params
  })
}

// 作业-详情-保存配置
export function SaveWorkItemConfig(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/woc/configWork',
    params: params
  })
}

// 作业-详情-提交日志
export function GetSubmitLogData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wok/getSubmitLog',
    params: params
  })
}

// 作业-详情-监控信息
export function GetResultItemDetail(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wok/getStatus',
    params: params
  })
}
