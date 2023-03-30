import React from 'react'
import { Layout, Menu, type MenuProps, message, theme } from 'antd'
import { Outlet, useNavigate } from 'react-router-dom'
import './Layouts.scss'

function Layouts () {
  const navigate = useNavigate()

  const { Header, Content, Sider } = Layout

  const menus: MenuProps['items'] = [
    {
      key: 1,
      label: '首页',
      onClick: () => {
        navigate('/monitor')
      }
    },
    {
      key: 2,
      label: '项目管理',
      onClick: () => {
        navigate('/project')
      }
    },
    {
      key: 3,
      label: '计算集群',
      onClick: () => {
        navigate('/engine')
      }
    },
    {
      key: 4,
      label: '数据源',
      onClick: () => {
        navigate('/datasource')
      }
    },
    {
      key: 5,
      label: '作业流',
      onClick: () => {
        navigate('/workflow')
      }
    },
    {
      key: 6,
      label: '数据建模',
      onClick: () => message.warning('请上传企业许可证！')
    },
    {
      key: 7,
      label: '调度历史',
      onClick: () => message.warning('请上传企业许可证！')
    },
    {
      key: 8,
      label: 'BI酷屏',
      onClick: () => message.warning('请上传企业许可证！')
    },
    {
      key: 9,
      label: '数据资产',
      onClick: () => message.warning('请上传企业许可证！')
    },
    {
      key: 10,
      label: '自定义Api',
      onClick: () => message.warning('请上传企业许可证！')
    },
    {
      key: 11,
      label: 'AI计算',
      onClick: () => message.warning('请上传企业许可证！')
    },
    {
      key: 12,
      label: '后台管理',
      onClick: () => message.warning('请上传企业许可证！')
    },
    {
      key: 13,
      label: '用户中心',
      onClick: () => message.warning('请上传企业许可证！')
    },
    {
      key: 14,
      label: '系统配置',
      onClick: () => message.warning('请上传企业许可证！')
    }
  ]

  const {
    token: { colorBgContainer, colorPrimary }
  } = theme.useToken()

  return (
    <>
      <Layout style={{ minHeight: '100vh' }}>
        <Header style={{ background: colorBgContainer, borderBottom: colorPrimary + ' solid 2px' }}>
          <div
            className={'logo'}
            style={{
              background: colorPrimary,
              color: 'white',
              width: '100px',
              textAlign: 'center'
            }}>
            至轻云
          </div>
        </Header>
        <Layout>
          <Sider width={150} style={{ background: colorBgContainer, textAlign: 'center' }}>
            <Menu defaultSelectedKeys={['1']} items={menus} />
          </Sider>
          <Layout style={{ borderLeft: colorPrimary + ' solid 2px' }}>
            <Content
              style={{
                padding: 24,
                margin: 0,
                background: colorBgContainer
              }}>
              <Outlet />
            </Content>
          </Layout>
        </Layout>
      </Layout>
    </>
  )
}

export default Layouts
