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
    name: '数据源',
    code: 'datasource'
  }
]
export const typeList =[
  {
    label: 'Clickhouse',
    value: 'CLICKHOUSE'
  },
  {
    label: 'Db2',
    value: 'DB2'
  },
  {
    label: 'Doris',
    value: 'DORIS'
  },
  {
    label: 'DuckDB',
    value: 'DUCK_DB'
  },
  {
    label: '达梦',
    value: 'DM'
  },
  {
    label: 'Gauss',
    value: 'GAUSS'
  },
  {
    label: 'Gbase',
    value: 'GBASE'
  },
  {
    label: 'Greenplum',
    value: 'GREENPLUM'
  },
  {
    label: 'H2',
    value: 'H2'
  },
  {
    label: 'HanaSap',
    value: 'HANA_SAP'
  },
  {
    label: 'Hive',
    value: 'HIVE'
  },
  {
    label: 'Impala',
    value: 'IMPALA'
  },
  {
    label: 'Kafka',
    value: 'KAFKA'
  },
  {
    label: 'Mysql',
    value: 'MYSQL'
  },
  {
    label: 'OceanBase',
    value: 'OCEANBASE'
  },
  {
    label: 'OpenGauss',
    value: 'OPEN_GAUSS'
  },
  {
    label: 'Oracle',
    value: 'ORACLE'
  },
  {
    label: 'PostgreSql',
    value: 'POSTGRE_SQL'
  },
  {
    label: 'Presto',
    value: 'PRESTO'
  },
  {
    label: 'SelectDB',
    value: 'SELECT_DB'
  },
  {
    label: 'SqlServer',
    value: 'SQL_SERVER'
  },
  {
    label: 'StarRocks',
    value: 'STAR_ROCKS'
  },
  {
    label: 'Sybase',
    value: 'SYBASE'
  },
  {
    label: 'TDengine',
    value: 'T_DENGINE'
  },
  {
    label: 'TiDB',
    value: 'TIDB'
  },
  {
    label: 'Trino',
    value: 'TRINO'
  }
]
export const colConfigs: colConfig[] = [
  {
    prop: 'name',
    title: '名称',
    minWidth: 100,
    showOverflowTooltip: true
  },
  {
    prop: 'dbType',
    title: '类型',
    minWidth: 60,
    showOverflowTooltip: true
  },
  {
    prop: 'jdbcUrl',
    title: '连接信息',
    minWidth: 180,
    showOverflowTooltip: true
  },
  {
    prop: 'username',
    title: '用户名',
    minWidth: 80,
    showOverflowTooltip: true
  },
  {
    prop: 'status',
    title: '状态',
    minWidth: 80,
    customSlot: 'statusTag'
  },
  {
    prop: 'checkDateTime',
    title: '检测时间',
    minWidth: 140
  },
  {
    prop: 'remark',
    title: '备注',
    minWidth: 140,
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
