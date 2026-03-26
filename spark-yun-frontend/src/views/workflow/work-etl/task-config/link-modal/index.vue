<template>
    <BlockModal :model-config="modelConfig">
        <div style="padding: 12px 20px;">
            <data-sync-table ref="dataSyncTableRef"></data-sync-table>
        </div>
        <template #customLeft>
            <el-button type="primary" @click="refreshSourceFields" style="margin-right: auto;">刷新</el-button>
        </template>
    </BlockModal>
</template>

<script lang="ts" setup>
import { nextTick, reactive, ref } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import DataSyncTable from '../data-output/data-sync-table/index.vue'
import { GetTableColumnsByTableId } from '@/services/data-sync.service'

const dataSyncTableRef = ref<any>()
const formDataRef = ref<any>()
const incomeNodesRef = ref<any>()

const modelConfig = reactive({
    title: '字段映射',
    visible: false,
    width: '60%',
    customClass: 'link-modal',
    okConfig: {
        title: '确定',
        ok: okEvent,
        disabled: false,
        loading: false,
    },
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent,
        disabled: false,
    },
    zIndex: 1200,
    closeOnClickModal: false,
})

function showModal(formData: any, incomeNodes: any) {
    formDataRef.value = formData
    incomeNodesRef.value = incomeNodes
    modelConfig.visible = true

    nextTick(() => {
        initMappingData()
    })
}

function initMappingData() {
    const outputEtl = formDataRef.value.outputEtl
    // If existing mapping data, restore it
    if (outputEtl.colMapping && outputEtl.colMapping.length) {
        dataSyncTableRef.value.initPageData({
            sourceTableColumn: outputEtl.fromColumnList,
            targetTableColumn: outputEtl.toColumnList,
            columnMap: outputEtl.colMapping.map((item: any) => ({
                source: item[0],
                target: item[1]
            }))
        })
    } else {
        // Set source columns from income nodes
        if (incomeNodesRef.value && incomeNodesRef.value[0]) {
            dataSyncTableRef.value.setSourceTableColumn(
                incomeNodesRef.value[0].data.nodeConfigData.outColumnList.filter((item: any) => item.checked !== false).map((column: any) => ({
                    colName: column.colName,
                    colType: column.colType,
                    remark: column.remark
                }))
            )
        }
        // Set target columns from selected table
        if (outputEtl.datasourceId && outputEtl.tableName) {
            GetTableColumnsByTableId({
                dataSourceId: outputEtl.datasourceId,
                tableName: outputEtl.tableName
            }).then((res: any) => {
                const tableData = (res.data.columns || []).map((column: any) => ({
                    colName: column.name,
                    colType: column.type,
                    remark: column.columnComment
                }))
                dataSyncTableRef.value.setTargetTableColumn(tableData)
            }).catch((err: any) => {
                console.error(err)
            })
        }
    }
}

function okEvent() {
    const data = dataSyncTableRef.value.getConnect()
    formDataRef.value.outputEtl.fromColumnList = data.fromColumnList
    formDataRef.value.outputEtl.toColumnList = data.toColumnList
    formDataRef.value.outputEtl.colMapping = data.columnMap.map((item: any) => [item.source, item.target])
    modelConfig.visible = false
}

function refreshSourceFields() {
    dataSyncTableRef.value.clearConnections()
    if (incomeNodesRef.value && incomeNodesRef.value[0]) {
        dataSyncTableRef.value.setSourceTableColumn(
            incomeNodesRef.value[0].data.nodeConfigData.outColumnList.filter((item: any) => item.checked !== false).map((column: any) => ({
                colName: column.colName,
                colType: column.colType,
                remark: column.remark
            }))
        )
    }
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.link-modal {
    .modal-content {
        padding: 0;
        box-sizing: border-box;
    }
}
</style>