<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="custom-api-form" label-position="top" :model="formData" :rules="rules">
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="图表类型" prop="type" v-if="renderSence === 'new'">
        <el-select v-model="formData.type" placeholder="请选择">
          <el-option v-for="item in chartTypeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="数据源" prop="datasourceId" v-if="renderSence === 'new'">
        <el-select v-model="formData.datasourceId" placeholder="请选择" @visible-change="getDataSourceList">
          <el-option v-for="item in dataSourceList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" maxlength="200" :autosize="{ minRows: 4, maxRows: 4 }"
          placeholder="请输入" />
      </el-form-item>
    </el-form>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetDatasourceList } from '@/services/datasource.service'
import { ChartTypeList } from '../report-item/report-item.config'

interface Option {
  label: string
  value: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const dataSourceList = ref([])  // 数据源
const chartTypeList = ref<Option[]>(ChartTypeList)
const renderSence = ref<string>('new')

const modelConfig = reactive({
  title: '添加卡片',
  visible: false,
  width: '564px',
  okConfig: {
    title: '确定',
    ok: okEvent,
    disabled: false,
    loading: false
  },
  cancelConfig: {
    title: '取消',
    cancel: closeEvent,
    disabled: false,
    hide: false
  },
  needScale: false,
  zIndex: 1100,
  closeOnClickModal: false
})
const formData = reactive<{
  id: string
  name: string
  datasourceId: string
  type: string
  remark: string
}>({
  id: '',
  // 基础配置
  name: '',             // 名称
  type: '',             // 图表类型
  datasourceId: '',     // 数据源
  remark: '',           // 备注
})
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入名称', trigger: ['blur', 'change'] }],
  type: [{ required: true, message: '请选择图表类型', trigger: ['blur', 'change'] }],
  datasourceId: [{ required: true, message: '请选择数据源', trigger: ['blur', 'change'] }],
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true
  getDataSourceList(true)
  if (data) {
    renderSence.value = 'edit'
    Object.keys(formData).forEach(key => {
      formData[key] = data[key]
    })
    modelConfig.title = '编辑卡片'
  } else {
    renderSence.value = 'new'
    Object.keys(formData).forEach(key => {
      formData[key] = null
    })
    modelConfig.title = '添加卡片'
  }
  nextTick(() => {
    form.value?.resetFields()
  })
}

// 查询数据源
function getDataSourceList(e: boolean, searchType?: string) {
  if (e) {
    GetDatasourceList({
      page: 0,
      pageSize: 10000,
      searchKeyWord: searchType || ''
    }).then((res: any) => {
      dataSourceList.value = res.data.content.map((item: any) => {
        return {
          label: item.name,
          value: item.id
        }
      })
    })
      .catch(() => {
        dataSourceList.value = []
      })
  }
}

function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      modelConfig.okConfig.loading = true
      callback.value({
        ...formData,
        id: formData.id ? formData.id : undefined
      }).then((res: any) => {
        modelConfig.okConfig.loading = false
        if (res === undefined) {
          modelConfig.visible = false
        } else {
          modelConfig.visible = true
        }
      }).catch(() => {
        modelConfig.okConfig.loading = false
      })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function closeEvent() {
  modelConfig.visible = false
}

defineExpose({
  showModal
})
</script>

<style lang="scss">
.custom-api-form__step {
  margin: auto;
  padding-top: 20px;
  position: sticky;
  top: 0;
  background: #ffffff;
  z-index: 10;
  padding-left: 25%;
  padding-right: 25%;
  box-sizing: border-box;
  border-bottom: 1px solid getCssVar('border-color');

  .el-step__head {
    &.is-process {
      color: getCssVar('color', 'primary');

      .el-step__line {
        // background-color: getCssVar('color', 'primary');
      }

      .el-step__icon {
        border-color: getCssVar('color', 'primary');
      }
    }
  }

  .el-step__main {
    margin-left: -12px;

    .el-step__title {
      font-size: 12px;

      &.is-process {
        color: getCssVar('color', 'primary');
      }
    }
  }
}

.custom-api-form {
  box-sizing: border-box;
  padding: 12px 20px 0 20px;
  width: 100%;

  .api-item {
    .item-title {
      font-size: 14px;
      padding-bottom: 12px;
      margin-bottom: 12px;
      box-sizing: border-box;
      border-bottom: 1px solid #ebeef5;
      font-weight: bolder;
      color: getCssVar('color', 'primary');
    }
  }

  .el-form-item {
    .format-json {
      position: absolute;
      top: -34px;
      right: 20px;
      font-size: 12px;
      color: getCssVar('color', 'primary');
      cursor: pointer;

      &:hover {
        text-decoration: underline;
      }
    }

    .tooltip-msg {
      position: absolute;
      top: -28px;
      // left: 20px;
      color: getCssVar('color', 'info');
      font-size: 16px;
    }

    &.show-screen__full {
      position: fixed;
      width: 100%;
      height: 100%;
      top: 0;
      left: 0;
      background-color: #ffffff;
      padding: 12px 20px;
      box-sizing: border-box;
      transition: all 0.15s linear;
      z-index: 10;

      .el-form-item__content {
        align-items: flex-start;
        height: 100%;

        .modal-full-screen {
          position: absolute;
          top: -26px;
          right: 0;
          cursor: pointer;

          &:hover {
            color: getCssVar('color', 'primary');
          }
        }

        .vue-codemirror {
          height: calc(100% - 36px);
        }
      }
    }

    .el-form-item__content {
      .vue-codemirror {
        height: 130px;
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

      .add-btn {
        position: absolute;
        right: 0;
        top: -32px;

        .el-icon {
          color: getCssVar('color', 'primary');
          cursor: pointer;
          font-size: 16px;
        }
      }

      .form-options__list {
        width: 100%;
        max-height: 210px;
        overflow: auto;

        .form-options__item {
          display: flex;
          margin-bottom: 8px;

          .input-item {
            display: flex;
            font-size: 12px;
            margin-right: 8px;
            color: #303133;
            width: 100%;

            .item-label {
              margin-right: 8px;
            }

            .el-input {
              .el-input__wrapper {
                // padding: 0;
              }
            }
          }

          .option-btn {
            display: flex;
            height: 32px;
            align-items: center;

            .remove {
              color: red;
              cursor: pointer;
              margin-right: 8px;

              &:hover {
                color: getCssVar('color', 'primary');
              }
            }

            .move {
              color: getCssVar('color', 'primary');

              &:active {
                cursor: move;
              }
            }
          }
        }
      }
    }
  }
}
</style>
