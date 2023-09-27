import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './store'

import "normalize.css"

import VXETable from 'vxe-table'
import 'vxe-table/lib/style.css'

import '@antv/x6-vue-shape'

import '@/assets/styles/global.scss'

import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)
for (const [ key, component ] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app
  .use(VXETable)
  .use(pinia)
  .use(router)
  .mount('#app')
