import type { RouteRecordRaw } from 'vue-router'

export default [
  {
    path: 'index',
    name: 'index',
    component: () => import('@/views/home-overview/index.vue')
  },
  {
    path: 'computer-group',
    name: 'computer-group',
    component: () => import('@/views/computer-group/index.vue')
  },
  {
    path: 'computer-pointer',
    name: 'computer-pointer',
    component: () => import('@/views/computer-group/computer-pointer/index.vue')
  },
  {
    path: 'datasource',
    name: 'datasource',
    component: () => import('@/views/datasource/index.vue')
  },
  {
    path: 'workflow',
    name: 'workflow',
    component: () => import('@/views/workflow/index.vue')
  },
  {
    path: 'workflow-detail',
    name: 'workflow-detail',
    component: () => import('@/views/workflow/workflow-detail/index.vue')
  },
  {
    path: 'workflow-page',
    name: 'workflow-page',
    component: () => import('@/views/workflow/workflow-page/index.vue')
  },
  {
    path: 'driver-management',
    name: 'driver-management',
    component: () => import('@/views/driver-management/index.vue')
  },
  {
    path: 'work-item',
    name: 'work-item',
    component: () => import('@/views/workflow/work-item/index.vue')
  },
  {
    path: 'tenant-user',
    name: 'tenant-user',
    component: () => import('@/views/tenant-user/index.vue')
  },
  {
    path: 'user-center',
    name: 'user-center',
    component: () => import('@/views/user-center/index.vue')
  },
  {
    path: 'tenant-list',
    name: 'tenant-list',
    component: () => import('@/views/tenant-list/index.vue')
  },
  {
    path: 'oauth-management',
    name: 'oauth-management',
    component: () => import('@/views/oauth-management/index.vue')
  },
  {
    path: 'license',
    name: 'license',
    component: () => import('@/views/license/index.vue')
  },
  {
    path: 'file-center',
    name: 'file-center',
    component: () => import('@/views/file-center/index.vue')
  },
  {
    path: 'schedule',
    name: 'schedule',
    component: () => import('@/views/schedule/index.vue')
  },
  {
    path: 'personal-info',
    name: 'personalInfo',
    component: () => import('@/views/personal-info/index.vue')
  },
  {
    path: 'custom-form',
    name: 'custom-form',
    component: () => import('@/views/custom-form/index.vue'),
    redirect: {
      name: 'form-list'
    },
    children: [
      {
        path: 'form-list',
        name: 'form-list',
        component: () => import('@/views/custom-form/custom-form-list.vue')
      },
      {
        path: 'form-query',
        name: 'form-query',
        component: () => import('@/views/custom-form/custom-form-query/index.vue')
      },
      {
        path: 'form-setting',
        name: 'form-setting',
        component: () => import('@/views/custom-form/form-setting/index.vue')
      }
    ]
  },
  {
    path: 'access-rule',
    name: 'access-rule',
    component: () => import('@/views/access-rule/index.vue')
  },
  {
    path: 'custom-api',
    name: 'custom-api',
    component: () => import('@/views/custom-api/index.vue')
  },
  {
    path: 'spark-container',
    name: 'spark-container',
    component: () => import('@/views/spark-container/index.vue')
  },
  {
    path: 'custom-func',
    name: 'custom-func',
    component: () => import('@/views/custom-func/index.vue')
  },
  {
    path: 'lib-package',
    name: 'lib-package',
    component: () => import('@/views/lib-package/index.vue')
  },
  {
    path: 'realtime-computing',
    name: 'realtime-computing',
    component: () => import('@/views/realtime-computing/index.vue')
  },
  {
    path: 'computing-detail',
    name: 'computing-detail',
    component: () => import('@/views/realtime-computing/computing-detail/index.vue')
  },
  {
    path: 'report-components',
    name: 'report-components',
    component: () => import('@/views/report-components/index.vue')
  },
  {
    path: 'report-item',
    name: 'report-item',
    component: () => import('@/views/report-components/report-item/index.vue')
  },
  {
    path: 'report-views',
    name: 'report-views',
    component: () => import('@/views/report-views/index.vue')
  },
  {
    path: 'report-views-detail',
    name: 'report-views-detail',
    component: () => import('@/views/report-views/report-views-detail/index.vue')
  },
  {
    path: 'message-notifications',
    name: 'message-notifications',
    component: () => import('@/views/message-center/message-notification/index.vue')
  },
  {
    path: 'warning-config',
    name: 'warning-config',
    component: () => import('@/views/message-center/warning-config/index.vue')
  },
  {
    path: 'warning-schedule',
    name: 'warning-schedule',
    component: () => import('@/views/message-center/warning-schedule/index.vue')
  },
  {
    path: 'acquisition-task',
    name: 'acquisition-task',
    component: () => import('@/views/metadata-page/acquisition-task/index.vue')
  },
  {
    path: 'acquisition-instance',
    name: 'acquisition-instance',
    component: () => import('@/views/metadata-page/acquisition-instance/index.vue')
  },
  {
    path: 'metadata-management',
    name: 'metadata-management',
    component: () => import('@/views/metadata-page/metadata-management/index.vue')
  },
  {
    path: 'data-layer',
    name: 'data-layer',
    component: () => import('@/views/data-planning/data-layer/index.vue')
  },
  {
    path: 'layer-area',
    name: 'layer-area',
    component: () => import('@/views/data-planning/data-layer/layer-area/index.vue')
  },
  {
    path: 'field-format',
    name: 'field-format',
    component: () => import('@/views/data-planning/field-format/index.vue')
  },
  {
    path: 'data-model',
    name: 'data-model',
    component: () => import('@/views/data-planning/data-model/index.vue')
  },
  {
    path: 'model-field',
    name: 'model-field',
    component: () => import('@/views/data-planning/data-model/model-field/index.vue')
  },
  {
    path: 'global-variables',
    name: 'global-variables',
    component: () => import('@/views/global-variables/index.vue')
  }
] satisfies RouteRecordRaw[]
