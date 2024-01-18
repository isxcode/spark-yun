<template>
    <div class="zqy-seach-table costom-form">
        <div class="zqy-table-top">
            <el-button type="primary" @click="addData">
                添加表单
            </el-button>
            <div class="zqy-seach">
                <el-input v-model="keyword" placeholder="请输入表单名称 回车进行搜索" :maxlength="200" clearable @input="inputEvent"
                    @keyup.enter="initData(false)" />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="form-card-container">
                <div class="form-card-list">
                    <div class="form-card-item" @click="redirectQuery">
                        <span>名称：表单名称</span>
                        <span>备注：备注</span>
                        <span>表单链接：www.baiduc.com</span>
                    </div>
                </div>
                <el-pagination v-if="pagination" class="pagination" popper-class="pagination-popper" background
                    layout="prev, pager, next, sizes, total, jumper" :hide-on-single-page="false" :total="pagination.total"
                    :page-size="pagination.pageSize" :current-page="pagination.currentPage" :page-sizes="[10, 20]"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </LoadingPage>
        <add-form ref="addFormRef"></add-form>
    </div>
</template>
  
<script lang="ts" setup>
import { ref, reactive } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { PaginationParam } from './custom-form.config'
import { useRouter } from 'vue-router'
import AddForm from './add-form/index.vue'

const router = useRouter()

const breadCrumbList = ref([
    {
        name: '自定义表单',
        code: 'custom-form'
    }
])
const networkError = ref(false)
const loading = ref(false)
const formData = ref({})
const formConfigList = ref([])
const addFormRef = ref()
const keyword = ref('')
const pagination = reactive(PaginationParam)

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    // GetLicenseList({
    //     page: tableConfig.pagination.currentPage - 1,
    //     pageSize: tableConfig.pagination.pageSize,
    //     searchKeyWord: keyword.value
    // })
    //     .then((res: any) => {
    //     tableConfig.tableData = res.data.content
    //     tableConfig.pagination.total = res.data.totalElements
    //     loading.value = false
    //     tableConfig.loading = false
    //     networkError.value = false
    //     })
    //     .catch(() => {
    //     tableConfig.tableData = []
    //     tableConfig.pagination.total = 0
    //     loading.value = false
    //     tableConfig.loading = false
    //     networkError.value = true
    //     })
}

function inputEvent(e: string) {
  if (e === '') {
    initData()
  }
}
function handleSizeChange(e: number) {
    pagination.pageSize = e
    initData()
}

function handleCurrentChange(e: number) {
  pagination.currentPage = e
  initData()
}


function addData() {
    addFormRef.value.showModal(() => {
        return new Promise((resolve, reject) => {
            router.push({
                name: 'form-setting',
                // query: {
                //   id: data.id,
                //   name: data.name
                // }
            })
            resolve()
        })
    })
}

function redirectQuery() {
    router.push({
        name: 'form-query',
        // query: {
        //   id: data.id,
        //   name: data.name
        // }
    })
}
</script>
  
<style lang="scss">
.costom-form {

    // &.zqy-form-engine {
    //   height: calc(100vh - 55px);
    // }
    .form-card-container {
        padding: 0 20px;
        box-sizing: border-box;

        .form-card-list {
            width: 100%;

            .form-card-item {
                width: 24%;
                height: 84px;
                border: 1px solid getCssVar('border-color');
                border-radius: 6px;
                background-color: getCssVar('color', 'white');
                box-shadow: getCssVar('box-shadow', 'lighter');
                transition: transform 0.15s linear;
                cursor: pointer;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
                padding: 12px;
                box-sizing: border-box;
                font-size: getCssVar('font-size', 'extra-small');

                &:hover {
                    transition: transform 0.15s linear;
                    transform: scale(1.03);
                }
            }
        }
    }
}</style>