<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table">
    <div class="zqy-table-top">
      <el-button
        type="primary"
        @click="addData"
      >
        添加实时
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
            <ZStatusTag :status="scopeSlot.row.status === 'STOP' ? 'STOP_S' : scopeSlot.row.status"></ZStatusTag>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span @click="checkData(scopeSlot.row)">检测</span>
              <el-dropdown trigger="click">
                <span class="click-show-more">更多</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="scopeSlot.row.status !== 'NEW'" @click="showLog(scopeSlot.row)">
                      提交日志
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scopeSlot.row.status === 'RUNNING'" @click="showRunningLog(scopeSlot.row)">
                      运行日志
                    </el-dropdown-item>
                    <el-dropdown-item @click="editData(scopeSlot.row)">
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item @click="startComputing(scopeSlot.row)">
                      运行
                    </el-dropdown-item>
                    <el-dropdown-item @click="stopComputing(scopeSlot.row)">
                      停止
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
    <ShowLog ref="showLogRef" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'
import { BreadCrumbList, TableConfig, FormData } from './realtime-computing.config.ts'
import { SaveTimeComputingData, GetTimeComputingList, UpdateTimeComputingData, DeleteTimeComputingData, RunTimeComputingData, CheckComputingStatus, StopTimeComputingData } from '@/services/realtime-computing.service.ts'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import ShowLog from './show-log/index.vue'
import {Loading} from "@element-plus/icons-vue";

const router = useRouter()

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)
const timer = ref()
const showLogRef = ref(null)
const isRequest = ref(false)

function initData(tableLoading?: boolean, type?: string) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  isRequest.value = true
  GetTimeComputingList({
    page: tableConfig.pagination.currentPage - 1,
    pageSize: tableConfig.pagination.pageSize,
    searchKeyWord: keyword.value
  })
    .then((res: any) => {
      if (type) {
        res.data.content.forEach((item: any) => {
          tableConfig.tableData.forEach((col: any) => {
            if (item.id === col.id) {
              col.status = item.status
            }
          })
        })
      } else {
        tableConfig.tableData = res.data.content
        tableConfig.pagination.total = res.data.totalElements
      }
      loading.value = false
      tableConfig.loading = false
      networkError.value = false
      isRequest.value = false
    })
    .catch(() => {
      isRequest.value = false
      tableConfig.tableData = []
      tableConfig.pagination.total = 0
      loading.value = false
      tableConfig.loading = false
      networkError.value = true
      if (timer.value) {
        clearInterval(timer.value)
      }
      timer.value = null
    })
}

function addData() {
  addModalRef.value.showModal((formData: FormData) => {
    return new Promise((resolve: any, reject: any) => {
      SaveTimeComputingData(formData)
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
      UpdateTimeComputingData(formData)
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

// 停止实时计算
function stopComputing(data: any) {
  StopTimeComputingData({
    id: data.id
  }).then((res: any) => {
    initData()
    ElMessage.success(res.msg)
  }).catch(() => {
  })
}

// 运行实时计算
function startComputing(data: any) {
  RunTimeComputingData({
      id: data.id
  }).then((res: any) => {
      ElMessage.success(res.msg)
      initData()
  }).catch(() => {
  })
}

// 检测实时计算
function checkData(data: any) {
  CheckComputingStatus({
      id: data.id
  }).then((res: any) => {
      ElMessage.success(res.msg)
      initData()
  }).catch(() => {
  })
}

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm('确定删除该实时计算吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteTimeComputingData({
      id: data.id
    }).then((res: any) => {
      ElMessage.success(res.msg)
      initData()
    })
    .catch(() => {})
  })
}

function showDetail(data: any) {
  router.push({
    name: 'computing-detail',
    query: {
      id: data.id,
      name: data.name,
      status: data.status
    }
  })
}

// 展示日志
function showLog(data: any) {
  showLogRef.value.showModal(data.id)
}
// 展示日志
function showRunningLog(data: any) {
  showLogRef.value.showModal(data.id, 'runningLog')
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
  timer.value = setInterval(() => {
    !isRequest.value && initData(true, 'interval')
  }, 3000)
})
onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
  timer.value = null
})
</script>

<style lang="scss">
.zqy-seach-table {
  .name-click {
    cursor: pointer;
    color: getCssVar('color', 'primary', 'light-5');
    &:hover {
      color: getCssVar('color', 'primary');;
    }
  }
}
</style>
./realtime-computing.config