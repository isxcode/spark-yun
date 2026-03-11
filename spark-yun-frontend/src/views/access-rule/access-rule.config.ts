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
    pagination?: Pagination;
    loading?: boolean;
}

export interface SerchParams {
    page: number;
    pageSize: number;
    searchKeyWord: string;
}

export const BreadCrumbList: Array<BreadCrumb> = [
    {
        name: '黑白名单',
        code: 'access-rule'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '名称',
        minWidth: 120,
        showOverflowTooltip: true
    },
    {
        prop: 'ruleType',
        title: '规则类型',
        minWidth: 100,
        customSlot: 'ruleTypeTag'
    },
    {
        prop: 'ipAddress',
        title: 'IP地址',
        minWidth: 150,
        showOverflowTooltip: true
    },
    {
        prop: 'createUsername',
        title: '创建人',
        minWidth: 80,
        showOverflowTooltip: true
    },
    {
        prop: 'createDateTime',
        title: '创建时间',
        minWidth: 140,
        showOverflowTooltip: true
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

