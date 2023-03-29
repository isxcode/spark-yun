import React from 'react'
import './Login.scss'
import { Button, Form, Input, theme } from 'antd'
function Login () {
  const onFinish = (values: any) => {
    console.log('Success:', values)
  }

  const onFinishFailed = (errorInfo: any) => {
    console.log('Failed:', errorInfo)
  }

  const {
    token: { colorPrimary }
  } = theme.useToken()

  return (
    <div className={'sy-login-page'}>
      <div className={'sy-login-div'}>
        <Form
          name="basic"
          labelCol={{ span: 8 }}
          wrapperCol={{ span: 16 }}
          style={{ maxWidth: 600 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          onFinishFailed={onFinishFailed}
          autoComplete="off">
          <div className={'sy-login-title'} style={{ background: colorPrimary }}>
            至轻云
          </div>

          <Form.Item name="account" rules={[{ required: true, message: '账号不能为空' }]}>
            <Input className={'sy-login-account-input'} placeholder="账号/邮箱/手机号" />
          </Form.Item>

          <Form.Item name="password" rules={[{ required: true, message: '密码不能为空' }]}>
            <Input.Password className={'sy-login-password-input'} placeholder="密码" />
          </Form.Item>

          <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
            <Button className={'sy-login-btn'} type="primary" htmlType="submit">
              登录
            </Button>
          </Form.Item>
        </Form>
      </div>
    </div>
  )
}

export default Login
