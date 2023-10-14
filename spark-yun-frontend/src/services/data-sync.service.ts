import { http } from '@/utils/http'

interface SourceTablesParam {
    dataSourceId: string
}

interface TableDetailParam {
    dataSourceId: string
    tableName: string
}

// 作业流-数据同步-获取数据源表
export function GetDataSourceTables(params: SourceTablesParam): Promise<any> {
    return http.request({
        method: 'post',
        url: '/work/getDataSourceTables',
        params: params
    })
}

// 作业流-数据同步-数据预览
export function GetSourceTablesDetail(params: TableDetailParam): Promise<any> {
    return http.request({
        method: 'post',
        url: '/work/getDataSourceData',
        params: params
    })
}

// 作业流-数据同步-生成建表作业
export function CreateTableWork(params: TableDetailParam): Promise<any> {
    return http.request({
        method: 'post',
        url: '/work/getCreateTableSql',
        params: params
    })
}

// 作业流-数据同步-根据表获取字段
export function GetTableColumnsByTableId(params: TableDetailParam): Promise<any> {
    return http.request({
        method: 'post',
        url: '/work/getDataSourceColumns',
        params: params
    })
}
