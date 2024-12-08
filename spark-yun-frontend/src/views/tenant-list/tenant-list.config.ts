/*
 * @Author: fanciNate
 * @Date: 2023-05-18 10:52:00
 * @LastEditTime: 2023-05-18 20:56:06
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/views/tenant-list/tenant-list.config.ts
 */
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
  formatter?: any;
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
    name: '租户列表',
    code: 'tenant-list'
  }
]

export const colConfigs: colConfig[] = [
  {
    prop: 'name',
    title: '租户名称',
    minWidth: 100,
    showOverflowTooltip: true
  },
  {
    prop: 'maxMemberNum',
    title: '成员',
    minWidth: 100,
    formatter: (data: any): string => {
      return `${data.row.usedMemberNum} / ${data.row.maxMemberNum}`
    }
  },
  {
    prop: 'maxWorkflowNum',
    title: '工作流',
    minWidth: 120,
    formatter: (data: any): string => {
      return `${data.row.usedWorkflowNum} / ${data.row.maxWorkflowNum}`
    }
  },
  {
    prop: 'status',
    title: '状态',
    minWidth: 100,
    customSlot: 'statusTag'
  },
  {
    prop: 'checkDateTime',
    title: '检测时间',
    minWidth: 140
  },
  {
    prop: 'remark',
    title: '备注',
    minWidth: 120,
    showOverflowTooltip: true
  },
  {
    title: '操作',
    align: 'center',
    customSlot: 'options',
    width: 140,
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
