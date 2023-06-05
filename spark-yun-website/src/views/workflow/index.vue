<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table">
    <div class="zqy-table-top">
      <el-button
        type="primary"
        @click="addGroup"
      >
        添加作业流
      </el-button>
      <div class="zqy-seach">
        <el-input
          v-model="keyword"
          placeholder="请输入名称/备注 回车进行搜索"
          :maxlength="200"
          clearable
          @input="inputEvent"
          @keyup.enter="initData(false)"
        />
      </div>
    </div>
    <LoadingPage
      :visible="loading"
      :network-error="networkError"
      @loading-refresh="initData(false)"
    >
      <div class="zqy-table">
        <BlockTable
          :table-config="tableConfig"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
          <template #nameSlot="scopeSlot">
            <span
              class="name-click"
              @click="showDetail(scopeSlot.row)"
            >{{ scopeSlot.row.name }}</span>
          </template>
          <template #statusTag="scopeSlot">
            <div class="btn-group">
              <el-tag
                v-if="scopeSlot.row.status === 'ACTIVE'"
                class="ml-2"
                type="success"
              >
                可用
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'NO_ACTIVE'"
                class="ml-2"
                type="danger"
              >
                不可用
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'NEW'"
                type="info"
              >
                待配置
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'UN_CHECK'">
                待检测
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'UN_AUTO'">
                未运行
              </el-tag>
            </div>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span @click="editData(scopeSlot.row)">编辑</span>
              <span @click="deleteData(scopeSlot.row)">删除</span>
              <!-- <span v-if="!scopeSlot.row.pubLoading" @click="publishData(scopeSlot.row)">发布</span> -->
              <!-- <el-icon v-else class="is-loading"><Loading /></el-icon> -->
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
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'

import { BreadCrumbList, TableConfig, FormData } from './workflow.config'
import { GetWorkflowList, AddWorkflowData, UpdateWorkflowData, DeleteWorkflowData } from '@/services/workflow.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  GetWorkflowList({
    page: tableConfig.pagination.currentPage - 1,
    pageSize: tableConfig.pagination.pageSize,
    searchKeyWord: keyword.value
  })
    .then((res: any) => {
      tableConfig.tableData = res.data.content
      tableConfig.pagination.total = res.data.totalElements
      loading.value = false
      tableConfig.loading = false
      networkError.value = false
    })
    .catch(() => {
      tableConfig.tableData = []
      tableConfig.pagination.total = 0
      loading.value = false
      tableConfig.loading = false
      networkError.value = true
    })
}

function addGroup() {
  addModalRef.value.showModal((formData: FormData) => {
    return new Promise((resolve: any, reject: any) => {
      AddWorkflowData(formData)
        .then((res: any) => {
          ElMessage.success(res.msg)
          initData()
          resolve()
        })
        .catch((error: any) => {
          reject(error)
        })
    })
  })
}

function editData(data: any) {
  addModalRef.value.showModal((formData: FormData) => {
    return new Promise((resolve: any, reject: any) => {
      UpdateWorkflowData(formData)
        .then((res: any) => {
          ElMessage.success(res.msg)
          initData()
          resolve()
        })
        .catch((error: any) => {
          reject(error)
        })
    })
  }, data)
}

// TODO:本版本暂未实现
// function publishData(data: any) {
//     data.pubLoading = true
//     CheckDatasourceData({
//         datasourceId: data.id
//     }).then((res: any) => {
//         data.pubLoading = false
//         ElMessage.success(res.msg)
//         initData()
//     }).catch((error: any) => {
//         data.pubLoading = false
//     })
// }

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm('确定删除该作业流吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteWorkflowData({
      datasourceId: data.id
    })
      .then((res: any) => {
        ElMessage.success(res.msg)
        initData()
      })
      .catch(() => {})
  })
}

function showDetail(data: any) {
  router.push({
    name: 'workflow-detail',
    query: {
      id: data.id
    }
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

onMounted(() => {
  initData()
})
</script>

<style lang="scss">
.zqy-seach-table {
  .name-click {
    cursor: pointer;
    color: $--app-unclick-color;
    &:hover {
      color: $--app-primary-color;
    }
  }
}
</style>
