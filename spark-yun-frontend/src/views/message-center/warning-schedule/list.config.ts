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
        name: '告警实例',
        code: 'warning-schedule'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'alarmName',
        title: '告警名称',
        minWidth: 125,
        showOverflowTooltip: true
    },
    {
        prop: 'alarmType',
        title: '告警类型',
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
        prop: 'instanceId',
        title: '任务实例ID',
        minWidth: 125,
        showOverflowTooltip: true
    },
    {
        prop: 'msgName',
        title: '通知方式',
        minWidth: 120,
        showOverflowTooltip: true
    },
    {
        prop: 'receiverUsername',
        title: '通知人',
        showOverflowTooltip: true,
        minWidth: 80
    },
    {
        prop: 'sendDateTime',
        title: '通知时间',
        minWidth: 140
    },
    {
        prop: 'sendStatus',
        title: '通知状态',
        minWidth: 100,
        customSlot: 'statusTag'
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
