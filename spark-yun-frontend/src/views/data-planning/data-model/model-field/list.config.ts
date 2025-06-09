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
    fixed?: string;
    dragSort?: boolean
    algin?: string
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
        name: '数据模型',
        code: 'data-model'
    },
    {
        name: '模型字段',
        code: 'model-field'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '字段',
        minWidth: 175,
        dragSort: true,
        showOverflowTooltip: true
    },
    {
        prop: 'columnName',
        title: '字段名',
        minWidth: 170,
        showOverflowTooltip: true
    },
    {
        prop: 'columnFormatName',
        title: '字段标准',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'columnType',
        title: '字段精度',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'isNull',
        title: '可为空',
        minWidth: 100,
        customSlot: 'booleanTag',
        showOverflowTooltip: true,
        align: 'center'
    },
    {
        prop: 'isDuplicate',
        title: '可重复',
        minWidth: 100,
        customSlot: 'booleanTag',
        showOverflowTooltip: true,
        align: 'center'
    },
    {
        prop: 'isPrimary',
        title: '是否主键',
        minWidth: 100,
        customSlot: 'booleanTag',
        showOverflowTooltip: true,
        align: 'center'
    },
    {
        prop: 'isPartition',
        title: '是否分区键',
        minWidth: 100,
        customSlot: 'booleanTag',
        showOverflowTooltip: true,
        align: 'center'
    },
    {
        prop: 'defaultValue',
        title: '默认值',
        minWidth: 120
    },
    {
        prop: 'remark',
        title: '备注',
        minWidth: 170
    },
    {
        title: '操作',
        align: 'center',
        customSlot: 'options',
        width: 90,
        fixed: 'right'
    }
]

export const TableConfig: TableConfig = {
    tableData: [],
    colConfigs: colConfigs,
    // pagination: {
    //     currentPage: 1,
    //     pageSize: 10,
    //     total: 0
    // },
    seqType: 'seq',
    loading: false
}
