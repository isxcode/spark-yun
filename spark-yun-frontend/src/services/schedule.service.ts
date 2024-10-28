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
  executeStatus: string
  workflowId?: string
}

interface LogParam {
  instanceId: string;
}

interface workflowInstanceId {
  workflowInstanceId: string
}

// 获取调度历史查询数据---作业实例
export function GetScheduleList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/queryInstance',
    params: params
  })
}

// 获取调度历史查询数据---作业流实例
export function GetScheduleWorkFlowList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/workflow/queryWorkFlowInstances',
    params: params
  })
}

// 获取调度历史查询数据---作业流实例
export function GetScheduleDetail(params: workflowInstanceId): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/getWorkflowInstance',
    params: params
  })
}

// 获取日志
export function GetLogData(params: LogParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/getSubmitLog',
    params: params
  })
}

// 获取Yarn日志
export function GetYarnLogData(params: LogParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/getYarnLog',
    params: params
  })
}

// 重新运行
export function ReStartRunning(params: LogParam): Promise<any> {
  return http.request({
    method: 'get',
    url: '/vip/work-instance/restartInstance',
    params: params
  })
}

// 获取结果表
export function GetResultData(params: LogParam): Promise<any> {
  return http.request({
    method: 'post',
    url: '/work/getData',
    params: params
  })
}

// 删除调度历史-作业
export function DeleteScheduleLog(params: LogParam): Promise<any> {
  return http.request({
    method: 'get',
    url: '/vip/work-instance/deleteInstance',
    params: params
  })
}

// 删除调度历史-作业流
export function DeleteWorkFlowScheduleLog(params: workflowInstanceId): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/workflow-instance/deleteWorkflowInstance',
    params: params
  })
}

