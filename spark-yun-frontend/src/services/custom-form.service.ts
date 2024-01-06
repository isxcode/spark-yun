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

// 查询表单配置
export function QueryFormConfigById(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/getForm',
        params: params
    })
}

// 保存表单配置
export function SaveFormConfigData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/saveFormComponents',
        params: params
    })
}

// 生成的表单查询列表
export function QueryFormDataList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/queryData',
        params: params
    })
}

// 生成的表单-添加数据
export function AddFormData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/addData',
        params: params
    })
}

// 生成的表单-更新数据
export function UpdateFormData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/updateData',
        params: params
    })
}