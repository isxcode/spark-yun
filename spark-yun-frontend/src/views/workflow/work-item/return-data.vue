
<template>
  <LoadingPage class="log-loading" :visible="loading">
    <LogContainer
      v-if="strData || jsonData"
      :logMsg="strData || jsonData"
      :showResult="true"
      :status="true"
    ></LogContainer>
    <template v-else>
      <BlockTable class="result-table-log" :table-config="tableConfig"/>
    </template>
  </LoadingPage>
  <span v-if="showParse" class="zqy-json-parse" @click="getJsonParseResult">结果解析</span>
</template>

<script lang="ts" setup>
import { reactive, ref, defineEmits, defineProps } from 'vue'
import BlockTable from '@/components/block-table/index.vue'
import { GetResultData } from '@/services/schedule.service'
import LoadingPage from '@/components/loading/index.vue'

const emit = defineEmits(['getJsonParseResult'])

const props = defineProps<{
  showParse: boolean
}>()

const tableConfig = reactive({
  tableData: [],
  colConfigs: [],
  seqType: 'seq',
  loading: false
})
const jsonData = ref()
const strData = ref()
const loading = ref<boolean>(false)

function initData(id: string): void {
  getResultDatalist(id)
}

function getJsonParseResult() {
    emit('getJsonParseResult')
}

// 获取结果
function getResultDatalist(id: string) {
  if (!id) {
    tableConfig.colConfigs = []
    tableConfig.tableData = []
    tableConfig.loading = false
    loading.value = false
    return
  }
  tableConfig.loading = true
  loading.value = true
  GetResultData({
    instanceId: id
  })
    .then((res: any) => {
      jsonData.value = res.data.jsonData
      strData.value = res.data.strData

      loading.value = false

      const col = res.data.data.slice(0, 1)[0]
      const tableData = res.data.data.slice(1, res.data.data.length)
      tableConfig.colConfigs = col.map((colunm: any) => {
        return {
          prop: colunm,
          title: colunm,
          minWidth: 100,
          showHeaderOverflow: true,
          showOverflowTooltip: true
        }
      })
      tableConfig.tableData = tableData.map((columnData: any) => {
        const dataObj: any = {
        }
        col.forEach((c: any, index: number) => {
          dataObj[c] = columnData[index]
        })
        return dataObj
      })
      tableConfig.loading = false
    })
    .catch(() => {
      tableConfig.colConfigs = []
      tableConfig.tableData = []
      tableConfig.loading = false
    })
}

defineExpose({
  initData
})
</script>

<style lang="scss">
.log-loading {
  &.zqy-loading {
    position: static;
    height: 100% !important;
    padding: 0 !important;
    margin-top: 0 !important;
    overflow: auto;
  }
}
.vxe-table--body-wrapper {
  max-height: calc(100vh - 428px);
  overflow: auto;
}
.zqy-json-parse {
    font-size: 12px;
    color: getCssVar('color', 'primary');
    cursor: pointer;
    position: absolute;
    right: 40px;
    top: 12px;
    &:hover {
        text-decoration: underline;
    }
}
</style>
