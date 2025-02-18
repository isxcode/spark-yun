<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="add-computer-group" label-position="top" :model="formData" :rules="rules">
            <el-form-item label="名称" prop="name">
                <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="类型" prop="workType">
                <el-select
                    v-model="formData.workType"
                    placeholder="请选择"
                    :disabled="formData.id ? true : false"
                    @change="workTypeChange"
                >
                    <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
            <template v-if="renderSense === 'new'">
                <el-form-item
                    label="计算集群"
                    prop="clusterId"
                    v-if="['BASH', 'PYTHON', 'DATA_SYNC_JDBC', 'SPARK_SQL', 'SPARK_JAR','EXCEL_SYNC_JDBC','PY_SPARK'].includes(formData.workType)"
                >
                    <el-select
                        v-model="formData.clusterId"
                        placeholder="请选择"
                        @change="clusterIdChangeEvent"
                        @visible-change="getClusterList"
                    >
                        <el-option
                            v-for="item in clusterList"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item
                    label="集群节点"
                    prop="clusterNodeId"
                    v-if="['BASH', 'PYTHON'].includes(formData.workType)"
                >
                    <el-select
                        v-model="formData.clusterNodeId"
                        placeholder="请选择"
                        @visible-change="getClusterNodeList"
                    >
                        <el-option
                            v-for="item in clusterNodeList"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="是否连接hive" v-if="['SPARK_SQL'].includes(formData.workType)">
                    <el-switch v-model="formData.enableHive" @change="enableHiveChange" />
                </el-form-item>
                <el-form-item
                    label="Hive数据源"
                    :prop="formData.enableHive ? 'datasourceId' : ''"
                    v-if="formData.enableHive && ['SPARK_SQL'].includes(formData.workType)"
                >
                    <el-select
                        v-model="formData.datasourceId"
                        placeholder="请选择"
                        @visible-change="getDataSourceList($event, 'HIVE')"
                    >
                        <el-option
                            v-for="item in dataSourceList"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item
                    label="数据源"
                    prop="datasourceId"
                    v-if="['EXE_JDBC', 'QUERY_JDBC', 'PRQL'].includes(formData.workType)"
                >
                    <el-select v-model="formData.datasourceId" placeholder="请选择" @visible-change="getDataSourceList">
                        <el-option
                            v-for="item in dataSourceList"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item
                    label="计算容器"
                    prop="containerId"
                    v-if="['SPARK_CONTAINER_SQL'].includes(formData.workType)"
                >
                    <el-select
                        v-model="formData.containerId"
                        placeholder="请选择"
                        @visible-change="getSparkContainerList"
                    >
                        <el-option
                            v-for="item in sparkContainerList"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </el-form-item>
            </template>
            <el-form-item label="备注">
                <el-input
                    v-model="formData.remark"
                    show-word-limit
                    type="textarea"
                    maxlength="200"
                    :autosize="{ minRows: 4, maxRows: 4 }"
                    placeholder="请输入"
                />
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetComputerGroupList, GetComputerPointData } from '@/services/computer-group.service'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetSparkContainerList } from '@/services/spark-container.service'
import { TypeList } from '../../workflow.config'

const form = ref<FormInstance>()
const callback = ref<any>()
const clusterList = ref([]) // 计算集群
const clusterNodeList = ref([]) // 集群节点
const dataSourceList = ref([]) // 数据源
const showForm = ref(true)
const renderSense = ref('')
const sparkContainerList = ref([]) // spark容器

const modelConfig = reactive({
    title: '添加作业',
    visible: false,
    width: '520px',
    okConfig: {
        title: '确定',
        ok: okEvent,
        disabled: false,
        loading: false
    },
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})
const formData = reactive({
    name: '',
    workType: '',
    clusterId: '', // 计算集群
    clusterNodeId: '', // 集群节点
    datasourceId: '', // 数据源
    containerId: '', // spark容器
    enableHive: false,
    remark: '',
    id: ''
})
const typeList = reactive(TypeList)
const rules = reactive<FormRules>({
    name: [
        {
            required: true,
            message: '请输入作业名称',
            trigger: ['blur', 'change']
        }
    ],
    workType: [
        {
            required: true,
            message: '请选择类型',
            trigger: ['blur', 'change']
        }
    ],
    clusterId: [
        {
            required: true,
            message: '请选择计算集群',
            trigger: ['blur', 'change']
        }
    ],
    clusterNodeId: [
        {
            required: true,
            message: '请选择集群节点',
            trigger: ['blur', 'change']
        }
    ],
    datasourceId: [
        {
            required: true,
            message: '请选择数据源',
            trigger: ['blur', 'change']
        }
    ],
    containerId: [
        {
            required: true,
            message: '请选择计算容器',
            trigger: ['blur', 'change']
        }
    ]
})

function showModal(cb: () => void, data: any): void {
    callback.value = cb
    modelConfig.visible = true
    if (data && data.id) {
        Object.keys(data).forEach((key: string) => {
            formData[key] = data[key]
        })
        formData.clusterId && getClusterList(true)
        formData.clusterNodeId && getClusterNodeList(true)
        formData.datasourceId && getDataSourceList(true)
        formData.containerId && getSparkContainerList(true)

        modelConfig.title = '编辑作业'
        renderSense.value = 'edit'
    } else {
        formData.name = ''
        formData.workType = ''
        formData.remark = ''
        formData.clusterId = ''
        formData.clusterNodeId = ''
        formData.datasourceId = ''
        formData.enableHive = false

        formData.id = ''
        modelConfig.title = '添加作业'
        renderSense.value = 'new'
    }
    nextTick(() => {
        form.value?.resetFields()
    })
}

function okEvent() {
    form.value?.validate((valid) => {
        if (valid) {
            modelConfig.okConfig.loading = true
            callback
                .value({
                    ...formData,
                    id: formData.id ? formData.id : undefined
                })
                .then((res: any) => {
                    modelConfig.okConfig.loading = false
                    if (res === undefined) {
                        modelConfig.visible = false
                    } else {
                        modelConfig.visible = true
                    }
                })
                .catch((err: any) => {
                    modelConfig.okConfig.loading = false
                })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}
function enableHiveChange() {
    showForm.value = false
    nextTick(() => {
        showForm.value = true
    })
}
function clusterIdChangeEvent() {
    formData.clusterNodeId = ''
}

function getClusterList(e: boolean) {
    if (e) {
        GetComputerGroupList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: ''
        })
            .then((res: any) => {
                clusterList.value = res.data.content.map((item: any) => {
                    return {
                        label: item.name,
                        value: item.id
                    }
                })
            })
            .catch(() => {
                clusterList.value = []
            })
    }
}
function getClusterNodeList(e: boolean) {
    if (e && formData.clusterId) {
        GetComputerPointData({
            page: 0,
            pageSize: 10000,
            searchKeyWord: '',
            clusterId: formData.clusterId
        })
            .then((res: any) => {
                clusterNodeList.value = res.data.content.map((item: any) => {
                    return {
                        label: item.name,
                        value: item.id
                    }
                })
            })
            .catch(() => {
                clusterNodeList.value = []
            })
    }
}
function getDataSourceList(e: boolean, searchType?: string) {
    if (e) {
        GetDatasourceList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: searchType || ''
        }).then((res: any) => {
            dataSourceList.value = res.data.content
            .filter((item: any) => !(item.dbType === 'KAFKA' && ['EXE_JDBC', 'QUERY_JDBC'].includes(formData.workType)))
            .map((item: any) => {
                return {
                    label: item.name,
                    value: item.id
                }
            })
        }).catch(() => {
            dataSourceList.value = []
        })
    }
}
function getSparkContainerList(e: boolean, searchType?: string) {
    if (e) {
        GetSparkContainerList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: ''
        })
            .then((res: any) => {
                sparkContainerList.value = res.data.content.map((item: any) => {
                    return {
                        label: item.name,
                        value: item.id
                    }
                })
            })
            .catch(() => {
                sparkContainerList.value = []
            })
    }
}
function workTypeChange() {
    formData.datasourceId = ''
    formData.clusterId = ''
    formData.clusterNodeId = ''
}
function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.add-computer-group {
    padding: 12px 20px 0 20px;
    box-sizing: border-box;
}
</style>
