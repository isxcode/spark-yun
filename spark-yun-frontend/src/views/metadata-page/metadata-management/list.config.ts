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
        name: '元数据管理',
        code: 'metadata-management'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'dbName',
        title: 'dbName',
        minWidth: 125,
        showOverflowTooltip: true
    },
    {
        prop: 'dbType',
        title: '数据源类型',
        minWidth: 125,
        showOverflowTooltip: true
    },
    {
        prop: 'datasourceId',
        title: '数据源名称',
        minWidth: 125,
        showOverflowTooltip: true
    },
    {
        prop: 'lastModifiedDateTime',
        title: '更新时间',
        minWidth: 140
    },
    // {
    //     title: '操作',
    //     align: 'center',
    //     customSlot: 'options',
    //     width: 120
    // }
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
