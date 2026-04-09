export const DataSourceType = [
  {
    label: '接口',
    value: 'API'
  },
  {
    label: '数据源',
    value: 'DATASOURCE'
  }
]
export const CurrentSourceType = [
  {
    label: '接口',
    value: 'API'
  },
  {
    label: '数据源',
    value: 'DATASOURCE'
  }
]
export const OverModeList = [
  {
    label: '追加模式',
    value: 'INTO',
  },
  {
    label: '覆写模式',
    value: 'OVERWRITE',
  }
]

export interface BreadCrumb {
  name: string;
  code: string;
  hidden?: boolean;
}

export const BreadCrumbList: Array<BreadCrumb> = [
  {
    name: '实时计算',
    code: 'realtime-computing'
  },
  {
    name: '详情',
    code: 'computing-detail'
  }
]
