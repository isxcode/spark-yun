import React, { useEffect, useState } from 'react'
import { Button, Card, Form, Input, message, Modal } from 'antd'
import './AddWorkflowModal.scss'
import axios from 'axios'

export const AddWorkflowModal = (props: {
  isModalVisible: any
  handleOk: any
  handleCancel: any
  queryWorkflow: any
}) => {
  const [form] = Form.useForm()

  const onFinish = (values: any) => {
    addWorkflow(values)
    onReset()
  }

  const onFinishFailed = (errorInfo: any) => {
    console.log('Failed:', errorInfo)
  }

  const onReset = () => {
    form.resetFields()
  }

  const addWorkflow = (data) => {
    axios({
      method: 'post',
      url: process.env.API_PREFIX_URL + '/workflow/addWorkflow',
      data: {
        name: data.name,
        comment: data.comment,
        label: data.label
      }
    })
      .then(function (response) {
        message.success('添加成功')
        props.handleCancel()
        props.queryWorkflow()
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  return (
    <>
      <Modal
        title="创建作业流"
        footer={false}
        open={props.isModalVisible}
        onOk={props.handleOk}
        onCancel={props.handleCancel}
        width={500}>
        <Form
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 18 }}
          style={{ maxWidth: 600 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          onFinishFailed={onFinishFailed}
          autoComplete="off"
          form={form}>
          <Form.Item label="名称" name="name">
            <Input />
          </Form.Item>

          <Form.Item label="备注" name="comment">
            <Input />
          </Form.Item>

          <Form.Item label="标签" name="label">
            <Input />
          </Form.Item>

          <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
            <Button type="primary" htmlType="submit" onClick={props.handleCancel}>
              创建
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
