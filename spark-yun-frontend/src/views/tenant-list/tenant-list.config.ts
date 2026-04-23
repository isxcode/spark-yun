/*
 * @Author: fanciNate
 * @Date: 2023-05-18 10:52:00
 * @LastEditTime: 2026-01-14 22:33:07
 * @LastEditors: fancinate 1585546519@qq.com
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
    name: '租户管理',
    code: 'tenant-list'
  }
]

export const colConfigs: colConfig[] = [
  {
    prop: 'name',
    title: '名称',
    minWidth: 100,
    customSlot: 'name',
    showOverflowTooltip: true
  },
  {
    prop: 'maxMemberNum',
    title: '成员数',
    minWidth: 180,
    customSlot: 'memberProgress'
  },
  {
    prop: 'maxWorkflowNum',
    title: '作业流数',
    minWidth: 180,
    customSlot: 'workflowProgress'
  },
  {
    prop: 'status',
    title: '状态',
    minWidth: 100,
    customSlot: 'statusTag'
  },
  {
    prop: 'validStartDateTime',
    title: '有效开始时间',
    minWidth: 140,
    showOverflowTooltip: true
  },
  {
    prop: 'validEndDateTime',
    title: '有效结束时间',
    minWidth: 140,
    showOverflowTooltip: true
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
