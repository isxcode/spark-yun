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
        name: '数据分层',
        code: 'data-layer'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '名称',
        minWidth: 125,
        // dragSort: true,
        customSlot: 'nameSlot'
    },
    {
        prop: 'tableRule',
        title: '表名规范',
        minWidth: 125
    },
    {
        prop: 'parentNameList',
        title: '父级分层',
        minWidth: 140,
        customSlot: 'parentNameSlot',
        formatter: (data: any) => {
            return data.cellValue ?? '-'
        }
    },
    {
        prop: 'remark',
        title: '备注',
        minWidth: 120
    },
    {
        prop: 'createByUsername',
        title: '创建人',
        minWidth: 120,
        showOverflowTooltip: 'tooltip'
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
