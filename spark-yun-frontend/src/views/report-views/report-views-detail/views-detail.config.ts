export interface BreadCrumb {
    name: string;
    code: string;
    hidden?: boolean;
}

export const BreadCrumbList: Array<BreadCrumb> = [
    {
        name: '数据大屏',
        code: 'report-views'
    },
    {
        name: '大屏详情',
        code: 'report-views-detail'
    }
]

export const ChartTypeList = [
    {
        label: '饼图',
        value: 'pie'
    }
]
