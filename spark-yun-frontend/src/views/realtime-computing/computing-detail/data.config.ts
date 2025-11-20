export const DataSourceType = [
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
export const CurrentSourceType = [
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
export const OverModeList = [
  {
    label: '追加模式',
    value: 'INTO',
  },
  // {
  //   label: '覆写模式',
  //   value: 'OVERWRITE',
  // }
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