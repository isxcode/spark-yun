<template>
    <el-descriptions>
        <el-descriptions-item label="表名">{{ 2 }}</el-descriptions-item>
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
</script>