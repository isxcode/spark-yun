import { http } from '@/utils/http'

// demo
export function GetDataSourceTables(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '',
        params: params
    })
}
