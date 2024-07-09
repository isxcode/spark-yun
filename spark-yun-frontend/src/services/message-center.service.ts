import { http } from '@/utils/http'
// ------------------------------- 消息通知 -----------------------------------
// 消息通知 - 分页查询
export function GetMessagePagesList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/pageMessage',
        params: params
    })
}

// 消息通知 - 添加消息体
export function AddMessageData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/addMessage',
        params: params
    })
}

// 消息通知 - 更新消息体
export function UpdateMessageData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/updateMessage',
        params: params
    })
}

// 消息通知 - 删除消息体
export function DeleteMessageData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/deleteMessage',
        params: params
    })
}

// 消息通知 - 启用消息体
export function EnableMessageData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/enableMessage',
        params: params
    })
}
// 消息通知 - 禁用消息体
export function DisabledMessageData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/disableMessage',
        params: params
    })
}
// 消息通知 - 检测消息体
export function CheckMessageData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/checkMessage',
        params: params
    })
}

// ------------------------------- 基线告警 -----------------------------------
// 基线告警 - 分页查询
export function GetAlarmPagesList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/pageAlarm',
        params: params
    })
}
// 基线告警 - 添加
export function AddAlarmData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/addAlarm',
        params: params
    })
}
// 基线告警 - 更新告警
export function UpdateAlarmData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/updateAlarm',
        params: params
    })
}
// 基线告警 - 删除
export function DeleteAlarmData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/deleteAlarm',
        params: params
    })
}
// 基线告警 - 启用
export function EnableAlarmData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/enableAlarm',
        params: params
    })
}
// 基线告警 - 禁用
export function DisabledAlarmData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/disableAlarm',
        params: params
    })
}

// ------------------------------- 告警实例 -----------------------------------
// 告警实例 - 分页查询
export function GetAlarmInstancePagesList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/pageAlarmInstance',
        params: params
    })
}
// 告警实例 - 删除
export function DeleteAlarmInstanceData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/alarm/deleteAlarmInstance',
        params: params
    })
}