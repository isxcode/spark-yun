import React, { useEffect, useState } from 'react'
import { Form, Input, Modal, Select, Switch } from 'antd'
import './TenantUserModal.less'
import { UserRow } from '../../../types/user/info/UserRow'
import { queryAllEnableUsersApi } from '../../../services/user/UserService'
import { QueryAllUserReq } from '../../../types/user/req/QueryAllUserReq'
import { AddTenantUserReq } from '../../../types/tenant/user/req/AddTenantUserReq'
import { addTenantUserApi } from '../../../services/tenant/user/TenantUserService'

export const TenantUserModal = (props: { isModalVisible: boolean, handleCancel: () => void, handleOk: () => void }) => {
  const { isModalVisible, handleCancel, handleOk } = props
  const [users, setUsers] = useState<UserRow[]>()
  const [form] = Form.useForm()

  const queryAllUserReq: QueryAllUserReq = {
    page: 1,
    pageSize: 999,
    searchKeyWord: ''
  }

  const fetchUsers = () => {
    queryAllEnableUsersApi(queryAllUserReq).then(function (response) {
      setUsers(response.content)
    })
  }

  useEffect(() => {
    fetchUsers()
    form.resetFields()
  }, [isModalVisible])

  const addTenantUser = (data: AddTenantUserReq) => {
    addTenantUserApi(data).then(function () {
      handleOk()
    })
  }

  const onFinish = (values: any) => {
    addTenantUser(values)
    form.resetFields()
  }

  return (
    <>
      <Modal
        title={'添加成员'}
        open={isModalVisible}
        onOk={() => {
          form.submit()
        }}
        onCancel={handleCancel}
        okText={'保存'}
        cancelText={'取消'}>
        <Form
          className={'sy-add-datasource-form'}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 20 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          form={form}>
          <Form.Item name="userId" label="成员" rules={[{ required: true, message: '成员不能为空' }]}>
            <Select placeholder="选择成员" allowClear>
              {users?.map((option) => (
                <Select.Option key={option.id} value={option.id}>
                  {option.account}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name={'tenantAdmin'} label="设为管理员" valuePropName="tenantAdmin">
            <Switch />
          </Form.Item>

          <Form.Item label="备注" name="remark">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
