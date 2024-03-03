export interface BreadCrumb {
  name: string;
  code: string;
  hidden?: boolean;
}

export interface colConfig {
  prop?: string;
  title: string;
  align?: string;
  showOverflowTooltip?: boolean;
  customSlot?: string;
  width?: number;
  minWidth?: number;
  formatter?: any
}

export interface Pagination {
  currentPage: number;
  pageSize: number;
  total: number;
}

export interface TableConfig {
  tableData: Array<any>;
  colConfigs: Array<colConfig>;
  seqType: string;
  pagination?: Pagination; // 分页数据
  loading?: boolean; // 表格loading
}

export const BreadCrumbList: Array<BreadCrumb> = [
  {
    name: '函数仓库',
    code: 'custom-func'
  }
]

export const colConfigs: colConfig[] = [
  {
    prop: 'funcName',
    title: '函数名称',
    minWidth: 125,
    showOverflowTooltip: true
  },
  {
    prop: 'type',
    title: '类型',
    minWidth: 80,
    // formatter: (data: any) => {
    //   const obj = {
    //     JOB: '作业',
    //     FUNC: '函数',
    //     LIB: '依赖'
    //   }
    //   return obj[data.cellValue]
    // }
  },
  {
    prop: 'fileName',
    title: '资源文件',
    minWidth: 140,
    showOverflowTooltip: true
  },
  {
    prop: 'className',
    title: '类名',
    minWidth: 100,
    showOverflowTooltip: true
  },
  {
    prop: 'resultType',
    title: '结果类型',
    minWidth: 100,
    showOverflowTooltip: true
  },
  {
    prop: 'createDateTime',
    title: '创建时间',
    minWidth: 140
  },
  {
    prop: 'remark',
    title: '备注',
    minWidth: 100,
    showOverflowTooltip: true
  },
  {
    title: '操作',
    align: 'center',
    customSlot: 'options',
    width: 80
  }
]

export const TableConfig: TableConfig = {
  tableData: [],
  colConfigs: colConfigs,
  pagination: {
    currentPage: 1,
    pageSize: 10,
    total: 0
  },
  seqType: 'seq',
  loading: false
}
