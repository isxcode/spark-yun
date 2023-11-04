// 开源免费部分
import ComputerGroup from '@/views/computer-group/index.vue'
import ComputerPointer from '@/views/computer-group/computer-pointer/index.vue'
import DataSource from '@/views/datasource/index.vue'
import Workflow from '@/views/workflow/index.vue'
import WorkflowDetail from '@/views/workflow/workflow-detail/index.vue'
import WorkflowPage from '@/views/workflow/workflow-page/index.vue'
import WorkItem from '@/views/workflow/work-item/index.vue'
import Schedule from '@/views/schedule/index.vue'

import UserCenter from '@/views/user-center/index.vue'
import TenantList from '@/views/tenant-list/index.vue'
import License from '@/views/license/index.vue'
import TenantUser from '@/views/tenant-user/index.vue'
import PersonalInfo from '@/views/personal-info/index.vue'

import DriverManagement from '@/views/driver-management/index.vue'

export default [
  {
    path: 'computer-group',
    name: 'computer-group',
    component: ComputerGroup
  },
  {
    path: 'computer-pointer',
    name: 'computer-pointer',
    component: ComputerPointer
  },
  {
    path: 'datasource',
    name: 'datasource',
    component: DataSource
  },
  {
    path: 'workflow',
    name: 'workflow',
    component: Workflow
  },
  {
    path: 'workflow-detail',
    name: 'workflow-detail',
    component: WorkflowDetail
  },
  {
    path: 'workflow-page',
    name: 'workflow-page',
    component: WorkflowPage
  },
  {
    path: 'driver-management',
    name: 'driver-management',
    component: DriverManagement
  },
  {
    path: 'work-item',
    name: 'work-item',
    component: WorkItem
  },
  {
    path: 'tenant-user',
    name: 'tenant-user',
    component: TenantUser
  },
  {
    path: 'user-center',
    name: 'user-center',
    component: UserCenter
  },
  {
    path: 'tenant-list',
    name: 'tenant-list',
    component: TenantList
  },
  {
    path: 'license',
    name: 'license',
    component: License
  },
  {
    path: 'schedule',
    name: 'schedule',
    component: Schedule
  },
  {
    path: 'personal-info',
    name: 'personalInfo',
    component: PersonalInfo
  }
]
