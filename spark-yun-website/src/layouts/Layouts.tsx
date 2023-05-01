import React from 'react'
import {Avatar, Col, Dropdown, Layout, Menu, type MenuProps, message, Row, Space, theme, Typography} from 'antd';
import {Outlet, useNavigate} from 'react-router-dom'
import './Layouts.less'
import {
  ApartmentOutlined,
  ApiOutlined,
  AppstoreOutlined,
  CloudOutlined, CopyrightOutlined,
  DashboardOutlined,
  DatabaseOutlined, DownOutlined,
  FireOutlined,
  FundProjectionScreenOutlined,
  HomeOutlined,
  NodeIndexOutlined, ProfileOutlined,
  ProjectOutlined, SearchOutlined,
  SettingOutlined, ShopOutlined, ShoppingCartOutlined,
  TeamOutlined, ToolOutlined, TrademarkOutlined,
  UserOutlined
} from '@ant-design/icons'

function Layouts() {
  const navigate = useNavigate()

  const {Header, Content, Sider} = Layout

  const memberMenus: MenuProps['items'] = [
    {
      key: 1,
      label: '首页',
      icon: <HomeOutlined/>,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 3,
      label: '计算集群',
      icon: <CloudOutlined/>,
      onClick: () => {
        navigate('/engine')
      }
    },
    {
      key: 4,
      label: '数据源',
      icon: <DatabaseOutlined/>,
      onClick: () => {
        navigate('/datasource')
      }
    },
    {
      key: 5,
      label: '作业流',
      icon: <NodeIndexOutlined/>,
      onClick: () => {
        navigate('/workflow')
      }
    },
    {
      key: 7,
      label: '调度历史',
      icon: <ApartmentOutlined/>,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 22,
      label: '模型仓库',
      icon:<ShopOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 6,
      label: '数据建模',
      icon: <AppstoreOutlined/>,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 8,
      label: 'BI酷屏',
      icon: <FundProjectionScreenOutlined/>,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 9,
      label: '数据资产',
      icon: <DashboardOutlined/>,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 21,
      label: '数据地图',
      icon: <SearchOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 10,
      label: '自定义Api',
      icon: <ApiOutlined/>,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 11,
      label: 'AI计算',
      icon: <FireOutlined/>,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 14,
      label: '租户成员',
      icon: <TeamOutlined/>,
      onClick: () => {
        navigate('/tenant_user')
      }
    },
    {
      key: 12,
      label: '后台设置',
      icon: <ToolOutlined />,
      onClick: () => {
        navigate('/auth')
      }
    }
  ];

  const adminMenus: MenuProps['items'] = [
    {
      key: 14,
      label: '租户成员',
      icon: <TeamOutlined/>,
      onClick: () => {
        navigate('/tenant_user')
      }
    },
    {
      key: 18,
      label: '用户中心',
      icon: <UserOutlined />,
      onClick: () => {
        navigate('/user')
      },
    },
    {
      key: 19,
      label: '证书安装',
      icon: <CopyrightOutlined />,
      onClick: () => {
        navigate('/license')
      }
    },
    {
      key: 17,
      label: '租户列表',
      icon: <ProfileOutlined />,
      onClick: () => {
        navigate('/tenant')
      }
    },
    {
      key: 15,
      label: '系统设置',
      icon: <SettingOutlined/>,
      onClick: () => {
        navigate('/auth')
      }
    }
  ];

  const items: MenuProps['items'] = [
    {
      key: '1',
      label: '设置',
    },
    {
      key: '2',
      label: '退出登录',
      onClick: () => {
        localStorage.removeItem('Authorization')
        localStorage.removeItem('Token')
        window.location.reload()
      }
    },
  ];

  const userItems: MenuProps['items'] = [
    {
      key: '1',
      label: '设置',
    },
    {
      key: '2',
      label: '退出登录',
    },
  ];

  const {
    token: {colorBgContainer, colorPrimary}
  } = theme.useToken()

  return (
    <>
      <Layout style={{minHeight: '100vh'}}>
        <Header className={'sy-header'}>
          <Row align="middle">
            <Col span={8}>
              <Row justify={'start'} style={{minWidth: '320px'}}>
                <Space>
                  <Col style={{minWidth: '160px'}}>
                    <div className={'sy-logo'} onClick={() => window.open('https://zhiqingyun.isxcode.com')}>
                      至轻云
                    </div>
                  </Col>
                  <Col style={{minWidth: '150px', display: 'flex', alignItems: 'center'}}>
                    <Dropdown.Button
                      icon={<DownOutlined/>}
                      menu={{items}}
                      onClick={() => {
                      }}
                    >
                      测试租户
                    </Dropdown.Button>
                  </Col>
                </Space>
              </Row>
            </Col>
            <Col span={8} offset={8}>
              <Row justify={'end'} style={{minWidth: '100px'}}>
                <Space>
                  <Col style={{minWidth: '80px'}}>
                    <a
                      className={'sy-table-a sy-help-doc-a'}
                      onClick={() => {
                        window.open(process.env.DOC_PREFIX_URL)
                      }}>
                      帮助文档
                    </a>
                  </Col>
                  <Col style={{minWidth: '40px'}}>
                    <Dropdown menu={{items}} placement="bottomRight" arrow>
                      <Avatar style={{backgroundColor: '#e25a1b', verticalAlign: 'middle'}} size="large" gap={4}>
                        {localStorage.getItem('Username')}
                      </Avatar>
                    </Dropdown>
                  </Col>
                </Space>
              </Row>
            </Col>
          </Row>
        </Header>
        <Layout>
          <Sider width={200} theme={"light"}>
            <Menu className={'sy-sider'} defaultSelectedKeys={['1']}
                  items={localStorage.getItem("Role") == 'ROLE_SYS_ADMIN' ? adminMenus : memberMenus} mode="inline"
                  style={{overflowY: 'scroll', maxHeight: '90vh', height: '90vh'}}/>
            {/*<div style={{*/}
            {/*  position: 'absolute', bottom: 10, textAlign: 'center', width: '100%', color: 'darkgrey',*/}
            {/*  fontSize: '14px'*/}
            {/*}}>v0.0.1*/}
            {/*</div>*/}
          </Sider>
          <Layout>
            <Content
              style={{
                padding: 0,
                margin: 0,
                background: colorBgContainer
              }}>
              <Outlet/>
            </Content>
          </Layout>
        </Layout>
      </Layout>
    </>
  );
}

export default Layouts
