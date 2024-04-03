import { http } from '@/utils/http'
import type { ColonyInfo } from '../components/component'

export type QueryComputeInstancesParam = {
  page: number
  pageSize: number
  searchKeyWord?: string | null
}

export type ComputeInstance = {
  workflowInstanceId: string
  workflowName: string
  duration: number
  startDateTime: string
  endDateTime: string
  status: string
  lastModifiedBy: string
}

export interface ResponseWarp<T> {
  code: number
  data: T
  msg: string
}

export type QueryComputeInstancesResponse = {
  totalElements: number
  content: ComputeInstance[]
}

export function queryComputeInstances(data: QueryComputeInstancesParam) {
  return http.request<ResponseWarp<QueryComputeInstancesResponse>>({
    method: 'post',
    url: '/monitor/pageInstances',
    data
  })
}

export type SystemBaseInfoItem = {
  total: number
  activeNum: number
}

export type SystemBaseInfo = {
  clusterMonitor: SystemBaseInfoItem
  datasourceMonitor: SystemBaseInfoItem
  workflowMonitor: SystemBaseInfoItem
  apiMonitor: SystemBaseInfoItem
}

export function querySystemBaseInfo() {
  return http.request<ResponseWarp<SystemBaseInfo>>({
    method: 'post',
    url: '/monitor/getSystemBaseInfo',
  })
}

export type QueryVmChartInfoParams = {
  localDate: string
}

export type queryVmChartInfoResponse = {
  instanceNumLine: Array<{
    runningNum: number
    successNum: number
    failNum: number
    localTime: string
  }>
}

export function queryVmChartInfo(data: QueryVmChartInfoParams) {
  return http.request<ResponseWarp<queryVmChartInfoResponse>>({
    method: 'post',
    url: '/monitor/getInstanceMonitor',
    data
  })
}

export type QueryClusterMonitorInfoParams = {
  clusterId: string
  timeType: 'THIRTY_MIN' | 'ONE_HOUR' | 'TWO_HOUR' | 'SIX_HOUR' | 'TWELVE_HOUR' | 'ONE_DAY' | 'SEVEN_DAY' | 'THIRTY_DAY'
}

export type ClusterMonitorInfo = {
  activeNodeSize: number
  tenantId: string
  dateTime: string
  cpuPercent: string
  diskIoReadSpeed: string
  networkIoReadSpeed: string
  diskIoWriteSpeed: string
  networkIoWriteSpeed: string
  usedMemorySize: string
  usedStorageSize: string
}

export type QueryClusterMonitorInfoResponse = {
  line: ClusterMonitorInfo[]
}

export function queryClusterMonitorInfo(data: QueryClusterMonitorInfoParams) {
  return http.request<ResponseWarp<QueryClusterMonitorInfoResponse>>({
    method: 'post',
    url: '/monitor/getClusterMonitor',
    data
  })
}


export function queryAllClusterInfo() {
  return http.request<ResponseWarp<ColonyInfo[]>>({
    method: 'post',
    url: '/cluster/queryAllCluster'
  })
}