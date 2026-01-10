<template>
    <BlockDrawer :drawer-config="drawerConfig">
        <!-- <LoadingPage :visible="loading"> -->
        <div class="workflow-page">
            <div class="flow-container">
                <spark-jar
                    v-if="(workConfig.workType === 'SPARK_JAR' || workConfig.workType === 'FLINK_JAR')"
                    :workItemConfig="workConfig"
                    :workFlowData="workFlowData"
                    :disabled="true"
                ></spark-jar>
                <WorkApi
                    v-if="workConfig.workType === 'API'"
                    :workItemConfig="workConfig"
                    :workFlowData="workFlowData"
                    :disabled="true"
                ></WorkApi>
                <WorkItem
                    v-if="
                        !['SPARK_JAR',
                        'DATA_SYNC_JDBC',
                        'EXCEL_SYNC_JDBC',
                        'DB_MIGRATE'
                        ].includes(workConfig.workType)
                    "
                    :workItemConfig="workConfig"
                    :workFlowData="workFlowData"
                    :disabled="true"
                ></WorkItem>
                <data-sync
                    v-if="workConfig.workType === 'DATA_SYNC_JDBC'"
                    :workItemConfig="workConfig"
                    :disabled="true"
                ></data-sync>
                <ExcelImport
                    v-if="workConfig.workType === 'EXCEL_SYNC_JDBC'"
                    :workItemConfig="workConfig"
                    :disabled="true"
                ></ExcelImport>
                <DatabaseMigrate
                    v-if="workConfig.workType === 'DB_MIGRATE'"
                    :workItemConfig="workConfig"
                    :disabled="true"
                ></DatabaseMigrate>
            </div>
        </div>
        <!-- </LoadingPage> -->
    </BlockDrawer>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref } from 'vue'
import BlockDrawer from '@/components/block-drawer/index.vue'

import sparkJar from '@/views/workflow/spark-jar/index.vue'
import WorkApi from '@/views/workflow/work-api/index.vue'
import DataSync from '@/views/workflow/data-sync/index.vue'
import WorkItem from '@/views/workflow/work-item/index.vue'
import DatabaseMigrate from '@/views/workflow/database-migrate/index.vue'
import ExcelImport from '@/views/workflow/excel-import/index.vue'

import {
    GetWorkItemConfig
} from '@/services/workflow.service'

interface WorkConfig {
    workType: string
    id: string
}

interface WorkFlowData {
    name: string
    id: string
}

const workConfig = ref<WorkConfig>()
const workFlowData = ref<WorkFlowData>()
const loading = ref(false)
const networkError = ref(false)

const drawerConfig = reactive({
    title: '作业详情',
    visible: false,
    width: '685px',
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false,
    },
    customClass: 'work-detail-modal',
    zIndex: 1100,
    closeOnClickModal: false,
});

function showModal(data: any) {
    workConfig.value = {
        workType: data.workType,
        id: data.workId
    }
    workFlowData.value = {
        name: data.name,
        id: data.workId
    }
    drawerConfig.title = `作业详情-${workFlowData.value.name}`
    drawerConfig.visible = true;
}

function initData() {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    GetWorkItemConfig({
        workId: props.workItemConfig.id
    }).then((res: any) => {
        workConfig.value = res.data

        loading.value = false
        networkError.value = false
    }).catch(() => {
        loading.value = false
        networkError.value = false
    })
}

function okEvent() {

}

function closeEvent() {
    drawerConfig.visible = false;
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.work-detail-modal {
    .workflow-page {
        height: calc(100% - 0px);
        overflow: auto;
    }
}
</style>