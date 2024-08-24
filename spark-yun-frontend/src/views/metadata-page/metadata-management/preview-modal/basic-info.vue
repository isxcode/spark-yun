<template>
    <el-descriptions :column="1" size="small">
        <el-descriptions-item label="表名">{{ info?.tableName }}</el-descriptions-item>
        <el-descriptions-item label="字段数量">{{ info?.columnCount }}</el-descriptions-item>
        <el-descriptions-item label="表总条数">{{ info?.totalRows }}</el-descriptions-item>
        <el-descriptions-item label="表大小">{{ info?.totalSizeStr }}</el-descriptions-item>
        <el-descriptions-item label="采集时间">{{ info?.collectDateTime }}</el-descriptions-item>
        <el-descriptions-item label="刷新时间">{{ info?.refreshDateTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ info?.tableComment }}</el-descriptions-item>
    </el-descriptions>
</template>

<script lang="ts" setup>
import { onMounted, ref, defineProps } from 'vue'
import { GetTableBasicInfo } from '@/services/metadata-page.service';

const props = defineProps<{
    datasourceId: string,
    tableName: string
}>()
const info = ref<any>()

function initData() {
    GetTableBasicInfo({
        datasourceId: props.datasourceId,
        tableName: props.tableName
    }).then((res: any) => {
        info.value = res.data
    }).catch(() => {
    })
}

onMounted(() => {
    initData()
})

defineExpose({
    initData
})
</script>