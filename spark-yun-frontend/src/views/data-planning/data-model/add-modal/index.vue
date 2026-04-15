<template>
    <BlockModal :model-config="modelConfig">
        <el-form
            ref="form"
            class="add-computer-group acquisition-task-add"
            label-position="top"
            :model="formData"
            :rules="rules"
        >
            <el-form-item label="名称" prop="name">
                <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="数据分层" prop="layerId">
                <el-select
                    v-model="formData.layerId"
                    filterable
                    clearable
                    @change="layerChangeEvent"
                    placeholder="请选择"
                >
                    <el-option
                        v-for="item in parentLayerIdList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="模型类型" prop="modelType">
                <el-select
                    v-model="formData.modelType"
                    filterable
                    :disabled="!!formData.id"
                    clearable
                    @change="modelTypeChangeEvent"
                    placeholder="请选择"
                >
                    <el-option
                        v-for="item in modelTypeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="数据源类型" prop="dbType">
                <el-select
                    v-model="formData.dbType"
                    placeholder="请选择"
                    filterable
                    clearable
                    @change="dbTypeChangeEvent"
                >
                    <el-option
                        v-for="item in dbTypeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="数据源" prop="datasourceId">
                <el-select
                    v-model="formData.datasourceId"
                    placeholder="请选择"
                    filterable
                    clearable
                    @change="datasouceChangeEvent"
                    @visible-change="getDataSourceList"
                >
                    <el-option
                        v-for="item in dataSourceList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="表名" prop="tableName" :show-message="false">
                <el-select
                    v-if="formData.modelType === 'LINK_MODEL'"
                    v-model="formData.tableName"
                    placeholder="请选择"
                    filterable
                    clearable
                    @visible-change="getDataSourceTable"
                >
                    <el-option
                        v-for="item in tableNameList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
                <el-input
                    v-else-if="showPrefixRuleInput"
                    v-model="formData.tableNameInput"
                    maxlength="500"
                    :placeholder="tableNamePlaceholder"
                >
                    <template #prepend>{{ currentLayerRule.input }}</template>
                </el-input>
                <el-input
                    v-else-if="showSuffixRuleInput"
                    v-model="formData.tableNameInput"
                    maxlength="500"
                    :placeholder="tableNamePlaceholder"
                >
                    <template #append>{{ currentLayerRule.input }}</template>
                </el-input>
                <el-input
                    v-else
                    v-model="formData.tableName"
                    :disabled="showExactRuleTip"
                    maxlength="500"
                    :placeholder="tableNamePlaceholder"
                />
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="formData.remark" type="textarea" maxlength="200"
                    :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" />
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, computed, watch } from 'vue'
import { GetDataLayerList } from '@/services/data-layer.service'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetDataSourceTables } from '@/services/data-sync.service'
import { useRoute } from 'vue-router'

interface Option {
    label: string
    value: string
    tableRule?: string
}

type TableRuleMode = 'none' | 'prefix' | 'suffix' | 'contains' | 'exact' | 'regex'

const route = useRoute()

const form = ref<FormInstance>()
const callback = ref<any>()
const parentLayerIdList = ref<Option[]>([])
const modelTypeList = ref<Option[]>([
    {
        label: '原始模型',
        value: 'ORIGIN_MODEL'
    },
    {
        label: '关联模型',
        value: 'LINK_MODEL'
    }
])
const dbTypeList = ref<Option[]>([
  {
    label: 'Clickhouse',
    value: 'CLICKHOUSE'
  },
  {
    label: 'Db2',
    value: 'DB2'
  },
  {
    label: 'Doris',
    value: 'DORIS'
  },
  {
    label: 'DuckDB',
    value: 'DUCK_DB'
  },
  {
    label: '达梦',
    value: 'DM'
  },
  {
    label: 'Gauss',
    value: 'GAUSS'
  },
  {
    label: 'Gbase',
    value: 'GBASE'
  },
  {
    label: 'Greenplum',
    value: 'GREENPLUM'
  },
  {
    label: 'H2',
    value: 'H2'
  },
  {
    label: 'HanaSap',
    value: 'HANA_SAP'
  },
  {
    label: 'Hive',
    value: 'HIVE'
  },
  {
    label: 'Impala',
    value: 'IMPALA'
  },
  {
    label: 'Mysql',
    value: 'MYSQL'
  },
  {
    label: 'OceanBase',
    value: 'OCEANBASE'
  },
  {
    label: 'OpenGauss',
    value: 'OPEN_GAUSS'
  },
  {
    label: 'Oracle',
    value: 'ORACLE'
  },
  {
    label: 'PostgreSql',
    value: 'POSTGRE_SQL'
  },
  {
    label: 'Presto',
    value: 'PRESTO'
  },
  {
    label: 'SelectDB',
    value: 'SELECT_DB'
  },
  {
    label: 'SqlServer',
    value: 'SQL_SERVER'
  },
  {
    label: 'StarRocks',
    value: 'STAR_ROCKS'
  },
  {
    label: 'Sybase',
    value: 'SYBASE'
  },
  {
    label: 'TDengine',
    value: 'T_DENGINE'
  },
  {
    label: 'TiDB',
    value: 'TIDB'
  },
  {
    label: 'Trino',
    value: 'TRINO'
  }
])
const dataSourceList = ref<Option[]>([])
const tableNameList = ref<Option[]>([])

const modelConfig = reactive({
    title: '添加',
    visible: false,
    width: '520px',
    okConfig: {
        title: '确定',
        ok: okEvent,
        disabled: false,
        loading: false
    },
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})
const formData = reactive<any>({
    name: '',
    layerId: '',
    modelType: '',  // 模型类型 ORIGIN_MODEL | LINK_MODEL
    dbType: '',     // 数据源类型
    datasourceId: '',
    tableName: '',
    tableNameInput: '',
    tableConfig: {}, // 高级配置
    remark: '',
    id: ''
})
const currentLayerRule = reactive<{ mode: TableRuleMode; input: string; raw: string }>({
    mode: 'none',
    input: '',
    raw: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入采集任务名称', trigger: ['blur', 'change'] }],
    layerId: [{ required: true, message: '请选择数据分层', trigger: ['blur', 'change'] }],
    modelType: [{ required: true, message: '请选择模型类型', trigger: ['blur', 'change'] }],
    dbType: [{ required: true, message: '请选择数据源类型', trigger: ['blur', 'change'] }],
    datasourceId: [{ required: true, message: '请选择数据源', trigger: ['blur', 'change'] }],
    tableName: [
        { required: true, message: '请选择表名', trigger: ['blur', 'change'] },
        {
            validator: (_rule, value, callback) => {
                if (formData.modelType !== 'ORIGIN_MODEL') {
                    callback()
                    return
                }
                if ((currentLayerRule.mode === 'prefix' || currentLayerRule.mode === 'suffix') && !formData.tableNameInput?.trim()) {
                    callback(new Error('请输入表名可变部分'))
                    return
                }
                if (!value?.trim()) {
                    callback()
                    return
                }

                if (currentLayerRule.mode === 'contains' && currentLayerRule.input && !value.includes(currentLayerRule.input)) {
                    callback(new Error(`表名需包含：${currentLayerRule.input}`))
                    return
                }
                if (currentLayerRule.mode === 'exact' && currentLayerRule.input && value !== currentLayerRule.input) {
                    callback(new Error(`表名需为：${currentLayerRule.input}`))
                    return
                }
                if (currentLayerRule.mode === 'regex' && currentLayerRule.raw) {
                    try {
                        const regex = new RegExp(currentLayerRule.raw)
                        if (!regex.test(value)) {
                            callback(new Error('表名不符合分层规范'))
                            return
                        }
                    } catch (error) {
                        // 分层规则非法时不阻塞模型创建
                    }
                }
                callback()
            },
            trigger: ['blur', 'change']
        }
    ]
})

const showPrefixRuleInput = computed(() => {
    return formData.modelType === 'ORIGIN_MODEL' && !formData.id && currentLayerRule.mode === 'prefix'
})

const showSuffixRuleInput = computed(() => {
    return formData.modelType === 'ORIGIN_MODEL' && !formData.id && currentLayerRule.mode === 'suffix'
})

const showContainsRuleTip = computed(() => {
    return formData.modelType === 'ORIGIN_MODEL' && !formData.id && currentLayerRule.mode === 'contains'
})

const showExactRuleTip = computed(() => {
    return formData.modelType === 'ORIGIN_MODEL' && !formData.id && currentLayerRule.mode === 'exact'
})

const tableNamePlaceholder = computed(() => {
    if (showPrefixRuleInput.value || showSuffixRuleInput.value) {
        return '请输入表名可变部分'
    }
    if (showContainsRuleTip.value) {
        return `请输入完整表名（需包含 ${currentLayerRule.input}）`
    }
    if (showExactRuleTip.value) {
        return '表名已按分层规范自动填写'
    }
    if (currentLayerRule.mode === 'regex' && currentLayerRule.input) {
        return `请按表达式填写：${currentLayerRule.input}`
    }
    return '请输入'
})

watch(() => formData.tableNameInput, () => {
    tableNameInputChange()
})

function showModal(cb: () => void, data: any): void {
    if (data) {
        Object.keys(formData).forEach((key: string) => {
            formData[key] = data[key]
        })
        modelConfig.title = '编辑'
    } else {
        Object.keys(formData).forEach((key: string) => {
            formData[key] = ''
            if (key === 'layerId' && route.query.id) {
                formData[key] = route.query.id
            }
        })
        formData.modelType = 'ORIGIN_MODEL'
        modelConfig.title = '添加'
    }

    getParentLayerIList()
    getDataSourceList(true)
    getDataSourceTable(true)

    callback.value = cb
    modelConfig.visible = true
}

function okEvent() {
    form.value?.validate((valid: boolean) => {
        if (valid) {
            modelConfig.okConfig.loading = true
            callback.value(formData).then((res: any) => {
                modelConfig.okConfig.loading = false
                if (res === undefined) {
                    modelConfig.visible = false
                } else {
                    modelConfig.visible = true
                }
            }).catch((err: any) => {
                modelConfig.okConfig.loading = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function getParentLayerIList() {
    GetDataLayerList({
        page: 0,
        pageSize: 10000,
        searchKeyWord: ''
    }).then((res: any) => {
        parentLayerIdList.value = [...res.data.content.map((item: any) => {
            return {
                label: item.fullPathName,
                value: item.id,
                tableRule: item.tableRule
            }
        })]
        fillDefaultTableNameByLayerRule(false)
    }).catch(() => {
        parentLayerIdList.value = []
    })
}

function closeEvent() {
    modelConfig.visible = false
}

function dbTypeChangeEvent() {
    formData.datasourceId = ''
    datasouceChangeEvent()
}

function datasouceChangeEvent() {
    if (formData.modelType === 'LINK_MODEL') {
        formData.tableName = ''
    }
}

function modelTypeChangeEvent(value: string) {
    formData.tableNameInput = ''
    if (value !== 'ORIGIN_MODEL') {
        formData.tableName = ''
        currentLayerRule.mode = 'none'
        currentLayerRule.input = ''
        currentLayerRule.raw = ''
        return
    }
    fillDefaultTableNameByLayerRule(true, true)
}

function layerChangeEvent() {
    fillDefaultTableNameByLayerRule(true, true)
}

function fillDefaultTableNameByLayerRule(force: boolean, resetInput?: boolean) {
    if (formData.id || formData.modelType !== 'ORIGIN_MODEL') {
        return
    }

    const currentLayer = parentLayerIdList.value.find((item: Option) => item.value === formData.layerId)
    const tableRule = currentLayer?.tableRule?.trim()
    const tableRuleInfo = parseTableRule(tableRule || '')
    currentLayerRule.mode = tableRuleInfo.mode
    currentLayerRule.input = tableRuleInfo.input
    currentLayerRule.raw = tableRule || ''

    if (!tableRule) {
        if (resetInput) {
            formData.tableNameInput = ''
            formData.tableName = ''
        }
        return
    }

    if (resetInput) {
        formData.tableNameInput = ''
    } else if (force || !formData.tableNameInput) {
        formData.tableNameInput = parseTableNameInputByRule(formData.tableName, tableRuleInfo)
    }

    if (force || !formData.tableName) {
        formData.tableName = buildTableNameByRule(formData.tableNameInput, tableRuleInfo)
    }
    if (tableRuleInfo.mode === 'prefix' || tableRuleInfo.mode === 'suffix') {
        formData.tableName = buildTableNameByRule(formData.tableNameInput, tableRuleInfo)
    }
    if (tableRuleInfo.mode === 'exact') {
        formData.tableName = tableRuleInfo.input
    }
    form.value?.validateField('tableName')
}

function parseTableRule(value: string): { mode: TableRuleMode; input: string } {
    if (!value) {
        return { mode: 'none', input: '' }
    }

    const prefixMatch = value.match(/^\^(.+)\.\*$/)
    if (prefixMatch?.[1]) {
        return { mode: 'prefix', input: unEscapeRegexChar(prefixMatch[1]) }
    }

    const suffixMatch = value.match(/^\.\*(.+)\$$/)
    if (suffixMatch?.[1]) {
        return { mode: 'suffix', input: unEscapeRegexChar(suffixMatch[1]) }
    }

    const containsMatch = value.match(/^\.\*(.+)\.\*$/)
    if (containsMatch?.[1]) {
        return { mode: 'contains', input: unEscapeRegexChar(containsMatch[1]) }
    }

    const exactMatch = value.match(/^\^(.+)\$$/)
    if (exactMatch?.[1]) {
        return { mode: 'exact', input: unEscapeRegexChar(exactMatch[1]) }
    }

    return { mode: 'regex', input: value }
}

function unEscapeRegexChar(value: string) {
    return value.replace(/\\([.*+?^${}()|[\]\\])/g, '$1')
}

function buildTableNameByRule(inputValue: string, ruleInfo: { mode: TableRuleMode; input: string }) {
    const safeInput = (inputValue || '').trim()
    if (ruleInfo.mode === 'prefix') {
        return `${ruleInfo.input}${safeInput}`
    }
    if (ruleInfo.mode === 'suffix') {
        return `${safeInput}${ruleInfo.input}`
    }
    if (ruleInfo.mode === 'exact') {
        return ruleInfo.input
    }
    return safeInput
}

function parseTableNameInputByRule(tableName: string, ruleInfo: { mode: TableRuleMode; input: string }) {
    const currentTableName = (tableName || '').trim()
    if (!currentTableName) {
        return ''
    }
    if (ruleInfo.mode === 'prefix' && currentTableName.startsWith(ruleInfo.input)) {
        return currentTableName.slice(ruleInfo.input.length)
    }
    if (ruleInfo.mode === 'suffix' && currentTableName.endsWith(ruleInfo.input)) {
        return currentTableName.slice(0, currentTableName.length - ruleInfo.input.length)
    }
    return currentTableName
}

function tableNameInputChange() {
    if (formData.modelType !== 'ORIGIN_MODEL' || formData.id) {
        return
    }
    if (currentLayerRule.mode === 'prefix' || currentLayerRule.mode === 'suffix') {
        formData.tableName = buildTableNameByRule(formData.tableNameInput, currentLayerRule)
    } else if (currentLayerRule.mode === 'exact') {
        formData.tableName = currentLayerRule.input
    } else {
        formData.tableName = formData.tableNameInput
    }
    form.value?.validateField('tableName')
}

function getDataSourceList(e: boolean, searchType?: string) {
    if (e && formData.dbType) {
        GetDatasourceList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: searchType || '',
            datasourceType: formData.dbType
        }).then((res: any) => {
            dataSourceList.value = res.data.content.map((item: any) => {
                return {
                    label: item.name,
                    value: item.id
                }
            })
        }).catch(() => {
            dataSourceList.value = []
        })
    } else {
        dataSourceList.value = []
    }
}

function getDataSourceTable(e: boolean) {
    if (e && formData.datasourceId) {
        GetDataSourceTables({
            dataSourceId: formData.datasourceId,
            tablePattern: ""
        }).then((res: any) => {
            tableNameList.value = res.data.tables.map((item: any) => {
                return {
                    label: item,
                    value: item
                }
            })
        }).catch(() => {
            tableNameList.value = []
        })
    } else {
        tableNameList.value = []
    }
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.acquisition-task-add {
    .el-form-item {
        .el-form-item__content {
            position: relative;
            flex-wrap: nowrap;
            justify-content: space-between;

            .time-num-input {
                height: 36px;

                .el-input-number__decrease {
                    top: 16px
                }
            }
        }
    }

    .cron-config {
        border: 1px solid getCssVar('border-color');
        padding: 8px 12px;
        margin-bottom: 12px;
        border-radius: 5px;
    }

    .table-rule-tip {
        width: 100%;
        font-size: 12px;
        line-height: 1.4;
        color: getCssVar('text-color', 'secondary');
        margin-top: 6px;
    }
}
</style>
