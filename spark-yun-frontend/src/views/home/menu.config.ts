export interface Menu {
  icon: string;
  name: string;
  code: string;
  authType?: Array<string>;
  childPage?: Array<string>
  children?: Array<Menu>
}

// ROLE_SYS_ADMIN
export const menuListData: Array<Menu> = [
  {
    code: 'index',
    name: '首页',
    icon: 'Monitor',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
    childPage: []
  },
  {
    code: 'resource-management',
    name: '资源管理',
    icon: 'School',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
    children: [
      {
        code: 'computer-group',
        name: '计算集群',
        icon: 'UploadFilled',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: ['computer-pointer']
      },
      {
        code: 'datasource',
        name: '数据源',
        icon: 'DataLine',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      },
      {
        code: 'file-center',
        name: '资源中心',
        icon: 'Paperclip',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      },
      {
        code: 'spark-container',
        name: '计算容器',
        icon: 'Box',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      },
      {
        code: 'driver-management',
        name: '驱动管理',
        icon: 'Cpu',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      }
    ]
  },
  {
    code: 'data-dev',
    name: '数据开发',
    icon: 'Wallet',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
    children: [
      {
        code: 'workflow',
        name: '作业流',
        icon: 'SetUp',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: ['workflow-page']
      },
      {
        code: 'custom-func',
        name: '函数仓库',
        icon: 'Mouse',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      },
      {
        code: 'realtime-computing',
        name: '实时计算',
        icon: 'Iphone',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: ['computing-detail']
      }
    ]
  },
  {
    code: 'schedule-management',
    name: '任务调度',
    icon: 'ScaleToOriginal',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
    children: [
      {
        code: 'schedule',
        name: '调度历史',
        icon: 'DocumentRemove',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      }
    ]
  },
  {
    code: 'message-management',
    name: '基线告警',
    icon: 'ChatDotRound',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
    children: [
      {
        code: 'message-notifications',
        name: '消息通知',
        icon: 'Message',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      },
      {
        code: 'warning-config',
        name: '告警配置',
        icon: 'TakeawayBox',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      },
      {
        code: 'warning-schedule',
        name: '告警实例',
        icon: 'List',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      }
    ]
  },
  {
    code: 'data-server',
    name: '数据服务',
    icon: 'DataAnalysis',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
    children: [
      {
        code: 'report-components',
        name: '数据卡片',
        icon: 'Grid',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: ['report-item']
      },
      {
        code: 'report-views',
        name: '数据大屏',
        icon: 'Histogram',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: ['report-views-detail']
      },
      {
        code: 'custom-api',
        name: '接口服务',
        icon: 'MessageBox',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      }
    ]
  },
  {
    code: 'tenant-management',
    name: '租户管理',
    icon: 'OfficeBuilding',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
    children: [
      {
        code: 'tenant-user',
        name: '租户成员',
        icon: 'User',
        authType: [ 'ROLE_TENANT_ADMIN', 'ROLE_SYS_ADMIN' ],
        childPage: []
      }
    ]
  },
  {
    code: 'user-center',
    name: '用户中心',
    icon: 'UserFilled',
    authType: [ 'ROLE_SYS_ADMIN' ],
    childPage: []
  },
  {
    code: 'tenant-list',
    name: '租户列表',
    icon: 'List',
    authType: [ 'ROLE_SYS_ADMIN' ],
    childPage: []
  },
  {
    code: 'license',
    name: '证书安装',
    icon: 'Files',
    authType: [ 'ROLE_SYS_ADMIN' ],
    childPage: []
  }
]
