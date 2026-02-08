<template>
    <div class="config-components">
        <el-form-item class="form-item-top" label="字段配置">
            <span class="add-btn">
                <el-icon @click="addNewOption">
                    <CirclePlus />
                </el-icon>
            </span>
            <div class="form-options__list">
                <div class="form-options__item" v-for="(element, index) in formData.transformEtl">
                    <el-form-item :prop="`transformEtl[${index}].colName`" :rules="rules.colName">
                        <el-select
                            v-model="element.colName"
                            filterable
                            clearable
                            placeholder="请选择字段"
                        >
                            <el-option
                                v-for="item in colNameOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item :prop="`transformEtl[${index}].transformWay`" :rules="rules.transformWay">
                        <el-select v-model="element.transformWay" @change="transformChangeEvent($Events, element)">
                            <el-option label="函数转换" value="FUNCTION_TRANSFORM"/>
                            <el-option label="自定义转换" value="CUSTOM_TRANSFORM"/>
                        </el-select>
                    </el-form-item>
                    <template v-if="element.transformWay === 'FUNCTION_TRANSFORM'">
                        <el-form-item :prop="`transformEtl[${index}].transformFunc`" :rules="rules.transformFunc">
                            <el-select
                                v-model="element.transformFunc"
                                filterable
                                clearable
                                placeholder="请选择函数"
                                @change="functionSelectEvent($event, element)"
                            >
                                <el-option
                                    v-for="item in funcOptions"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value"
                                />
                            </el-select>
                        </el-form-item>
                        <el-form-item v-for="(inputV, inputIndex) in element.inputValue" :key="inputIndex">
                            <el-input
                                v-model="element.inputValue[inputIndex]"
                                placeholder="请输入"
                            ></el-input>
                        </el-form-item>
                    </template>
                    <el-form-item
                        v-if="element.transformWay === 'CUSTOM_TRANSFORM'"
                        :prop="`transformEtl[${index}].transformSql`" :rules="rules.transformSql"
                    >
                        <el-input
                            v-model="element.transformSql"
                            clearable
                            placeholder="请输入"
                        ></el-input>
                    </el-form-item>
                    <!-- --------------- -->
                    <div class="option-btn">
                        <el-icon v-if="formData.transformEtl.length > 1" class="remove"
                            @click="removeItem(index)">
                            <CircleClose />
                        </el-icon>
                    </div>
                </div>
            </div>
        </el-form-item>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, computed, onMounted, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { GetTransformFunction } from '@/services/etl-config.service'

interface Option {
    label: string
    value: string
    inputSize?: number
}

const props = defineProps<{
    modelValue: any,
    incomeNodes: any
}>()
const emit = defineEmits(['update:modelValue'])

const colNameOptions = ref<Option[]>([])
const funcOptions = ref<Option[]>([])

const rules = reactive<FormRules>({
    colName: [{ required: true, message: '字段不能为空', trigger: ['blur', 'change'] }],
    transformWay: [{ required: true, message: '转换方式不能为空', trigger: ['blur', 'change'] }],
    transformFunc: [{ required: true, message: '函数不能为空', trigger: ['blur', 'change'] }],
    transformSql: [{ required: true, message: '自定义sql不能为空', trigger: ['blur', 'change'] }]
})

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

function addNewOption() {
    formData.value.transformEtl.push({
        colName: '',
        transformWay: 'FUNCTION_TRANSFORM',
        transformFunc: '',
        transformSql: '',
        inputValue: []
    })
}
function removeItem(index: number) {
    formData.value.transformEtl.splice(index, 1)
}

function transformChangeEvent(e: string, element: any) {
    element.inputValue = []
    element.transformSql = ''
}

function functionSelectEvent(e: string, element: any) {
    if (e) {
        const currentItem = funcOptions.value.find((item: Option) => item.value === e)
        element.inputValue = []
        if (currentItem.inputSize) {
            for (let i = 0; i < currentItem.inputSize; i++) {
                element.inputValue.push('')
            }
        }
    } else {
        element.inputValue = []
    }
}

function getFunctionList() {
    GetTransformFunction().then((res: any) => {
        funcOptions.value = Object.keys(res.data).map((key: string) => {
            return {
                label: res.data[key].funcName,
                value: key,
                inputSize: res.data[key].inputSize
            }
        })
    }).catch(err => {
        console.error(err)
    })
}

onMounted(() => {
    if (props.incomeNodes && props.incomeNodes[0]) {
        colNameOptions.value = props.incomeNodes[0].data.nodeConfigData.outColumnList.map((column) => {
            return {
                label: column.colName,
                value: column.colName
            }
        })
        formData.value.outColumnList = props.incomeNodes[0].data.nodeConfigData.outColumnList.map((column) => {
            return {
                colName: column.colName,
                colType: column.colType,
                remark: column.remark
            }
        })
    }

    getFunctionList()
})
</script>

<style lang="scss">
.config-components {
    padding:  12px 20px;
    box-sizing: border-box;
    .el-form-item {
        .el-form-item__content {
            justify-content: flex-end;
        }
        &.form-item-top {
            display: flex;
            flex-direction: column;
            .el-form-item__content {
                .add-btn {
                    position: absolute;
                    top: -22px;
                    right: 0;
                    color: getCssVar('color', 'primary');
                    cursor: pointer;
                }
            }
            .form-options__list {
                width: 100%;
                margin-top: 12px;

                .form-options__item {
                    display: flex;
                    margin-bottom: 8px;

                    .el-form-item {
                        width: 100%;
                        margin-bottom: 12px;
                    }
                    .el-form-item+.el-form-item {
                        margin-left: 4px;
                    }

                    .option-btn {
                        display: flex;
                        height: 32px;
                        align-items: center;
                        justify-content: flex-end;
                        min-width: 20px;

                        .remove {
                            color: red;
                            cursor: pointer;

                            &:hover {
                                color: getCssVar('color', 'primary');
                            }
                        }

                        .move {
                            color: getCssVar('color', 'primary');

                            &:active {
                                cursor: move;
                            }
                        }
                    }
                }
            }
        }
    }
}
</style>
