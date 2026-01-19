<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table">
    <div class="zqy-table-top">
      <el-button type="primary" @click="addData">
        添加依赖包
      </el-button>
      <div class="zqy-seach">
        <el-input v-model="keyword" placeholder="请输入名称 回车进行搜索" :maxlength="200" clearable @input="inputEvent"
          @keyup.enter="initData(false)" />
      </div>
    </div>
    <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
      <div class="zqy-table">
        <BlockTable :table-config="tableConfig" @size-change="handleSizeChange" @current-change="handleCurrentChange">
          <template #name="scopeSlot">
            <span class="name-click" @click="configData(scopeSlot.row)">
              {{ scopeSlot.row.name }}
            </span>
          </template>
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
    <ConfigModal ref="configModalRef" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'
import ConfigModal from './config-modal/index.vue'

import { BreadCrumbList, TableConfig } from './lib-package.config'
import { PageLibPackage, AddLibPackage, UpdateLibPackage, DeleteLibPackage, GetLibPackage } from '@/services/lib-package.service'
import { ElMessage, ElMessageBox } from 'element-plus'

const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)
const configModalRef = ref(null)

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)

function initData(tableLoading = true) {
  loading.value = true
  tableConfig.loading = tableLoading

  PageLibPackage({
    page: tableConfig.pagination.currentPage - 1,
    pageSize: tableConfig.pagination.pageSize,
    searchKeyWord: keyword.value
  }).then((res: any) => {
    tableConfig.tableData = res.data.content
    tableConfig.pagination.total = res.data.totalElements
    loading.value = false
    tableConfig.loading = false
    networkError.value = false
  }).catch(() => {
    tableConfig.tableData = []
    tableConfig.pagination.total = 0
    loading.value = false
    tableConfig.loading = false
    networkError.value = true
  })
}

function addData() {
  addModalRef.value.showModal((data: any) => {
    return new Promise((resolve: any, reject: any) => {
      AddLibPackage(data).then((res: any) => {
        ElMessage.success(res.msg)
        initData()
        resolve()
      }).catch((error: any) => {
        reject(error)
      })
    })
  })
}

function editData(data: any) {
  addModalRef.value.showModal((formData: any) => {
    return new Promise((resolve: any, reject: any) => {
      UpdateLibPackage(formData).then((res: any) => {
        ElMessage.success(res.msg)
        initData()
        resolve()
      }).catch((error: any) => {
        reject(error)
      })
    })
  }, data)
}

function configData(data: any) {
  configModalRef.value.showModal(data)
}

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm('确定删除该数据吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteLibPackage({
      id: data.id
    }).then((res: any) => {
      ElMessage.success(res.msg)
      initData()
    }).catch(() => { })
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
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initData()
})
</script>

<style lang="scss" scoped>
.name-click {
  cursor: pointer;
  font-weight: bold;
  color: getCssVar('color', 'primary', 'light-3');

  &:hover {
    color: getCssVar('color', 'primary');
    text-decoration: underline;
  }
}
</style>

