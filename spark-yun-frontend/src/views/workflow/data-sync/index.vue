<template>
  <div class="data-sync-page">
    <div class="data-sync__option-container">
      <div class="btn-box" @click="goBack">
        <el-icon>
          <RefreshLeft/>
        </el-icon>
        <span class="btn-text">返回</span>
      </div>
      <div class="btn-box" @click="saveData">
        <el-icon>
          <Finished/>
        </el-icon>
        <span class="btn-text">保存</span>
      </div>
      <div class="btn-box" @click="setConfigData">
        <el-icon>
          <Setting/>
        </el-icon>
        <span class="btn-text">配置</span>
      </div>
      <div class="btn-box" @click="locationNode">
        <el-icon>
          <RefreshLeft/>
        </el-icon>
        <span class="btn-text">定位</span>
      </div>
    </div>
    <div class="data-sync" id="data-sync">
      <div class="data-sync-top">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>数据来源</span>
            </div>
          </template>
          <el-form ref="form" label-position="left" label-width="70px" :model="formData" :rules="rules">
            <el-form-item prop="sourceDBType" label="类型">
              <el-select v-model="formData.sourceDBType" clearable placeholder="请选择" @change="dbTypeChange('source')">
                <el-option v-for="item in typeList" :key="item.value" :label="item.label"
                           :value="item.value"/>
              </el-select>
            </el-form-item>
            <el-form-item prop="sourceDBId" label="数据源">
              <el-select v-model="formData.sourceDBId" clearable placeholder="请选择"
                         @visible-change="getDataSource($event, formData.sourceDBType, 'source')"
                         @change="dbIdChange('source')">
                <el-option v-for="item in sourceList" :key="item.value" :label="item.label"
                           :value="item.value"/>
              </el-select>
            </el-form-item>
            <el-form-item prop="sourceTable" label="表">
              <el-select v-model="formData.sourceTable" clearable placeholder="请选择"
                         @visible-change="getDataSourceTable($event, formData.sourceDBId, 'source')"
                         @change="tableChangeEvent($event, formData.sourceDBId, 'source')"
              >
                <el-option v-for="item in sourceTablesList" :key="item.value" :label="item.label"
                           :value="item.value"/>
              </el-select>
              <el-button type="primary" link @click="showTableDetail">数据预览</el-button>
            </el-form-item>
            <el-form-item label="分区键">
              <el-select v-model="formData.partitionColumn" clearable placeholder="请选择"
                @visible-change="getTableColumnData($event, formData.sourceDBId, formData.sourceTable)">
                <el-option v-for="item in partKeyList" :key="item.value" :label="item.label"
                           :value="item.value"/>
              </el-select>
            </el-form-item>
            <el-form-item prop="queryCondition" label="过滤条件">
              <code-mirror v-model="formData.queryCondition" basic :lang="lang"/>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>数据去向</span>
            </div>
          </template>
          <el-form ref="form" label-position="left" label-width="70px" :model="formData" :rules="rules">
            <el-form-item prop="targetDBType" label="类型">
              <el-select v-model="formData.targetDBType" clearable placeholder="请选择" @change="dbTypeChange('target')">
                <el-option v-for="item in typeList" :key="item.value" :label="item.label"
                           :value="item.value"/>
              </el-select>
            </el-form-item>
            <el-form-item prop="targetDBId" label="数据源">
              <el-select v-model="formData.targetDBId" clearable placeholder="请选择"
                         @visible-change="getDataSource($event, formData.targetDBType, 'target')"
                         @change="dbIdChange('target')">
                <el-option v-for="item in targetList" :key="item.value" :label="item.label"
                           :value="item.value"/>
              </el-select>
            </el-form-item>
            <el-form-item prop="targetTable" label="表">
              <el-select v-model="formData.targetTable" clearable placeholder="请选择"
                         @visible-change="getDataSourceTable($event, formData.targetDBId, 'target')"
                         @change="tableChangeEvent($event, formData.targetDBId, 'target')"
              >
                <el-option v-for="item in targetTablesList" :key="item.value" :label="item.label"
                           :value="item.value"/>
              </el-select>
              <el-button type="primary" link @click="createTableWork">生成建表作业</el-button>
            </el-form-item>
            <el-form-item prop="overMode" label="写入模式">
              <el-select v-model="formData.overMode" clearable placeholder="请选择">
                <el-option v-for="item in overModeList" :key="item.value" :label="item.label"
                           :value="item.value"/>
              </el-select>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
      <data-sync-table ref="dataSyncTableRef" :formData="formData"></data-sync-table>
    </div>
    <!-- 数据预览 -->
    <table-detail ref="tableDetailRef"></table-detail>
    <!-- 配置 -->
    <config-detail ref="configDetailRef"></config-detail>
  </div>
</template>

<script lang="ts" setup>
import {ref, reactive, onMounted, defineProps, nextTick} from 'vue'
import {ElMessage, FormInstance, FormRules} from 'element-plus'
import CodeMirror from 'vue-codemirror6'
import {sql} from '@codemirror/lang-sql'
import {DataSourceType, OverModeList} from './data.config.ts'
import {GetDatasourceList} from '@/services/datasource.service'
import {CreateTableWork, GetDataSourceTables, GetTableColumnsByTableId, SaveDataSync} from '@/services/data-sync.service'
import TableDetail from './table-detail/index.vue'
import DataSyncTable from './data-sync-table/index.vue'
import ConfigDetail from '../workflow-page/config-detail/index.vue'
import { GetWorkItemConfig, SaveWorkItemConfig } from '@/services/workflow.service'

interface Option {
  label: string
  value: string
}

const props = defineProps<{
  workItemConfig: any
}>()

const emit = defineEmits(['back', 'locationNode'])

const configDetailRef = ref()
const form = ref<FormInstance>()
const tableDetailRef = ref()
const dataSyncTableRef = ref()
const lang = ref<any>(sql())
const sourceList = ref<Option[]>([])
const targetList = ref<Option[]>([])
const sourceTablesList = ref<Option[]>([])
const targetTablesList = ref<Option[]>([])
const overModeList = ref<Option[]>(OverModeList)
const partKeyList = ref<Option[]>([])       // 分区键

const typeList = ref(DataSourceType);
const formData = reactive({
  workId: '',     // 作业id
  sourceDBType: '',     // 来源数据源类型
  sourceDBId: '',       // 来源数据源
  sourceTable: '',      // 来源数据库表名
  queryCondition: '',   // 来源数据库查询条件
  partitionColumn: '',  // 分区键

  targetDBType: '',     // 目标数据库类型
  targetDBId: '',       // 目标数据源
  targetTable: '',      // 目标数据库表名
  overMode: '',         // 写入模式
})
const rules = reactive<FormRules>({
  // sourceDBType: [
  //     {
  //         required: true,
  //         message: '请选择数据源类型',
  //         trigger: ['blur', 'change']
  //     }
  // ]
})

// 保存数据
function saveData() {
    SaveWorkItemConfig({
        workId: formData.workId,
        syncWorkConfig: {
            ...formData,
            sourceTableColumn: dataSyncTableRef.value.getSourceTableColumn(),
            targetTableColumn: dataSyncTableRef.value.getTargetTableColumn(),
            columnMap: dataSyncTableRef.value.getConnect()
        }
    }).then((res: any) => {
        ElMessage.success('保存成功')
    }).catch(err => {
      console.error(err)
    })
}

function getDate() {
    GetWorkItemConfig({
        workId: props.workItemConfig.id
    }).then((res: any) => {
        formData.sourceDBType = res.data.syncWorkConfig.sourceDBType
        formData.sourceDBId = res.data.syncWorkConfig.sourceDBId
        formData.sourceTable = res.data.syncWorkConfig.sourceTable
        formData.queryCondition = res.data.syncWorkConfig.queryCondition
        formData.targetDBType = res.data.syncWorkConfig.targetDBType
        formData.targetDBId = res.data.syncWorkConfig.targetDBId
        formData.targetTable = res.data.syncWorkConfig.targetTable
        formData.overMode = res.data.syncWorkConfig.overMode

        nextTick(() => {
            getDataSource(true, formData.sourceDBType, 'source')
            getDataSource(true, formData.targetDBType, 'target')
            getDataSourceTable(true, formData.sourceDBId, 'source')
            getDataSourceTable(true, formData.targetDBId, 'target')

            dataSyncTableRef.value.initPageData(res.data.syncWorkConfig)
        })
    }).catch(err => {
      console.error(err)
    })
}

// 获取数据源
function getDataSource(e: boolean, sourceType: string, type: string) {
  if (e && sourceType) {
    let options = []
    GetDatasourceList({
      page: 0,
      pageSize: 10000,
      searchKeyWord: sourceType || ''
    }).then((res: any) => {
      options = res.data.content.map((item: any) => {
        return {
          label: item.name,
          value: item.id
        }
      })
      type === 'source' ? sourceList.value = options : targetList.value = options
    }).catch(err => {
      console.error(err)
      type === 'source' ? sourceList.value = [] : targetList.value = []
    })
  } else {
    type === 'source' ? sourceList.value = [] : targetList.value = []
  }
}

// 获取数据源表
function getDataSourceTable(e: boolean, dataSourceId: string, type: string) {
  if (e && dataSourceId) {
    let options = []
    GetDataSourceTables({
      dataSourceId: dataSourceId,
      tablePattern: ""
    }).then((res: any) => {
      options = res.data.tables.map((item: any) => {
        return {
          label: item,
          value: item
        }
      })
      type === 'source' ? sourceTablesList.value = options : targetTablesList.value = options
    }).catch(err => {
      console.error(err)
      type === 'source' ? sourceTablesList.value = [] : targetTablesList.value = []
    })
  } else {
    type === 'source' ? sourceTablesList.value = [] : targetTablesList.value = []
  }
}

// 数据预览
function showTableDetail(): void {
  if (formData.sourceDBId && formData.sourceTable) {
    tableDetailRef.value.showModal({
      dataSourceId: formData.sourceDBId,
      tableName: formData.sourceTable
    })
  } else {
    ElMessage.warning('请选择数据源和表')
  }
}

// 生成建表作业
function createTableWork() {
  CreateTableWork({
    dataSourceId: formData.sourceDBId,
    tableName: formData.sourceTable
  }).then((res: any) => {
    ElMessage.success('操作成功')
  }).catch(err => {
    console.error(err)
  })
}

// 分区键
function getTableColumnData(e: boolean, dataSourceId: string, tableName: string) {
    if (e && dataSourceId && tableName) {
        GetTableColumnsByTableId({
            dataSourceId: dataSourceId,
            tableName: tableName
        }).then((res: any) => {
            partKeyList.value = (res.data.columns || []).map((column: any) => {
                return {
                    label: column[0],
                    value: column[1]
                }
            })
        }).catch(err => {
            console.error(err)
        })
    }
}

function tableChangeEvent(e: string, dataSourceId: string, type: string) {
    if (type === 'source') {
        formData.partitionColumn = ''
    }
    dataSyncTableRef.value.getTableColumnData({
        dataSourceId: dataSourceId,
        tableName: e
    }, type)
}

// 级联控制
function dbTypeChange(type: string) {
    if (type === 'source') {
        formData.sourceDBId = ''
        formData.sourceTable = ''
    } else {
        formData.targetDBId = ''
        formData.targetTable = ''
    }
}
// 级联控制
function dbIdChange(type: string) {
    if (type === 'source') {
        formData.sourceTable = ''
    } else {
        formData.targetTable = ''
    }
}

// 返回
function goBack() {
  emit('back', props.workItemConfig.id)
}
function locationNode() {
  emit('locationNode', props.workItemConfig.id)
}

// 配置打开
function setConfigData() {
    configDetailRef.value.showModal(props.workItemConfig)
}

onMounted(() => {
    formData.workId = props.workItemConfig.id
    getDate()
})
</script>

<style lang="scss">
.data-sync-page {
  position: relative;
  padding-top: 50px;
  background-color: #ffffff;
  width: 100%;

  .data-sync__option-container {
    height: 50px;
    display: flex;
    align-items: center;
    color: getCssVar('color', 'primary', 'light-5');
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    padding-left: 20px;
    z-index: 10;
    border-bottom: 1px solid getCssVar('border-color');

    .btn-box {
      font-size: getCssVar('font-size', 'extra-small');
      display: flex;
      cursor: pointer;
      width: 48px;
      margin-right: 8px;

      &.btn-box__4 {
        width: 70px;
      }

      .btn-text {
        margin-left: 4px;
      }

      &:hover {
        color: getCssVar('color', 'primary');
      }
    }
  }

  .data-sync {
    width: 100%;
    padding: 20px;
    box-sizing: border-box;
    overflow: auto;
    height: calc(100vh - 100px);
    position: relative;

    .data-sync-top {
      display: flex;
      width: 100%;

      .vue-codemirror {
        height: 100px;
        width: 100%;

        .cm-editor {
          height: 100%;
          outline: none;
          border: 1px solid #dcdfe6;
        }

        .cm-gutters {
          font-size: 12px;
          font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        }

        .cm-content {
          font-size: 12px;
          font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        }

        .cm-tooltip-autocomplete {

          // display: none !important;
          ul {
            li {
              height: 40px;
              display: flex;
              align-items: center;
              font-size: 12px;
              background-color: #ffffff;
              font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
            }

            li[aria-selected] {
              background: #409EFF;
            }

            .cm-completionIcon {
              margin-right: -4px;
              opacity: 0;
            }
          }
        }
      }

      .el-card {
        width: 100%;

        & + .el-card {
          margin-left: 12px;
        }

        .el-card__header {
          padding: 0;
          border: 0;

          .card-header {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 32px;
            font-size: 14px;
          }
        }

        .el-card__body {
          .el-form {
            .el-form-item {
              .el-form-item__label {
                position: relative;

                &::before {
                  position: absolute;
                  left: -8px;
                }
              }

              .el-form-item__content {
                flex-wrap: nowrap;
                justify-content: flex-end;
              }
            }
          }
        }
      }
    }

    .select-link-type {
      height: 44px;
      display: flex;
      align-items: center;
      font-size: 12px;
      width: 100%;

      .el-select {
        margin-left: 20px;
      }
    }
  }
}</style>
