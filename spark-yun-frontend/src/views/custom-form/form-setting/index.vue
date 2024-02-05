<template>
    <div class="form-setting-button">
        <el-button @click="redirectQuery">返回</el-button>
        <!-- <el-button @click="showSetting">高级设置</el-button> -->
        <el-button type="primary" :loading="saveLoading" @click="publishForm">发布</el-button>
        <el-button type="primary" :loading="saveLoading" @click="saveData">保存</el-button>
    </div>
    <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
        <z-form-engine
            ref="zFormEnginRef"
            class="costom-form-engine"
            v-model="formData"
            renderSence="new"
            :isAutoCreateTable="isAutoCreateTable"
            :isDragger="true"
            :formConfigList="formConfigList"
            :getTableCodesMethod="getTableCodesMethod"
        ></z-form-engine>
    </LoadingPage>
    <!-- 高级设置 -->
    <more-setting ref="moreSettingRef"></more-setting>
</template>
  
<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import ZFormEngine from '@/lib/packages/z-form-engine/index.vue'
import { useRouter, useRoute } from 'vue-router'
import { GetTableColumnsByTableId } from '@/services/data-sync.service'
import MoreSetting from './more-setting.vue'
import LoadingPage from '@/components/loading/index.vue'
import { DeployCustomFormData, QueryFormConfigById, SaveFormConfigData } from '@/services/custom-form.service'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

const formData = ref({})
const zFormEnginRef = ref()
const moreSettingRef = ref()
const formConfigList = ref()
const networkError = ref(false)
const loading = ref(false)
const baseFormConfig = ref()
const saveLoading = ref(false)
const isAutoCreateTable = ref(false)

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryFormConfigById({
        formId: route.query.id
    }).then((res: any) => {
        baseFormConfig.value = res.data
        formConfigList.value = res.data?.components || []
        isAutoCreateTable.value = res.data?.createMode === 'AUTO_TABLE'
        loading.value = false
        networkError.value = false
    }).catch(() => {
        loading.value = false
        networkError.value = true
    })
}

// 配置拿绑定code
function getTableCodesMethod() {
    return new Promise((resolve, reject) => {
        GetTableColumnsByTableId({
            dataSourceId: baseFormConfig.value.datasourceId,
            tableName: baseFormConfig.value.mainTable
        }).then((res: any) => {
            const arr = (res.data.columns || []).map((column: any) => {
                return {
                    label: column.name,
                    value: column.name,
                    required: column.isNoNullColumn,
                    isPrimaryColumn: column.isPrimaryColumn,
                    maxlength: column.columnLength
                }
            })
            resolve(arr)
        }).catch(err => {
            reject(err)
        })
    })
}

function redirectQuery() {
    router.go(-1)
}
function showSetting() {
    moreSettingRef.value.showModal()
}
// 保存数据
function saveData() {
    const components = zFormEnginRef.value.getFormItemConfigList()
    if (components) {
        formConfigList.value = zFormEnginRef.value.getFormItemConfigList()
        saveLoading.value = true
        SaveFormConfigData({
            formId: baseFormConfig.value.formId,
            formVersion: route.query.formVersion,
            components: formConfigList.value
        }).then((res: any) => {
            saveLoading.value = false
            ElMessage.success(res.msg)
        }).catch(err => {
            saveLoading.value = false
        })
    }
}

// 发布
function publishForm() {
    ElMessageBox.confirm('确定发布该表单吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        saveLoading.value = true
        DeployCustomFormData({
            formId: route.query.id
        }).then((res: any) => {
            initData()
            saveLoading.value = false
            ElMessage.success('发布成功')
            redirectQuery()
        }).catch(err => {
            saveLoading.value = false
        })
    })
}

onMounted(() => {
    initData()
})
</script>

<style lang="scss">
.costom-form-engine {
    &.zqy-form-engine {
        height: calc(100vh - 55px);
    }
}
.form-setting-button {
    position: absolute;
    top: 0;
    height: 55px;
    right: 0;
    display: flex;
    align-items: center;
    padding-right: 20px;
}
</style>