import React from 'react'
import { Layout, Menu, type MenuProps, message, theme } from 'antd'
import { Outlet, useNavigate } from 'react-router-dom'
import './Layouts.less'
import {
  ApartmentOutlined,
  ApiOutlined,
  AppstoreOutlined,
  CloudOutlined,
  DashboardOutlined,
  DatabaseOutlined,
  FireOutlined,
  FundProjectionScreenOutlined,
  HomeOutlined,
  NodeIndexOutlined,
  ProjectOutlined,
  SettingOutlined,
  TeamOutlined,
  UserOutlined
} from '@ant-design/icons'

function Layouts() {
  const navigate = useNavigate()

  const { Header, Content, Sider } = Layout

  const menus: MenuProps['items'] = [
    {
      key: 1,
      label: '首页',
      icon: <HomeOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 2,
      label: '项目管理',
      icon: <ProjectOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 3,
      label: '计算集群',
      icon: <CloudOutlined />,
      onClick: () => {
        navigate('/engine')
      }
    },
    {
      key: 4,
      label: '数据源',
      icon: <DatabaseOutlined />,
      onClick: () => {
        navigate('/datasource')
      }
    },
    {
      key: 5,
      label: '作业流',
      icon: <NodeIndexOutlined />,
      onClick: () => {
        navigate('/workflow')
      }
    },
    {
      key: 6,
      label: '数据建模',
      icon: <AppstoreOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 7,
      label: '调度历史',
      icon: <ApartmentOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 8,
      label: 'BI酷屏',
      icon: <FundProjectionScreenOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 9,
      label: '数据资产',
      icon: <DashboardOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 10,
      label: '自定义Api',
      icon: <ApiOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 11,
      label: 'AI计算',
      icon: <FireOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 12,
      label: '后台管理',
      icon: <TeamOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 14,
      label: '系统配置',
      icon: <SettingOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    }
  ]

  const {
    token: { colorBgContainer, colorPrimary }
  } = theme.useToken()

  return (
    <>
      <Layout style={{ minHeight: '100vh' }}>
        <Header className={'sy-header'}>
          <div className={'sy-logo'} onClick={() => window.open('https://zhiqingyun.isxcode.com')}>
            至轻云
          </div>
          <a
            className={'sy-table-a sy-help-doc-a'}
            onClick={() => {
              window.open(process.env.DOC_PREFIX_URL)
            }}>
            帮助文档
          </a>
        </Header>
        <Layout>
          <Sider width={200}>
            <Menu className={'sy-sider'} defaultSelectedKeys={['1']} items={menus} />
          </Sider>
          <Layout>
            <Content
              style={{
                padding: 0,
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
