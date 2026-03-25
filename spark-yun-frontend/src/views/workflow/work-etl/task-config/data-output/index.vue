<template>
    <div class="config-components">
        <el-form-item label="类型" prop="outputEtl.dbType" :rules="rules.dbType">
            <el-select
                v-model="formData.dbType"
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
        <el-form-item label="数据源" prop="outputEtl.datasourceId" :rules="rules.datasourceId">
            <el-select
                v-model="formData.datasourceId"
                filterable
                clearable
                placeholder="请选择"
                @change="changeEvent($event, 'datasourceId')"
                @visible-change="getDataSource($event, formData.dbType)"
            >
                <el-option
                    v-for="item in dataSourceList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
        </el-form-item>
        <el-form-item label="表" prop="outputEtl.tableName" :rules="rules.tableName">
            <el-select
                v-model="formData.tableName"
                filterable
                clearable
                placeholder="请选择"
                @change="changeEvent($event, 'tableName')"
                @visible-change="getDataSourceTable($event, formData.datasourceId)"
            >
                <el-option
                    v-for="item in sourceTablesList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
        </el-form-item>
        <el-form-item prop="outputEtl.writeMode" label="写入模式" :rules="rules.writeMode">
            <el-select
                v-model="formData.writeMode"
                clearable
                filterable
                placeholder="请选择"
            >
                <el-option
                    v-for="item in filteredOverModeList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
        </el-form-item>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, defineEmits, computed, onMounted, reactive, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { TypeList, ConfigRules, TableConfig, OverModeList } from './config.ts'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetDataSourceTables } from '@/services/data-sync.service'

interface Option {
    label: string
    value: string
}

const props = defineProps<{
    modelValue: any,
    incomeNodes: any
}>()
const emit = defineEmits(['update:modelValue'])

const typeList = ref(TypeList)
const dataSourceList = ref<Option[]>([])
const sourceTablesList = ref<Option[]>([])
const overModeList = ref<Option[]>(OverModeList)

const preNodeConfig = ref<any>()

const rules = reactive<FormRules>(ConfigRules)

watch(() => props.incomeNodes, (v: any) => {
    preNodeConfig.value = v.length ? v[0].data : {}
}, {
    immediate: true,
    deep: true
})
const filteredOverModeList = computed(() => {
    // if (formData.targetDBType === 'CLICKHOUSE') {
    //     return overModeList.value.filter(item => item.value === 'INTO')
    // }
    return overModeList.value
})
const formDataAll = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

const formData = computed({
    get() {
        return formDataAll.value.outputEtl
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

function changeEvent(e: string, type: string) {
    if (type === 'dbType') {
        formData.value.datasourceId = ''
        formData.value.tableName = ''
    }
    if (type === 'datasourceId') {
        formData.value.tableName = ''
    }
    formData.value.partitionColumn = ''

    // 清空字段映射，下次打开字段映射弹窗时重新获取目标表字段
    formDataAll.value.outputEtl.fromColumnList = []
    formDataAll.value.outputEtl.toColumnList = []
    formDataAll.value.outputEtl.colMapping = []
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
onMounted(() => {
    if (formData.value.dbType) {
        getDataSource(true, formData.value.dbType)
        if (formData.value.datasourceId) {
            getDataSourceTable(true, formData.value.datasourceId)
        }
    }
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
    }
}
</style>
