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

export interface SerchParams {
    page: number;
    pageSize: number;
    searchKeyWord: string;
}

export const BreadCrumbList: Array<BreadCrumb> = [
    {
        name: '数据卡片',
        code: 'report-components'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '名称',
        minWidth: 160,
        customSlot: 'nameSlot',
        showOverflowTooltip: true
    },
    {
        prop: 'type',
        title: '类型',
        minWidth: 100,
        customSlot: 'typeSlot',
        showOverflowTooltip: true
    },
    {
        prop: 'datasourceName',
        title: '数据源',
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
        prop: 'createUsername',
        title: '创建人',
        minWidth: 100,
        showOverflowTooltip: true,
    },
    {
        prop: 'createDateTime',
        title: '创建时间',
        minWidth: 140,
        showOverflowTooltip: true,
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
