<template>
    <div class="zqy-seach-table custom-form-query">
        <div class="zqy-table-top">
            <div class="btn-container">
                <el-button type="primary" @click="addData">添加</el-button>
                <el-button type="default" @click="editFormConfigEvent">配置</el-button>
            </div>
            <div class="zqy-seach">
                <el-input
                    v-model="keyword"
                    placeholder="请输入 回车进行搜索"
                    :maxlength="200"
                    clearable
                    @input="inputEvent"
                    @keyup.enter="initData(false)"
                />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="zqy-table">
                <BlockTable
                    :table-config="tableConfig"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                >
                    <template #options="scopeSlot">
                        <div class="btn-group">
                            <span @click="editData(scopeSlot.row)">编辑</span>
                            <span @click="deleteData(scopeSlot.row)">删除</span>
                        </div>
                    </template>
                </BlockTable>
            </div>
        </LoadingPage>
        <AddModal ref="addModalRef" />
    </div>
</template>
  
<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'
import { useRouter } from 'vue-router'
import { BreadCrumbList, TableConfig } from './form-query.config'
import { } from '@/services/custom-form.service'
import { ElMessage, ElMessageBox } from 'element-plus'

interface FormUser {
    account: string
    email: string
    passwd?: string
    phone: string
    remark: string
    username: string
    id?: string
}

const router = useRouter()

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)

const formConfigList = reactive([
  {
    "uuid": "cdd90901-1d54-ae12-0595-fc8a415b4c64",
    "type": "simple",
    "formValueCode": "1",
    "codeType": "custom",
    "label": "文本输入",
    "placeholder": "请输入",
    "disabled": false,
    "required": false,
    "isColumn": true,
    "width": 2,
    "componentType": "FormInputText",
    "valid": true,
    "icon": "Document",
    "name": "文本输入"
  },
  {
    "uuid": "9fed36f1-b031-1ffa-2ad9-2446c375c8fe",
    "type": "simple",
    "formValueCode": "2",
    "codeType": "custom",
    "label": "下拉选择",
    "placeholder": "请选择",
    "disabled": false,
    "multiple": false,
    "isColumn": true,
    "width": 2,
    "componentType": "FormInputSelect",
    "options": [
      {
        "label": "选项1",
        "value": "1"
      }
    ],
    "valid": true,
    "icon": "Document",
    "name": "下拉选择"
  },
  {
        uuid: '2c82156f-2cc2-abe1-f7fd-10bc3671774e',
        type: 'simple',
        formValueCode: '333',
        codeType: 'custom',
        label: '金额输入',
        placeholder: '请输入',
        disabled: false,
        required: false,
        isColumn: true,
        precision: 2,
        width: 2,
        componentType: 'FormInputMoney',
        valid: false,
        icon: 'Document',
        name: '金额输入'
    },
    {
        uuid: '323515c0-ea7c-e5e1-ae28-9f37dd1d281f',
        type: 'simple',
        formValueCode: '1111',
        codeType: 'custom',
        label: '手机号输入',
        placeholder: '请输入',
        disabled: false,
        required: true,
        defaultValue: '13308749289',
        isColumn: true,
        width: 2,
        componentType: 'FormInputPhone',
        valid: false,
        icon: 'Document',
        name: '手机号输入'
    },
    {
        uuid: '942365e6-938c-5694-ea81-8679d4ced15b',
        type: 'simple',
        formValueCode: '333dd',
        codeType: 'custom',
        label: '邮箱输入',
        placeholder: '请输入',
        disabled: false,
        required: true,
        isColumn: true,
        defaultValue: '123@qq.com',
        width: 2,
        componentType: 'FormInputEmail',
        valid: false,
        icon: 'Document',
        name: '邮箱输入'
    },
  {
    "uuid": "3bc1a3d8-4844-5524-9730-b5674cee01c4",
    "type": "simple",
    "formValueCode": "3",
    "codeType": "custom",
    "label": "数字输入",
    "placeholder": "请输入",
    "disabled": false,
    "required": false,
    "isColumn": true,
    "precision": null,
    "width": 2,
    "componentType": "FormInputNumber",
    "valid": true,
    "icon": "Document",
    "name": "数字输入"
  },
  {
    "uuid": "0b71d994-56cd-a063-70d2-7ff7aa505fcd",
    "type": "simple",
    "formValueCode": "4",
    "codeType": "custom",
    "label": "日期选择",
    "placeholder": "请选择",
    "disabled": false,
    "required": false,
    "isColumn": true,
    "width": 1,
    "dateType": "date",
    "componentType": "FormInputDate",
    "valid": true,
    "icon": "Document",
    "name": "日期选择"
  },
  {
    "uuid": "b52d09d6-43e9-86d6-db3f-315038f95e8e",
    "type": "simple",
    "formValueCode": "5",
    "codeType": "custom",
    "label": "开关组件",
    "disabled": false,
    "required": false,
    "width": 2,
    "isColumn": true,
    "componentType": "FormInputSwitch",
    "switchInfo": {
      "open": "是",
      "close": "否"
    },
    "valid": true,
    "icon": "Open",
    "name": "开关组件"
  },
  {
    "uuid": "635ccf08-8001-2585-b166-8fdad5a75d76",
    "type": "simple",
    "formValueCode": "6",
    "codeType": "custom",
    "label": "单选框",
    "placeholder": "请选择",
    "disabled": false,
    "width": 4,
    "isColumn": true,
    "componentType": "FormInputRadio",
    "options": [
      {
        "label": "选项1",
        "value": "1"
      }
    ],
    "valid": true,
    "icon": "Document",
    "name": "单选框"
  },
  {
    "uuid": "5dac13bb-5ae7-2980-0a70-74fcf250c267",
    "type": "simple",
    "formValueCode": "7",
    "codeType": "custom",
    "label": "多选框",
    "placeholder": "请选择",
    "disabled": false,
    "width": 4,
    "multiple": true,
    "componentType": "FormInputCheckbox",
    "isColumn": true,
    "options": [
      {
        "label": "选项1",
        "value": "1"
      }
    ],
    "valid": true,
    "icon": "Document",
    "name": "多选框",
    "defaultValue": []
  }
] )

function getFormConfigById(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    tableConfig.colConfigs = [...formConfigList.map(item => {
        return {
            prop: item.formValueCode,
            title: item.label,
            minWidth: 100,
            showOverflowTooltip: true
        }
    }), {
        title: '操作',
        align: 'center',
        customSlot: 'options',
        fixed: 'right',
        width: 80
    }]

    initData()
}

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false

    tableConfig.tableData = [{}]
    tableConfig.pagination.total = 0
    loading.value = false
    tableConfig.loading = false
    // GetUserCenterList({
    //   page: tableConfig.pagination.currentPage - 1,
    //   pageSize: tableConfig.pagination.pageSize,
    //   searchKeyWord: keyword.value
    // })
    //   .then((res: any) => {
    //     tableConfig.tableData = res.data.content
    //     tableConfig.pagination.total = res.data.totalElements
    //     loading.value = false
    //     tableConfig.loading = false
    //     networkError.value = false
    //   })
    //   .catch(() => {
    //     tableConfig.tableData = []
    //     tableConfig.pagination.total = 0
    //     loading.value = false
    //     tableConfig.loading = false
    //     networkError.value = true
    //   })
}

function addData() {
    addModalRef.value.showModal((formData: FormUser) => {
        // return new Promise((resolve: any, reject: any) => {
        //     AddUserData(formData)
        //         .then((res: any) => {
        //             ElMessage.success(res.msg)
        //             initData()
        //             resolve()
        //         })
        //         .catch((error: any) => {
        //             reject(error)
        //         })
        // })
    })
}

function editData(data: any) {
    addModalRef.value.showModal((formData: FormUser) => {
        // return new Promise((resolve: any, reject: any) => {
        //     UpdateUserData(formData)
        //         .then((res: any) => {
        //             ElMessage.success(res.msg)
        //             initData()
        //             resolve()
        //         })
        //         .catch((error: any) => {
        //             reject(error)
        //         })
        // })
    }, data)
}

// 删除
function deleteData(data: any) {
    ElMessageBox.confirm('确定删除该数据吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        
    })
}

function inputEvent(e: string) {
    if (e === '') {
        initData()
    }
}

function handleSizeChange(e: number) {
    tableConfig.pagination.pageSize = e
    initData()
}

function handleCurrentChange(e: number) {
    tableConfig.pagination.currentPage = e
    initData()
}

function editFormConfigEvent() {
    router.push({
        name: 'form-setting',
        // query: {
        //   id: data.id,
        //   name: data.name
        // }
    })
}

onMounted(() => {
    getFormConfigById()
})
</script>

<style lang="scss">
.custom-form-query {
    .zqy-table {
        padding: 0 20px;
        .vxe-table--body-wrapper {
            max-height: calc(100vh - 232px);
        }
    }
    .zqy-table-top {
        .btn-container {
            display: flex;
        }
    }
}
</style>