import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './store'

import "normalize.css"

import VXETable from 'vxe-table'
import 'vxe-table/lib/style.css'
import VxeUIAll from 'vxe-pc-ui'
import 'vxe-pc-ui/lib/style.css'
import '@antv/x6-vue-shape'
import '@/assets/styles/global.scss'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import VueGridLayout from 'vue-grid-layout'

// 打印版本号
console.log(`%c 至轻云 %c ${__APP_VERSION__} `, 'background:#35495e; padding: 1px; border-radius: 3px 0 0 3px; color: #fff', 'background:#41b883; padding: 1px; border-radius: 0 3px 3px 0; color: #fff')

const app = createApp(App)
for (const [ key, component ] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app
  .use(VxeUIAll)
  .use(VXETable)
  .use(pinia)
  .use(router)
  .use(VueGridLayout)
  .mount('#app')
