import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
import path from "path";

export default defineNuxtConfig({
  devtools: { enabled: false },
  build: {
    transpile: ['vueuc']
  },
  modules: [
    "@nuxt/content",
    "@pinia/nuxt",
    "@vueuse/nuxt",
    "@element-plus/nuxt",
    "@nuxtjs/tailwindcss",
    "nuxt-lodash",
    "@nuxtjs/i18n",
    "nuxtjs-naive-ui"
  ],
  pinia: {
    autoImports: ["defineStore"],
  },
  plugins: [{ src: "~/plugins/svgicon.client.ts" }],
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
    ]
  },
  lodash: {
    prefix: "_",
  },
  content: {
    highlight: {
      theme: "dark-plus",
      langs: [
        "bash",
        "java",
        "json",
        "markdown",
        "typescript",
        "yaml",
        "yml",
        "xml",
        "javascript",
        "sql",
        "python",
        "html",
        "css",
        "shell",
        "vue",
        "go",
        "csharp",
        "cpp",
        "swift",
        "dockerfile",
        "ini",
        "toml",
        "powershell",
        "makefile",
        "graphql",
        "log",
        "wikitext",
        "groovy"
      ],
    },
  },
  i18n: {
    locales: [
      { name: "中文", code: "zh", iso: "zh-CN", dir: "ltr" },
      { name: "English", code: "en", iso: "en-US", dir: "ltr" },
    ],
    detectBrowserLanguage: false,
    vueI18n: "./locales/i18n.config.ts",
    defaultLocale: "zh",
    strategy: "prefix_and_default",
  },
});
