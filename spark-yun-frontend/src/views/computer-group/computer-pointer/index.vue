<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table zqy-computer-node">
    <div class="zqy-table-top">
      <el-button type="primary" @click="addData">
        添加节点
      </el-button>
      <div class="zqy-seach">
        <el-input v-model="keyword" placeholder="请输入节点名称/地址/备注 回车进行搜索" :maxlength="200" clearable @input="inputEvent"
          @keyup.enter="initData(false)" />
        <el-button type="primary" @click="initData(false)">
          刷新
        </el-button>
      </div>
    </div>
    <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
      <div class="zqy-table">
        <BlockTable :table-config="tableConfig" @size-change="handleSizeChange" @current-change="handleCurrentChange">
          <template #statusTag="scopeSlot">
            <div class="btn-group">
              <el-tag v-if="scopeSlot.row.status === 'RUNNING'" class="ml-2" type="success">
                启动
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'NO_ACTIVE'" class="ml-2" type="danger">
                不可用
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'STOP'" class="ml-2" type="danger">
                待激活
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'UN_INSTALL'">
                未安装
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'CHECKING'">
                检测中
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'STARTING'">
                启动中
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'STOPPING'">
                停止中
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'INSTALLING'">
                安装中
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'REMOVING'">
                卸载中
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'UN_CHECK'">
                待检测
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'CHECK_ERROR'" class="ml-2" type="danger">
                检测失败
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'CAN_NOT_INSTALL'" class="ml-2" type="danger">
                不可安装
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'CAN_INSTALL'" class="ml-2" type="success">
                可安装
              </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'INSTALL_ERROR'" class="ml-2" type="danger">
                安装失败
              </el-tag>
            </div>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span @click="showLog(scopeSlot.row)">日志</span>
              <el-dropdown trigger="click">
                <span class="click-show-more">更多</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="editNodeData(scopeSlot.row)">
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scopeSlot.row.status === 'RUNNING'" @click="stopAgent(scopeSlot.row)">
                      停止
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scopeSlot.row.status === 'STOP'" @click="startAgent(scopeSlot.row)">
                      激活
                    </el-dropdown-item>
                    <el-dropdown-item @click="checkData(scopeSlot.row)">
                      检测
                    </el-dropdown-item>
                    <el-dropdown-item @click="installData(scopeSlot.row)">
                      安装
                    </el-dropdown-item>
                    <el-dropdown-item @click="uninstallData(scopeSlot.row)">
                      卸载
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
import ShowLog from './show-log/index.vue'

import { PointTableConfig, FormData } from '../computer-group.config'
import { GetComputerPointData, CheckComputerPointData, AddComputerPointData, InstallComputerPointData, UninstallComputerPointData, DeleteComputerPointData, StopComputerPointData, StartComputerPointData, EditComputerPointData } from '@/services/computer-group.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'

const route = useRoute()
const breadCrumbList = reactive([
  {
    name: '计算集群',
    code: 'computer-group'
  },
  {
    name: '节点',
    code: 'computer-pointer'
  }
])
const tableConfig: any = reactive(PointTableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)
const showLogRef = ref(null)
const timer = ref(null)

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  GetComputerPointData({
    page: tableConfig.pagination.currentPage - 1,
    pageSize: tableConfig.pagination.pageSize,
    searchKeyWord: keyword.value,
    clusterId: route.query.id
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
      AddComputerPointData({
        ...formData,
        clusterId: route.query.id
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

// 编辑节点数据
function editNodeData(data: any) {
  addModalRef.value.showModal((formData: FormData) => {
    return new Promise((resolve: any, reject: any) => {
      EditComputerPointData({
        ...formData,
        clusterId: route.query.id
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
  }, data)
}

// 查看日志
function showLog(e: any) {
  showLogRef.value.showModal(e.agentLog)
}

// 停止
function stopAgent(data: any) {
  data.stopAgentLoading = true
  StopComputerPointData({
    engineNodeId: data.id
  }).then((res: any) => {
    ElMessage.success(res.msg)
    data.stopAgentLoading = false
    initData(true)
  }).catch(() => {
    data.stopAgentLoading = false
  })
}

// 停止
function startAgent(data: any) {
  data.stopAgentLoading = true
  StartComputerPointData({
    engineNodeId: data.id
  }).then((res: any) => {
    ElMessage.success(res.msg)
    data.stopAgentLoading = false
    initData(true)
  }).catch(() => {
    data.stopAgentLoading = false
  })
}

// 安装
function installData(data: any) {
  data.installLoading = true
  InstallComputerPointData({
    engineNodeId: data.id
  })
    .then((res: any) => {
      ElMessage.success(res.msg)
      data.installLoading = false
      initData(true)
    })
    .catch(() => {
      data.installLoading = false
    })
}

// 卸载
function uninstallData(data: any) {
  data.uninstallLoading = true
  UninstallComputerPointData({
    engineNodeId: data.id
  })
    .then((res: any) => {
      ElMessage.success(res.msg)
      data.uninstallLoading = false
      initData(true)
    })
    .catch(() => {
      data.uninstallLoading = false
    })
}

// 检测
function checkData(data: any) {
  data.checkLoading = true
  CheckComputerPointData({
    engineNodeId: data.id
  })
    .then((res: any) => {
      data.checkLoading = false
      ElMessage.success(res.msg)
      initData(true)
    })
    .catch(() => {
      data.checkLoading = false
    })
}

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm('确定删除该节点吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteComputerPointData({
      engineNodeId: data.id
    })
      .then((res: any) => {
        ElMessage.success(res.msg)
        initData()
      })
      .catch(() => { })
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
  timer.value = setInterval(() => {
    initData(true)
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
  .click-show-more {
    font-size: getCssVar('font-size', 'extra-small');
  }

  &.zqy-computer-node {
    .zqy-seach {
      display: flex;
      align-items: center;
      .el-button {
        margin-left: 12px;
      }
    }
  }
}
</style>
