import { http } from '@/utils/http'
interface SerchParams {
    page: number
    pageSize: number
    searchKeyWord: string
}

export function GetGlobalVariablesList(params: SerchParams): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/secret/pageSecret',
        params: params
    })
}

export function SaveGlobalVariablesData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/secret/addSecret',
        params: params
    })
}

export function UpdateGlobalVariablesData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/secret/updateSecret',
        params: params
    })
}

// 删除
export function DeleteGlobalVariablesData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/secret/deleteSecret',
        params: params
    })
}

// 一键复制
export function CopyGlobalVariablesPath(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/secret/getSecretExpression',
        params: params
    })
}
