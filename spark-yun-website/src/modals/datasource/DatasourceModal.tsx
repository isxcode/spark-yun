import React, { useEffect } from 'react'
import { Form, Input, Modal, Select } from 'antd'
import './DatasourceModal.less'
import { DatasourceRow } from '../../types/datasource/info/DatasourceRow'
import { AddDatasourceReq } from '../../types/datasource/req/AddDatasourceReq'
import { addDatasourceApi, updateDatasourceApi } from '../../services/datasource/DatasourceService'
import { UpdateWorkReq } from '../../types/woks/req/UpdateWorkReq'
import { updateWorkApi } from '../../services/works/WorksService'
import { UpdateDatasourceReq } from '../../types/datasource/req/UpdateDatasourceReq'

const { Option } = Select
export const DatasourceModal = (props: {
  isModalVisible: boolean
  handleCancel: () => void
  handleOk: () => void
  datasource?: DatasourceRow
}) => {
  const { isModalVisible, handleCancel, handleOk, datasource } = props
  const [form] = Form.useForm()

  useEffect(() => {
    if (datasource?.id == null) {
      form.resetFields()
    } else {
      form.setFieldsValue(datasource)
    }
  }, [datasource])

  const addDatasource = (data: AddDatasourceReq) => {
    addDatasourceApi(data).then(function () {
      handleOk()
    })
  }

  const updateDatasource = (data: UpdateDatasourceReq) => {
    updateDatasourceApi(data).then(function () {
      handleOk()
    })
  }

  const onFinish = (values: any) => {
    if (datasource?.id === undefined) {
      addDatasource(values)
    } else {
      values.id = datasource?.id
      updateDatasource(values)
    }
    form.resetFields()
  }

  return (
    <>
      <Modal
        title={datasource?.id == null ? '添加数据源' : '编辑数据源'}
        open={isModalVisible}
        onOk={() => {
          form.submit()
        }}
        onCancel={handleCancel}
        okText={'保存'}
        cancelText={'取消'}>
        <Form
          className={'sy-add-datasource-form'}
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 20 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          form={form}>
          <Form.Item label="名称" name="name" rules={[{ required: true, message: '名称不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item name="dbType" label="类型" rules={[{ required: true, message: '类型不能为空' }]}>
            <Select placeholder="选择数据库类型" allowClear>
              <Option value="MYSQL">Mysql</Option>
              <Option value="ORACLE">Oracle</Option>
              <Option value="SQL_SERVER">SqlServer</Option>
            </Select>
          </Form.Item>

          <Form.Item label="JdbcUrl:" name="jdbcUrl" rules={[{ required: true, message: 'JdbcUrl不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="用户名" name="username" rules={[{ required: true, message: '用户名不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="密码" name="passwd" rules={[{ required: true, message: '密码不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="备注" name="remark">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
