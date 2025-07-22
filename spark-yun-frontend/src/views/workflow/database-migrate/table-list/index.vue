<template>
    <div class="table-list-body" id="container" v-loading="connectNodeLoading">
        <div class="source-table-container">
            <el-table ref="sourceTableRef" :data="sourceTableColumn" row-key="code">
                <el-table-column prop="code" :show-overflow-tooltip="true" label="表名" />
            </el-table>
        </div>
        <div class="target-table-container">
            <el-table ref="targetTableRef" :data="targetTableColumn" row-key="code">
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

function setSourceTableList(params: any) {
    connectNodeLoading.value = true
    GetDataSourceTables(params).then((res: any) => {
        sourceTableColumn.value = res.data.tables.map((item: any) => {
            return { code: item }
        })
        connectNodeLoading.value = false
    }).catch(err => {
        sourceTableColumn.value = []
        connectNodeLoading.value = false
    })
}

function setTargetTableList(params) {
    connectNodeLoading.value = true
    GetDataSourceTables(params).then((res: any) => {
        targetTableColumn.value = res.data.tables.map((item: any) => {
            return { code: item }
        })
        connectNodeLoading.value = false
    }).catch(err => {
        targetTableColumn.value = []
        connectNodeLoading.value = false
    })
}

defineExpose({
    setSourceTableList,
    setTargetTableList
})
</script>

<style lang="scss">
.table-list-body {
    display: flex;
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

    .source-table-container {
        width: 100%;
        margin-right: 100px;
        position: relative;

        .el-table {
            border-top: 1px solid #ebeef5;
            border-left: 1px solid #ebeef5;
            border-right: 1px solid #ebeef5;
            // width: 100%;
            .el-table__row {
                td.el-table__cell {
                    font-size: getCssVar('font-size', 'extra-small');
                }
            }
        }
    }
    .target-table-container {
        width: 100%;
        overflow: auto;


        .el-table {
            border-top: 1px solid #ebeef5;
            border-left: 1px solid #ebeef5;
            border-right: 1px solid #ebeef5;
            .el-table__row {
                td.el-table__cell {
                    font-size: getCssVar('font-size', 'extra-small');
                }
                &:hover {
                    td.el-table__cell {
                        background-color: unset;
                    }
                }
            }
        }

    }
}
</style>