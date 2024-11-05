<template>
    <BlockModal :model-config="modelConfig">
        <div class="json-path-container" v-if="modalType === 'jsonPath'">
            <BlockTable :table-config="tableConfig">
                <template #options="scopeSlot">
                    <div class="btn-group">
                        <span @click="copyParse(scopeSlot.row?.copyValue)">复制</span>
                    </div>
                </template>
            </BlockTable>
        </div>
        <div class="table-path-container" v-if="modalType === 'tablePath'">
            <div class="input-table-index">
                <span class="label">表行数</span>
                <el-input-number
                    v-model="tableRow"
                    placeholder="请输入"
                    :min="0"
                    controls-position="right"
                />
                <span class="label">表列数</span>
                <el-input-number
                    v-model="tableCol"
                    placeholder="请输入"
                    :min="0"
                    controls-position="right"
                />
            </div>
            <div class="search-parser">
                <span class="result-label">解析结果：</span>
                <span class="result-text">{{ tableValue.value || '暂无解析结果' }}</span>
            </div>
            <div class="search-parser search-parser-btn">
                <el-button type="primary" @click="getWorkTablePath">获取结果</el-button>
                <el-button v-if="tableValue.copyValue" type="text" @click="copyParse(tableValue.copyValue)">复制表达式</el-button>
            </div>
        </div>
        <div class="regex-path-container" v-if="modalType === 'regexPath'">
            <div class="input-table-index">
                <span class="label">正则匹配规则</span>
                <el-input
                    v-model="regexStr"
                    placeholder="请输入正则"
                />
            </div>
            <div class="search-parser">
                <span class="result-label">解析结果：</span>
                <span class="result-text">{{ regexValue.value || '暂无解析结果' }}</span>
            </div>
            <div class="search-parser search-parser-btn">
                <el-button type="primary" @click="getWorkRegexPath">获取结果</el-button>
                <el-button v-if="regexValue.copyValue" type="text" @click="copyParse(regexValue.copyValue)">复制表达式</el-button>
            </div>
        </div>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import { GetWorkInstanceJsonPath, GetWorkInstanceRegexPath, GetWorkInstanceTablePath } from '@/services/workflow.service'
import { ElMessage } from 'element-plus'

const instanceId = ref<string>('')
const modalType = ref<string>('jsonPath') // jsonPath | tablePath | regexPath

const tableRow = ref<number | null>(null)
const tableCol = ref<number | null>(null)
const tableValue = ref<any>({})

const regexStr = ref<string>('')
const regexValue = ref<any>({})

const tableConfig = reactive({
    tableData: [],
    colConfigs: [
        {
            prop: 'value',
            title: '值',
            minWidth: 100,
            showOverflowTooltip: true
        },
        {
            prop: 'jsonPath',
            title: 'jsonPath',
            minWidth: 100,
            showOverflowTooltip: true
        },
        {
            title: '操作',
            align: 'center',
            customSlot: 'options',
            width: 120
        }
    ],
    seqType: 'seq',
    loading: false
})
const modelConfig = reactive({
    title: '结果解析',
    visible: false,
    width: '520px',
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false
    },
    customClass: 'parse-value-modal',
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})
function showModal(id: string, type: string): void {
    instanceId.value = id
    modalType.value = type

    if (type === 'jsonPath') {
        tableConfig.tableData = []
        getWorkJsonPath()
    } else if (type === 'tablePath') {
        tableRow.value = null
        tableCol.value = null
        tableValue.value = {
            copyValue: '',
            value: ''
        }
    } else if (type === 'regexPath') {
        regexStr.value = 'XX(\\S+)XX'
        regexValue.value = {
            copyValue: '',
            value: ''
        }
    }
    modelConfig.visible = true
}

function getWorkJsonPath() {
    GetWorkInstanceJsonPath({
        workInstanceId: instanceId.value
    }).then((res: any) => {
        tableConfig.tableData = res.data
    }).catch(() => {
        tableConfig.tableData = []
    })
}
function getWorkTablePath() {
    if (tableRow.value !== null && tableCol.value !== null) {
        GetWorkInstanceTablePath({
            workInstanceId: instanceId.value,
            tableRow: tableRow.value,
            tableCol: tableCol.value
        }).then((res: any) => {
            tableValue.value = res.data ? res.data : {
                copyValue: '',
                value: ''
            }
            ElMessage.success('获取成功')
        }).catch(() => {
            tableValue.value = {
                copyValue: '',
                value: ''
            }
        })
    } else {
        ElMessage.error('请将表行数或表列数输入完整')
    }
}
function getWorkRegexPath() {
    if (regexStr.value) {
        GetWorkInstanceRegexPath({
            workInstanceId: instanceId.value,
            regexStr: regexStr.value
        }).then((res: any) => {
            regexValue.value = res.data ? res.data : {
                copyValue: '',
                value: ''
            }
        }).catch(() => {
            regexValue.value = {
                copyValue: '',
                value: ''
            }
        })
    } else {
        ElMessage.error('请填写正则匹配规则')
    }
}

async function copyParse(text: string) {
  try {
    await navigator.clipboard.writeText(text);
    ElMessage({
      duration: 800,
      message: '复制成功',
      type: 'success',
    });
  } catch (err) {
    console.error("Failed to copy: ", err);
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
.parse-value-modal {
    .json-path-container {
        padding: 20px;
        box-sizing: border-box;
        .btn-group {
            span {
                cursor: pointer;
                &:hover {
                    color: getCssVar('color', 'primary');
                    text-decoration: underline;
                }
            }
        }
    }
    .table-path-container {
        padding: 20px;
        box-sizing: border-box;
        .input-table-index {
            width: 100%;
            .label {
                font-size: 12px;
                margin-right: 12px;
            }
            .el-input-number {
                margin-right: 12px;
                .el-input {
                    .el-input__inner {
                        text-align: left
                    }
                }
            }
        }
        .search-parser {
            margin-top: 20px;
            .result-label {
                font-size: 12px;
            }
            .result-text {
                font-size: 12px;
            }
            &.search-parser-btn {
                display: flex;
            }
        }
    }
    .regex-path-container {
        padding: 20px;
        box-sizing: border-box;
        .input-table-index {
            width: 100%;
            display: flex;
            align-items: center;
            .label {
                font-size: 12px;
                margin-right: 12px;
                min-width: 78px;
            }
        }
        .search-parser {
            margin-top: 20px;
            .result-label {
                font-size: 12px;
            }
            .result-text {
                font-size: 12px;
            }
            &.search-parser-btn {
                display: flex;
            }
        }
    }
}
</style>