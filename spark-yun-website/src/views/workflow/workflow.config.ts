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
  remark: string;
}

export const BreadCrumbList: Array<BreadCrumb> = [
  {
    name: '作业流',
    code: 'workflow'
  }
]

export const colConfigs: colConfig[] = [
  {
    prop: 'name',
    title: '作业流名称',
    minWidth: 100,
    customSlot: 'nameSlot',
    showOverflowTooltip: true
  },
  // {
  //   prop: 'status',
  //   title: '发布状态',
  //   minWidth: 100,
  //   customSlot: 'statusTag'
  // },
  {
    prop: 'remark',
    title: '备注',
    minWidth: 100
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

export const DetailColConfigs: colConfig[] = [
  {
    prop: 'name',
    title: '作业名称',
    minWidth: 120,
    customSlot: 'nameSlot',
    showOverflowTooltip: true
  },
  {
    prop: 'workType',
    title: '类型',
    minWidth: 100,
    customSlot: 'typeSlot',
    showOverflowTooltip: true
  },
  {
    prop: 'status',
    title: '状态',
    minWidth: 100,
    customSlot: 'statusTag'
  },
  {
    prop: 'createDateTime',
    title: '创建时间',
    minWidth: 140
  },
  {
    prop: 'remark',
    title: '备注',
    minWidth: 100
  },
  {
    title: '操作',
    align: 'center',
    customSlot: 'options',
    width: 80
  }
]

export const DetailTableConfig: TableConfig = {
  tableData: [],
  colConfigs: DetailColConfigs,
  pagination: {
    currentPage: 1,
    pageSize: 10,
    total: 0
  },
  seqType: 'seq',
  loading: false
}
