/*
 * @Author: fanciNate
 * @Date: 2023-05-16 10:14:32
 * @LastEditTime: 2023-05-18 21:18:54
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/main.ts
 */
import { createApp, nextTick } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import zhCn from "element-plus/dist/locale/zh-cn.mjs";
import VXETable from "vxe-table";
import "vxe-table/lib/style.css";
import "@/assets/scss/index.scss";

import * as ElementPlusIconsVue from "@element-plus/icons-vue";

const app = createApp(App);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.use(VXETable);
app
  .use(store)
  .use(ElementPlus, {
    locale: zhCn,
  })
  .use(router)
  .mount("#app");
