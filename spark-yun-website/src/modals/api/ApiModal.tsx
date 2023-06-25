import React, { useEffect } from 'react'
import { Form, Input, Modal } from 'antd'
import './ApiModal.less'
import { UserRow } from '../../types/user/info/UserRow'
import { addUserApi, updateUserApi } from '../../services/user/UserService'
import { UpdateUserReq } from '../../types/user/req/UpdateUserReq'
import { AddUserReq } from '../../types/user/req/AddUserReq'

export const ApiModal = (props: {
  isModalVisible: boolean
  handleCancel: () => void
  handleOk: () => void
  user?: UserRow
}) => {
  const { isModalVisible, handleCancel, handleOk, user } = props
  const [form] = Form.useForm()

  useEffect(() => {
    if (user?.id == null) {
      form.resetFields()
    } else {
      form.setFieldsValue(user)
    }
  }, [user])

  const addUser = (data: AddUserReq) => {
    addUserApi(data).then(function () {
      handleOk()
    })
  }

  const updateUser = (data: UpdateUserReq) => {
    updateUserApi(data).then(function () {
      handleOk()
    })
  }

  const onFinish = (values: any) => {
    if (user?.id === undefined) {
      addUser(values)
    } else {
      values.id = user?.id
      updateUser(values)
    }
    form.resetFields()
  }

  return (
    <>
      <Modal
        title={user?.id == null ? '创建用户' : '更新用户'}
        open={isModalVisible}
        onOk={() => {
          form.submit()
        }}
        style={{ marginTop: '10px' }}
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
          <Form.Item label="名称" name="username" rules={[{ required: true, message: '名称不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="路径" name="account" rules={[{ required: true, message: '账号不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="请求方式" name="account" rules={[{ required: true, message: '账号不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="数据源" name="account" rules={[{ required: true, message: '账号不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="请求头" name="remark">
            <Input.TextArea />
          </Form.Item>

          <Form.Item label="请求体" name="remark">
            <Input.TextArea />
          </Form.Item>

          <Form.Item label="执行sql" name="account" rules={[{ required: true, message: '账号不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item label="响应体" name="remark">
            <Input.TextArea />
          </Form.Item>

          <Form.Item label="备注" name="remark">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
