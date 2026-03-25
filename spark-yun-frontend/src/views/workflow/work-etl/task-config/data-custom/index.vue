<template>
    <div class="config-components data-custom">
        <el-form-item prop="customSqlEtl.sql" label-width="0" :class="{ 'show-screen__full': fullStatus }">
            <code-mirror v-model="formData.customSqlEtl.sql" basic :lang="sqlLang" />
        </el-form-item>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, computed, onMounted } from 'vue'
import { sql } from '@codemirror/lang-sql'

const props = defineProps<{
    modelValue: any,
    incomeNodes: any
}>()
const emit = defineEmits(['update:modelValue'])

const sqlLang = ref<any>(sql())
const fullStatus = ref<boolean>(false)

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

onMounted(() => {
    if (!formData.value.customSqlEtl) {
        formData.value.customSqlEtl = { sql: '' }
    }
    if (!formData.value.outColumnList) {
        formData.value.outColumnList = []
    }
    if (props.incomeNodes && props.incomeNodes[0]) {
        formData.value.inputEtl = props.incomeNodes[0].data.nodeConfigData.inputEtl
    }
})
</script>

<style lang="scss">
.config-components {
    padding: 12px 20px;
    box-sizing: border-box;
    &.data-custom {
        .el-form-item {
            flex-direction: column;
        }
    }
    .el-form-item {
        .el-form-item__content {
            .vue-codemirror {
                height: 180px;
                width: 100%;
                .cm-editor {
                    height: 100%;
                    outline: none;
                    border: 1px solid #dcdfe6;
                }
                .cm-gutters {
                    font-size: 12px;
                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif,
                        'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';
                }
                .cm-content {
                    font-size: 12px;
                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif,
                        'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';
                }
                .cm-tooltip-autocomplete {
                    ul {
                        li {
                            height: 40px;
                            display: flex;
                            align-items: center;
                            font-size: 12px;
                            background-color: #ffffff;
                            font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif,
                                'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';
                        }
                        li[aria-selected] {
                            background: #409eff;
                        }
                        .cm-completionIcon {
                            margin-right: -4px;
                            opacity: 0;
                        }
                    }
                }
            }
        }
    }
}
</style>
