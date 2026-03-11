import { http } from '@/utils/http'

// 分页查询黑白名单
export function QueryAccessRuleList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/api-service/pageAccessRule',
        params: params
    })
}

// 添加黑白名单规则
export function CreateAccessRule(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/api-service/addAccessRule',
        params: params
    })
}

// 更新黑白名单规则
export function UpdateAccessRule(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/api-service/updateAccessRule',
        params: params
    })
}

// 删除黑白名单规则
export function DeleteAccessRule(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/api-service/deleteAccessRule',
        params: params
    })
}

