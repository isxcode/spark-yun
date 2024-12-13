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
    name: '数据大屏',
    code: 'report-views'
  }
]

export const colConfigs: colConfig[] = [
  {
    prop: 'name',
    title: '名称',
    minWidth: 100,
    // customSlot: 'nameSlot',
    showOverflowTooltip: true
  },
  {
    prop: 'status',
    title: '状态',
    minWidth: 100,
    customSlot: 'statusTag'
  },
  {
    prop: 'createUsername',
    title: '创建人',
    minWidth: 80,
    showOverflowTooltip: true,
  },
  {
    prop: 'createDateTime',
    title: '创建时间',
    minWidth: 140,
    showOverflowTooltip: true,
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
