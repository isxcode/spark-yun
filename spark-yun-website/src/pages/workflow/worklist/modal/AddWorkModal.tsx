import React, { useEffect, useState } from 'react'
import { Button, Card, Form, Input, message, Modal, Select } from 'antd'
import './AddWorkModal.scss'
import axios from 'axios'

const { Option } = Select
export const AddWorkModal = (props: {
  isModalVisible: any
  handleOk: any
  handleCancel: any
  queryWork: any
  workflowId: any
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
      url: process.env.API_PREFIX_URL + '/workflow/addWork',
      data: {
        name: data.name,
        comment: data.comment,
        label: data.label,
        workflowId: props.workflowId,
        type: data.type
      }
    })
      .then(function (response) {
        message.success('添加成功')
        props.handleCancel()
        props.queryWork()
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const onGenderChange = (value: string) => {
    switch (value) {
      case 'male':
        form.setFieldsValue({ note: 'Hi, man!' })
        break
      case 'female':
        form.setFieldsValue({ note: 'Hi, lady!' })
        break
      case 'other':
        form.setFieldsValue({ note: 'Hi there!' })
        break
      default:
    }
  }

  return (
    <>
      <Modal
        title="创建作业"
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

          <Form.Item name="type" label="类型" rules={[{ required: true }]}>
            <Select placeholder="选择作业类型" onChange={onGenderChange} allowClear>
              <Option value="executeSql">执行sql作业</Option>
              <Option value="querySql">查询sql作业</Option>
              <Option value="sparkSql">sparkSql作业</Option>
              <Option value="sparkJob">sparkJob作业</Option>
            </Select>
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
