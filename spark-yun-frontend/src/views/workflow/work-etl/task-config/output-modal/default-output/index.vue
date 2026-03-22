<template>
    <div class="default-output">
        <div style="max-height: 444px;">
            <BlockTable :table-config="tableConfig"></BlockTable>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, computed, onMounted, reactive } from 'vue'

const props = defineProps<{
    modelValue: any
}>()

const addCodeRef = ref()
const tableConfig = reactive({
    tableData: [],
    colConfigs: [
        {
            prop: 'colName',
            title: '字段名',
            minWidth: 120,
            showOverflowTooltip: true
        },
        {
            prop: 'colType',
            title: '类型',
            minWidth: 80,
            showOverflowTooltip: true
        },
        {
            prop: 'remark',
            title: '备注',
            minWidth: 100,
            showOverflowTooltip: true
        }
    ],
    seqType: 'seq',
    loading: false
})

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

onMounted(() => {
    tableConfig.tableData = formData.value
})
</script>

<style lang="scss">
.default-output {
    padding:  12px 20px;
    box-sizing: border-box;
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
        .el-dropdown {
            .option-more {
                font-size: 14px;
                transform: rotate(90deg);
                cursor: pointer;
                color: getCssVar('color', 'info');
            }
        }
    }
}
</style>
