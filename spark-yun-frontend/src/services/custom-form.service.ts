import { http } from '@/utils/http'

// 创建表单
export function CreateCustomFormData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/addForm',
        params: params
    })
}

// 查询表单
export function QueryCustomFormList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/pageForm',
        params: params
    })
}
