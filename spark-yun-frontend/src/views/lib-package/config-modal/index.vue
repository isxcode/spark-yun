<template>
  <BlockModal :model-config="modelConfig">
    <div class="config-lib-package">
      <div class="search-box">
        <el-input
          v-model="keyword"
          placeholder="请输入文件名 回车进行搜索"
          clearable
          @input="inputEvent"
          @keyup.enter="resetAndLoad"
        />
      </div>

      <div class="file-table" @scroll="handleScroll" ref="tableScrollRef" v-loading="tableConfig.loading">
        <div
          v-for="file in tableConfig.tableData"
          :key="file.id"
          class="file-item"
          @click="toggleFile(file)"
        >
          <el-checkbox
            :model-value="isSelected(file.id)"
            @change="handleCheckboxChange(file, $event)"
            @click.stop
          />
          <div class="file-info">
            <div class="file-name">{{ file.fileName }}</div>
            <div class="file-remark">{{ file.remark || '暂无备注' }}</div>
          </div>
        </div>

        <div v-if="tableConfig.tableData.length === 0 && !tableConfig.loading" class="empty-list">
          暂无数据
        </div>

        <div v-if="loadingMore" class="loading-more">
          加载中...
        </div>
      </div>

      <div class="selected-section">
        <div class="section-title">
          已选择的依赖文件
          <span class="count">({{ selectedFiles.length }})</span>
        </div>
        <div class="selected-list">
          <div v-if="selectedFiles.length === 0" class="empty-text">
            暂无选择的依赖文件，请从上方添加
          </div>
          <div v-else class="selected-items">
            <div v-for="file in selectedFiles" :key="file.id" class="selected-item">
              <span class="file-name">{{ file.fileName }}</span>
              <el-icon class="remove-icon" @click="removeFromSelected(file.id)">
                <Close />
              </el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Close } from '@element-plus/icons-vue'
import { GetFileCenterList } from '@/services/file-center.service'
import { ConfigLibPackage, GetLibPackage } from '@/services/lib-package.service'

const keyword = ref('')
const selectedFiles = ref([])
const currentLibPackageId = ref('')
const tableScrollRef = ref(null)
const loadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(0)

const modelConfig = reactive({
  title: '配置依赖包',
  visible: false,
  width: '900px',
  okConfig: {
    title: '保存',
    ok: okEvent,
    disabled: false,
    loading: false
  },
  cancelConfig: {
    title: '取消',
    cancel: closeEvent,
    disabled: false
  },
  needScale: false,
  zIndex: 1100,
  closeOnClickModal: false,
  customClass: 'config-lib-package-modal'
})

// 表格配置
const tableConfig: any = reactive({
  tableData: [],
  loading: false
})

function showModal(data: any): void {
  currentLibPackageId.value = data.id
  keyword.value = ''
  selectedFiles.value = []
  tableConfig.tableData = []
  currentPage.value = 0
  hasMore.value = true

  // 获取依赖包信息，加载已配置的依赖文件详情
  GetLibPackage({ id: data.id }).then((res: any) => {
    const fileIdList = res.data.fileIdList || []

    // 如果有已配置的依赖，获取文件详情
    if (fileIdList.length > 0) {
      GetFileCenterList({
        page: 0,
        pageSize: 10000,
        searchKeyWord: '',
        type: 'LIB'
      }).then((fileRes: any) => {
        const allFiles = fileRes.data.content || []
        selectedFiles.value = allFiles.filter((file: any) => fileIdList.includes(file.id))
      }).catch(() => {
        selectedFiles.value = []
      })
    }
  }).catch(() => {
    selectedFiles.value = []
  })

  // 加载文件列表
  loadFileList()

  modelConfig.visible = true
}

function loadFileList(isLoadMore = false) {
  if (!isLoadMore) {
    tableConfig.loading = true
  } else {
    loadingMore.value = true
  }

  GetFileCenterList({
    page: currentPage.value,
    pageSize: 6,
    searchKeyWord: keyword.value,
    type: 'LIB'
  }).then((res: any) => {
    const newData = res.data.content || []

    if (isLoadMore) {
      tableConfig.tableData = [...tableConfig.tableData, ...newData]
    } else {
      tableConfig.tableData = newData
    }

    // 判断是否还有更多数据
    hasMore.value = newData.length >= 6

    tableConfig.loading = false
    loadingMore.value = false
  }).catch(() => {
    if (!isLoadMore) {
      tableConfig.tableData = []
    }
    hasMore.value = false
    tableConfig.loading = false
    loadingMore.value = false
  })
}

function inputEvent(e: string) {
  if (e === '') {
    resetAndLoad()
  }
}

function resetAndLoad() {
  currentPage.value = 0
  hasMore.value = true
  tableConfig.tableData = []
  loadFileList()
}

// 滚动加载
function handleScroll(e: any) {
  const { scrollTop, scrollHeight, clientHeight } = e.target

  // 滚动到底部且还有更多数据且不在加载中
  if (scrollTop + clientHeight >= scrollHeight - 10 && hasMore.value && !loadingMore.value && !tableConfig.loading) {
    currentPage.value++
    loadFileList(true)
  }
}

// 判断文件是否已被选择
function isSelected(fileId: string): boolean {
  return selectedFiles.value.some((file: any) => file.id === fileId)
}

// 处理checkbox变化
function handleCheckboxChange(file: any, checked: boolean) {
  if (checked) {
    // 添加到已选择列表
    if (!isSelected(file.id)) {
      selectedFiles.value.push(file)
    }
  } else {
    // 从已选择列表移除
    selectedFiles.value = selectedFiles.value.filter((f: any) => f.id !== file.id)
  }
}

// 从已选择列表移除
function removeFromSelected(fileId: string) {
  selectedFiles.value = selectedFiles.value.filter((file: any) => file.id !== fileId)
}

// 切换文件选择状态
function toggleFile(file: any) {
  const selected = isSelected(file.id)
  handleCheckboxChange(file, !selected)
}

function okEvent() {
  modelConfig.okConfig.loading = true

  const fileIdList = selectedFiles.value.map((file: any) => file.id)

  ConfigLibPackage({
    id: currentLibPackageId.value,
    fileIdList: fileIdList
  }).then((res: any) => {
    ElMessage.success(res.msg)
    modelConfig.okConfig.loading = false
    modelConfig.visible = false
  }).catch((err: any) => {
    modelConfig.okConfig.loading = false
  })
}

function closeEvent() {
  modelConfig.visible = false
}

defineExpose({
  showModal
})
</script>

<style lang="scss" scoped>
.config-lib-package {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 600px;

  .search-box {
    margin-bottom: 12px;
  }

  .file-table {
    height: 300px;
    overflow-y: auto;
    margin-bottom: 16px;
    position: relative;
    border: 1px solid #e4e7ed;
    border-radius: 4px;

    .file-item {
      display: flex;
      align-items: center;
      padding: 10px 12px;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      transition: background-color 0.2s;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background-color: #f5f7fa;
      }

      .el-checkbox {
        margin-right: 12px;
      }

      .file-info {
        flex: 1;
        min-width: 0;

        .file-name {
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .file-remark {
          font-size: 12px;
          color: #909399;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

    .empty-list {
      text-align: center;
      padding: 60px 0;
      color: #909399;
      font-size: 14px;
    }

    .loading-more {
      text-align: center;
      padding: 8px;
      color: #909399;
      font-size: 13px;
    }
  }

  .selected-section {
    flex: 1;
    border-top: 1px solid #e4e7ed;
    padding-top: 12px;
    display: flex;
    flex-direction: column;
    min-height: 0;

    .section-title {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 10px;

      .count {
        color: #909399;
        font-weight: normal;
        font-size: 12px;
      }
    }

    .selected-list {
      flex: 1;
      overflow-y: auto;
      min-height: 0;

      .empty-text {
        text-align: center;
        color: #909399;
        padding: 20px 0;
        font-size: 13px;
      }

      .selected-items {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;

        .selected-item {
          display: inline-flex;
          align-items: center;
          padding: 4px 10px;
          background-color: #f4f4f5;
          border: 1px solid #e4e7ed;
          border-radius: 4px;
          font-size: 13px;

          .file-name {
            color: #606266;
          }

          .remove-icon {
            margin-left: 6px;
            cursor: pointer;
            color: #909399;
            font-size: 14px;

            &:hover {
              color: #f56c6c;
            }
          }
        }
      }
    }
  }
}

// 全局样式，禁用弹窗滚动
:deep(.config-lib-package-modal) {
  .el-dialog__body {
    overflow: hidden !important;
    padding: 0 !important;
  }
}
</style>

