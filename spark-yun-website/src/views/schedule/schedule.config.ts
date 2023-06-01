/*
 * @Author: fanciNate
 * @Date: 2023-05-23 20:10:12
 * @LastEditTime: 2023-05-23 20:41:47
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/views/schedule/schedule.config.ts
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
    name: "调度历史",
    code: "schedule",
  },
];
// 实例编码	作业	类型	状态	执行时间	结束时间	下次计划时间	操作
export const colConfigs: colConfig[] = [
  {
    prop: "id",
    title: "实例编码",
    minWidth: 158,
    showOverflowTooltip: true,
  },
  {
    prop: "workName",
    title: "作业",
    minWidth: 120,
    showOverflowTooltip: true,
  },
  {
    prop: "instanceType",
    title: "类型",
    minWidth: 110,
    customSlot: "instanceTypeTag",
  },
  {
    prop: "status",
    title: "状态",
    minWidth: 100,
    customSlot: "statusTag",
  },
  {
    prop: "execStartDateTime",
    title: "执行时间",
    minWidth: 140,
  },
  {
    prop: "execEndDateTime",
    title: "结束时间",
    minWidth: 140,
  },
  {
    prop: "nextPlanDateTime",
    title: "下次计划时间",
    minWidth: 140,
  },
  {
    title: "操作",
    align: "center",
    customSlot: "options",
    width: 80,
  },
];

export const TableConfig: TableConfig = {
  tableData: [],
  colConfigs: colConfigs,
  pagination: {
    currentPage: 1,
    pageSize: 10,
    total: 0,
  },
  seqType: "seq",
  loading: false,
};
