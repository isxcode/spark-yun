export const TypeList = [
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

export const ConfigRules = {
    dbType: [
        {
            required: true,
            message: '请选择数据源类型',
            trigger: ['blur', 'change']
        }
    ],
    datasourceId: [
        {
            required: true,
            message: '请选择数据源',
            trigger: ['blur', 'change']
        }
    ],
    tableName: [
        {
            required: true,
            message: '请选择表',
            trigger: ['blur', 'change']
        }
    ],
    writeMode: [
        {
            required: true,
            message: '请选择写入模式',
            trigger: ['blur', 'change']
        }
    ]
}

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
export interface TableConfig {
    tableData: Array<any>;
    colConfigs: Array<colConfig>;
    seqType: string;
    loading?: boolean; // 表格loading
}
export const TableConfig: TableConfig = {
    tableData: [],
    colConfigs: [
        {
            prop: 'colName',
            title: '字段名',
            minWidth: 120,
            showOverflowTooltip: true
        },
        {
            prop: 'colType',
            title: '类型',
            minWidth: 80,
            showOverflowTooltip: true
        },
        {
            prop: 'remark',
            title: '备注',
            minWidth: 100,
            showOverflowTooltip: true
        },
        // {
        //     title: '操作',
        //     align: 'center',
        //     customSlot: 'options',
        //     width: 80,
        //     fixed: 'right'
        // }
    ],
    seqType: 'seq',
    loading: false
}