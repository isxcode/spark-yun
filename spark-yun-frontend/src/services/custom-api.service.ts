import { http } from '@/utils/http'

// 自定义接口分页查询
export function QueryCustomApiList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/api/pageApi',
        params: params
    })
}

// 创建api
export function CreateCustomApiData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/api/addApi',
        params: params
    })
}
// 更新api
export function UpdateCustomApiData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/api/updateApi',
        params: params
    })
}
// 删除api
export function DeleteCustomApiData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/api/deleteApi',
        params: params
    })
}
// 发布api
export function PublishCustomApiData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/api/publishApi',
        params: params
    })
}
// 下线api
export function OfflineCustomApiData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/api/offlineApi',
        params: params
    })
}
