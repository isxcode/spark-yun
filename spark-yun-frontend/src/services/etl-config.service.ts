import { http } from '@/utils/http'

// etl获取转换函数
export function GetTransformFunction(): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/work/listEtlTransformFunction',
    })
}

// etl获取过滤条件
export function GetEtlFilterCondition(): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/work/listEtlFilterCondition',
    })
}

// etl自定义节点解析
export function ParseCustomSqlFunction(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/work/parseEtlCustomDataSql',
        params: params
    })
}
