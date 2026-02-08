import { http } from '@/utils/http'

// etl获取转换函数
export function GetTransformFunction(): Promise<any> {
    return http.request({
        method: 'post',
        url: '/vip/work/listEtlTransformFunction',
    })
}
