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

