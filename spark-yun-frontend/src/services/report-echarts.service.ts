import { http } from '@/utils/http'

// 报表组件-分页查询
export function QueryReportComponent(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/pageViewCard',
        params: params
    })
}

// 报表组件-创建组件
export function CreateReportComponentData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/addViewCard',
        params: params
    })
}
// 报表组件-删除组件
export function DeleteReportComponentData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/deleteViewCard',
        params: params
    })
}
// 报表组件-刷新数据
export function RefreshReportComponentData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/getViewCardData',
        params: params
    })
}
// 报表组件-单个sql预览
export function PreviewReportSingleComponentData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/getSqlData',
        params: params
    })
}

// 报表组件-发布大屏组件
export function PublishReportComponentData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/publishViewCard',
        params: params
    })
}
// 报表组件-下线大屏组件
export function OfflineReportComponentData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/offlineViewCard',
        params: params
    })
}
// 报表组件-查询大屏组件信息
export function GetReportComponentData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/getViewCard',
        params: params
    })
}
// 报表组件-配置大屏组件信息
export function ConfigReportComponentData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/configViewCard',
        params: params
    })
}