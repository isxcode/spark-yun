import { http } from '@/utils/http'
interface SerchParams {
    page: number
    pageSize: number
    searchKeyWord: string
}

// 分页查询
export function GetTimeComputingList(params: SerchParams): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/pageReal',
        params: params
    })
}

// 新建
export function SaveTimeComputingData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/addReal',
        params: params
    })
}

// 更新
export function UpdateTimeComputingData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/updateReal',
        params: params
    })
}

// 删除
export function DeleteTimeComputingData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/deleteReal',
        params: params
    })
}

// 运行
export function RunTimeComputingData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/startReal',
        params: params
    })
}

// 停止
export function StopTimeComputingData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/stopReal',
        params: params
    })
}

// 检测
export function CheckComputingStatus(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/checkReal',
        params: params
    })
}

// 配置实时作业
export function ConifgTimeComputingData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/configReal',
        params: params
    })
}
// 获取实时作业详情
export function GetTimeComputingDetail(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/getReal',
        params: params
    })
}

// 查询topic列表
export function GetTopicDataList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/queryTopic',
        params: params
    })
}

// 查询json字段节点
export function GetJsonParamNodeList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/queryColumnPath',
        params: params
    })
}

// 查询json数组节点
export function GetJsonArrayNodeList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/queryArrayPath',
        params: params
    })
}

// 获取提交日志
export function GetRealSubLog(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/getRealSubmitLog',
        params: params
    })
}
// 获取运行日志
export function GetRealSubRunningLog(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/real/getRealRunningLog',
        params: params
    })
}
