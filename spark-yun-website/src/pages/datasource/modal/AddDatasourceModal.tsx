import React, { useEffect, useState } from 'react'
import { Button, Card, Form, Input, message, Modal, Select } from 'antd'
import axios from 'axios'

const { Option } = Select

export const AddDatasourceModal = (props: {
  isModalVisible: any
  handleOk: any
  handleCancel: any
  queryDatasources: any
}) => {
  const [form] = Form.useForm()
  const addDatasource = (data) => {
    axios({
      method: 'post',
      url: process.env.API_PREFIX_URL + '/das/addDatasource',
      data: {
        name: data.name,
        comment: data.comment,
        jdbcUrl: data.jdbcUrl,
        username: data.username,
        port: data.port,
        type: data.type,
        password: data.password
      }
    })
      .then(function (response) {
        message.success('添加成功')
        props.handleCancel()
        props.queryDatasources()
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const onFinish = (values: any) => {
    addDatasource(values)
    onReset()
  }

  const onFinishFailed = (errorInfo: any) => {
    console.log('Failed:', errorInfo)
  }

  const onReset = () => {
    form.resetFields()
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
        title="添加数据源"
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

          <Form.Item name="type" label="类型" rules={[{ required: true }]}>
            <Select placeholder="选择数据库类型" onChange={onGenderChange} allowClear>
              <Option value="MYSQL">mysql数据库</Option>
              <Option value="ORACLE">oracle数据库</Option>
              <Option value="SQL_SERVER">sqlserver数据库</Option>
            </Select>
          </Form.Item>

          <Form.Item label="jdbcUrl:" name="jdbcUrl">
            <Input />
          </Form.Item>

          <Form.Item label="用户名" name="username">
            <Input />
          </Form.Item>

          <Form.Item label="密码" name="password">
            <Input />
          </Form.Item>

          <Form.Item label="备注" name="comment">
            <Input />
          </Form.Item>

          <Form.Item wrapperCol={{ offset: 11, span: 16 }}>
            <Button type="primary" htmlType="submit" onClick={props.handleCancel}>
              保存
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
