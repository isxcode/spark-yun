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
      </div>
    </div>
    <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
      <div class="zqy-table">
        <BlockTable :table-config="tableConfig" @size-change="handleSizeChange" @current-change="handleCurrentChange">
          <template #statusTag="scopeSlot">
            <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
          </template>
          <template #defaultNodeTag="scopeSlot">
            <div class="btn-group">
              <el-tag v-if="scopeSlot.row.defaultClusterNode" class="ml-2" type="success">
                是
              </el-tag>
              <el-tag v-if="!scopeSlot.row.defaultClusterNode" class="ml-2" type="danger">
                否
              </el-tag>
            </div>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span @click="checkData(scopeSlot.row)">检测</span>
              <el-dropdown trigger="click">
                <span class="click-show-more">更多</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="editNodeData(scopeSlot.row)">
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item @click="showLog(scopeSlot.row)">
                      日志
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scopeSlot.row.status === 'RUNNING'" @click="stopAgent(scopeSlot.row)">
                      停止
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scopeSlot.row.status === 'STOP'" @click="startAgent(scopeSlot.row)">
                      激活
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scopeSlot.row.status === 'UN_INSTALL'|| scopeSlot.row.status === 'INSTALL_ERROR'" @click="installData(scopeSlot.row)">
                      安装
                    </el-dropdown-item>
                    <el-dropdown-item @click="uninstallData(scopeSlot.row)">
                      卸载
                    </el-dropdown-item>
                    <el-dropdown-item @click="cleanData(scopeSlot.row)">
                      清理
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
import {
  GetComputerPointData,
  CheckComputerPointData,
  AddComputerPointData,
  InstallComputerPointData,
  UninstallComputerPointData,
  DeleteComputerPointData,
  StopComputerPointData,
  StartComputerPointData,
  EditComputerPointData,
  CleanComputerPointData,
SetDefaultComputerPointNode
} from '@/services/computer-group.service'
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
const timer = ref()

function initData(tableLoading?: boolean, type?: string) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  GetComputerPointData({
    page: tableConfig.pagination.currentPage - 1,
    pageSize: tableConfig.pagination.pageSize,
    searchKeyWord: keyword.value,
    clusterId: route.query.id
  })
    .then((res: any) => {
      if (type) {
        res.data.content.forEach((item: any) => {
          tableConfig.tableData.forEach((col: any) => {
            if (item.id === col.id) {
              col.status = item.status
              col.cpu=item.cpu
              col.memory=item.memory
              col.storage=item.storage
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
  showLogRef.value.showModal(e.id, 'cluster')
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

// 清理
function cleanData(data: any) {
  data.cleanLoading = true
  CleanComputerPointData({
    engineNodeId: data.id
  })
    .then((res: any) => {
      ElMessage.success(res.msg)
      data.cleanLoading = false
      initData(true)
    })
    .catch(() => {
      data.cleanLoading = false
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

// 设置默认节点
function setDefaultNode(data: any) {
  SetDefaultComputerPointNode({
    clusterNodeId: data.id
  }).then((res: any) => {
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
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initData()
  timer.value = setInterval(() => {
    initData(true, 'interval')
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
