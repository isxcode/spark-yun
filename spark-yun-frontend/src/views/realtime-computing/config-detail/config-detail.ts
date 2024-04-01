export const ScheduleRange = [
    {
        label: '分钟',
        value: 'min',
    },
    {
        label: '小时',
        value: 'hour',
    },
    {
        label: '日',
        value: 'day',
    },
    {
        label: '月',
        value: 'month',
    },
    {
        label: '星期',
        value: 'week',
    }
]
export const WeekDateList = [
    {
        label: '星期一',
        value: '1',
    },
    {
        label: '星期二',
        value: '2',
    },
    {
        label: '星期三',
        value: '3',
    },
    {
        label: '星期四',
        value: '4',
    },
    {
        label: '星期五',
        value: '5',
    },
    {
        label: '星期六',
        value: '6',
    },
    {
        label: '星期日',
        value: '7',
    }
]
export const ResourceLevelOptions = [
    {
        label: '高',
        value: 'HIGH',
    },
    {
        label: '中',
        value: 'MEDIUM',
    },
    {
        label: '低',
        value: 'LOW',
    }
]
export const DataSourceRules = {
    datasourceId: [
        {
            required: true,
            message: '请选择数据源',
            trigger: ['blur', 'change']
        }
    ]
}
export const ClusterConfigRules = {
    clusterId: [
        {
            required: true,
            message: '请选择计算集群',
            trigger: ['blur', 'change']
        }
    ],
    clusterNodeId: [
        {
            required: true,
            message: '请选择集群节点',
            trigger: ['blur', 'change']
        }
    ],
    datasourceId: [
        {
            required: true,
            message: '请选择Hive数据源',
            trigger: ['blur', 'change']
        }
    ]
}
export const CronConfigRules = {
    workDate: [
        {
            required: true,
            message: '请选择生效时间',
            trigger: ['blur', 'change']
        }
    ],
    cron: [
        {
            required: true,
            message: '请输入cron表达式',
            trigger: ['blur', 'change']
        }
    ],
    range: [
        {
            required: true,
            message: '请选择调度周期',
            trigger: ['blur', 'change']
        }
    ],
    endDate: [
        {
            required: true,
            message: '请选择结束时间',
            trigger: ['blur', 'change']
        }
    ],
    startDate: [
        {
            required: true,
            message: '请选择开始时间',
            trigger: ['blur', 'change']
        }
    ],
    startDateMin: [
        {
            required: true,
            message: '请选择开始时间',
            trigger: ['blur', 'change']
        }
    ],
    endDateMin: [
        {
            required: true,
            message: '请选择结束时间',
            trigger: ['blur', 'change']
        }
    ],
    minNum: [
        {
            required: true,
            message: '请输入时间间隔',
            trigger: ['blur', 'change']
        }
    ],
    hourNum: [
        {
            required: true,
            message: '请输入时间间隔',
            trigger: ['blur', 'change']
        }
    ],
    scheduleDate: [
        {
            required: true,
            message: '请选择调度时间',
            trigger: ['blur', 'change']
        }
    ],
    monthDay: [
        {
            required: true,
            message: '请选择指定时间',
            trigger: ['blur', 'change']
        }
    ],
    weekDate: [
        {
            required: true,
            message: '请选择指定时间',
            trigger: ['blur', 'change']
        }
    ]
}
export const SyncRuleConfigRules = {
    // name: [
    //     {
    //         required: true,
    //         message: '请输入数据源名称',
    //         trigger: ['blur', 'change']
    //     }
    // ]
}
