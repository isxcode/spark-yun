<!--
 * @Author: fanciNate
 * @Date: 2023-04-27 16:57:57
 * @LastEditTime: 2023-05-27 14:58:39
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/views/workflow/workflow-detail/index.vue
-->
<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table">
    <div class="zqy-table-top">
      <el-button
        type="primary"
        @click="addData"
      >
        添加作业
      </el-button>
      <div class="zqy-seach">
        <el-input
          v-model="keyword"
          placeholder="请输入名称/类型/备注 回车进行搜索"
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
                v-if="scopeSlot.row.status === 'PUBLISHED'"
                class="ml-2"
                type="success"
              >
                已发布
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'STOP'"
                class="ml-2"
                type="danger"
              >
                下线
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'PAUSED'"
                class="ml-2"
                type="warning"
              >
                已暂停
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'UN_PUBLISHED'">
                未发布
              </el-tag>
            </div>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span @click="editData(scopeSlot.row)">编辑</span>
              <span @click="deleteData(scopeSlot.row)">删除</span>
              <!-- <span>历史</span> -->
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

import { DetailTableConfig, FormData } from '../workflow.config'
import { GetWorkflowDetailList, AddWorkflowDetailList, UpdateWorkflowDetailList, DeleteWorkflowDetailList } from '@/services/workflow.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)
const tableConfig: any = reactive(DetailTableConfig)
const breadCrumbList = reactive([
  {
    name: '作业流',
    code: 'workflow'
  },
  {
    name: '作业',
    code: 'workflow-detail'
  }
])

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  GetWorkflowDetailList({
    page: tableConfig.pagination.currentPage - 1,
    pageSize: tableConfig.pagination.pageSize,
    searchKeyWord: keyword.value,
    workflowId: route.query.id
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

// 添加节点数据
function addData() {
  addModalRef.value.showModal((formData: FormData) => {
    return new Promise((resolve: any, reject: any) => {
      AddWorkflowDetailList({
        ...formData,
        workflowId: route.query.id
      })
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

// 编辑
function editData(data: any) {
  addModalRef.value.showModal((formData: FormData) => {
    return new Promise((resolve: any, reject: any) => {
      UpdateWorkflowDetailList(formData)
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

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm('确定删除该节点吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteWorkflowDetailList({
      workId: data.id
    })
      .then((res: any) => {
        ElMessage.success(res.msg)
        initData()
      })
      .catch((error: any) => {
        console.error(error)
      })
  })
}

// 查看作业详细配置
function showDetail(data: any) {
  router.push({
    name: 'work-item',
    query: {
      id: data.id,
      workflowId: route.query.id
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
  initData(true)
}

function handleCurrentChange(e: number) {
  tableConfig.pagination.currentPage = e
  initData(true)
}

onMounted(() => {
  initData()
})
</script>
