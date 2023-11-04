export interface Menu {
  icon: string;
  name: string;
  code: string;
  authType?: Array<string>;
}

// ROLE_SYS_ADMIN
export const menuListData: Array<Menu> = [
  {
    code: 'computer-group',
    name: '计算集群',
    icon: 'UploadFilled',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ]
  },
  {
    code: 'datasource',
    name: '数据源',
    icon: 'DataLine',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ]
  },
  {
    code: 'workflow',
    name: '作业流',
    icon: 'SetUp',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ]
  },
  {
    code: 'driver-management',
    name: '驱动管理',
    icon: 'Cpu',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ]
  },
  {
    code: 'schedule',
    name: '调度历史',
    icon: 'DocumentRemove',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ]
  },
  {
    code: 'user-center',
    name: '用户中心',
    icon: 'UserFilled',
    authType: [ 'ROLE_SYS_ADMIN' ]
  },
  {
    code: 'tenant-list',
    name: '租户列表',
    icon: 'List',
    authType: [ 'ROLE_SYS_ADMIN' ]
  },
  {
    code: 'tenant-user',
    name: '租户成员',
    icon: 'User',
    authType: [ 'ROLE_TENANT_ADMIN', 'ROLE_SYS_ADMIN' ]
  },
  {
    code: 'license',
    name: '证书安装',
    icon: 'Files',
    authType: [ 'ROLE_SYS_ADMIN' ]
  }
]
