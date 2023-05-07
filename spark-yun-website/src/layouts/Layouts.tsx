import React, {useEffect, useState} from 'react'
import {Avatar, Col, Dropdown, Layout, Menu, type MenuProps, Row, Space, theme, Typography} from 'antd'
import {Outlet, useNavigate} from 'react-router-dom'
import './Layouts.less'
import {
  ApartmentOutlined,
  ApiOutlined,
  CloudOutlined,
  CopyrightOutlined,
  DashboardOutlined,
  DatabaseOutlined,
  DownOutlined,
  HomeOutlined,
  NodeIndexOutlined,
  ProfileOutlined,
  SearchOutlined,
  SettingOutlined,
  TeamOutlined,
  ToolOutlined,
  UserOutlined
} from '@ant-design/icons'
import {QueryTenantsReq} from '../types/tenant/req/QueryTenantsReq'
import {chooseTenantApi, getTenantApi, queryTenantsApi, queryUserTenantsApi} from '../services/tenant/TenantService'
import {TenantRow} from '../types/tenant/info/TenantRow'

function Layouts() {
  const navigate = useNavigate()

  const [tenant, setTenant] = useState("");
  const [tenants, setTenants] = useState<TenantRow[]>([])

  useEffect(() => {
    if (localStorage.getItem('Tenant') === 'undefined') {
      setTenant("选择租户");
    }else{
      getTenantApi(localStorage.getItem('Tenant') as string).then(r => setTenant(r.name));
      localStorage.getItem('Tenant')
    }
  }, []);

  const {Header, Content, Sider} = Layout

  const genMenus = () => {
    if (localStorage.getItem('Role') == 'ROLE_SYS_ADMIN') {
      return adminMenus
    }
    if (localStorage.getItem('Role') == 'ROLE_TENANT_MEMBER') {
      return memberMenus
    }
    memberMenus.push({
      key: 14,
      label: '租户成员',
      icon: <TeamOutlined/>,
      onClick: () => {
        navigate('/tenant_user')
      }
    })
    // memberMenus.push({
    //   key: 12,
    //   label: '后台设置',
    //   icon: <ToolOutlined/>,
    //   onClick: () => {
    //     navigate('/auth')
    //   }
    // })
    return memberMenus
  }

  const memberMenus = [
    // {
    //   key: 1,
    //   label: '首页',
    //   icon: <HomeOutlined/>,
    //   onClick: () => {
    //     navigate('/auth')
    //   }
    // },
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
    // {
    //   key: 22,
    //   label: '模型仓库',
    //   icon:<ShopOutlined />,
    //   onClick: () => {
    //     navigate('/auth')
    //   }
    // },
    // {
    //   key: 6,
    //   label: '数据建模',
    //   icon: <AppstoreOutlined/>,
    //   onClick: () => {
    //     navigate('/auth')
    //   }
    // },
    // {
    //   key: 8,
    //   label: 'BI酷屏',
    //   icon: <FundProjectionScreenOutlined/>,
    //   onClick: () => {
    //     navigate('/auth')
    //   }
    // },
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
      icon: <SearchOutlined/>,
      onClick: () => {
        navigate('/auth')
      }
    },
    {
      key: 10,
      label: '自定义Api',
      icon: <ApiOutlined/>,
      onClick: () => {
        navigate('/api')
      }
    }
    // {
    //   key: 11,
    //   label: 'AI计算',
    //   icon: <FireOutlined/>,
    //   onClick: () => {
    //     navigate('/auth')
    //   }
    // },
  ]

  const adminMenus: MenuProps['items'] = [
    {
      key: 18,
      label: '用户中心',
      icon: <UserOutlined/>,
      onClick: () => {
        navigate('/user')
      }
    },
    {
      key: 17,
      label: '租户列表',
      icon: <ProfileOutlined/>,
      onClick: () => {
        navigate('/tenant')
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
      key: 19,
      label: '证书安装',
      icon: <CopyrightOutlined/>,
      onClick: () => {
        navigate('/license')
      }
    },
    // {
    //   key: 15,
    //   label: '系统设置',
    //   icon: <SettingOutlined />,
    //   onClick: () => {
    //     navigate('/setting')
    //   }
    // }
  ]

  const items2: MenuProps['items'] = [
    // {
    //   key: '1',
    //   label: '设置'
    // },
    {
      key: '2',
      label: '退出登录',
      onClick: () => {
        localStorage.removeItem('Authorization')
        localStorage.removeItem('Token')
        window.location.reload()
      }
    }
  ]

  const items: MenuProps['items'] = tenants.map((row) => {
    const rowData = {
      key: '',
      label: '',
      onClick: () => {
      }
    }
    rowData.key = row.id as string
    rowData.label = row.name as string
    rowData.onClick = () => {
      console.log(row)
      setTenant(row.name as string)
      chooseTenantApi(row.id as string).then();
      window.location.reload()
      localStorage.setItem('Tenant', row.id as string)
    }
    return rowData
  })

  const {
    token: {colorBgContainer, colorPrimary}
  } = theme.useToken()

  const queryTenantsReq: QueryTenantsReq = {
    page: 1,
    pageSize: 999,
    searchKeyWord: ''
  }

  const fetchTenant = () => {
    if (localStorage.getItem('Role') == 'ROLE_SYS_ADMIN') {
      queryTenantsApi(queryTenantsReq).then(function (response) {
        setTenants(response.content)
      });
    } else {
      queryUserTenantsApi().then(function (response) {
        setTenants(response);
      });
    }
  }

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
                    <Dropdown
                      onOpenChange={() => {
                        fetchTenant()
                      }}
                      menu={{
                        items,
                        selectable: true,
                        defaultSelectedKeys: [tenant as string]
                      }}>
                      <Typography.Link>
                        <Space style={{color: colorPrimary}}>
                          {tenant}
                          <DownOutlined/>
                        </Space>
                      </Typography.Link>
                    </Dropdown>
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
                    <Dropdown menu={{items: items2}} placement="bottomRight" arrow>
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
          <Sider width={200} theme={'light'}>
            <Menu
              className={'sy-sider'}
              defaultSelectedKeys={[localStorage.getItem('Role') == 'ROLE_SYS_ADMIN' ? "18" : "1"]}
              items={genMenus()}
              mode="inline"
              style={{overflowY: 'scroll', maxHeight: '90vh', height: '90vh'}}
            />
            {/* <div style={{ */}
            {/*  position: 'absolute', bottom: 10, textAlign: 'center', width: '100%', color: 'darkgrey', */}
            {/*  fontSize: '14px' */}
            {/* }}>v0.0.1 */}
            {/* </div> */}
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
  )
}

export default Layouts
