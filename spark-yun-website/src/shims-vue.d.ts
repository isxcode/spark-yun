/* eslint-disable */
declare module "*.vue" {
  import type { DefineComponent } from "vue";
  const component: DefineComponent<{}, {}, any>;
  export default component;
}

declare module "easyele-ui";

declare module "element-plus/dist/locale/zh-cn.mjs";
