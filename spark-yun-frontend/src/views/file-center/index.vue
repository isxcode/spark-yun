<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table">
    <div class="zqy-table-top">
      <el-button
        type="primary"
        @click="addData"
      >
        上传资源
      </el-button>
      <div class="zqy-tenant__select">
        <el-select
          v-model="type"
          clearable
          placeholder="请选择类型进行搜索"
          @change="initData(false)"
        >
          <el-option
            v-for="item in typeList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>
      <div class="zqy-seach">
        <el-input
          v-model="keyword"
          placeholder="请输入备注 回车进行搜索"
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
          <template #options="scopeSlot">
            <div class="btn-group">
              <span
                v-if="!scopeSlot.row.downloadLoading"
                @click="downloadFile(scopeSlot.row, true)"
              >下载</span>
              <el-icon v-else class="is-loading">
                <Loading />
              </el-icon>
              <el-dropdown trigger="click">
                <span class="click-show-more">更多</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="editData(scopeSlot.row)">
                      备注
                    </el-dropdown-item>
                    <el-dropdown-item @click="deleteData(scopeSlot.row)">
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
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

import { BreadCrumbList, TableConfig } from './file-center.config'
import { GetFileCenterList, UploadFileData, DeleteFileData, DownloadFileData, UpdateFileData } from '@/services/file-center.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const type = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref<any>(null)
const typeList = ref([
  {
    label: '作业',
    value: 'JOB',
  },
  {
    label: '函数',
    value: 'FUNC',
  },
  {
    label: '依赖',
    value: 'LIB',
  },
  {
    label: 'Excel',
    value: 'EXCEL',
  }
])

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  GetFileCenterList({
    page: tableConfig.pagination.currentPage - 1,
    pageSize: tableConfig.pagination.pageSize,
    searchKeyWord: keyword.value,
    type: type.value
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
  addModalRef.value.showModal((data: any) => {
    return new Promise((resolve: any, reject: any) => {
      const formData = new FormData()
      formData.append('type', data.type)
      formData.append('remark', data.remark)
      formData.append('file', data.fileData)
      UploadFileData(formData).then((res: any) => {
        ElMessage.success(res.data.msg)
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
      UpdateFileData({
        fileId: formData.id,
        remark: formData.remark
      }).then((res: any) => {
        ElMessage.success(res.msg)
        initData()
        resolve()
      }).catch((error: any) => {
        reject(error)
      })
    })
  }, data)
}

// 下载
function downloadFile(data: any) {
  data.downloadLoading = true
  DownloadFileData({
    fileId: data.id
  }).then((res: any) => {
    const blobURL = URL.createObjectURL(res);

    // 创建一个链接元素并模拟点击下载
    const link = document.createElement('a');
    link.href = blobURL;
    link.download = data.fileName; // 根据实际情况设置下载文件的名称和扩展名
    link.click();

    // 释放Blob URL
    URL.revokeObjectURL(blobURL);


    data.downloadLoading = false
  }).catch(() => {
    data.downloadLoading = false
  })
}

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm('确定删除该文件吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteFileData({
      fileId: data.id
    }).then((res: any) => {
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
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initData()
})
</script>
