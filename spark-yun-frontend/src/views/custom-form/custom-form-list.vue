<template>
    <div class="zqy-seach-table costom-form">
        <div class="zqy-table-top">
            <el-button type="primary" @click="addData">
                新建表单
            </el-button>
            <div class="zqy-seach">
                <el-input v-model="keyword" placeholder="请输入表单名称 回车进行搜索" :maxlength="200" clearable @input="inputEvent"
                    @keyup.enter="initData(false)" />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="form-card-container">
                <template v-if="formList?.length">
                    <el-scrollbar max-height="calc(100vh - 146px)" class="form-card-list">
                        <template v-for="card in formList">
                            <el-tooltip :disabled="!card.remark" :content="card.remark" placement="top" :show-after="600">
                                <div class="form-card-item" @click="redirectQuery(card)">
                                    <div class="card-title">
                                        <EllipsisTooltip class="card-title-name" :label="card.name" />
                                    </div>
                                    <div class="card-item">
                                        <span class="name name_3">数据源：</span>
                                        <EllipsisTooltip class="card-item-name card-item-name_3" :label="card.datasourceName" />
                                    </div>
                                    <div class="card-item">
                                        <span class="name">表名：</span>
                                        <EllipsisTooltip class="card-item-name" :label="card.mainTable" />
                                    </div>
                                    <div class="card-item">
                                        <span class="name name_4">创建时间：</span>
                                        <EllipsisTooltip class="card-item-name card-item-name_4" :label="card.createDateTime" />
                                    </div>
                                    <div class="card-item">状态：{{card.status === 'UNPUBLISHED' ? '未发布' : '已发布'}}</div>
                                    <div class="card-actions">
                                        <span v-if="card.status === 'UNPUBLISHED'" class="card-action" @click.stop="editData(card)">配置</span>
                                        <span class="card-action" @click.stop="updateData(card)">编辑</span>
                                        <span v-if="card.status === 'UNPUBLISHED'" class="card-action card-action__danger" @click.stop="deleteData(card)">删除</span>
                                        <span v-if="card.status !== 'UNPUBLISHED'" class="card-action" @click.stop="shareForm(card)">分享</span>
                                        <span v-if="card.status !== 'UNPUBLISHED'" class="card-action" @click.stop="underlineForm(card)">下线</span>
                                        <span v-else class="card-action" @click.stop="publishForm(card)">发布</span>
                                    </div>
                                </div>
                            </el-tooltip>
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
                </template>
                <template v-else>
                    <empty-page></empty-page>
                </template>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import ShareForm from './share-form-modal/index.vue'
import { CreateCustomFormData, DeleteCustomFormData, DeployCustomFormData, OfflineCustomFormData, QueryCustomFormList, UpdateCustomFormData } from '@/services/custom-form.service'

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
        name: '表单管理',
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
    } else if (formList.value?.length < 4 && formList.value?.length > 0) {
        const length = 4 - formList.value?.length
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
                resolve()
                router.push({
                    name: 'form-setting',
                    query: {
                      id: res.data.id,
                      formVersion: res.data.formVersion
                    }
                })
                ElMessage.success(res.msg)
            }).catch(err => {
                reject(err)
            })
        })
    })
}
function updateData(card: any) {
    addFormRef.value.showModal((data: formDataParam) => {
        return new Promise((resolve, reject) => {
            UpdateCustomFormData({
                id: data.id,
                name: data.name,
                remark: data.remark
            }).then((res: any) => {
                ElMessage.success(res.msg)
                resolve()
                initData()
            }).catch(err => {
                reject(err)
            })
        })
    }, card)
}
// 编辑表单配置
function editData(card: any) {
    router.push({
        name: 'form-setting',
        query: {
          id: card.id,
          formVersion: card.formVersion
        }
    })
}
function deleteData(card: any) {
    ElMessageBox.confirm('确定删除该表单吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        DeleteCustomFormData({
            formId: card.id
        }).then((res: any) => {
            handleCurrentChange(1)
            ElMessage.success(res.msg)
        }).catch(err => {
        })
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
        OfflineCustomFormData({
            formId: card.id
        }).then((res: any) => {
            initData()
            ElMessage.success('下线成功')
        }).catch(err => {
        })
    })
}
// 发布
function publishForm(card: any) {
    ElMessageBox.confirm('确定发布该表单吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        DeployCustomFormData({
            formId: card.id
        }).then((res: any) => {
            initData()
            ElMessage.success('发布成功')
        }).catch(err => {
        })
    })
}

function redirectQuery(data: any) {
    router.push({
        name: 'form-query',
        query: {
          id: data.id,
          formVersion: data.formVersion
        }
    })
}

onMounted(() => {
    pagination.currentPage = 1
    pagination.pageSize = 10
    initData()
})
</script>

<style lang="scss">
.costom-form {
    .form-card-container {
        margin-top: -20px;
        .form-card-list {
            width: 100%;
            margin-bottom: -20px;
            .el-scrollbar__wrap {
                .el-scrollbar__view {
                    padding: 20px 20px;
                    display: flex;
                    flex-wrap: wrap;
                    justify-content: space-between;
                    // padding: 0 20px;
                    box-sizing: border-box;
                }
            }

            .form-card-item {
                width: 24%;
                min-height: 166px;
                border: 1px solid getCssVar('border-color');
                border-radius: 6px;
                background-color: getCssVar('color', 'white');
                box-shadow: getCssVar('box-shadow', 'lighter');
                transition: transform 0.15s linear;
                display: inline-flex;
                flex-direction: column;
                justify-content: space-between;
                padding: 12px;
                box-sizing: border-box;
                font-size: getCssVar('font-size', 'extra-small');
                // margin-top: 12px;
                cursor: pointer;
                color: #666;
                position: relative;

                &:not(:nth-child(1),:nth-child(2),:nth-child(3),:nth-child(4)) {
                    margin-top: 12px;
                }
                &:hover {
                    transition: transform 0.15s linear;
                    transform: scale(1.03);
                    .card-title {
                        color: getCssVar('color', 'primary');
                    }
                }

                .card-item {
                    display: flex;
                    .name {
                        width: 40px;
                        &.name_3 {
                            min-width: 48px;
                        }
                        &.name_4 {
                            min-width: 64px;
                        }
                    }
                    .card-item-name {
                        display: inline-block;
                        max-width: 72%;
                        &.card-item-name_3 {
                            max-width: 68%;
                        }
                        &.card-item-name_4 {
                            max-width: 54%;
                        }
                    }
                    .url {
                        width: 62px;
                    }
                    .card-item-url {
                        display: inline-block;
                        max-width: 60%;
                    }
                }
                .card-title {
                    display: flex;
                    align-items: center;
                    font-size: 15px;
                    font-weight: 600;
                    color: getCssVar('text-color', 'regular');
                    transition: color 0.15s linear;
                    margin-bottom: 6px;

                    .card-title-name {
                        display: inline-block;
                        max-width: 100%;
                    }
                }
                &.form-card-item__empty {
                    opacity: 0;
                    cursor: default;
                    pointer-events: none;
                }

                .card-actions {
                    display: flex;
                    align-items: center;
                    flex-wrap: wrap;
                    padding-top: 8px;
                    margin-top: 4px;
                    line-height: 16px;
                }
                .card-action {
                    cursor: pointer;
                    font-size: 12px;
                    color: getCssVar('color', 'primary');
                    padding: 0 8px;
                    border-right: 1px solid getCssVar('border-color');
                    &:first-child {
                        padding-left: 0;
                    }
                    &:last-child {
                        padding-right: 0;
                        border-right: none;
                    }
                    &:hover {
                        text-decoration: underline;
                    }
                }
            }
        }

        .el-pagination {
            padding-right: 20px;
            box-sizing: border-box;
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
