<template>
    <BlockModal :model-config="modelConfig">
        <el-tabs v-model="activeName" @tab-click="changeTypeEvent">
            <el-tab-pane label="基础信息" name="basicInfo"></el-tab-pane>
            <el-tab-pane label="字段信息" name="codeInfo"></el-tab-pane>
            <el-tab-pane label="数据预览" name="dataPreview"></el-tab-pane>
        </el-tabs>
        <div class="info-container">
            <component
                ref="preTabRef"
                :is="tabComponent"
                :datasourceId="infoData.datasourceId"
                :tableName="infoData.tableName"
                @editEvent="editEvent"
            ></component>
        </div>
        <template #customLeft>
            <el-button @click="closeEvent">关闭</el-button>
            <el-button v-if="activeName === 'basicInfo'" type="primary" :loading="exportLoading" @click="refreshEvent">刷新</el-button>
            <el-button v-if="activeName === 'dataPreview'" type="primary" :loading="exportLoading" @click="exportEvent">导出</el-button>
        </template>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, markRaw, defineEmits } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import basicInfo from './basic-info.vue'
import codeInfo from './code-info.vue'
import dataPreview from './data-preview.vue'
import { ExportTableDetailData, RefreshTableDetailData } from '@/services/metadata-page.service'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['editEvent'])

const activeName = ref<string>('basicInfo')
const exportLoading = ref<boolean>(false)
const tabComponent = ref<any>()
const infoData = ref<any>()
const preTabRef = ref<any>()

const modelConfig = reactive<any>({
    title: '详情',
    visible: false,
    width: '720px',
    customClass: 'table-code-preview',
    needScale: false,
    zIndex: 1100,
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        hide: true
    },
    closeOnClickModal: false
})

function showModal(data: any): void {
    activeName.value = 'basicInfo'
    changeTypeEvent({ paneName: 'basicInfo' })
    infoData.value = data
    modelConfig.visible = true
}

function changeTypeEvent(e: any) {
    const lookup: any = {
        basicInfo: basicInfo,
        codeInfo: codeInfo,
        dataPreview: dataPreview
    }
    tabComponent.value = markRaw(lookup[e.paneName])
}

function refreshEvent() {
    exportLoading.value = true
    RefreshTableDetailData({
        datasourceId: infoData.value.datasourceId,
        tableName: infoData.value.tableName
    }).then((res: any) => {
        ElMessage.success('刷新成功')
        exportLoading.value = false
        preTabRef.value?.initData()
    }).catch(() => {
        exportLoading.value = false
    })
}

function exportEvent() {
    exportLoading.value = true
    ExportTableDetailData({
        datasourceId: infoData.value.datasourceId,
        tableName: infoData.value.tableName
    }).then((res: any) => {
        if (res.type === 'application/json') {
            ElMessage.error('暂不支持10000条数据导出')
        } else {
            const blobURL = URL.createObjectURL(res)
            // 创建一个链接元素并模拟点击下载
            const link = document.createElement('a')
            link.href = blobURL
            link.download = `${infoData.value.tableName}.xls`  // 根据实际情况设置下载文件的名称和扩展名
            link.click()
            // 释放Blob URL
            URL.revokeObjectURL(blobURL)
        }
        exportLoading.value = false
    }).catch(() => {
        exportLoading.value = false
    })
}

function closeEvent() {
    modelConfig.visible = false
}

function editEvent(data: any) {
    emit('editEvent', data)
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.table-code-preview {
    .modal-content {
        padding: 0 20px;
        box-sizing: border-box;

        .el-tabs {
            .el-tabs__item {
                width: 100px;
                padding: 0;
                font-size: 13px;
            }
            .el-tabs__nav-wrap::after {
                background-color: getCssVar('border-color');
                height: 1px;
            }

        }

        .info-container {
            height: calc(100vh - 342px);
            .vxe-table {
                .vxe-table--body-wrapper {
                    max-height: calc(100vh - 404px);
                }
            }
        }
        .btn-group {
            display: flex;
            justify-content: space-between;
            align-items: center;
            &.btn-group__center {
                justify-content: center;
            }
            span {
                cursor: pointer;
                color: getCssVar('color', 'primary', 'light-5');
                &:hover {
                color: getCssVar('color', 'primary');;
                }
            }
        }
    }
}
</style>