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
    { label: '等于(=)', value: '=' },
    { label: '不等于(!=)', value: '!=' },
    { label: '大于(>)', value: '>' },
    { label: '大于等于(>=)', value: '>=' },
    { label: '小于(<)', value: '<' },
    { label: '小于等于(<=)', value: '<=' },
    { label: '包含(LIKE)', value: 'LIKE' },
    { label: '不包含(NOT LIKE)', value: 'NOT LIKE' },
    { label: '为空(IS NULL)', value: 'IS NULL' },
    { label: '不为空(IS NOT NULL)', value: 'IS NOT NULL' },
    { label: '在范围内(IN)', value: 'IN' },
    { label: '不在范围内(NOT IN)', value: 'NOT IN' }
]

// 分组背景色调色板，从橙黄开始
export const groupColorPalette = [
    { border: '#ff9800', background: '#fffaf2', borderOuter: '#ffd699' },
    { border: '#409eff', background: '#f9fbff', borderOuter: '#d9ecff' },
    { border: '#67c23a', background: '#f9fff5', borderOuter: '#c2e7b0' },
    { border: '#9b59b6', background: '#faf5ff', borderOuter: '#d9b3e8' },
    { border: '#e91e63', background: '#fff5f8', borderOuter: '#f4a0bd' },
    { border: '#00bcd4', background: '#f5fdfe', borderOuter: '#a0e5ed' },
    { border: '#e6a23c', background: '#fffcf5', borderOuter: '#f5dab1' },
    { border: '#607d8b', background: '#f7f9fa', borderOuter: '#b0bec5' },
]

