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

export interface SerchParams {
  page: number;
  pageSize: number;
  searchKeyWord: string;
}

export interface FormData {
  name: string;
  comment: string;
}

export const BreadCrumbList: Array<BreadCrumb> = [
  {
    name: '数据源',
    code: 'datasource'
  }
]

export const colConfigs: colConfig[] = [
  {
    prop: 'name',
    title: '数据源名称',
    minWidth: 100,
    showOverflowTooltip: true
  },
  {
    prop: 'type',
    title: '类型',
    minWidth: 100,
    showOverflowTooltip: true
  },
  {
    prop: 'jdbcUrl',
    title: '连接信息',
    minWidth: 160,
    showOverflowTooltip: true
  },
  {
    prop: 'username',
    title: '用户名',
    minWidth: 100,
    showOverflowTooltip: true
  },
  {
    prop: 'status',
    title: '状态',
    minWidth: 100,
    customSlot: 'statusTag'
  },
  {
    prop: 'checkTime',
    title: '检测时间',
    minWidth: 140
  },
  {
    prop: 'comment',
    title: '备注',
    minWidth: 100
  },
  {
    title: '操作',
    align: 'center',
    customSlot: 'options',
    width: 110
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
