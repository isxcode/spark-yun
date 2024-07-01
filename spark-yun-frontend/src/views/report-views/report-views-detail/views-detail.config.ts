export interface BreadCrumb {
    name: string;
    code: string;
    hidden?: boolean;
}

export const BreadCrumbList: Array<BreadCrumb> = [
    {
        name: '报表视图',
        code: 'report-views'
    },
    {
        name: '视图详情',
        code: 'report-views-detail'
    }
]

export const ChartTypeList = [
    {
        label: '饼图',
        value: 'pie'
    }
]
