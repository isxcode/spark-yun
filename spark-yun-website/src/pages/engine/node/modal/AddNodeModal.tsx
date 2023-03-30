import React, { useEffect, useState } from 'react'
import { Button, Card, Form, Input, message, Modal } from 'antd'
import './AddNodeModal.scss'
import axios from 'axios'
import engine from '../../Engine'

export const AddNodeModal = (props: {
  isModalVisible: any
  handleOk: any
  handleCancel: any
  queryNodes: any
  engineId: any
}) => {
  const [form] = Form.useForm()
  const addEngine = (data) => {
    axios({
      method: 'post',
      url: 'http://localhost:8080/node/addNode',
      data: {
        name: data.name,
        comment: data.comment,
        username: data.username,
        host: data.host,
        port: data.port,
        password: data.password,
        engineId: props.engineId
      }
    })
      .then(function (response) {
        message.success('添加成功')
        props.handleCancel()
        props.queryNodes(props.engineId)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const onFinish = (values: any) => {
    addEngine(values)
    onReset()
  }

  const onFinishFailed = (errorInfo: any) => {
    console.log('Failed:', errorInfo)
  }

  const onReset = () => {
    form.resetFields()
  }

  return (
    <>
      <Modal
        title="添加节点"
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
          <Form.Item label="节点名称" name="name">
            <Input />
          </Form.Item>

          <Form.Item label="备注" name="comment">
            <Input />
          </Form.Item>

          <Form.Item label="地址" name="host">
            <Input />
          </Form.Item>

          <Form.Item label="端口号" name="port">
            <Input />
          </Form.Item>

          <Form.Item label="用户名" name="username">
            <Input />
          </Form.Item>

          <Form.Item label="密码" name="password">
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
