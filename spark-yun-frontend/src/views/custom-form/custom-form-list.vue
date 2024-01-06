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
                <el-scrollbar max-height="calc(100vh - 186px)" class="form-card-list">
                    <template v-for="card in formList">
                        <div class="form-card-item" @click="redirectQuery">
                            <div class="card-item">
                                <span class="name">名称：</span>
                                <EllipsisTooltip class="card-item-name" :label="card.name" />
                            </div>
                            <div class="card-item">创建时间：{{card.createDate}}</div>
                            <div class="card-item">状态：{{card.status}}</div>
                            <div class="card-item">版本：{{card.version}}</div>
                            <!-- <div class="card-item">
                                <span class="url">表单链接：</span>
                                <EllipsisTooltip class="card-item-url" :label="card.url" />
                            </div> -->
                            <el-dropdown trigger="click" popper-class="custom-form-popover">
                                <div class="card-button" @click.stop>
                                    <el-icon><MoreFilled /></el-icon>
                                </div>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item @click="editData(card)">编辑</el-dropdown-item>
                                        <el-dropdown-item @click="deleteData(card)">删除</el-dropdown-item>
                                        <el-dropdown-item @click="shareForm(card)">分享</el-dropdown-item>
                                        <el-dropdown-item @click="underlineForm(card)">下线</el-dropdown-item>
                                        <el-dropdown-item @click="publishForm(card)">发布</el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </template>
                    <template v-for="card in emptyBox">
                        <div class="form-card-item form-card-item__empty"></div>
                    </template>
                </el-scrollbar>
                <el-pagination
                    v-if="pagination"
                    class="pagination"
                    popper-class="pagination-popper"
                    background
                    layout="prev, pager, next, sizes, total, jumper" :hide-on-single-page="false" :total="pagination.total"
                    :page-size="pagination.pageSize" :current-page="pagination.currentPage" :page-sizes="[10, 20]"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange"
                />
            </div>
        </LoadingPage>
        <add-form ref="addFormRef"></add-form>
        <ShareForm ref="shareFormRef"></ShareForm>
    </div>
</template>
  
<script lang="ts" setup>
import { ref, reactive, onMounted, computed } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { PaginationParam } from './custom-form.config'
import { useRouter } from 'vue-router'
import AddForm from './add-form/index.vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
import { ElMessageBox } from 'element-plus'
import ShareForm from './share-form-modal/index.vue'
import { CreateCustomFormData, QueryCustomFormList } from '@/services/custom-form.service'

interface formDataParam {
    name: string
    // clusterId: string
    datasourceId: string
    createMode: string
    mainTable: string
    remark: string
    id?: string
}

const router = useRouter()
const breadCrumbList = ref([
    {
        name: '自定义表单',
        code: 'custom-form'
    }
])
const networkError = ref(false)
const loading = ref(false)
const addFormRef = ref()
const keyword = ref('')
const formList = ref()   // 卡片列表
const pagination = reactive(PaginationParam)
const shareFormRef = ref()

const emptyBox = computed(() => {
    if (formList.value?.length > 4 && (formList.value?.length % 4)) {
        const length = 4 - formList.value?.length % 4
        return new Array(length)
    } else {
        return []
    }
})

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryCustomFormList({
        page: pagination.currentPage - 1,
        pageSize: pagination.pageSize,
        searchKeyWord: keyword.value || ''
    }).then((res: any) => {
        formList.value = res.data.content
        pagination.total = res.data.totalElements
        loading.value = false
        networkError.value = false
    }).catch(() => {
        formList.value = []
        pagination.total = 0
        loading.value = false
        networkError.value = true
    })
    // formList.value = [
    //     {
    //         name: '表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1',
    //         clusterId: '集群1',
    //         datasourceId: '数据源1',
    //         remark: '备注',
    //         createDate: '2023-12-14',
    //         status: '未发布',
    //         version: 1,
    //         url: '表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1'
    //     },
    //     {
    //         name: '表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1',
    //         clusterId: '集群1',
    //         datasourceId: '数据源1',
    //         remark: '备注',
    //         createDate: '2023-12-14',
    //         status: '未发布',
    //         version: 1,
    //         url: '表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1'
    //     },
    //     {
    //         name: '表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1',
    //         clusterId: '集群1',
    //         datasourceId: '数据源1',
    //         remark: '备注',
    //         createDate: '2023-12-14',
    //         status: '未发布',
    //         version: 1,
    //         url: '表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1'
    //     },
    //     {
    //         name: '表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1',
    //         clusterId: '集群1',
    //         datasourceId: '数据源1',
    //         remark: '备注',
    //         createDate: '2023-12-14',
    //         status: '未发布',
    //         version: 1,
    //         url: '表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1'
    //     },
    //     {
    //         name: '表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1',
    //         clusterId: '集群1',
    //         datasourceId: '数据源1',
    //         remark: '备注',
    //         createDate: '2023-12-14',
    //         status: '未发布',
    //         version: 1,
    //         url: '表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1表单1'
    //     }
    // ]
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
    addFormRef.value.showModal((data: formDataParam) => {
        return new Promise((resolve, reject) => {
            CreateCustomFormData(data).then((res: any) => {
                debugger
                resolve(true)
                router.push({
                    name: 'form-setting',
                    query: {
                      id: res.formId,
                      name: res.name
                    }
                })
            }).catch(err => {
                reject(err)
            })
        })
    })
}
// 编辑表单配置
function editData(card: any) {
    router.push({
        name: 'form-setting',
        // query: {
        //   id: data.id,
        //   name: data.name
        // }
    })
}
function deleteData(card: any) {
    ElMessageBox.confirm('确定删除该表单吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        console.log('删除')
    })
}
// 分享
function shareForm(card: any) {
    shareFormRef.value.showModal(card)
}
// 下线
function underlineForm(card: any) {
    ElMessageBox.confirm('确定下线该表单吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        console.log('下线')
    })
}
// 发布
function publishForm(card: any) {
    ElMessageBox.confirm('确定发布该表单吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        console.log('发布')
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

onMounted(() => {
    initData()
})
</script>
  
<style lang="scss">
.costom-form {

    .form-card-container {
        box-sizing: border-box;
        padding: 0 20px;

        .form-card-list {
            width: 100%;
            .el-scrollbar__wrap {
                .el-scrollbar__view {
                    display: flex;
                    flex-wrap: wrap;
                    justify-content: space-between;
                    // padding: 0 20px;
                    box-sizing: border-box;
                }
            }

            .form-card-item {
                width: 24%;
                height: 104px;
                border: 1px solid getCssVar('border-color');
                border-radius: 6px;
                background-color: getCssVar('color', 'white');
                box-shadow: getCssVar('box-shadow', 'lighter');
                transition: transform 0.1s linear;
                display: inline-flex;
                flex-direction: column;
                justify-content: space-between;
                padding: 12px;
                box-sizing: border-box;
                font-size: getCssVar('font-size', 'extra-small');
                // margin-top: 12px;
                cursor: default;
                color: #666;
                position: relative;

                &:not(:nth-child(1),:nth-child(2),:nth-child(3),:nth-child(4)) {
                    margin-top: 12px;
                }
                &:hover {
                    transition: transform 0.1s linear;
                    transform: scale(1.01);
                }

                .card-item {
                    display: flex;
                    .name {
                        width: 40px;
                    }
                    .card-item-name {
                        display: inline-block;
                        max-width: 74%;
                    }
                    .url {
                        width: 62px;
                    }
                    .card-item-url {
                        display: inline-block;
                        max-width: 60%;
                    }
                }
                .el-dropdown {
                    position: absolute;
                    right: 6px;
                    bottom: 6px;
                    .card-button {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        border: 1px solid getCssVar('color', 'info');
                        border-radius: 50%;
                        height: 16px;
                        width: 16px;
                        color: getCssVar('color', 'info');
                        &:hover {
                            cursor: pointer;
                            color: getCssVar('color', 'primary');
                            border-color: getCssVar('color', 'primary');
                        }
                    }
                }
                &.form-card-item__empty {
                    opacity: 0;
                    cursor: default;
                    pointer-events: none;
                }
            }
        }
    }
}
.custom-form-popover {
    .el-scrollbar {
        .el-scrollbar__wrap {
            .el-dropdown__list {
                .el-dropdown-menu {
                    .el-dropdown-menu__item {
                        font-size: 12px;
                    }
                }
            }
        }
    }
}
</style>