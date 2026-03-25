export interface colConfig {
    prop?: string;
    title: string;
    align?: string;
    showOverflowTooltip?: boolean;
    customSlot?: string;
    width?: number;
    minWidth?: number;
    fixed?: string;
  }
export interface TableConfig {
    tableData: Array<any>;
    colConfigs: Array<colConfig>;
    seqType: string;
    loading?: boolean;
}
export const TableConfig: TableConfig = {
    tableData: [],
    colConfigs: [
        {
            prop: 'colName',
            title: '字段名',
            minWidth: 120,
            showOverflowTooltip: true
        },
        {
            prop: 'fromAliaCode',
            title: '来源',
            minWidth: 100,
            showOverflowTooltip: true
        },
        {
            prop: 'colType',
            title: '类型',
            minWidth: 80,
            showOverflowTooltip: true
        },
        {
            prop: 'remark',
            title: '备注',
            minWidth: 100,
            showOverflowTooltip: true
        }
    ],
    seqType: 'seq',
    loading: false
}

export const FilterConditionOptions = [
    { label: '等于(=)', value: 'equal' },
    { label: '不等于(!=)', value: 'not_equal' },
    { label: '大于(>)', value: 'greater' },
    { label: '大于等于(>=)', value: 'greater_equal' },
    { label: '小于(<)', value: 'less' },
    { label: '小于等于(<=)', value: 'less_equal' },
    { label: '包含(LIKE)', value: 'like' },
    { label: '不包含(NOT LIKE)', value: 'not_like' },
    { label: '为空(IS NULL)', value: 'is_null' },
    { label: '不为空(IS NOT NULL)', value: 'is_not_null' },
    { label: '在范围内(IN)', value: 'in' },
    { label: '不在范围内(NOT IN)', value: 'not_in' }
]

// 分组背景色调色板
export const groupColorPalette = [
    { border: '#67c23a', background: '#f9fff5', borderOuter: '#c2e7b0' },
    { border: '#9b59b6', background: '#faf5ff', borderOuter: '#d9b3e8' },
    { border: '#e91e63', background: '#fff5f8', borderOuter: '#f4a0bd' },
    { border: '#00bcd4', background: '#f5fdfe', borderOuter: '#a0e5ed' },
    { border: '#607d8b', background: '#f7f9fa', borderOuter: '#b0bec5' },
]

