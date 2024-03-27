import {createSvgIconsPlugin} from "vite-plugin-svg-icons";
import path from "path";

export default defineNuxtConfig({
  devtools: {enabled: false},
  modules: [
    "@nuxt/content",
    "@pinia/nuxt",
    "@vueuse/nuxt",
    "@element-plus/nuxt",
    "@nuxtjs/tailwindcss",
    "nuxt-lodash",
  ],
  pinia: {
    autoImports: ["defineStore"],
  },
  // 引入plugins
  plugins: [{src: "~/plugins/svgicon.client.ts"}],
  css: [
    "element-plus/dist/index.css",
    "element-plus/theme-chalk/dark/css-vars.css",
    "~/assets/css/index.scss",
  ],
  vite: {
    plugins: [
      createSvgIconsPlugin({
        iconDirs: [path.resolve(process.cwd(), "assets/svg")],
      }),
    ],
  },
  lodash: {
    prefix: "_",
  },
  content: {
    // highlight: {
    //   theme: 'github-light'
    // }
  }
});
