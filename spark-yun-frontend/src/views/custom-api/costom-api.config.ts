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

export interface SerchParams {
    page: number;
    pageSize: number;
    searchKeyWord: string;
}

export const BreadCrumbList: Array<BreadCrumb> = [
    {
        name: '自定义接口',
        code: 'custom-api'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '接口名称',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
      prop: 'apiType',
      title: '请求方式',
      minWidth: 100,
      showOverflowTooltip: true
    },
    {
      prop: 'path',
      title: '访问地址',
      minWidth: 230,
      showOverflowTooltip: true
    },
    {
        prop: 'status',
        title: '状态',
        minWidth: 100,
        customSlot: 'statusTag'
    },
    // {
    //     prop: 'defaultDriver',
    //     title: '创建人',
    //     minWidth: 120,
    //     showOverflowTooltip: true,
    // },
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
        width: 80
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
