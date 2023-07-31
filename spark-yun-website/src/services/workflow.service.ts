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

// 作业-下线
export function DeleteWorkData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/vip/wok/deleteWork',
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

// 工作流------------流程图接口
// 保存工作流--流程图
export function SaveWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/wfc/configWorkflow',
    params: params
  })
}

// 获取配置的工作流程图信息--流程图
export function GetWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wof/getWorkflow',
    params: params
  })
}

// 运行工作流--流程图
export function RunWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wof/runFlow',
    params: params
  })
}

// 中止正在运行的工作流--流程图
export function StopWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wof/abortFlow',
    params: params
  })
}

// 查询作业流实例接口--流程图
export function QueryRunWorkInstances(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wof/queryRunWorkInstances',
    params: params
  })
}

// 导出作业--流程图
export function ExportWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/wof/exportWorks',
    params: params
  })
}
// 导入作业--流程图
export function ImportWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/wof/importWorks',
    params: params
  })
}

// 发布作业流--流程图
export function PublishWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/vip/wok/deployWorkflow',
    params: params
  })
}

// 发布作业流--流程图--重跑下游节点
export function RunAfterFlowData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wof/runAfterFlow',
    params: params
  })
}
// 发布作业流--流程图--中断
export function BreakFlowData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wof/breakFlow',
    params: params
  })
}
// 发布作业流--流程图--重跑当前节点
export function RerunCurrentNodeFlowData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wof/runCurrentNode',
    params: params
  })
}

// 作业流--流程图--重跑工作流
export function ReRunWorkflow(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/wof/reRunFlow',
    params: params
  })
}
