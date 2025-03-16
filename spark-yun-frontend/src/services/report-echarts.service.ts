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
// 报表组件-编辑组件
export function EditReportComponent(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/editViewCard',
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

// 报表大屏展示-配置大屏组件信息
export function QueryReportView(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/pageView',
        params: params
    })
}
// 报表大屏展示-添加视图
export function AddReportView(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/addView',
        params: params
    })
}
// 报表大屏展示-编辑视图
export function EditReportView(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/editView',
        params: params
    })
}
// 报表大屏展示-获取详情视图数据
export function GetReportViewDetail(params: any, config?: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/getView',
        params: params,
        headers: {
            ...config
        }
    })
}

// 报表大屏展示-保存大屏配置数据
export function SaveReportViewDetail(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/configView',
        params: params
    })
}

// 报表大屏展示-刷新真实数据接口
export function RefreshReportViewItemData(params: any, config?: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/getViewCardDataById',
        params: params,
        headers: {
            ...config
        }
    })
}

// 报表大屏展示-发布大屏
export function PublishReportViewData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/publishView',
        params: params
    })
}
// 报表大屏展示-下线
export function UnderlineReportViewData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/offlineView',
        params: params
    })
}
// 报表大屏展示-删除
export function DeleteReportViewData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/deleteView',
        params: params
    })
}

// 获取分享大屏链接的id接口
export function GetChartsLinkConfig(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/getViewLink',
        params: params
    })
}

// 根据linkId 获取相关配置接口
export function GetChartsLinkInfoConfig(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/view/open/getViewLinkInfo',
        params: params
    })
}
