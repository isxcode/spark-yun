/*
 * @Author: fanciNate
 * @Date: 2023-06-04 22:27:08
 * @LastEditTime: 2023-06-16 22:20:14
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/src/views/user-center/user-center.config.ts
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
  fixed?: string
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
  pagination: Pagination; // 分页数据
  loading?: boolean; // 表格loading
}

export const BreadCrumbList: Array<BreadCrumb> = [
  {
    name: '用户中心',
    code: 'user-center'
  }
]

export const colConfigs: colConfig[] = []

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
