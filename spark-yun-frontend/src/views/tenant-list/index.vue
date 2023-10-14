<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table">
    <div class="zqy-table-top">
      <el-button
        type="primary"
        @click="addData"
      >
        添加租户
      </el-button>
      <div class="zqy-seach">
        <el-input
          v-model="keyword"
          placeholder="请输入租户名 回车进行搜索"
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
      @loading-refresh="initData(true)"
    >
      <div class="zqy-table">
        <BlockTable
          :table-config="tableConfig"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
          <template #statusTag="scopeSlot">
            <div class="btn-group">
              <el-tag
                v-if="scopeSlot.row.status === 'ENABLE'"
                class="ml-2"
                type="success"
              >
                启用
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'DISABLE'"
                class="ml-2"
                type="danger"
              >
                禁用
              </el-tag>
            </div>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span @click="editData(scopeSlot.row)">编辑</span>
              <span
                v-if="!scopeSlot.row.checkLoding"
                @click="checkTenant(scopeSlot.row)"
              >检测</span>
              <el-icon
                v-else
                class="is-loading"
              >
                <Loading />
              </el-icon>
              <template v-if="scopeSlot.row.status === 'ENABLE'">
                <span
                  v-if="!scopeSlot.row.statusLoading"
                  @click="changeStatus(scopeSlot.row, false)"
                >禁用</span>
                <el-icon
                  v-else
                  class="is-loading"
                >
                  <Loading />
                </el-icon>
              </template>
              <template v-else>
                <span
                  v-if="!scopeSlot.row.statusLoading"
                  @click="changeStatus(scopeSlot.row, true)"
                >启用</span>
                <el-icon
                  v-else
                  class="is-loading"
                >
                  <Loading />
                </el-icon>
              </template>
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
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'

import { BreadCrumbList, TableConfig } from './tenant-list.config'
import { GetTenantList, AddTenantData, DeleteTenantData, CheckTenantData, DisableTenantData, EnableTenantData, UpdateTenantData } from '@/services/tenant-list.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import eventBus from '@/utils/eventBus'
import { useAuthStore } from '@/store/useAuth'
// import { useState, useMutations } from '@/hooks/useStore'

interface FormTenant {
  adminUserId: string;
  maxMemberNum: string;
  maxWorkflowNum: string;
  name: string;
  remark: string;
}

const authStore = useAuthStore()

// const state = useState(['tenantId'], 'authStoreModule')
// const mutations = useMutations(['setTenantId'], 'authStoreModule')

const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)
const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  GetTenantList({
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
      if (!authStore.tenantId && tableConfig.tableData.length === 1) {
        authStore.setTenantId(tableConfig.tableData[0].id)
      }
    })
    .catch(() => {
      tableConfig.tableData = []
      tableConfig.pagination.total = 0
      loading.value = false
      tableConfig.loading = false
      networkError.value = true
    })
}

function addData() {
  addModalRef.value.showModal((formData: FormTenant) => {
    return new Promise((resolve: any, reject: any) => {
      AddTenantData(formData)
        .then((res: any) => {
          ElMessage.success(res.msg)
          // 这里发送eventbus，刷新当前打开的页面
          eventBus.emit('tenantListUpdate')

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
  addModalRef.value.showModal((formData: FormUser) => {
    return new Promise((resolve: any, reject: any) => {
      UpdateTenantData(formData)
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

function checkTenant(data: any) {
  data.checkLoding = true
  CheckTenantData({
    tenantId: data.id
  })
    .then((res: any) => {
      data.checkLoding = false
      ElMessage.success(res.msg)
      initData(true)
    })
    .catch(() => {
      data.checkLoding = false
    })
}

// 启用 or 禁用
function changeStatus(data: any, status: boolean) {
  data.statusLoading = true
  if (status) {
    EnableTenantData({
      tenantId: data.id
    })
      .then((res: any) => {
        ElMessage.success(res.msg)
        data.statusLoading = false
        initData(true)
      })
      .catch(() => {
        data.statusLoading = false
      })
  } else {
    DisableTenantData({
      tenantId: data.id
    })
      .then((res: any) => {
        ElMessage.success(res.msg)
        data.statusLoading = false
        initData(true)
      })
      .catch(() => {
        data.statusLoading = false
      })
  }
}

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm('确定删除该租户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteTenantData({
      tenantId: data.id
    })
      .then((res: any) => {
        ElMessage.success(res.msg)
        initData()
      })
      .catch(() => {})
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
