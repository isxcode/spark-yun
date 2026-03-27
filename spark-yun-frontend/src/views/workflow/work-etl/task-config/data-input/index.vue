<template>
    <div class="config-components">
        <el-form-item label="类型" prop="inputEtl.dbType" :rules="rules.dbType">
            <el-select
                v-model="formData.inputEtl.dbType"
                filterable
                clearable
                placeholder="请选择"
                @change="changeEvent($event, 'dbType')"
            >
                <el-option
                    v-for="item in typeList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
        </el-form-item>
        <el-form-item label="数据源" prop="inputEtl.datasourceId" :rules="rules.datasourceId">
            <el-select
                v-model="formData.inputEtl.datasourceId"
                filterable
                clearable
                placeholder="请选择"
                @change="changeEvent($event, 'datasourceId')"
                @visible-change="getDataSource($event, formData.inputEtl.dbType)"
            >
                <el-option
                    v-for="item in dataSourceList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
        </el-form-item>
        <el-form-item label="表" prop="inputEtl.tableName" :rules="rules.tableName" class="table-select-row">
            <el-select
                v-model="formData.inputEtl.tableName"
                filterable
                clearable
                placeholder="请选择"
                @change="changeEvent($event, 'tableName')"
                @visible-change="getDataSourceTable($event, formData.inputEtl.datasourceId)"
            >
                <el-option
                    v-for="item in sourceTablesList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
            <el-button type="primary" link @click="showTableDetail">数据预览</el-button>
        </el-form-item>
        <el-form-item label="分区键" prop="inputEtl.partitionColumn" :rules="rules.partitionColumn">
            <el-select
                v-model="formData.inputEtl.partitionColumn"
                filterable
                clearable
                placeholder="请选择"
                @visible-change="getTableColumnData($event, formData.inputEtl.datasourceId, formData.inputEtl.tableName)"
            >
                <el-option
                    v-for="item in partKeyList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
        </el-form-item>
        <el-form-item label="分区数" prop="inputEtl.numPartitions">
            <el-input-number
                v-model="formData.inputEtl.numPartitions"
                placeholder="请输入"
                :min="0"
                controls-position="right"
            />
        </el-form-item>
        <!-- 数据预览 -->
        <table-detail ref="tableDetailRef"></table-detail>
        <div v-show="false" style="max-height: 444px;">
            <BlockTable
              :table-config="tableConfig"
            >
                <template #options="scopeSlot">
                    <div class="btn-group">
                        <span @click="editEvent(scopeSlot.row)">备注</span>
                    </div>
                </template>
            </BlockTable>
            <RemarkModal ref="remarkModalRef"></RemarkModal>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, defineEmits, computed, onMounted, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { TypeList, ConfigRules, TableConfig } from './config.ts'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetDataSourceTables, GetTableColumnsByTableId } from '@/services/data-sync.service'
import TableDetail from './table-detail/index.vue'
import RemarkModal from '@/views/metadata-page/metadata-management/remark-modal/index.vue'
import { CodeRemarkEdit } from '@/services/metadata-page.service'

interface Option {
    label: string
    value: string
}

const props = defineProps<{
    modelValue: any
}>()
const emit = defineEmits(['update:modelValue'])

const typeList = ref(TypeList)
const dataSourceList = ref<Option[]>([])
const sourceTablesList = ref<Option[]>([])
const partKeyList = ref<Option[]>([])
const tableDetailRef = ref<any>()
const remarkModalRef = ref<any>(null)
const tableConfig = reactive(TableConfig)
const rules = reactive<FormRules>(ConfigRules)

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

// const formData = computed({
//     get() {
//         return formDataAll.value.inputEtl
//     },
//     set(value) {
//         emit('update:modelValue', value)
//     }
// })

function changeEvent(e: string, type: string) {
    if (type === 'dbType') {
        formData.value.inputEtl.datasourceId = ''
        formData.value.tableName = ''
    }
    if (type === 'datasourceId') {
        formData.value.inputEtl.tableName = ''
    }
    if (type === 'tableName' && e) {
        nextTick(() => {
            getTableColumn()
        })
    }
    formData.value.inputEtl.partitionColumn = ''
}

function getDataSource(e: boolean, searchType?: string) {
    if (e) {
        GetDatasourceList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: searchType || ''
        }).then((res: any) => {
            dataSourceList.value = res.data.content.filter((item: any) => item.dbType !== 'KAFKA').map((item: any) => {
                return {
                    label: item.name,
                    value: item.id
                }
            })
        }).catch(() => {
            dataSourceList.value = []
        })
    }
}

// 获取数据源表
function getDataSourceTable(e: boolean, dataSourceId: string) {
    if (e && dataSourceId) {
        let options = []
        GetDataSourceTables({
            dataSourceId: dataSourceId,
            tablePattern: ''
        }).then((res: any) => {
            sourceTablesList.value = res.data.tables.map((item: any) => {
                return {
                    label: item,
                    value: item
                }
            })
        }).catch(err => {
            console.error(err)
            sourceTablesList.value = []
        })
    }
}
// 分区键
function getTableColumnData(e: boolean, dataSourceId: string, tableName: string) {
    if (e && dataSourceId && tableName) {
        GetTableColumnsByTableId({
            dataSourceId: dataSourceId,
            tableName: tableName
        }).then((res: any) => {
            partKeyList.value = (res.data.columns || []).map((column: any) => {
                return {
                    label: column.name,
                    value: column.name
                }
            })
        }).catch(err => {
            console.error(err)
        })
    }
}

// 数据预览
function showTableDetail(): void {
    if (formData.value.inputEtl.datasourceId && formData.value.inputEtl.tableName) {
        tableDetailRef.value.showModal({
            dataSourceId: formData.value.inputEtl.datasourceId,
            tableName: formData.value.inputEtl.tableName
        })
    } else {
        ElMessage.warning('请选择数据源和表')
    }
}

// 刷新数据
function getTableColumn() {
    GetTableColumnsByTableId({
        dataSourceId: formData.value.inputEtl.datasourceId,
        tableName: formData.value.inputEtl.tableName
    }).then((res: any) => {
        tableConfig.tableData = (res.data.columns || []).map((column: any) => {
            return {
                colName: column.name,
                colType: column.type,
                remark: column.columnComment,
                checked: true
            }
        })
        formData.value.outColumnList = [...tableConfig.tableData]
    }).catch(err => {
        console.error(err)
    })
}

function editEvent(e: any) {
    const remark = e.remark
    remarkModalRef.value.showModal((data: any) => {
        return new Promise((resolve, reject) => {
            CodeRemarkEdit({
                datasourceId: formData.value.inputEtl.datasourceId,
                tableName: formData.value.inputEtl.tableName,
                columnName: e.colName,
                comment: data.remark
            }).then((res: any) => {
                ElMessage.success(res.msg)
                getTableColumn()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    }, {
        remark: remark
    })
}

onMounted(() => {
    if (formData.value.inputEtl.dbType) {
        getDataSource(true, formData.value.inputEtl.dbType)
        if (formData.value.inputEtl.datasourceId) {
            getDataSourceTable(true, formData.value.inputEtl.datasourceId)
            if (formData.value.inputEtl.tableName) {
                getTableColumnData(true, formData.value.inputEtl.datasourceId, formData.value.inputEtl.tableName)
            }
        }
    }
    tableConfig.tableData = formData.value.outColumnList
})
</script>

<style lang="scss">
.config-components {
    padding:  12px 20px;
    box-sizing: border-box;
    .el-form-item {
        .el-form-item__content {
            justify-content: flex-end;
        }
        &.table-select-row {
            .el-form-item__content {
                display: flex;
                flex-wrap: nowrap;
                align-items: center;
                justify-content: flex-start;
                .el-select {
                    flex: 1;
                    min-width: 0;
                }
                .el-button {
                    flex-shrink: 0;
                    margin-left: 8px;
                }
            }
        }
    }
    .btn-group {
        display: flex;
        justify-content: space-around;
        align-items: center;
        span {
            cursor: pointer;
            color: getCssVar('color', 'primary', 'light-5');
            &:hover {
                color: getCssVar('color', 'primary');;
            }
        }
    }
}
</style>
