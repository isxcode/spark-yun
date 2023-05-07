import React, {useEffect, useState} from 'react'
import {Form, Input, Modal, Select} from 'antd'
import './TenantModal.less'
import {UserRow} from '../../types/user/info/UserRow'
import {queryAllUsersApi, updateUserApi} from '../../services/user/UserService'
import {TenantRow} from '../../types/tenant/info/TenantRow'
import {QueryAllUserReq} from '../../types/user/req/QueryAllUserReq'
import {AddTenantReq} from '../../types/tenant/req/AddTenantReq'
import {addTenantApi, updateTenantApi} from '../../services/tenant/TenantService'
import {UpdateTenantReq} from '../../types/tenant/req/UpdateTenantReq'

export const TenantModal = (props: {
  isModalVisible: boolean
  handleCancel: () => void
  handleOk: () => void
  tenant?: TenantRow
}) => {
  const {isModalVisible, handleCancel, handleOk, tenant} = props
  const [users, setUsers] = useState<UserRow[]>()
  const [form] = Form.useForm()

  const queryAllUserReq: QueryAllUserReq = {
    page: 1,
    pageSize: 999,
    searchKeyWord: ''
  }

  const fetchUsers = () => {
    queryAllUsersApi(queryAllUserReq).then(function (response) {
      setUsers(response.content)
    })
  }

  useEffect(() => {
    fetchUsers()
    if (tenant?.id == null) {
      form.resetFields()
    } else {
      form.setFieldsValue(tenant)
    }
  }, [tenant])

  const addTenant = (data: AddTenantReq) => {
    addTenantApi(data).then(function () {
      handleOk()
    })
  }

  const updateTenant = (data: UpdateTenantReq) => {
    updateTenantApi(data).then(function () {
      handleOk()
    })
  }

  const onFinish = (values: any) => {
    if (tenant?.id === undefined) {
      addTenant(values)
    } else {
      values.id = tenant?.id
      updateTenant(values)
    }
    form.resetFields()
  }

  return (
    <>
      <Modal
        title={tenant?.id == null ? '创建租户' : '更新租户'}
        open={isModalVisible}
        onOk={() => {
          form.submit()
        }}
        onCancel={handleCancel}
        okText={'保存'}
        cancelText={'取消'}>
        <Form
          className={'sy-add-datasource-form'}
          labelCol={{span: 5}}
          wrapperCol={{span: 17}}
          initialValues={{remember: true}}
          onFinish={onFinish}
          form={form}>
          <Form.Item label="租户名称" name="name" rules={[{required: true, message: '名称不能为空'}]}>
            <Input/>
          </Form.Item>

          <Form.Item label="成员数" name="maxMemberNum">
            <Input/>
          </Form.Item>

          <Form.Item label="作业流数" name="maxWorkflowNum">
            <Input/>
          </Form.Item>

          {tenant?.id == null
            ? (
              <Form.Item name="adminUserId" label="管理员" rules={[{required: true, message: '租户管理员不能为空'}]}>
                <Select placeholder="选择管理员" allowClear>
                  {users?.map((option) => (
                    <Select.Option key={option.id} value={option.id}>
                      {option.account}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            )
            : (
              <></>
            )}

          <Form.Item label="备注" name="remark">
            <Input.TextArea/>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
