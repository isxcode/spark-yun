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
        name: '消息体',
        code: 'message-notifications'
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
        prop: 'msgType',
        title: '类型',
        minWidth: 80,
        formatter: (data: any) => {
          const obj = {
            ALI_SMS: '阿里短信',
            EMAIL: '邮箱'
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
        prop: 'createByUsername',
        title: '创建人',
        minWidth: 140
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

export const RegionList = [
    {
        "regionId": "cn-zhangjiakou",
        "regionName": "华北3（张家口）",
        "areaId": "asiaPacific",
        "areaName": "亚太",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-wulanchabu",
        "regionName": "华北6（乌兰察布）",
        "areaId": "asiaPacific",
        "areaName": "亚太",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-shenzhen-finance-1",
        "regionName": "华南1 金融云",
        "areaId": "industryCloud",
        "areaName": "行业云",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-shenzhen",
        "regionName": "华南1（深圳）",
        "areaId": "asiaPacific",
        "areaName": "亚太",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-shanghai-finance-1",
        "regionName": "华东2 金融云",
        "areaId": "industryCloud",
        "areaName": "行业云",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-shanghai",
        "regionName": "华东2（上海）",
        "areaId": "asiaPacific",
        "areaName": "亚太",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-huhehaote",
        "regionName": "华北5（呼和浩特）",
        "areaId": "asiaPacific",
        "areaName": "亚太",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-hongkong",
        "regionName": "中国香港",
        "areaId": "asiaPacific",
        "areaName": "亚太",
        "public": "dysmsapi.aliyuncs.com",
        "vpc": "dysmsapi-xman.vpc-proxy.aliyuncs.com"
    },
    {
        "regionId": "cn-hangzhou-finance",
        "regionName": "华东1 金融云",
        "areaId": "industryCloud",
        "areaName": "行业云",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-hangzhou",
        "regionName": "华东1（杭州）",
        "areaId": "asiaPacific",
        "areaName": "亚太",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-chengdu",
        "regionName": "西南1（成都）",
        "areaId": "asiaPacific",
        "areaName": "亚太",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-beijing-finance-1",
        "regionName": "华北2 金融云（邀测）",
        "areaId": "industryCloud",
        "areaName": "行业云",
        "public": "dysmsapi.aliyuncs.com"
    },
    {
        "regionId": "cn-beijing",
        "regionName": "华北2（北京）",
        "areaId": "asiaPacific",
        "areaName": "亚太",
        "public": "dysmsapi.aliyuncs.com"
    }
]
