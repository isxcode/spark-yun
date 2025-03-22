// 开源免费部分
import HomeOverview from '@/views/home-overview/index.vue'
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
import OauthManagement from '@/views/oauth-management/index.vue'

import DriverManagement from '@/views/driver-management/index.vue'
import CustomForm from '@/views/custom-form/index.vue'
import CustomFormList from '@/views/custom-form/custom-form-list.vue'
import CustomFormQuery from '@/views/custom-form/custom-form-query/index.vue'
import CustomFormSetting from '@/views/custom-form/form-setting/index.vue'
import CustomApi from '@/views/custom-api/index.vue'
import SparkContainer from '@/views/spark-container/index.vue'
import RealtimeComputing from '@/views/realtime-computing/index.vue'
import ComputingDetail from '@/views/realtime-computing/computing-detail/index.vue'

import fileCenter from '@/views/file-center/index.vue'
import CustomFunc from '@/views/custom-func/index.vue'

import ReportComponents from '@/views/report-components/index.vue'
import ReportItem from '@/views/report-components/report-item/index.vue'
import ReportViews from '@/views/report-views/index.vue'
import ReportViewsDetail from '@/views/report-views/report-views-detail/index.vue'

// 消息告警
import MessageNotifications from '@/views/message-center/message-notification/index.vue'
import WarningConfig from '@/views/message-center/warning-config/index.vue'
import WarningSchedule from '@/views/message-center/warning-schedule/index.vue'

// 元数据
import AcquisitionTask from '@/views/metadata-page/acquisition-task/index.vue'
import AcquisitionInstance from '@/views/metadata-page/acquisition-instance/index.vue'
import MetadataManagement from '@/views/metadata-page/metadata-management/index.vue'

export default [
  {
    path: 'index',
    name: 'index',
    component: HomeOverview
  },
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
    path: 'oauth-management',
    name: 'oauth-management',
    component: OauthManagement
  },
  {
    path: 'license',
    name: 'license',
    component: License
  },
  {
    path: 'file-center',
    name: 'file-center',
    component: fileCenter
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
  },
  {
    path: 'custom-form',
    name: 'custom-form',
    component: CustomForm,
    redirect: {
      name: 'form-list'
    },
    children: [
      {
        path: 'form-list',
        name: 'form-list',
        component: CustomFormList
      },
      {
        path: 'form-query',
        name: 'form-query',
        component: CustomFormQuery
      },
      {
        path: 'form-setting',
        name: 'form-setting',
        component: CustomFormSetting
      }
    ]
  },
  {
    path: 'custom-api',
    name: 'custom-api',
    component: CustomApi
  },
  {
    path: 'spark-container',
    name: 'spark-container',
    component: SparkContainer
  },
  {
    path: 'custom-func',
    name: 'custom-func',
    component: CustomFunc
  },
  {
    path: 'realtime-computing',
    name: 'realtime-computing',
    component: RealtimeComputing
  },
  {
    path: 'computing-detail',
    name: 'computing-detail',
    component: ComputingDetail
  },
  {
    path: 'report-components',
    name: 'report-components',
    component: ReportComponents,
  },
  {
    path: 'report-item',
    name: 'report-item',
    component: ReportItem
  },
  {
    path: 'report-views',
    name: 'report-views',
    component: ReportViews
  },
  {
    path: 'report-views-detail',
    name: 'report-views-detail',
    component: ReportViewsDetail
  },
  {
    path: 'message-notifications',
    name: 'message-notifications',
    component: MessageNotifications
  },
  {
    path: 'warning-config',
    name: 'warning-config',
    component: WarningConfig
  },
  {
    path: 'warning-schedule',
    name: 'warning-schedule',
    component: WarningSchedule
  },
  {
    path: 'acquisition-task',
    name: 'acquisition-task',
    component: AcquisitionTask
  },
  {
    path: 'acquisition-instance',
    name: 'acquisition-instance',
    component: AcquisitionInstance
  },
  {
    path: 'metadata-management',
    name: 'metadata-management',
    component: MetadataManagement
  },
]
