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
        name: '字段标准',
        code: 'field-format'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '名称',
        minWidth: 125,
        customSlot: 'nameSlot',
        showOverflowTooltip: true
    },
    {
        prop: 'columnTypeCode',
        title: '字段类型',
        minWidth: 125,
        showOverflowTooltip: true
    },
    {
        prop: 'columnType',
        title: '字段精度',
        minWidth: 100,
        formatter: (data: any) => {
            return data.cellValue ?? '-'
        },
        showOverflowTooltip: true
    },
    {
        prop: 'columnRule',
        title: '字段名规范',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'status',
        title: '状态',
        customSlot: 'statusTag',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'remark',
        title: '备注',
        minWidth: 120
    },
    {
        prop: 'createUsername',
        title: '创建人',
        minWidth: 120
    },
    {
        prop: 'createDateTime',
        title: '创建时间',
        minWidth: 140
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
    pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
    },
    seqType: 'seq',
    loading: false
}
