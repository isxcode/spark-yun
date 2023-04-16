import React, { useEffect } from 'react'
import { Form, Input, Modal } from 'antd'
import { type CalculateEngineRow } from '../../../types/calculate/engine/info/CalculateEngineRow'
import { type AddEngineNodeReq } from '../../../types/calculate/node/req/AddEngineNodeReq'
import { addEngineNodeApi, updateEngineNodeApi } from '../../../services/calculate/node/EngineNodeService'
import './EngineNodeModal.less'
import { UpdateEngineNodeReq } from '../../../types/calculate/node/req/UpdateEngineNodeReq'

export const EngineNodeModal = (props: {
  isModalVisible: boolean
  handleCancel: () => void
  handleOk: () => void
  engineNode?: CalculateEngineRow
  calculateEngineId: string
}) => {
  const { isModalVisible, handleCancel, handleOk, engineNode, calculateEngineId } = props
  const [form] = Form.useForm()

  useEffect(() => {
    if (engineNode?.id == null) {
      form.resetFields()
    } else {
      form.setFieldsValue(engineNode)
    }
  }, [engineNode])

  const addEngineNode = (data: AddEngineNodeReq) => {
    data.calculateEngineId = calculateEngineId
    addEngineNodeApi(data).then(function () {
      handleOk()
    })
  }

  const updateEngineNode = (data: UpdateEngineNodeReq) => {
    data.calculateEngineId = calculateEngineId
    updateEngineNodeApi(data).then(function () {
      handleOk()
    })
  }

  const onFinish = (values: any) => {
    if (engineNode?.id === undefined) {
      addEngineNode(values)
    } else {
      values.id = engineNode?.id
      updateEngineNode(values)
    }
    form.resetFields()
  }

  return (
    <>
      <Modal
        title={engineNode?.id == null ? '添加节点' : '编辑节点'}
        open={isModalVisible}
        onOk={() => {
          form.submit()
        }}
        onCancel={handleCancel}
        okText={'保存'}
        cancelText={'取消'}>
        <Form
          className={'sy-add-engine-node-form'}
          labelCol={{ span: 5 }}
          wrapperCol={{ span: 20 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          form={form}>
          <Form.Item label="名称" name="name" rules={[{ required: true, message: '名称不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="Host" name="host" rules={[{ required: true, message: 'Host不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="Port" name="port">
            <Input />
          </Form.Item>

          <Form.Item label="用户名" name="username" rules={[{ required: true, message: '用户名不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="密码" name="password" rules={[{ required: true, message: '密码不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="YunHome" name="agentHomePath">
            <Input />
          </Form.Item>

          <Form.Item label="YunPort" name="agentPort">
            <Input />
          </Form.Item>

          <Form.Item label="Hadoop" name="hadoopHomePath">
            <Input />
          </Form.Item>

          <Form.Item label="备注" name="comment">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
