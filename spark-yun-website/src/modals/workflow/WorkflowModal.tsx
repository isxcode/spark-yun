import React, { useEffect, useState } from 'react'
import { Form, Input, Modal } from 'antd'
import './WorkflowModal.less'
import { addWorkflowApi, updateWorkflowApi } from '../../services/worflow/WorkflowService'
import { type AddWorkflowReq } from '../../types/workflow/req/AddWorkflowReq'
import { type WorkflowRow } from '../../types/workflow/info/WorkflowRow'
import { type UpdateWorkflowReq } from '../../types/workflow/req/UpdateWorkflowReq'

export const WorkflowModal = (props: {
  isModalVisible: boolean
  handleCancel: () => void
  handleOk: () => void
  workflow?: WorkflowRow
}) => {
  const { isModalVisible, handleCancel, handleOk, workflow } = props
  const [form] = Form.useForm()

  useEffect(() => {
    form.setFieldsValue(workflow)
  }, [workflow])

  const addWorkflow = (data: AddWorkflowReq) => {
    addWorkflowApi(data).then(function () {
      handleOk()
    })
  }

  const updateWorkflow = (data: UpdateWorkflowReq) => {
    updateWorkflowApi(data).then(function () {
      handleOk()
    })
  }

  const onFinish = (values: any) => {
    if (workflow?.id === undefined) {
      addWorkflow(values)
    } else {
      values.id = workflow?.id
      updateWorkflow(values)
    }
    form.resetFields()
  }

  return (
    <>
      <Modal
        title={workflow?.id == null ? '添加作业流' : '编辑作业流'}
        open={isModalVisible}
        onOk={() => {
          form.submit()
        }}
        onCancel={handleCancel}
        okText={'保存'}
        cancelText={'取消'}>
        <Form form={form} className={'sy-add-workflow-form'} initialValues={{ remember: true }} onFinish={onFinish}>
          <Form.Item
            label="名称"
            name="name"
            rules={[{ required: true, message: '名称不能为空' }]}
            labelCol={{ span: 4 }}
            wrapperCol={{ span: 20 }}>
            <Input />
          </Form.Item>

          <Form.Item label="备注" name="comment" labelCol={{ span: 4 }} wrapperCol={{ span: 20 }}>
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
