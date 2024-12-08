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
  fixed?: string;
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
    title: '名称',
    minWidth: 180,
    customSlot: 'nameSlot',
    showOverflowTooltip: true
  },
  {
    prop: 'status',
    title: '发布状态',
    minWidth: 80,
    customSlot: 'statusTag'
  },
  {
    prop: 'nextDateTime',
    title: '下次执行时间',
    minWidth: 120,
    showOverflowTooltip: true
  },
  {
    prop: 'createUsername',
    title: '创建人',
    minWidth: 80
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

export const TypeList = [
  {
    label: 'Jdbc执行作业',
    value: 'EXE_JDBC'
  },
  {
    label: 'Jdbc查询作业',
    value: 'QUERY_JDBC'
  },
  {
    label: 'Prql查询作业',
    value: 'PRQL'
  },
  {
    label: 'Curl作业',
    value: 'CURL'
  },
  {
    label: 'SparkSql查询作业',
    value: 'SPARK_SQL'
  },
  {
    label: 'SparkSql容器作业',
    value: 'SPARK_CONTAINER_SQL'
  },
  {
    label: '数据同步作业',
    value: 'DATA_SYNC_JDBC'
  },
  {
    label: 'Bash作业',
    value: 'BASH'
  },
  {
    label: 'Python作业',
    value: 'PYTHON'
  },
  {
    label: '自定义作业',
    value: 'SPARK_JAR'
  },
  {
    label: '接口调用作业',
    value: 'API'
  },
  {
    label: 'Excel导入作业',
    value: 'EXCEL_SYNC_JDBC'
  }
]