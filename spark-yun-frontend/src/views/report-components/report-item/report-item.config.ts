export interface BreadCrumb {
    name: string;
    code: string;
    hidden?: boolean;
}

export const BreadCrumbList: Array<BreadCrumb> = [
    {
        name: '报表组件',
        code: 'report-components'
    },
    {
        name: '组件详情',
        code: 'report-item'
    }
]

export const ChartTypeList = [
    {
        label: '饼图',
        value: 'pie'
    }
]

const validateSqls = (rule: any, value: any, callback: any) => {
    // debugger
    const status = (value || []).some((v: string) => !v)
    if (status) {
      callback(new Error('请将sql填写完整'))
    } else {
      callback()
    }
}


export const BaseConfigRules = {
    name: [{ required: true, message: '请输入名称', trigger: ['blur', 'change'] }],
    datasourceId: [{ required: true, message: '请选择数据源', trigger: ['blur', 'change'] }],
    sqls: [
        { validator: validateSqls, trigger: ['blur', 'change'] },
        { required: true, message: '请输入聚合Sql', trigger: ['blur', 'change'] }
    ],
}