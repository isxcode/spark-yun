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
    url: '/workflow/pageWorkflow',
    params: params
  })
}

// 添加
export function AddWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/addWorkflow',
    params: params
  })
}

// 更新
export function UpdateWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/updateWorkflow',
    params: params
  })
}

// 发布
// export function PublishWorkflowData(params: any): Promise<any> {
//     return http.request({
//         method: 'post',
//         url: '/workflow/testConnect',
//         params: params
//     })
// }

// 删除
export function DeleteWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/deleteWorkflow',
    params: params
  })
}

// 作业-查询
export function GetWorkflowDetailList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/pageWork',
    params: params
  })
}

// 作业-添加
export function AddWorkflowDetailList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/addWork',
    params: params
  })
}

// 作业-更新
export function UpdateWorkflowDetailList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/updateWork',
    params: params
  })
}

// 作业-复制
export function CopyWorkflowDetailList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/copyWork',
    params: params
  })
}

// 作业-删除
export function DeleteWorkflowDetailList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/deleteWork',
    params: params
  })
}

// 作业-发布
export function PublishWorkData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/vip/work/deployWork',
    params: params
  })
}

// 作业-下线
export function DeleteWorkData(params: any): Promise<any> {
  return http.request({
    method: 'get',
    url: '/vip/work/deleteWork',
    params: params
  })
}

// 作业-详情-获取数据
export function GetWorkItemConfig(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/getWork',
    params: params
  })
}

// 作业-详情-运行
export function RunWorkItemConfig(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/runWork',
    params: params
  })
}

// 作业-详情-终止
export function TerWorkItemConfig(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/stopJob',
    params: params
  })
}

// 作业-详情-保存配置
export function SaveWorkItemConfig(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/configWork',
    params: params
  })
}

// 作业-详情-提交日志
export function GetSubmitLogData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/getSubmitLog',
    params: params
  })
}

// 作业-详情-监控信息
export function GetResultItemDetail(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/getStatus',
    params: params
  })
}

// 工作流------------流程图接口
// 保存工作流--流程图
export function SaveWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/configWorkflow',
    params: params
  })
}

// 获取配置的工作流程图信息--流程图
export function GetWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/getWorkflow',
    params: params
  })
}

// 运行工作流--流程图
export function RunWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/runWorkflow',
    params: params
  })
}

// 中止正在运行的工作流--流程图
export function StopWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/abortFlow',
    params: params
  })
}

// 查询作业流实例接口--流程图
export function QueryRunWorkInstances(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/getRunWorkInstances',
    params: params
  })
}

// 导出作业--流程图
export function ExportWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/exportWorks',
    params: params
  })
}
// 作业流配置保存
export function SaveWorkflowConfigData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/configWorkflowSetting',
    params: params
  })
}
// 导入作业--流程图
export function ImportWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/importWorks',
    params: params
  })
}

// 发布作业流--流程图
export function PublishWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/work/deployWorkflow',
    params: params
  })
}
// 下线作业流--流程图
export function UnderlineWorkflowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/work/killWorkflow',
    params: params
  })
}

// 发布作业流--流程图--重跑下游节点
export function RunAfterFlowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/runAfterFlow',
    params: params
  })
}
// 发布作业流--流程图--中断
export function BreakFlowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/breakFlow',
    params: params
  })
}
// 发布作业流--流程图--重跑当前节点
export function RerunCurrentNodeFlowData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/runCurrentNode',
    params: params
  })
}

// 作业流--流程图--重跑工作流
export function ReRunWorkflow(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/reRunFlow',
    params: params
  })
}
