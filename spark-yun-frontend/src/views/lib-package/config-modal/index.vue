<template>
  <BlockModal :model-config="modelConfig">
    <div class="config-lib-package">
      <div class="search-box">
        <el-input v-model="keyword" placeholder="请输入文件名搜索" clearable @input="searchFiles" />
      </div>
      <div class="file-list">
        <el-checkbox-group v-model="selectedFiles">
          <div v-for="file in fileList" :key="file.id" class="file-item">
            <el-checkbox :label="file.id">{{ file.fileName }}</el-checkbox>
          </div>
        </el-checkbox-group>
        <div v-if="fileList.length === 0" class="empty-text">
          暂无依赖文件
        </div>
      </div>
    </div>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { GetFileCenterList } from '@/services/file-center.service'
import { ConfigLibPackage, GetLibPackage } from '@/services/lib-package.service'

const keyword = ref('')
const fileList = ref([])
const selectedFiles = ref([])
const currentLibPackageId = ref('')

const modelConfig = reactive({
  title: '配置依赖包',
  visible: false,
  width: '600px',
  okConfig: {
    title: '确定',
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
  closeOnClickModal: false
})

function showModal(data: any): void {
  currentLibPackageId.value = data.id
  keyword.value = ''
  selectedFiles.value = []
  
  // 获取依赖包信息
  GetLibPackage({ id: data.id }).then((res: any) => {
    selectedFiles.value = res.data.fileIdList || []
  }).catch(() => {
    selectedFiles.value = []
  })
  
  // 加载文件列表
  loadFileList()
  
  modelConfig.visible = true
}

function loadFileList() {
  GetFileCenterList({
    page: 0,
    pageSize: 10000,
    searchKeyWord: keyword.value,
    type: 'LIB'
  }).then((res: any) => {
    fileList.value = res.data.content || []
  }).catch(() => {
    fileList.value = []
  })
}

function searchFiles() {
  loadFileList()
}

function okEvent() {
  modelConfig.okConfig.loading = true
  
  ConfigLibPackage({
    id: currentLibPackageId.value,
    fileIdList: selectedFiles.value
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
  
  .search-box {
    margin-bottom: 20px;
  }
  
  .file-list {
    max-height: 400px;
    overflow-y: auto;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 10px;
    
    .file-item {
      padding: 8px 0;
      border-bottom: 1px solid #f0f0f0;
      
      &:last-child {
        border-bottom: none;
      }
    }
    
    .empty-text {
      text-align: center;
      color: #909399;
      padding: 40px 0;
    }
  }
}
</style>

