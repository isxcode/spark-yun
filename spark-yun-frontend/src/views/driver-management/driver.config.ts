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

export const BreadCrumbList: Array<BreadCrumb> = [
    {
        name: '驱动上传',
        code: 'driver-management'
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
      minWidth: 100,
      showOverflowTooltip: true
    },
    {
      prop: 'fileName',
      title: '驱动文件',
      minWidth: 120,
      showOverflowTooltip: true
    },
    {
        prop: 'defaultDriver',
        title: '默认',
        minWidth: 80,
        showOverflowTooltip: true,
        customSlot: 'defaultTag'
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
        minWidth: 100,
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
