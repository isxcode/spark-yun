import React from 'react'
import './Login.scss'
import { Button, Form, Input, message, theme } from 'antd'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'
function Login () {
  const navigate = useNavigate()

  const login = (value) => {
    axios({
      method: 'post',
      url: process.env.API_PREFIX_URL + '/usr/login',
      data: {
        account: value.account,
        passwd: value.password
      }
    })
      .then(function (response) {
        console.log(response)
        if ("200" === response.data.code) {
          message.success('登录成功').then(() => {
          });
          localStorage.setItem('Authorization', response.data.username);
          navigate('/');
        } else {
          message.error(response.data.message);
        }
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const onFinish = (values: any) => {
    login(values)
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
          labelCol={{span: 8}}
          wrapperCol={{span: 16}}
          style={{maxWidth: 600}}
          initialValues={{remember: true}}
          onFinish={onFinish}
          onFinishFailed={onFinishFailed}
          autoComplete="off">

          <div className={'sy-login-title'} style={{background: colorPrimary}}>
            至轻云
          </div>

          <div className={'sy-login-desc'}>
            基于spark打造超轻量级批处理大数据平台
          </div>

          <Form.Item wrapperCol={{offset: 5, span: 16}} name="account" rules={[{required: true, message: '账号不能为空'}]}>
            <Input className={'sy-login-account-input'} placeholder="账号/邮箱/手机号"/>
          </Form.Item>

          <Form.Item wrapperCol={{offset: 5, span: 16}} name="password" rules={[{required: true, message: '密码不能为空'}]}>
            <Input.Password className={'sy-login-password-input'} placeholder="密码"/>
          </Form.Item>

          <Form.Item wrapperCol={{offset: 10, span: 16}}>
            <Button className={'sy-login-btn'} type="primary" htmlType="submit">
              登录
            </Button>
          </Form.Item>
        </Form>
      </div>
    </div>
  );
}

export default Login
