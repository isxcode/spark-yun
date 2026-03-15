<template>
    <div class="config-components">
        <el-form-item label="SQL输入" prop="sql" :class="{ 'show-screen__full': fullStatus }">
            <!-- <el-icon class="modal-full-screen" @click="fullScreenEvent">
                <FullScreen v-if="!fullStatus" />
                <Close v-else />
            </el-icon> -->
            <code-mirror v-model="formData.sql" basic :lang="sqlLang" />
        </el-form-item>
        <el-form-item>
            <el-popover
                placement="right"
                title="提示"
                :width="400"
                trigger="hover"
                popper-class="message-error-tooltip"
                :content="errorResult"
                v-if="errorResult"
                >
                <template #reference>
                    <el-icon class="hover-tooltip"><WarningFilled /></el-icon>
                </template>
            </el-popover>
            <el-button :loading="parseLoading" type="primary" @click="parseEvent">解析</el-button>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="addNewCode">添加</el-button>
        </el-form-item>
        <div style="max-height: 444px;">
            <BlockTable
              :table-config="tableConfig"
            >
                <template #options="scopeSlot">
                    <div class="btn-group">
                        <el-dropdown trigger="click">
                            <el-icon class="option-more" @click.stop>
                                <MoreFilled />
                            </el-icon>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item @click="editCode(scopeSlot.row)">编辑</el-dropdown-item>
                                    <el-dropdown-item @click="removeCode(scopeSlot.row)">删除</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </template>
            </BlockTable>
        </div>
        <!-- 添加字段 -->
        <add-code ref="addCodeRef"></add-code>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, defineEmits, computed, onMounted, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { TypeList, ConfigRules, TableConfig } from './config.ts'
import AddCode from './add-code/index.vue'
import { sql } from '@codemirror/lang-sql'
import { ParseCustomSqlFunction } from '@/services/etl-config.service'

interface codeParam {
    colName: string
    colType: string
    remark: string
}

const props = defineProps<{
    modelValue: any,
    incomeNodes: any
}>()
const emit = defineEmits(['update:modelValue'])

const addCodeRef = ref()
const sqlLang = ref<any>(sql())
const errorResult = ref<string>('')
const fullStatus = ref<boolean>(false)
const parseLoading = ref<boolean>(false)
const tableConfig = reactive(TableConfig)

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

// 删除来源编码
function removeCode(cData: codeParam) {
    ElMessageBox.confirm('确定删除该字段吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        formData.value.outColumnList = formData.value.outColumnList.filter(item => item.colName !== row.colName)
        tableConfig.tableData = formData.value.outColumnList
    })
}

function addNewCode() {
    addCodeRef.value.showModal((params: codeParam) => {
        formData.value.outColumnList.push({ ...params })
        tableConfig.tableData = formData.value.outColumnList
    })
}
function editCode(row: codeParam) {
    addCodeRef.value.showModal((formData: codeParam) => {
        row.colName = formData.colName
        row.colType = formData.colType
        row.remark = formData.remark
    }, row)
}

// function fullScreenEvent(type: string) {
//     fullStatus.value = !fullStatus.value
// }

// 解析
function parseEvent() {
    if (!formData.value.sql) {
        ElMessage.error('请输入sql')
        return
    }
    parseLoading.value = true
    ParseCustomSqlFunction({
        sql: formData.value.sql
    }).then((res: any) => {
        errorResult.value = ''
        formData.value.outColumnList = (res.data.columns || []).map((column: any) => {
            return {
                colName: column.name,
                colType: column.type,
                remark: column.columnComment
            }
        })
        tableConfig.tableData = formData.value.outColumnList
        parseLoading.value = false
    }).catch(err => {
        console.error(err)
        errorResult.value = err
        parseLoading.value = false
    })
}

onMounted(() => {
    // tableConfig.tableData = formData.value.outColumnList
    tableConfig.tableData = []
    if (formData.value.outColumnList && formData.value.outColumnList.length) {
        tableConfig.tableData = formData.value.outColumnList
    } else {
        if (props.incomeNodes && props.incomeNodes[0]) {
            formData.value.outColumnList = props.incomeNodes[0].data.nodeConfigData.outColumnList
            tableConfig.tableData = formData.value.outColumnList
            formData.value.inputEtl = props.incomeNodes[0].data.nodeConfigData.inputEtl
        }
    }
})
</script>

<style lang="scss">
.config-components {
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
            // position: absolute;
            // right: 4px;
            // top: 13px;

            .option-more {
                font-size: 14px;
                transform: rotate(90deg);
                cursor: pointer;
                color: getCssVar('color', 'info');
            }
        }
    }
    .el-form-item {
        &.show-screen__full {
            position: fixed;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            background-color: #ffffff;
            padding: 12px 20px;
            box-sizing: border-box;
            transition: all 0.15s linear;
            z-index: 10;
            .el-form-item__content {
                align-items: flex-start;
                height: 100%;
                .modal-full-screen {
                    position: absolute;
                    top: -26px;
                    right: 0;
                    cursor: pointer;
                    &:hover {
                        color: getCssVar('color', 'primary');
                    }
                }
                .vue-codemirror {
                    height: calc(100% - 36px);
                }
            }
        }
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
            .add-btn {
                position: absolute;
                right: 0;
                top: -32px;
                .el-icon {
                    color: getCssVar('color', 'primary');
                    cursor: pointer;
                    font-size: 16px;
                }
            }
            .hover-tooltip {
                margin-right: 8px;
                font-size: 16px;
                color: getCssVar('color', 'danger');
            }
        }
    }
}
</style>
