import { http } from '@/utils/http'

// 创建表单
export function CreateCustomFormData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/addForm',
        params: params
    })
}
// 更新表单
export function UpdateCustomFormData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/updateForm',
        params: params
    })
}
// 发布表单
export function DeployCustomFormData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/deployForm',
        params: params
    })
}
// 下线表单
export function OfflineCustomFormData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/offlineForm',
        params: params
    })
}
// 删除表单
export function DeleteCustomFormData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/deleteForm',
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
        url: '/vip/form/getFormConfig',
        params: params
    })
}

// 保存表单配置
export function SaveFormConfigData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/saveFormConfig',
        params: params
    })
}

// 生成的表单查询列表
export function QueryFormDataList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/pageData',
        params: params
    })
}

// 生成的表单-添加数据
export function AddFormData(params: any, config: any = {}): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/addData',
        params: params,
        headers: {
            ...config
        }
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

// 生成的表单-更新数据
export function DeleteFormData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/deleteData',
        params: params
    })
}

//  ----------- 分享表单 ------------
interface customTokenParam {
    validDay: number
}
export function ShareFormGetCustomToken(params: customTokenParam): Promise<any> {
    return http.request({
        method: 'post',
        url: '/user/getAnonymousToken',
        params: params
    })
}

export function ShareFormGetFormConfig(params: any, config: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/form/getFormConfigForAnonymous',
        params: params,
        headers: {
            ...config
        }
    })
}
