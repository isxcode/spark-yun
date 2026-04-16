<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table">
    <div class="zqy-table-top">
      <el-button
        type="primary"
        @click="addGroup"
      >
        添加集群
      </el-button>
      <div class="zqy-seach">
        <el-input
          v-model="keyword"
          placeholder="请输入集群名称/备注 回车进行搜索"
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
          <template #memorySlot="scopeSlot">
            <div class="resource-progress">
              <el-progress
                :percentage="getPercentFromRatio(scopeSlot.row.memory)"
                :color="getPercentColor()"
                :stroke-width="12"
                :show-text="false"
              />
              <span class="resource-progress__value">{{ getDisplayValue(scopeSlot.row.memory) }}</span>
            </div>
          </template>
          <template #storageSlot="scopeSlot">
            <div class="resource-progress">
              <el-progress
                :percentage="getPercentFromRatio(scopeSlot.row.storage)"
                :color="getPercentColor()"
                :stroke-width="12"
                :show-text="false"
              />
              <span class="resource-progress__value">{{ getDisplayValue(scopeSlot.row.storage) }}</span>
            </div>
          </template>
          <template #statusTag="scopeSlot">
            <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
          </template>
          <template #defaultTag="scopeSlot">
            <div class="btn-group">
              <el-tag v-if="scopeSlot.row.defaultCluster" class="ml-2" type="success">
                是
              </el-tag>
              <el-tag v-if="!scopeSlot.row.defaultCluster" class="ml-2" type="danger">
                否
              </el-tag>
            </div>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span v-if="!scopeSlot.row.checkLoading" @click="checkData(scopeSlot.row)">检测</span>
              <el-icon
                  v-else
                  class="is-loading"
              >
                <Loading />
              </el-icon>
              <el-dropdown trigger="click">
                <span class="click-show-more">更多</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="editData(scopeSlot.row)">
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item @click="setDefaultNode(scopeSlot.row)">
                      默认
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

import { BreadCrumbList, TableConfig, FormData } from './computer-group.config'
import { GetComputerGroupList, AddComputerGroupData, UpdateComputerGroupData, CheckComputerGroupData, DeleteComputerGroupData, SetDefaultComputerGroup } from '@/services/computer-group.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { Loading } from '@element-plus/icons-vue'

const router = useRouter()
const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)

function normalizePercent(value: number): number {
  if (!Number.isFinite(value)) {
    return 0
  }
  return Math.max(0, Math.min(100, Math.round(value)))
}

function getPercentFromRatio(valueText: string): number {
  if (!valueText) {
    return 0
  }
  const ratioMatch = String(valueText)
    .replace(/\s/g, '')
    .match(/^([\d.]+)[a-zA-Z]*\/([\d.]+)[a-zA-Z]*$/)
  if (!ratioMatch) {
    return 0
  }
  const used = Number(ratioMatch[1])
  const total = Number(ratioMatch[2])
  if (!Number.isFinite(used) || !Number.isFinite(total) || total <= 0) {
    return 0
  }
  return normalizePercent((used / total) * 100)
}

function getPercentColor(): string {
  return 'var(--el-color-primary-light-3)'
}

function getDisplayValue(valueText: string): string {
  if (!valueText) {
    return '--'
  }
  return String(valueText)
}

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  GetComputerGroupList({
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
      AddComputerGroupData(formData)
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
      UpdateComputerGroupData(formData)
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

// 检测
function checkData(data: any) {
  data.checkLoading = true
  CheckComputerGroupData({
    engineId: data.id
  })
    .then((res: any) => {
      data.checkLoading = false
      ElMessage.success(res.msg)
      initData()
    })
    .catch(() => {
      data.checkLoading = false
    })
}

// 查看节点
function showPointDetail(data: any) {
  router.push({
    name: 'computer-pointer',
    query: {
      id: data.id
    }
  })
}

// 设置默认节点
function setDefaultNode(data: any) {
  SetDefaultComputerGroup({
    clusterId: data.id
  }).then((res: any) => {
    ElMessage.success(res.msg)
    initData(true)
  })
  .catch(() => {
  })
}

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm('确定删除该集群吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteComputerGroupData({
      engineId: data.id
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
    name: 'computer-pointer',
    query: {
      id: data.id,
      type: data.clusterType
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
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initData()
})
</script>

<style lang="scss">
.zqy-seach-table {
  .resource-progress {
    min-width: 120px;
    padding-right: 6px;
    display: flex;
    align-items: center;
    gap: 8px;

    .el-progress {
      flex: 1;
    }
  }

  .resource-progress__value {
    color: getCssVar('text-color', 'secondary');
    white-space: nowrap;
    font-size: getCssVar('font-size', 'extra-small');
  }
}
</style>
