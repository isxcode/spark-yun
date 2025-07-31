<template>
    <div class="table-list-body" id="container" v-loading="connectNodeLoading">
        <div class="merged-table-container">
            <el-table ref="mergedTableRef" :data="mergedTableColumn" row-key="code">
                <el-table-column type="index" width="50" :align="'center'" />
                <el-table-column prop="code" :show-overflow-tooltip="true" label="表名" />
            </el-table>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { GetDataSourceTables } from '@/services/data-sync.service'

interface column {
    code: string
}

const connectNodeLoading = ref<boolean>(false)

const sourceTableColumn = ref<column[]>([])
const targetTableColumn = ref<column[]>([])
const mergedTableColumn = ref<column[]>([])

// 合并表名列表并去重
function mergeTableColumns() {
    const sourceTableNames = sourceTableColumn.value.map(item => item.code)
    const targetTableNames = targetTableColumn.value.map(item => item.code)

    // 合并并去重
    const allTableNames = [...new Set([...sourceTableNames, ...targetTableNames])]

    mergedTableColumn.value = allTableNames.map(tableName => ({
        code: tableName
    }))
}

function setSourceTableList(params: any) {
    connectNodeLoading.value = true
    GetDataSourceTables(params).then((res: any) => {
        sourceTableColumn.value = res.data.tables.map((item: any) => {
            return { code: item }
        })
        mergeTableColumns()
        connectNodeLoading.value = false
    }).catch(err => {
        sourceTableColumn.value = []
        mergeTableColumns()
        connectNodeLoading.value = false
    })
}

function setTargetTableList(params) {
    connectNodeLoading.value = true
    GetDataSourceTables(params).then((res: any) => {
        targetTableColumn.value = res.data.tables.map((item: any) => {
            return { code: item }
        })
        mergeTableColumns()
        connectNodeLoading.value = false
    }).catch(err => {
        targetTableColumn.value = []
        mergeTableColumns()
        connectNodeLoading.value = false
    })
}

function getSourceTableColumn() {
    return mergedTableColumn.value
}

function setSourceTableColumn(list: any[]) {
    sourceTableColumn.value = list
    mergeTableColumns()
}

defineExpose({
    setSourceTableList,
    setTargetTableList,
    getSourceTableColumn,
    setSourceTableColumn
})
</script>

<style lang="scss">
.table-list-body {
    position: relative;
    width: 100%;
    margin-top: 20px;

    .el-table__body-wrapper {
        max-height: calc(100vh - 430px);
        overflow: auto;
    }
    .el-table__empty-block {
        width: 100% !important;
    }

    .merged-table-container {
        width: 100%;
        position: relative;

        .el-table {
            border-top: 1px solid #ebeef5;
            border-left: 1px solid #ebeef5;
            border-right: 1px solid #ebeef5;
            .el-table__row {
                td.el-table__cell {
                    font-size: getCssVar('font-size', 'extra-small');
                }
            }
        }
    }
}
</style>