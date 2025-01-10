import { http } from '@/utils/http'
// ------------------------------- 采集任务 -----------------------------------
// 元数据 - 采集任务 - 分页查询
export function GetMetadataTaskList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/pageMetaWork',
        params: params
    })
}

// 元数据 - 采集任务 - 添加
export function AddMetadataTaskData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/addMetaWork',
        params: params
    })
}

// 元数据 - 采集任务 - 编辑更新
export function UpdateMetadataTaskData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/updateMetaWork',
        params: params
    })
}

// 元数据 - 采集任务 - 删除任务
export function DeleteMetadataTaskData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/deleteMetaWork',
        params: params
    })
}

// 元数据 - 采集任务 - 启用
export function EnableMetadataTaskData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/enableMetaWork',
        params: params
    })
}

// 元数据 - 采集任务 - 禁用
export function DisableMetadataTaskData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/disableMetaWork',
        params: params
    })
}

// 元数据 - 采集任务 - 立即采集
export function FastTriggerMetadataTaskData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/fastTriggerMetaWork',
        params: params
    })
}

// 元数据 - 采集任务 - 立即采集
export function TriggerMetadataTaskData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/triggerMetaWork',
        params: params
    })
}

// ------------------------------- 元数据管理 -----------------------------------
// 元数据 - 元数据管理 - 查询数据源接口
export function GetMetadataManagementList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/pageMetaDatabase',
        params: params
    })
}
// 元数据 - 元数据管理 - 刷新元数据（采集数据）
export function RefreshMetadataManagementList(): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/refreshMetaDatabase'
    })
}
// 元数据 - 元数据管理 - 查询表接口
export function GetMetadataTableList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/pageMetaTable',
        params: params
    })
}
// 元数据 - 元数据管理 - 查询字段列表
export function GetMetadataCodesList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/pageMetaColumn',
        params: params
    })
}

// 元数据 - 元数据管理 - 表基础信息
export function GetTableBasicInfo(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/getMetaTableInfo',
        params: params
    })
}

// 元数据 - 元数据管理 - 表字段信息
export function GetTableCodeInfo(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/getMetaTableColumn',
        params: params
    })
}

// 元数据 - 元数据管理 - 预览数据信息
export function GetTableDetailData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/getMetaTableData',
        params: params
    })
}

// 元数据 - 元数据管理 - 刷新基础信息
export function RefreshTableDetailData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/refreshMetaTableInfo',
        params: params
    })
}

// 元数据 - 元数据管理 - 导出
export function ExportTableDetailData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/exportTableExcel',
        params: params,
        responseType: 'blob'
    })
}

// ------------------------------- 元数据实例 -----------------------------------
// 元数据 - 采集实例 - 实例查询
export function RefreshMetadataInstanceList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/pageMetaWorkInstance',
        params: params
    })
}

// 元数据 - 采集实例 - 中止
export function AbortMetadataInstanceList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/abortMetaWorkInstance',
        params: params
    })
}

// 元数据 - 采集实例 - 删除
export function RemoveMetadataInstanceList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/deleteMetaWorkInstance',
        params: params
    })
}

// 元数据 - 数据地图 - 数据源备注编辑
export function DatasourceRemarkEdit(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/updateDatabaseComment',
        params: params
    })
}
// 元数据 - 数据地图 - 表备注编辑
export function TableRemarkEdit(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/updateTableComment',
        params: params
    })
}
// 元数据 - 数据地图 - 字段备注编辑
export function CodeRemarkEdit(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/meta/updateColumnComment',
        params: params
    })
}