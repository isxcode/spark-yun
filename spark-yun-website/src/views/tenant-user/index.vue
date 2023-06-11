<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table">
    <div class="zqy-table-top">
      <el-button
        type="primary"
        @click="addData"
      >
        添加成员
      </el-button>
      <div class="zqy-seach">
        <el-input
          v-model="keyword"
          placeholder="请输入用户名/手机号/邮箱 回车进行搜索"
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
          <template #roleCode="scopeSlot">
            <div class="btn-group">
              <el-tag
                v-if="scopeSlot.row.roleCode === 'ROLE_TENANT_ADMIN'"
                class="ml-2"
                type="success"
              >
                管理员
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.roleCode === 'ROLE_TENANT_MEMBER'"
                type="info"
              >
                成员
              </el-tag>
            </div>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <template v-if="scopeSlot.row.roleCode === 'ROLE_TENANT_MEMBER'">
                <span v-if="!scopeSlot.row.authLoading" @click="giveAuth(scopeSlot.row)">管理授权</span>
                <el-icon v-else class="is-loading">
                  <Loading />
                </el-icon>
              </template>
              <template v-else>
                <span
                  v-if="!scopeSlot.row.authLoading"
                  @click="removeAuth(scopeSlot.row)"
                >取消授权</span>
                <el-icon
                  v-else
                  class="is-loading"
                >
                  <Loading />
                </el-icon>
              </template>
              <span @click="deleteData(scopeSlot.row)">移除</span>
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

import { BreadCrumbList, TableConfig } from './tenant-user.config'
import { GetUserList, AddTenantUserData, DeleteTenantUser, GiveAuth, RemoveAuth } from '@/services/tenant-user.service'
import { ElMessage, ElMessageBox } from 'element-plus'

interface FormUser {
  tenantAdmin: boolean;
  userId: string;
}

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  GetUserList({
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

function addData() {
  addModalRef.value.showModal((formData: FormUser) => {
    return new Promise((resolve: any, reject: any) => {
      AddTenantUserData(formData)
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

// 授权
function giveAuth(data: any) {
  data.authLoading = true
  GiveAuth({
    tenantUserId: data.id
  })
    .then((res: any) => {
      data.authLoading = false
      ElMessage.success(res.msg)
      initData(true)
    })
    .catch(() => {
      data.authLoading = false
    })
}

// 取消授权
function removeAuth(data: any) {
  data.authLoading = true
  RemoveAuth({
    tenantUserId: data.id
  })
    .then((res: any) => {
      data.authLoading = false
      ElMessage.success(res.msg)
      initData(true)
    })
    .catch(() => {
      data.authLoading = false
    })
}

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm("确定移除该成员吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    DeleteTenantUser({
      tenantUserId: data.id
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
