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
        name: '采集实例',
        code: 'acquisition-instance'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'id',
        title: '实例ID',
        minWidth: 145,
        showOverflowTooltip: true
    },
    {
        prop: 'metaWorkName',
        title: '任务',
        showOverflowTooltip: true,
        minWidth: 180
    },
    {
        prop: 'triggerType',
        title: '类型',
        minWidth: 100,
        formatter: (data: any) => {
            const obj = {
                FAST_TRIGGER: '立即采集',
                AUTO_TRIGGER: '自动采集'
            }
            return obj[data.cellValue]
        }
    },
    {
        prop: 'status',
        title: '状态',
        minWidth: 100,
        customSlot: 'statusTag'
    },
    {
        prop: 'startDateTime',
        title: '开始时间',
        minWidth: 140
    },
    {
        prop: 'endDateTime',
        title: '结束时间',
        minWidth: 140
    },
    {
        prop: 'duration',
        title: '耗时（秒）',
        minWidth: 100
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
