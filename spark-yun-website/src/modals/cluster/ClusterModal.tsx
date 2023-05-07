import React, { useEffect } from 'react'
import { Form, Input, Modal } from 'antd'
import './ClusterModal.less'
import { type CalculateEngineRow } from '../../types/calculate/engine/info/CalculateEngineRow'
import { type AddEngineReq } from '../../types/calculate/engine/req/AddEngineReq'
import { addEngineApi, updateEngineApi } from '../../services/cluster/ClusterService'
import { type UpdateEngineReq } from '../../types/calculate/engine/req/UpdateEngineReq'

export const ClusterModal = (props: {
  isModalVisible: boolean
  handleCancel: () => void
  handleOk: () => void
  engine?: CalculateEngineRow
}) => {
  const { isModalVisible, handleCancel, handleOk, engine } = props
  const [form] = Form.useForm()

  useEffect(() => {
    if (engine?.id == null) {
      form.resetFields()
    } else {
      form.setFieldsValue(engine)
    }
  }, [engine])

  const addEngine = (data: AddEngineReq) => {
    addEngineApi(data).then(function () {
      handleOk()
    })
  }

  const updateEngine = (data: UpdateEngineReq) => {
    data.calculateEngineId = engine?.id
    updateEngineApi(data).then(function () {
      handleOk()
    })
  }

  const onFinish = (values: any) => {
    if (engine?.id === undefined) {
      addEngine(values)
    } else {
      values.id = engine?.id
      updateEngine(values)
    }
    form.resetFields()
  }

  return (
    <>
      <Modal
        title={engine?.id == null ? '添加集群' : '编辑集群'}
        open={isModalVisible}
        onOk={() => {
          form.submit()
        }}
        onCancel={handleCancel}
        okText={'保存'}
        cancelText={'取消'}>
        <Form
          className={'sy-engine-form'}
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 18 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          form={form}>
          <Form.Item label="名称" name="name" rules={[{ required: true, message: '名称不能为空' }]}>
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
