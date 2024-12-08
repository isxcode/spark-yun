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
        name: '基线配置',
        code: 'warning-config'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '名称',
        minWidth: 125,
        showOverflowTooltip: true
    },
    {
        prop: 'alarmType',
        title: '类型',
        minWidth: 80,
        formatter: (data: any) => {
            const obj = {
                WORK: '作业',
                WORKFLOW: '作业流'
            }
            return obj[data.cellValue]
        }
    },
    {
        prop: 'alarmEvent',
        title: '告警事件',
        minWidth: 80,
        formatter: (data: any) => {
            const obj = {
                START_RUN: '开始运行',
                RUN_END: '运行结束',
                RUN_SUCCESS: '运行成功',
                RUN_FAIL: '运行失败'
            }
            return obj[data.cellValue]
        }
    },
    {
        prop: 'msgName',
        title: '消息体',
        minWidth: 125,
        showOverflowTooltip: true
    },
    {
        prop: 'receiverUsers',
        title: '通知人',
        showOverflowTooltip: true,
        minWidth: 140,
        formatter: (data: any) => {
            if (data.cellValue && data.cellValue.length > 0) {
                return data.cellValue.map((item: any) => item.username).join('，')
            } else {
                return ''
            }
        }
    },
    {
        prop: 'status',
        title: '状态',
        minWidth: 100,
        customSlot: 'statusTag'
    },
    {
        prop: 'createByUsername',
        title: '创建人',
        minWidth: 100
    },
    {
        prop: 'createDateTime',
        title: '创建时间',
        minWidth: 140
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
