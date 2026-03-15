export const TaskParams = {
    // 数据输入
    'DATA_INPUT': {
        inputEtl: {
            datasourceId: '',
            dbType: '',
            tableName: '',
            numPartitions: null,
            partitionColumn: ''
        },
        outColumnList: []
    },
    // 数据输出
    'DATA_OUTPUT': {
        outputEtl: {
            datasourceId: '',
            dbType: '',
            tableName: '',
            writeMode: '',
            partitionColumn: ''
        },
        fromColumnList: [],
        toColumnList: [],
        colMapping: []
    },
    // 数据转换
    'DATA_TRANSFORM': {
        transformEtl: [{
            colName: '',
            transformWay: 'FUNCTION_TRANSFORM',
            transformFunc: '',
            transformSql: '',
            inputValue: []
        }],
        outColumnList: []
    },
    // 数据关联
    'DATA_JOIN': {
        joinEtl: [{
            joinWay: 'LEFT_JOIN',
            joinAliaCode: '',
            joinConditions: [{
                joinType: 'COLUMN_JOIN',
                joinLeftColumn: '',
                joinCondition: '',
                joinRightColumn: '',
                joinValue: '',
                joinSql: ''
            }]
        }],
        outColumnList: [],
        mainAliaCode: ''
    },
    // 数据合并
    'DATA_UNION': {
        unionEtl: [{
            aliaCode: '',
            unionWay: 'UNION'
        }],
        outColumnList: [],
        mainAliaCode: ''
    },
    // 新增字段
    'DATA_ADD_COL': {
        outColumnList: []
    },
    // 数据自定义
    'DATA_CUSTOM': {
        sql: '',
        outColumnList: []
    }
}