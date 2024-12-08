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
  fixed?: string;
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
    name: '计算集群',
    code: 'computer-group'
  }
]

export const colConfigs: colConfig[] = [
  {
    prop: 'name',
    title: '名称',
    minWidth: 120,
    customSlot: 'nameSlot',
    showOverflowTooltip: true
  },
  {
    prop: 'clusterType',
    title: '类型',
    minWidth: 100,
    showOverflowTooltip: true,
  },
  {
    prop: 'node',
    title: '节点',
    minWidth: 60,
    showOverflowTooltip: true
  },
  {
    prop: 'memory',
    title: '内存',
    minWidth: 100,
    showOverflowTooltip: true
  },
  {
    prop: 'storage',
    title: '存储',
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
    prop: 'defaultCluster',
    title: '默认集群',
    minWidth: 80,
    customSlot: 'defaultTag'
  },
  {
    prop: 'checkDateTime',
    title: '检测时间',
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
    width: 80,
    fixed: 'right'
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

export const PointColConfigs: colConfig[] = [
  {
    prop: 'name',
    title: '名称',
    minWidth: 120,
    showOverflowTooltip: true
  },
  {
    prop: 'host',
    title: '地址',
    minWidth: 80,
    showOverflowTooltip: true
  },
  {
    prop: 'cpu',
    title: 'CPU',
    minWidth: 60,
    showOverflowTooltip: true
  },
  {
    prop: 'memory',
    title: '内存',
    minWidth: 60,
    showOverflowTooltip: true
  },
  {
    prop: 'storage',
    title: '存储',
    minWidth: 60,
    showOverflowTooltip: true
  },
  {
    prop: 'status',
    title: '状态',
    minWidth: 100,
    customSlot: 'statusTag'
  },
  // {
  //   prop: 'defaultClusterNode',
  //   title: '是否默认节点',
  //   minWidth: 120,
  //   customSlot: 'defaultNodeTag'
  // },
  {
    prop: 'checkDateTime',
    title: '检测时间',
    minWidth: 140
  },
  {
    prop: 'remark',
    title: '备注',
    minWidth: 120,
    showOverflowTooltip: true
  },
  {
    title: '操作',
    align: 'center',
    customSlot: 'options',
    width: 80,
    fixed: 'right'
  }
]

export const PointTableConfig: TableConfig = {
  tableData: [],
  colConfigs: PointColConfigs,
  pagination: {
    currentPage: 1,
    pageSize: 10,
    total: 0
  },
  seqType: 'seq',
  loading: false
}
