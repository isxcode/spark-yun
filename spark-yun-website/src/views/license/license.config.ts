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
    name: "证书安装",
    code: "license",
  },
];

export const colConfigs: colConfig[] = [
  {
    prop: "code",
    title: "许可证编号",
    minWidth: 125,
    showOverflowTooltip: true,
  },
  {
    prop: "startDateTime",
    title: "创建时间",
    minWidth: 110,
  },
  {
    prop: "endDateTime",
    title: "到期时间",
    minWidth: 110,
  },
  {
    prop: "maxMemberNum",
    title: "成员",
    minWidth: 80,
  },
  {
    prop: "maxTenantNum",
    title: "租户",
    minWidth: 80,
  },
  {
    prop: "status",
    title: "状态",
    minWidth: 100,
    customSlot: "statusTag",
  },
  {
    prop: "remark",
    title: "备注",
    minWidth: 100,
    showOverflowTooltip: true,
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
