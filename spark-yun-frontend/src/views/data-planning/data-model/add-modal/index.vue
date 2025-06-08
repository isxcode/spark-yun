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
                    :disabled="!!formData.id"
                    v-model="formData.layerId"
                    filterable
                    clearable
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
                    clearable
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
            <el-form-item label="表名" prop="tableName">
                <el-select
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
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="formData.remark" type="textarea" maxlength="200"
                    :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" />
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref } from 'vue'
import { GetDataLayerList } from '@/services/data-layer.service'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetDataSourceTables } from '@/services/data-sync.service'
import { useRoute } from 'vue-router'

interface Option {
    label: string
    value: string
}

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
        label: 'Mysql',
        value: 'MYSQL',
    },
    {
        label: 'Oracle',
        value: 'ORACLE',
    },
    {
        label: 'SqlServer',
        value: 'SQL_SERVER',
    },
    {
        label: 'PostgreSql',
        value: 'POSTGRE_SQL',
    },
    {
        label: 'Clickhouse',
        value: 'CLICKHOUSE',
    },
    {
        label: 'Hive',
        value: 'HIVE',
    },
    {
        label: 'H2',
        value: 'H2',
    },
    {
        label: 'HanaSap',
        value: 'HANA_SAP',
    },
    {
        label: '达梦',
        value: 'DM',
    },
    {
        label: 'Doris',
        value: 'DORIS',
    },
    {
        label: 'OceanBase',
        value: 'OCEANBASE',
    },
    {
        label: 'TiDB',
        value: 'TIDB',
    },
    {
        label: 'StarRocks',
        value: 'STAR_ROCKS',
    },
    {
        label: 'Greenplum',
        value: 'GREENPLUM',
    },
    {
        label: 'Gbase',
        value: 'GBASE',
    },
    {
        label: 'Sybase',
        value: 'SYBASE',
    },
    {
        label: 'Db2',
        value: 'DB2',
    },
    {
        label: 'Gauss',
        value: 'GAUSS',
    },
    {
        label: 'OpenGauss',
        value: 'OPEN_GAUSS',
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
    tableConfig: {}, // 高级配置
    remark: '',
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入采集任务名称', trigger: ['blur', 'change'] }],
    layerId: [{ required: true, message: '请选择数据分层', trigger: ['blur', 'change'] }],
    modelType: [{ required: true, message: '请选择模型类型', trigger: ['blur', 'change'] }],
    dbType: [{ required: true, message: '请选择数据源类型', trigger: ['blur', 'change'] }],
    datasourceId: [{ required: true, message: '请选择数据源', trigger: ['blur', 'change'] }],
    tableName: [{ required: true, message: '请选择表名', trigger: ['blur', 'change'] }]
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
                label: item.name,
                value: item.id
            }
        })]
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
    formData.tableName = ''
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
}
</style>