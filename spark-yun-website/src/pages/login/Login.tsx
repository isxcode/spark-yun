import React, { useState } from 'react'
import { Button, Col, Form, Input, message, Row, theme } from 'antd'
import { useNavigate } from 'react-router-dom'
import { loginApi } from '../../services/login/loginService'
import './Login.less'
import * as CryptoJS from 'crypto-js'

function Login() {
  const navigate = useNavigate()

  const [loading, setLoading] = useState(false)

  const handleLogin = (value) => {
    setLoading(true)
    // const { passwd } = value
    // value.passwd = CryptoJS.MD5(passwd).toString()
    loginApi(value)
      .then(function (response) {
        localStorage.setItem('Authorization', response.username)
        localStorage.setItem('Token', response.token)
        localStorage.setItem('Tenant', response.tenantId)
        localStorage.setItem('Username', response.username)
        localStorage.setItem('Role', response.role)
        navigate('/')
        window.location.reload()
      })
      .finally(() => {
        setLoading(false)
      })
  }

  return (
    <div className={'sy-login-div'}>
      <div className={'sy-login-logo'}>至轻云</div>

      <Form onFinish={handleLogin} initialValues={{ remember: true }}>
        <Form.Item className={'sy-login-item'} name="account" rules={[{ required: true, message: '账号不能为空' }]}>
          <Input placeholder="账号" />
        </Form.Item>

        <Form.Item className={'sy-login-item'} name="passwd" rules={[{ required: true, message: '密码不能为空' }]}>
          <Input.Password placeholder="密码" />
        </Form.Item>

        <Form.Item>
          <Row justify="center">
            <Col>
              <Button className={'sy-login-btn'} type="primary" htmlType="submit" loading={loading}>
                登录
              </Button>
            </Col>
          </Row>
        </Form.Item>
      </Form>

      <div className={'sy-login-describe'}>基于Spark打造超轻量级大数据平台</div>
    </div>
  )
}

export default Login
